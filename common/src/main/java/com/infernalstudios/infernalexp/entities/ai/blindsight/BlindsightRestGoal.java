package com.infernalstudios.infernalexp.entities.ai.blindsight;

import com.infernalstudios.infernalexp.entities.BlindsightEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BlindsightRestGoal extends Goal {
    private final BlindsightEntity blindsight;
    private int restDuration;

    public BlindsightRestGoal(BlindsightEntity blindsight) {
        this.blindsight = blindsight;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return this.blindsight.getTarget() == null &&
                this.blindsight.onGround() &&
                this.blindsight.consecutiveHops >= this.blindsight.hopsUntilIdle;
    }

    @Override
    public void start() {
        this.restDuration = 40 + this.blindsight.getRandom().nextInt(21);
        this.blindsight.setResting(true);
        this.blindsight.getNavigation().stop();
        ((BlindsightMoveControl) this.blindsight.getMoveControl()).setSpeed(0.0D);

        this.blindsight.consecutiveHops = 0;
        this.blindsight.hopsUntilIdle = 6 + this.blindsight.getRandom().nextInt(3);
    }

    @Override
    public boolean canContinueToUse() {
        return this.restDuration > 0 && this.blindsight.getTarget() == null;
    }

    @Override
    public void tick() {
        this.restDuration--;
        ((BlindsightMoveControl) this.blindsight.getMoveControl()).setSpeed(0.0D);
    }

    @Override
    public void stop() {
        this.blindsight.setResting(false);
    }
}