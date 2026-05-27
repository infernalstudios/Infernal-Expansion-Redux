package com.infernalstudios.infernalexp.entities;

import com.infernalstudios.infernalexp.module.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ThrowableFireChargeEntity extends SmallFireball {

    public ThrowableFireChargeEntity(EntityType<? extends ThrowableFireChargeEntity> type, Level level) {
        super(type, level);
    }

    public ThrowableFireChargeEntity(Level level, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(ModEntityTypes.THROWABLE_FIRE_CHARGE.get(), level);
        this.setOwner(shooter);
        this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());

        Vec3 movement = new Vec3(accelX, accelY, accelZ);
        if (movement.lengthSqr() != 0.0D) {
            this.setDeltaMovement(movement.normalize().scale(this.accelerationPower));
            this.hasImpulse = true;
        }
    }
}