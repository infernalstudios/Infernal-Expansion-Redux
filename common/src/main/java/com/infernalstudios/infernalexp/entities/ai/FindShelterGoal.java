package com.infernalstudios.infernalexp.entities.ai;

import com.infernalstudios.infernalexp.entities.VolineEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FindShelterGoal extends MoveToBlockGoal {
    private final VolineEntity voline;

    public FindShelterGoal(VolineEntity voline, double speedModifier, int searchRange) {
        super(voline, speedModifier, searchRange);
        this.voline = voline;
    }

    @Override
    public boolean canUse() {
        return this.voline.isSeekingShelter() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.voline.isSeekingShelter() && super.canContinueToUse();
    }

    @Override
    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(20 + mob.getRandom().nextInt(20));
    }

    @Override
    protected boolean isValidTarget(LevelReader level, @NotNull BlockPos pos) {
        if (!level.getBlockState(pos).isSolidRender(level, pos)) return false;
        if (!level.getBlockState(pos.above()).isAir()) return false;

        List<VolineEntity> list = this.voline.level().getEntitiesOfClass(VolineEntity.class, new AABB(pos.above()));
        for (VolineEntity voline : list) {
            if (voline.isSleeping()) {
                return false;
            }
        }

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;

                mutable.setWithOffset(pos, x, 0, z);
                if (isHotBlock(level, mutable)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isHotBlock(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.MAGMA_BLOCK)) return true;
        if (state.is(Blocks.LAVA)) return true;
        if (state.is(BlockTags.FIRE)) return true;
        return state.is(BlockTags.CAMPFIRES) && state.hasProperty(CampfireBlock.LIT) && state.getValue(CampfireBlock.LIT);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isReachedTarget()) {
            double targetX = this.blockPos.getX() + 0.5D;
            double targetY = this.blockPos.getY() + 1.0D;
            double targetZ = this.blockPos.getZ() + 0.5D;

            if (this.voline.distanceToSqr(targetX, this.voline.getY(), targetZ) > 0.04D) {
                this.voline.getNavigation().moveTo(targetX, targetY, targetZ, this.speedModifier);
            } else {
                this.voline.startSleeping(this.blockPos.above());
            }
        }
    }

    @Override
    public double acceptedDistance() {
        return 1.5D;
    }
}