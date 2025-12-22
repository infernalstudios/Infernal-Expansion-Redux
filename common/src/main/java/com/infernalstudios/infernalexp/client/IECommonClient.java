package com.infernalstudios.infernalexp.client;

import com.infernalstudios.infernalexp.module.ModEntityRenderers;
import com.infernalstudios.infernalexp.module.ModModelLayers;

public class IECommonClient {
    public static void init() {
        ModEntityRenderers.load();
        ModModelLayers.load();
    }
}