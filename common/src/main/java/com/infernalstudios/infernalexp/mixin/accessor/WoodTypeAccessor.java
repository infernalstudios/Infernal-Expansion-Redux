package com.infernalstudios.infernalexp.mixin.accessor;

import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WoodType.class)
public interface WoodTypeAccessor {

    @Invoker("register")
    static WoodType infernalexp$register(WoodType woodType) {
        throw new UnsupportedOperationException();
    }
}