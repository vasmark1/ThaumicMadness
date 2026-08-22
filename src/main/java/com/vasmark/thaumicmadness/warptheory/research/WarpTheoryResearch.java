package com.vasmark.thaumicmadness.warptheory.research;

import net.minecraft.item.ItemStack;

import com.vasmark.thaumicmadness.ThaumcraftCompat;
import com.vasmark.thaumicmadness.item.ModItems;
import com.vasmark.thaumicmadness.warptheory.WarpTheoryManager;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

public class WarpTheoryResearch {

    public static ResearchItem researchSanityCharm;
    public static ResearchItem researchPureTear;
    public static ResearchItem researchPurificationAmulet;
    public static ResearchItem researchUnstableCatalyst;
    public static ResearchItem researchCursedParchment;

    public static void init() {
        String cat = ThaumcraftCompat.CATEGORY_KEY; // "MYMOD" (Thaumic Madness category)

        // 1. Sanity Charm (Talisman of Clarity) - (2, 2)
        AspectList charmAspects = new AspectList().add(Aspect.MIND, 8)
            .add(Aspect.HEAL, 8)
            .add(Aspect.ORDER, 6)
            .add(Aspect.ARMOR, 4);

        researchSanityCharm = new ResearchItem(
            "SANITY_CHARM",
            cat,
            charmAspects,
            2,
            2,
            2,
            new ItemStack(ModItems.itemSanityCharm));
        researchSanityCharm.setPages(
            new ResearchPage("tc.research_page.SANITY_CHARM.1"),
            new ResearchPage(WarpTheoryRecipes.recipeSanityCharm));
        researchSanityCharm.setParents(ThaumcraftCompat.RESEARCH_CONSTANT_VIGILANCE);
        researchSanityCharm.registerResearchItem();

        // 2. Cursed Parchment (Arcane Litmus Paper) - (-2, 0)
        AspectList paperAspects = new AspectList().add(Aspect.MIND, 6)
            .add(Aspect.MAGIC, 6)
            .add(Aspect.SENSES, 6);

        researchCursedParchment = new ResearchItem(
            "WARPPAPER",
            cat,
            paperAspects,
            -2,
            0,
            1,
            new ItemStack(WarpTheoryManager.itemCursedParchment));
        researchCursedParchment.setPages(
            new ResearchPage("tc.research_page.WARPPAPER.1"),
            new ResearchPage(WarpTheoryRecipes.recipeCursedParchment));
        researchCursedParchment.setParents(ThaumcraftCompat.RESEARCH_CONSTANT_VIGILANCE);
        researchCursedParchment.registerResearchItem();

        // 3. Unstable Catalyst (Hunk of Something) - (-2, 2)
        AspectList catalystAspects = new AspectList().add(Aspect.ELDRITCH, 8)
            .add(Aspect.ENTROPY, 8)
            .add(Aspect.EXCHANGE, 6);

        researchUnstableCatalyst = new ResearchItem(
            "WARPSOMETHING",
            cat,
            catalystAspects,
            -2,
            2,
            2,
            new ItemStack(WarpTheoryManager.itemUnstableCatalyst));
        researchUnstableCatalyst.setPages(
            new ResearchPage("tc.research_page.WARPSOMETHING.1"),
            new ResearchPage(WarpTheoryRecipes.recipeUnstableCatalyst));
        researchUnstableCatalyst.setParents("WARPPAPER");
        researchUnstableCatalyst.registerResearchItem();
        ThaumcraftApi.addWarpToResearch("WARPSOMETHING", 2);

        // 4. Pure Tear (High-Tier Infusion) - (0, 3)
        AspectList tearAspects = new AspectList().add(Aspect.ELDRITCH, 12)
            .add(Aspect.EXCHANGE, 8)
            .add(Aspect.ORDER, 8)
            .add(Aspect.HEAL, 8)
            .add(Aspect.MIND, 8);

        researchPureTear = new ResearchItem(
            "WARPCLEANSER",
            cat,
            tearAspects,
            0,
            3,
            3,
            new ItemStack(WarpTheoryManager.itemPureTear));
        researchPureTear.setPages(
            new ResearchPage("tc.research_page.WARPCLEANSER.1"),
            new ResearchPage(WarpTheoryRecipes.recipePureTear));
        researchPureTear.setParents("SANITY_CHARM", "WARPPAPER");
        researchPureTear.registerResearchItem();
        ThaumcraftApi.addWarpToResearch("WARPCLEANSER", 2);

        // 5. Purification Talisman (Endgame Infusion) - (0, 5)
        AspectList amuletAspects = new AspectList().add(Aspect.ELDRITCH, 16)
            .add(Aspect.EXCHANGE, 16)
            .add(Aspect.MAGIC, 16)
            .add(Aspect.ORDER, 12)
            .add(Aspect.ARMOR, 12);

        researchPurificationAmulet = new ResearchItem(
            "WARPAMULET",
            cat,
            amuletAspects,
            0,
            5,
            3,
            new ItemStack(WarpTheoryManager.itemPurificationAmulet));
        researchPurificationAmulet.setPages(
            new ResearchPage("tc.research_page.WARPAMULET.1"),
            new ResearchPage(WarpTheoryRecipes.recipePurificationAmulet));
        researchPurificationAmulet.setParents("WARPCLEANSER");
        researchPurificationAmulet.setSpecial();
        researchPurificationAmulet.registerResearchItem();
        ThaumcraftApi.addWarpToResearch("WARPAMULET", 3);
    }
}
