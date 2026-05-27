package com.infernalstudios.infernalexp.neoforge.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.module.ModEffects;
import com.infernalstudios.infernalexp.registration.holders.MobEffectDataHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Map;

@EventBusSubscriber(modid = IEConstants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class EffectModuleNeoForge {
    @SubscribeEvent
    public static void registerEffects(RegisterEvent event) {
        for (Map.Entry<ResourceLocation, MobEffectDataHolder<?>> entry : ModEffects.getEffectRegistry().entrySet()) {
            // Register effect
            event.register(NeoForgeRegistries.MOB_EFFECTS.getRegistryKey(), entry.getKey(), entry.getValue()::get);

            if (entry.getValue().hasPotion()) {
                String id = entry.getKey().getPath();

                event.register(NeoForgeRegistries.POTIONS.getRegistryKey(), entry.getKey(), () ->
                        new Potion(new MobEffectInstance(entry.getValue().get(), 3600)));
                event.register(NeoForgeRegistries.POTIONS.getRegistryKey(), IECommon.makeID("long_" + id), () ->
                        new Potion(id, new MobEffectInstance(entry.getValue().get(), 9600)));
                event.register(NeoForgeRegistries.POTIONS.getRegistryKey(), IECommon.makeID("strong_" + id), () ->
                        new Potion(id, new MobEffectInstance(entry.getValue().get(), 1800, 1)));
            }
        }
    }
}