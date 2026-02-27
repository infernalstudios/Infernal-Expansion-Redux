package com.infernalstudios.infernalexp.entities;

import com.infernalstudios.infernalexp.module.ModEntityTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class ThrowableMagmaCreamEntity extends ThrowableItemProjectile {

    public ThrowableMagmaCreamEntity(EntityType<? extends ThrowableMagmaCreamEntity> type, Level level) {
        super(type, level);
    }

    public ThrowableMagmaCreamEntity(Level level, LivingEntity shooter) {
        super(ModEntityTypes.THROWABLE_MAGMA_CREAM.get(), shooter, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.MAGMA_CREAM;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60));
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            if (result.getType() == HitResult.Type.BLOCK) {
                ItemEntity item = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), Items.MAGMA_CREAM.getDefaultInstance());
                this.level().addFreshEntity(item);
            }
            this.discard();
        }
    }
}