package com.infernalstudios.infernalexp.forge.potion;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import org.jetbrains.annotations.NotNull;

public class PotionRecipe implements IBrewingRecipe {
    public Potion input;
    public Item ingredient;
    public Potion output;

    public PotionRecipe(Potion input, Item ingredient, Potion output) {
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean isInput(ItemStack input) {
        return PotionUtils.getPotion(input) == this.input;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ingredient.is(this.ingredient);
    }

    @Override
    public @NotNull ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (this.isInput(input) && this.isIngredient(ingredient)) {
            return PotionUtils.setPotion(input.copy(), this.output);
        }
        return ItemStack.EMPTY;
    }
}
