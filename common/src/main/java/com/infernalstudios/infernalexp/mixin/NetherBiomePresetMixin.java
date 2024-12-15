package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.module.ModBiomes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

@Mixin(targets = "net/minecraft/world/level/biome/MultiNoiseBiomeSourceParameterList$Preset$1")
public class NetherBiomePresetMixin {
    @Inject(method = "apply", at = @At("RETURN"), cancellable = true)
    public <T> void apply(Function<ResourceKey<Biome>, T> function, CallbackInfoReturnable<Climate.ParameterList<T>> cir) {
        ArrayList<Pair<Climate.ParameterPoint, T>> entryList = new ArrayList<>(cir.getReturnValue().values());

        // Add Nether biome entries here
        entryList.add(Pair.of(Climate.parameters(-0.3F, -0.3F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1F), function.apply(ModBiomes.GLOWSTONE_CANYON)));

        cir.setReturnValue(new Climate.ParameterList<>(Collections.unmodifiableList(entryList)));
    }
}
