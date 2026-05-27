package com.infernalstudios.infernalexp.world.surface;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.mixin.accessor.SurfaceRulesContextAccessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.NotNull;

public record ChanceConditionSource(String name, float percentageChance) implements SurfaceRules.ConditionSource {

    public static final MapCodec<ChanceConditionSource> MAP_CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(ChanceConditionSource::name),
                    Codec.FLOAT.fieldOf("percentage_chance").forGetter(ChanceConditionSource::percentageChance)
            ).apply(instance, ChanceConditionSource::new));

    public static final KeyDispatchDataCodec<ChanceConditionSource> CODEC = KeyDispatchDataCodec.of(MAP_CODEC);

    @Override
    public @NotNull KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.Condition apply(SurfaceRules.Context context) {
        class ChanceCondition extends SurfaceRules.LazyYCondition {
            private final PositionalRandomFactory randomFactory;

            protected ChanceCondition(SurfaceRules.Context context) {
                super(context);
                this.randomFactory = ((SurfaceRulesContextAccessor) (Object) context).infernalexp$getRandomState()
                        .getOrCreateRandomFactory(ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, name()));
            }

            @Override
            protected boolean compute() {
                SurfaceRulesContextAccessor contextAccessor = (SurfaceRulesContextAccessor) (Object) context;
                int x = contextAccessor.infernalexp$getBlockX();
                int y = contextAccessor.infernalexp$getBlockY();
                int z = contextAccessor.infernalexp$getBlockZ();

                RandomSource randomSource = this.randomFactory.at(x, y, z);
                return randomSource.nextFloat() < percentageChance();
            }
        }

        return new ChanceCondition(context);
    }
}