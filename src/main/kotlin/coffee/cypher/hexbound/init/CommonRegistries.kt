package coffee.cypher.hexbound.init

import coffee.cypher.hexbound.feature.construct.command.*
import coffee.cypher.hexbound.feature.construct.entity.SpiderConstructEntity
import com.mojang.serialization.Codec
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.DefaultedRegistry
import net.minecraft.util.registry.Registry
import org.quiltmc.qkl.library.registry.RegistryAction
import org.quiltmc.qkl.library.registry.provide
import org.quiltmc.qkl.library.serialization.CodecFactory
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

fun initCommonRegistries() {
    CommonRegistries.init()
    EntityTypes.init()
    ConstructCommandTypes.init()
}

object CommonRegistries {
    lateinit var CONSTRUCT_COMMANDS: DefaultedRegistry<ConstructCommand.Type<*>>

    fun init() {
        CONSTRUCT_COMMANDS = FabricRegistryBuilder.createDefaulted(
            ConstructCommand.Type::class.java,
            Hexbound.id("construct_command"),
            Hexbound.id("no_op")
        ).buildAndRegister()
    }
}

abstract class Initializer<T>(
    registry: Registry<T>
) {
    protected val registry = RegistryAction(Hexbound.MOD_ID, registry)

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

object EntityTypes : Initializer<EntityType<*>>(Registry.ENTITY_TYPE) {
    val SPIDER_CONSTRUCT: EntityType<SpiderConstructEntity> by registry.provide("spider_construct") {
        QuiltEntityTypeBuilder
            .createMob<SpiderConstructEntity>()
            .spawnGroup(SpawnGroup.MISC)
            .entityFactory(::SpiderConstructEntity)
            .setDimensions(EntityDimensions.fixed(1.25f, 0.75f))
            .defaultAttributes(SpiderConstructEntity.createAttributes())
            .build()
    }
}

object ConstructCommandTypes : Initializer<ConstructCommand.Type<*>>(CommonRegistries.CONSTRUCT_COMMANDS) {
    private val codecFactory = CodecFactory {
        codecs {
            named(Vec3d.CODEC, "Vec3d")
            unnamed(Codec.STRING.xmap(UUID::fromString, UUID::toString))
        }
    }
    private inline fun <reified T : ConstructCommand<*, T>> provideType(id: String): Lazy<ConstructCommand.Type<T>> {
        return registry.provide(id) {
            ConstructCommand.Type(codecFactory.create())
        }
    }

    val PICK_UP by provideType<PickUp>("pick_up")
    val DROP_OFF by provideType<DropOff>("drop_off")
    val MOVE_TO by provideType<MoveTo>("move_to")
    val NO_OP by provideType<NoOpCommand>("no_op")
}
