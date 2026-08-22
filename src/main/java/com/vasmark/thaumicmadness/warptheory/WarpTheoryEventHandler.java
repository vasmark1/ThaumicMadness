package com.vasmark.thaumicmadness.warptheory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import com.vasmark.thaumicmadness.warptheory.events.IWarpEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WarpTheoryEventHandler {

    public static void init() {
        WarpTheoryEventHandler handler = new WarpTheoryEventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance()
            .bus()
            .register(handler);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent e) {
        if (!(e.entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) e.entity;

        if (player.worldObj.isRemote || player.capabilities.isCreativeMode) return;

        // Periodic warp event trigger check (every 2000 ticks / 100 seconds)
        if (player.ticksExisted % 2000 == 0) {
            int totalWarp = WarpTheoryManager.getTotalWarp(player);
            if (totalWarp > 0 && (double) player.worldObj.rand.nextInt(100) <= Math.sqrt(totalWarp)) {
                IWarpEvent event = WarpTheoryManager.queueOneEvent(player, totalWarp);
                if (event != null) {
                    int[] warps = WarpTheoryManager.getIndividualWarps(player);
                    int tempWarp = warps[2];
                    if (tempWarp > 0) {
                        WarpTheoryManager.removeWarp(player, Math.min(event.getCost(), tempWarp));
                    }
                }
            }
        }

        // Dequeue and fire pending warp events (every second)
        if (player.ticksExisted % 20 == 0 && player.worldObj.rand.nextBoolean()) {
            IWarpEvent event = WarpTheoryManager.dequeueEvent(player);
            if (event != null && event.canDo(player)) {
                event.doEvent(player.worldObj, player);
            }
        }
    }
}
