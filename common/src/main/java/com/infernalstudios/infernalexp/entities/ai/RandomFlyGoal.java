package com.infernalstudios.infernalexp.entities.ai;

import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class RandomFlyGoal extends Goal {
    private final GlowsquitoEntity parentEntity;

    public RandomFlyGoal(GlowsquitoEntity glowsquito) {
        this.parentEntity = glowsquito;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return parentEntity.getNavigation().isDone();
    }

    @Override
    public boolean canContinueToUse() {
        return parentEntity.getNavigation().isInProgress();
    }

    @Override
    public void start() {
        RandomSource random = this.parentEntity.getRandom();
        for (int i = 0; i < 10; i++) {
            BlockPos randomPos = this.parentEntity.blockPosition().offset(
                    random.nextInt(20) - 10,
                    random.nextInt(20) - 10,
                    random.nextInt(20) - 10
            );

            if (this.parentEntity.blockPosition().distSqr(randomPos) < 16.0D) {
                continue;
            }

            if (this.parentEntity.level().isEmptyBlock(randomPos)) {
                // Check if the move was successful (path found) before returning
                if (this.parentEntity.getNavigation().moveTo(
                        randomPos.getX() + 0.5D,
                        randomPos.getY() + 0.5D,
                        randomPos.getZ() + 0.5D,
                        1.0D
                )) {
                    return;
                }
            }
        }
    }
}