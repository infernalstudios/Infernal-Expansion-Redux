package com.infernalstudios.infernalexp.neoforge.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.module.ModEffects;
import com.infernalstudios.infernalexp.registration.holders.MobEffectDataHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Map;

@EventBusSubscriber(modid = IEConstants.MOD_ID)
public class EffectModuleNeoForge {
    @SubscribeEvent
    public static void registerEffects(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.MOB_EFFECT)) {
            for (Map.Entry<ResourceLocation, MobEffectDataHolder<?>> entry : ModEffects.getEffectRegistry().entrySet()) {
                Registry.register(BuiltInRegistries.MOB_EFFECT, entry.getKey(), entry.getValue().get());
            }
        }

        if (event.getRegistryKey().equals(Registries.POTION)) {
            for (Map.Entry<ResourceLocation, MobEffectDataHolder<?>> entry : ModEffects.getEffectRegistry().entrySet()) {
                if (entry.getValue().hasPotion()) {
                    String id = entry.getKey().getPath();

                    Registry.register(BuiltInRegistries.POTION, entry.getKey(),
                            new Potion(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(entry.getValue().get()), 3600)));
                    Registry.register(BuiltInRegistries.POTION, IECommon.makeID("long_" + id),
                            new Potion(id, new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(entry.getValue().get()), 9600)));
                    Registry.register(BuiltInRegistries.POTION, IECommon.makeID("strong_" + id),
                            new Potion(id, new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(entry.getValue().get()), 1800, 1)));
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        for (Map.Entry<ResourceLocation, MobEffectDataHolder<?>> entry : ModEffects.getEffectRegistry().entrySet()) {
            if (entry.getValue().hasPotion()) {
                ResourceLocation key = entry.getKey();
                String id = key.getPath();

                Holder<Potion> baseHolder = BuiltInRegistries.POTION.getHolderOrThrow(ResourceKey.create(Registries.POTION, key));
                Holder<Potion> longHolder = BuiltInRegistries.POTION.getHolderOrThrow(ResourceKey.create(Registries.POTION, IECommon.makeID("long_" + id)));
                Holder<Potion> strongHolder = BuiltInRegistries.POTION.getHolderOrThrow(ResourceKey.create(Registries.POTION, IECommon.makeID("strong_" + id)));

                builder.addMix(Potions.AWKWARD, entry.getValue().getPotionIngredient().get(), baseHolder);
                builder.addMix(baseHolder, Items.REDSTONE, longHolder);
                builder.addMix(baseHolder, Items.GLOWSTONE_DUST, strongHolder);
            }
        }
    }
}