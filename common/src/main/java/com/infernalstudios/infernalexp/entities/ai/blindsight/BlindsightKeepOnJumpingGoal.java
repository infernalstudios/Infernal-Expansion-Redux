package com.infernalstudios.infernalexp.entities.ai.blindsight;

import com.infernalstudios.infernalexp.entities.BlindsightEntity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BlindsightKeepOnJumpingGoal extends Goal {
    private final BlindsightEntity blindsight;

    public BlindsightKeepOnJumpingGoal(BlindsightEntity blindsight) {
        this.blindsight = blindsight;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return !this.blindsight.isPassenger() && !this.blindsight.isAttacking() && !this.blindsight.wantsToTongueAttack;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void tick() {
        MoveControl movecontrol = this.blindsight.getMoveControl();
        if (movecontrol instanceof BlindsightMoveControl blindsightMoveControl) {
            blindsightMoveControl.setSpeed(1.0D);
        }
    }

    @Override
    public void stop() {
        MoveControl movecontrol = this.blindsight.getMoveControl();
        if (movecontrol instanceof BlindsightMoveControl blindsightMoveControl) {
            blindsightMoveControl.setSpeed(0.0D);
        }
    }
}