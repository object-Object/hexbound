package coffee.cypher.hexbound.feature.item_patterns.iota

import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.api.utils.downcast
import at.petrak.hexcasting.api.utils.serializeToNBT
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import org.quiltmc.qkl.library.text.*

class ItemStackIota(val itemStack: ItemStack) : Iota(Type, itemStack) {
    override fun isTruthy(): Boolean {
        return !itemStack.isEmpty
    }

    override fun toleratesOther(that: Iota): Boolean {
        val otherStack = (that as? ItemStackIota)?.itemStack ?: return false

        return ItemStack.areEqual(itemStack, otherStack)
    }

    override fun serialize(): NbtElement {
        return NbtCompound().apply {
            put("stack", itemStack.serializeToNBT())
        }
    }

    object Type : IotaType<ItemStackIota>() {
        override fun deserialize(tag: NbtElement, world: ServerWorld): ItemStackIota {
            val compound = tag.downcast(NbtCompound.TYPE)
            val stack = ItemStack.fromNbt(compound.getCompound("stack"))

            return ItemStackIota(stack)
        }

        override fun display(tag: NbtElement): Text {
            val compound = tag.downcast(NbtCompound.TYPE)
            val stack = ItemStack.fromNbt(compound.getCompound("stack"))

            return buildText {
                color(Color(0x1E90FF)) {
                    if (stack.isEmpty) {
                        translatable("hexbound.stack.empty")
                    } else {
                        //TODO QKL pls
                        text(Text.translatable("hexbound.stack.number_prefix_format", stack.count))
                        text(stack.name)
                    }
                }
            }
        }

        override fun color(): Int {
            return 0xFF1E90FFu.toInt()
        }

    }
}
