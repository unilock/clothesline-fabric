package com.jamieswhiteshirt.clothesline.common.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ClotheslineItemGroups {
    public static final ItemGroup ITEMS = FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.clothesline.items"))
            .icon(() -> new ItemStack(ClotheslineItems.CLOTHESLINE))
            .entries((displayContext, entries) -> {
                entries.add(ClotheslineItems.CLOTHESLINE_ANCHOR);
                entries.add(ClotheslineItems.CLOTHESLINE);
                entries.add(ClotheslineItems.CRANK);
                entries.add(ClotheslineItems.SPINNER);
            })
            .build();
    
    public static void init() {
        Registry.register(Registries.ITEM_GROUP, new Identifier("clothesline", "items"), ITEMS);
    }
}
