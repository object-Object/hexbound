package coffee.cypher.hexbound.feature.construct.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import coffee.cypher.hexbound.feature.construct.entity.component.ConstructComponentKey
import net.minecraft.text.Text

class MishapMissingConstructComponent(key: ConstructComponentKey<*>) : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer {
        TODO("Not yet implemented")
    }

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text {
        TODO("Not yet implemented")
    }

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
        TODO("Not yet implemented")
    }
}
