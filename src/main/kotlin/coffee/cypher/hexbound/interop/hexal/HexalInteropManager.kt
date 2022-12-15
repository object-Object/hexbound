package coffee.cypher.hexbound.interop.hexal

import at.petrak.hexcasting.api.spell.iota.Iota
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.feature.item_patterns.iota.ItemIota
import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraft.item.Items
import ram.talia.hexal.api.linkable.LinkableRegistry
import ram.talia.hexal.api.spell.iota.ItemTypeIota

object HexalInteropManager {
    fun init() {
        ItemIota.CONVERTER = HexalItemIotaConverter

        //LinkableRegistry.registerLinkableType(ConstructLinkableType)
        //AbstractConstructEntity.EXTRA_TICK_HANDLERS += ConstructLinkable::handleLinkRendering
    }
}

object HexalItemIotaConverter : ItemIota.Converter {
    override fun fromItem(item: Item): Iota {
        return ItemTypeIota(item)
    }

    override fun toItem(iota: Iota): Item? {
        val type = iota as? ItemTypeIota ?: return null

        return type.either.map(
            { it },
            { block ->
                if (block == Blocks.AIR) {
                    Items.AIR
                } else {
                    val item = block.asItem()

                    item.takeUnless { it == Blocks.AIR }
                }
            })
    }
}
