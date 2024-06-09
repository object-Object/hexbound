package gay.`object`.hexbound.interop

import gay.`object`.hexbound.interop.hexal.HexalInteropManager
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import org.quiltmc.loader.api.QuiltLoader

object InteropManager {
    fun init() {
        if (QuiltLoader.isModLoaded("hexal")) {
            HexalInteropManager.init()
        }
    }

    fun registerEntityComponents(registry: EntityComponentFactoryRegistry) {
        if (QuiltLoader.isModLoaded("hexal")) {
            HexalInteropManager.registerEntityComponents(registry)
        }
    }
}
