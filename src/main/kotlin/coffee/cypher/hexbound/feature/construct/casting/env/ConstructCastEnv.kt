package coffee.cypher.hexbound.feature.construct.casting.env

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.PatternShapeMatch
import at.petrak.hexcasting.api.casting.PatternShapeMatch.Normal
import at.petrak.hexcasting.api.casting.PatternShapeMatch.PerWorld
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.pigment.FrozenPigment
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.init.config.HexboundConfig
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.quiltmc.qkl.library.networking.allPlayers
import java.util.function.Predicate

const val CONSTRUCT_AMBIT = 32.0

class ConstructCastEnv(
    val construct: AbstractConstructEntity,
    val isCastFromHeldItem: Boolean = false
) : CastingEnvironment(construct.world as ServerWorld) {
    init {
    }

    override fun precheckAction(match: PatternShapeMatch) {
        super.precheckAction(match)

        val key = when (match) {
            is Normal -> match.key.value
            is PerWorld -> match.key.value
            is PatternShapeMatch.Special -> match.key.value
            else -> return
        }

        if (false /* TODO check REA */) {
            throw MishapDisallowedSpell("disallowed_construct")
        }
    }

    override fun getCaster(): ServerPlayerEntity? {
        return null
    }

    override fun getMishapEnvironment(): MishapEnvironment {
        return ConstructMishapEnv(construct)
    }

    override fun postExecution(result: CastResult?) {
        //TODO record mishaps
    }

    override fun mishapSprayPos(): Vec3d {
        return construct.pos
    }

    //todo free action REA
    override fun extractMedia(cost: Long): Long =
        if (cost <= MediaConstants.DUST_UNIT / 20)
            0
        else
            cost

    override fun isVecInRange(vec: Vec3d): Boolean {
        return vec.squaredDistanceTo(construct.pos) <= CONSTRUCT_AMBIT * CONSTRUCT_AMBIT
    }

    override fun hasEditPermissionsAt(vec: BlockPos): Boolean {
        return construct.fakePlayer?.canModifyAt(world, vec) == true
    }

    override fun getCastingHand(): Hand =
        if (isCastFromHeldItem)
            Hand.MAIN_HAND
        else
            Hand.OFF_HAND

    override fun getUsableStacks(mode: StackDiscoveryMode): List<ItemStack> =
        if (isCastFromHeldItem) {
            emptyList()
        } else {
            listOf(
                if (construct.getStackInHand(Hand.MAIN_HAND).isEmpty)
                    ItemStack.EMPTY.copy()
                else
                    construct.getStackInHand(Hand.MAIN_HAND)
            )
        }

    override fun getPrimaryStacks(): List<HeldItemInfo> {
        val stack = construct.getStackInHand(Hand.MAIN_HAND).let {
            if (it.isEmpty) {
                it.copy()
            } else {
                it
            }
        }

        return listOf(HeldItemInfo(stack, Hand.MAIN_HAND))
    }

    override fun replaceItem(stackOk: Predicate<ItemStack>, replaceWith: ItemStack, hand: Hand?): Boolean = false

    override fun getPigment(): FrozenPigment {
        return FrozenPigment.DEFAULT.get() //TODO yeah actually impl this shit etc etc
    }

    override fun setPigment(pigment: FrozenPigment?): FrozenPigment? {
        return null
    }

    override fun produceParticles(particles: ParticleSpray, colorizer: FrozenPigment) {
        particles.sprayParticles(world, colorizer)
    }

    override fun printMessage(message: Text) {
        //TODO add to history
        world.server.allPlayers.forEach {
            //STOPSHIP no
            it.sendSystemMessage(message, false)
        }
    }
}
