package coffee.cypher.hexbound.feature.item_patterns.iota

import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.api.utils.downcast
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class ItemIota private constructor(val item: Item) : Iota(Type, item) {
    override fun isTruthy(): Boolean {
        return item != Items.AIR
    }

    override fun toleratesOther(that: Iota): Boolean {
        val otherItem = (that as? ItemIota)?.item ?: return false

        return item == otherItem
    }

    override fun serialize(): NbtElement {
        return NbtCompound().apply {
            putString("item", Registry.ITEM.getId(item).toString())
        }
    }

    object Type : IotaType<Iota>() {
        override fun deserialize(tag: NbtElement, world: ServerWorld): Iota? {
            val itemId = tag.downcast(NbtCompound.TYPE)
                .getString("item")
                .let(Identifier::tryParse)
                         ?: return null

            return CONVERTER.fromItem(Registry.ITEM.get(itemId))
        }

        override fun display(tag: NbtElement): Text {
            val itemId = tag.downcast(NbtCompound.TYPE)
                             .getString("item")
                             .let(Identifier::tryParse)
                         ?: return Text.translatable("hexcasting.spelldata.unknown")

            return Registry.ITEM.get(itemId).name.copy().formatted(Formatting.GOLD)
        }

        override fun color(): Int {
            return 0xFFFFAA00u.toInt()
        }
    }

    interface Converter {
        fun fromItem(item: Item): Iota

        fun toItem(iota: Iota): Item?
    }

    companion object {
        var CONVERTER = object : Converter {
            override fun fromItem(item: Item): Iota {
                return ItemIota(item)
            }

            override fun toItem(iota: Iota): Item? {
                return (iota as? ItemIota)?.item
            }
        }
    }
}
