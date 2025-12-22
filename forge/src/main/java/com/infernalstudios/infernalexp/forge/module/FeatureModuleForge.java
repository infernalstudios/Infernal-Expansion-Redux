package com.infernalstudios.infernalexp.forge.module;

import com.infernalstudios.infernalexp.module.ModFeatures;
import com.infernalstudios.infernalexp.registration.FuelRegistry;
import com.infernalstudios.infernalexp.registration.holders.ItemDataHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Map;

@Mod.EventBusSubscriber(modid = "infernalexp", bus = Mod.EventBusSubscriber.Bus.MOD)
public class FeatureModuleForge {
    @SubscribeEvent
    public static void registerFeatures(RegisterEvent event) {
        for (Map.Entry<ResourceKey<Feature<?>>, Feature<?>> entry : ModFeatures.getFeatureRegistry().entrySet()) {
            // Register feature
            event.register(Registries.FEATURE, featureRegisterHelper ->
                    featureRegisterHelper.register(entry.getKey(), entry.getValue())
            );
        }
    }
}