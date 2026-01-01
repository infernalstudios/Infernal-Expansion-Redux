package com.infernalstudios.infernalexp.compat;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.world.surface.ModSurfaceRules;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

public class TerraBlenderCompat {
    public static void register() {
        Regions.register(new TerraBlenderRegion(new ResourceLocation(IEConstants.MOD_ID, "nether"), 4));
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.NETHER, IEConstants.MOD_ID, ModSurfaceRules.addNetherSurfaceRulesWithBedrock());    }
}