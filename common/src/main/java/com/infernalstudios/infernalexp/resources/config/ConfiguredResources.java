package com.infernalstudios.infernalexp.resources.config;

import com.infernalstudios.infernalexp.IEConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class ConfiguredResources implements PackResources {
    public static final ConfiguredResources INSTANCE = new ConfiguredResources();

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String @NotNull ... segments) {
        return null;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(@NotNull PackType type, @NotNull ResourceLocation id) {
        return null;
    }

    @Override
    public void listResources(@NotNull PackType type, @NotNull String namespace, @NotNull String prefix, @NotNull ResourceOutput consumer) {

    }

    @Override
    public @NotNull Set<String> getNamespaces(@NotNull PackType type) {
        return Set.of();
    }

    @Override
    public @Nullable <T> T getMetadataSection(@NotNull MetadataSectionSerializer<T> metaReader) throws IOException {
        return null;
    }

    @Override
    public @NotNull String packId() {
        return IEConstants.MOD_ID + "_configured_data";
    }

    @Override
    public void close() {

    }
}
