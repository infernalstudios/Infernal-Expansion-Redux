package com.infernalstudios.infernalexp;

import com.infernalstudios.infernalexp.datagen.config.ConfiguredData;
import com.infernalstudios.infernalexp.module.*;
import net.minecraft.resources.ResourceLocation;

public class IECommon {
    public static void init() {
        ConfiguredData.register();

        ModBlocks.load();
        ModItems.load();

        ModFireTypes.load();

        ModEntityTypes.load();
        ModCreativeTabs.load();

        ModBiomes.load();
        ModFeatures.load();
        ModCarvers.load();
        ModParticleTypes.load();
        ModSounds.load();
    }

    public static ResourceLocation makeID(String name) {
        return new ResourceLocation(IEConstants.MOD_ID, name);
    }

    @Deprecated
    public static <T> T log(T message) {
        return log(message, 0);
    }

    public static <T> T log(T message, int level) {
        if (level == 0)
            IEConstants.LOG.info("[InfernalExpansion] {}", message);
        else if (level == 1)
            IEConstants.LOG.warn("[InfernalExpansion] {}", message);
        else if (level == 2)
            IEConstants.LOG.error("[InfernalExpansion] {}", message);
        return message;
    }
}