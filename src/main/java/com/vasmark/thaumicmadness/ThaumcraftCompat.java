package com.vasmark.thaumicmadness;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

public class ThaumcraftCompat {

    public static final String CATEGORY_KEY = "MYMOD";
    public static final String RESEARCH_INFUSED_DIRT = "INFUSED_DIRT";
    public static final String RESEARCH_CONSTANT_VIGILANCE = "CONSTANT_VIGILANCE";
    public static InfusionRecipe recipeInfusedDirt;

    public static void init() {
        if (!Config.enableInfusedDirt) return;

        // 1. Register new Thaumonomicon category tab
        ResourceLocation icon = new ResourceLocation("thaumcraft", "textures/aspects/terra.png");
        ResourceLocation background = new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png");
        ResearchCategories.registerCategory(CATEGORY_KEY, icon, background);

        // 2. Register Infusion recipe (1 Terra essentia, Dirt block central, 1 Clay ball on pedestal)
        recipeInfusedDirt = ThaumcraftApi.addInfusionCraftingRecipe(
            RESEARCH_INFUSED_DIRT,
            new ItemStack(ModBlocks.infusedDirt),
            1,
            new AspectList().add(Aspect.EARTH, 1),
            new ItemStack(Blocks.dirt),
            new ItemStack[] { new ItemStack(net.minecraft.init.Items.clay_ball) });

        // 3. Register Research Item in the new category
        AspectList researchTags = new AspectList().add(Aspect.EARTH, 3)
            .add(Aspect.MAGIC, 2);
        ResearchItem research = new ResearchItem(
            RESEARCH_INFUSED_DIRT,
            CATEGORY_KEY,
            researchTags,
            0,
            0,
            1,
            new ItemStack(ModBlocks.infusedDirt));

        research.setPages(new ResearchPage("tc.research_page.INFUSED_DIRT.1"), new ResearchPage(recipeInfusedDirt));
        research.registerResearchItem();

        // 4. Register Research Item: Constant Vigilance ("Постоянная бдительность")
        AspectList vigilanceAspects = new AspectList().add(Aspect.MIND, 4)
            .add(Aspect.ELDRITCH, 3)
            .add(Aspect.SENSES, 3);

        ItemStack sanityCheckerStack = thaumcraft.common.config.ConfigItems.itemSanityChecker != null
            ? new ItemStack(thaumcraft.common.config.ConfigItems.itemSanityChecker)
            : new ItemStack(ModBlocks.infusedDirt);

        ResearchItem vigilanceResearch = new ResearchItem(
            RESEARCH_CONSTANT_VIGILANCE,
            CATEGORY_KEY,
            vigilanceAspects,
            2,
            0,
            2,
            sanityCheckerStack);

        vigilanceResearch.setParents("SANITYCHECK");
        vigilanceResearch.setPages(new ResearchPage("tc.research_page.CONSTANT_VIGILANCE.1"));
        vigilanceResearch.registerResearchItem();
    }
}
