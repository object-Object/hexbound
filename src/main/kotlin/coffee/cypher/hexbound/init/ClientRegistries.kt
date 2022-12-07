package coffee.cypher.hexbound.init

import coffee.cypher.hexbound.feature.construct.rendering.SpiderConstructRenderer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

fun initClientRegistries() {
    EntityRendererRegistry.register(HexboundData.EntityTypes.SPIDER_CONSTRUCT, ::SpiderConstructRenderer)
}
