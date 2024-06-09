package gay.`object`.hexbound.feature.item_patterns.action

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import gay.`object`.hexbound.util.getItemStack

object OpGetStackSize : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val stack = args.getItemStack(0, argc)

        val count = if (stack.isEmpty)
            0
        else
            stack.count

        return listOf(DoubleIota(count.toDouble()))
    }
}
