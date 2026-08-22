package com.vasmark.thaumicmadness.compat.nei;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import com.vasmark.thaumicmadness.ModBlocks;
import com.vasmark.thaumicmadness.Tags;
import com.vasmark.thaumicmadness.compact.infusion.GuiCompactInfusionMatrix;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NEICompactInfusionConfig implements IConfigureNEI, INEIGuiHandler {

    @Override
    public void loadConfig() {
        if (ModBlocks.compactInfusionMatrix != null) {
            API.addRecipeCatalyst(new ItemStack(ModBlocks.compactInfusionMatrix), "infusionCrafting");
        }

        API.registerGuiOverlay(GuiCompactInfusionMatrix.class, "infusionCrafting");
        API.registerGuiOverlayHandler(
            GuiCompactInfusionMatrix.class,
            new codechicken.nei.recipe.DefaultOverlayHandler(),
            "infusionCrafting");
        API.registerNEIGuiHandler(this);

        // Compact Infernal Furnace NEI Integration
        if (ModBlocks.compactInfernalFurnace != null) {
            API.addRecipeCatalyst(new ItemStack(ModBlocks.compactInfernalFurnace), "compact_infernal_furnace");
            API.addRecipeCatalyst(new ItemStack(ModBlocks.compactInfernalFurnace), "smelting");
        }

        NEICompactFurnaceRecipeHandler furnaceHandler = new NEICompactFurnaceRecipeHandler();
        API.registerRecipeHandler(furnaceHandler);
        API.registerUsageHandler(furnaceHandler);

        API.registerGuiOverlay(
            com.vasmark.thaumicmadness.compact.furnace.GuiCompactInfernalFurnace.class,
            "compact_infernal_furnace");
        API.registerGuiOverlay(com.vasmark.thaumicmadness.compact.furnace.GuiCompactInfernalFurnace.class, "smelting");
        API.registerGuiOverlayHandler(
            com.vasmark.thaumicmadness.compact.furnace.GuiCompactInfernalFurnace.class,
            new codechicken.nei.recipe.DefaultOverlayHandler(),
            "compact_infernal_furnace");
    }

    @Override
    public String getName() {
        return "Thaumic Madness: Compact Machines";
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }

    // --- INEIGuiHandler Implementation ---
    @Override
    public VisiblityData modifyVisiblity(GuiContainer gui, VisiblityData currentVisibility) {
        return currentVisibility;
    }

    @Override
    public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item) {
        return null;
    }

    @Override
    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui) {
        return null;
    }

    @Override
    public boolean handleDragNDrop(GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button) {
        return false;
    }

    @Override
    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h) {
        return false;
    }
}
