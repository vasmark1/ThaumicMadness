package com.vasmark.thaumicmadness.compat.baubles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Loader;

/**
 * Compatibility bridge for Baubles and Baubles-Expanded (GTNH).
 */
public class BaublesCompat {

    private static final Logger LOG = LogManager.getLogger("ThaumicMadness/BaublesCompat");

    private static boolean baublesLoaded = false;
    private static boolean baublesExpandedLoaded = false;

    public static void init() {
        baublesLoaded = Loader.isModLoaded("Baubles");
        if (baublesLoaded) {
            try {
                Class.forName("baubles.api.expanded.IBaubleExpanded");
                baublesExpandedLoaded = true;
            } catch (ClassNotFoundException e) {
                baublesExpandedLoaded = false;
            }
        }

        LOG.info("Baubles Ecosystem Status:");
        LOG.info(" - Baubles: {}", baublesLoaded ? "DETECTED" : "Not present");
        LOG.info(
            " - Baubles-Expanded (GTNH 20-slot): {}",
            baublesExpandedLoaded ? "DETECTED (Multi-slot API active)" : "Not present (Legacy 4-slot mode)");
    }

    public static boolean isBaublesLoaded() {
        return baublesLoaded;
    }

    public static boolean isBaublesExpandedLoaded() {
        return baublesExpandedLoaded;
    }

    public static IInventory getBaublesInventory(EntityPlayer player) {
        if (!baublesLoaded || player == null) return null;
        try {
            return baubles.api.BaublesApi.getBaubles(player);
        } catch (Throwable t) {
            return null;
        }
    }

    public static boolean isItemEquipped(EntityPlayer player, Item item) {
        IInventory inv = getBaublesInventory(player);
        if (inv == null || item == null) return false;
        int size = inv.getSizeInventory();
        for (int i = 0; i < size; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && stack.getItem() == item) {
                return true;
            }
        }
        return false;
    }
}
