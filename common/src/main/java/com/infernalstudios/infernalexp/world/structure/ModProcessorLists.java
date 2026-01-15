package com.infernalstudios.infernalexp.world.structure;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.module.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.List;

public class ModProcessorLists {
    public static final ResourceKey<StructureProcessorList> HEAVEN_PORTAL_PROCESSOR = ResourceKey.create(Registries.PROCESSOR_LIST, IECommon.makeID("heaven_portal_processor"));

    public static void bootstrap(BootstapContext<StructureProcessorList> context) {
        context.register(HEAVEN_PORTAL_PROCESSOR, new StructureProcessorList(List.of(
                new RuleProcessor(List.of(
                        new ProcessorRule(
                                new RandomBlockMatchTest(Blocks.GLOWSTONE, 0.3F),
                                AlwaysTrueTest.INSTANCE,
                                ModBlocks.DIMSTONE.get().defaultBlockState()
                        )
                ))
        )));
    }
}