package com.vasmark.thaumicmadness.compact.furnace;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.vasmark.thaumicmadness.ModBlocks;
import com.vasmark.thaumicmadness.item.ModItems;

import cpw.mods.fml.common.registry.GameRegistry;
import thaumcraft.api.ItemApi;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.common.config.ConfigBlocks;

public class CompactFurnaceRecipes {

    public static InfusionRecipe recipeCompactInfernalFurnace;
    public static IArcaneRecipe recipeSilverwoodFilter;

    public static void init() {
        // 1. Register aspect tags for filters
        ModItems.registerAspects();

        // 2. Silverwood Planks (blockWoodenDevice:7)
        ItemStack silverwoodPlank = ItemApi.getBlock("blockWoodenDevice", 7);
        if (silverwoodPlank == null && ConfigBlocks.blockWoodenDevice != null) {
            silverwoodPlank = new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 7);
        }
        if (silverwoodPlank == null) {
            silverwoodPlank = new ItemStack(Blocks.planks);
        }

        ItemStack tcFilter = ItemApi.getItem("itemResource", 8);
        if (tcFilter != null) {
            GameRegistry.addShapelessRecipe(
                new ItemStack(ModItems.itemSilverwoodFilter, 2),
                tcFilter.copy(),
                silverwoodPlank.copy());
        }

        // Cheap Arcane Recipe: 4 Silverwood Planks + 2 String + 1 Quicksilver -> 8 Filters
        ItemStack quicksilver = ItemApi.getItem("itemResource", 1);
        if (quicksilver == null) quicksilver = new ItemStack(Items.gold_nugget);

        AspectList filterVis = new AspectList().add(Aspect.AIR, 5)
            .add(Aspect.WATER, 5)
            .add(Aspect.ORDER, 5);

        recipeSilverwoodFilter = ThaumcraftApi.addArcaneCraftingRecipe(
            "SILVERWOOD_FILTER",
            new ItemStack(ModItems.itemSilverwoodFilter, 8),
            filterVis,
            " P ",
            "SQS",
            " P ",
            'P',
            silverwoodPlank.copy(),
            'S',
            new ItemStack(Items.string),
            'Q',
            quicksilver.copy());

        // 3. Compact Infernal Furnace Infusion Recipe
        ItemStack brainJar = ItemApi.getBlock("blockJar", 1);
        if (brainJar == null) brainJar = new ItemStack(ConfigBlocks.blockJar, 1, 1);

        ItemStack primordialPearl = ItemApi.getItem("itemEldritchObject", 3);
        if (primordialPearl == null) primordialPearl = ItemApi.getItem("itemResource", 16);
        if (primordialPearl == null) primordialPearl = new ItemStack(Items.ender_eye);

        ItemStack alumentum = ItemApi.getItem("itemResource", 0);
        if (alumentum == null) alumentum = new ItemStack(Items.coal);

        AspectList aspects = new AspectList().add(Aspect.FIRE, 96)
            .add(Aspect.METAL, 96)
            .add(Aspect.ENTROPY, 48)
            .add(Aspect.MIND, 48)
            .add(Aspect.ELDRITCH, 32)
            .add(Aspect.CRAFT, 32);

        // 3x original materials (36 Obsidian, 36 Nether Brick, 3 Iron Bars, 3 Lava) + Brain in a Jar + Primordial Pearl
        ItemStack[] components = new ItemStack[] { brainJar.copy(), primordialPearl.copy(),
            new ItemStack(Blocks.obsidian), new ItemStack(Blocks.nether_brick), new ItemStack(Blocks.iron_bars),
            new ItemStack(Items.lava_bucket), new ItemStack(Blocks.obsidian), new ItemStack(Blocks.nether_brick),
            new ItemStack(Blocks.iron_bars), alumentum.copy(), new ItemStack(Blocks.obsidian),
            new ItemStack(Blocks.nether_brick), new ItemStack(Items.lava_bucket), alumentum.copy() };

        recipeCompactInfernalFurnace = ThaumcraftApi.addInfusionCraftingRecipe(
            "COMPACT_INFERNAL_FURNACE",
            new ItemStack(ModBlocks.compactInfernalFurnace),
            5,
            aspects,
            new ItemStack(Items.lava_bucket),
            components);
    }
}
