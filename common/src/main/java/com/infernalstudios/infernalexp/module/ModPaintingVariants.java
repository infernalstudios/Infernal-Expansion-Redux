package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.registration.util.RegistrationProvider;
import com.infernalstudios.infernalexp.registration.util.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class ModPaintingVariants {
    public static final RegistrationProvider<PaintingVariant> PAINTING_VARIANTS =
            RegistrationProvider.get(BuiltInRegistries.PAINTING_VARIANT, IEConstants.MOD_ID);

    public static final RegistryObject<PaintingVariant> GLOWSTONE_CANYON =
            PAINTING_VARIANTS.register("glowstone_canyon", () -> new PaintingVariant(64, 64));

    public static final RegistryObject<PaintingVariant> THE_FALLEN_ONES =
            PAINTING_VARIANTS.register("the_fallen_ones", () -> new PaintingVariant(48, 64));

    public static void load() {
    }
}