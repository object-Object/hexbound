package coffee.cypher.hexbound.interop

//import coffee.cypher.hexbound.interop.hexal.HexalInteropManager
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexPattern
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import org.quiltmc.loader.api.QuiltLoader

typealias RegisterActionCallback = (pattern: HexPattern, id: String, action: Action, perWorld: Boolean) -> Unit

interface InteropManager {
    fun init()
    fun registerActions(actionCallback: RegisterActionCallback)
    fun registerEntityComponents(registry: EntityComponentFactoryRegistry)
}

object RootInteropManager : InteropManager {
    override fun init() {
        if (QuiltLoader.isModLoaded("hexal")) {
            //HexalInteropManager.init()
        }
    }

    override fun registerActions(actionCallback: RegisterActionCallback) {

    }

    override fun registerEntityComponents(registry: EntityComponentFactoryRegistry) {
        if (QuiltLoader.isModLoaded("hexal")) {
            //HexalInteropManager.registerEntityComponents(registry)
        }
    }
}
