package gay.`object`.hexbound.feature.construct.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import gay.`object`.hexbound.feature.construct.entity.AbstractConstructEntity
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import org.quiltmc.qkl.library.math.minus

class MishapConstructForbidden(val construct: AbstractConstructEntity) : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer {
        return dyeColor(DyeColor.BROWN)
    }

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text {
        return error("construct_forbidden", construct.displayName, construct.boundPlayerData?.displayName ?: "")
    }

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
        val directionVec = ctx.caster.pos - construct.pos
        val movementVec = directionVec.normalize()
        ctx.caster.addVelocity(movementVec.x, movementVec.y, movementVec.z)
        ctx.caster.velocityModified = true
    }
}
