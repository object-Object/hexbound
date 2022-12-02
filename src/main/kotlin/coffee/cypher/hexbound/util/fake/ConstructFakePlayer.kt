package coffee.cypher.hexbound.util.fake

import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.init.Hexbound
import com.mojang.authlib.GameProfile
import dev.cafeteria.fakeplayerapi.server.FakePlayerBuilder
import net.minecraft.server.world.ServerWorld
import java.util.*

class ConstructFakePlayer(
    world: ServerWorld,
    val construct: AbstractConstructEntity
) : HexboundFakePlayer(CONSTRUCT_BUILDER, world.server, world, CONSTRUCT_PROFILE) {
    override fun getEyeY(): Double {
        return construct.eyeY
    }

    companion object {
        val CONSTRUCT_UUID: UUID = UUID.fromString("e4d9ffe8-8f9b-4fda-839f-c854f8771f0c")
        val CONSTRUCT_PROFILE = GameProfile(CONSTRUCT_UUID, "Construct Figment")
        val CONSTRUCT_BUILDER = FakePlayerBuilder(Hexbound.id("impetus_fake_player"))
    }
}
