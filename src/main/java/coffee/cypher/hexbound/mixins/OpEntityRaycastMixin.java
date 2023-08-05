package coffee.cypher.hexbound.mixins;

import at.petrak.hexcasting.common.casting.actions.raycast.OpEntityRaycast;
import coffee.cypher.hexbound.feature.combat.shield.ShieldEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Predicate;

@Mixin(OpEntityRaycast.class)
public class OpEntityRaycastMixin {
    @ModifyVariable(
        method = "getEntityHitResult",
        at = @At(
            value = "LOAD"
        ),
        argsOnly = true
    )
    private Predicate<Entity> hexbound$raycastIgnoresShield(
        @NotNull Predicate<Entity> original,
        @Nullable Entity entity,
        @NotNull World level,
        @NotNull Vec3d startPos,
        @NotNull Vec3d endPos
    ) {
        var look = endPos.subtract(startPos);
        return (e) -> original.test(e) && !ShieldEntity.canBypassShieldForDirection(look, e);
    }
}
