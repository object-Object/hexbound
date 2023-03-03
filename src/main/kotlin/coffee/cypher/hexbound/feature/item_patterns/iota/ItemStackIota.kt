package coffee.cypher.hexbound.feature.item_patterns.iota

import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.api.utils.downcast
import at.petrak.hexcasting.api.utils.serializeToNBT
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import org.quiltmc.qkl.library.text.*

class ItemStackIota private constructor(val itemStack: ItemStack) : Iota(Type, itemStack) {
    override fun isTruthy(): Boolean {
        return !itemStack.isEmpty
    }

    override fun toleratesOther(that: Iota): Boolean {
        val otherStack = (that as? ItemStackIota)?.itemStack ?: return false

        return ItemStack.areEqual(itemStack, otherStack)
    }

    override fun serialize(): NbtElement {
        return NbtCompound().apply {
            //NBT editing calls for a fully-qualified key
            put("hexbound:iota_stack", itemStack.serializeToNBT())
            //default count is a byte, that's annoying
            putInt("hexbound:iota_stack_count", itemStack.count)
        }
    }

    companion object {
        fun createFiltered(originalStack: ItemStack): ItemStackIota {
            val stack = originalStack.copy()

            val workQueue = ArrayDeque<NbtElement>()
            stack.nbt?.let { workQueue.addLast(it) }

            while (!workQueue.isEmpty()) {
                val next = workQueue.removeFirst()

                if (next is NbtList) {
                    workQueue.addAll(next)
                }

                if (next is NbtCompound) {
                    if ("hexbound:iota_stack" in next) {
                        next.remove("hexbound:iota_stack")
                        next.remove("hexbound:iota_stack_count")
                    }

                    workQueue.addAll(next.keys.map { next[it]!! })
                }
            }

            return ItemStackIota(stack)
        }
    }

    object Type : IotaType<ItemStackIota>() {
        override fun deserialize(tag: NbtElement, world: ServerWorld?): ItemStackIota {
            val compound = tag.downcast(NbtCompound.TYPE)
            val stack = if ("hexbound:iota_stack" in compound)
                ItemStack.fromNbt(compound.getCompound("hexbound:iota_stack")).also {
                    it.count = compound.getInt("hexbound:iota_stack_count")
                }
            else
                ItemStack.EMPTY

            return ItemStackIota(stack)
        }

        override fun display(tag: NbtElement): Text {
            val stack = deserialize(tag, null).itemStack

            return buildText {
                color(Color(0x1E90FF)) {
                    if (stack.isEmpty) {
                        translatable("hexbound.stack.empty")
                    } else {
                        translatable("hexbound.stack.format", stack.count, stack.name)
                    }
                }
            }
        }

        override fun color(): Int {
            return 0xFF1E90FFu.toInt()
        }
    }
}
