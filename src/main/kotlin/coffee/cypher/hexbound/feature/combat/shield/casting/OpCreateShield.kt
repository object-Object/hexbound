package coffee.cypher.hexbound.feature.combat.shield.casting

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.xplat.IXplatAbstractions
import coffee.cypher.hexbound.feature.combat.shield.ShieldEntity
import coffee.cypher.hexbound.init.HexboundData
import net.minecraft.command.argument.EntityAnchorArgumentType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d
import org.quiltmc.qkl.library.math.minus
import org.quiltmc.qkl.library.math.plus

object OpCreateShield : SpellAction {
    private val shields = mutableMapOf<ServerPlayerEntity, ShieldEntity>() //TODO pls pls pls don't ship this

    override val argc = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val position = args.getVec3(0, argc)
        val direction = args.getVec3(1, argc).normalize()

        return Triple(
            Spell(position, direction),
            10 * MediaConstants.CRYSTAL_UNIT,
            listOf(ParticleSpray.burst(position, 3.0))
        )
    }

    private class Spell(val position: Vec3d, val direction: Vec3d) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            if (ctx.caster in shields) {
                //shields.getValue(ctx.caster).kill()
            }

            val basePos = position - Vec3d(0.0, 1.3125, 0.0)

            val shield = ShieldEntity(HexboundData.EntityTypes.SHIELD, ctx.world)
            shields[ctx.caster] = shield
            shield.setPosition(basePos)
            shield.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, basePos + direction)
            ctx.world.spawnEntity(shield)
            shield.colorizer = IXplatAbstractions.INSTANCE.getColorizer(ctx.caster)
            shield.lockPosition()
        }
    }
}
