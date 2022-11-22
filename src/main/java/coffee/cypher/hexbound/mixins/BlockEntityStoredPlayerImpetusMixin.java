package coffee.cypher.hexbound.mixins;

import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.common.blocks.entity.BlockEntityStoredPlayerImpetus;
import coffee.cypher.hexbound.util.FakePlayerFactory;
import coffee.cypher.hexbound.mixinaccessor.ImpetusFakePlayerAccessor;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
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
    private boolean hexbound$useFakeFallback = false;

    @Override
    public boolean getHexbound$useFakeFallback() {
        return hexbound$useFakeFallback;
    }

    @Override
    public void setHexbound$useFakeFallback(boolean value) {
        hexbound$useFakeFallback = value;
    }

    public BlockEntityStoredPlayerImpetusMixin(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    @Inject(method = "saveModData", at = @At("RETURN"))
    private void hexbound$saveData(NbtCompound tag, CallbackInfo ci) {
        tag.putBoolean("hexbound:useFakeFallback", hexbound$useFakeFallback);
    }

    @Inject(method = "loadModData", at = @At("RETURN"))
    private void hexbound$loadData(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains("hexbound:useFakeFallback")) {
            hexbound$useFakeFallback = tag.getBoolean("hexbound:useFakeFallback");
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
        if (hexbound$useFakeFallback) {
            lines.add(new Pair<>(ItemStack.EMPTY, Text.translatable("hexbound.impetus.fake_enabled")));
        }
    }
}
