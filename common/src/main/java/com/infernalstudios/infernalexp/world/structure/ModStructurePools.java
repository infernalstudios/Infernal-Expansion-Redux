package com.infernalstudios.infernalexp.world.structure;

import com.google.common.collect.ImmutableList;
import com.infernalstudios.infernalexp.IECommon;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public class ModStructurePools {
    public static final ResourceKey<StructureTemplatePool> HEAVEN_PORTAL_POOL = ResourceKey.create(Registries.TEMPLATE_POOL, IECommon.makeID("heaven_portal"));

    public static void bootstrap(BootstapContext<StructureTemplatePool> context) {
        Holder<StructureProcessorList> processor = context.lookup(Registries.PROCESSOR_LIST).getOrThrow(ModProcessorLists.HEAVEN_PORTAL_PROCESSOR);

        context.register(HEAVEN_PORTAL_POOL, new StructureTemplatePool(
                context.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY),
                ImmutableList.of(
                        Pair.of(StructurePoolElement.single(IECommon.makeID("heaven_portal_1").toString(), processor), 1),
                        Pair.of(StructurePoolElement.single(IECommon.makeID("heaven_portal_2").toString(), processor), 1),
                        Pair.of(StructurePoolElement.single(IECommon.makeID("heaven_portal_3").toString(), processor), 1),
                        Pair.of(StructurePoolElement.single(IECommon.makeID("heaven_portal_4").toString(), processor), 1),
                        Pair.of(StructurePoolElement.single(IECommon.makeID("heaven_portal_5").toString(), processor), 1),
                        Pair.of(StructurePoolElement.single(IECommon.makeID("heaven_portal_6").toString(), processor), 1)
                ),
                StructureTemplatePool.Projection.RIGID
        ));
    }
}