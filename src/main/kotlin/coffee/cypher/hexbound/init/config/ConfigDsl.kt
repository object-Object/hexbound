package coffee.cypher.hexbound.init.config

import net.minecraft.util.Identifier
import org.quiltmc.config.api.Config
import org.quiltmc.config.api.Constraint
import org.quiltmc.config.api.annotations.Comment
import org.quiltmc.config.api.values.TrackedValue
import org.quiltmc.loader.api.config.QuiltConfig
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

class ConfigDelegate<T : Any> : ReadWriteProperty<Any?, T> {
    lateinit var trackedValue: TrackedValue<T>

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return trackedValue.value()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        trackedValue.setValue(value, true)
    }
}

fun <T : Any> configField(): ConfigDelegate<T> {
    return ConfigDelegate()
}

class SectionBuilder {
    val sections: MutableMap<String, SectionBuilder> = mutableMapOf()
    val values: MutableList<ValueBuilder<*>> = mutableListOf()

    fun buildAsSection(builder: Config.SectionBuilder) {
        sections.forEach {
            builder.section(it.key, it.value::buildAsSection)
        }

        values.forEach {
            builder.field(it.build())
        }
    }

    fun buildAsConfig(builder: Config.Builder) {
        sections.forEach {
            builder.section(it.key, it.value::buildAsSection)
        }

        values.forEach {
            builder.field(it.build())
        }
    }
}

class ValueBuilder<T : Any>(val key: String) {
    val bindings: MutableList<ConfigDelegate<T>> = mutableListOf()
    val constraints: MutableList<Constraint<T>> = mutableListOf()

    val comments: MutableList<String> = mutableListOf()

    lateinit var defaultValue: T

    fun build(): TrackedValue<T> {
        val tracked = TrackedValue.create(defaultValue, key) { builder ->
            constraints.forEach { constraint ->
                builder.constraint(constraint)
            }

            comments.forEach { comment ->
                builder.metadata(Comment.TYPE) { it.add(comment) }
            }
        }

        bindings.forEach {
            it.trackedValue = tracked
        }

        return tracked
    }
}

fun buildConfig(id: Identifier, action: SectionBuilder.() -> Unit): Config {
    return QuiltConfig.create(id.namespace, id.path, Config.Creator {
        SectionBuilder().apply(action).buildAsConfig(it)
    })
}

fun SectionBuilder.section(name: String, action: SectionBuilder.() -> Unit) {
    sections[name] = SectionBuilder().apply(action)
}

fun <T : Any> SectionBuilder.value(key: String, action: ValueBuilder<T>.() -> Unit) {
    values += ValueBuilder<T>(key).apply(action)
}

fun <T : Any> ValueBuilder<T>.comment(comment: String) {
    comments += comment
}

fun ValueBuilder<Int>.allowedRange(intRange: IntRange) {
    constraints += Constraint.range(intRange.first, intRange.last)
}

fun <T : Any> ValueBuilder<T>.bind(property: KProperty0<T>) {
    val delegate = property.also { it.isAccessible = true }.getDelegate()

    if (delegate !is ConfigDelegate<*>) {
        throw IllegalArgumentException("Can only bind to config delegate")
    }

    @Suppress("UNCHECKED_CAST")
    bindings += (delegate as ConfigDelegate<T>)
}
