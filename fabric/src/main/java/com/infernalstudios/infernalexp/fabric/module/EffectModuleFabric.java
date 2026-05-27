package com.infernalstudios.infernalexp.fabric.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.module.ModEffects;
import com.infernalstudios.infernalexp.registration.holders.MobEffectDataHolder;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

import java.util.Map;

public class EffectModuleFabric {
    public static void registerEffects() {
        for (Map.Entry<ResourceLocation, MobEffectDataHolder<?>> entry : ModEffects.getEffectRegistry().entrySet()) {
            Registry.register(BuiltInRegistries.MOB_EFFECT, entry.getKey(), entry.getValue().get());

            if (entry.getValue().hasPotion()) {
                String id = entry.getKey().getPath();

                Potion base = Registry.register(BuiltInRegistries.POTION, entry.getKey(),
                        new Potion(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(entry.getValue().get()), 3600)));
                Potion long_ = Registry.register(BuiltInRegistries.POTION, IECommon.makeID("long_" + id),
                        new Potion(id, new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(entry.getValue().get()), 9600)));
                Potion strong = Registry.register(BuiltInRegistries.POTION, IECommon.makeID("strong_" + id),
                        new Potion(id, new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(entry.getValue().get()), 1800, 1)));
            }
        }

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            for (Map.Entry<ResourceLocation, MobEffectDataHolder<?>> entry : ModEffects.getEffectRegistry().entrySet()) {
                if (entry.getValue().hasPotion()) {
                    String id = entry.getKey().getPath();

                    Holder<Potion> baseHolder = BuiltInRegistries.POTION.wrapAsHolder(BuiltInRegistries.POTION.get(entry.getKey()));
                    Holder<Potion> longHolder = BuiltInRegistries.POTION.wrapAsHolder(BuiltInRegistries.POTION.get(IECommon.makeID("long_" + id)));
                    Holder<Potion> strongHolder = BuiltInRegistries.POTION.wrapAsHolder(BuiltInRegistries.POTION.get(IECommon.makeID("strong_" + id)));

                    builder.addMix(Potions.AWKWARD, entry.getValue().getPotionIngredient().get(), baseHolder);
                    builder.addMix(baseHolder, Items.REDSTONE, longHolder);
                    builder.addMix(baseHolder, Items.GLOWSTONE_DUST, strongHolder);
                }
            }
        });
    }
}