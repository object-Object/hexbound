package coffee.cypher.hexbound.mixins;

import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.common.casting.operators.selectors.OpGetCaster;
import coffee.cypher.hexbound.util.HexboundFakePlayer;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = OpGetCaster.class, remap = false)
abstract class OpGetCasterMixin {
    @ModifyReturnValue(method = "execute", at = @At("RETURN"))
    private List<Iota> hexbound$hideFakePlayer(List<Iota> original) {
        var iota = original.get(0);

        if ((iota instanceof EntityIota entityIota) && entityIota.getEntity() instanceof HexboundFakePlayer) {
            return List.of(new NullIota());
        }

        return original;
    }
}
