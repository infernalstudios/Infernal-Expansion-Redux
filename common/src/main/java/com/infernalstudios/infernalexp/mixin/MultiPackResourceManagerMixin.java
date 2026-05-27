package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.resources.config.ConfiguredData;
import com.infernalstudios.infernalexp.resources.config.ConfiguredResources;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(MultiPackResourceManager.class)
public class MultiPackResourceManagerMixin {
    @Unique
    private static Resource infernalexp$readAndApply(Optional<Resource> resource, ConfiguredData data) {
        IECommon.log("Applying configured data: " + data.target, 0);

        if (resource.isEmpty()) {
            String result = data.apply(null);
            return new Resource(ConfiguredResources.INSTANCE,
                    () -> new CharSequenceInputStream(result, StandardCharsets.UTF_8));
        }

        try (InputStream stream = resource.get().open()) {
            String originalText = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            String result = data.apply(originalText);

            return new Resource(ConfiguredResources.INSTANCE,
                    () -> new CharSequenceInputStream(result, StandardCharsets.UTF_8));

        } catch (IOException e) {
            e.printStackTrace();
            return resource.get();
        }
    }

    @Unique
    private static Resource infernalexp$readAndApply(Resource resource, ConfiguredData data) {
        if (resource.source() instanceof ConfiguredResources) return resource;
        return infernalexp$readAndApply(Optional.of(resource), data);
    }

    @ModifyReturnValue(method = "getResource", at = @At("RETURN"))
    public Optional<Resource> getConfiguredResource(Optional<Resource> original, ResourceLocation id) {
        ConfiguredData data = ConfiguredData.get(id);
        if (data == null || !data.enabled.get() || (original.isPresent() && original.get().source() instanceof ConfiguredResources))
            return original;

        return Optional.of(infernalexp$readAndApply(original, data));
    }

    @ModifyReturnValue(method = "getResourceStack", at = @At("RETURN"))
    public List<Resource> getAllConfiguredResource(List<Resource> original, ResourceLocation id) {
        ConfiguredData data = ConfiguredData.get(id);
        if (data == null || !data.enabled.get()) return original;

        return original.stream()
                .map(resource -> infernalexp$readAndApply(resource, data)).toList();
    }

    @ModifyReturnValue(method = "listResources", at = @At("RETURN"))
    public Map<ResourceLocation, Resource> findConfiguredResources(Map<ResourceLocation, Resource> original,
                                                                   String startingPath, Predicate<ResourceLocation> allowedPathPredicate) {

        for (ConfiguredData data : ConfiguredData.INSTANCES) {
            if (data.enabled.get() && data.target.getPath().startsWith(startingPath + "/") && allowedPathPredicate.test(data.target)) {
                if (!original.containsKey(data.target)) {
                    original.put(data.target, infernalexp$readAndApply(Optional.empty(), data));
                }
            }
        }

        List<ResourceLocation> ids = original.keySet().stream().toList();
        for (ResourceLocation id : ids) {
            ConfiguredData data = ConfiguredData.get(id);
            if (data == null || !data.enabled.get()) continue;

            original.replace(id, infernalexp$readAndApply(original.get(id), data));
        }
        return original;
    }

    @ModifyReturnValue(method = "listResourceStacks", at = @At("RETURN"))
    public Map<ResourceLocation, List<Resource>> findAllConfiguredResources(Map<ResourceLocation, List<Resource>> original,
                                                                            String startingPath, Predicate<ResourceLocation> allowedPathPredicate) {

        for (ConfiguredData data : ConfiguredData.INSTANCES) {
            if (data.enabled.get() && data.target.getPath().startsWith(startingPath) && allowedPathPredicate.test(data.target)) {
                if (!original.containsKey(data.target)) {
                    original.put(data.target, List.of(infernalexp$readAndApply(Optional.empty(), data)));
                }
            }
        }

        List<ResourceLocation> ids = original.keySet().stream().toList();
        for (ResourceLocation id : ids) {
            ConfiguredData data = ConfiguredData.get(id);
            if (data == null || !data.enabled.get()) continue;

            original.replace(id, original.get(id).stream()
                    .map(resource -> infernalexp$readAndApply(resource, data)).toList());
        }
        return original;
    }
}