package coffee.cypher.hexbound.mixins.fakeplayer;

import coffee.cypher.hexbound.util.fakeplayer.FakeServerPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashReportSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "populateCrashReport", at = @At("TAIL"))
    private void hexbound$fakeplayer$populate(CrashReportSection section, CallbackInfo ci) {
        //noinspection ConstantValue
        if ((Object) this instanceof FakeServerPlayer) {
            section.add("Fake Player From", "Hexbound");
        }
    }
}
