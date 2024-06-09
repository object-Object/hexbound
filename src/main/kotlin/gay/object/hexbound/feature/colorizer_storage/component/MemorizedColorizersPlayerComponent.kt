package gay.`object`.hexbound.feature.colorizer_storage.component

import at.petrak.hexcasting.api.misc.FrozenColorizer
import dev.onyxstudios.cca.api.v3.component.ComponentV3
import net.minecraft.nbt.NbtCompound

class MemorizedColorizersPlayerComponent(
    val colorizers: MutableMap<String, FrozenColorizer>
) : ComponentV3 {
    override fun readFromNbt(tag: NbtCompound) {
        colorizers.clear()
        tag.keys.forEach {
            colorizers[it] = FrozenColorizer.fromNBT(tag.getCompound(it))
        }
    }

    override fun writeToNbt(tag: NbtCompound) {
        colorizers.forEach { (k, v) ->
            tag.put(k, v.serializeToNBT())
        }
    }
}
