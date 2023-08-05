package coffee.cypher.hexbound.mixins.fakeplayer;

import coffee.cypher.hexbound.util.fakeplayer.FakeServerPlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetPredicate.class)
public class TargetPredicateMixin {
    @Inject(method = "test", at = @At("HEAD"), cancellable = true)
    private void hexbound$fakeplayer$canTrack(LivingEntity baseEntity, LivingEntity targetEntity, CallbackInfoReturnable<Boolean> cir) {
        //noinspection ConstantValue
        if ((Object) this instanceof FakeServerPlayer fakePlayer) {
            if (!fakePlayer.canBeTarget(baseEntity)) cir.setReturnValue(false);
        }
    }
}
