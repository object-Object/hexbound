package coffee.cypher.hexbound.feature.item_patterns.action

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import coffee.cypher.hexbound.util.getItemStack

object OpGetStackSize : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val stack = args.getItemStack(0, argc)

        val count = if (stack.isEmpty)
            0
        else
            stack.count

        return listOf(DoubleIota(count.toDouble()))
    }
}
