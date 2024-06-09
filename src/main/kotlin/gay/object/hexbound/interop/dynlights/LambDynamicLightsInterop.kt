package gay.`object`.hexbound.interop.dynlights

import gay.`object`.hexbound.init.HexboundData
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer

class LambDynamicLightsInterop : DynamicLightsInitializer {
    override fun onInitializeDynamicLights() {
        DynamicLightHandlers.registerDynamicLightHandler(HexboundData.EntityTypes.SHIELD) { 8 }
    }
}
