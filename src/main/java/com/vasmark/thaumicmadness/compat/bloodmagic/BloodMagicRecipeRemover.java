package com.vasmark.thaumicmadness.compat.bloodmagic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

import WayofTime.alchemicalWizardry.ModBlocks;
import WayofTime.alchemicalWizardry.ModItems;
import WayofTime.alchemicalWizardry.api.altarRecipeRegistry.AltarRecipe;
import WayofTime.alchemicalWizardry.api.altarRecipeRegistry.AltarRecipeRegistry;
import WayofTime.alchemicalWizardry.api.bindingRegistry.BindingRecipe;
import WayofTime.alchemicalWizardry.api.bindingRegistry.BindingRegistry;

public class BloodMagicRecipeRemover {

    private static final Set<Item> PORTED_ITEMS = new HashSet<>();

    private static void registerPortedItem(Item item) {
        if (item != null) {
            PORTED_ITEMS.add(item);
        }
    }

    private static void registerPortedBlock(Block block) {
        if (block != null) {
            Item item = Item.getItemFromBlock(block);
            if (item != null) {
                PORTED_ITEMS.add(item);
            }
        }
    }

    public static void removeDuplicateRecipes() {
        if (!com.vasmark.thaumicmadness.Config.disableDuplicateBMRecipes) return;
        if (!BloodMagicCompat.isBloodMagicLoaded) return;

        // Register all items/blocks ported into Thaumcraft recipes
        registerPortedBlock(ModBlocks.blockAltar);
        registerPortedBlock(ModBlocks.bloodRune);
        registerPortedBlock(ModBlocks.speedRune);
        registerPortedBlock(ModBlocks.efficiencyRune);
        registerPortedBlock(ModBlocks.runeOfSacrifice);
        registerPortedBlock(ModBlocks.runeOfSelfSacrifice);

        registerPortedItem(ModItems.sacrificialDagger);
        registerPortedItem(ModItems.daggerOfSacrifice);
        registerPortedItem(ModItems.blankSlate);
        registerPortedItem(ModItems.reinforcedSlate);
        registerPortedItem(ModItems.imbuedSlate);
        registerPortedItem(ModItems.demonicSlate);

        registerPortedItem(ModItems.weakBloodOrb);
        registerPortedItem(ModItems.apprenticeBloodOrb);
        registerPortedItem(ModItems.magicianBloodOrb);
        registerPortedItem(ModItems.masterBloodOrb);
        registerPortedItem(ModItems.archmageBloodOrb);

        registerPortedItem(ModItems.divinationSigil);
        registerPortedItem(ModItems.waterSigil);
        registerPortedItem(ModItems.lavaSigil);
        registerPortedItem(ModItems.airSigil);
        registerPortedItem(ModItems.voidSigil);
        registerPortedItem(ModItems.growthSigil);
        registerPortedItem(ModItems.sigilOfHolding);

        registerPortedItem(ModItems.energySword);
        registerPortedItem(ModItems.boundHelmet);
        registerPortedItem(ModItems.boundPlate);
        registerPortedItem(ModItems.boundLeggings);
        registerPortedItem(ModItems.boundBoots);

        if (BloodMagicCompat.isBloodArsenalLoaded) {
            registerBloodArsenalItems();
        }

        int removedCrafting = removeCraftingRecipes();
        int removedAltar = removeAltarRecipes();
        int removedBinding = removeBindingRecipes();

        BloodMagicCompat.LOGGER.info(
            "Disabled duplicated original Blood Magic recipes: {} Crafting Table, {} Blood Altar, {} Binding Ritual.",
            removedCrafting,
            removedAltar,
            removedBinding);
    }

    private static void registerBloodArsenalItems() {
        try {
            registerPortedItem(com.arc.bloodarsenal.common.items.ModItems.blood_infused_iron);
            registerPortedItem(com.arc.bloodarsenal.common.items.ModItems.blood_diamond);
            registerPortedItem(com.arc.bloodarsenal.common.items.ModItems.sacrifice_amulet);
            registerPortedItem(com.arc.bloodarsenal.common.items.ModItems.self_sacrifice_amulet);
        } catch (Throwable ignored) {}
    }

    private static int removeCraftingRecipes() {
        int count = 0;
        List<?> recipes = CraftingManager.getInstance()
            .getRecipeList();
        Iterator<?> iterator = recipes.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof IRecipe) {
                ItemStack output = ((IRecipe) obj).getRecipeOutput();
                if (output != null && output.getItem() != null && PORTED_ITEMS.contains(output.getItem())) {
                    iterator.remove();
                    count++;
                }
            }
        }
        return count;
    }

    private static int removeAltarRecipes() {
        int count = 0;
        if (AltarRecipeRegistry.altarRecipes != null) {
            Iterator<AltarRecipe> iterator = AltarRecipeRegistry.altarRecipes.iterator();
            while (iterator.hasNext()) {
                AltarRecipe recipe = iterator.next();
                if (recipe != null) {
                    ItemStack output = recipe.getResult();
                    if (output != null && output.getItem() != null && PORTED_ITEMS.contains(output.getItem())) {
                        iterator.remove();
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private static int removeBindingRecipes() {
        int count = 0;
        if (BindingRegistry.bindingRecipes != null) {
            Iterator<BindingRecipe> iterator = BindingRegistry.bindingRecipes.iterator();
            while (iterator.hasNext()) {
                BindingRecipe recipe = iterator.next();
                if (recipe != null) {
                    ItemStack output = recipe.getResult();
                    if (output != null && output.getItem() != null && PORTED_ITEMS.contains(output.getItem())) {
                        iterator.remove();
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
