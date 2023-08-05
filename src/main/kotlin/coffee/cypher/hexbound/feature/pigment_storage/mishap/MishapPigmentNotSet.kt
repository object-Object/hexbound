package coffee.cypher.hexbound.feature.pigment_storage.mishap

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapPigmentNotSet : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
        return dyeColor(DyeColor.WHITE)
    }

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text {
        return error("pigment.absent")
    }

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
    }
}
