package coffee.cypher.hexbound.interop.hexal

import at.petrak.hexcasting.api.casting.iota.Iota
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.feature.item_patterns.iota.ItemIota
import coffee.cypher.hexbound.init.Hexbound
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraft.item.Items
import ram.talia.hexal.api.linkable.LinkableRegistry
import ram.talia.hexal.api.casting.iota.ItemTypeIota

object HexalInteropManager {
    val CONSTRUCT_LINKABLE: ComponentKey<ConstructLinkable> =
        ComponentRegistryV3.INSTANCE.getOrCreate(
            Hexbound.id("construct_linkable"),
            ConstructLinkable::class.java
        )

    fun init() {
        ItemIota.CONVERTER = HexalItemIotaConverter

        LinkableRegistry.registerLinkableType(ConstructLinkableType)
    }

    fun registerEntityComponents(registry: EntityComponentFactoryRegistry) {
        registry.registerFor(AbstractConstructEntity::class.java, CONSTRUCT_LINKABLE, ::ConstructLinkable)
    }
}

fun AbstractConstructEntity.getLinkable(): ConstructLinkable =
    HexalInteropManager.CONSTRUCT_LINKABLE[this]

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
