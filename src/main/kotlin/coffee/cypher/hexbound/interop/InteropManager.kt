package coffee.cypher.hexbound.interop

import coffee.cypher.hexbound.interop.hexal.HexalInteropManager
import org.quiltmc.loader.api.QuiltLoader

object InteropManager {
    fun init() {
        if (QuiltLoader.isModLoaded("hexal")) {
            HexalInteropManager.init()
        }
    }
}
