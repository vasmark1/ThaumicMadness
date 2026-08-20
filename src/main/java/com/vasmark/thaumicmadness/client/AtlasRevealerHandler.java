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

        return false;
    }
}
