package coffee.cypher.hexbound.mixins;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.fabric.xplat.FabricXplatImpl;
import coffee.cypher.hexbound.feature.fake_circles.entity.ImpetusFakePlayer;
import coffee.cypher.hexbound.feature.media_attachment.MediaAttachmentKt;
import coffee.cypher.hexbound.util.mixinaccessor.StoredPlayerImpetusAccessorKt;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FabricXplatImpl.class)
abstract class FabricXPlatMixin {
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

    @ModifyReturnValue(
        method = "findMediaHolder",
        at = @At("RETURN")
    )
    private ADMediaHolder hexbound$tryMediaAttachment(ADMediaHolder base, ItemStack stack) {
        var attachment = MediaAttachmentKt.getMediaAttachmentForStack(stack);

        if (attachment != null) {
            return attachment;
        }

        return base;
    }
}
