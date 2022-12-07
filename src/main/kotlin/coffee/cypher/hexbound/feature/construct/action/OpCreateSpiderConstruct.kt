package coffee.cypher.hexbound.feature.construct.action

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
import coffee.cypher.hexbound.init.HexboundData.Items.SPIDER_CONSTRUCT_BATTERY
import coffee.cypher.hexbound.init.HexboundData.Items.SPIDER_CONSTRUCT_CORE
import net.minecraft.command.argument.EntityAnchorArgumentType
import net.minecraft.entity.ItemEntity

object OpCreateSpiderConstruct : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val stack1 = args.getItemEntity(0, argc)
        val stack2 = args.getItemEntity(1, argc)

        ctx.assertEntityInRange(stack1)
        ctx.assertEntityInRange(stack2)

        //doubt that happens, but let's cover all bases
        if (stack1.stack.isEmpty) {
            throw MishapInvalidIota.of(args[0], 1, "spider_component")
        }

        if (stack2.stack.isEmpty) {
            throw MishapInvalidIota.of(args[0], 1, "spider_component")
        }

        val (coreStack, batteryStack) = when {
            stack1.stack.isOf(SPIDER_CONSTRUCT_CORE) && stack2.stack.isOf(SPIDER_CONSTRUCT_BATTERY) -> stack1 to stack2
            stack1.stack.isOf(SPIDER_CONSTRUCT_BATTERY) && stack2.stack.isOf(SPIDER_CONSTRUCT_CORE) -> stack2 to stack1
            stack1.stack.isOf(SPIDER_CONSTRUCT_CORE) -> throw MishapInvalidIota.of(
                args[1],
                0,
                "spider_component.battery"
            )

            stack1.stack.isOf(SPIDER_CONSTRUCT_BATTERY) -> throw MishapInvalidIota.of(
                args[1],
                0,
                "spider_component.core"
            )

            else -> throw MishapInvalidIota.of(args[0], 1, "spider_component")
        }

        if (!SpiderConstructBatteryItem.isFullyCharged(batteryStack.stack)) {
            throw MishapInvalidIota.of(args[1], 0, "spider_component.battery")
        }

        return Triple(
            Spell(coreStack, batteryStack),
            5 * MediaConstants.CRYSTAL_UNIT,
            listOf(ParticleSpray.cloud(coreStack.pos, 1.0))
        )
    }

    private class Spell(val coreStack: ItemEntity, val batteryStack: ItemEntity) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            coreStack.stack.decrement(1)
            if (coreStack.stack.isEmpty) {
                coreStack.kill()
            }

            batteryStack.stack.decrement(1)
            if (batteryStack.stack.isEmpty) {
                coreStack.kill()
            }

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

            construct.setPosition(coreStack.x, coreStack.y + 0.25, coreStack.z)
            construct.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, ctx.caster.pos)
            ctx.world.spawnEntity(construct)
        }

    }
}
