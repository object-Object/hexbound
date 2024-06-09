package gay.`object`.hexbound.feature.colorizer_storage.action

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.math.HexPattern
import gay.`object`.hexbound.feature.colorizer_storage.mishap.MishapMissingColorizerKey
import gay.`object`.hexbound.init.memorizedColorizers
import gay.`object`.hexbound.util.nonBlankSignature

object OpColorizerDelete : SpellAction {
    override val argc = 1

    override fun execute(
        args: List<Iota>,
        ctx: CastingContext
    ): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val pattern = args.getPattern(0, 1)

        if (pattern.nonBlankSignature !in ctx.caster.memorizedColorizers) {
            throw MishapMissingColorizerKey(pattern)
        }

        return Triple(
            Spell(pattern),
            0,
            emptyList()
        )
    }

    private data class Spell(val key: HexPattern) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            ctx.caster.memorizedColorizers.remove(key.nonBlankSignature)
        }
    }
}
