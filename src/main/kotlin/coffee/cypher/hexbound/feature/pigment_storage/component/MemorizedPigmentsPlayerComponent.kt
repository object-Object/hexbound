package coffee.cypher.hexbound.feature.pigment_storage.component

import at.petrak.hexcasting.api.pigment.FrozenPigment
import dev.onyxstudios.cca.api.v3.component.ComponentV3
import net.minecraft.nbt.NbtCompound

class MemorizedPigmentsPlayerComponent(
    val pigments: MutableMap<String, FrozenPigment>
) : ComponentV3 {
    override fun readFromNbt(tag: NbtCompound) {
        pigments.clear()
        tag.keys.forEach {
            pigments[it] = FrozenPigment.fromNBT(tag.getCompound(it))
        }
    }

    override fun writeToNbt(tag: NbtCompound) {
        pigments.forEach { (k, v) ->
            tag.put(k, v.serializeToNBT())
        }
    }
}
