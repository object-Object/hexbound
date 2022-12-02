package coffee.cypher.hexbound.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import ram.talia.hexal.common.entities.LinkableEntity;

//TODO bug fixing mixin, remove when Hexal updates!
@Mixin(LinkableEntity.class)
abstract class HexalLinkableEntityMixin extends Entity {
    public HexalLinkableEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * @author Cypher121
     * @reason fix mapping conflict
     */
    @Overwrite
    public Vec3d getPos() {
        return super.getPos();
    }
}
