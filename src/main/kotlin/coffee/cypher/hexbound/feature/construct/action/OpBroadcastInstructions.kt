package coffee.cypher.hexbound.feature.construct.action

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadBlock
import coffee.cypher.hexbound.feature.construct.broadcasting.BroadcastingContext
import coffee.cypher.hexbound.feature.construct.broadcasting.ConstructBroadcasterBlock
import coffee.cypher.hexbound.init.HexboundData

object OpBroadcastInstructions : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val broadcasterPos = args.getBlockPos(0, OpSendInstructions.argc)
        val instructions = args.getList(1, OpSendInstructions.argc)

        ctx.assertVecInRange(broadcasterPos)

        val broadcasterState = ctx.world.getBlockState(broadcasterPos)

        if (!broadcasterState.isOf(HexboundData.Blocks.CONSTRUCT_BROADCASTER)) {
            throw MishapBadBlock.of(broadcasterPos, "construct_broadcaster")
        }

        return Triple(
            Spell(
                ConstructBroadcasterBlock.createBroadcastingContext(ctx.world, broadcasterState, broadcasterPos),
                instructions.toList()
            ),
            0,
            emptyList()
        )
    }

    private class Spell(val context: BroadcastingContext, val instructions: List<Iota>) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            context.broadcast(instructions, ctx)
        }
    }
}
