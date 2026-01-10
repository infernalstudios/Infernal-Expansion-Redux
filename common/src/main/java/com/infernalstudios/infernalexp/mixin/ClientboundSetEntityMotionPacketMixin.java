package com.infernalstudios.infernalexp.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundSetEntityMotionPacket.class)
public class ClientboundSetEntityMotionPacketMixin {

    @Shadow
    private int xa;
    @Shadow
    private int ya;
    @Shadow
    private int za;

    @Unique
    private double infernal$originalX;
    @Unique
    private double infernal$originalY;
    @Unique
    private double infernal$originalZ;

    @Inject(method = "<init>(ILnet/minecraft/world/phys/Vec3;)V", at = @At("TAIL"))
    private void infernal$capturePreciseVelocity(int id, Vec3 deltaMovement, CallbackInfo ci) {
        this.infernal$originalX = deltaMovement.x;
        this.infernal$originalY = deltaMovement.y;
        this.infernal$originalZ = deltaMovement.z;
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void infernal$writePreciseVelocity(FriendlyByteBuf buffer, CallbackInfo ci) {
        buffer.writeDouble(this.infernal$originalX);
        buffer.writeDouble(this.infernal$originalY);
        buffer.writeDouble(this.infernal$originalZ);
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
    private void infernal$readPreciseVelocity(FriendlyByteBuf buffer, CallbackInfo ci) {
        if (buffer.readableBytes() >= 24) {
            double x = buffer.readDouble();
            double y = buffer.readDouble();
            double z = buffer.readDouble();

            this.xa = (int) (x * 8000.0D);
            this.ya = (int) (y * 8000.0D);
            this.za = (int) (z * 8000.0D);
        }
    }
}