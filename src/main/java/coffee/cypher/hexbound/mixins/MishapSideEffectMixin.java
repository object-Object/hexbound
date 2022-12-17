package coffee.cypher.hexbound.mixins;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import coffee.cypher.hexbound.feature.construct.entity.ConstructFakePlayer;
import coffee.cypher.hexbound.util.mixinaccessor.CastingContextConstructAccessorKt;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = OperatorSideEffect.DoMishap.class)
abstract class MishapSideEffectMixin {
    @WrapWithCondition(
        method = "performEffect",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendSystemMessage(Lnet/minecraft/text/Text;)V"
        )
    )
    private boolean hexbound$sendMishapToConstruct(ServerPlayerEntity player, Text text, CastingHarness harness) {
        var construct = CastingContextConstructAccessorKt.getConstruct(harness.getCtx());

        if (construct == null && player instanceof ConstructFakePlayer fake) {
            construct = fake.getConstruct();
        }

        if (construct != null) {
            construct.setLastError(text);
            return false;
        } else {
            return true;
        }
    }

    @WrapWithCondition(
        method = "performEffect",
        at = @At(
            value = "INVOKE",
            target = "Lat/petrak/hexcasting/api/spell/mishaps/Mishap;execute(Lat/petrak/hexcasting/api/spell/casting/CastingContext;Lat/petrak/hexcasting/api/spell/mishaps/Mishap$Context;Ljava/util/List;)V",
            remap = false
        ),
        remap = false
    )
    private boolean hexbound$skipConstructMishaps(Mishap mishap, CastingContext ctx, Mishap.Context errorCtx, List<Iota> stack) {
        return CastingContextConstructAccessorKt.getConstruct(ctx) == null;
    }
}
