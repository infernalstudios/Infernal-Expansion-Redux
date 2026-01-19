package com.infernalstudios.infernalexp.block;

import com.infernalstudios.infernalexp.IECommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class GlimmerGravelBlock extends FallingBlock {
    public GlimmerGravelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState otherState, @NotNull LevelAccessor accessor, @NotNull BlockPos otherPos, @NotNull BlockPos whatever) {
        return state;
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState old, boolean piston) {
    }

    @Override
    public void tick(@NotNull BlockState state, ServerLevel level, BlockPos pos, @NotNull RandomSource random) {
        if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
            level.playSound(null, pos, SoundEvents.SAND_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);
            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                    pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                    5, 0.25D, 0.25D, 0.25D, 0.05D);
        }
        super.tick(state, level, pos, random);
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        super.stepOn(level, pos, state, entity);

        if (!level.isClientSide && !entity.isSteppingCarefully()) {
            triggerChainReaction((ServerLevel) level, pos);
        }
    }

    /**
     * Scans a radius around the stepped-on block and triggers all other GlimmerGravelBlocks.
     */
    private void triggerChainReaction(ServerLevel level, BlockPos centerPos) {
        int radius = IECommon.getConfig().common.miscellaneous.glimmerGravelTriggerRadius;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos targetPos = centerPos.offset(x, y, z);
                    BlockState targetState = level.getBlockState(targetPos);

                    if (targetState.getBlock() instanceof GlimmerGravelBlock) {
                        if (!level.getBlockTicks().hasScheduledTick(targetPos, this)) {
                            level.scheduleTick(targetPos, this, 0);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected int getDelayAfterPlace() {
        return 2;
    }
}