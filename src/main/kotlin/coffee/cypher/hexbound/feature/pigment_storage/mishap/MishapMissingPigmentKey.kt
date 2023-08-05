package coffee.cypher.hexbound.feature.pigment_storage.mishap

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapMissingPigmentKey(private val pattern: HexPattern) : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
        return dyeColor(DyeColor.WHITE)
    }

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text {
        return error("pigment.missing_key", pattern.anglesSignature())
    }

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        env.caster?.let {
            HexCardinalComponents.FAVORED_PIGMENT[it].pigment = FrozenPigment.DEFAULT.get()
        }
    }
}
