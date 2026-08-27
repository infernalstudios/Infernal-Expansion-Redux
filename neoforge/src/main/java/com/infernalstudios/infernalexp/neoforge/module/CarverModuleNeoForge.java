package com.infernalstudios.infernalexp.neoforge.module;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.module.ModCarvers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Map;

@EventBusSubscriber(modid = IEConstants.MOD_ID)
public class CarverModuleNeoForge {
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