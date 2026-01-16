package com.infernalstudios.infernalexp.world.structure;

import com.infernalstudios.infernalexp.module.ModStructureTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HeavenPortalStructure extends Structure {

    public static final Codec<HeavenPortalStructure> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.maxDepth),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter(structure -> structure.useExpansionHack),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_height_map").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, HeavenPortalStructure::new));

    private final Holder<StructureTemplatePool> startPool;
    private final int maxDepth;
    private final HeightProvider startHeight;
    private final boolean useExpansionHack;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;

    public HeavenPortalStructure(Structure.StructureSettings settings, Holder<StructureTemplatePool> startPool, int maxDepth, HeightProvider startHeight, boolean useExpansionHack, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter) {
        super(settings);
        this.startPool = startPool;
        this.maxDepth = maxDepth;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    public HeavenPortalStructure(Structure.StructureSettings settings, Holder<StructureTemplatePool> startPool, int maxDepth, HeightProvider startHeight, boolean useExpansionHack) {
        this(settings, startPool, maxDepth, startHeight, useExpansionHack, Optional.empty(), 80);
    }

    private static Optional<Integer> getSuitableNetherYLevel(Structure.GenerationContext context, BlockPos pos) {
        NoiseColumn column = context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), context.heightAccessor(), context.randomState());
        List<Integer> suitableYLevels = new ArrayList<>();

        for (int y = 127; y > context.chunkGenerator().getSeaLevel(); y--) {
            if (column.getBlock(y - 1).canOcclude() && column.getBlock(y).isAir() && column.getBlock(y + 4).isAir()) {
                suitableYLevels.add(y);
            }
        }

        if (suitableYLevels.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(suitableYLevels.get(context.random().nextInt(suitableYLevels.size())));
    }

    @Override
    public @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        var structureSets = context.registryAccess().registryOrThrow(Registries.STRUCTURE_SET);
        var netherComplexes = structureSets.getHolder(BuiltinStructureSets.NETHER_COMPLEXES);

        if (netherComplexes.isPresent()) {
            StructurePlacement placement = netherComplexes.get().value().placement();
            if (placement instanceof RandomSpreadStructurePlacement spreadPlacement) {
                ChunkPos structureChunk = spreadPlacement.getPotentialStructureChunk(context.seed(), context.chunkPos().x, context.chunkPos().z);

                if (structureChunk.x == context.chunkPos().x && structureChunk.z == context.chunkPos().z) {
                    return Optional.empty();
                }
            }
        }

        Optional<Integer> yLevel = getSuitableNetherYLevel(context, context.chunkPos().getMiddleBlockPosition(0));

        if (yLevel.isEmpty()) {
            return Optional.empty();
        }

        BlockPos blockPos = context.chunkPos().getMiddleBlockPosition(yLevel.get());

        return JigsawPlacement.addPieces(
                context,
                this.startPool,
                Optional.empty(),
                this.maxDepth,
                blockPos,
                this.useExpansionHack,
                this.projectStartToHeightmap,
                this.maxDistanceFromCenter
        );
    }

    @Override
    public @NotNull StructureType<?> type() {
        return ModStructureTypes.HEAVEN_PORTAL.get();
    }
}