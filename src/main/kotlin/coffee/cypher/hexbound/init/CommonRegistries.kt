package coffee.cypher.hexbound.init

import coffee.cypher.hexbound.feature.construct.entity.SpiderConstructEntity
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.util.registry.Registry

fun initCommonRegistries() {
    EntityTypes.init()
}

object EntityTypes {
    lateinit var SPIDER_CONSTRUCT: EntityType<SpiderConstructEntity>

    fun init() {
        SPIDER_CONSTRUCT = Registry.register(
            Registry.ENTITY_TYPE,
            Hexbound.id("spider_construct"),
            FabricEntityTypeBuilder
                .create(SpawnGroup.MISC, ::SpiderConstructEntity)
                .dimensions(EntityDimensions.fixed(1f, 1f))
                .build()
        )

        FabricDefaultAttributeRegistry.register(SPIDER_CONSTRUCT, SpiderConstructEntity.createAttributes())
    }
}
