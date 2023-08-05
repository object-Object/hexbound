package coffee.cypher.hexbound.util.fakeplayer

import com.mojang.datafixers.DataFixer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.stat.ServerStatHandler
import net.minecraft.stat.Stat
import net.minecraft.stat.StatType
import org.quiltmc.loader.api.QuiltLoader

class FakeServerStatHandler(server: MinecraftServer?) : ServerStatHandler(server, QuiltLoader.getConfigDir().toFile()) {
    override fun save() {}
    override fun setStat(player: PlayerEntity, stat: Stat<*>?, value: Int) {}
    override fun parse(dataFixer: DataFixer, json: String) {}
    override fun updateStatSet() {}
    override fun sendStats(player: ServerPlayerEntity) {}
    override fun increaseStat(player: PlayerEntity, stat: Stat<*>?, value: Int) {}
    override fun <T> getStat(type: StatType<T>, stat: T): Int {
        return 0
    }

    override fun getStat(stat: Stat<*>?): Int {
        return 0
    }
}
