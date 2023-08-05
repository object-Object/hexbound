package coffee.cypher.hexbound.feature.construct.entity

import coffee.cypher.hexbound.util.fakeplayer.FakeServerPlayer
import com.mojang.authlib.GameProfile
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import java.util.*

class ConstructFakePlayer(
    world: ServerWorld,
    val construct: AbstractConstructEntity
) : FakeServerPlayer(world.server, world, CONSTRUCT_PROFILE) {
    fun resetToValidState() {
        unsetRemoved()
        health = maxHealth
    }

    override fun getEyeY(): Double {
        return construct.eyeY
    }

    override fun getDisplayName(): Text {
        return construct.displayName
    }

    companion object {
        val CONSTRUCT_UUID: UUID = UUID.fromString("e4d9ffe8-8f9b-4fda-839f-c854f8771f0c")
        val CONSTRUCT_PROFILE = GameProfile(CONSTRUCT_UUID, "Construct")
    }
}
