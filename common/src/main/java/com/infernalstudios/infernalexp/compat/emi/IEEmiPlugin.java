package com.infernalstudios.infernalexp.compat.emi;

import com.infernalstudios.infernalexp.module.ModItems;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiAnvilRecipe;

@EmiEntrypoint
public class IEEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {

        registry.addRecipe(new EmiAnvilRecipe(
                EmiStack.of(ModItems.BLINDSIGHT_TONGUE_WHIP.get()),
                EmiStack.of(ModItems.BLINDSIGHT_TONGUE.get())
        ));
    }
}