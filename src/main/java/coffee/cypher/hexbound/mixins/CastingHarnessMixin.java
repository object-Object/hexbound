package coffee.cypher.hexbound.mixins;

import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import at.petrak.hexcasting.api.spell.mishaps.MishapDisallowedSpell;
import coffee.cypher.hexbound.init.config.HexboundConfig;
import coffee.cypher.hexbound.util.mixinaccessor.CastingContextConstructAccessorKt;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CastingHarness.class, remap = false)
abstract class CastingHarnessMixin {
    @Shadow
    @Final
    private CastingContext ctx;

    @WrapOperation(
        method = "updateWithPattern",
        at = @At(
            value = "INVOKE",
            target = "Lat/petrak/hexcasting/api/mod/HexConfig$ServerConfigAccess;isActionAllowed(Lnet/minecraft/util/Identifier;)Z"
        )
    )
    private boolean hexbound$failForbiddenConstructSpells(
        HexConfig.ServerConfigAccess target,
        Identifier id,
        Operation<Boolean> op
    ) throws Mishap {
        if (
            CastingContextConstructAccessorKt.getConstruct(ctx) != null &&
                HexboundConfig.INSTANCE.isActionForbiddenForConstruct(id)
        ) {
            throw new MishapDisallowedSpell("disallowed_construct");
        }

        return op.call(target, id);
    }
}
