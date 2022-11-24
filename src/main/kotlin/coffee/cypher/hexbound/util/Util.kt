package coffee.cypher.hexbound.util

import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.feature.construct.entity.SpiderConstructEntity
import coffee.cypher.hexbound.init.config.HexboundConfig
import coffee.cypher.hexbound.mixins.accessor.MutableTextAccessor
import net.minecraft.entity.Entity
import net.minecraft.text.Text
import net.minecraft.text.component.TranslatableComponent

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

fun List<Iota>.getConstruct(index: Int, argc: Int = 0): AbstractConstructEntity<*> =
    getEntityOfType("entity.construct.generic", index, argc)

fun List<Iota>.getSpiderConstruct(index: Int, argc: Int = 0): SpiderConstructEntity =
    getEntityOfType(redirectSpiderLang("entity.construct.spider"), index, argc)

fun redirectSpiderLang(original: String): String {
    return if (HexboundConfig.replaceSpiderConstruct)
        original.replace("construct.spider", "construct.robot")
            .replace("spider_construct", "robot_construct")
    else
        original
}

fun redirectSpiderLang(original: Text): Text {
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
