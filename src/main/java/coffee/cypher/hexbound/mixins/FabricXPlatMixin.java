package coffee.cypher.hexbound.mixins;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.fabric.xplat.FabricXplatImpl;
import coffee.cypher.hexbound.feature.fake_circles.entity.ImpetusFakePlayer;
import coffee.cypher.hexbound.util.mixinaccessor.StoredPlayerImpetusAccessorKt;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FabricXplatImpl.class)
public class FabricXPlatMixin {
    @ModifyReturnValue(
        method = "getColorizer",
        at = @At("RETURN")
    )
    private FrozenColorizer hexbound$useFakeColorizer(FrozenColorizer original, PlayerEntity player) {
        if (player instanceof ImpetusFakePlayer fake) {
            var memorized = StoredPlayerImpetusAccessorKt.getMemorizedPlayer(fake.getImpetus());
            if (memorized != null) {
                return memorized.getColorizer();
            }
        }

        return original;
    }
}
