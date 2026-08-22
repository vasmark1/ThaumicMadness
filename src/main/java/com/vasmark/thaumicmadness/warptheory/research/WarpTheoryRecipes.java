package com.vasmark.thaumicmadness.warptheory.research;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.vasmark.thaumicmadness.item.ModItems;
import com.vasmark.thaumicmadness.warptheory.WarpTheoryManager;

import thaumcraft.api.ItemApi;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;

public class WarpTheoryRecipes {

    public static InfusionRecipe recipeSanityCharm;
    public static InfusionRecipe recipePureTear;
    public static InfusionRecipe recipePurificationAmulet;
    public static CrucibleRecipe recipeUnstableCatalyst;
    public static ShapelessArcaneRecipe recipeCursedParchment;

    public static void init() {
        ItemStack voidIngot = ItemApi.getItem("itemResource", 16);
        ItemStack voidSeed = ItemApi.getItem("itemResource", 17);
        ItemStack salisMundus = ItemApi.getItem("itemResource", 14);
        ItemStack primalCharm = ItemApi.getItem("itemResource", 15);
        ItemStack amber = ItemApi.getItem("itemResource", 6);
        ItemStack sanitySoap = ItemApi.getItem("itemSanitySoap", 0);
        ItemStack baubleAmulet = ItemApi.getItem("itemBaubleBlanks", 0);

        if (voidIngot == null) voidIngot = new ItemStack(Items.iron_ingot);
        if (voidSeed == null) voidSeed = new ItemStack(Items.ender_pearl);
        if (salisMundus == null) salisMundus = new ItemStack(Items.glowstone_dust);
        if (primalCharm == null) primalCharm = new ItemStack(Items.gold_ingot);
        if (amber == null) amber = new ItemStack(Items.gold_nugget);
        if (sanitySoap == null) sanitySoap = new ItemStack(Items.slime_ball);
        if (baubleAmulet == null) baubleAmulet = new ItemStack(Items.gold_ingot);

        // 1. Sanity Charm (Mid-Tier Infusion)
        AspectList sanityCharmAspects = new AspectList().add(Aspect.MIND, 32)
            .add(Aspect.HEAL, 32)
            .add(Aspect.ORDER, 32)
            .add(Aspect.ARMOR, 16);

        ItemStack[] sanityCharmComponents = new ItemStack[] { sanitySoap.copy(), amber.copy(), salisMundus.copy(),
            amber.copy() };

        recipeSanityCharm = ThaumcraftApi.addInfusionCraftingRecipe(
            "SANITY_CHARM",
            new ItemStack(ModItems.itemSanityCharm),
            4,
            sanityCharmAspects,
            baubleAmulet.copy(),
            sanityCharmComponents);

        // 2. Pure Tear (High-Tier Infusion)
        AspectList pureTearAspects = new AspectList().add(Aspect.ELDRITCH, 64)
            .add(Aspect.EXCHANGE, 64)
            .add(Aspect.ORDER, 64)
            .add(Aspect.MIND, 32)
            .add(Aspect.HEAL, 32);

        ItemStack[] pureTearComponents = new ItemStack[] { voidIngot.copy(), salisMundus.copy(),
            new ItemStack(Items.ghast_tear), primalCharm.copy(), voidIngot.copy(), salisMundus.copy(),
            new ItemStack(Items.ghast_tear), voidSeed.copy() };

        recipePureTear = ThaumcraftApi.addInfusionCraftingRecipe(
            "WARPCLEANSER",
            new ItemStack(WarpTheoryManager.itemPureTear),
            8,
            pureTearAspects,
            new ItemStack(Items.nether_star),
            pureTearComponents);

        // 3. Purification Amulet (Endgame Infusion)
        AspectList amuletAspects = new AspectList().add(Aspect.ELDRITCH, 128)
            .add(Aspect.EXCHANGE, 128)
            .add(Aspect.MAGIC, 128)
            .add(Aspect.ORDER, 64)
            .add(Aspect.ARMOR, 64);

        ItemStack[] amuletComponents = new ItemStack[] { new ItemStack(WarpTheoryManager.itemPureTear),
            voidIngot.copy(), sanitySoap.copy(), voidSeed.copy(), new ItemStack(WarpTheoryManager.itemPureTear),
            voidIngot.copy(), sanitySoap.copy(), voidSeed.copy() };

        recipePurificationAmulet = ThaumcraftApi.addInfusionCraftingRecipe(
            "WARPAMULET",
            new ItemStack(WarpTheoryManager.itemPurificationAmulet),
            10,
            amuletAspects,
            baubleAmulet.copy(),
            amuletComponents);

        // 4. Unstable Catalyst (Crucible Transmutation)
        AspectList catalystAspects = new AspectList().add(Aspect.ELDRITCH, 32)
            .add(Aspect.ENTROPY, 32)
            .add(Aspect.EXCHANGE, 16)
            .add(Aspect.FLESH, 16);

        recipeUnstableCatalyst = ThaumcraftApi.addCrucibleRecipe(
            "WARPSOMETHING",
            new ItemStack(WarpTheoryManager.itemUnstableCatalyst),
            new ItemStack(Items.rotten_flesh),
            catalystAspects);
        ThaumcraftApi.addCrucibleRecipe(
            "WARPSOMETHING",
            new ItemStack(WarpTheoryManager.itemUnstableCatalyst),
            new ItemStack(Items.porkchop),
            catalystAspects);
        ThaumcraftApi.addCrucibleRecipe(
            "WARPSOMETHING",
            new ItemStack(WarpTheoryManager.itemUnstableCatalyst),
            new ItemStack(Items.beef),
            catalystAspects);
        ThaumcraftApi.addWarpToItem(new ItemStack(WarpTheoryManager.itemUnstableCatalyst), 2);

        // 5. Cursed Parchment (Arcane Crafting)
        AspectList paperVis = new AspectList().add(Aspect.WATER, 16)
            .add(Aspect.ORDER, 16)
            .add(Aspect.ENTROPY, 16);

        recipeCursedParchment = ThaumcraftApi.addShapelessArcaneCraftingRecipe(
            "WARPPAPER",
            new ItemStack(WarpTheoryManager.itemCursedParchment, 2),
            paperVis,
            new Object[] { salisMundus.copy(), new ItemStack(Items.paper), voidSeed.copy(),
                new ItemStack(Items.paper) });
    }
}
