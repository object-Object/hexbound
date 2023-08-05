package coffee.cypher.hexbound.util.fakeplayer

import coffee.cypher.hexbound.mixins.fakeplayer.ServerPlayerEntityAccessor
import com.mojang.authlib.GameProfile
import net.minecraft.entity.LivingEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld

@Suppress("LeakingThis")
open class FakeServerPlayer protected constructor(server: MinecraftServer?, world: ServerWorld?, profile: GameProfile?) :
    ServerPlayerEntity(server, world, profile) {
    init {
        networkHandler = FakeServerPlayNetworkHandler(server, FakeClientConnection.SERVER_FAKE_CONNECTION, this)
        (this as ServerPlayerEntityAccessor).setAdvancementTracker(FakePlayerAdvancementTracker(this))
        (this as ServerPlayerEntityAccessor).setStatHandler(FakeServerStatHandler(server))
    }

    override fun tick() {}
    fun canBeTarget(otherEntity: LivingEntity?) = false
}
