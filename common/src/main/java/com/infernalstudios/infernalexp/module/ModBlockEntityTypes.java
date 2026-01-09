package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.block.entity.LuminousFungusBlockEntity;
import com.infernalstudios.infernalexp.block.entity.VolatileGeyserBlockEntity;
import com.infernalstudios.infernalexp.platform.Services;
import com.infernalstudios.infernalexp.registration.util.RegistrationProvider;
import com.infernalstudios.infernalexp.registration.util.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntityTypes {

    public static final RegistrationProvider<BlockEntityType<?>> BLOCK_ENTITY_TYPES = RegistrationProvider.get(BuiltInRegistries.BLOCK_ENTITY_TYPE, IEConstants.MOD_ID);

    public static final RegistryObject<BlockEntityType<VolatileGeyserBlockEntity>> VOLATILE_GEYSER = BLOCK_ENTITY_TYPES.register("volatile_geyser",
            () -> Services.PLATFORM.createBlockEntityType(
                    VolatileGeyserBlockEntity::new,
                    ModBlocks.VOLATILE_GEYSER.get()
            ));

    public static final RegistryObject<BlockEntityType<LuminousFungusBlockEntity>> LUMINOUS_FUNGUS = BLOCK_ENTITY_TYPES.register("luminous_fungus",
            () -> Services.PLATFORM.createBlockEntityType(
                    LuminousFungusBlockEntity::new,
                    ModBlocks.LUMINOUS_FUNGUS.get()
            ));

    public static void load() {}
}