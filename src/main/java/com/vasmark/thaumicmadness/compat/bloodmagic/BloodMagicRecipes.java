package com.vasmark.thaumicmadness.compat.bloodmagic;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import WayofTime.alchemicalWizardry.ModBlocks;
import WayofTime.alchemicalWizardry.ModItems;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionRecipe;

public class BloodMagicRecipes {

    // Altar & Slates & Knives
    public static InfusionRecipe recipeBloodAltar;
    public static IArcaneRecipe recipeSacrificialKnife;
    public static InfusionRecipe recipeDaggerOfSacrifice;
    public static IArcaneRecipe recipeBlankSlate;
    public static IArcaneRecipe recipeReinforcedSlate;
    public static InfusionRecipe recipeImbuedSlate;
    public static InfusionRecipe recipeDemonicSlate;

    // Runes
    public static IArcaneRecipe recipeBlankRune;
    public static IArcaneRecipe recipeSpeedRune;
    public static IArcaneRecipe recipeCapacityRune;
    public static InfusionRecipe recipeSacrificeRune;
    public static InfusionRecipe recipeSelfSacrificeRune;
    public static IArcaneRecipe recipeDislocationRune;

    // Blood Orbs
    public static InfusionRecipe recipeWeakBloodOrb;
    public static InfusionRecipe recipeApprenticeBloodOrb;
    public static InfusionRecipe recipeMagicianBloodOrb;
    public static InfusionRecipe recipeMasterBloodOrb;
    public static InfusionRecipe recipeArchmageBloodOrb;

    // Sigils
    public static IArcaneRecipe recipeDivinationSigil;
    public static IArcaneRecipe recipeWaterSigil;
    public static IArcaneRecipe recipeLavaSigil;
    public static IArcaneRecipe recipeAirSigil;
    public static IArcaneRecipe recipeVoidSigil;
    public static IArcaneRecipe recipeGreenGroveSigil;
    public static IArcaneRecipe recipeSigilHolding;

    // Bound Gear & Armor
    public static InfusionRecipe recipeBoundBlade;
    public static InfusionRecipe recipeBoundHelmet;
    public static InfusionRecipe recipeBoundChestplate;
    public static InfusionRecipe recipeBoundLeggings;
    public static InfusionRecipe recipeBoundBoots;

    // Sanguine Wand & Focus
    public static IArcaneRecipe recipeInertCap;
    public static InfusionRecipe recipeChargedCap;
    public static InfusionRecipe recipeWandRod;
    public static InfusionRecipe recipeStaffRod;
    public static InfusionRecipe recipeFocusSacrifice;

    // Blood Arsenal (if loaded)
    public static CrucibleRecipe recipeBloodInfusedIron;
    public static CrucibleRecipe recipeBloodInfusedDiamond;
    public static IArcaneRecipe recipeSanguineAmulet;

    public static void registerRecipes() {
        ItemStack thaumium = new ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 2);
        ItemStack salisMundus = new ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 14);
        ItemStack voidMetal = new ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 16);
        ItemStack primalCharm = new ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 15);
        ItemStack arcaneStone = new ItemStack(thaumcraft.common.config.ConfigBlocks.blockCosmeticSolid, 1, 6);

        // 1. Blood Altar (Infusion)
        recipeBloodAltar = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_ALTAR_1",
            new ItemStack(ModBlocks.blockAltar),
            4,
            new AspectList().add(Aspect.LIFE, 32)
                .add(Aspect.FLESH, 24)
                .add(Aspect.MAGIC, 24)
                .add(Aspect.HEAL, 16),
            new ItemStack(thaumcraft.common.config.ConfigBlocks.blockMetalDevice, 1, 0), // Crucible
            new ItemStack[] { thaumium, new ItemStack(Blocks.gold_block), new ItemStack(Items.diamond), thaumium,
                new ItemStack(Blocks.gold_block), new ItemStack(Items.diamond), salisMundus, salisMundus });

        // 2. Sacrificial Knife (Arcane Worktable)
        recipeSacrificialKnife = ThaumcraftApi.addArcaneCraftingRecipe(
            "BM_SACRIFICE",
            new ItemStack(ModItems.sacrificialDagger),
            new AspectList().add(Aspect.ORDER, 15)
                .add(Aspect.WATER, 15),
            " G ",
            " GT",
            "G  ",
            'G',
            new ItemStack(Blocks.glass_pane),
            'T',
            thaumium);

        // 3. Dagger of Sacrifice (Infusion)
        recipeDaggerOfSacrifice = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_SACRIFICE",
            new ItemStack(ModItems.daggerOfSacrifice),
            5,
            new AspectList().add(Aspect.DEATH, 32)
                .add(Aspect.SOUL, 32)
                .add(Aspect.WEAPON, 24),
            new ItemStack(thaumcraft.common.config.ConfigItems.itemSwordThaumium),
            new ItemStack[] { new ItemStack(Items.ender_eye), salisMundus, new ItemStack(Items.skull, 1, 0),
                new ItemStack(Items.ghast_tear) });

        // 4. Blank Slate (Arcane Worktable)
        recipeBlankSlate = ThaumcraftApi.addArcaneCraftingRecipe(
            "BM_ALTAR_1",
            new ItemStack(ModItems.blankSlate, 2),
            new AspectList().add(Aspect.EARTH, 10)
                .add(Aspect.ORDER, 10),
            " S ",
            "SCS",
            " S ",
            'S',
            new ItemStack(Blocks.stone),
            'C',
            salisMundus);

        // 5. Reinforced Slate (Arcane Worktable)
        recipeReinforcedSlate = ThaumcraftApi.addArcaneCraftingRecipe(
            "BM_ALTAR_TIERS",
            new ItemStack(ModItems.reinforcedSlate),
            new AspectList().add(Aspect.EARTH, 15)
                .add(Aspect.ORDER, 15)
                .add(Aspect.WATER, 10),
            " T ",
            "TST",
            " T ",
            'T',
            thaumium,
            'S',
            new ItemStack(ModItems.blankSlate));

        // 6. Imbued Slate (Infusion)
        recipeImbuedSlate = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_ALTAR_TIERS",
            new ItemStack(ModItems.imbuedSlate),
            4,
            new AspectList().add(Aspect.MAGIC, 16)
                .add(Aspect.SOUL, 16)
                .add(Aspect.CRYSTAL, 12),
            new ItemStack(ModItems.reinforcedSlate),
            new ItemStack[] { new ItemStack(Items.diamond),
                new ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 6), // Amber
                new ItemStack(Items.glowstone_dust), new ItemStack(Items.glowstone_dust) });

        // 7. Demonic Slate (Infusion)
        recipeDemonicSlate = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_DEMONOLOGY",
            new ItemStack(ModItems.demonicSlate),
            6,
            new AspectList().add(Aspect.ELDRITCH, 32)
                .add(Aspect.SOUL, 32)
                .add(Aspect.DARKNESS, 24),
            new ItemStack(ModItems.imbuedSlate),
            new ItemStack[] {
                new ItemStack(ModItems.demonBloodShard != null ? ModItems.demonBloodShard : Items.nether_star),
                new ItemStack(Items.nether_star), voidMetal, voidMetal });

        // 8. Blank Rune (Arcane Worktable)
        recipeBlankRune = ThaumcraftApi.addArcaneCraftingRecipe(
            "BM_RUNES",
            new ItemStack(ModBlocks.bloodRune, 1, 0),
            new AspectList().add(Aspect.EARTH, 10)
                .add(Aspect.ORDER, 10),
            "ASA",
            "SCS",
            "ASA",
            'A',
            arcaneStone,
            'S',
            new ItemStack(ModItems.blankSlate),
            'C',
            salisMundus);

        // 9. Speed Rune (Arcane Worktable)
        recipeSpeedRune = ThaumcraftApi.addArcaneCraftingRecipe(
            "BM_RUNES",
            new ItemStack(ModBlocks.speedRune),
            new AspectList().add(Aspect.AIR, 15)
                .add(Aspect.ORDER, 15),
            "BQB",
            "QRQ",
            "BQB",
            'B',
            new ItemStack(ModItems.blankSlate),
            'Q',
            new ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 3), // Quicksilver
            'R',
            new ItemStack(ModBlocks.bloodRune, 1, 0));

        // 10. Capacity Rune (Arcane Worktable)
        recipeCapacityRune = ThaumcraftApi.addArcaneCraftingRecipe(
            "BM_RUNES",
            new ItemStack(ModBlocks.efficiencyRune),
            new AspectList().add(Aspect.WATER, 15)
                .add(Aspect.EARTH, 15),
            "BJB",
            "JRJ",
            "BJB",
            'B',
            new ItemStack(ModItems.reinforcedSlate),
            'J',
            new ItemStack(thaumcraft.common.config.ConfigBlocks.blockJar),
            'R',
            new ItemStack(ModBlocks.bloodRune, 1, 0));

        // 11. Sacrifice Rune (Infusion)
        recipeSacrificeRune = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_RUNES",
            new ItemStack(ModBlocks.runeOfSacrifice),
            4,
            new AspectList().add(Aspect.LIFE, 16)
                .add(Aspect.FLESH, 16)
                .add(Aspect.WEAPON, 12),
            new ItemStack(ModBlocks.bloodRune, 1, 0),
            new ItemStack[] { new ItemStack(ModItems.reinforcedSlate), new ItemStack(Items.golden_apple), thaumium,
                new ItemStack(Items.speckled_melon) });

        // 12. Self-Sacrifice Rune (Infusion)
        recipeSelfSacrificeRune = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_RUNES",
            new ItemStack(ModBlocks.runeOfSelfSacrifice),
            4,
            new AspectList().add(Aspect.LIFE, 16)
                .add(Aspect.HEAL, 16)
                .add(Aspect.HEAL, 12),
            new ItemStack(ModBlocks.bloodRune, 1, 0),
            new ItemStack[] { new ItemStack(ModItems.reinforcedSlate), new ItemStack(Items.ghast_tear), thaumium,
                new ItemStack(Items.golden_apple) });

        // 13. Dislocation Rune (Arcane Worktable)
        recipeDislocationRune = ThaumcraftApi.addArcaneCraftingRecipe(
            "BM_RUNES",
            new ItemStack(ModBlocks.bloodRune, 1, 4),
            new AspectList().add(Aspect.WATER, 15)
                .add(Aspect.ORDER, 15),
            "BTB",
            "TRT",
            "BTB",
            'B',
            new ItemStack(ModItems.reinforcedSlate),
            'T',
            new ItemStack(thaumcraft.common.config.ConfigBlocks.blockTube),
            'R',
            new ItemStack(ModBlocks.bloodRune, 1, 0));

        // 14. Weak Blood Orb (Infusion)
        recipeWeakBloodOrb = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_SOUL_NETWORK",
            new ItemStack(ModItems.weakBloodOrb),
            3,
            new AspectList().add(Aspect.SOUL, 16)
                .add(Aspect.LIFE, 16)
                .add(Aspect.CRYSTAL, 8),
            new ItemStack(Items.diamond),
            new ItemStack[] { salisMundus, new ItemStack(Items.redstone), new ItemStack(Items.redstone), salisMundus });

        // 15. Apprentice Blood Orb (Infusion)
        recipeApprenticeBloodOrb = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_SOUL_NETWORK",
            new ItemStack(ModItems.apprenticeBloodOrb),
            4,
            new AspectList().add(Aspect.SOUL, 32)
                .add(Aspect.LIFE, 32)
                .add(Aspect.CRYSTAL, 16),
            new ItemStack(ModItems.weakBloodOrb),
            new ItemStack[] { new ItemStack(Blocks.redstone_block), new ItemStack(Blocks.redstone_block),
                new ItemStack(Items.gold_ingot), new ItemStack(Items.gold_ingot) });

        // 16. Magician's Blood Orb (Infusion)
        recipeMagicianBloodOrb = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_SOUL_NETWORK",
            new ItemStack(ModItems.magicianBloodOrb),
            5,
            new AspectList().add(Aspect.SOUL, 48)
                .add(Aspect.LIFE, 48)
                .add(Aspect.MAGIC, 24),
            new ItemStack(ModItems.apprenticeBloodOrb),
            new ItemStack[] { new ItemStack(Items.emerald),
                new ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 6), // Amber
                new ItemStack(Items.diamond), new ItemStack(Items.diamond) });

        // 17. Master Blood Orb (Infusion)
        recipeMasterBloodOrb = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_SOUL_NETWORK",
            new ItemStack(ModItems.masterBloodOrb),
            6,
            new AspectList().add(Aspect.SOUL, 64)
                .add(Aspect.LIFE, 64)
                .add(Aspect.ELDRITCH, 32),
            new ItemStack(ModItems.magicianBloodOrb),
            new ItemStack[] { new ItemStack(Items.nether_star), thaumium, thaumium, salisMundus });

        // 18. Archmage Blood Orb (Infusion)
        recipeArchmageBloodOrb = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_SOUL_NETWORK",
            new ItemStack(ModItems.archmageBloodOrb),
            7,
            new AspectList().add(Aspect.SOUL, 96)
                .add(Aspect.LIFE, 96)
                .add(Aspect.ELDRITCH, 48),
            new ItemStack(ModItems.masterBloodOrb),
            new ItemStack[] { voidMetal, primalCharm, new ItemStack(Items.nether_star), voidMetal });

        // 19. Divination Sigil (Arcane Worktable)
        recipeDivinationSigil = ThaumcraftApi.addArcaneCraftingRecipe(
            "BM_SIGILS_BASIC",
            new ItemStack(ModItems.divinationSigil),
            new AspectList().add(Aspect.ORDER, 10)
                .add(Aspect.AIR, 10),
            " S ",
            "SAS",
            " S ",
            'S',
            new ItemStack(ModItems.blankSlate),
            'A',
            new ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 6) // Amber
        );

        // 20. Water Sigil (Arcane Worktable)
        recipeWaterSigil = ThaumcraftApi.addArcaneCraftingRecipe(
            "BM_SIGILS_BASIC",
            new ItemStack(ModItems.waterSigil),
            new AspectList().add(Aspect.WATER, 20),
            " W ",
            "WSW",
            " W ",
            'S',
            new ItemStack(ModItems.reinforcedSlate),
            'W',
            new ItemStack(Items.water_bucket));

        // 21. Lava Sigil (Arcane Worktable)
        recipeLavaSigil = ThaumcraftApi.addArcaneCraftingRecipe(
            "BM_SIGILS_BASIC",
            new ItemStack(ModItems.lavaSigil),
            new AspectList().add(Aspect.FIRE, 20)
                .add(Aspect.EARTH, 10),
            " L ",
            "LSL",
            " L ",
            'S',
            new ItemStack(ModItems.reinforcedSlate),
            'L',
            new ItemStack(Items.lava_bucket));

        // 22. Air Sigil (Arcane Worktable)
        recipeAirSigil = ThaumcraftApi.addArcaneCraftingRecipe(
            "BM_SIGILS_BASIC",
            new ItemStack(ModItems.airSigil),
            new AspectList().add(Aspect.AIR, 20),
            " F ",
            "FSF",
            " F ",
            'S',
            new ItemStack(ModItems.reinforcedSlate),
            'F',
            new ItemStack(Items.feather));

        // 23. Void Sigil (Arcane Worktable)
        recipeVoidSigil = ThaumcraftApi.addArcaneCraftingRecipe(
            "BM_SIGILS_BASIC",
            new ItemStack(ModItems.voidSigil),
            new AspectList().add(Aspect.ENTROPY, 20),
            " B ",
            "BSB",
            " B ",
            'S',
            new ItemStack(ModItems.reinforcedSlate),
            'B',
            new ItemStack(Items.bucket));

        // 24. Green Grove Sigil (Arcane Worktable)
        recipeGreenGroveSigil = ThaumcraftApi.addArcaneCraftingRecipe(
            "BM_SIGILS_ADVANCED",
            new ItemStack(ModItems.growthSigil),
            new AspectList().add(Aspect.EARTH, 20)
                .add(Aspect.WATER, 20),
            " B ",
            "ISI",
            " B ",
            'S',
            new ItemStack(ModItems.imbuedSlate),
            'B',
            new ItemStack(Items.dye, 1, 15), // Bone Meal
            'I',
            new ItemStack(Blocks.sapling, 1, 0));

        // 25. Sigil of Holding (Arcane Worktable)
        recipeSigilHolding = ThaumcraftApi.addArcaneCraftingRecipe(
            "BM_SIGILS_ADVANCED",
            new ItemStack(ModItems.sigilOfHolding),
            new AspectList().add(Aspect.ORDER, 25)
                .add(Aspect.ENTROPY, 25),
            " C ",
            "ISI",
            " C ",
            'S',
            new ItemStack(ModItems.imbuedSlate),
            'C',
            new ItemStack(Blocks.chest),
            'I',
            thaumium);

        // 26. Bound Blade (Infusion)
        recipeBoundBlade = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_BOUND_GEAR",
            new ItemStack(ModItems.energySword),
            6,
            new AspectList().add(Aspect.WEAPON, 32)
                .add(Aspect.SOUL, 32)
                .add(Aspect.LIFE, 24),
            new ItemStack(thaumcraft.common.config.ConfigItems.itemSwordThaumium),
            new ItemStack[] { new ItemStack(ModItems.masterBloodOrb), new ItemStack(Items.diamond),
                new ItemStack(Items.diamond), salisMundus });

        // 27. Bound Armor (Infusion)
        recipeBoundHelmet = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_BOUND_GEAR",
            new ItemStack(ModItems.boundHelmet),
            6,
            new AspectList().add(Aspect.ARMOR, 32)
                .add(Aspect.SOUL, 32)
                .add(Aspect.LIFE, 24),
            new ItemStack(thaumcraft.common.config.ConfigItems.itemHelmetThaumium),
            new ItemStack[] { new ItemStack(ModItems.masterBloodOrb), new ItemStack(Items.diamond), thaumium,
                salisMundus });

        recipeBoundChestplate = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_BOUND_GEAR",
            new ItemStack(ModItems.boundPlate),
            6,
            new AspectList().add(Aspect.ARMOR, 48)
                .add(Aspect.SOUL, 48)
                .add(Aspect.LIFE, 32),
            new ItemStack(thaumcraft.common.config.ConfigItems.itemChestThaumium),
            new ItemStack[] { new ItemStack(ModItems.masterBloodOrb), new ItemStack(Items.diamond),
                new ItemStack(Items.diamond), thaumium, thaumium, salisMundus });

        recipeBoundLeggings = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_BOUND_GEAR",
            new ItemStack(ModItems.boundLeggings),
            6,
            new AspectList().add(Aspect.ARMOR, 40)
                .add(Aspect.SOUL, 40)
                .add(Aspect.LIFE, 28),
            new ItemStack(thaumcraft.common.config.ConfigItems.itemLegsThaumium),
            new ItemStack[] { new ItemStack(ModItems.masterBloodOrb), new ItemStack(Items.diamond), thaumium,
                salisMundus });

        recipeBoundBoots = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_BOUND_GEAR",
            new ItemStack(ModItems.boundBoots),
            6,
            new AspectList().add(Aspect.ARMOR, 24)
                .add(Aspect.SOUL, 24)
                .add(Aspect.LIFE, 16),
            new ItemStack(thaumcraft.common.config.ConfigItems.itemBootsThaumium),
            new ItemStack[] { new ItemStack(ModItems.masterBloodOrb), new ItemStack(Items.diamond), thaumium,
                salisMundus });

        // 28. Sanguine Wand & Focus Recipes
        recipeInertCap = ThaumcraftApi.addArcaneCraftingRecipe(
            "BM_WAND_CAP",
            new ItemStack(BloodMagicItems.itemWandCapBlood, 1, 1),
            new AspectList().add(Aspect.ORDER, 15)
                .add(Aspect.EARTH, 15),
            " T ",
            "T T",
            'T',
            thaumium);

        recipeChargedCap = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_WAND_CAP",
            new ItemStack(BloodMagicItems.itemWandCapBlood, 1, 0),
            4,
            new AspectList().add(Aspect.LIFE, 16)
                .add(Aspect.MAGIC, 12)
                .add(Aspect.ORDER, 8),
            new ItemStack(BloodMagicItems.itemWandCapBlood, 1, 1),
            new ItemStack[] { new ItemStack(ModItems.apprenticeBloodOrb), salisMundus,
                new ItemStack(Items.speckled_melon), new ItemStack(Items.ghast_tear) });

        recipeWandRod = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_WAND_ROD",
            new ItemStack(BloodMagicItems.itemWandRodBlood, 1, 0),
            5,
            new AspectList().add(Aspect.LIFE, 32)
                .add(Aspect.MAGIC, 24)
                .add(Aspect.SOUL, 16)
                .add(Aspect.HEAL, 12),
            new ItemStack(thaumcraft.common.config.ConfigItems.itemWandRod, 1, 0), // Greatwood Wand Rod
            new ItemStack[] { new ItemStack(ModItems.magicianBloodOrb), salisMundus, new ItemStack(ModItems.blankSlate),
                new ItemStack(ModItems.reinforcedSlate), new ItemStack(Items.golden_apple),
                new ItemStack(Items.nether_star) });

        recipeStaffRod = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_STAFF_ROD",
            new ItemStack(BloodMagicItems.itemWandRodBlood, 1, 1),
            7,
            new AspectList().add(Aspect.LIFE, 64)
                .add(Aspect.MAGIC, 48)
                .add(Aspect.SOUL, 32)
                .add(Aspect.ELDRITCH, 24),
            new ItemStack(BloodMagicItems.itemWandRodBlood, 1, 0),
            new ItemStack[] { new ItemStack(ModItems.masterBloodOrb), new ItemStack(ModItems.demonicSlate), primalCharm,
                primalCharm, new ItemStack(ModItems.imbuedSlate), new ItemStack(Items.nether_star) });

        recipeFocusSacrifice = ThaumcraftApi.addInfusionCraftingRecipe(
            "BM_FOCUS_SACRIFICE",
            new ItemStack(BloodMagicItems.itemFocusBloodSacrifice, 1, 0),
            5,
            new AspectList().add(Aspect.WEAPON, 24)
                .add(Aspect.LIFE, 24)
                .add(Aspect.ENTROPY, 16)
                .add(Aspect.DEATH, 16),
            new ItemStack(thaumcraft.common.config.ConfigItems.itemFocusExcavation),
            new ItemStack[] { new ItemStack(ModItems.sacrificialDagger), new ItemStack(ModItems.magicianBloodOrb),
                new ItemStack(Items.quartz), new ItemStack(Items.ender_pearl) });

        // 29. Blood Arsenal Recipes (if loaded)
        if (BloodMagicCompat.isBloodArsenalLoaded) {
            registerBloodArsenalRecipes();
        }
    }

    private static void registerBloodArsenalRecipes() {
        try {
            if (com.arc.bloodarsenal.common.items.ModItems.blood_infused_iron != null) {
                recipeBloodInfusedIron = ThaumcraftApi.addCrucibleRecipe(
                    "BM_ARSENAL",
                    new ItemStack(com.arc.bloodarsenal.common.items.ModItems.blood_infused_iron),
                    new ItemStack(Items.iron_ingot),
                    new AspectList().add(Aspect.LIFE, 8)
                        .add(Aspect.FLESH, 8));
            }
            if (com.arc.bloodarsenal.common.items.ModItems.blood_diamond != null) {
                recipeBloodInfusedDiamond = ThaumcraftApi.addCrucibleRecipe(
                    "BM_ARSENAL",
                    new ItemStack(com.arc.bloodarsenal.common.items.ModItems.blood_diamond),
                    new ItemStack(Items.diamond),
                    new AspectList().add(Aspect.LIFE, 16)
                        .add(Aspect.SOUL, 16));
            }
            if (com.arc.bloodarsenal.common.items.ModItems.sacrifice_amulet != null) {
                recipeSanguineAmulet = ThaumcraftApi.addArcaneCraftingRecipe(
                    "BM_ARSENAL",
                    new ItemStack(com.arc.bloodarsenal.common.items.ModItems.sacrifice_amulet),
                    new AspectList().add(Aspect.ORDER, 20)
                        .add(Aspect.WATER, 20),
                    " S ",
                    "SAS",
                    " S ",
                    'S',
                    new ItemStack(ModItems.reinforcedSlate),
                    'A',
                    new ItemStack(thaumcraft.common.config.ConfigItems.itemBaubleBlanks, 1, 0));
            }
        } catch (Throwable t) {
            BloodMagicCompat.LOGGER.warn("Failed to register Blood Arsenal recipes: ", t);
        }
    }
}
