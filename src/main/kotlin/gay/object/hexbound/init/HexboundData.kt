@file:Suppress("unused")

package gay.`object`.hexbound.init

import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import gay.`object`.hexbound.feature.combat.shield.ShieldEntity
import gay.`object`.hexbound.feature.combat.status_effects.ReducedAmbitStatusEffect
import gay.`object`.hexbound.feature.construct.broadcasting.ConstructBroadcasterBlock
import gay.`object`.hexbound.feature.construct.entity.SpiderConstructEntity
import gay.`object`.hexbound.feature.construct.item.SpiderConstructBatteryItem
import gay.`object`.hexbound.feature.construct.item.SpiderConstructCoreItem
import gay.`object`.hexbound.feature.item_patterns.iota.ItemIota
import gay.`object`.hexbound.feature.item_patterns.iota.ItemStackIota
import gay.`object`.hexbound.feature.media_attachment.STATIC_MEDIA_ATTACHMENT
import gay.`object`.hexbound.feature.construct.command.*
import com.mojang.serialization.Codec
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.DefaultedRegistry
import net.minecraft.util.registry.Registry
import org.quiltmc.qkl.library.items.itemSettingsOf
import org.quiltmc.qkl.library.registry.RegistryAction
import org.quiltmc.qkl.library.registry.provide
import org.quiltmc.qkl.library.serialization.CodecFactory
import org.quiltmc.qsl.item.group.api.QuiltItemGroup
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

object HexboundData : DataInitializer() {
    fun init() {
        Registries.init()
        ItemGroups.init()
        initRegistries()
        STATIC_MEDIA_ATTACHMENT
    }

    object Registries {
        lateinit var CONSTRUCT_COMMANDS: DefaultedRegistry<ConstructCommand.Type<*>>

        fun init() {
            CONSTRUCT_COMMANDS = FabricRegistryBuilder.createDefaulted(
                ConstructCommand.Type::class.java,
                Hexbound.id("construct_command"),
                Hexbound.id("no_op")
            ).buildAndRegister()
        }
    }

    object ItemGroups {
        lateinit var HEXBOUND: ItemGroup

        fun init() {
            HEXBOUND = QuiltItemGroup.createWithIcon(Hexbound.id("hexbound_group")) {
                Items.SPIDER_CONSTRUCT_CORE.defaultStack
            }
        }
    }

    object EntityTypes : Initializer<EntityType<*>>(Registry.ENTITY_TYPE) {
        val SPIDER_CONSTRUCT: EntityType<SpiderConstructEntity> by registry.provide("spider_construct") {
            SpiderConstructEntity.createType()
        }

        val SHIELD : EntityType<ShieldEntity> by registry.provide("shield") {
            ShieldEntity.createType()
        }
    }

    object IotaTypes : Initializer<IotaType<*>>(HexIotaTypes.REGISTRY) {
        val ITEM_STACK by registry.provide("item_stack") {
            ItemStackIota.Type
        }

        val ITEM by registry.provide("item") {
            ItemIota.Type
        }
    }

    object ConstructCommandTypes : Initializer<ConstructCommand.Type<*>>(Registries.CONSTRUCT_COMMANDS) {
        private val codecFactory = CodecFactory {
            codecs {
                named(Vec3d.CODEC, "Vec3d")
                named(BlockPos.CODEC, "BlockPos")
                unnamed(Codec.STRING.xmap(UUID::fromString, UUID::toString))
            }
        }

        private inline fun <reified T : ConstructCommand<T>> provideType(id: String): Lazy<ConstructCommand.Type<T>> {
            return registry.provide(id) {
                ConstructCommand.Type(codecFactory.create())
            }
        }

        val PICK_UP by provideType<PickUp>("pick_up")
        val DROP_OFF by provideType<DropOff>("drop_off")
        val MOVE_TO by provideType<MoveTo>("move_to")
        val NO_OP by provideType<NoOpCommand>("no_op")
        val HARVEST by provideType<Harvest>("harvest")
        val USE_ON_BLOCK by provideType<UseItemOnBlock>("use_on_block")
    }

    object Blocks : Initializer<Block>(Registry.BLOCK) {
        val CONSTRUCT_BROADCASTER by registry.provide("construct_broadcaster") {
            ConstructBroadcasterBlock
        }
    }

    object Items : Initializer<Item>(Registry.ITEM) {
        val SPIDER_CONSTRUCT_BATTERY by registry.provide("spider_construct_battery") {
            SpiderConstructBatteryItem
        }

        val SPIDER_CONSTRUCT_CORE by registry.provide("spider_construct_core") {
            SpiderConstructCoreItem
        }

        val CONSTRUCT_BROADCASTER by registry.provide("construct_broadcaster") {
            BlockItem(ConstructBroadcasterBlock, itemSettingsOf(group = ItemGroups.HEXBOUND))
        }
    }

    object StatusEffects : Initializer<StatusEffect>(Registry.STATUS_EFFECT) {
        val REDUCED_AMBIT by registry.provide("reduced_ambit") {
            ReducedAmbitStatusEffect()
        }
    }
}

abstract class DataInitializer {
    private val initializers = mutableListOf<Initializer<*>>()

    fun initRegistries() {
        this::class.nestedClasses.forEach {
            it.objectInstance
        }

        initializers.forEach {
            it.init()
            Hexbound.LOGGER.debug("Registered {}", it)
        }
    }

    abstract inner class Initializer<T>(
        registry: Registry<T>
    ) {
        protected val registry = RegistryAction(Hexbound.MOD_ID, registry)

        init {
            @Suppress("LeakingThis")
            initializers += this
        }

        fun init() {
            initClass(this)
        }

        private fun <T : Any> initClass(instance: T) {
            @Suppress("UNCHECKED_CAST")
            val klass = instance::class as KClass<T>

            klass.declaredMemberProperties.onEach {
                it.isAccessible = true
            }.forEach {
                val delegate = it.getDelegate(instance)

                if (delegate is Lazy<*>) {
                    delegate.getValue(instance, it)
                }
            }
        }
    }
}
