package coffee.cypher.hexbound.interop.hexal

import at.petrak.hexcasting.api.spell.iota.Iota
import coffee.cypher.hexbound.feature.item_patterns.iota.ItemIota
import net.minecraft.item.Item
import ram.talia.hexal.api.spell.iota.ItemTypeIota

object HexalInteropManager {
    fun init() {
        ItemIota.CONVERTER = HexalItemIotaConverter
    }
}

object HexalItemIotaConverter : ItemIota.Converter {
    override fun fromItem(item: Item): Iota {
        return ItemTypeIota(item)
    }

    override fun toItem(iota: Iota): Item? {
        return (iota as? ItemTypeIota)?.item //TODO BlockItem handling
    }
}
