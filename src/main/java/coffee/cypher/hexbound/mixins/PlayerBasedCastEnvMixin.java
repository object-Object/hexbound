package coffee.cypher.hexbound.mixins;

import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import coffee.cypher.hexbound.init.HexboundData;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value = PlayerBasedCastEnv.class)
abstract class PlayerBasedCastEnvMixin {
    @Shadow
    @Final
    protected ServerPlayerEntity caster;

    @ModifyConstant(
            method = "isVecInRange",
            constant = @Constant(
                    doubleValue = 1024.0
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/server/network/ServerPlayerEntity;getPos()Lnet/minecraft/util/math/Vec3d;"
                    )
            )
    )
    private double hexbound$ambitGone(double ambitSq) {
        var status = caster.getStatusEffect(HexboundData.StatusEffects.INSTANCE.getREDUCED_AMBIT());

        if (status != null) {
            var factor = (status.getAmplifier() + 1);
            return ambitSq / (factor * factor);
        }

        return ambitSq;
    }

    //TODO I'd take teleport on lvl 2 as well, but the sticky code is too much of a mess so you get to live
    @ModifyExpressionValue(
            method = "isVecInRange",
            at = @At(
                    value = "INVOKE",
                    target = "Lat/petrak/hexcasting/api/player/Sentinel;extendsRange()Z",
                    remap = false
            )
    )
    private boolean hexbound$sentinelGone(boolean original) {
        return original && !caster.hasStatusEffect(HexboundData.StatusEffects.INSTANCE.getREDUCED_AMBIT());
    }
}

//TODO arrow imbuement ideas: event horizon, teleport lock, shimmering (bypasses shields, cannot be zone/entity selected)
