package coffee.cypher.hexbound.feature.item_patterns.action

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getItemEntity
import at.petrak.hexcasting.api.spell.iota.Iota
import coffee.cypher.hexbound.feature.item_patterns.iota.ItemStackIota

object OpGetEntityItem : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val entity = args.getItemEntity(0, argc)

        return listOf(ItemStackIota.createFiltered(entity.stack))
    }
}
