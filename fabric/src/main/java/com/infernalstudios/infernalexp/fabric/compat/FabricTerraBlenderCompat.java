package com.infernalstudios.infernalexp.fabric.compat;

import com.infernalstudios.infernalexp.compat.TerraBlenderCompat;
import terrablender.api.TerraBlenderApi;

public class FabricTerraBlenderCompat implements TerraBlenderApi {
    @Override
    public void onTerraBlenderInitialized() {
        TerraBlenderCompat.register();
    }
}