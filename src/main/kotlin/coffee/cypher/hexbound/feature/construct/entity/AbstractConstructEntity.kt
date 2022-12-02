package coffee.cypher.hexbound.feature.construct.entity

import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.casting.CastingHarness
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.utils.downcast
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import coffee.cypher.hexbound.feature.construct.command.ConstructCommand
import coffee.cypher.hexbound.feature.construct.command.exception.ConstructCommandException
import coffee.cypher.hexbound.feature.construct.command.exception.UnknownConstructCommandException
import coffee.cypher.hexbound.feature.construct.command.execution.ConstructCommandExecutor
import coffee.cypher.hexbound.feature.construct.entity.component.ConstructComponentKey
import coffee.cypher.hexbound.init.CommonRegistries
import coffee.cypher.hexbound.init.Hexbound
import coffee.cypher.hexbound.util.fake.ConstructFakePlayer
import coffee.cypher.hexbound.util.mixinaccessor.construct
import coffee.cypher.hexbound.util.provideDelegate
import com.mojang.serialization.Codec
import net.minecraft.entity.EntityType
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtOps
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Arm
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.world.World
import org.quiltmc.qkl.library.nbt.set
import org.quiltmc.qkl.library.text.*
import kotlin.jvm.optionals.getOrNull

abstract class AbstractConstructEntity(
    entityType: EntityType<out PathAwareEntity>,
    world: World
) : PathAwareEntity(entityType, world) {
    var instructionSet: List<Iota>? = null

    internal var hexalLinkable: Any? = null
    internal var hexalLinks by HEXAL_LINKS

    private val components = mutableMapOf<ConstructComponentKey<*>, Any>()

    @Suppress("LeakingThis")
    protected val fakePlayer = if (world.isClient)
        null
    else
        ConstructFakePlayer(world as ServerWorld, this)

    protected var command: Pair<ConstructCommand<*>, List<Iota>>? = null
    protected var harness: CastingHarness? = null
    protected var error: Text? = null

    private lateinit var _executor: ConstructCommandExecutor

    private fun getOrCreateHarness(): CastingHarness {
        if (harness == null) {
            harness = CastingHarness(createCastingContext())
        }

        return harness!!
    }

    private fun createCastingContext(): CastingContext {
        val castingContext = CastingContext(
            fakePlayer!!,
            Hand.OFF_HAND,
            CastingContext.CastSource.PACKAGED_HEX,
            null
        )

        castingContext.construct = this

        return castingContext
    }

    private fun getOrCreateExecutor(world: ServerWorld): ConstructCommandExecutor {
        if (!this::_executor.isInitialized) {
            _executor = ConstructCommandExecutor(this, world, this::onCommandCompleted, this::onCommandError)
        }

        return _executor
    }

    private fun onCommandCompleted() {
        val (_, callback) = command ?: return

        command = null
        evaluateInstructions(callback)
    }

    private fun onCommandError(error: Throwable) {
        val commandException = if (error is ConstructCommandException) {
            error
        } else {
            UnknownConstructCommandException(error)
        }

        setLastError(commandException.errorText)
    }

    fun setLastError(error: Text) {
        this.error = error
        command = null
        harness = null
    }

    private fun evaluateInstructions(instructionSet: List<Iota>) {
        val serverWorld = world as? ServerWorld ?: return

        error = null

        val info = getOrCreateHarness().executeIotas(instructionSet, serverWorld)

        if (!info.resolutionType.success) {
            getOrCreateExecutor(serverWorld).cancelCommand()
        }

        if (command == null) {
            harness = null
        }
    }

    fun executeCommand(
        command: ConstructCommand<*>,
        onComplete: List<Iota>,
        world: ServerWorld
    ) {
        this.command = command to onComplete
        getOrCreateExecutor(world).startCommand(command)
    }

    override fun tick() {
        super.tick()
        fakePlayer?.setPos(x, y, z)

        if (instructionSet != null) {
            harness = null
            evaluateInstructions(instructionSet!!)
            instructionSet = null
        }
        if (!world.isClient) {
            getOrCreateExecutor(world as ServerWorld).tick()
        }
    }

    protected fun <T : Any> registerComponent(key: ConstructComponentKey<T>, value: T) {
        components += key to value
    }

    fun <T : Any> getComponent(key: ConstructComponentKey<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return components[key] as T?
    }

    override fun interactMob(player: PlayerEntity, hand: Hand): ActionResult {
        if (!player.isSneaking && player.getStackInHand(hand).isOf(HexItems.SCRYING_LENS)) {
            if (!world.isClient) {
                val text = when {
                    error != null -> buildText {
                        color(Color(0xFFA500)) {
                            translatable("hexbound.construct.status.error")
                        }

                        text(error!!)
                    }

                    command != null -> buildText {
                        color(Color.GREEN) {
                            translatable("hexbound.construct.status.executing")
                        }

                        text(command!!.first.display(world as ServerWorld))
                    }

                    else -> Text.translatable("hexbound.construct.status.idle")
                }

                player.sendSystemMessage(text)
            }

            return ActionResult.SUCCESS
        }

        return super.interactMob(player, hand)
    }

    override fun isPersistent(): Boolean {
        return true
    }

    override fun cannotDespawn(): Boolean {
        return true
    }

    override fun initDataTracker() {
        super.initDataTracker()

        val compound = NbtCompound()
        compound["render_links"] = NbtList()
        dataTracker.startTracking(HEXAL_LINKS, compound)
    }

    override fun getArmorItems(): Iterable<ItemStack> {
        return mutableListOf()
    }

    override fun getMainArm(): Arm {
        return Arm.RIGHT
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)

        nbt["hexal_links"] = hexalLinks

        if (world is ServerWorld) {
            command?.let {
                nbt["command"] = encodeCommand(it)
            }

            harness?.let {
                nbt["harness"] = it.serializeToNBT()
            }
        }
    }

    private fun <C : ConstructCommand<*>> encodeCommand(commandPair: Pair<C, List<Iota>>): NbtCompound {
        val (command, callback) = commandPair
        val compound = NbtCompound()

        val type = CommonRegistries.CONSTRUCT_COMMANDS.getId(command.getType())
        compound["type"] = type.toString()

        val callbackList = NbtList()

        callback.forEach {
            callbackList.add(HexIotaTypes.serialize(it))
        }

        compound["on_complete"] = callbackList

        if (CommonRegistries.CONSTRUCT_COMMANDS.get(type) != command.getType()) {
            Hexbound.LOGGER.warn("Construct command type for $command was not registered")
            compound["data"] = NbtCompound()
            return compound
        }

        @Suppress("UNCHECKED_CAST")
        compound["data"] = (command.getType().codec as Codec<C>).encodeStart(NbtOps.INSTANCE, command).result().get()

        return compound
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)

        hexalLinks = nbt.getCompound("hexal_links")

        command = null
        val serverWorld = world as? ServerWorld ?: return

        if (nbt.contains("command")) {
            val commandNbt = nbt.getCompound("command")
            val typeId = Identifier.tryParse(commandNbt.getString("type"))
            val type = CommonRegistries.CONSTRUCT_COMMANDS.get(typeId)
            val result = type.codec.decode(NbtOps.INSTANCE, commandNbt.get("data"))
            val onComplete = commandNbt.getList("on_complete", NbtElement.COMPOUND_TYPE.toInt()).map {
                HexIotaTypes.deserialize(it.downcast(NbtCompound.TYPE), serverWorld)
            }

            val newCommand = result.result().getOrNull()?.first

            if (newCommand != null) {
                executeCommand(newCommand, onComplete, serverWorld)
            }
        }

        if (nbt.contains("harness")) {
            harness = CastingHarness.fromNBT(nbt.getCompound("harness"), createCastingContext())
        }
    }

    companion object {
        val HEXAL_LINKS: TrackedData<NbtCompound> = DataTracker.registerData(
            AbstractConstructEntity::class.java,
            TrackedDataHandlerRegistry.TAG_COMPOUND
        )

        val EXTRA_TICK_HANDLERS = mutableListOf<(AbstractConstructEntity) -> Unit>()
    }
}
