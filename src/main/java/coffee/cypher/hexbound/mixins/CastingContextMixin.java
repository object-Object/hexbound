package coffee.cypher.hexbound.mixins;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity;
import coffee.cypher.hexbound.util.mixinaccessor.CastingContextMinionAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CastingContext.class)
public class CastingContextMixin implements CastingContextMinionAccessor {
    @Nullable
    private AbstractConstructEntity<?> hexbound$construct;

    @Nullable
    @Override
    public AbstractConstructEntity<?> getHexbound$construct() {
        return hexbound$construct;
    }

    @Override
    public void setHexbound$construct(@Nullable AbstractConstructEntity<?> constructEntity) {
        hexbound$construct = constructEntity;
    }
}
