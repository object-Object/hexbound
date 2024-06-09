package gay.object.hexbound.mixins;

import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.common.blocks.entity.BlockEntityStoredPlayerImpetus;
import gay.object.hexbound.util.MemorizedPlayerData;
import gay.object.hexbound.feature.fake_circles.entity.ImpetusFakePlayer;
import gay.object.hexbound.util.mixinaccessor.StoredPlayerImpetusAccessor;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.util.Pair;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(value = BlockEntityStoredPlayerImpetus.class)
abstract class BlockEntityStoredPlayerImpetusMixin extends BlockEntityAbstractImpetus implements StoredPlayerImpetusAccessor {
    @Shadow(remap = false)
    private UUID storedPlayer;

    @Unique
    @Nullable
    private ImpetusFakePlayer hexbound$fakeFallback = null;

    @NotNull
    @Override
    public UUID getHexbound$storedPlayerUuid() {
        return storedPlayer;
    }

    @Unique
    @Nullable
    private MemorizedPlayerData hexbound$memorizedPlayer = null;

    @Override
    public boolean getHexbound$useFakeFallback() {
        return hexbound$fakeFallback != null;
    }

    @Override
    public void setHexbound$useFakeFallback(boolean value) {
        if (value && world instanceof ServerWorld serverWorld) {
            hexbound$fakeFallback = new ImpetusFakePlayer(serverWorld, (BlockEntityStoredPlayerImpetus) (Object) this);
            var player = getPlayer();

            if (player != null) {
                hexbound$memorizedPlayer = MemorizedPlayerData.Companion.forPlayer(player);
            }
        } else {
            hexbound$fakeFallback = null;
            hexbound$memorizedPlayer = null;
        }
    }

    public BlockEntityStoredPlayerImpetusMixin(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    @Inject(method = "saveModData", at = @At("RETURN"))
    private void hexbound$saveData(NbtCompound tag, CallbackInfo ci) {
        tag.putBoolean("hexbound:useFakeFallback", getHexbound$useFakeFallback());

        if (hexbound$memorizedPlayer != null) {
            tag.put("hexbound:memorizedPlayer", hexbound$memorizedPlayer.toNbt());
        }
    }

    @Inject(method = "loadModData", at = @At("RETURN"))
    private void hexbound$loadData(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains("hexbound:useFakeFallback")) {
            setHexbound$useFakeFallback(tag.getBoolean("hexbound:useFakeFallback"));
        } else {
            setHexbound$useFakeFallback(false);
        }

        if (tag.contains("hexbound:memorizedPlayer")) {
            hexbound$memorizedPlayer = MemorizedPlayerData.Companion.fromNbt(tag.getCompound("hexbound:memorizedPlayer"));
        } else {
            hexbound$memorizedPlayer = null;
        }
    }

    @ModifyReturnValue(method = "getPlayer", at = @At("RETURN"))
    private PlayerEntity hexbound$fallbackToFakePlayer(PlayerEntity original) {
        if (hexbound$fakeFallback != null) {
            if (original == null) {
                hexbound$fakeFallback.resetToValidState();
                return hexbound$fakeFallback;
            } else {
                hexbound$memorizedPlayer = MemorizedPlayerData.Companion.forPlayer(original);
            }
        }

        return original;
    }

    @Inject(method = "applyScryingLensOverlay", at = @At("RETURN"))
    private void hexbound$appendFakeFallbackStatus(List<Pair<ItemStack, Text>> lines, BlockState state, BlockPos pos, PlayerEntity observer, World world, Direction hitFace, CallbackInfo ci) {
        if (getHexbound$useFakeFallback()) {
            lines.add(new Pair<>(ItemStack.EMPTY, Text.translatable("hexbound.impetus.fake_enabled")));
        }
    }

    @Nullable
    @Override
    public MemorizedPlayerData getHexbound$memorizedPlayer() {
        return hexbound$memorizedPlayer;
    }
}
