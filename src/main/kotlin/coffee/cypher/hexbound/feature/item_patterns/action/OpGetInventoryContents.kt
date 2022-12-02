package coffee.cypher.hexbound.feature.item_patterns.action

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getBlockPos
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.iota.NullIota
import coffee.cypher.hexbound.feature.item_patterns.iota.ItemIota
import coffee.cypher.hexbound.feature.item_patterns.iota.ItemStackIota
import coffee.cypher.kettle.inventory.asIterable
import coffee.cypher.kettle.inventory.getSideAccess
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

class OpGetInventoryContents(val returnStacks: Boolean) : ConstMediaAction {
    override val argc = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val pos = args.getBlockPos(0, argc)
        ctx.assertVecInRange(pos)

        val inventory = ctx.world.getBlockEntity(pos)

        if (inventory == null || inventory !is Inventory) {
            return listOf(NullIota())
        }

        val sideVec = args.getVec3(1, argc)

        val slots = if (sideVec != Vec3d.ZERO && inventory is SidedInventory) {
            inventory.getSideAccess(Direction.getFacing(sideVec.x, sideVec.y, sideVec.z))
        } else {
            inventory
        }

        val items = slots.asIterable().map {
            if (returnStacks) {
                ItemStackIota(it.copy())
            } else {
                ItemIota.CONVERTER.fromItem(it.item)
            }
        }

        return listOf(ListIota(items))
    }
}
