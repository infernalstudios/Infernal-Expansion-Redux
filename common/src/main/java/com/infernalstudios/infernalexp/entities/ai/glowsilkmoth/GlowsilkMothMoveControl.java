package com.infernalstudios.infernalexp.entities.ai.glowsilkmoth;

import com.infernalstudios.infernalexp.entities.GlowsilkMothEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class GlowsilkMothMoveControl extends MoveControl {
    private final GlowsilkMothEntity moth;

    public GlowsilkMothMoveControl(GlowsilkMothEntity moth) {
        super(moth);
        this.moth = moth;
    }

    @Override
    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO) {
            Vec3 wanted = new Vec3(this.wantedX - this.moth.getX(), this.wantedY - this.moth.getY(), this.wantedZ - this.moth.getZ());
            double dist = wanted.length();

            if (this.moth.horizontalCollision && this.moth.level().getGameTime() % 20 == 0) {
                this.moth.getNavigation().recomputePath();
            }

            if (dist < 0.5D) {
                this.operation = MoveControl.Operation.WAIT;
                this.moth.setDeltaMovement(this.moth.getDeltaMovement().scale(0.5D));
                this.moth.getNavigation().stop();
            } else {
                this.moth.setDeltaMovement(this.moth.getDeltaMovement().add(wanted.scale(this.speedModifier * 0.05D / dist)));

                if (this.moth.getTarget() == null) {
                    Vec3 velocity = this.moth.getDeltaMovement();
                    this.moth.setYRot(-((float) Mth.atan2(velocity.x, velocity.z)) * (180F / (float) Math.PI));
                } else {
                    double dx = this.moth.getTarget().getX() - this.moth.getX();
                    double dz = this.moth.getTarget().getZ() - this.moth.getZ();
                    this.moth.setYRot(-((float) Mth.atan2(dx, dz)) * (180F / (float) Math.PI));
                }
                this.moth.yBodyRot = this.moth.getYRot();
            }
        }
    }
}