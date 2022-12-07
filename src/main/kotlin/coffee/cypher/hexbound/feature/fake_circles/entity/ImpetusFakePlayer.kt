package coffee.cypher.hexbound.feature.fake_circles.entity

import at.petrak.hexcasting.common.blocks.entity.BlockEntityStoredPlayerImpetus
import coffee.cypher.hexbound.init.Hexbound
import coffee.cypher.hexbound.util.HexboundFakePlayer
import com.mojang.authlib.GameProfile
import dev.cafeteria.fakeplayerapi.server.FakePlayerBuilder
import net.minecraft.server.world.ServerWorld
import java.util.*

class ImpetusFakePlayer(
    world: ServerWorld,
    val impetus: BlockEntityStoredPlayerImpetus
) : HexboundFakePlayer(IMPETUS_BUILDER, world.server, world, IMPETUS_PROFILE) {
    init {
        setPos(0.0, -200.0, 0.0)
    }

    companion object {
        val IMPETUS_UUID: UUID = UUID.fromString("223dbe38-f6e1-44aa-8bc3-971f36c87b54")
        val IMPETUS_PROFILE = GameProfile(IMPETUS_UUID, "Impetus Figment")
        val IMPETUS_BUILDER = FakePlayerBuilder(Hexbound.id("impetus_fake_player"))
    }
}
