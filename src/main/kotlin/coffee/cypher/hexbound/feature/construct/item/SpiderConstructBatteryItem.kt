package coffee.cypher.hexbound.feature.construct.item

import at.petrak.hexcasting.api.item.MediaHolderItem
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.mediaBarColor
import at.petrak.hexcasting.api.utils.mediaBarWidth
import coffee.cypher.hexbound.init.config.HexboundConfig
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import org.quiltmc.qkl.library.items.itemSettingsOf
import org.quiltmc.qkl.library.nbt.set
import org.quiltmc.qkl.library.text.*

object SpiderConstructBatteryItem : Item(itemSettingsOf(maxCount = 1)), MediaHolderItem {
    val maxCharge: Long
        get() = (HexboundConfig.spiderBatteryChargeRequired * MediaConstants.DUST_UNIT)

    var ItemStack.charge: Long
        get() {
            if (!orCreateNbt.contains("charge")) {
                orCreateNbt["charge"] = 0L
            }

            return orCreateNbt.getLong("charge")
        }
        set(value) {
            orCreateNbt["charge"] = value
        }

    val chargedStack
        get() = defaultStack.apply { charge = maxCharge }

    override fun getMedia(stack: ItemStack): Long {
        return stack.charge
    }

    override fun getMaxMedia(stack: ItemStack): Long {
        return maxCharge
    }

    override fun setMedia(stack: ItemStack, media: Long) {
        stack.charge = media
    }

    override fun canProvideMedia(stack: ItemStack): Boolean {
        return false
    }

    override fun canRecharge(stack: ItemStack): Boolean {
        return true
    }

    override fun isItemBarVisible(stack: ItemStack): Boolean {
        return stack.charge < maxCharge
    }

    override fun getItemBarColor(stack: ItemStack): Int {
        return mediaBarColor(stack.charge, maxCharge)
    }

    override fun getItemBarStep(stack: ItemStack): Int {
        return mediaBarWidth(stack.charge, maxCharge)
    }

    override fun getDefaultStack(): ItemStack {
        return ItemStack(this, 1).also { it.charge }
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        tooltip += buildText {
            val hexColor = Color(0xB38EF3)

            if (stack.charge == maxCharge) {
                color(color = hexColor) {
                    translatable("item.hexbound.spider_construct_battery.full_charge")
                }
            } else {
                val percentage = (stack.charge.toDouble() / maxCharge).toInt()

                val currentText = buildText {
                    color(color = hexColor) {
                        literal((stack.charge / MediaConstants.DUST_UNIT).toString())
                    }
                }

                val maxText = buildText {
                    color(color = hexColor) {
                        literal(HexboundConfig.spiderBatteryChargeRequired.toString())
                    }
                }

                translatable("item.hexbound.spider_construct_battery.charge", percentage, currentText, maxText)
            }

        }
    }

    fun isFullyCharged(stack: ItemStack): Boolean {
        return stack.charge >= maxCharge
    }
}
