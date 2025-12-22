package com.infernalstudios.infernalexp.compat;

import com.infernalstudios.infernalexp.module.ModBiomes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class TerraBlenderRegion extends Region {
    public TerraBlenderRegion(ResourceLocation name, int weight) {
        super(name, RegionType.NETHER, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        ModBiomes.getBiomeRegistry().forEach((biomeKey, parameterPoint) -> this.addBiome(mapper, parameterPoint, biomeKey));
    }
}