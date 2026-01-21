package com.infernalstudios.infernalexp.entities.ai.glowsilkmoth;

import com.infernalstudios.infernalexp.entities.GlowsilkMothEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

public class MothRandomFlyGoal extends Goal {
    private final GlowsilkMothEntity moth;

    public MothRandomFlyGoal(GlowsilkMothEntity moth) {
        this.moth = moth;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return moth.getNavigation().isDone();
    }

    @Override
    public boolean canContinueToUse() {
        return moth.getNavigation().isInProgress();
    }

    @Override
    public void start() {
        RandomSource random = this.moth.getRandom();
        BlockPos randomPos = null;

        for (int i = 0; i < 10; i++) {
            BlockPos potentialPos = this.moth.blockPosition().offset(
                    random.nextInt(10) - 5,
                    random.nextInt(6) - 3,
                    random.nextInt(10) - 5
            );

            if (this.moth.level().isEmptyBlock(potentialPos)) {
                BlockPathTypes nodeType = WalkNodeEvaluator.getBlockPathTypeStatic(this.moth.level(), potentialPos.mutable());
                if (nodeType != BlockPathTypes.LAVA && nodeType != BlockPathTypes.DAMAGE_FIRE) {
                    randomPos = potentialPos;
                    break;
                }
            }
        }

        if (randomPos != null) {
            this.moth.getNavigation().moveTo(randomPos.getX() + 0.5D, randomPos.getY() + 0.5D, randomPos.getZ() + 0.5D, 1.0D);
        }
    }
}