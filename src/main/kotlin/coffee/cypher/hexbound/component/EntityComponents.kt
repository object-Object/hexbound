package coffee.cypher.hexbound.component

import at.petrak.hexcasting.api.misc.FrozenColorizer
import coffee.cypher.hexbound.init.Hexbound
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy
import net.minecraft.server.network.ServerPlayerEntity

object EntityComponents : EntityComponentInitializer {
    val MEMORIZED_COLORIZERS: ComponentKey<MemorizedColorizersPlayerComponent> =
        ComponentRegistryV3.INSTANCE.getOrCreate(
            Hexbound.id("memorized_colorizers"),
            MemorizedColorizersPlayerComponent::class.java
        )

    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerForPlayers(
            MEMORIZED_COLORIZERS,
            { MemorizedColorizersPlayerComponent(mutableMapOf()) },
            RespawnCopyStrategy.ALWAYS_COPY
        )
    }
}

val ServerPlayerEntity.memorizedColorizers: MutableMap<String, FrozenColorizer>
    get() = EntityComponents.MEMORIZED_COLORIZERS[this].colorizers
