package coffee.cypher.hexbound.feature.construct.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.feature.construct.entity.component.ConstructComponentKey
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapMissingConstructComponent(
    val construct: AbstractConstructEntity,
    val key: ConstructComponentKey<*>
) : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer {
        return dyeColor(DyeColor.YELLOW)
    }

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text {
        return Text.translatable("hexbound.construct.exception.component_missing.${key.key}", construct.displayName)
    }

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
    }
}
