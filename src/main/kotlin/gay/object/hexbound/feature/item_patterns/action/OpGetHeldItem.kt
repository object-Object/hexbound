package gay.`object`.hexbound.feature.item_patterns.action

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadEntity
import gay.`object`.hexbound.feature.item_patterns.iota.ItemStackIota
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.util.Hand

class OpGetHeldItem(private val hand: Hand) : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val stack = when (val holder = args.getEntity(0)) {
            is LivingEntity -> holder.getStackInHand(hand)
            is ItemFrameEntity ->
                if (hand == Hand.MAIN_HAND) {
                    holder.heldItemStack
                } else {
                    throw MishapBadEntity.of(holder, "item.read.offhand")
                }
            is ItemEntity ->
                if (hand == Hand.MAIN_HAND) {
                    holder.stack
                } else {
                    throw MishapBadEntity.of(holder, "item.read.offhand")
                }
            else -> throw MishapBadEntity.of(holder, "item.read.any")
        }

        return listOf(ItemStackIota.createFiltered(stack.copy()))
    }
}
