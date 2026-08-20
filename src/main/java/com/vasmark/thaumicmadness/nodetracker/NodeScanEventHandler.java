package com.vasmark.thaumicmadness.nodetracker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.nodes.INode;
import thaumcraft.common.config.ConfigItems;

@SideOnly(Side.CLIENT)
public class NodeScanEventHandler {

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!com.vasmark.thaumicmadness.Config.enableNodeTracker) return;
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;
        if (event.world == null || !event.world.isRemote) return;

        EntityPlayer player = event.entityPlayer;
        if (player == null) return;

        ItemStack held = player.getHeldItem();
        if (held == null || held.getItem() != ConfigItems.itemThaumometer) return;

        World world = event.world;
        TileEntity te = world.getTileEntity(event.x, event.y, event.z);
        if (te instanceof INode) {
            INode node = (INode) te;
            int dim = world.provider.dimensionId;
            NodeTrackerManager.getInstance()
                .addOrUpdateNode(node, dim, event.x, event.y, event.z);
        }
    }

    @SubscribeEvent
    public void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        String sessionName = "local";
        if (mc.isSingleplayer()) {
            if (mc.getIntegratedServer() != null) {
                sessionName = mc.getIntegratedServer()
                    .getWorldName();
            }
        } else {
            ServerData serverData = mc.func_147104_D();
            if (serverData != null) {
                sessionName = serverData.serverIP;
            }
        }

        NodeTrackerManager.getInstance()
            .setSessionId(sessionName);
    }

    @SubscribeEvent
    public void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        NodeTrackerManager.getInstance()
            .saveToJson();
        NodeTrackerManager.getInstance()
            .clearActiveTarget();
    }
}
