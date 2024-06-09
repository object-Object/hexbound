package gay.`object`.hexbound.feature.construct.action

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import gay.`object`.hexbound.feature.construct.mishap.MishapNoConstruct
import gay.`object`.hexbound.util.mixinaccessor.construct

object OpConstructGetSelf : ConstMediaAction {
    override val argc = 0

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val construct = ctx.construct ?: throw MishapNoConstruct()

        return listOf(EntityIota(construct))
    }
}
