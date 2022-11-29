package coffee.cypher.hexbound.feature.colorizer_storage.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapMissingColorizerKey(private val pattern: HexPattern) : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer {
        return dyeColor(DyeColor.WHITE)
    }

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text {
        return error("colorizer.missing_key", pattern.anglesSignature())
    }

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
        HexCardinalComponents.FAVORED_COLORIZER[ctx.caster].colorizer = FrozenColorizer.DEFAULT.get()
    }
}
