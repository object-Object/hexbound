package coffee.cypher.hexbound.feature.construct.mishap

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import coffee.cypher.hexbound.util.fakeplayer.FakeServerPlayer
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapNoConstruct : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
        return dyeColor(DyeColor.PURPLE)
    }

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text {
        return error("no_construct", actionName(errorCtx.name))
    }

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        if (env.caster !is FakeServerPlayer) {
            env.caster?.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS, 20, 2))
        }
    }
}
