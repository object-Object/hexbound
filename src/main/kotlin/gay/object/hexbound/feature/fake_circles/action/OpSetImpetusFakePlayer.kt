package gay.`object`.hexbound.feature.fake_circles.action

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadBlock
import at.petrak.hexcasting.common.blocks.entity.BlockEntityStoredPlayerImpetus
import gay.`object`.hexbound.feature.fake_circles.mishap.MishapTargetNotEnlightened
import gay.`object`.hexbound.util.mixinaccessor.useFakeFallback
import net.minecraft.server.network.ServerPlayerEntity

object OpSetImpetusFakePlayer : SpellAction {
    override val argc = 1

    override val isGreat = true
    override val alwaysProcessGreatSpell = false

    override fun execute(
        args: List<Iota>,
        ctx: CastingContext
    ): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val pos = args.getBlockPos(0)
        ctx.assertVecInRange(pos)

        val blockEntity = ctx.world.getBlockEntity(pos)

        if (blockEntity !is BlockEntityStoredPlayerImpetus) {
            throw MishapBadBlock.of(pos, "stored_impetus")
        }

        val player = blockEntity.storedPlayer


        if (player !is ServerPlayerEntity) {
            throw MishapBadBlock.of(pos, "stored_impetus")
        }

        val adv = ctx.world.server.advancementLoader.get(HexAPI.modLoc("enlightenment"))
        val advs = player.advancementTracker
        val enlightened = advs.getProgress(adv).isDone

        if (!enlightened) {
            throw MishapTargetNotEnlightened(player)
        }

        return Triple(
            Spell(blockEntity),
            MediaConstants.CRYSTAL_UNIT * 10,
            emptyList()
        )
    }

    private class Spell(
        val impetus: BlockEntityStoredPlayerImpetus
    ) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            impetus.useFakeFallback = true
        }
    }
}
