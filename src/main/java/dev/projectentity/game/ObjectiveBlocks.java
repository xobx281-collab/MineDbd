package dev.projectentity.game;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** Visible, placeable objective props for the first in-world vertical slice. */
public final class ObjectiveBlocks {
    public static final Block GENERATOR = registerBlock("generator",
        new Block(BlockBehaviour.Properties.of().strength(3.0F).requiresCorrectToolForDrops()) {
            @Override
            public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
                super.onPlace(state, level, pos, oldState, movedByPiston);
                if (!level.isClientSide() && !state.is(oldState.getBlock())) {
                    ObjectiveProgress.registerGenerator(pos, MatchManager.current());
                }
            }
        });
    public static final Block EXIT_GATE = registerBlock("exit_gate",
        new Block(BlockBehaviour.Properties.of().strength(4.0F).requiresCorrectToolForDrops()) {
            @Override
            public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
                super.onPlace(state, level, pos, oldState, movedByPiston);
                if (!level.isClientSide() && !state.is(oldState.getBlock())) {
                    ObjectiveProgress.registerExitGate(pos);
                }
            }
        });
    public static final Block PALLET = registerBlock("pallet",
        new Block(BlockBehaviour.Properties.of().strength(2.0F).requiresCorrectToolForDrops()) {
            @Override
            public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
                super.onPlace(state, level, pos, oldState, movedByPiston);
                if (!level.isClientSide() && !state.is(oldState.getBlock())) ObjectiveProgress.registerPallet(pos);
            }
        });
    public static final Block DROPPED_PALLET = registerBlock("dropped_pallet",
        new Block(BlockBehaviour.Properties.of().strength(3.0F).requiresCorrectToolForDrops()) {
            @Override
            public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
                super.onPlace(state, level, pos, oldState, movedByPiston);
                if (!level.isClientSide() && !state.is(oldState.getBlock())) ObjectiveProgress.registerPallet(pos);
            }
        }, false);
    public static final Block TRAP = registerBlock("trap",
        new Block(BlockBehaviour.Properties.of().noCollission().strength(0.5F)) {
            @Override
            public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
                super.onPlace(state, level, pos, oldState, movedByPiston);
                if (!level.isClientSide() && !state.is(oldState.getBlock())) ObjectiveProgress.registerTrap(pos);
            }
        });

    private ObjectiveBlocks() {}

    private static Block registerBlock(String path, Block block) {
        return registerBlock(path, block, true);
    }

    private static Block registerBlock(String path, Block block, boolean registerItem) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("project_entity", path);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        if (registerItem) {
            Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, new Item.Properties()));
        }
        return block;
    }
}
