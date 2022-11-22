package coffee.cypher.hexbound.feature.construct.entity

import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.casting.CastingHarness
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import coffee.cypher.hexbound.init.Hexbound
import coffee.cypher.hexbound.mixinaccessor.construct
import coffee.cypher.hexbound.util.FakePlayerFactory
import dev.cafeteria.fakeplayerapi.server.FakeServerPlayer
import net.minecraft.entity.EntityType
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Arm
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.world.World

abstract class AbstractConstructEntity(
    entityType: EntityType<out PathAwareEntity>,
    world: World
) : PathAwareEntity(entityType, world) {
    var command: SpiderConstructEntity.Command? = null
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

    fun executeCommand(command: SpiderConstructEntity.Command) {
        this.command?.let { goalSelector.remove(it.goal) }

        this.command = command
        goalSelector.add(1, command.goal)
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
                nbt.putString("command_id", it.id.toString())
                nbt.put("command_data", it.serialize())
            }
        }
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)

        command = null

        val serverWorld = world as? ServerWorld ?: return

        if (nbt.contains("command_id")) {
            val commandId = Identifier.tryParse(nbt.getString("command_id")) ?: return
            val reader = SpiderConstructEntity.Command.COMMAND_READERS[commandId] ?: return
            command = TODO()
        }
    }
}
