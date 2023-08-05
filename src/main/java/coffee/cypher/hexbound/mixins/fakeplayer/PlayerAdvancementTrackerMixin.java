package coffee.cypher.hexbound.mixins.fakeplayer;

import coffee.cypher.hexbound.util.fakeplayer.FakePlayerAdvancementTracker;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.ServerAdvancementLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {
    @Inject(method = "load", at = @At("HEAD"), cancellable = true)
    private void hexbound$fakeplayer$cancelLoad(ServerAdvancementLoader advancementLoader, CallbackInfo ci) {
        //noinspection ConstantValue
        if ((Object) this instanceof FakePlayerAdvancementTracker) ci.cancel();
    }
}
