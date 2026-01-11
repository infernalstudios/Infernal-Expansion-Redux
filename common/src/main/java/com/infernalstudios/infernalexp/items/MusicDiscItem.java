package com.infernalstudios.infernalexp.items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;

import java.util.function.Supplier;

public class MusicDiscItem extends RecordItem {
    public MusicDiscItem(int comparatorValue, Supplier<SoundEvent> soundSupplier, Item.Properties builder, int lengthInSeconds) {
        super(comparatorValue, soundSupplier.get(), builder, lengthInSeconds);
    }

    public MusicDiscItem(int comparatorValue, SoundEvent sound, Item.Properties builder, int lengthInSeconds) {
        super(comparatorValue, sound, builder, lengthInSeconds);
    }
}