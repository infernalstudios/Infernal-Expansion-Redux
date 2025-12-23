package com.infernalstudios.infernalexp.entities.ai;

import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class RandomFlyGoal extends Goal {
    private final GlowsquitoEntity parentEntity;

    public RandomFlyGoal(GlowsquitoEntity glowsquito) {
        this.parentEntity = glowsquito;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
        MoveControl movementcontroller = this.parentEntity.getMoveControl();
        if (!movementcontroller.hasWanted()) {
            return true;
        } else {
            double d0 = movementcontroller.getWantedX() - this.parentEntity.getX();
            double d1 = movementcontroller.getWantedY() - this.parentEntity.getY();
            double d2 = movementcontroller.getWantedZ() - this.parentEntity.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            return d3 < 1.0D || d3 > 3600.0D;
        }
    }

    public boolean canContinueToUse() {
        return false;
    }

    public void start() {
        RandomSource random = this.parentEntity.getRandom();
        double d0 = this.parentEntity.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        double d1 = this.parentEntity.getY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        double d2 = this.parentEntity.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        this.parentEntity.getMoveControl().setWantedPosition(d0, d1, d2, 1.0D);
    }
}