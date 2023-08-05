package coffee.cypher.hexbound.feature.pigment_storage.action

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents
import coffee.cypher.hexbound.feature.pigment_storage.mishap.MishapMissingPigmentKey
import coffee.cypher.hexbound.init.memorizedPigments
import coffee.cypher.hexbound.util.nonBlankSignature
import coffee.cypher.hexbound.util.requireCaster
import net.minecraft.server.network.ServerPlayerEntity

object OpPigmentLoad : SpellAction {
    override val argc = 1

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): SpellAction.Result {
        val pattern = args.getPattern(0, 1)
        val caster = env.requireCaster()

        if (pattern.nonBlankSignature !in caster.memorizedPigments) {
            throw MishapMissingPigmentKey(pattern)
        }

        return SpellAction.Result(
            Spell(pattern, caster),
            1,
            emptyList()
        )
    }

    private data class Spell(val key: HexPattern, val caster: ServerPlayerEntity) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            HexCardinalComponents.FAVORED_PIGMENT[caster].pigment =
                caster.memorizedPigments.getValue(key.nonBlankSignature)
        }
    }
}
