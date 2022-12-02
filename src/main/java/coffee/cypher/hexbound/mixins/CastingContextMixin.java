package coffee.cypher.hexbound.mixins;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity;
import coffee.cypher.hexbound.util.fake.ImpetusFakePlayer;
import coffee.cypher.hexbound.util.mixinaccessor.CastingContextConstructAccessor;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CastingContext.class, remap = false)
public class CastingContextMixin implements CastingContextConstructAccessor {
    @Nullable
    private AbstractConstructEntity hexbound$construct;

    @Shadow
    @Final
    private ServerPlayerEntity caster;

    @Nullable
    @Override
    public AbstractConstructEntity getHexbound$construct() {
        return hexbound$construct;
    }

    @Override
    public void setHexbound$construct(@Nullable AbstractConstructEntity constructEntity) {
        hexbound$construct = constructEntity;
    }

    @ModifyReturnValue(
        method = "isCasterEnlightened",
        at = @At("RETURN")
    )
    private boolean hexbound$fakeImpetusPlayersAreEnlightened(boolean original) {
        return original || caster instanceof ImpetusFakePlayer;
    }
}
