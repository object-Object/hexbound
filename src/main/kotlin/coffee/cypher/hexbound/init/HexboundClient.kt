package coffee.cypher.hexbound.init

import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer

object HexboundClient : ClientModInitializer {
    override fun onInitializeClient(mod: ModContainer) {
        initClientRegistries()
    }
}
