package com.vasmark.thaumicmadness.compat.bloodmagic;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import WayofTime.alchemicalWizardry.ModBlocks;
import WayofTime.alchemicalWizardry.ModItems;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

public class BloodMagicResearch {

    private static void addPage(List<ResearchPage> list, Object obj) {
        if (obj == null) return;
        if (obj instanceof String) {
            list.add(new ResearchPage((String) obj));
        } else if (obj instanceof IArcaneRecipe) {
            list.add(new ResearchPage((IArcaneRecipe) obj));
        } else if (obj instanceof InfusionRecipe) {
            list.add(new ResearchPage((InfusionRecipe) obj));
        } else if (obj instanceof CrucibleRecipe) {
            list.add(new ResearchPage((CrucibleRecipe) obj));
        }
    }

    private static ResearchPage[] toArray(List<ResearchPage> list) {
        return list.toArray(new ResearchPage[list.size()]);
    }

    public static void registerResearch() {
        String cat = BloodMagicCompat.CATEGORY_BLOOD_MAGIC;

        // 1. BM_INTRO (Введение в гемомантию)
        List<ResearchPage> pIntro = new ArrayList<>();
        addPage(pIntro, "tc.research_page.BM_INTRO.1");
        addPage(pIntro, "tc.research_page.BM_INTRO.2");
        addPage(pIntro, "tc.research_page.BM_INTRO.3");
        new ResearchItem(
            "BM_INTRO",
            cat,
            new AspectList().add(Aspect.LIFE, 4)
                .add(Aspect.MAGIC, 4)
                .add(Aspect.SOUL, 4),
            0,
            0,
            1,
            new ItemStack(ModItems.weakBloodOrb)).setPages(toArray(pIntro))
                .setAutoUnlock()
                .setRound()
                .registerResearchItem();

        // 2. BM_SACRIFICE (Жертвенные кинжалы)
        List<ResearchPage> pSacrifice = new ArrayList<>();
        addPage(pSacrifice, "tc.research_page.BM_SACRIFICE.1");
        addPage(pSacrifice, BloodMagicRecipes.recipeSacrificialKnife);
        addPage(pSacrifice, "tc.research_page.BM_SACRIFICE.2");
        addPage(pSacrifice, BloodMagicRecipes.recipeDaggerOfSacrifice);
        new ResearchItem(
            "BM_SACRIFICE",
            cat,
            new AspectList().add(Aspect.WEAPON, 4)
                .add(Aspect.LIFE, 6)
                .add(Aspect.DEATH, 4),
            -2,
            0,
            2,
            new ItemStack(ModItems.sacrificialDagger)).setPages(toArray(pSacrifice))
                .setParents("BM_INTRO")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("BM_SACRIFICE", 1);

        // 3. BM_ALTAR_1 (Кровавый алтарь I уровня)
        List<ResearchPage> pAltar1 = new ArrayList<>();
        addPage(pAltar1, "tc.research_page.BM_ALTAR_1.1");
        addPage(pAltar1, BloodMagicRecipes.recipeBloodAltar);
        addPage(pAltar1, "tc.research_page.BM_ALTAR_1.2");
        addPage(pAltar1, BloodMagicRecipes.recipeBlankSlate);
        addPage(pAltar1, "tc.research_page.BM_ALTAR_1.3");
        new ResearchItem(
            "BM_ALTAR_1",
            cat,
            new AspectList().add(Aspect.LIFE, 8)
                .add(Aspect.MAGIC, 6)
                .add(Aspect.HEAL, 4),
            0,
            -2,
            2,
            new ItemStack(ModBlocks.blockAltar)).setPages(toArray(pAltar1))
                .setParents("BM_INTRO")
                .registerResearchItem();

        // 4. BM_SOUL_NETWORK (Сеть душ и Кровавые шары)
        List<ResearchPage> pSoul = new ArrayList<>();
        addPage(pSoul, "tc.research_page.BM_SOUL_NETWORK.1");
        addPage(pSoul, BloodMagicRecipes.recipeWeakBloodOrb);
        addPage(pSoul, BloodMagicRecipes.recipeApprenticeBloodOrb);
        addPage(pSoul, "tc.research_page.BM_SOUL_NETWORK.2");
        addPage(pSoul, BloodMagicRecipes.recipeMagicianBloodOrb);
        addPage(pSoul, BloodMagicRecipes.recipeMasterBloodOrb);
        addPage(pSoul, BloodMagicRecipes.recipeArchmageBloodOrb);
        addPage(pSoul, "tc.research_page.BM_SOUL_NETWORK.3");
        new ResearchItem(
            "BM_SOUL_NETWORK",
            cat,
            new AspectList().add(Aspect.SOUL, 8)
                .add(Aspect.LIFE, 8)
                .add(Aspect.CRYSTAL, 4),
            2,
            0,
            2,
            new ItemStack(ModItems.apprenticeBloodOrb)).setPages(toArray(pSoul))
                .setParents("BM_INTRO")
                .registerResearchItem();

        // 5. BM_RUNES (Руны крови)
        List<ResearchPage> pRunes = new ArrayList<>();
        addPage(pRunes, "tc.research_page.BM_RUNES.1");
        addPage(pRunes, BloodMagicRecipes.recipeBlankRune);
        addPage(pRunes, BloodMagicRecipes.recipeSpeedRune);
        addPage(pRunes, BloodMagicRecipes.recipeCapacityRune);
        addPage(pRunes, "tc.research_page.BM_RUNES.2");
        addPage(pRunes, BloodMagicRecipes.recipeSacrificeRune);
        addPage(pRunes, BloodMagicRecipes.recipeSelfSacrificeRune);
        addPage(pRunes, BloodMagicRecipes.recipeDislocationRune);
        addPage(pRunes, "tc.research_page.BM_RUNES.3");
        new ResearchItem(
            "BM_RUNES",
            cat,
            new AspectList().add(Aspect.LIFE, 6)
                .add(Aspect.MAGIC, 6)
                .add(Aspect.ORDER, 4),
            0,
            -4,
            3,
            new ItemStack(ModBlocks.bloodRune)).setPages(toArray(pRunes))
                .setParents("BM_ALTAR_1")
                .registerResearchItem();

        // 6. BM_ALTAR_TIERS (Уровни Кровавого алтаря: II - VI)
        List<ResearchPage> pTiers = new ArrayList<>();
        addPage(pTiers, "tc.research_page.BM_ALTAR_TIERS.1");
        addPage(pTiers, BloodMagicRecipes.recipeReinforcedSlate);
        addPage(pTiers, "tc.research_page.BM_ALTAR_TIERS.2");
        addPage(pTiers, BloodMagicRecipes.recipeImbuedSlate);
        addPage(pTiers, "tc.research_page.BM_ALTAR_TIERS.3");
        addPage(pTiers, "tc.research_page.BM_ALTAR_TIERS.4");
        addPage(pTiers, "tc.research_page.BM_ALTAR_TIERS.5");
        new ResearchItem(
            "BM_ALTAR_TIERS",
            cat,
            new AspectList().add(Aspect.LIFE, 12)
                .add(Aspect.MAGIC, 10)
                .add(Aspect.ELDRITCH, 6),
            -2,
            -4,
            3,
            new ItemStack(
                ModBlocks.largeBloodStoneBrick != null ? ModBlocks.largeBloodStoneBrick : ModBlocks.blockAltar))
                    .setPages(toArray(pTiers))
                    .setParents("BM_RUNES")
                    .setSpecial()
                    .registerResearchItem();

        // 7. BM_SIGILS_BASIC (Базовые сигилы)
        List<ResearchPage> pSigils = new ArrayList<>();
        addPage(pSigils, "tc.research_page.BM_SIGILS_BASIC.1");
        addPage(pSigils, BloodMagicRecipes.recipeDivinationSigil);
        addPage(pSigils, BloodMagicRecipes.recipeWaterSigil);
        addPage(pSigils, BloodMagicRecipes.recipeLavaSigil);
        addPage(pSigils, "tc.research_page.BM_SIGILS_BASIC.2");
        addPage(pSigils, BloodMagicRecipes.recipeAirSigil);
        addPage(pSigils, BloodMagicRecipes.recipeVoidSigil);
        new ResearchItem(
            "BM_SIGILS_BASIC",
            cat,
            new AspectList().add(Aspect.SENSES, 6)
                .add(Aspect.WATER, 6)
                .add(Aspect.FIRE, 6),
            2,
            -2,
            2,
            new ItemStack(ModItems.divinationSigil)).setPages(toArray(pSigils))
                .setParents("BM_SOUL_NETWORK", "BM_ALTAR_1")
                .registerResearchItem();

        // 8. BM_SIGILS_ADVANCED (Продвинутые сигилы)
        List<ResearchPage> pSigilsAdv = new ArrayList<>();
        addPage(pSigilsAdv, "tc.research_page.BM_SIGILS_ADVANCED.1");
        addPage(pSigilsAdv, BloodMagicRecipes.recipeGreenGroveSigil);
        addPage(pSigilsAdv, BloodMagicRecipes.recipeSigilHolding);
        addPage(pSigilsAdv, "tc.research_page.BM_SIGILS_ADVANCED.2");
        addPage(pSigilsAdv, "tc.research_page.BM_SIGILS_ADVANCED.3");
        new ResearchItem(
            "BM_SIGILS_ADVANCED",
            cat,
            new AspectList().add(Aspect.MOTION, 8)
                .add(Aspect.VOID, 8)
                .add(Aspect.PLANT, 8),
            4,
            -2,
            3,
            new ItemStack(ModItems.growthSigil != null ? ModItems.growthSigil : ModItems.divinationSigil))
                .setPages(toArray(pSigilsAdv))
                .setParents("BM_SIGILS_BASIC")
                .registerResearchItem();

        // 9. BM_RITUALS_BASIC (Основы ритуалов и Связывание)
        List<ResearchPage> pRituals = new ArrayList<>();
        addPage(pRituals, "tc.research_page.BM_RITUALS_BASIC.1");
        addPage(pRituals, "tc.research_page.BM_RITUALS_BASIC.2");
        addPage(pRituals, "tc.research_page.BM_RITUALS_BASIC.3");
        new ResearchItem(
            "BM_RITUALS_BASIC",
            cat,
            new AspectList().add(Aspect.MAGIC, 10)
                .add(Aspect.SOUL, 8)
                .add(Aspect.EARTH, 6),
            2,
            2,
            3,
            new ItemStack(ModBlocks.blockMasterStone)).setPages(toArray(pRituals))
                .setParents("BM_SOUL_NETWORK")
                .registerResearchItem();

        // 10. BM_BOUND_GEAR (Связанное снаряжение и Броня)
        List<ResearchPage> pBound = new ArrayList<>();
        addPage(pBound, "tc.research_page.BM_BOUND_GEAR.1");
        addPage(pBound, BloodMagicRecipes.recipeBoundBlade);
        addPage(pBound, "tc.research_page.BM_BOUND_GEAR.2");
        addPage(pBound, BloodMagicRecipes.recipeBoundHelmet);
        addPage(pBound, BloodMagicRecipes.recipeBoundChestplate);
        addPage(pBound, BloodMagicRecipes.recipeBoundLeggings);
        addPage(pBound, BloodMagicRecipes.recipeBoundBoots);
        addPage(pBound, "tc.research_page.BM_BOUND_GEAR.3");
        new ResearchItem(
            "BM_BOUND_GEAR",
            cat,
            new AspectList().add(Aspect.WEAPON, 10)
                .add(Aspect.ARMOR, 10)
                .add(Aspect.SOUL, 10),
            4,
            2,
            4,
            new ItemStack(ModItems.energySword != null ? ModItems.energySword : ModItems.boundHelmet))
                .setPages(toArray(pBound))
                .setParents("BM_RITUALS_BASIC")
                .setSpecial()
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("BM_BOUND_GEAR", 2);

        // 11. BM_RITUALS_ADVANCED (Великие ритуалы)
        List<ResearchPage> pRitualsAdv = new ArrayList<>();
        addPage(pRitualsAdv, "tc.research_page.BM_RITUALS_ADVANCED.1");
        addPage(pRitualsAdv, "tc.research_page.BM_RITUALS_ADVANCED.2");
        addPage(pRitualsAdv, "tc.research_page.BM_RITUALS_ADVANCED.3");
        new ResearchItem(
            "BM_RITUALS_ADVANCED",
            cat,
            new AspectList().add(Aspect.ELDRITCH, 12)
                .add(Aspect.LIFE, 12)
                .add(Aspect.MAGIC, 10),
            2,
            4,
            4,
            ModItems.activationCrystal != null ? new ItemStack(ModItems.activationCrystal, 1, 1)
                : new ItemStack(ModBlocks.blockMasterStone)).setPages(toArray(pRitualsAdv))
                    .setParents("BM_RITUALS_BASIC")
                    .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("BM_RITUALS_ADVANCED", 1);

        // 12. BM_ALCHEMY (Алхимия крови и Маршрутизация)
        List<ResearchPage> pAlchemy = new ArrayList<>();
        addPage(pAlchemy, "tc.research_page.BM_ALCHEMY.1");
        addPage(pAlchemy, "tc.research_page.BM_ALCHEMY.2");
        addPage(pAlchemy, "tc.research_page.BM_ALCHEMY.3");
        new ResearchItem(
            "BM_ALCHEMY",
            cat,
            new AspectList().add(Aspect.CRAFT, 8)
                .add(Aspect.MAGIC, 8)
                .add(Aspect.ORDER, 6),
            -2,
            2,
            3,
            new ItemStack(ModBlocks.blockWritingTable != null ? ModBlocks.blockWritingTable : ModBlocks.blockAltar))
                .setPages(toArray(pAlchemy))
                .setParents("BM_INTRO")
                .registerResearchItem();

        // 13. BM_SPELLS (Сложные заклинания и Парадигмы)
        List<ResearchPage> pSpells = new ArrayList<>();
        addPage(pSpells, "tc.research_page.BM_SPELLS.1");
        addPage(pSpells, "tc.research_page.BM_SPELLS.2");
        addPage(pSpells, "tc.research_page.BM_SPELLS.3");
        new ResearchItem(
            "BM_SPELLS",
            cat,
            new AspectList().add(Aspect.MAGIC, 12)
                .add(Aspect.ENERGY, 10)
                .add(Aspect.ORDER, 8),
            -4,
            2,
            4,
            new ItemStack(
                ModBlocks.blockSpellParadigm != null ? ModBlocks.blockSpellParadigm : ModBlocks.blockMasterStone))
                    .setPages(toArray(pSpells))
                    .setParents("BM_ALCHEMY")
                    .registerResearchItem();

        // 14. BM_DEMONOLOGY (Демонология и Созыв проклятых)
        List<ResearchPage> pDemo = new ArrayList<>();
        addPage(pDemo, "tc.research_page.BM_DEMONOLOGY.1");
        addPage(pDemo, BloodMagicRecipes.recipeDemonicSlate);
        addPage(pDemo, "tc.research_page.BM_DEMONOLOGY.2");
        addPage(pDemo, "tc.research_page.BM_DEMONOLOGY.3");
        new ResearchItem(
            "BM_DEMONOLOGY",
            cat,
            new AspectList().add(Aspect.ELDRITCH, 16)
                .add(Aspect.DARKNESS, 12)
                .add(Aspect.SOUL, 12),
            0,
            4,
            5,
            new ItemStack(ModItems.demonBloodShard != null ? ModItems.demonBloodShard : ModItems.weakBloodOrb))
                .setPages(toArray(pDemo))
                .setParents("BM_RITUALS_ADVANCED")
                .setSpecial()
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("BM_DEMONOLOGY", 3);

        // 15. BM_ARSENAL (Blood Arsenal)
        if (BloodMagicCompat.isBloodArsenalLoaded) {
            List<ResearchPage> pArsenal = new ArrayList<>();
            addPage(pArsenal, "tc.research_page.BM_ARSENAL.1");
            addPage(pArsenal, BloodMagicRecipes.recipeBloodInfusedIron);
            addPage(pArsenal, BloodMagicRecipes.recipeBloodInfusedDiamond);
            addPage(pArsenal, "tc.research_page.BM_ARSENAL.2");
            addPage(pArsenal, BloodMagicRecipes.recipeSanguineAmulet);
            addPage(pArsenal, "tc.research_page.BM_ARSENAL.3");
            new ResearchItem(
                "BM_ARSENAL",
                cat,
                new AspectList().add(Aspect.METAL, 10)
                    .add(Aspect.LIFE, 10)
                    .add(Aspect.CRYSTAL, 8),
                4,
                0,
                3,
                new ItemStack(com.arc.bloodarsenal.common.items.ModItems.blood_diamond)).setPages(toArray(pArsenal))
                    .setParents("BM_SOUL_NETWORK")
                    .registerResearchItem();
        }

        // 16. BM_WAND_CAP (Кровавые наконечники)
        List<ResearchPage> pCap = new ArrayList<>();
        addPage(pCap, "tc.research_page.BM_WAND_CAP.1");
        addPage(pCap, BloodMagicRecipes.recipeInertCap);
        addPage(pCap, BloodMagicRecipes.recipeChargedCap);
        new ResearchItem(
            "BM_WAND_CAP",
            cat,
            new AspectList().add(Aspect.LIFE, 8)
                .add(Aspect.MAGIC, 8)
                .add(Aspect.METAL, 6),
            -2,
            -2,
            2,
            new ItemStack(BloodMagicItems.itemWandCapBlood, 1, 0)).setPages(toArray(pCap))
                .setParents("BM_ALTAR_1")
                .registerResearchItem();

        // 17. BM_WAND_ROD (Кровавый стержень жезла)
        List<ResearchPage> pRod = new ArrayList<>();
        addPage(pRod, "tc.research_page.BM_WAND_ROD.1");
        addPage(pRod, BloodMagicRecipes.recipeWandRod);
        new ResearchItem(
            "BM_WAND_ROD",
            cat,
            new AspectList().add(Aspect.LIFE, 16)
                .add(Aspect.MAGIC, 12)
                .add(Aspect.SOUL, 10),
            -4,
            0,
            3,
            new ItemStack(BloodMagicItems.itemWandRodBlood, 1, 0)).setPages(toArray(pRod))
                .setParents("BM_WAND_CAP", "BM_SOUL_NETWORK")
                .registerResearchItem();

        // 18. BM_STAFF_ROD (Кровавый стержень посоха)
        List<ResearchPage> pStaff = new ArrayList<>();
        addPage(pStaff, "tc.research_page.BM_STAFF_ROD.1");
        addPage(pStaff, BloodMagicRecipes.recipeStaffRod);
        new ResearchItem(
            "BM_STAFF_ROD",
            cat,
            new AspectList().add(Aspect.LIFE, 24)
                .add(Aspect.MAGIC, 20)
                .add(Aspect.ELDRITCH, 16),
            -4,
            -2,
            4,
            new ItemStack(BloodMagicItems.itemWandRodBlood, 1, 1)).setPages(toArray(pStaff))
                .setParents("BM_WAND_ROD", "BM_ALTAR_TIERS")
                .setSpecial()
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("BM_STAFF_ROD", 1);

        // 19. BM_FOCUS_SACRIFICE (Набалдашник «Жатва крови»)
        List<ResearchPage> pFocus = new ArrayList<>();
        addPage(pFocus, "tc.research_page.BM_FOCUS_SACRIFICE.1");
        addPage(pFocus, BloodMagicRecipes.recipeFocusSacrifice);
        new ResearchItem(
            "BM_FOCUS_SACRIFICE",
            cat,
            new AspectList().add(Aspect.WEAPON, 12)
                .add(Aspect.LIFE, 12)
                .add(Aspect.ENTROPY, 8),
            -4,
            -4,
            4,
            new ItemStack(BloodMagicItems.itemFocusBloodSacrifice, 1, 0)).setPages(toArray(pFocus))
                .setParents("BM_SACRIFICE", "BM_WAND_ROD")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("BM_FOCUS_SACRIFICE", 1);
    }
}
