package coffee.cypher.hexbound.feature.colorizer_storage.action

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import coffee.cypher.hexbound.feature.colorizer_storage.mishap.MishapMissingColorizerKey
import coffee.cypher.hexbound.init.memorizedColorizers
import coffee.cypher.hexbound.util.nonBlankSignature

object OpColorizerDelete : SpellAction {
    override val argc = 1

    override fun execute(
        args: List<Iota>,
        ctx: CastingEnvironment
    ): SpellAction.Result {
        val pattern = args.getPattern(0, 1)

        if (pattern.nonBlankSignature !in ctx.caster.memorizedColorizers) {
            throw MishapMissingColorizerKey(pattern)
        }

        return SpellAction.Result(
            Spell(pattern),
            0,
            emptyList()
        )
    }

    private data class Spell(val key: HexPattern) : RenderedSpell {
        override fun cast(ctx: CastingEnvironment) {
            ctx.caster.memorizedColorizers.remove(key.nonBlankSignature)
        }
    }
}
