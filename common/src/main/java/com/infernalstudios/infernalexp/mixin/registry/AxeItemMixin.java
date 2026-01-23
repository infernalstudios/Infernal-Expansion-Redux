package com.infernalstudios.infernalexp.mixin.registry;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.registration.StrippableRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "getStripped", at = @At("RETURN"), cancellable = true)
    private void infernalexp$getRegisteredStrippables(BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {
        if (cir.getReturnValue().isPresent()) return;

        Block strippedBlock = StrippableRegistry.get(state.getBlock());
        if (strippedBlock != null) {
            BlockState strippedState = strippedBlock.defaultBlockState();

            if (state.hasProperty(RotatedPillarBlock.AXIS) && strippedState.hasProperty(RotatedPillarBlock.AXIS)) {
                strippedState = strippedState.setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
            }

            cir.setReturnValue(Optional.of(strippedState));
        }
    }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void infernalexp$stripWaxedGlowstone(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.is(ModBlocks.WAXED_GLOWSTONE.get())) {
            Player player = context.getPlayer();

            level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);

            level.levelEvent(player, 3004, pos, 0);

            if (!level.isClientSide) {
                level.setBlock(pos, Blocks.GLOWSTONE.defaultBlockState(), Block.UPDATE_ALL);
                if (player != null) {
                    context.getItemInHand().hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                }
            }

            cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
        }
    }
}