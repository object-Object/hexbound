package coffee.cypher.hexbound.feature.item_patterns.action

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.NullIota
import coffee.cypher.hexbound.feature.item_patterns.iota.ItemIota
import coffee.cypher.hexbound.feature.item_patterns.iota.ItemStackIota
import coffee.cypher.kettle.inventory.asIterable
import coffee.cypher.kettle.inventory.getSideAccess
import coffee.cypher.kettle.math.toDoubleVector
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

class OpGetInventoryContents(val returnStacks: Boolean) : ConstMediaAction {
    override val argc = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val pos = args.getBlockPos(0, argc)
        env.assertVecInRange(pos.toDoubleVector())

        val inventory = env.world.getBlockEntity(pos)

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
                ItemStackIota.createFiltered(it.copy())
            } else {
                ItemIota.CONVERTER.fromItem(it.item)
            }
        }

        return listOf(ListIota(items))
    }
}
