package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.module.ModEffects;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LuminousEffectSyncMixin extends Entity {

    public LuminousEffectSyncMixin(net.minecraft.world.entity.EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    private void infernalexp$syncLuminousAdded(MobEffectInstance effectInstance, Entity source, CallbackInfo ci) {
        if (this.level().isClientSide) return;

        if (effectInstance.getEffect() == ModEffects.LUMINOUS.get()) {
            Packet<?> packet = new ClientboundUpdateMobEffectPacket(this.getId(), effectInstance);
            ((ServerLevel) this.level()).getChunkSource().broadcast(this, packet);
        }
    }

    @Inject(method = "onEffectUpdated", at = @At("TAIL"))
    private void infernalexp$syncLuminousUpdated(MobEffectInstance effectInstance, boolean forced, Entity source, CallbackInfo ci) {
        if (this.level().isClientSide) return;

        if (effectInstance.getEffect() == ModEffects.LUMINOUS.get()) {
            Packet<?> packet = new ClientboundUpdateMobEffectPacket(this.getId(), effectInstance);
            ((ServerLevel) this.level()).getChunkSource().broadcast(this, packet);
        }
    }

    @Inject(method = "onEffectRemoved", at = @At("TAIL"))
    private void infernalexp$syncLuminousRemoved(MobEffectInstance effectInstance, CallbackInfo ci) {
        if (this.level().isClientSide) return;

        if (effectInstance.getEffect() == ModEffects.LUMINOUS.get()) {
            Packet<?> packet = new ClientboundRemoveMobEffectPacket(this.getId(), effectInstance.getEffect());
            ((ServerLevel) this.level()).getChunkSource().broadcast(this, packet);
        }
    }
}