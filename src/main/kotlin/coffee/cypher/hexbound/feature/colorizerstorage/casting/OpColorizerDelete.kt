package coffee.cypher.hexbound.feature.colorizerstorage.casting

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.math.HexPattern
import coffee.cypher.hexbound.init.memorizedColorizers
import coffee.cypher.hexbound.util.nonBlankSignature

object OpColorizerDelete : SpellAction {
    override val argc = 1

    override fun execute(
        args: List<Iota>,
        ctx: CastingContext
    ): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val pattern = args.getPattern(0, 1)

        if (pattern.nonBlankSignature !in ctx.caster.memorizedColorizers) {
            throw IllegalArgumentException() // TODO mishap!
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
