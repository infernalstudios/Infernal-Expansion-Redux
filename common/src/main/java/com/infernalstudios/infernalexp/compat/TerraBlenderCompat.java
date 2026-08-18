package com.infernalstudios.infernalexp.compat;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.world.surface.ModSurfaceRules;
import terrablender.api.SurfaceRuleManager;

public class TerraBlenderCompat {
    public static void register() {
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.NETHER, IEConstants.MOD_ID, ModSurfaceRules.addNetherSurfaceRulesWithBedrock());
    }
}