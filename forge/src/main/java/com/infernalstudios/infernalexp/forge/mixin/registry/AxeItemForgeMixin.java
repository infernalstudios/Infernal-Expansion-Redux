package com.infernalstudios.infernalexp.forge.mixin.registry;

import com.infernalstudios.infernalexp.registration.StrippableRegistry;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class AxeItemForgeMixin {

    @Inject(method = "getAxeStrippingState", at = @At("RETURN"), cancellable = true, remap = false)
    private static void infernalexp$getRegisteredStrippables(BlockState originalState, CallbackInfoReturnable<BlockState> cir) {
        if (cir.getReturnValue() != null) return;

        Block block = StrippableRegistry.get(originalState.getBlock());
        cir.setReturnValue(block != null ? block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, originalState.getValue(RotatedPillarBlock.AXIS)) : null);
    }
}
