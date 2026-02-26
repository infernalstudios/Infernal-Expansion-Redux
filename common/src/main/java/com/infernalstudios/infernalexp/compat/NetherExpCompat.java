package com.infernalstudios.infernalexp.compat;

import com.infernalstudios.infernalexp.platform.Services;
import net.jadenxgamer.netherexp.registry.worldgen.feature.custom.WarpedFungusFeature;

public class NetherExpCompat {

    public static boolean isNetherExpFungus(Object feature) {
        if (!Services.PLATFORM.isModLoaded("netherexp")) {
            return false;
        }

        return InnerCompat.isWarpedFungus(feature);
    }

    private static class InnerCompat {
        static boolean isWarpedFungus(Object feature) {
            return feature instanceof WarpedFungusFeature;
        }
    }
}