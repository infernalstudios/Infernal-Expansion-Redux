package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.effect.InfectionEffect;
import com.infernalstudios.infernalexp.effect.LuminousEffect;
import com.infernalstudios.infernalexp.effect.StatusEffect;
import com.infernalstudios.infernalexp.registration.holders.MobEffectDataHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

public class ModEffects {
    private static final Map<ResourceLocation, MobEffectDataHolder<?>> EFFECT_REGISTRY = new HashMap<>();

    public static MobEffectDataHolder<?> register(String name, MobEffectDataHolder<?> effect) {
        ResourceLocation id = IECommon.makeID(name);
        EFFECT_REGISTRY.put(id, effect);
        return effect;
    }

    public static Map<ResourceLocation, MobEffectDataHolder<?>> getEffectRegistry() {
        return EFFECT_REGISTRY;
    }

    public static void load() {}

    public static final MobEffectDataHolder<?> WARPED = register("warped", MobEffectDataHolder.of(() ->
            new StatusEffect(MobEffectCategory.BENEFICIAL, 0x00ffba)))
            .withTranslation("Warped")
            .withPotion(() -> Items.WARPED_FUNGUS);

    public static final MobEffectDataHolder<?> INFECTION = register("infection", MobEffectDataHolder.of(() ->
            new InfectionEffect(MobEffectCategory.HARMFUL, 12918043)))
            .withTranslation("Infection");

    public static final MobEffectDataHolder<?> LUMINOUS = register("luminous", MobEffectDataHolder.of(() ->
            new LuminousEffect(MobEffectCategory.NEUTRAL, 16777086)))
            .withTranslation("Luminous");
}