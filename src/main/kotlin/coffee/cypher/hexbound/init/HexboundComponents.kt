package coffee.cypher.hexbound.init

import at.petrak.hexcasting.api.pigment.FrozenPigment
import coffee.cypher.hexbound.feature.pigment_storage.component.MemorizedPigmentsPlayerComponent
import coffee.cypher.hexbound.interop.RootInteropManager
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy
import net.minecraft.server.network.ServerPlayerEntity

object HexboundComponents : EntityComponentInitializer {
    val MEMORIZED_PIGMENTS: ComponentKey<MemorizedPigmentsPlayerComponent> =
        ComponentRegistryV3.INSTANCE.getOrCreate(
            Hexbound.id("memorized_pigments"),
            MemorizedPigmentsPlayerComponent::class.java
        )

    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerForPlayers(
            MEMORIZED_PIGMENTS,
            { MemorizedPigmentsPlayerComponent(mutableMapOf()) },
            RespawnCopyStrategy.ALWAYS_COPY
        )

        RootInteropManager.registerEntityComponents(registry)
    }
}

val ServerPlayerEntity.memorizedPigments: MutableMap<String, FrozenPigment>
    get() = HexboundComponents.MEMORIZED_PIGMENTS[this].pigments
