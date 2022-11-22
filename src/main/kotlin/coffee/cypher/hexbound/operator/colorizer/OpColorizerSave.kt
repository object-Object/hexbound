package coffee.cypher.hexbound.operator.colorizer

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents
import coffee.cypher.hexbound.component.memorizedColorizers
import coffee.cypher.hexbound.util.nonBlankSignature

object OpColorizerSave : SpellAction {
    override val argc = 1

    override fun execute(
        args: List<Iota>,
        ctx: CastingContext
    ): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val pattern = args.getPattern(0, 1)

        val currentColorizer = HexCardinalComponents.FAVORED_COLORIZER[ctx.caster].colorizer

        @Suppress("ControlFlowWithEmptyBody")
        if (currentColorizer == FrozenColorizer.DEFAULT.get()) {
            //TODO error?
            /* no-op */
        }

        return Triple(
            Spell(pattern, currentColorizer),
            0,
            emptyList()
        )
    }

    private data class Spell(val key: HexPattern, val value: FrozenColorizer) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            ctx.caster.memorizedColorizers[key.nonBlankSignature] = value
        }
    }
}
