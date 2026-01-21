package com.infernalstudios.infernalexp.entities.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class LookAroundGoal extends Goal {
    private final Mob mob;

    public LookAroundGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void tick() {
        if (this.mob.getTarget() == null) {
            Vec3 vector3d = this.mob.getDeltaMovement();
            if (vector3d.horizontalDistanceSqr() > 0.003D) {
                float targetYaw = -((float) Mth.atan2(vector3d.x, vector3d.z)) * (180F / (float) Math.PI);
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), targetYaw, 10.0F));
                this.mob.yBodyRot = this.mob.getYRot();
            }
        }
    }

    private float rotlerp(float current, float target, float maxChange) {
        float f = Mth.wrapDegrees(target - current);
        if (f > maxChange) f = maxChange;
        if (f < -maxChange) f = -maxChange;
        return current + f;
    }
}