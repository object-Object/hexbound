package coffee.cypher.hexbound.interop.gravityapi.action

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import com.fusionflux.gravity_api.api.GravityChangerAPI
import net.minecraft.util.math.Vec3d

object OpGetGravity : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getEntity(0)
        val grav = GravityChangerAPI.getGravityDirection(target)
        return Vec3d.of(grav.vector).asActionResult
    }
}
