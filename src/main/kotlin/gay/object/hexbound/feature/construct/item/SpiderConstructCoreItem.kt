package gay.`object`.hexbound.feature.construct.item

import gay.`object`.hexbound.init.HexboundData
import net.minecraft.item.Item
import org.quiltmc.qkl.library.items.buildItemSettings

object SpiderConstructCoreItem : Item(
    buildItemSettings {
        group(HexboundData.ItemGroups.HEXBOUND)
    }
)
