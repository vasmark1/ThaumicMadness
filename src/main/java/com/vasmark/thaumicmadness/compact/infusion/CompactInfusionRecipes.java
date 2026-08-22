package com.vasmark.thaumicmadness.compact.infusion;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.vasmark.thaumicmadness.ModBlocks;

import thaumcraft.api.ItemApi;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.common.config.ConfigBlocks;

public class CompactInfusionRecipes {

    public static InfusionRecipe recipeCompactInfusionMatrix;

    public static void init() {
        ItemStack infusionMatrix = ItemApi.getBlock("blockStoneDevice", 2);
        if (infusionMatrix == null) infusionMatrix = new ItemStack(ConfigBlocks.blockStoneDevice, 1, 2);

        ItemStack pedestal = ItemApi.getBlock("blockStoneDevice", 1);
        if (pedestal == null) pedestal = new ItemStack(ConfigBlocks.blockStoneDevice, 1, 1);

        ItemStack voidIngot = ItemApi.getItem("itemResource", 16);
        if (voidIngot == null) voidIngot = ItemApi.getItem("itemResource", 2); // Thaumium fallback
        if (voidIngot == null) voidIngot = new ItemStack(Items.iron_ingot);

        ItemStack salisMundus = ItemApi.getItem("itemResource", 14);
        if (salisMundus == null) salisMundus = new ItemStack(Items.glowstone_dust);

        ItemStack sanitySoap = ItemApi.getItem("itemSanitySoap", 0);
        if (sanitySoap == null) sanitySoap = ItemApi.getItem("itemResource", 6); // Amber
        if (sanitySoap == null) sanitySoap = new ItemStack(Items.slime_ball);

        AspectList aspects = new AspectList().add(Aspect.MAGIC, 64)
            .add(Aspect.CRAFT, 64)
            .add(Aspect.ORDER, 32)
            .add(Aspect.ELDRITCH, 32);

        ItemStack[] components = new ItemStack[] { pedestal.copy(), voidIngot.copy(), pedestal.copy(),
            salisMundus.copy(), pedestal.copy(), sanitySoap.copy(), pedestal.copy(), voidIngot.copy(),
            salisMundus.copy(), sanitySoap.copy() };

        recipeCompactInfusionMatrix = ThaumcraftApi.addInfusionCraftingRecipe(
            "COMPACT_INFUSION_MATRIX",
            new ItemStack(ModBlocks.compactInfusionMatrix),
            6,
            aspects,
            infusionMatrix.copy(),
            components);
    }
}
