package coffee.cypher.hexbound.interop.gravityapi

import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import coffee.cypher.hexbound.interop.InteropManager
import coffee.cypher.hexbound.interop.RegisterActionCallback
import coffee.cypher.hexbound.interop.gravityapi.action.OpChangeGravity
import coffee.cypher.hexbound.interop.gravityapi.action.OpGetGravity
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry

object GravityApiInteropManager : InteropManager {
    override fun init() {
    }

    override fun registerActions(actionCallback: RegisterActionCallback) {
        actionCallback(
            HexPattern.fromAngles("wawawddew", HexDir.NORTH_EAST),
            "interop/gravity/get",
            OpGetGravity,
            false
        )

        actionCallback(
            HexPattern.fromAngles("wdwdwaaqw", HexDir.NORTH_WEST),
            "interop/gravity/set",
            OpChangeGravity,
            false
        )
    }

    override fun registerEntityComponents(registry: EntityComponentFactoryRegistry) {
    }
}
