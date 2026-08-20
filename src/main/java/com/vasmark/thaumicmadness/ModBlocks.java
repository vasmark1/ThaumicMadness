package com.vasmark.thaumicmadness;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ModBlocks {

    public static Block infusedDirt;

    public static void init() {
        if (!Config.enableInfusedDirt) return;

        infusedDirt = new BlockInfusedDirt();
        GameRegistry.registerBlock(infusedDirt, "infused_dirt");

        // Register Thaumcraft aspects for the block
        ThaumcraftApi.registerObjectTag(
            new ItemStack(infusedDirt),
            new AspectList().add(Aspect.EARTH, 2)
                .add(Aspect.MAGIC, 1));
    }
}
