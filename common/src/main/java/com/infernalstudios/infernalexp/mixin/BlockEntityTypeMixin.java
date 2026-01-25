package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.module.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    public void infernalexp$isValid(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == BlockEntityType.CAMPFIRE && state.is(ModBlocks.GLOWLIGHT_CAMPFIRE.get())) {
            cir.setReturnValue(true);
        }

        if ((Object) this == BlockEntityType.SIGN && (state.is(ModBlocks.LUMINOUS_SIGN.get()) || state.is(ModBlocks.LUMINOUS_WALL_SIGN.get()))) {
            cir.setReturnValue(true);
        }

        if ((Object) this == BlockEntityType.HANGING_SIGN && (state.is(ModBlocks.LUMINOUS_HANGING_SIGN.get()) || state.is(ModBlocks.LUMINOUS_WALL_HANGING_SIGN.get()))) {
            cir.setReturnValue(true);
        }
    }
}