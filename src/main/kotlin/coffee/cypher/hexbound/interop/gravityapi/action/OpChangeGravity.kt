package coffee.cypher.hexbound.interop.gravityapi.action

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.fusionflux.gravity_api.api.GravityChangerAPI
import net.minecraft.entity.Entity
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

object OpChangeGravity : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val target = args.getEntity(0, argc)
        val vec = args.getVec3(1, argc)

        val snapped = Direction.getFacing(vec.x, vec.y, vec.z)

        return SpellAction.Result(
            Spell(target, snapped),
            10 * MediaConstants.DUST_UNIT,
            listOf(ParticleSpray(target.pos, Vec3d.of(snapped.vector), 0.1, 0.1))
        )
    }

    private data class Spell(val target: Entity, val dir: Direction) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            GravityChangerAPI.setDefaultGravityDirection(target, dir)
        }
    }
}
