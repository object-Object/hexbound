package coffee.cypher.hexbound.util

import at.petrak.hexcasting.api.HexAPI
import coffee.cypher.hexbound.init.Hexbound
import com.mojang.authlib.GameProfile
import dev.cafeteria.fakeplayerapi.server.FakePlayerBuilder
import dev.cafeteria.fakeplayerapi.server.FakeServerPlayer
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import java.util.*

// TODO don't cache players maybe idk
// also functions to ID if player is from this factory
object FakePlayerFactory {
    val FAKE_PLAYER_BUILDER_ID = Hexbound.id("fake_player")

    private val constructUuid = UUID(-5169284172464829411L, -8829751243538922937L)
    private val constructProfile = GameProfile(constructUuid, "Minion")


    //TODO mixin to fakeserverplayer, add base player UUID reference so it can be used for construct filtering!
    fun getFakePlayerForImpetus(
        basePlayerUuid: UUID,
        basePlayerProfile: GameProfile,
        serverWorld: ServerWorld
    ): FakeServerPlayer {
        val newPlayer = createFakePlayer(basePlayerProfile, serverWorld)
        newPlayer.setPos(0.0, -200.0, 0.0)

        val enlightenmentAdvancement = serverWorld.server.advancementLoader.get(HexAPI.modLoc("enlightenment"))
        //TODO this does nothing, mixin to enlightenment checks instead
        val progress = newPlayer.advancementTracker.getProgress(enlightenmentAdvancement)
        progress.unobtainedCriteria.forEach {
            progress.obtain(it)
        }

        return newPlayer
    }

    fun getFakePlayerForConstruct(serverWorld: ServerWorld): FakeServerPlayer {
        return createFakePlayer(constructProfile, serverWorld)
    }

    private fun createFakePlayer(profile: GameProfile, serverWorld: ServerWorld): FakeServerPlayer {
        val newPlayer = FakePlayerBuilder(FAKE_PLAYER_BUILDER_ID)
            .create(serverWorld.server, serverWorld, profile)

        newPlayer.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY)
        newPlayer.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY)

        return newPlayer
    }
}
