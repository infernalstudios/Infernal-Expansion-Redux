package com.infernalstudios.infernalexp.registration.holders;

import com.infernalstudios.infernalexp.mixin.accessor.ButtonBlockAccessor;
import com.infernalstudios.infernalexp.mixin.accessor.IronBarsBlockAccessor;
import com.infernalstudios.infernalexp.mixin.accessor.PressurePlateBlockAccessor;
import com.infernalstudios.infernalexp.mixin.accessor.StairBlockAccessor;
import com.infernalstudios.infernalexp.registration.FlammabilityRegistry;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BlockDataHolder<T extends Block> {
    private static final Map<TagKey<Block>, List<BlockDataHolder<?>>> BLOCK_TAGS = new HashMap<>();
    private static final List<BlockDataHolder<?>> CUTOUT_BLOCKS = new ArrayList<>();

    private T cachedEntry;
    private final Supplier<T> entrySupplier;

    private ItemDataHolder<? extends Item> blockItem;
    private Model model;
    private String defaultTranslation;
    private final Map<Block, FlammabilityRegistry.Entry> FLAMMABILITIES = new HashMap<>();
    private Supplier<? extends Block> strippingResult;
    private int fuelDuration;
    private final Map<Model, BlockDataHolder<?>> BLOCKSETS = new HashMap<>();
    private Supplier<ItemLike> drop;
    private NumberProvider dropCount;
    private boolean isGlass;
    private BlockDataHolder<?> paneBlock;

    public BlockDataHolder(Supplier<T> entrySupplier) {
        this.entrySupplier = entrySupplier;
    }

    public static BlockDataHolder<? extends Block> of(Supplier<?> blockSupplier) {
        return new BlockDataHolder(blockSupplier);
    }

    /**
     * Retrieves the cached entry if it exists, otherwise calls the supplier to create a new entry.
     * @return The cached entry, or a new entry if the cached entry does not exist.
     */
    public T get() {
        if (this.cachedEntry != null) return cachedEntry;

        T entry = entrySupplier.get();
        this.cachedEntry = entry;

        return entry;
    }

    /**
     * Creates a default BlockItem for this block
     */
    public BlockDataHolder<? extends Block> withItem() {
        this.blockItem = ItemDataHolder.of(() -> new BlockItem(this.get(), new Item.Properties())).withModel(ModelTemplates.FLAT_ITEM);
        return this;
    }

    /**
     * Adds the ability to strip this block with an axe
     * @param stripResult the block to set it to when it gets stripped
     */
    public BlockDataHolder<?> withStripping(Block stripResult) {
        this.strippingResult = () -> stripResult;
        return this;
    }

    /**
     * Adds the ability to strip this block with an axe (Supplier version)
     * @param stripResult the supplier of the block to set it to when it gets stripped
     */
    public BlockDataHolder<?> withStripping(Supplier<? extends Block> stripResult) {
        this.strippingResult = stripResult;
        return this;
    }

    public boolean hasStrippingResult() {
        return this.strippingResult != null;
    }

    public Block getStrippingResult() {
        return this.strippingResult != null ? this.strippingResult.get() : null;
    }

    /**
     * Registers flammability with Vanilla Fire
     * @param flammabilityEntry an Entry of the ignite and spread chances
     */
    public BlockDataHolder<?> withFlammableDefault(FlammabilityRegistry.Entry flammabilityEntry) {
        return this.withFlammable(Blocks.FIRE, flammabilityEntry);
    }


    /**
     * Registers flammability with the supplied fire block
     * @param fireBlock must extend FireBlock
     * @param flammabilityEntry an Entry of the ignite and spread chances
     */
    public BlockDataHolder<?> withFlammable(Block fireBlock, FlammabilityRegistry.Entry flammabilityEntry) {
        this.FLAMMABILITIES.put(fireBlock, flammabilityEntry);
        return this;
    }

    public Map<Block, FlammabilityRegistry.Entry> getFlammabilities() {
        return this.FLAMMABILITIES;
    }

    /**
     * Registers the block item as a fuel source. Does nothing if the block has no item.
     * @param fuelDuration the length in ticks this fuel source burns
     */
    public BlockDataHolder<?> withFuel(int fuelDuration) {
        this.fuelDuration = fuelDuration;
        return this;
    }

    public boolean isFuel() {
        return this.fuelDuration > 0;
    }

    public int getFuelDuration() {
        return this.fuelDuration;
    }

    /**
     * Registers this block to the supplied tags
     * @param tags the tag keys to register the block to
     */
    @SafeVarargs
    public final BlockDataHolder<?> withTags(TagKey<Block>... tags) {
        for (TagKey<Block> tag : tags) {
            BLOCK_TAGS.putIfAbsent(tag, new ArrayList<>());
            BLOCK_TAGS.get(tag).add(this);
            for (BlockDataHolder<?> block : this.getBlocksets().values())
                BLOCK_TAGS.get(tag).add(block);
        }

        return this;
    }

    public static Map<TagKey<Block>, List<BlockDataHolder<?>>> getBlockTags() {
        return BLOCK_TAGS;
    }

    public boolean hasItem() {
        return this.blockItem != null;
    }

    public ItemDataHolder<?> getBlockItem() {
        return this.blockItem;
    }

    /**
     * The model type of this block for datagen
     * If any of the blocksets are added to this block, this value will be ignored and CUBE will be used
     */
    public BlockDataHolder<?> withModel(Model model) {
        this.model = model;
        return this;
    }

    public boolean hasModel() {
        return this.model != null;
    }

    public Model getModel() {
        return this.model;
    }

    /**
     * Sets the rendertype of this block to cutout, allowing for transparency
     */
    public BlockDataHolder<?> cutout() {
        CUTOUT_BLOCKS.add(this);
        return this;
    }

    public static List<BlockDataHolder<?>> getCutoutBlocks() {
        return CUTOUT_BLOCKS;
    }

    /**
     * Makes this a glass block with a glass pane
     */
    public BlockDataHolder<?> glass() {
        this.isGlass = true;
        this.paneBlock = BlockDataHolder.of(() -> IronBarsBlockAccessor.createIronBarsBlock(BlockBehaviour.Properties.copy(this.get()))).cutout().withItem();
        return this;
    }

    /**
     * Makes this a colored glass block with a glass pane
     * @param dye DyeColor to make the pane
     */
    public BlockDataHolder<?> glass(DyeColor dye) {
        this.isGlass = true;
        this.paneBlock = BlockDataHolder.of(() -> new StainedGlassPaneBlock(dye, BlockBehaviour.Properties.copy(this.get()))).cutout().withItem();
        return this;
    }

    public boolean isGlass() {
        return this.isGlass;
    }

    public BlockDataHolder<?> getPaneBlock() {
        return this.paneBlock;
    }

    /**
     * Sets the default EN_US translation for this block
     * @param translation the name for this block
     */
    public BlockDataHolder<?> withTranslation(String translation) {
        this.defaultTranslation = translation;
        return this;
    }

    public boolean hasTranslation() {
        return this.defaultTranslation != null;
    }

    public String getTranslation() {
        return this.defaultTranslation;
    }

    public Map<Model, BlockDataHolder<?>> getBlocksets() {
        return this.BLOCKSETS;
    }

    public Supplier<ItemLike> getDrop() {
        return this.drop;
    }

    public NumberProvider getDropCount() {
        return this.dropCount;
    }

    public BlockDataHolder<?> dropsSelf() {
        this.drop = this::get;
        this.dropCount = ConstantValue.exactly(1);
        return this;
    }

    public BlockDataHolder<?> dropsSelf(int count) {
        this.drop = this::get;
        this.dropCount = ConstantValue.exactly(count);
        return this;
    }

    public final BlockDataHolder<?> dropsOther(Supplier<ItemLike> drop) {
        this.drop = drop;
        this.dropCount = ConstantValue.exactly(1);
        return this;
    }

    public final BlockDataHolder<?> dropsOther(Supplier<ItemLike> drop, int count) {
        this.drop = drop;
        this.dropCount = ConstantValue.exactly(count);
        return this;
    }

    public final BlockDataHolder<?> dropsOther(Supplier<ItemLike> drop, NumberProvider count) {
        this.drop = drop;
        this.dropCount = count;
        return this;
    }

    public BlockDataHolder<?> dropsWithSilk() {
        this.drop = this::get;
        this.dropCount = null;
        return this;
    }

    public boolean hasDrop() {
        return this.drop != null;
    }

    public BlockDataHolder<?> withStairs() {
        BlockDataHolder<?> stairs = BlockDataHolder.of(() -> StairBlockAccessor.createStairBlock(this.get().defaultBlockState(), BlockBehaviour.Properties.copy(this.get())))
                .withModel(Model.STAIRS)
                .withItem()
                .withTags(BlockTags.STAIRS);
        this.BLOCKSETS.put(Model.STAIRS, stairs);
        return this;
    }

    public BlockDataHolder<?> getStairs() {
        return this.BLOCKSETS.get(Model.STAIRS);
    }

    public BlockDataHolder<?> withSlab() {
        BlockDataHolder<?> slab = BlockDataHolder.of(() -> new SlabBlock(BlockBehaviour.Properties.copy(this.get())))
                .withModel(Model.SLAB)
                .withItem()
                .withTags(BlockTags.SLABS);
        this.BLOCKSETS.put(Model.SLAB, slab);
        return this;
    }

    public BlockDataHolder<?> getSlab() {
        return this.BLOCKSETS.get(Model.SLAB);
    }

    public BlockDataHolder<?> withWall() {
        BlockDataHolder<?> wall = BlockDataHolder.of(() -> new WallBlock(BlockBehaviour.Properties.copy(this.get())))
                .withModel(Model.WALL)
                .withItem()
                .withTags(BlockTags.WALLS);
        this.BLOCKSETS.put(Model.WALL, wall);
        return this;
    }

    public BlockDataHolder<?> getWall() {
        return this.BLOCKSETS.get(Model.WALL);
    }

    public BlockDataHolder<?> withButton(BlockSetType type, int ticksPressed, boolean arrowCanPress) {
        BlockDataHolder<?> button = BlockDataHolder.of(() -> ButtonBlockAccessor.createButtonBlock(BlockBehaviour.Properties.copy(this.get()), type, ticksPressed, arrowCanPress))
                .withModel(Model.BUTTON)
                .withItem()
                .withTags(BlockTags.BUTTONS);
        this.BLOCKSETS.put(Model.BUTTON, button);
        return this;
    }

    public BlockDataHolder<?> getButton() {
        return this.BLOCKSETS.get(Model.BUTTON);
    }

    public BlockDataHolder<?> withPressurePlate(PressurePlateBlock.Sensitivity sensitivity, BlockSetType type) {
        BlockDataHolder<?> pressurePlate = BlockDataHolder.of(() -> PressurePlateBlockAccessor.createPressurePlateBlock(sensitivity, BlockBehaviour.Properties.copy(this.get()), type))
                .withModel(Model.PRESSURE_PLATE)
                .withItem()
                .withTags(BlockTags.PRESSURE_PLATES);
        this.BLOCKSETS.put(Model.PRESSURE_PLATE, pressurePlate);
        return this;
    }

    public BlockDataHolder<?> getPressurePlate() {
        return this.BLOCKSETS.get(Model.PRESSURE_PLATE);
    }

    public BlockDataHolder<?> withFence() {
        BlockDataHolder<?> fence = BlockDataHolder.of(() -> new FenceBlock(BlockBehaviour.Properties.copy(this.get())))
                .withModel(Model.FENCE)
                .withItem()
                .withTags(BlockTags.FENCES);
        this.BLOCKSETS.put(Model.FENCE, fence);
        return this;
    }

    public BlockDataHolder<?> getFence() {
        return this.BLOCKSETS.get(Model.FENCE);
    }

    public BlockDataHolder<?> withFenceGate(WoodType woodType) {
        BlockDataHolder<?> fenceGate = BlockDataHolder.of(() -> new FenceGateBlock(BlockBehaviour.Properties.copy(this.get()), woodType))
                .withModel(Model.FENCE_GATE)
                .withItem()
                .withTags(BlockTags.FENCE_GATES);
        this.BLOCKSETS.put(Model.FENCE_GATE, fenceGate);
        return this;
    }

    public BlockDataHolder<?> getFenceGate() {
        return this.BLOCKSETS.get(Model.FENCE_GATE);
    }

    public BlockDataHolder<? extends Block> withCustomItem(java.util.function.Function<T, BlockItem> itemFactory) {
        this.blockItem = ItemDataHolder.of(() -> itemFactory.apply(this.get()))
                .withModel(ModelTemplates.FLAT_ITEM);
        return this;
    }

    public enum Model {
        CUBE("", ""),
        PILLAR("", ""),
        WOOD("", ""),
        ROTATABLE("", ""),
        CROSS("", ""),
        DOOR("", ""),
        TRAPDOOR("", ""),
        STAIRS("stairs", "Stairs"),
        SLAB("slab", "Slab"),
        WALL("wall", "Wall"),
        BUTTON("button", "Button"),
        PRESSURE_PLATE("pressure_plate", "Pressure Plate"),
        FENCE("fence", "Fence"),
        FENCE_GATE("fence_gate", "Fence Gate");

        private final String suffix;
        private final String lang;

        Model(String suffix, String lang) {
            this.suffix = suffix;
            this.lang = lang;
        }

        public String suffix() {
            return this.suffix;
        }

        public String getLang() {
            return this.lang;
        }
    }
}