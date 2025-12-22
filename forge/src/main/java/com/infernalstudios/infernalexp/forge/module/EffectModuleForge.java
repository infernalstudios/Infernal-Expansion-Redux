package com.infernalstudios.infernalexp.forge.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.module.ModEffects;
import com.infernalstudios.infernalexp.registration.holders.MobEffectDataHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Map;

@Mod.EventBusSubscriber(modid = "infernalexp", bus = Mod.EventBusSubscriber.Bus.MOD)
public class EffectModuleForge {
    @SubscribeEvent
    public static void registerEffects(RegisterEvent event) {
        for (Map.Entry<ResourceLocation, MobEffectDataHolder<?>> entry : ModEffects.getEffectRegistry().entrySet()) {
            // Register effect
            event.register(ForgeRegistries.MOB_EFFECTS.getRegistryKey(), entry.getKey(), entry.getValue()::get);

            if (entry.getValue().hasPotion()) {
                String id = entry.getKey().getPath();

                event.register(ForgeRegistries.POTIONS.getRegistryKey(), entry.getKey(), () ->
                        new Potion(new MobEffectInstance(entry.getValue().get(), 3600)));
                event.register(ForgeRegistries.POTIONS.getRegistryKey(), IECommon.makeID("long_" + id), () ->
                        new Potion(id, new MobEffectInstance(entry.getValue().get(), 9600)));
                event.register(ForgeRegistries.POTIONS.getRegistryKey(), IECommon.makeID("strong_" + id), () ->
                        new Potion(id, new MobEffectInstance(entry.getValue().get(), 1800, 1)));
            }
        }
    }
}