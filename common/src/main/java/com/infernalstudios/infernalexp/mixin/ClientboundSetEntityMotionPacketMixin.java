package com.infernalstudios.infernalexp.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundSetEntityMotionPacket.class)
public class ClientboundSetEntityMotionPacketMixin {

    @Shadow
    @Final
    @Mutable
    private int id;

    @Shadow
    @Final
    @Mutable
    private int xa;

    @Shadow
    @Final
    @Mutable
    private int ya;

    @Shadow
    @Final
    @Mutable
    private int za;

    @Inject(method = "<init>(ILnet/minecraft/world/phys/Vec3;)V", at = @At("RETURN"))
    private void infernalexp$cacheVelocity(int id, Vec3 velocity, CallbackInfo ci) {
        this.xa = (int) (velocity.x * 8000.0D);
        this.ya = (int) (velocity.y * 8000.0D);
        this.za = (int) (velocity.z * 8000.0D);
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("RETURN"))
    private void infernalexp$readVelocity(FriendlyByteBuf buffer, CallbackInfo ci) {
        buffer.readerIndex(buffer.readerIndex() - 6);

        float x = buffer.readFloat();
        float y = buffer.readFloat();
        float z = buffer.readFloat();

        this.xa = (int) (x * 8000.0F);
        this.ya = (int) (y * 8000.0F);
        this.za = (int) (z * 8000.0F);
    }

    @Inject(method = "write", at = @At("HEAD"), cancellable = true)
    private void infernalexp$writeVelocity(FriendlyByteBuf buffer, CallbackInfo ci) {
        buffer.writeVarInt(this.id);
        buffer.writeFloat((float) this.xa / 8000.0F);
        buffer.writeFloat((float) this.ya / 8000.0F);
        buffer.writeFloat((float) this.za / 8000.0F);
        ci.cancel();
    }
}