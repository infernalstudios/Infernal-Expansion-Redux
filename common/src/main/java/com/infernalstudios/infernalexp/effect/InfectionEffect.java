package com.infernalstudios.infernalexp.effect;

import com.infernalstudios.infernalexp.module.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class InfectionEffect extends MobEffect {

    public InfectionEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.getHealth() > 1.0F) {
            entity.hurt(entity.damageSources().magic(), 1.0F);
        }

        for (LivingEntity nearbyEntity : entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(3))) {
            if (!nearbyEntity.hasEffect(ModEffects.INFECTION.get())) {
                int currentDuration = entity.getEffect(ModEffects.INFECTION.get()).getDuration();
                nearbyEntity.addEffect(new MobEffectInstance(ModEffects.INFECTION.get(), currentDuration / 2));
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        int j = 50 >> amplifier;
        if (j > 0) {
            return duration % j == 0;
        } else {
            return true;
        }
    }
}