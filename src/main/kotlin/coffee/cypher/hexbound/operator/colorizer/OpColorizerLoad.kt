package coffee.cypher.hexbound.operator.colorizer

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents
import coffee.cypher.hexbound.component.memorizedColorizers
import coffee.cypher.hexbound.util.nonBlankSignature

object OpColorizerLoad : SpellAction {
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
            1,
            emptyList()
        )
    }

    private data class Spell(val key: HexPattern) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            HexCardinalComponents.FAVORED_COLORIZER[ctx.caster].colorizer =
                ctx.caster.memorizedColorizers.getValue(key.nonBlankSignature)
        }
    }
}
