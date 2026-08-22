package com.vasmark.thaumicmadness;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.vasmark.thaumicmadness.compact.infusion.BlockCompactInfusionMatrix;
import com.vasmark.thaumicmadness.compact.infusion.TileCompactInfusionMatrix;

import cpw.mods.fml.common.registry.GameRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ModBlocks {

    public static Block infusedDirt;
    public static Block compactInfusionMatrix;
    public static Block compactInfernalFurnace;

    public static void init() {
        if (Config.enableInfusedDirt) {
            infusedDirt = new BlockInfusedDirt();
            GameRegistry.registerBlock(infusedDirt, "infused_dirt");

            // Register Thaumcraft aspects for the block
            ThaumcraftApi.registerObjectTag(
                new ItemStack(infusedDirt),
                new AspectList().add(Aspect.EARTH, 2)
                    .add(Aspect.MAGIC, 1));
        }

        compactInfusionMatrix = new BlockCompactInfusionMatrix();
        GameRegistry.registerBlock(compactInfusionMatrix, "compact_infusion_matrix");
        GameRegistry.registerTileEntity(TileCompactInfusionMatrix.class, "thaumicmadness:compact_infusion_matrix");

        ThaumcraftApi.registerObjectTag(
            new ItemStack(compactInfusionMatrix),
            new AspectList().add(Aspect.MAGIC, 8)
                .add(Aspect.CRAFT, 8)
                .add(Aspect.ORDER, 4)
                .add(Aspect.ELDRITCH, 4));

        compactInfernalFurnace = new com.vasmark.thaumicmadness.compact.furnace.BlockCompactInfernalFurnace();
        GameRegistry.registerBlock(compactInfernalFurnace, "compact_infernal_furnace");
        GameRegistry.registerTileEntity(
            com.vasmark.thaumicmadness.compact.furnace.TileCompactInfernalFurnace.class,
            "thaumicmadness:compact_infernal_furnace");

        ThaumcraftApi.registerObjectTag(
            new ItemStack(compactInfernalFurnace),
            new AspectList().add(Aspect.FIRE, 8)
                .add(Aspect.METAL, 8)
                .add(Aspect.ENTROPY, 4)
                .add(Aspect.MIND, 4)
                .add(Aspect.ELDRITCH, 4));
    }
}
