package com.infernalstudios.infernalexp.world.surface;

import com.infernalstudios.infernalexp.module.ModBiomes;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModSurfaceRuleConditions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

public class ModSurfaceRules {
    private static final SurfaceRules.RuleSource SHIMMER_SAND = makeStateRule(ModBlocks.SHIMMER_SAND.get());
    private static final SurfaceRules.RuleSource SHIMMER_STONE = makeStateRule(ModBlocks.SHIMMER_STONE.get());
    private static final SurfaceRules.RuleSource GLIMMER_GRAVEL = makeStateRule(ModBlocks.GLIMMER_GRAVEL.get());
    private static final SurfaceRules.RuleSource DIMSTONE = makeStateRule(ModBlocks.DIMSTONE.get());
    private static final SurfaceRules.RuleSource DULLSTONE = makeStateRule(ModBlocks.DULLSTONE.get());
    private static final SurfaceRules.RuleSource BEDROCK = makeStateRule(Blocks.BEDROCK);

    // New Rules
    private static final SurfaceRules.RuleSource BASILT = makeStateRule(ModBlocks.BASILT.get());
    private static final SurfaceRules.RuleSource GRAVEL = makeStateRule(Blocks.GRAVEL);
    private static final SurfaceRules.RuleSource BLACKSTONE = makeStateRule(Blocks.BLACKSTONE);

    private static final SurfaceRules.RuleSource GLOWSTONE_CANYON = SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.GLOWSTONE_CANYON), SurfaceRules.sequence(
            SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
                    SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, GLIMMER_GRAVEL),
                    SHIMMER_SAND)),
            SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(3, false, CaveSurface.FLOOR), SHIMMER_STONE),
            SurfaceRules.ifTrue(ModSurfaceRuleConditions.chance("dimstone", 0.02F), DIMSTONE),
            DULLSTONE
    ));

    private static final SurfaceRules.RuleSource DELTA_SHORES = SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.DELTA_SHORES), SurfaceRules.sequence(
            SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), SurfaceRules.sequence(
                    SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, -0.012, 1.7976931348623157E308),
                            SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(30), 0),
                                    SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(35), 0)),
                                            GRAVEL))),
                    SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.NETHER_STATE_SELECTOR, 0.0, 1.7976931348623157E308), BASILT),
                    BLACKSTONE
            ))
    ));

    public static SurfaceRules.RuleSource addNetherSurfaceRules() {
        return SurfaceRules.sequence(GLOWSTONE_CANYON, DELTA_SHORES);
    }

    public static SurfaceRules.RuleSource addNetherSurfaceRulesWithBedrock() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK),
                SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK),
                addNetherSurfaceRules()
        );
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}