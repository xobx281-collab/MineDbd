package dev.projectentity.game;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.core.Registry;

/** Visible, placeable objective props for the first in-world vertical slice. */
public final class ObjectiveBlocks {
    public static final Block GENERATOR = registerBlock("generator",
        new Block(BlockBehaviour.Properties.of().strength(3.0F).requiresCorrectToolForDrops()));
    public static final Block EXIT_GATE = registerBlock("exit_gate",
        new Block(BlockBehaviour.Properties.of().strength(4.0F).requiresCorrectToolForDrops()));

    private ObjectiveBlocks() {}

    private static Block registerBlock(String path, Block block) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("project_entity", path);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        Registry.register(BuiltInRegistries.ITEM, id,
            new BlockItem(block, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
        return block;
    }
}
