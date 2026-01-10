package com.infernalstudios.infernalexp.client;

import com.infernalstudios.infernalexp.module.ModEntityRenderers;
import com.infernalstudios.infernalexp.module.ModBlockEntityRenderers;
import com.infernalstudios.infernalexp.module.ModItemProperties;
import com.infernalstudios.infernalexp.module.ModModelLayers;

public class IECommonClient {
    public static void init() {
        ModEntityRenderers.load();
        ModBlockEntityRenderers.load();
        ModModelLayers.load();
    }

    public static void initItemProperties() {
        ModItemProperties.init();
    }
}