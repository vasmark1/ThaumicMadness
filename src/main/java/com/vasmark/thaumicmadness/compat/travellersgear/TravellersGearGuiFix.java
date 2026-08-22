package com.vasmark.thaumicmadness.compat.travellersgear;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vasmark.thaumicmadness.compat.baubles.BaublesCompat;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TravellersGearGuiFix {

    private static final Logger LOG = LogManager.getLogger("ThaumicMadness/TravellersGearFix");

    public static void init() {
        if (!Loader.isModLoaded("TravellersGear")) return;

        if (BaublesCompat.isBaublesExpandedLoaded()) {
            try {
                // If Baubles-Expanded is active (which places its button at top-left [26, 9]),
                // move Traveller's Gear equipment button to top-right [66, 9] to prevent overlap.
                travellersgear.client.ClientProxy.equipmentButtonPos = new int[] { 66, 9 };
                LOG.info(
                    "Baubles-Expanded detected! Automatically repositioned Traveller's Gear equipment button to top-right [66, 9].");
            } catch (Throwable t) {
                LOG.warn("Could not set travellersgear.client.ClientProxy.equipmentButtonPos", t);
            }
        } else {
            LOG.info(
                "Standard Baubles active. Traveller's Gear equipment button retained at original position [27, 9].");
        }

        MinecraftForge.EVENT_BUS.register(new TravellersGearGuiFix());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiPostInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event == null || event.gui == null || event.buttonList == null) return;
        if (!(event.gui instanceof GuiInventory)) return;
        if (!BaublesCompat.isBaublesExpandedLoaded()) return;

        int guiLeft = (event.gui.width - 176) / 2;
        int guiTop = (event.gui.height - 166) / 2;

        if (event.gui.mc != null && event.gui.mc.thePlayer != null
            && !event.gui.mc.thePlayer.getActivePotionEffects()
                .isEmpty()) {
            try {
                if (travellersgear.common.util.ModCompatability.isNeiHidden()) {
                    guiLeft = 160 + (event.gui.width - 176 - 200) / 2;
                }
            } catch (Throwable ignored) {}
        }

        for (Object obj : event.buttonList) {
            if (obj instanceof GuiButton) {
                GuiButton btn = (GuiButton) obj;
                if (btn.id == 106 || btn.getClass()
                    .getName()
                    .contains("GuiButtonGear")) {
                    btn.xPosition = guiLeft + 66;
                    btn.yPosition = guiTop + 9;
                }
            }
        }
    }
}
