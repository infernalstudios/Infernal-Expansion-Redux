package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.module.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin {

    @Shadow
    @Final
    protected FlowingFluid fluid;

    @Inject(method = "shouldSpreadLiquid", at = @At("HEAD"), cancellable = true)
    private void infernalexp$shimmerStoneStationaryInteraction(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (this.fluid.isSame(net.minecraft.world.level.material.Fluids.LAVA)) {
            if (level.getBlockState(pos.below()).is(ModBlocks.SHIMMER_SAND.get())) {
                for (Direction direction : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
                    if (direction == Direction.DOWN) continue;

                    BlockPos neighborPos = pos.relative(direction.getOpposite());
                    if (level.getBlockState(neighborPos).is(Blocks.BLUE_ICE)) {
                        level.setBlockAndUpdate(pos, ModBlocks.SHIMMER_STONE.get().defaultBlockState());
                        level.levelEvent(1501, pos, 0);
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
        }
    }
}