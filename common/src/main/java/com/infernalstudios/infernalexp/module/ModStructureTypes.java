package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.registration.util.RegistrationProvider;
import com.infernalstudios.infernalexp.registration.util.RegistryObject;
import com.infernalstudios.infernalexp.world.structure.HeavenPortalStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class ModStructureTypes {

    public static final RegistrationProvider<StructureType<?>> STRUCTURE_TYPES = RegistrationProvider.get(Registries.STRUCTURE_TYPE, IEConstants.MOD_ID);

    public static final RegistryObject<StructureType<HeavenPortalStructure>> HEAVEN_PORTAL = STRUCTURE_TYPES.register("heaven_portal", () -> () -> HeavenPortalStructure.CODEC);

    public static void load() {
    }
}