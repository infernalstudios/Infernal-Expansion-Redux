package com.infernalstudios.infernalexp.entities.ai.blindsight;

import com.infernalstudios.infernalexp.entities.BlindsightEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public class BlindsightMoveControl extends MoveControl {
    private float yRot;
    private int jumpDelay;
    private final BlindsightEntity blindsight;
    private boolean isAggressive;

    public BlindsightMoveControl(BlindsightEntity blindsight) {
        super(blindsight);
        this.blindsight = blindsight;
        this.yRot = 180.0F * blindsight.getYRot() / (float) Math.PI;
    }

    public void setDirection(float yRot, boolean aggressive) {
        this.yRot = yRot;
        this.isAggressive = aggressive;
    }

    public void setSpeed(double speedIn) {
        this.speedModifier = speedIn;
        this.operation = Operation.MOVE_TO;
    }

    public void tick() {
        this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
        this.mob.setYHeadRot(this.mob.getYRot());
        this.mob.setYBodyRot(this.mob.getYRot());

        if (this.blindsight.alertTimer > 0) {
            this.mob.setSpeed(0.0F);
            this.blindsight.xxa = 0.0F;
            this.blindsight.zza = 0.0F;
            return;
        }

        if (this.operation != Operation.MOVE_TO || this.speedModifier <= 0.0D) {
            this.mob.setZza(0.0F);
            this.mob.setSpeed(0.0F);
        } else {
            this.operation = Operation.WAIT;

            if (this.mob.onGround()) {
                this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));

                if (this.jumpDelay-- <= 0) {
                    this.jumpDelay = this.blindsight.getJumpDelay();

                    if (this.isAggressive) {
                        this.jumpDelay /= 3;
                    }

                    this.blindsight.getJumpControl().jump();

                    if (this.blindsight.doPlayJumpSound()) {
                        this.blindsight.playSound(this.blindsight.getJumpSound(), 1.0f, this.blindsight.getVoicePitch());
                    }
                } else {
                    this.blindsight.xxa = 0.0F;
                    this.blindsight.zza = 0.0F;
                    this.mob.setSpeed(0.0F);
                }
            } else {
                this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            }
        }
    }
}