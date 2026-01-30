package com.infernalstudios.infernalexp.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "infernalexp")
public class ClothConfigConstructor implements ConfigData {
    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.TransitiveObject
    public IEConfig.Common common = new IEConfig.Common();

    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject
    public IEConfig.Client client = new IEConfig.Client();
}