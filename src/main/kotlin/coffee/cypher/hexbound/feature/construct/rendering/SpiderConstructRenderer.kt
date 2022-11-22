package coffee.cypher.hexbound.feature.construct.rendering

import coffee.cypher.hexbound.feature.construct.entity.SpiderConstructEntity
import coffee.cypher.hexbound.init.Hexbound
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import software.bernie.geckolib3.model.AnimatedGeoModel
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer
import software.bernie.geckolib3.renderers.geo.IGeoRenderer

class SpiderConstructRenderer(renderManager: EntityRendererFactory.Context) : GeoEntityRenderer<SpiderConstructEntity>(
    renderManager,
    SpiderConstructModel()
) {
    init {
        addLayer(SpiderConstructTranslucentLayer(this))
    }
}

class SpiderConstructModel : AnimatedGeoModel<SpiderConstructEntity>() {
    companion object {
        val MODEL_RESOURCE = Hexbound.id("geo/spider_construct.geo.json")
        val TEXTURE_RESOURCE = Hexbound.id("textures/construct/spider_construct.png")
        val ANIMATION_RESOURCE = Hexbound.id("animations/spider_construct.animation.json")
        val LAYER_TEXTURE_RESOURCE = Hexbound.id("textures/construct/spider_construct_translucent.png")
    }

    override fun getModelResource(`object`: SpiderConstructEntity): Identifier {
        return MODEL_RESOURCE
    }

    override fun getTextureResource(`object`: SpiderConstructEntity): Identifier {
        return TEXTURE_RESOURCE
    }

    override fun getAnimationResource(animatable: SpiderConstructEntity): Identifier {
        return ANIMATION_RESOURCE
    }
}

class SpiderConstructTranslucentLayer(
    renderer: IGeoRenderer<SpiderConstructEntity>
) : GeoLayerRenderer<SpiderConstructEntity>(renderer) {
    override fun render(
        matrixStackIn: MatrixStack,
        bufferIn: VertexConsumerProvider,
        packedLightIn: Int,
        entitylivingbaseIn: SpiderConstructEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val type = RenderLayer.getEntityTranslucentCull(SpiderConstructModel.LAYER_TEXTURE_RESOURCE)

        renderer.render(
            entityModel.getModel(SpiderConstructModel.MODEL_RESOURCE),
            entitylivingbaseIn,
            partialTicks,
            type,
            matrixStackIn,
            bufferIn,
            bufferIn.getBuffer(type),
            packedLightIn,
            OverlayTexture.DEFAULT_UV,
            1f,
            1f,
            1f,
            1f
        )
    }

}
