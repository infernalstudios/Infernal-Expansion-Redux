package com.infernalstudios.infernalexp.entities.ai.blindsight;

import com.infernalstudios.infernalexp.entities.BlindsightEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BlindsightRandomDirectionGoal extends Goal {
    private final BlindsightEntity blindsight;
    private float chosenDegrees;
    private int nextRandomizeTime;

    public BlindsightRandomDirectionGoal(BlindsightEntity blindsight) {
        this.blindsight = blindsight;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.blindsight.getTarget() == null &&
                (this.blindsight.onGround() || this.blindsight.isInWater() || this.blindsight.isInLava() || this.blindsight.hasEffect(MobEffects.LEVITATION)) &&
                this.blindsight.getMoveControl() instanceof BlindsightMoveControl;
    }

    @Override
    public void tick() {
        if (--this.nextRandomizeTime <= 0) {
            this.nextRandomizeTime = this.adjustedTickDelay(40 + this.blindsight.getRandom().nextInt(60));
            this.chosenDegrees = (float)this.blindsight.getRandom().nextInt(360);
        }

        MoveControl movecontrol = this.blindsight.getMoveControl();
        if (movecontrol instanceof BlindsightMoveControl blindsightMoveControl) {
            blindsightMoveControl.setDirection(this.chosenDegrees, false);
        }
    }
}