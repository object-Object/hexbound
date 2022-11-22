package coffee.cypher.hexbound.operator

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import net.minecraft.util.Hand

class OpGetHeldItem(private val hand: Hand) : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
//        val stack = when (val holder = args.getChecked<Entity>(0)) {
//            is LivingEntity -> holder.getStackInHand(hand)
//            is ItemFrameEntity ->
//                if (hand == Hand.MAIN_HAND) {
//                    holder.heldItemStack
//                } else {
//                    throw MishapBadEntity.of(holder, "item.read.offhand") //TODO better mishaps!
//                }
//            else -> throw MishapBadEntity.of(holder, "item.read.any") //TODO better mishaps!
//        }
//
//        if (stack.isEmpty) {
//            return Widget.NULL.asSpellResult
//        }
//
//        return spellListOf(FakeItemEntity(ctx.world, stack.copy(), ctx))

        return emptyList()
    }
}
