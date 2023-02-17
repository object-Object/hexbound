package coffee.cypher.hexbound.feature.pattern_editing.action

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getPattern
import at.petrak.hexcasting.api.spell.getPositiveIntUnderInclusive
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.PatternIota
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota

object OpPatternTail : ConstMediaAction {
    override val argc = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val pattern = args.getPattern(0, OpPatternHead.argc)
        val lineCount = args.getPositiveIntUnderInclusive(1, pattern.angles.size + 1, OpPatternHead.argc)

        if (lineCount == 0) {
            throw MishapInvalidIota.of(args[1], 0, "int.positive.less.equal", pattern.angles.size + 1)
        }

        val angleCount = lineCount - 1

        val newStartDir = pattern.angles.dropLast(angleCount).fold(pattern.startDir) { dir, angle -> dir.rotatedBy(angle) }

        val newPattern = HexPattern(newStartDir, pattern.angles.takeLast(angleCount).toMutableList())
        return listOf(PatternIota(newPattern))
    }
}
