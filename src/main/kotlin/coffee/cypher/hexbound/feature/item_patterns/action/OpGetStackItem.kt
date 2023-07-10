package coffee.cypher.hexbound.feature.item_patterns.action

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import coffee.cypher.hexbound.feature.item_patterns.iota.ItemIota
import coffee.cypher.hexbound.util.getItemStack
import net.minecraft.item.Items

object OpGetStackItem : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val stack = args.getItemStack(0, argc)

        val item = if (stack.isEmpty)
            Items.AIR
        else
            stack.item

        return listOf(ItemIota.CONVERTER.fromItem(item))
    }
}
