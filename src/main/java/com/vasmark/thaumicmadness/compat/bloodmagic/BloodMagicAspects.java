package com.vasmark.thaumicmadness.compat.bloodmagic;

import net.minecraft.item.ItemStack;

import WayofTime.alchemicalWizardry.ModBlocks;
import WayofTime.alchemicalWizardry.ModItems;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class BloodMagicAspects {

    public static void registerAspects() {
        // Blood Altar & Runes
        if (ModBlocks.blockAltar != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModBlocks.blockAltar),
                new AspectList().add(Aspect.LIFE, 6)
                    .add(Aspect.MAGIC, 6)
                    .add(Aspect.HEAL, 4)
                    .add(Aspect.EARTH, 4));
        }
        if (ModBlocks.bloodRune != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModBlocks.bloodRune, 1, 32767),
                new AspectList().add(Aspect.LIFE, 4)
                    .add(Aspect.MAGIC, 4)
                    .add(Aspect.EARTH, 2));
        }

        // Ritual Blocks
        if (ModBlocks.blockMasterStone != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModBlocks.blockMasterStone),
                new AspectList().add(Aspect.MAGIC, 8)
                    .add(Aspect.LIFE, 8)
                    .add(Aspect.EARTH, 4)
                    .add(Aspect.ORDER, 4));
        }
        if (ModBlocks.ritualStone != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModBlocks.ritualStone, 1, 32767),
                new AspectList().add(Aspect.MAGIC, 4)
                    .add(Aspect.EARTH, 4)
                    .add(Aspect.ORDER, 2));
        }

        // Knives
        if (ModItems.sacrificialDagger != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModItems.sacrificialDagger, 1, 32767),
                new AspectList().add(Aspect.WEAPON, 4)
                    .add(Aspect.LIFE, 4)
                    .add(Aspect.FLESH, 2)
                    .add(Aspect.DEATH, 2));
        }
        if (ModItems.daggerOfSacrifice != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModItems.daggerOfSacrifice, 1, 32767),
                new AspectList().add(Aspect.WEAPON, 6)
                    .add(Aspect.DEATH, 6)
                    .add(Aspect.SOUL, 4)
                    .add(Aspect.MAGIC, 4));
        }

        // Blood Orbs
        if (ModItems.weakBloodOrb != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModItems.weakBloodOrb, 1, 32767),
                new AspectList().add(Aspect.SOUL, 4)
                    .add(Aspect.LIFE, 4)
                    .add(Aspect.CRYSTAL, 2));
        }
        if (ModItems.apprenticeBloodOrb != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModItems.apprenticeBloodOrb, 1, 32767),
                new AspectList().add(Aspect.SOUL, 8)
                    .add(Aspect.LIFE, 8)
                    .add(Aspect.CRYSTAL, 4));
        }
        if (ModItems.magicianBloodOrb != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModItems.magicianBloodOrb, 1, 32767),
                new AspectList().add(Aspect.SOUL, 12)
                    .add(Aspect.LIFE, 12)
                    .add(Aspect.MAGIC, 6));
        }
        if (ModItems.masterBloodOrb != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModItems.masterBloodOrb, 1, 32767),
                new AspectList().add(Aspect.SOUL, 16)
                    .add(Aspect.LIFE, 16)
                    .add(Aspect.MAGIC, 8)
                    .add(Aspect.ELDRITCH, 4));
        }
        if (ModItems.archmageBloodOrb != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModItems.archmageBloodOrb, 1, 32767),
                new AspectList().add(Aspect.SOUL, 20)
                    .add(Aspect.LIFE, 20)
                    .add(Aspect.MAGIC, 12)
                    .add(Aspect.ELDRITCH, 8));
        }
        if (ModItems.transcendentBloodOrb != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModItems.transcendentBloodOrb, 1, 32767),
                new AspectList().add(Aspect.SOUL, 25)
                    .add(Aspect.LIFE, 25)
                    .add(Aspect.MAGIC, 16)
                    .add(Aspect.ELDRITCH, 12));
        }

        // Sigils
        if (ModItems.divinationSigil != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModItems.divinationSigil, 1, 32767),
                new AspectList().add(Aspect.SENSES, 6)
                    .add(Aspect.LIFE, 4)
                    .add(Aspect.MAGIC, 2));
        }
        if (ModItems.waterSigil != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModItems.waterSigil, 1, 32767),
                new AspectList().add(Aspect.WATER, 8)
                    .add(Aspect.LIFE, 4));
        }
        if (ModItems.lavaSigil != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModItems.lavaSigil, 1, 32767),
                new AspectList().add(Aspect.FIRE, 8)
                    .add(Aspect.EARTH, 4)
                    .add(Aspect.LIFE, 4));
        }
        if (ModItems.airSigil != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModItems.airSigil, 1, 32767),
                new AspectList().add(Aspect.AIR, 8)
                    .add(Aspect.FLIGHT, 4)
                    .add(Aspect.LIFE, 4));
        }
        if (ModItems.voidSigil != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModItems.voidSigil, 1, 32767),
                new AspectList().add(Aspect.VOID, 8)
                    .add(Aspect.ENTROPY, 4)
                    .add(Aspect.LIFE, 4));
        }
        if (ModItems.growthSigil != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModItems.growthSigil, 1, 32767),
                new AspectList().add(Aspect.PLANT, 8)
                    .add(Aspect.HARVEST, 4)
                    .add(Aspect.LIFE, 4));
        }

        // Custom Sanguine Wand & Focus Aspects
        ThaumcraftApi.registerObjectTag(
            new ItemStack(BloodMagicItems.itemWandRodBlood, 1, 0),
            new AspectList().add(Aspect.LIFE, 12)
                .add(Aspect.MAGIC, 8)
                .add(Aspect.TREE, 4));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(BloodMagicItems.itemWandRodBlood, 1, 1),
            new AspectList().add(Aspect.LIFE, 20)
                .add(Aspect.MAGIC, 16)
                .add(Aspect.TREE, 6)
                .add(Aspect.ELDRITCH, 4));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(BloodMagicItems.itemWandCapBlood, 1, 0),
            new AspectList().add(Aspect.LIFE, 8)
                .add(Aspect.MAGIC, 6)
                .add(Aspect.METAL, 4));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(BloodMagicItems.itemWandCapBlood, 1, 1),
            new AspectList().add(Aspect.METAL, 4)
                .add(Aspect.ORDER, 2));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(BloodMagicItems.itemFocusBloodSacrifice, 1, 0),
            new AspectList().add(Aspect.WEAPON, 8)
                .add(Aspect.LIFE, 10)
                .add(Aspect.MAGIC, 6)
                .add(Aspect.ENTROPY, 4));

        // Blood Arsenal Aspects (if loaded)
        if (BloodMagicCompat.isBloodArsenalLoaded) {
            registerBloodArsenalAspects();
        }
    }

    private static void registerBloodArsenalAspects() {
        if (com.arc.bloodarsenal.common.block.ModBlocks.blood_infused_iron_block != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(com.arc.bloodarsenal.common.block.ModBlocks.blood_infused_iron_block),
                new AspectList().add(Aspect.METAL, 8)
                    .add(Aspect.LIFE, 6));
        }
        if (com.arc.bloodarsenal.common.items.ModItems.blood_infused_iron != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(com.arc.bloodarsenal.common.items.ModItems.blood_infused_iron),
                new AspectList().add(Aspect.METAL, 4)
                    .add(Aspect.LIFE, 3));
        }
        if (com.arc.bloodarsenal.common.items.ModItems.blood_diamond != null) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(com.arc.bloodarsenal.common.items.ModItems.blood_diamond),
                new AspectList().add(Aspect.CRYSTAL, 4)
                    .add(Aspect.LIFE, 4)
                    .add(Aspect.GREED, 2));
        }
    }
}
