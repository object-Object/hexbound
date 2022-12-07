package coffee.cypher.hexbound.feature.colorizer_storage.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapTooManyColorizers : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer {
        return dyeColor(DyeColor.RED)
    }

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text {
        return error("colorizer.storage_full")
    }

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
    }
}
