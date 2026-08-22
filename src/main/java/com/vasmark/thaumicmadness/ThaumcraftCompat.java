package com.vasmark.thaumicmadness;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.vasmark.thaumicmadness.compact.furnace.CompactFurnaceRecipes;
import com.vasmark.thaumicmadness.item.ModItems;

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
        // 1. Register new Thaumonomicon category tab
        ResourceLocation icon = new ResourceLocation("thaumcraft", "textures/aspects/alienis.png");
        ResourceLocation background = new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png");
        ResearchCategories.registerCategory(CATEGORY_KEY, icon, background);

        if (!Config.enableInfusedDirt) return;

        // 2. Register Infusion recipe (1 Terra essentia, Dirt block central, 1 Clay
        // ball on pedestal)
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

        research.setParents("INFUSION");
        research.setPages(
            new ResearchPage("tc.research_page.INFUSED_DIRT.1"),
            new ResearchPage(recipeInfusedDirt),
            new ResearchPage("tc.research_page.INFUSED_DIRT.2"));
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

        // 5. Clone Original Infusion Matrix ("INFUSION") into our tab at (1, -2) (Thaumic Bases style)
        ResearchItem origInfusion = ResearchCategories.getResearchList("ARTIFICE") != null
            ? ResearchCategories.getResearchList("ARTIFICE").research.get("INFUSION")
            : ResearchCategories.getResearch("INFUSION");
        if (origInfusion != null) {
            ResearchItem copyInfusion = copy(origInfusion, "TM.INFUSION", CATEGORY_KEY, 1, -2);
            if (copyInfusion != null) {
                copyInfusion.setConcealed();
                copyInfusion.setHidden();
                copyInfusion.registerResearchItem();
            }
        }

        // 6. Register Research Item: Compact Infusion Matrix ("Компактная матрица наполнения")
        AspectList matrixAspects = new AspectList().add(Aspect.MAGIC, 12)
            .add(Aspect.CRAFT, 12)
            .add(Aspect.ORDER, 8)
            .add(Aspect.ELDRITCH, 8)
            .add(Aspect.MECHANISM, 6);

        ResearchItem matrixResearch = new ResearchItem(
            "COMPACT_INFUSION_MATRIX",
            CATEGORY_KEY,
            matrixAspects,
            1,
            -4,
            3,
            new ItemStack(ModBlocks.compactInfusionMatrix));

        matrixResearch.setParents("TM.INFUSION");
        matrixResearch.setParentsHidden("INFUSION", RESEARCH_INFUSED_DIRT);
        matrixResearch.setSpecial();
        matrixResearch.setPages(
            new ResearchPage("tc.research_page.COMPACT_INFUSION_MATRIX.1"),
            new ResearchPage(
                com.vasmark.thaumicmadness.compact.infusion.CompactInfusionRecipes.recipeCompactInfusionMatrix),
            new ResearchPage("tc.research_page.COMPACT_INFUSION_MATRIX.2"));
        matrixResearch.registerResearchItem();
        ThaumcraftApi.addWarpToResearch("COMPACT_INFUSION_MATRIX", 1);

        // 7. Clone Original Infernal Furnace ("INFERNALFURNACE") into our tab at (-3, -2) (Thaumic Bases style)
        ResearchItem origInfernal = ResearchCategories.getResearchList("ARTIFICE") != null
            ? ResearchCategories.getResearchList("ARTIFICE").research.get("INFERNALFURNACE")
            : ResearchCategories.getResearch("INFERNALFURNACE");
        if (origInfernal != null) {
            ResearchItem copyInfernal = copy(origInfernal, "TM.INFERNALFURNACE", CATEGORY_KEY, -3, -2);
            if (copyInfernal != null) {
                copyInfernal.setConcealed();
                copyInfernal.setHidden();
                copyInfernal.registerResearchItem();
            }
        }

        // 8. Clone Original Brain in a Jar ("JARBRAIN") into our tab at (-1, -2) (Thaumic Bases style)
        ResearchItem origJarBrain = ResearchCategories.getResearchList("ARTIFICE") != null
            ? ResearchCategories.getResearchList("ARTIFICE").research.get("JARBRAIN")
            : ResearchCategories.getResearch("JARBRAIN");
        if (origJarBrain != null) {
            ResearchItem copyJarBrain = copy(origJarBrain, "TM.JARBRAIN", CATEGORY_KEY, -1, -2);
            if (copyJarBrain != null) {
                copyJarBrain.setConcealed();
                copyJarBrain.setHidden();
                copyJarBrain.registerResearchItem();
            }
        }

        // 9. Register Research Item: Compact Infernal Furnace ("Компактная адская печь")
        AspectList furnaceAspects = new AspectList().add(Aspect.FIRE, 12)
            .add(Aspect.METAL, 12)
            .add(Aspect.ENTROPY, 8)
            .add(Aspect.MIND, 8)
            .add(Aspect.ELDRITCH, 6);

        ResearchItem furnaceResearch = new ResearchItem(
            "COMPACT_INFERNAL_FURNACE",
            CATEGORY_KEY,
            furnaceAspects,
            -2,
            -4,
            3,
            new ItemStack(ModBlocks.compactInfernalFurnace));

        furnaceResearch.setParents("TM.INFERNALFURNACE", "TM.JARBRAIN");
        furnaceResearch.setParentsHidden("INFERNALFURNACE", "JARBRAIN", RESEARCH_INFUSED_DIRT);
        furnaceResearch.setSpecial();
        furnaceResearch.setPages(
            new ResearchPage("tc.research_page.COMPACT_INFERNAL_FURNACE.1"),
            new ResearchPage(
                com.vasmark.thaumicmadness.compact.furnace.CompactFurnaceRecipes.recipeCompactInfernalFurnace),
            new ResearchPage("tc.research_page.COMPACT_INFERNAL_FURNACE.2"));
        furnaceResearch.registerResearchItem();
        ThaumcraftApi.addWarpToResearch("COMPACT_INFERNAL_FURNACE", 1);

        // 10. Register Research Item: Silverwood Filter ("Фильтры из серебряного дерева")
        AspectList filterAspects = new AspectList().add(Aspect.ORDER, 8)
            .add(Aspect.TREE, 8)
            .add(Aspect.TAINT, 6)
            .add(Aspect.MAGIC, 4);

        ResearchItem filterResearch = new ResearchItem(
            "SILVERWOOD_FILTER",
            CATEGORY_KEY,
            filterAspects,
            -2,
            -6,
            2,
            new ItemStack(ModItems.itemSilverwoodFilter));

        filterResearch.setParents("COMPACT_INFERNAL_FURNACE");
        filterResearch.setPages(
            new ResearchPage("tc.research_page.SILVERWOOD_FILTER.1"),
            new ResearchPage(CompactFurnaceRecipes.recipeSilverwoodFilter),
            new ResearchPage("tc.research_page.SILVERWOOD_FILTER.2"));
        filterResearch.registerResearchItem();
    }

    /**
     * Clones an original Thaumcraft ResearchItem into a new category with bi-directional sibling synchronization,
     * matching the exact method used in Thaumic Bases (TBThaumonomicon.copy).
     */
    public static ResearchItem copy(ResearchItem toCopy, String newKey, String newCat, int x, int y) {
        if (toCopy == null) return null;
        ResearchItem copy;
        if (toCopy.icon_resource != null) {
            copy = new ResearchItem(newKey, newCat, toCopy.tags, x, y, toCopy.getComplexity(), toCopy.icon_resource);
        } else {
            copy = new ResearchItem(newKey, newCat, toCopy.tags, x, y, toCopy.getComplexity(), toCopy.icon_item);
        }
        copy.parents = toCopy.parents;
        copy.parentsHidden = toCopy.parentsHidden;
        if (toCopy.getPages() != null) {
            copy.setPages(toCopy.getPages());
        }
        if (toCopy.getAspectTriggers() != null) {
            copy.setAspectTriggers(toCopy.getAspectTriggers());
        }
        if (toCopy.getEntityTriggers() != null) {
            copy.setEntityTriggers(toCopy.getEntityTriggers());
        }
        if (toCopy.getItemTriggers() != null) {
            copy.setItemTriggers(toCopy.getItemTriggers());
        }
        if (toCopy.isAutoUnlock()) copy.setAutoUnlock();
        if (toCopy.isConcealed()) copy.setConcealed();
        if (toCopy.isHidden()) copy.setHidden();
        if (toCopy.isLost()) copy.setLost();
        if (toCopy.isRound()) copy.setRound();
        if (toCopy.isSecondary()) copy.setSecondary();
        if (toCopy.isSpecial()) copy.setSpecial();
        if (toCopy.isStub()) copy.setStub();

        // Bi-directional Sibling synchronization between original research and the copy
        if (toCopy.siblings != null && toCopy.siblings.length > 0) {
            String[] newSiblings = new String[toCopy.siblings.length + 1];
            System.arraycopy(toCopy.siblings, 0, newSiblings, 0, toCopy.siblings.length);
            newSiblings[newSiblings.length - 1] = newKey;
            toCopy.setSiblings(newSiblings);
        } else {
            toCopy.setSiblings(new String[] { newKey });
        }

        if (copy.siblings != null && copy.siblings.length > 0) {
            String[] newSiblings = new String[copy.siblings.length + 1];
            System.arraycopy(copy.siblings, 0, newSiblings, 0, copy.siblings.length);
            newSiblings[newSiblings.length - 1] = toCopy.key;
            copy.setSiblings(newSiblings);
        } else {
            copy.setSiblings(new String[] { toCopy.key });
        }

        return copy;
    }
}
