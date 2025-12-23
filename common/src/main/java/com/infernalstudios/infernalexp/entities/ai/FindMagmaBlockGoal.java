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

public class FindMagmaBlockGoal extends MoveToBlockGoal {
    private final VolineEntity voline;

    public FindMagmaBlockGoal(VolineEntity voline, double speedModifier, int searchRange) {
        super(voline, speedModifier, searchRange);
        this.voline = voline;
    }

    @Override
    public boolean canUse() {
        return this.voline.isSeekingShelter() && super.canUse();
    }

    @Override
    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(20 + mob.getRandom().nextInt(20));
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
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
            this.voline.startSleeping(this.blockPos);
        }
    }

    @Override
    public double acceptedDistance() {
        return 1.5D;
    }
}