package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.entities.WarpbeetleEntity;
import com.infernalstudios.infernalexp.module.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.HoglinSpecificSensor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(HoglinSpecificSensor.class)
public class HoglinSpecificSensorMixin {

    @Inject(method = "doTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("TAIL"))
    public void avoidWarped(ServerLevel world, LivingEntity hoglin, CallbackInfo ci) {
        Player player = world.getNearestPlayer(hoglin.getX(), hoglin.getY(), hoglin.getZ(), 10,
                p -> p instanceof LivingEntity l && l.hasEffect(ModEffects.WARPED.get()));

        if (player != null) {
            hoglin.getBrain().setMemory(MemoryModuleType.NEAREST_REPELLENT, player.blockPosition());
            return;
        }

        Optional<WarpbeetleEntity> nearestBeetle = world.getEntitiesOfClass(WarpbeetleEntity.class, hoglin.getBoundingBox().inflate(8.0D, 4.0D, 8.0D))
                .stream()
                .min((b1, b2) -> Float.compare(b1.distanceTo(hoglin), b2.distanceTo(hoglin)));

        nearestBeetle.ifPresent(beetle -> hoglin.getBrain().setMemory(MemoryModuleType.NEAREST_REPELLENT, beetle.blockPosition()));
    }
}