package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.module.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFluid.class)
public abstract class LavaFluidMixin {

    @Inject(method = "spreadTo", at = @At("HEAD"), cancellable = true)
    private void infernalexp$shimmerStoneGenerator(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluidState, CallbackInfo ci) {
        if (direction == Direction.DOWN) return;

        if (level.getBlockState(pos.below()).is(ModBlocks.SHIMMER_SAND.get())) {
            for (Direction dir : Direction.values()) {
                if (dir != Direction.DOWN && level.getBlockState(pos.relative(dir)).is(Blocks.BLUE_ICE)) {
                    level.setBlock(pos, ModBlocks.SHIMMER_STONE.get().defaultBlockState(), 3);
                    level.levelEvent(1501, pos, 0);
                    ci.cancel();
                    return;
                }
            }
        }
    }
}