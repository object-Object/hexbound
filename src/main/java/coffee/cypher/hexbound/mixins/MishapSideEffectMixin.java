package coffee.cypher.hexbound.mixins;

import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect;
import coffee.cypher.hexbound.util.mixinaccessor.CastingContextConstructAccessorKt;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = OperatorSideEffect.DoMishap.class, remap = false)
abstract class MishapSideEffectMixin {
    @WrapWithCondition(
        method = "performEffect",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendSystemMessage(Lnet/minecraft/text/Text;)V",
            remap = true
        )
    )
    private boolean hexbound$sendMishapToConstruct(ServerPlayerEntity player, Text text, CastingHarness harness) {
        var construct = CastingContextConstructAccessorKt.getConstruct(harness.getCtx());

        if (construct != null) {
            construct.setLastError(text);
            return false;
        } else {
            return true;
        }
    }
}
