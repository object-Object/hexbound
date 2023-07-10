package coffee.cypher.hexbound.feature.construct.rendering

import coffee.cypher.hexbound.feature.construct.entity.SpiderConstructEntity
import coffee.cypher.hexbound.init.Hexbound
import coffee.cypher.hexbound.init.config.HexboundConfig
import net.minecraft.block.BlockState
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import software.bernie.geckolib.renderer.GeoEntityRenderer

class SpiderConstructRenderer(
    renderManager: EntityRendererFactory.Context
) : GeoEntityRenderer<SpiderConstructEntity>(
    renderManager,
    SpiderConstructModel()
) {
    init {
        addLayer(SpiderConstructTranslucentLayer(this))
    }

    override fun isArmorBone(bone: GeoBone): Boolean {
        return false
    }

    override fun getCameraTransformForItemAtBone(boneItem: ItemStack, boneName: String): ModelTransformation.Mode {
        return ModelTransformation.Mode.FIXED
    }

    override fun preRenderItem(
        poseStack: MatrixStack,
        item: ItemStack,
        boneName: String,
        currentEntity: SpiderConstructEntity,
        bone: IBone
    ) {
        poseStack.push()
        poseStack.scale(0.25f, 0.25f, 0.25f)
        poseStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90f))
    }

    override fun postRenderItem(
        poseStack: MatrixStack,
        item: ItemStack,
        boneName: String,
        currentEntity: SpiderConstructEntity,
        bone: IBone
    ) {
        poseStack.pop()
    }

    override fun preRenderBlock(
        poseStack: MatrixStack?,
        block: BlockState?,
        boneName: String?,
        currentEntity: SpiderConstructEntity?
    ) {
    }

    override fun postRenderBlock(
        poseStack: MatrixStack,
        block: BlockState,
        boneName: String,
        currentEntity: SpiderConstructEntity
    ) {
    }

    override fun getHeldBlockForBone(boneName: String, currentEntity: SpiderConstructEntity): BlockState? {
        return null
    }

    override fun getHeldItemForBone(boneName: String, currentEntity: SpiderConstructEntity): ItemStack? {
        if ("item" in boneName && !currentEntity.heldStack.isEmpty) {
            return currentEntity.heldStack
        }

        return null
    }

    override fun getTextureForBone(boneName: String, currentEntity: SpiderConstructEntity): Identifier? {
        return null
    }
}

class SpiderConstructModel : AnimatedGeoModel<SpiderConstructEntity>() {
    companion object {
        val MODEL_RESOURCE = Hexbound.id("geo/spider_construct.geo.json")
        val TEXTURE_RESOURCE = Hexbound.id("textures/construct/spider_construct.png")
        val ANIMATION_RESOURCE = Hexbound.id("animations/spider_construct.animation.json")
        val LAYER_TEXTURE_RESOURCE = Hexbound.id("textures/construct/spider_construct_translucent.png")

        val ALT_MODEL_RESOURCE = Hexbound.id("geo/robot_construct.geo.json")
        val ALT_TEXTURE_RESOURCE = Hexbound.id("textures/construct/robot_construct.png")
        val ALT_ANIMATION_RESOURCE = Hexbound.id("animations/robot_construct.animation.json")
        val ALT_LAYER_TEXTURE_RESOURCE = Hexbound.id("textures/construct/robot_construct_translucent.png")
    }

    override fun getModelResource(obj: SpiderConstructEntity): Identifier {
        return if (obj.isAltModelEnabled || HexboundConfig.replaceSpiderConstruct)
            ALT_MODEL_RESOURCE
        else
            MODEL_RESOURCE
    }

    override fun getTextureResource(obj: SpiderConstructEntity): Identifier {
        return if (obj.isAltModelEnabled || HexboundConfig.replaceSpiderConstruct)
            ALT_TEXTURE_RESOURCE
        else
            TEXTURE_RESOURCE
    }

    override fun getAnimationResource(animatable: SpiderConstructEntity): Identifier {
        return if (animatable.isAltModelEnabled || HexboundConfig.replaceSpiderConstruct)
            ALT_ANIMATION_RESOURCE
        else
            ANIMATION_RESOURCE
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
        val layerTexture = if (entitylivingbaseIn.isAltModelEnabled || HexboundConfig.replaceSpiderConstruct)
            SpiderConstructModel.ALT_LAYER_TEXTURE_RESOURCE
        else
            SpiderConstructModel.LAYER_TEXTURE_RESOURCE

        val type = RenderLayer.getEntityTranslucentCull(layerTexture)

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
