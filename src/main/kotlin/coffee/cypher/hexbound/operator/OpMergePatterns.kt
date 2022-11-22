package coffee.cypher.hexbound.operator

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.math.HexPattern

object OpMergePatterns : ConstMediaAction {
    override val argc = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val startPattern = args.getPattern(0, argc)
        val extensionPattern = args.getPattern(1, argc)

        val endDir = startPattern.angles.fold(startPattern.startDir) { dir, angle -> dir.rotatedBy(angle) }
        println("end dir of $startPattern was $endDir")
        val joinAngle = extensionPattern.startDir.angleFrom(endDir)
        println("${extensionPattern.startDir} angleFrom $endDir = $joinAngle")

        return HexPattern(
            startPattern.startDir,
            (startPattern.angles + joinAngle + extensionPattern.angles).toMutableList()
        ).asActionResult
    }
}
