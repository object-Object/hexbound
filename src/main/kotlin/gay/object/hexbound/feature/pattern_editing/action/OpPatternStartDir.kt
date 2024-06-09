package gay.`object`.hexbound.feature.pattern_editing.action

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getPattern
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota

object OpPatternStartDir : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        return listOf(
            DoubleIota(
                args.getPattern(0, argc)
                    .startDir
                    .ordinal
                    .toDouble()
            )
        )
    }
}
