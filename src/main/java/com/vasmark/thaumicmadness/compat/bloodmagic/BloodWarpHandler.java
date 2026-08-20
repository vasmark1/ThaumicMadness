package com.vasmark.thaumicmadness.compat.bloodmagic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import WayofTime.alchemicalWizardry.ModItems;
import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.Thaumcraft;

public class BloodWarpHandler {

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        if (event.entityPlayer == null || event.entityPlayer.worldObj.isRemote) return;

        ItemStack held = event.entityPlayer.getHeldItem();
        if (held != null && ModItems.daggerOfSacrifice != null && held.getItem() == ModItems.daggerOfSacrifice) {
            // Sacrificing entities adds dark temporary warp
            if (event.entityPlayer.getRNG()
                .nextInt(4) == 0) {
                Thaumcraft.proxy.getPlayerKnowledge()
                    .addWarpTemp(event.entityPlayer.getCommandSenderName(), 1);
            }
        }
    }

    /**
     * Purges temporary and sticky warp by syphoning LP from the player's Soul Network.
     * 
     * @return true if warp was successfully cleansed
     */
    public static boolean cleanseWarpWithBlood(EntityPlayer player, int warpPointsToCleanse) {
        if (player == null || player.worldObj.isRemote) return false;

        String owner = player.getCommandSenderName();
        int lpPerPoint = 50000;
        int requiredLP = warpPointsToCleanse * lpPerPoint;

        int currentLP = SoulNetworkHandler.getCurrentEssence(owner);
        if (currentLP >= requiredLP) {
            SoulNetworkHandler.syphonFromNetwork(owner, requiredLP);
            int tempWarp = Thaumcraft.proxy.getPlayerKnowledge()
                .getWarpTemp(owner);
            if (tempWarp > 0) {
                Thaumcraft.proxy.getPlayerKnowledge()
                    .setWarpTemp(owner, Math.max(0, tempWarp - warpPointsToCleanse));
            }
            int stickyWarp = Thaumcraft.proxy.getPlayerKnowledge()
                .getWarpSticky(owner);
            if (stickyWarp > 0) {
                Thaumcraft.proxy.getPlayerKnowledge()
                    .setWarpSticky(owner, Math.max(0, stickyWarp - warpPointsToCleanse));
            }
            return true;
        }
        return false;
    }
}
