package com.vasmark.thaumicmadness;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearch;
import thaumcraft.common.lib.research.ResearchManager;

public class ResearchSyncEventHandler {

    public static void init() {
        FMLCommonHandler.instance()
            .bus()
            .register(new ResearchSyncEventHandler());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        syncMirroredResearch(event.player);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        syncMirroredResearch(event.player);
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        syncMirroredResearch(event.player);
    }

    public static void syncMirroredResearch(EntityPlayer player) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) return;
        if (!(player instanceof EntityPlayerMP)) return;

        String username = player.getCommandSenderName();
        ResearchCategoryList catList = ResearchCategories.getResearchList(ThaumcraftCompat.CATEGORY_KEY);
        if (catList == null || catList.research == null) return;

        boolean anyUpdated = false;

        for (ResearchItem ri : catList.research.values()) {
            if (ri == null || ri.key == null) continue;

            // 1. If this node has sibling mirrors, sync status
            if (ri.siblings != null && ri.siblings.length > 0) {
                for (String siblingKey : ri.siblings) {
                    if (siblingKey == null) continue;
                    if (ResearchManager.isResearchComplete(username, siblingKey)
                        && !ResearchManager.isResearchComplete(username, ri.key)) {
                        Thaumcraft.proxy.getResearchManager()
                            .completeResearch(player, ri.key);
                        anyUpdated = true;
                        break;
                    }
                }
            }

            // 2. Also check if the node has original parents that are complete (e.g. TM.INFUSION depending on INFUSION)
            if (ri.key.startsWith("TM.") && !ResearchManager.isResearchComplete(username, ri.key)) {
                String origKey = ri.key.substring(3);
                if (ResearchManager.isResearchComplete(username, origKey)) {
                    Thaumcraft.proxy.getResearchManager()
                        .completeResearch(player, ri.key);
                    anyUpdated = true;
                }
            }
        }

        if (anyUpdated) {
            PacketHandler.INSTANCE.sendTo(new PacketSyncResearch(player), (EntityPlayerMP) player);
        }
    }
}
