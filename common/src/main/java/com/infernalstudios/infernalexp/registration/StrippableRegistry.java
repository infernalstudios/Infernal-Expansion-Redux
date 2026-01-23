package com.infernalstudios.infernalexp.registration;

import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StrippableRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(StrippableRegistry.class);
    private static final Map<Block, Block> REGISTRY = new HashMap<>();

    public static void register(Block input, Block stripped) {
        Objects.requireNonNull(input, "input block cannot be null");
        Objects.requireNonNull(stripped, "stripped block cannot be null");

        Block prev = REGISTRY.put(input, stripped);

        if (prev != null) {
            LOGGER.debug("Replaced block {} stripping to {} with {}", input, prev, stripped);
        }
    }

    public static Block get(Block input) {
        return REGISTRY.get(input);
    }
}