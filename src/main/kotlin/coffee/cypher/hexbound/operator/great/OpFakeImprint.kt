package coffee.cypher.hexbound.operator.great

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadBlock
import at.petrak.hexcasting.common.blocks.entity.BlockEntityStoredPlayerImpetus
import coffee.cypher.hexbound.mixinaccessor.useFakeFallback
import net.minecraft.util.math.BlockPos

object OpFakeImprint : SpellAction {
    override val argc = 1

    override fun execute(
        args: List<Iota>,
        ctx: CastingContext
    ): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val pos = args.getVec3(0)
        ctx.assertVecInRange(pos)

        val blockEntity = ctx.world.getBlockEntity(BlockPos(pos))

        if (blockEntity !is BlockEntityStoredPlayerImpetus) {
            throw MishapBadBlock.of(BlockPos(pos), "stored_impetus")
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
