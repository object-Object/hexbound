package coffee.cypher.hexbound.mixins;

import at.petrak.hexcasting.interop.HexInterop;
import org.quiltmc.loader.api.QuiltLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.api.PatchouliAPI;

@Mixin(HexInterop.class)
public class HexInteropMixin {
    @Inject(
        method = "initPatchouli",
        at = @At("RETURN"),
        remap = false
    )
    private static void hexbound$forceInitIfHexalPresent(CallbackInfo ci) {
        if (QuiltLoader.isModLoaded("hexal")) {
            PatchouliAPI.get().setConfigFlag(HexInterop.PATCHOULI_ANY_INTEROP_FLAG, true);
        }
    }
}
