package coffee.cypher.hexbound.feature.construct.action.crafting

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getItemEntity
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import coffee.cypher.hexbound.feature.construct.item.SpiderConstructBatteryItem
import coffee.cypher.hexbound.init.Hexbound
import coffee.cypher.hexbound.init.HexboundData
import coffee.cypher.hexbound.init.HexboundData.Items.SPIDER_CONSTRUCT_CORE
import coffee.cypher.hexbound.util.getAllay
import net.minecraft.command.argument.EntityAnchorArgumentType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.passive.AllayEntity
import org.quiltmc.qkl.library.math.component1
import org.quiltmc.qkl.library.math.component2
import org.quiltmc.qkl.library.math.component3

object OpCreateSpiderConstruct : SpellAction {
    override val argc = 3

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val allay = args.getAllay(0, argc)
        val coreStack = args.getItemEntity(1, argc)
        val batteryStack = args.getItemEntity(2, argc)

        ctx.assertEntityInRange(allay)
        ctx.assertEntityInRange(coreStack)
        ctx.assertEntityInRange(batteryStack)

        if (coreStack.stack.isEmpty || !coreStack.stack.isOf(SPIDER_CONSTRUCT_CORE)) {
            throw MishapInvalidIota.of(args[1], 1, "spider_component.core")
        }

        if (
            batteryStack.stack.isEmpty ||
            !batteryStack.stack.isOf(SPIDER_CONSTRUCT_CORE) ||
            !SpiderConstructBatteryItem.isFullyCharged(batteryStack.stack)
        ) {
            throw MishapInvalidIota.of(args[2], 0, "spider_component.battery")
        }

        return Triple(
            Spell(allay, coreStack, batteryStack),
            5 * MediaConstants.CRYSTAL_UNIT,
            listOf(ParticleSpray.cloud(coreStack.pos, 1.0))
        )
    }

    private class Spell(val allay: AllayEntity, val coreStack: ItemEntity, val batteryStack: ItemEntity) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            coreStack.stack.decrement(1)
            if (coreStack.stack.isEmpty) {
                coreStack.kill()
            }

            batteryStack.stack.decrement(1)
            if (batteryStack.stack.isEmpty) {
                coreStack.kill()
            }

            val (x, y, z) = allay.pos

            allay.discard()

            val construct = HexboundData.EntityTypes.SPIDER_CONSTRUCT.create(ctx.world)

            if (construct == null) {
                Hexbound.LOGGER.error(
                    "Failed to summon Spider Construct at {}, {}, {}",
                    coreStack.x,
                    coreStack.y,
                    coreStack.z
                )

                return
            }

            construct.setPosition(x, y + 0.25, z)
            construct.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, ctx.caster.pos)
            ctx.world.spawnEntity(construct)
        }

    }
}
