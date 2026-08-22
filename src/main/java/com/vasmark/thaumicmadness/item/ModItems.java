package com.vasmark.thaumicmadness.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ModItems {

    public static Item itemThaumonomiconAtlas;
    public static Item itemSanityCharm;
    public static Item itemSilverwoodFilter;
    public static Item itemTaintedFilter;

    public static void init() {
        itemThaumonomiconAtlas = new ItemThaumonomiconAtlas();
        GameRegistry.registerItem(itemThaumonomiconAtlas, "thaumonomicon_atlas");

        itemSanityCharm = new ItemSanityCharm();
        GameRegistry.registerItem(itemSanityCharm, "sanity_charm");

        itemSilverwoodFilter = new ItemSilverwoodFilter();
        GameRegistry.registerItem(itemSilverwoodFilter, "silverwood_filter");

        itemTaintedFilter = new ItemTaintedFilter();
        GameRegistry.registerItem(itemTaintedFilter, "tainted_filter");

        registerAspects();
    }

    public static void registerAspects() {
        // Silverwood Filter Aspects (Total: 12 vis)
        AspectList silverwoodAspects = new AspectList().add(Aspect.ORDER, 4)
            .add(Aspect.TREE, 4)
            .add(Aspect.CLOTH, 2)
            .add(Aspect.MAGIC, 2);
        ThaumcraftApi.registerObjectTag(new ItemStack(itemSilverwoodFilter, 1, 0), silverwoodAspects);
        ThaumcraftApi
            .registerObjectTag(new ItemStack(itemSilverwoodFilter, 1, OreDictionary.WILDCARD_VALUE), silverwoodAspects);
        ThaumcraftApi.registerObjectTag(
            new ItemStack(itemSilverwoodFilter),
            new int[] { 0, OreDictionary.WILDCARD_VALUE },
            silverwoodAspects);

        // Tainted Filter Aspects (Total: 36 vis <= 50 max capacity of Alchemical Furnace)
        AspectList taintedAspects = new AspectList().add(Aspect.TAINT, 20)
            .add(Aspect.ENTROPY, 8)
            .add(Aspect.DARKNESS, 4)
            .add(Aspect.TREE, 2)
            .add(Aspect.CLOTH, 2);
        ThaumcraftApi.registerObjectTag(new ItemStack(itemTaintedFilter, 1, 0), taintedAspects);
        ThaumcraftApi
            .registerObjectTag(new ItemStack(itemTaintedFilter, 1, OreDictionary.WILDCARD_VALUE), taintedAspects);
        ThaumcraftApi.registerObjectTag(
            new ItemStack(itemTaintedFilter),
            new int[] { 0, OreDictionary.WILDCARD_VALUE },
            taintedAspects);
    }
}
