package com.infernalstudios.infernalexp.fabric.datagen.providers;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.world.level.block.FlowerPotBlock;

import java.util.Map;
import java.util.Objects;

public class IEBlockLootTableProvider extends FabricBlockLootTableProvider {
    public IEBlockLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        for (BlockDataHolder<?> blockDataHolder : ModBlocks.getBlockRegistry().values()) {
            for (BlockDataHolder<?> blocksetHolder : blockDataHolder.getBlocksets().values()) {
                if (blocksetHolder.hasModel()) {
                    if (Objects.requireNonNull(blockDataHolder.getModel()) == BlockDataHolder.Model.SLAB) {
                        add(blockDataHolder.get(), createSlabItemTable(blockDataHolder.get()));
                    }
                }
            }

            if (blockDataHolder.isGlass()) {
                add(blockDataHolder.get(), createSilkTouchOnlyTable(blockDataHolder.get()));
                add(blockDataHolder.getPaneBlock().get(), createSilkTouchOnlyTable(blockDataHolder.getPaneBlock().get()));
            } else if (blockDataHolder.hasModel()) {
                switch (blockDataHolder.getModel()) {
                    case SLAB -> {
                        add(blockDataHolder.get(), createSlabItemTable(blockDataHolder.get()));
                        continue;
                    }
                    case DOOR -> {
                        add(blockDataHolder.get(), createDoorTable(blockDataHolder.get()));
                        continue;
                    }
                    case FLOWER_POT -> {
                        add(blockDataHolder.get(), createPotFlowerItemTable(((FlowerPotBlock) blockDataHolder.get()).getContent()));
                        continue;
                    }
                }
            }

            if (blockDataHolder.hasDrop()) {
                if (blockDataHolder.getDropCount() == null)
                    add(blockDataHolder.get(), createSilkTouchOnlyTable(blockDataHolder.getDrop().get()));
                else
                    add(blockDataHolder.get(), createSingleItemTable(blockDataHolder.getDrop().get(), blockDataHolder.getDropCount()));
            }

            for (Map.Entry<BlockDataHolder.Model, BlockDataHolder<?>> entry : blockDataHolder.getBlocksets().entrySet()) {
                switch (entry.getKey()) {
                    case SLAB -> add(entry.getValue().get(), createSlabItemTable(entry.getValue().get()));
                    case DOOR -> add(entry.getValue().get(), createDoorTable(entry.getValue().get()));
                    default -> add(entry.getValue().get(), createSingleItemTable(entry.getValue().get()));
                }
            }
        }
    }
}