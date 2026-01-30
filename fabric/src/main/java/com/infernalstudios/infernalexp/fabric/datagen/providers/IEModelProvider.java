package com.infernalstudios.infernalexp.fabric.datagen.providers;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModItems;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import com.infernalstudios.infernalexp.registration.holders.ItemDataHolder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;

import java.util.Map;

public class IEModelProvider extends FabricModelProvider {
    public IEModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        for (BlockDataHolder<?> blockDataHolder : ModBlocks.getBlockRegistry().values()) {
            if (blockDataHolder.getBlocksets().isEmpty()) {

                if (blockDataHolder.isGlass()) {
                    generator.createGlassBlocks(blockDataHolder.get(), blockDataHolder.getPaneBlock().get());
                } else if (blockDataHolder.hasModel()) {
                    switch (blockDataHolder.getModel()) {
                        case CUBE -> generator.createTrivialCube(blockDataHolder.get());
                        case PILLAR -> {
                            var pillar = generator.woodProvider(blockDataHolder.get());
                            pillar.log(blockDataHolder.get());
                        }
                        case WOOD -> {
                            Block block = blockDataHolder.get();
                            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

                            if (name.contains("hyphae")) {
                                Block stem = BuiltInRegistries.BLOCK.get(IECommon.makeID(name.replace("hyphae", "stem")));
                                generator.woodProvider(stem).wood(block);
                            } else {
                                var pillar = generator.woodProvider(block);
                                pillar.wood(block);
                            }
                        }
                        case ROTATABLE -> generator.createRotatedVariantBlock(blockDataHolder.get());
                        case CROSS ->
                                generator.createCrossBlockWithDefaultItem(blockDataHolder.get(), BlockModelGenerators.TintState.NOT_TINTED);
                        case DOOR -> generator.createDoor(blockDataHolder.get());
                        case TRAPDOOR -> generator.createTrapdoor(blockDataHolder.get());
                        case FLOWER_POT -> {
                            Block potted = blockDataHolder.get();
                            if (potted instanceof FlowerPotBlock pot) {
                                TextureMapping textureMapping = TextureMapping.plant(pot.getContent());
                                ResourceLocation modelLocation = ModelTemplates.FLOWER_POT_CROSS.create(potted, textureMapping, generator.modelOutput);
                                generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(potted, modelLocation));
                            }
                        }
                        case SIGN, HANGING_SIGN -> {
                            Block sign = blockDataHolder.get();
                            Block wallSign = blockDataHolder.getWallSignBlock();
                            Block textureSource = blockDataHolder.getTextureSourceBlock();
                            TextureMapping textureMapping = TextureMapping.particle(textureSource);
                            ResourceLocation resourceLocation = ModelTemplates.PARTICLE_ONLY.create(sign, textureMapping, generator.modelOutput);
                            generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(sign, resourceLocation));
                            generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(wallSign, resourceLocation));
                            generator.skipAutoItemBlock(wallSign);
                        }
                    }
                }
            } else {
                BlockModelGenerators.BlockFamilyProvider familyProvider = generator.family(blockDataHolder.get());
                for (Map.Entry<BlockDataHolder.Model, BlockDataHolder<?>> entry : blockDataHolder.getBlocksets().entrySet()) {
                    switch (entry.getKey()) {
                        case STAIRS -> familyProvider.stairs(entry.getValue().get());
                        case SLAB -> familyProvider.slab(entry.getValue().get());
                        case WALL -> familyProvider.wall(entry.getValue().get());
                        case PRESSURE_PLATE -> familyProvider.pressurePlate(entry.getValue().get());
                        case BUTTON -> familyProvider.button(entry.getValue().get());
                        case FENCE -> familyProvider.fence(entry.getValue().get());
                        case FENCE_GATE -> familyProvider.fenceGate(entry.getValue().get());
                    }
                }
            }
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        for (ItemDataHolder<?> itemDataHolder : ModItems.getItemRegistry().values()) {
            if (itemDataHolder.hasModel()) {
                generator.generateFlatItem(itemDataHolder.get(), itemDataHolder.getModel());
            }
        }
    }
}