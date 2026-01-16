package com.infernalstudios.infernalexp.world.structure;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.module.ModBiomes;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

import java.util.Map;

public class ModStructures {
    public static final ResourceKey<Structure> HEAVEN_PORTAL = ResourceKey.create(Registries.STRUCTURE, IECommon.makeID("heaven_portal"));
    public static final ResourceKey<StructureSet> HEAVEN_PORTALS = ResourceKey.create(Registries.STRUCTURE_SET, IECommon.makeID("heaven_portals"));

    public static void bootstrapStructures(BootstapContext<Structure> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        context.register(HEAVEN_PORTAL, new HeavenPortalStructure(
                new Structure.StructureSettings(
                        HolderSet.direct(biomes.getOrThrow(ModBiomes.GLOWSTONE_CANYON)),
                        Map.of(),
                        GenerationStep.Decoration.SURFACE_STRUCTURES,
                        TerrainAdjustment.NONE
                ),
                context.lookup(Registries.TEMPLATE_POOL).getOrThrow(ModStructurePools.HEAVEN_PORTAL_POOL),
                1,
                UniformHeight.of(VerticalAnchor.absolute(32), VerticalAnchor.absolute(100)),
                false
        ));
    }

    public static void bootstrapSets(BootstapContext<StructureSet> context) {
        context.register(HEAVEN_PORTALS, new StructureSet(
                context.lookup(Registries.STRUCTURE).getOrThrow(HEAVEN_PORTAL),
                new RandomSpreadStructurePlacement(
                        8,
                        4,
                        RandomSpreadType.LINEAR,
                        20394857
                )
        ));
    }
}