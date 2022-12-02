package coffee.cypher.hexbound.mixins;

import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.common.blocks.entity.BlockEntityStoredPlayerImpetus;
import coffee.cypher.hexbound.util.FakePlayerFactory;
import coffee.cypher.hexbound.util.mixinaccessor.ImpetusFakePlayerAccessor;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import dev.cafeteria.fakeplayerapi.server.FakeServerPlayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(value = BlockEntityStoredPlayerImpetus.class, remap = false)
abstract class BlockEntityStoredPlayerImpetusMixin extends BlockEntityAbstractImpetus implements ImpetusFakePlayerAccessor {
    @Shadow
    private UUID storedPlayer;

    @Shadow
    private GameProfile storedPlayerProfile;

    @Unique
    @Nullable
    private FakeServerPlayer hexbound$fakeFallback = null;

    @Override
    public boolean getHexbound$useFakeFallback() {
        return hexbound$fakeFallback != null;
    }

    @Override
    public void setHexbound$useFakeFallback(boolean value) {
        if (value && world instanceof ServerWorld serverWorld) {
            hexbound$fakeFallback = FakePlayerFactory.INSTANCE.getFakePlayerForImpetus(
                storedPlayer,
                storedPlayerProfile,
                serverWorld
            );
        } else {
            hexbound$fakeFallback = null;
        }
    }

    public BlockEntityStoredPlayerImpetusMixin(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    @Inject(method = "saveModData", at = @At("RETURN"))
    private void hexbound$saveData(NbtCompound tag, CallbackInfo ci) {
        tag.putBoolean("hexbound:useFakeFallback", getHexbound$useFakeFallback());
    }

    @Inject(method = "loadModData", at = @At("RETURN"))
    private void hexbound$loadData(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains("hexbound:useFakeFallback")) {
            setHexbound$useFakeFallback(tag.getBoolean("hexbound:useFakeFallback"));
        }
    }

    @ModifyReturnValue(method = "getPlayer", at = @At("RETURN"))
    private PlayerEntity hexbound$fallbackToFakePlayer(PlayerEntity original) {
        if (original == null && world instanceof ServerWorld serverWorld) {
            return FakePlayerFactory.INSTANCE.getFakePlayerForImpetus(storedPlayer, storedPlayerProfile, serverWorld);
        }

        return original;
    }

    @Inject(method = "applyScryingLensOverlay", at = @At("RETURN"))
    private void hexbound$appendFakeFallbackStatus(List<Pair<ItemStack, Text>> lines, BlockState state, BlockPos pos, PlayerEntity observer, World world, Direction hitFace, CallbackInfo ci) {
        if (getHexbound$useFakeFallback()) {
            lines.add(new Pair<>(ItemStack.EMPTY, Text.translatable("hexbound.impetus.fake_enabled")));
        }
    }
}
