package com.infernalstudios.infernalexp.fabric.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.module.ModEffects;
import com.infernalstudios.infernalexp.registration.holders.MobEffectDataHolder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;

import java.util.Map;

public class EffectModuleFabric {
    public static void registerEffects() {
        for (Map.Entry<ResourceLocation, MobEffectDataHolder<?>> entry : ModEffects.getEffectRegistry().entrySet()) {
            // Register effect
            Registry.register(BuiltInRegistries.MOB_EFFECT, entry.getKey(), entry.getValue().get());

            if (entry.getValue().hasPotion()) {
                String id = entry.getKey().getPath();

                Potion base = Registry.register(BuiltInRegistries.POTION, entry.getKey(),
                        new Potion(new MobEffectInstance(entry.getValue().get(), 3600)));
                Potion long_ = Registry.register(BuiltInRegistries.POTION, IECommon.makeID("long_" + id),
                        new Potion(id, new MobEffectInstance(entry.getValue().get(), 9600)));
                Potion strong = Registry.register(BuiltInRegistries.POTION, IECommon.makeID("strong_" + id),
                        new Potion(id, new MobEffectInstance(entry.getValue().get(), 1800, 1)));

                PotionBrewing.addMix(Potions.AWKWARD, entry.getValue().getPotionIngredient().get(), base);
                PotionBrewing.addMix(base, Items.REDSTONE, long_);
                PotionBrewing.addMix(base, Items.GLOWSTONE_DUST, strong);
            }
        }
    }
}
