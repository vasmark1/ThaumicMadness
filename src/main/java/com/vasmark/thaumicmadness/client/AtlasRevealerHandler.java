package com.vasmark.thaumicmadness.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.vasmark.thaumicmadness.item.ItemThaumonomiconAtlas;

public class AtlasRevealerHandler {

    public static boolean hasThaumonomiconAtlas(EntityPlayer player) {
        if (!com.vasmark.thaumicmadness.Config.enableAtlas
            || !com.vasmark.thaumicmadness.Config.enableAtlasPassiveRevealing) return false;
        if (player == null || player.inventory == null) return false;

        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() instanceof ItemThaumonomiconAtlas) {
                return true;
            }
        }

        try {
            net.minecraft.inventory.IInventory baublesInv = baubles.api.BaublesApi.getBaubles(player);
            if (baublesInv != null) {
                for (int i = 0; i < baublesInv.getSizeInventory(); i++) {
                    ItemStack stack = baublesInv.getStackInSlot(i);
                    if (stack != null && stack.getItem() instanceof ItemThaumonomiconAtlas) {
                        return true;
                    }
                }
            }
        } catch (Throwable ignored) {}

        return false;
    }
}
