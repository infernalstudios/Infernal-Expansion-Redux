package com.infernalstudios.infernalexp.neoforge.module;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.module.ModFeatures;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Map;

@EventBusSubscriber(modid = IEConstants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class FeatureModuleNeoForge {
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