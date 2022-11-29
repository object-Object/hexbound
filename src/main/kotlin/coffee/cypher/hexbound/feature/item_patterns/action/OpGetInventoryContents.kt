package coffee.cypher.hexbound.feature.item_patterns.action

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getBlockPos
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadBlock
import coffee.cypher.hexbound.feature.item_patterns.iota.ItemStackIota
import net.minecraft.inventory.Inventory

object OpGetInventoryContents : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val pos = args.getBlockPos(0, argc)
        ctx.assertVecInRange(pos)

        val inventory = ctx.world.getBlockEntity(pos)

        if (inventory == null || inventory !is Inventory) {
            throw MishapBadBlock.of(pos, "inventory.basic")
        }

        return (0 until inventory.size()).map {
            ItemStackIota(inventory.getStack(it))
        }
    }
}
