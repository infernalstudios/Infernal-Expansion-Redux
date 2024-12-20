package com.infernalstudios.infernalexp.module;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Map;

@Mod.EventBusSubscriber(modid = "infernalexp", bus = Mod.EventBusSubscriber.Bus.MOD)
public class CarverModuleForge {
    @SubscribeEvent
    public static void registerCarvers(RegisterEvent event) {
        for (Map.Entry<ResourceKey<WorldCarver<?>>, WorldCarver<?>> entry : ModCarvers.getCarverRegistry().entrySet()) {
            // Register carver
            event.register(Registries.CARVER, carverRegisterHelper ->
                    carverRegisterHelper.register(entry.getKey(), entry.getValue())
            );
        }
    }
}