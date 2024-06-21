package com.jamieswhiteshirt.clothesline.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ClotheslineBlocks {
    public static final Block CLOTHESLINE_ANCHOR = register("clothesline_anchor", new ClotheslineAnchorBlock(Block.Settings.copy(Blocks.LEVER)));

    private static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier("clothesline", id), block);
    }

    public static void init() { }
}
