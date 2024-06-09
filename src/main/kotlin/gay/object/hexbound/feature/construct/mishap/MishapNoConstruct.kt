package gay.`object`.hexbound.feature.construct.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import dev.cafeteria.fakeplayerapi.server.FakeServerPlayer
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapNoConstruct() : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer {
        return dyeColor(DyeColor.PURPLE)
    }

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text {
        return error("no_construct", actionName(errorCtx.action))
    }

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
        if (ctx.caster !is FakeServerPlayer) {
            ctx.caster.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS, 20, 2))
        }
    }
}
