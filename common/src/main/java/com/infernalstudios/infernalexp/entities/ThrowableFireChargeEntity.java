package com.infernalstudios.infernalexp.entities;

import com.infernalstudios.infernalexp.module.ModEntityTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ThrowableFireChargeEntity extends SmallFireball {

    public ThrowableFireChargeEntity(EntityType<? extends ThrowableFireChargeEntity> type, Level level) {
        super(type, level);
    }

    public ThrowableFireChargeEntity(Level level, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(ModEntityTypes.THROWABLE_FIRE_CHARGE.get(), level);
        this.setOwner(shooter);
        this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        double d0 = Math.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        if (d0 != 0.0D) {
            this.xPower = accelX / d0 * 0.1D;
            this.yPower = accelY / d0 * 0.1D;
            this.zPower = accelZ / d0 * 0.1D;
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        Vec3 originalMotion = this.getDeltaMovement();
        this.setDeltaMovement(this.xPower, this.yPower, this.zPower);
        Packet<ClientGamePacketListener> packet = new ClientboundAddEntityPacket(this);
        this.setDeltaMovement(originalMotion);
        return packet;
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        double x = packet.getXa();
        double y = packet.getYa();
        double z = packet.getZa();

        this.xPower = x;
        this.yPower = y;
        this.zPower = z;

        this.setDeltaMovement(Vec3.ZERO);
    }
}