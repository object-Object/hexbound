package gay.`object`.hexbound.feature.pattern_editing.action

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getPattern
import at.petrak.hexcasting.api.spell.getPositiveIntUnderInclusive
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.PatternIota
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota

object OpPatternHead : ConstMediaAction {
    override val argc = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val pattern = args.getPattern(0, argc)
        val lineCount = args.getPositiveIntUnderInclusive(1, pattern.angles.size + 1, argc)

        if (lineCount == 0) {
            throw MishapInvalidIota.of(args[1], 0, "int.positive.less.equal", pattern.angles.size + 1)
        }

        val newPattern = HexPattern(pattern.startDir, pattern.angles.take(lineCount - 1).toMutableList())
        return listOf(PatternIota(newPattern))
    }
}
