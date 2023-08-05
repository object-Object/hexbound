package coffee.cypher.hexbound.feature.pigment_storage.action

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents
import coffee.cypher.hexbound.feature.pigment_storage.mishap.MishapPigmentNotSet
import coffee.cypher.hexbound.feature.pigment_storage.mishap.MishapTooManyPigments
import coffee.cypher.hexbound.init.memorizedPigments
import coffee.cypher.hexbound.util.nonBlankSignature
import coffee.cypher.hexbound.util.requireCaster
import net.minecraft.server.network.ServerPlayerEntity

object OpPigmentSave : SpellAction {
    override val argc = 1

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): SpellAction.Result {
        val pattern = args.getPattern(0, 1)
        val caster = env.requireCaster()

        val currentPigment = HexCardinalComponents.FAVORED_PIGMENT[caster].pigment

        if (caster.memorizedPigments.size >= 64 && pattern.nonBlankSignature !in caster.memorizedPigments) {
            throw MishapTooManyPigments()
        }

        if (currentPigment == FrozenPigment.DEFAULT.get()) {
            throw MishapPigmentNotSet()
        }

        return SpellAction.Result(
            Spell(pattern, currentPigment, caster),
            0,
            emptyList()
        )
    }

    private data class Spell(
        val key: HexPattern,
        val value: FrozenPigment,
        val caster: ServerPlayerEntity
    ) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            caster.memorizedPigments[key.nonBlankSignature] = value
        }
    }
}
