package com.infernalstudios.infernalexp.entities.ai.blindsight;

import com.infernalstudios.infernalexp.entities.BlindsightEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;

public class BlindsightFloatGoal extends Goal {
    private final BlindsightEntity blindsight;

    public BlindsightFloatGoal(BlindsightEntity blindsight) {
        this.blindsight = blindsight;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        blindsight.getNavigation().setCanFloat(true);
    }

    public boolean canUse() {
        return (this.blindsight.isInWater() || this.blindsight.isInLava());
    }

    public void tick() {
        if (this.blindsight.getRandom().nextFloat() < 0.8F) this.blindsight.getJumpControl().jump();
        ((BlindsightMoveControl) this.blindsight.getMoveControl()).setSpeed(1.2D);
    }
}