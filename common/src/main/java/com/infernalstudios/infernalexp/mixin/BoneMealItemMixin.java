package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.block.ShroomlightTearBlock;
import com.infernalstudios.infernalexp.compat.ShroomlightCompatRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    public void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);

        Block tearBlock = infernalExp$getTearVariant(state.getBlock());

        if (tearBlock != null) {
            if (infernalexp$tryGrowTear(context, world, pos, tearBlock)) {
                cir.setReturnValue(InteractionResult.sidedSuccess(world.isClientSide));
            }
        }
    }

    /**
     * Determines which tear block corresponds to the target light block.
     */
    @Unique
    @Nullable
    private Block infernalExp$getTearVariant(Block block) {
        return ShroomlightCompatRegistry.getTearVariant(block);
    }

    /**
     * Handles the placement logic, direction checks, sounds, and particles.
     */
    @Unique
    private boolean infernalexp$tryGrowTear(UseOnContext context, Level world, BlockPos pos, Block tearBlock) {
        boolean isWarped = world.getBiome(pos).is(Biomes.WARPED_FOREST);
        BlockPos targetPos = isWarped ? pos.above() : pos.below();

        if (!world.getBlockState(targetPos).isAir()) {
            return false;
        }

        if (!world.isClientSide) {
            context.getItemInHand().shrink(1);

            BlockState tearState = tearBlock.defaultBlockState();
            if (tearState.hasProperty(ShroomlightTearBlock.UP)) {
                tearState = tearState.setValue(ShroomlightTearBlock.UP, isWarped);
            }

            world.setBlock(targetPos, tearState, Block.UPDATE_ALL);

            world.playSound(null, targetPos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (world instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        targetPos.getX() + 0.5D, targetPos.getY() + 0.5D, targetPos.getZ() + 0.5D,
                        15, 0.25D, 0.25D, 0.25D, 0.05D);
            }
        }

        return true;
    }
}