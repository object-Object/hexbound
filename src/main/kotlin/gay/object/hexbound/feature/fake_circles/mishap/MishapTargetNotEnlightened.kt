package gay.`object`.hexbound.feature.fake_circles.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapTargetNotEnlightened(val target: PlayerEntity) : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer {
        return dyeColor(DyeColor.PURPLE)
    }

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text {
        return error("target_not_enlightened", target.displayName)
    }

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
        ctx.caster.yaw = ctx.caster.yaw + 180
    }
}
