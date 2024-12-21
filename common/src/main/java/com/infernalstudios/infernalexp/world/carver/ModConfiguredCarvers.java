package com.infernalstudios.infernalexp.world.carver;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.module.ModTags;
import com.infernalstudios.infernalexp.world.carver.custom.GlowstoneRavineCarver;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.*;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

public class ModConfiguredCarvers {
    public static ResourceKey<ConfiguredWorldCarver<?>> create(String name) {
        return ResourceKey.create(Registries.CONFIGURED_CARVER, IECommon.id(name));
    }

    private static <CC extends CarverConfiguration, C extends WorldCarver<CC>> void register(BootstapContext<ConfiguredWorldCarver<?>> context,
                                                                                             ResourceKey<ConfiguredWorldCarver<?>> key, C carver, CC config) {
        context.register(key, new ConfiguredWorldCarver<>(carver, config));
    }

    public static void bootstrap(BootstapContext<ConfiguredWorldCarver<?>> context) {
        register(context, GLOWSTONE_RAVINE, GlowstoneRavineCarver.INSTANCE,
                new CanyonCarverConfiguration(0.1f, UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.belowTop(1)),
                        ConstantFloat.of(2.0F), VerticalAnchor.aboveBottom(10),
                        CarverDebugSettings.of(false, Blocks.WARPED_BUTTON.defaultBlockState()), BuiltInRegistries.BLOCK.getOrCreateTag(ModTags.Blocks.GLOWSTONE_CANYON_CARVER_REPLACEABLES),
                        UniformFloat.of(-0.125F, 0.125F), new CanyonCarverConfiguration.CanyonShapeConfiguration(UniformFloat.of(0.75F, 1.0F),
                        TrapezoidFloat.of(0.0F, 6.0F, 2.0F), 3, UniformFloat.of(0.75F, 1.0F), 1.0F, 0.0F)));
    }



    public static final ResourceKey<ConfiguredWorldCarver<?>> GLOWSTONE_RAVINE = create("glowstone_ravine");
}
