package com.infernalstudios.infernalexp;

import com.infernalstudios.infernalexp.module.*;
import net.minecraft.resources.ResourceLocation;

public class IECommon {

    public static void init() {
        ModBlocks.load();
        ModItems.load();

        ModFireTypes.load();
        ModParticles.load();

        ModEntityTypes.load();
        ModCreativeTabs.load();

        ModBiomes.load();
        ModFeatures.load();
        ModCarvers.load();
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(IEConstants.MOD_ID, name);
    }
}