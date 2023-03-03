package coffee.cypher.hexbound.feature.combat.shield

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import coffee.cypher.hexbound.init.HexboundData
import net.minecraft.command.argument.EntityAnchorArgumentType
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import org.quiltmc.qkl.library.math.minus
import org.quiltmc.qkl.library.math.plus
import java.util.*
import kotlin.collections.ArrayDeque

class OpCreateShield(val visualType: ShieldEntity.VisualType) : SpellAction {
    override val argc = 3

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val position = args.getVec3(0, argc)
        val direction = args.getVec3(1, argc).normalize()
        val durationSeconds = args.getPositiveDouble(2, argc)

        return Triple(
            Spell(position, direction, visualType, (durationSeconds * 20).toInt()),
            (durationSeconds * MediaConstants.DUST_UNIT * 2).toInt(),
            listOf(ParticleSpray.burst(position, 3.0))
        )
    }

    private class Spell(
        val position: Vec3d,
        val direction: Vec3d,
        val visualType: ShieldEntity.VisualType,
        val maxAge: Int
    ) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            val shields = cleanAndGetShieldsFor(ctx.caster)

            if (shields.size >= MAX_SHIELDS) {
                repeat(shields.size + 1 - MAX_SHIELDS) {
                    shields.removeFirst().discard()
                }
            }

            val basePos = position - Vec3d(0.0, 1.3125, 0.0)

            val shield = ShieldEntity(
                HexboundData.EntityTypes.SHIELD,
                ctx.world,
                ctx.caster,
                maxAge,
                visualType
            )
            shields.addLast(shield)
            shield.setPosition(basePos)
            shield.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, basePos + direction)
            ctx.world.spawnEntity(shield)
            shield.lockPosition()
        }
    }

    companion object {
        const val MAX_SHIELDS = 10

        private val shieldMap = WeakHashMap<Entity, ArrayDeque<ShieldEntity>>()

        fun cleanAndGetShieldsFor(owner: Entity): ArrayDeque<ShieldEntity> {
            return shieldMap.getOrPut(owner, ::ArrayDeque).apply { removeIf { it.isRemoved || !it.isAlive } }
        }
    }
}
