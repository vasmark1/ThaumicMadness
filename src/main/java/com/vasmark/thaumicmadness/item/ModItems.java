package com.vasmark.thaumicmadness.item;

import net.minecraft.item.Item;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModItems {

    public static Item itemThaumonomiconAtlas;

    public static void init() {
        itemThaumonomiconAtlas = new ItemThaumonomiconAtlas();
        GameRegistry.registerItem(itemThaumonomiconAtlas, "thaumonomicon_atlas");
    }
}
