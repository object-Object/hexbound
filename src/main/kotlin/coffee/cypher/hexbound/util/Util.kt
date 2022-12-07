package coffee.cypher.hexbound.util

import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.feature.construct.entity.SpiderConstructEntity
import coffee.cypher.hexbound.feature.item_patterns.iota.ItemStackIota
import coffee.cypher.hexbound.init.config.HexboundConfig
import coffee.cypher.hexbound.mixins.accessor.MutableTextAccessor
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.component.TranslatableComponent
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import org.quiltmc.qkl.library.text.Color
import org.quiltmc.qkl.library.text.buildText
import org.quiltmc.qkl.library.text.color
import org.quiltmc.qkl.library.text.translatable
import java.text.DecimalFormat

val HexPattern.nonBlankSignature: String
    get() = anglesSignature().ifBlank { "empty" }

inline fun <reified T : Entity> List<Iota>.getEntityOfType(errorStub: String, index: Int, argc: Int = 0): T {
    val x = this.getOrElse(index) { throw MishapNotEnoughArgs(index + 1, this.size) }
    if (x is EntityIota) {
        val entity = x.entity
        if (entity is T) {
            return entity
        }
    }

    throw MishapInvalidIota.of(x, if (argc == 0) index else argc - (index + 1), errorStub)
}

fun List<Iota>.getConstruct(index: Int, argc: Int = 0): AbstractConstructEntity =
    getEntityOfType("entity.construct.generic", index, argc)

fun List<Iota>.getSpiderConstruct(index: Int, argc: Int = 0): SpiderConstructEntity =
    getEntityOfType("entity.construct.spider", index, argc)

fun List<Iota>.getItemStack(index: Int, argc: Int = 0): ItemStack {
    val x = this.getOrElse(index) { throw MishapNotEnoughArgs(index + 1, this.size) }

    return (x as? ItemStackIota)?.itemStack
           ?: throw MishapInvalidIota.of(x, if (argc == 0) index else argc - (index + 1), "item_stack")
}

/*
 * For now only used for display name on Robot version constructs
 */
fun redirectSpiderLang(original: String, entity: SpiderConstructEntity? = null): String {
    return if (entity?.isAltModelEnabled == true || HexboundConfig.replaceSpiderConstruct)
        original.replace("construct.spider", "construct.robot")
            .replace("spider_construct", "robot_construct")
    else
        original
}

fun redirectSpiderLang(original: Text, entity: SpiderConstructEntity? = null): Text {
    if (entity?.isAltModelEnabled != true && !HexboundConfig.replaceSpiderConstruct) {
        return original
    }

    val component = original.asComponent()

    val newComponent = if (component is TranslatableComponent)
        TranslatableComponent(redirectSpiderLang(component.key))
    else
        component

    return MutableTextAccessor.create(
        newComponent,
        original.siblings.map(::redirectSpiderLang),
        original.style
    )
}

val DECIMAL_FORMAT = DecimalFormat("#0.#")

fun formatVector(vec: Vec3d): Text {
    return buildText {
        color(Color.RED) {
            translatable(
                "hexbound.vector_format",
                DECIMAL_FORMAT.format(vec.x),
                DECIMAL_FORMAT.format(vec.y),
                DECIMAL_FORMAT.format(vec.z)
            )
        }
    }
}

fun formatVector(vec: Vec3i): Text {
    return buildText {
        return buildText {
            color(Color.RED) {
                translatable(
                    "hexbound.vector_format",
                    vec.x.toString(),
                    vec.y.toString(),
                    vec.z.toString(),
                )
            }
        }
    }
}

fun localizeSide(direction: Direction): Text {
    return Text.translatable("hexbound.direction.${direction.name.lowercase()}")
}
