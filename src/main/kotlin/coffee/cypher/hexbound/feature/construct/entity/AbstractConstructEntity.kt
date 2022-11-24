package coffee.cypher.hexbound.feature.construct.entity

import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.casting.CastingHarness
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import coffee.cypher.hexbound.feature.construct.command.ConstructCommand
import coffee.cypher.hexbound.init.CommonRegistries
import coffee.cypher.hexbound.init.Hexbound
import coffee.cypher.hexbound.mixinaccessor.construct
import coffee.cypher.hexbound.util.FakePlayerFactory
import com.mojang.serialization.Codec
import dev.cafeteria.fakeplayerapi.server.FakeServerPlayer
import net.minecraft.entity.EntityType
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtOps
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Arm
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.world.World
import kotlin.jvm.optionals.getOrNull

abstract class AbstractConstructEntity<T : AbstractConstructEntity<T>>(
    entityType: EntityType<out PathAwareEntity>,
    world: World
) : PathAwareEntity(entityType, world) {
    var command: ConstructCommand<AbstractConstructEntity<T>, *>? = null
    var instructionSet: List<Iota>? = null

    private fun evaluateInstructions(instructionSet: List<Iota>) {
        val serverWorld = world as? ServerWorld ?: return

        val castingContext = CastingContext(
            prepareFakePlayer(serverWorld),
            Hand.OFF_HAND,
            CastingContext.CastSource.PACKAGED_HEX,
            null
        )
        castingContext.construct = this

        val harness = CastingHarness(castingContext)
        harness.stack += EntityIota(this)

        val info = harness.executeIotas(instructionSet, serverWorld)
    }

    fun executeCommand(command: ConstructCommand<AbstractConstructEntity<T>, *>, world: ServerWorld) {
        this.command = command
        goalSelector.add(1, command.createGoal(this, world))
    }

    protected open fun prepareFakePlayer(world: ServerWorld): FakeServerPlayer {
        val fakePlayer = FakePlayerFactory.getFakePlayerForConstruct(world)
        fakePlayer.setPos(x, y, z)

        return fakePlayer
    }

    override fun tick() {
        super.tick()
        if (instructionSet != null) {
            evaluateInstructions(instructionSet!!)
            instructionSet = null
        }
    }

    override fun getArmorItems(): Iterable<ItemStack> {
        return mutableListOf()
    }

    override fun getMainArm(): Arm {
        return Arm.RIGHT
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)

        if (world is ServerWorld) {
            command?.let {
                nbt.put("command", encodeCommand(it))
            }
        }
    }

    private fun <C : ConstructCommand<*, *>> encodeCommand(command: C) : NbtCompound {
        val compound = NbtCompound()

        val type = CommonRegistries.CONSTRUCT_COMMANDS.getId(command.type)
        compound.putString(
            "type",
            type.toString()
        )

        if (CommonRegistries.CONSTRUCT_COMMANDS.get(type) != command.type) {
            Hexbound.LOGGER.warn("Construct command type for $command was not registered")
            compound.put("data", NbtCompound())
            return compound
        }

        @Suppress("UNCHECKED_CAST")
        compound.put(
            "data",
            (command.type.codec as Codec<C>).encodeStart(NbtOps.INSTANCE, command).result().get()
        )

        return compound
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)

        command = null

        val serverWorld = world as? ServerWorld ?: return

        if (nbt.contains("command")) {
            val commandNbt = nbt.getCompound("command")
            val typeId = Identifier.tryParse(commandNbt.getString("type"))
            val type = CommonRegistries.CONSTRUCT_COMMANDS.get(typeId)
            val result = type.codec.decode(NbtOps.INSTANCE, commandNbt.get("data"))

            @Suppress("UNCHECKED_CAST")
            val newCommand = result.result().getOrNull()?.first as ConstructCommand<AbstractConstructEntity<T>, *>?

            if (newCommand != null) {
                executeCommand(newCommand, serverWorld)
            }
        }
    }
}
