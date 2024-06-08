@file:Suppress("unused")

package coffee.cypher.hexbound.init

import coffee.cypher.hexbound.feature.combat.shield.ShieldEntity
import coffee.cypher.hexbound.feature.combat.status_effects.ReducedAmbitStatusEffect
import coffee.cypher.hexbound.feature.construct.broadcasting.ConstructBroadcasterBlock
import coffee.cypher.hexbound.feature.construct.command.*
import coffee.cypher.hexbound.feature.construct.entity.SpiderConstructEntity
import coffee.cypher.hexbound.feature.construct.item.SpiderConstructBatteryItem
import coffee.cypher.hexbound.feature.construct.item.SpiderConstructCoreItem
import coffee.cypher.hexbound.feature.media_attachment.STATIC_MEDIA_ATTACHMENT
import com.mojang.serialization.Codec
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.registry.DefaultedRegistry
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.quiltmc.qkl.library.items.itemSettingsOf
import org.quiltmc.qkl.library.registry.RegistryAction
import org.quiltmc.qkl.library.registry.provide
import org.quiltmc.qkl.library.serialization.CodecFactory
import java.util.*
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object HexboundData : DataInitializer() {
    override fun init() {
        ModRegistries.init()
        super.init()
        ItemGroups.fillGroups()
        STATIC_MEDIA_ATTACHMENT
    }

    object ModRegistries {
        lateinit var CONSTRUCT_COMMANDS_KEY: RegistryKey<Registry<ConstructCommand.Type<*>>>
        lateinit var CONSTRUCT_COMMANDS: DefaultedRegistry<ConstructCommand.Type<*>>

        fun init() {
            CONSTRUCT_COMMANDS_KEY = RegistryKey.ofRegistry(Hexbound.id("construct_command"))

            CONSTRUCT_COMMANDS = FabricRegistryBuilder.createDefaulted(
                CONSTRUCT_COMMANDS_KEY,
                Hexbound.id("no_op")
            ).buildAndRegister()
        }
    }

    object ItemGroups : Initializer<ItemGroup>(Registries.ITEM_GROUP) {
        val HEXBOUND: ItemGroup by provide("item_group") {
            FabricItemGroup.builder()
                .name(Text.translatable("hexbound.item_group"))
                .icon(Items.SPIDER_CONSTRUCT_CORE::getDefaultStack)
                .build()
        }

        fun fillGroups() {
            ItemGroupEvents.modifyEntriesEvent(Registries.ITEM_GROUP.getKey(HEXBOUND).get()).register {
                it.addItem(Items.CONSTRUCT_BROADCASTER)
                it.addItem(Items.SPIDER_CONSTRUCT_CORE)

                it.addStack(Items.SPIDER_CONSTRUCT_BATTERY.defaultStack)
                it.addStack(Items.SPIDER_CONSTRUCT_BATTERY.chargedStack)
            }
        }
    }

    object EntityTypes : Initializer<EntityType<*>>(Registries.ENTITY_TYPE) {
        val SPIDER_CONSTRUCT: EntityType<SpiderConstructEntity> by provide("spider_construct") {
            SpiderConstructEntity.createType()
        }

        val SHIELD: EntityType<ShieldEntity> by provide("shield") {
            ShieldEntity.createType()
        }
    }

    object ConstructCommandTypes : Initializer<ConstructCommand.Type<*>>(ModRegistries.CONSTRUCT_COMMANDS) {
        private val codecFactory = CodecFactory {
            codecs {
                named(Vec3d.CODEC, "Vec3d")
                named(BlockPos.CODEC, "BlockPos")
                unnamed(Codec.STRING.xmap(UUID::fromString, UUID::toString))
            }
        }

        private inline fun <reified T : ConstructCommand<T>> provideType(id: String) = provide(id) {
            ConstructCommand.Type<T>(codecFactory.create())
        }

        val PICK_UP by provideType<PickUp>("pick_up")
        val DROP_OFF by provideType<DropOff>("drop_off")
        val MOVE_TO by provideType<MoveTo>("move_to")
        val NO_OP by provideType<NoOpCommand>("no_op")
        val HARVEST by provideType<Harvest>("harvest")
        val USE_ON_BLOCK by provideType<UseItemOnBlock>("use_on_block")
    }

    object Blocks : Initializer<Block>(Registries.BLOCK) {
        val CONSTRUCT_BROADCASTER by provide("construct_broadcaster") {
            ConstructBroadcasterBlock
        }
    }

    object Items : Initializer<Item>(Registries.ITEM) {
        val SPIDER_CONSTRUCT_BATTERY by provide("spider_construct_battery") {
            SpiderConstructBatteryItem
        }

        val SPIDER_CONSTRUCT_CORE by provide("spider_construct_core") {
            SpiderConstructCoreItem
        }

        val CONSTRUCT_BROADCASTER by provide("construct_broadcaster") {
            BlockItem(ConstructBroadcasterBlock, itemSettingsOf())
        }
    }

    object StatusEffects : Initializer<StatusEffect>(Registries.STATUS_EFFECT) {
        val REDUCED_AMBIT by provide("reduced_ambit") {
            ReducedAmbitStatusEffect()
        }
    }
}

abstract class DataInitializer {
    private val initializers = mutableListOf<Initializer<*>>()

    open fun init() {
        this::class.nestedClasses.forEach {
            it.objectInstance
        }

        initializers.forEach {
            it.init()
            Hexbound.LOGGER.debug("Registered {}", it)
        }
    }

    abstract inner class Initializer<T : Any>(
        registry: Registry<T>
    ) {
        private val registry = RegistryAction(Hexbound.MOD_ID, registry)
        private val registryDelegates = mutableListOf<RegistryDelegate<*>>()

        init {
            @Suppress("LeakingThis")
            initializers += this
        }

        fun init() {
            registryDelegates.forEach {
                it.getValue(this, it.property)
            }
        }

        protected fun <V: T> provide(path: String, init: () -> V): PropertyDelegateProvider<Initializer<T>, RegistryDelegate<V>> =
            RegistryDelegateProvider(path, init)

        protected inner class RegistryDelegate<V : T>(
            val property: KProperty<*>,
            path: String,
            init: () -> V
        ) : ReadOnlyProperty<Initializer<T>, V> {
            private val value: V by registry.provide(path, init)

            override fun getValue(thisRef: Initializer<T>, property: KProperty<*>): V {
                return value
            }
        }

        protected inner class RegistryDelegateProvider<V: T>(val path: String, val init: () -> V) : PropertyDelegateProvider<Initializer<T>, RegistryDelegate<V>> {
            override fun provideDelegate(thisRef: Initializer<T>, property: KProperty<*>): RegistryDelegate<V> {
                val delegate = RegistryDelegate(property, path, init)

                registryDelegates += delegate

                return delegate
            }
        }
    }
}
