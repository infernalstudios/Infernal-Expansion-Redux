package com.infernalstudios.infernalexp.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class IEConfig {
    public Client client = new Client();
    public Common common = new Common();

    public static class Client {
    }

    public static class Common {
        @ConfigEntry.Gui.CollapsibleObject
        public Geyser geyser = new Geyser();

        @ConfigEntry.Gui.CollapsibleObject
        public Voline voline = new Voline();

        @ConfigEntry.Gui.CollapsibleObject
        public Miscellaneous miscellaneous = new Miscellaneous();

        @ConfigEntry.Gui.CollapsibleObject
        public MobInteractions mobInteractions = new MobInteractions();

        public static class Geyser {
            @ConfigEntry.Gui.Tooltip
            public int geyserSteamHeight = 8;
        }

        public static class Voline {
            @ConfigEntry.Gui.Tooltip
            public boolean volineTurnIntoGeyser = true;

            @ConfigEntry.Gui.Tooltip
            public boolean volineSleepWhenFed = true;

            @ConfigEntry.Gui.Tooltip
            public boolean volineGetBig = true;

            @ConfigEntry.Gui.Tooltip
            public int volineMagmaCreamAmount = 3;
        }

        public static class Miscellaneous {
            @ConfigEntry.Gui.Tooltip
            public double luminousFungusActivateDistance = 4.0D;
        }

        public static class MobInteractions {
            @ConfigEntry.Gui.Tooltip
            public double glowsilkBowSpeed = 1.0D;
        }
    }
}