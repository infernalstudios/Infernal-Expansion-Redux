package com.infernalstudios.infernalexp.fabric.datagen.providers;

import com.infernalstudios.infernalexp.config.IEConfig;
import com.infernalstudios.infernalexp.module.*;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import com.infernalstudios.infernalexp.registration.holders.EntityTypeDataHolder;
import com.infernalstudios.infernalexp.registration.holders.ItemDataHolder;
import com.infernalstudios.infernalexp.registration.holders.MobEffectDataHolder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

import static com.infernalstudios.infernalexp.IEConstants.MOD_ID;

public class IELangProvider extends FabricLanguageProvider {
    public IELangProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        // Put manually added entries here
        builder.add(ModCreativeTabs.INFERNAL_EXPANSION_TAB.getResourceKey(), "Infernal Expansion");
        generateConfigTranslations(builder);

        // Tag Translations
        builder.add("tag.item.infernalexp.voline_food", "Voline Food");
        builder.add("tag.item.infernalexp.glowsquito_temptation_items", "Glowsquito Temptation Items");

        // Subtitles
        builder.add("subtitles.infernalexp.entity.voline.ambient", "Voline grunts");
        builder.add("subtitles.infernalexp.entity.voline.hurt", "Voline hurts");

        builder.add("subtitles.infernalexp.entity.glowsquito.hurt", "Glowsquito hurts");
        builder.add("subtitles.infernalexp.entity.glowsquito.death", "Glowsquito dies");
        builder.add("subtitles.infernalexp.entity.glowsquito.loop", "Glowsquito buzzes");
        builder.add("subtitles.infernalexp.entity.glowsquito.slurp", "Glowsquito slurps");

        builder.add("subtitles.infernalexp.entity.glowsilk_moth.ambient", "Glowsilk Moth flutters");
        builder.add("subtitles.infernalexp.entity.glowsilk_moth.hurt", "Glowsilk Moth hurts");
        builder.add("subtitles.infernalexp.entity.glowsilk_moth.death", "Glowsilk Moth dies");

        builder.add("subtitles.infernalexp.entity.blindsight.ambient", "Blindsight croaks");
        builder.add("subtitles.infernalexp.entity.blindsight.hurt", "Blindsight hurts");
        builder.add("subtitles.infernalexp.entity.blindsight.death", "Blindsight dies");
        builder.add("subtitles.infernalexp.entity.blindsight.leap", "Blindsight leaps");
        builder.add("subtitles.infernalexp.entity.blindsight.alert", "Blindsight roars");
        builder.add("subtitles.infernalexp.entity.blindsight.lick", "Blindsight licks");

        builder.add("subtitles.infernalexp.item.whip.whip_crack", "Whip cracks");

        // Tooltips
        builder.add("item.infernalexp.blindsight_tongue_whip.damage", " 4 Attack Damage");

        // Commands
        builder.add("commands.infernalexp.ntp.not_found", "The Nether dimension could not be found!");
        builder.add("commands.infernalexp.ntp.already_in_nether", "You are already in the Nether!");
        builder.add("commands.infernalexp.ntp.failed", "No safe spot found.");
        builder.add("commands.infernalexp.ntp.success", "Teleported safely to the Nether!");

        // Advancements
        builder.add("advancements.infernalexp.freeze_punk.title", "Freeze, Punk!");
        builder.add("advancements.infernalexp.freeze_punk.desc", "Freeze a sleeping Voline with a snowball");

        builder.add("advancements.infernalexp.magma_mia.title", "Magma Mia!");
        builder.add("advancements.infernalexp.magma_mia.desc", "Feed a baby Voline enough Magma Cream to make it reach adulthood");

        builder.add("advancements.infernalexp.hot_pocket.title", "Hot Pocket");
        builder.add("advancements.infernalexp.hot_pocket.desc", "Catch a baby Voline in a Lava Bucket");

        builder.add("advancements.infernalexp.deer_in_headlights.title", "Deer in Headlights");
        builder.add("advancements.infernalexp.deer_in_headlights.desc", "Kill a Blindsight while afflicted with Luminous");

        builder.add("advancements.infernalexp.pressure_cooker.title", "Pressure Cooker");
        builder.add("advancements.infernalexp.pressure_cooker.desc", "Activate a Volatile Geyser using Redstone");

        // Paintings
        builder.add("painting.infernalexp.the_fallen_ones.author", "LazTheArtist");
        builder.add("painting.infernalexp.the_fallen_ones.title", "The Fallen Ones");
        builder.add("painting.infernalexp.glowstone_canyon.author", "Nekomaster");
        builder.add("painting.infernalexp.glowstone_canyon.title", "Glowstone Canyon");

        // Enchantments
        builder.add("enchantment.infernalexp.disarming", "Disarming");
        builder.add("enchantment.infernalexp.leaping", "Leaping");
        builder.add("enchantment.infernalexp.illuminating", "Illuminating");
        builder.add("enchantment.infernalexp.lashing", "Lashing");

        builder.add("enchantment.infernalexp.disarming.desc", "Chance to cause mobs to drop their held item, or disable a player's held item for a short duration.");
        builder.add("enchantment.infernalexp.leaping.desc", "Propels the wielder forward when used in the air, or causes them to rebound off the target upon a successful hit.");
        builder.add("enchantment.infernalexp.illuminating.desc", "Applies the Luminous effect to the target. Incompatible with Fire Aspect.");
        builder.add("enchantment.infernalexp.lashing.desc", "Increases the damage dealt when attacking with this weapon.");

        // Autumnity Compat
        builder.add("block.infernalexp.glowlight_jack_o_lantern",
                "Glowlight Jack o'Lantern");
        builder.add("block.infernalexp.large_glowlight_jack_o_lantern_slice",
                "Large Glowlight Jack o'Lantern Slice");

        // Geyser Tooltips
        builder.add("text.autoconfig.infernalexp.option.common.geyser.geyserSteamHeight.@Tooltip",
                "Determines the maximum height of the steam particles produced by the geyser.");

        // Voline Tooltips
        builder.add("text.autoconfig.infernalexp.option.common.voline.volineTurnIntoGeyser.@Tooltip",
                "If enabled, sleeping Volines will transform into Volatile Geysers when hit with a snowball.");

        builder.add("text.autoconfig.infernalexp.option.common.voline.volineSleepWhenFed.@Tooltip",
                "If enabled, Volines will fall asleep after eating Magma Cream.");

        builder.add("text.autoconfig.infernalexp.option.common.voline.volineGetBig.@Tooltip",
                "If enabled, Volines will grow in size when they eat Magma Cream.");

        builder.add("text.autoconfig.infernalexp.option.common.voline.volineMagmaCreamAmount.@Tooltip",
                "Determines the amount of Magma Cream a Voline needs to eat to grow.");

        // Mob Interactions Tooltips
        builder.add("text.autoconfig.infernalexp.option.common.mobInteractions.glowsquitoBlockSucking.@Tooltip",
                "Determines if Glowsquitos should drink from Glowstone and Shroomlight blocks.");

        builder.add("text.autoconfig.infernalexp.option.common.mobInteractions.blindsightExtinguishFire.@Tooltip",
                "If enabled, Blindsights will stomp out fire sources.");

        builder.add("text.autoconfig.infernalexp.option.common.mobInteractions.blindsightEatBabyMobs.@Tooltip",
                "If enabled, Blindsights will target and eat baby mobs.");

        // Miscellaneous Tooltips
        builder.add("text.autoconfig.infernalexp.option.common.miscellaneous.luminousFungusActivateDistance.@Tooltip",
                "Determines the radius in blocks around Luminous Fungus that will cause them to light up.");

        builder.add("text.autoconfig.infernalexp.option.common.miscellaneous.glowsilkBowSpeed.@Tooltip",
                "Determines the speed at which arrows are fired from the Glowsilk Bow.");

        // World Generation Tooltips
        builder.add("text.autoconfig.infernalexp.option.common.worldGeneration.enablePlantedQuartz.@Tooltip",
                "Determines whether Planted Quartz will generate in the world and be placeable.");

        builder.add("text.autoconfig.infernalexp.option.common.worldGeneration.enableBuriedBone.@Tooltip",
                "Determines whether Buried Bone will generate in the world and be placeable.");

        // Biome Translations
        builder.add("biome.infernalexp.glowstone_canyon", "Glowstone Canyon");

        // Music
        builder.add("item.infernalexp.music_disc_flush.desc", "LudoCrypt - Flush");

        // This handles all supplied block and item entries automatically
        for (BlockDataHolder<?> blockDataHolder : ModBlocks.getBlockRegistry().values()) {
            if (blockDataHolder.hasTranslation()) {
                builder.add(blockDataHolder.get(), blockDataHolder.getTranslation());
            }

            if (blockDataHolder.isGlass()) {
                builder.add(blockDataHolder.getPaneBlock().get(), blockDataHolder.getTranslation() + " Pane");
            }

            for (Map.Entry<BlockDataHolder.Model, BlockDataHolder<?>> blocksetEntry : blockDataHolder.getBlocksets().entrySet()) {
                if (blockDataHolder.hasTranslation()) {
                    String translation = blockDataHolder.getTranslation();
                    if (translation.endsWith(" Bricks")) {
                        translation = translation.substring(0, translation.length() - 1);
                    } else if (translation.endsWith(" Planks")) {
                        translation = translation.substring(0, translation.length() - 7);
                    }
                    builder.add(blocksetEntry.getValue().get(), translation + " " + blocksetEntry.getKey().getLang());
                }
            }
        }

        for (ItemDataHolder<?> itemDataHolder : ModItems.getItemRegistry().values()) {
            if (itemDataHolder.hasTranslation()) {
                builder.add(itemDataHolder.get(), itemDataHolder.getTranslation());
            }
        }

        for (EntityTypeDataHolder<?> entityTypeDataHolder : ModEntityTypes.getEntityTypeRegistry().values()) {
            if (entityTypeDataHolder.hasTranslation()) {
                builder.add(entityTypeDataHolder.get(), entityTypeDataHolder.getTranslation());
            }
        }

        for (Map.Entry<ResourceLocation, MobEffectDataHolder<?>> entry : ModEffects.getEffectRegistry().entrySet()) {
            if (entry.getValue().hasTranslation()) {
                builder.add(entry.getValue().get(), entry.getValue().getTranslation());

                if (entry.getValue().hasPotion()) {
                    String id = entry.getKey().getPath();

                    builder.add("item.minecraft.potion.effect." + id, "Potion of " + entry.getValue().getTranslation());
                    builder.add("item.minecraft.splash_potion.effect." + id, "Splash Potion of " + entry.getValue().getTranslation());
                    builder.add("item.minecraft.lingering_potion.effect." + id, "Lingering Potion of " + entry.getValue().getTranslation());
                    builder.add("item.minecraft.tipped_arrow.effect." + id, "Arrow of " + entry.getValue().getTranslation());
                }
            }
        }
    }

    private void generateConfigTranslations(TranslationBuilder builder) {
        String baseKey = "text.autoconfig." + MOD_ID;

        builder.add(baseKey + ".title", "Infernal Expansion Config");

        for (Field field : IEConfig.class.getDeclaredFields()) {
            if (isValidConfigField(field)) {
                String name = field.getName();
                String categoryKey = baseKey + ".category." + name;
                builder.add(categoryKey, toHumanReadable(name) + " Settings");

                processConfigNested(builder, field.getType(), baseKey + ".option." + name);
            }
        }
    }

    private void processConfigNested(TranslationBuilder builder, Class<?> clazz, String parentKey) {
        for (Field field : clazz.getDeclaredFields()) {
            if (isValidConfigField(field)) {
                String name = field.getName();
                String key = parentKey + "." + name;
                String humanReadable = toHumanReadable(name);

                boolean isNestedCategory = field.getType().getName().contains("IEConfig$");

                if (isNestedCategory) {
                    builder.add(key, humanReadable + " Content");
                    processConfigNested(builder, field.getType(), key);
                } else {
                    builder.add(key, humanReadable);
                }
            }
        }
    }

    private boolean isValidConfigField(Field field) {
        return !Modifier.isStatic(field.getModifiers()) && !field.isSynthetic();
    }

    private String toHumanReadable(String camelCase) {
        String[] words = StringUtils.splitByCharacterTypeCamelCase(camelCase);
        return Arrays.stream(words)
                .map(StringUtils::capitalize)
                .reduce((a, b) -> a + " " + b)
                .orElse(camelCase);
    }
}