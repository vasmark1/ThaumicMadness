package com.vasmark.thaumicmadness.compat.bloodmagic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.items.wands.ItemWandCasting;

public class SanguineWandRechargeHandler {

    private static final Aspect[] PRIMALS = new Aspect[] { Aspect.AIR, Aspect.EARTH, Aspect.FIRE, Aspect.WATER,
        Aspect.ORDER, Aspect.ENTROPY };

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != cpw.mods.fml.common.gameevent.TickEvent.Phase.END) return;

        EntityPlayer player = event.player;
        if (player.worldObj.isRemote || player.ticksExisted % 20 != 0) return;

        ItemStack held = player.getHeldItem();
        if (held == null || !(held.getItem() instanceof ItemWandCasting)) return;

        ItemWandCasting wand = (ItemWandCasting) held.getItem();
        WandRod rod = wand.getRod(held);
        if (rod == null) return;

        if ("blood".equals(rod.getTag()) || "blood_staff".equals(rod.getTag())) {
            int maxVis = wand.getMaxVis(held);
            boolean needsRecharge = false;

            for (Aspect primal : PRIMALS) {
                if (wand.getVis(held, primal) < maxVis) {
                    needsRecharge = true;
                    break;
                }
            }

            if (needsRecharge) {
                String owner = player.getCommandSenderName();
                int currentLP = SoulNetworkHandler.getCurrentEssence(owner);
                int costLP = "blood_staff".equals(rod.getTag()) ? 150 : 100;
                int visToAdd = "blood_staff".equals(rod.getTag()) ? 200 : 100; // 2 or 1 Vis

                if (currentLP >= costLP) {
                    SoulNetworkHandler.syphonFromNetwork(owner, costLP);
                    for (Aspect primal : PRIMALS) {
                        int cur = wand.getVis(held, primal);
                        if (cur < maxVis) {
                            wand.addVis(held, primal, Math.min(visToAdd, maxVis - cur), true);
                        }
                    }
                }
            }
        }
    }
}
