package gay.`object`.hexbound.feature.pattern_editing.action

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.math.HexDir
import at.petrak.hexcasting.api.spell.math.HexPattern

object OpRotatePattern : ConstMediaAction {
    override val argc = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val pattern = args.getPattern(0, argc)
        val rotation = args.getInt(1, argc)

        val rawOrdinal = pattern.startDir.ordinal + rotation
        val clampedOrdinal = rawOrdinal - (rawOrdinal / 6) * 6

        return HexPattern(HexDir.values()[clampedOrdinal], pattern.angles).asActionResult
    }
}
