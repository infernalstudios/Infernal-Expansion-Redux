package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.resources.config.ConfiguredData;
import com.infernalstudios.infernalexp.resources.config.ConfiguredDataResourcePack;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(MultiPackResourceManager.class)
public class MultiPackResourceManagerMixin {
    @Unique
    private static Resource readAndApply(Optional<Resource> resource, ConfiguredData data) {
        IECommon.log("Applying configured data: " + data.target, 0);

        String result = "";
        if (resource.isEmpty())
            result = data.apply(null);
        else {
            try {
                result = data.apply(new String(resource.get().open().readAllBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String finalResult = result;
        return new Resource(ConfiguredDataResourcePack.INSTANCE,
                () -> new CharSequenceInputStream(finalResult, Charset.defaultCharset()));
    }

    @Unique
    private static Resource readAndApply(Resource resource, ConfiguredData data) {
        if (resource.source() instanceof ConfiguredDataResourcePack) return resource;
        return readAndApply(Optional.of(resource), data);
    }

    // thank you remapping, very cool
    @ModifyReturnValue(method = {"getResource", "method_14486", "m_213713_"}, remap = false, at = @At("RETURN"))
    public Optional<Resource> getConfiguredResource(Optional<Resource> original, ResourceLocation id) {
        ConfiguredData data = ConfiguredData.get(id);
        if (data == null || !data.enabled.get() || (original.isPresent() && original.get().source() instanceof ConfiguredDataResourcePack))
            return original;

        return Optional.of(readAndApply(original, data));
    }

    @ModifyReturnValue(method = "getResourceStack", at = @At("RETURN"))
    public List<Resource> getAllConfiguredResource(List<Resource> original, ResourceLocation id) {
        ConfiguredData data = ConfiguredData.get(id);
        if (data == null || !data.enabled.get()) return original;

        return original.stream()
                .map(resource -> readAndApply(resource, data)).toList();
    }

    @ModifyReturnValue(method = "listResources", at = @At("RETURN"))
    public Map<ResourceLocation, Resource> findConfiguredResources(Map<ResourceLocation, Resource> original,
                                                                   String startingPath, Predicate<ResourceLocation> allowedPathPredicate) {

        for (ConfiguredData data : ConfiguredData.INSTANCES) {
            if (data.enabled.get() && data.target.getPath().startsWith(startingPath + "/") && allowedPathPredicate.test(data.target)) {
                if (!original.containsKey(data.target)) {
                    original.put(data.target, readAndApply(Optional.empty(), data));
                }
            }
        }

        List<ResourceLocation> ids = original.keySet().stream().toList();
        for (ResourceLocation id : ids) {
            ConfiguredData data = ConfiguredData.get(id);
            if (data == null || !data.enabled.get()) continue;

            original.replace(id, readAndApply(original.get(id), data));
        }
        return original;
    }

    @ModifyReturnValue(method = "listResourceStacks", at = @At("RETURN"))
    public Map<ResourceLocation, List<Resource>> findAllConfiguredResources(Map<ResourceLocation, List<Resource>> original,
                                                                            String startingPath, Predicate<ResourceLocation> allowedPathPredicate) {

        for (ConfiguredData data : ConfiguredData.INSTANCES) {
            if (data.enabled.get() && data.target.getPath().startsWith(startingPath) && allowedPathPredicate.test(data.target)) {
                if (!original.containsKey(data.target)) {
                    original.put(data.target, List.of(readAndApply(Optional.empty(), data)));
                }
            }
        }

        List<ResourceLocation> ids = original.keySet().stream().toList();
        for (ResourceLocation id : ids) {
            ConfiguredData data = ConfiguredData.get(id);
            if (data == null || !data.enabled.get()) continue;

            original.replace(id, original.get(id).stream()
                    .map(resource -> readAndApply(resource, data)).toList());
        }
        return original;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void reloadConfigs(PackType type, List<PackResources> packs, CallbackInfo ci) {
        //ReloadListener.INSTANCE.preload((ResourceManager) (Object) this);
    }
}