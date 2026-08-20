package com.vasmark.thaumicmadness.nodetracker;

import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vasmark.thaumicmadness.item.ModItems;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigItems;

public class AtlasResearchAndRecipes {

    private static final Logger LOGGER = LogManager.getLogger("ThaumicMadness-AtlasResearch");
    public static InfusionRecipe recipeAtlas;

    public static void init() {
        if (!com.vasmark.thaumicmadness.Config.enableAtlas) return;
        registerRecipes();
        registerResearch();
    }

    private static void registerRecipes() {
        AspectList aspects = new AspectList().add(Aspect.LIGHT, 100)
            .add(Aspect.LIFE, 80)
            .add(Aspect.GREED, 20);

        ItemStack[] components = new ItemStack[] { new ItemStack(ConfigItems.itemThaumometer),
            new ItemStack(ConfigItems.itemGoggles), new ItemStack(ConfigItems.itemAmuletVis) };

        recipeAtlas = ThaumcraftApi.addInfusionCraftingRecipe(
            "THAUMONOMICON_ATLAS",
            new ItemStack(ModItems.itemThaumonomiconAtlas),
            3,
            aspects,
            new ItemStack(ConfigItems.itemThaumonomicon),
            components);

        LOGGER.info("Registered Infusion recipe for THAUMONOMICON_ATLAS");
    }

    private static void registerResearch() {
        AspectList tags = new AspectList().add(Aspect.MIND, 6)
            .add(Aspect.AURA, 6)
            .add(Aspect.SENSES, 6)
            .add(Aspect.LIGHT, 4)
            .add(Aspect.LIFE, 4)
            .add(Aspect.GREED, 4);

        ResearchItem research = new ResearchItem(
            "THAUMONOMICON_ATLAS",
            "BASICS",
            tags,
            -5,
            2,
            2,
            new ItemStack(ModItems.itemThaumonomiconAtlas));

        research.setParents("NODES");
        research.setParentsHidden("GOGGLES", "INFUSION");
        research.setSpecial();
        research.setPages(
            new ResearchPage("tc.research_page.THAUMONOMICON_ATLAS.1"),
            new ResearchPage(recipeAtlas),
            new ResearchPage("tc.research_page.THAUMONOMICON_ATLAS.2"));

        research.registerResearchItem();
        LOGGER.info(
            "Registered Research THAUMONOMICON_ATLAS in category BASICS. Total researches in BASICS: {}",
            ResearchCategories.getResearchList("BASICS") != null
                ? ResearchCategories.getResearchList("BASICS").research.size()
                : 0);
    }
}
