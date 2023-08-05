package coffee.cypher.hexbound.util

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import org.quiltmc.qkl.library.nbt.set
import java.util.*

data class MemorizedPlayerData(
    val uuid: UUID,
    val name: String,
    val displayName: Text
) {
    fun toNbt(): NbtCompound {
        val result = NbtCompound()

        result.putUuid("uuid", uuid)
        result["name"] = name
        result["displayName"] = Text.Serializer.toJson(displayName)

        return result
    }

    companion object {
        fun fromNbt(nbt: NbtCompound): MemorizedPlayerData {
            val uuid = nbt.getUuid("uuid")
            val name = nbt.getString("name")
            val displayName = Text.Serializer.fromLenientJson(nbt.getString("displayName")) ?: Text.literal(name)

            return MemorizedPlayerData(uuid, name, displayName)
        }

        fun forPlayer(player: PlayerEntity): MemorizedPlayerData {
            return MemorizedPlayerData(
                player.uuid,
                player.gameProfile.name,
                player.displayName
            )
        }
    }
}
