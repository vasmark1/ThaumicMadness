package com.vasmark.thaumicmadness;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        com.vasmark.thaumicmadness.resources.LocalizationManager.getInstance()
            .injectAll();
        if (Config.enableSanityWarpOverlay) {
            MinecraftForge.EVENT_BUS.register(new WarpInventoryOverlay());
        }

        if (Config.enableThaumonomiconHDText || Config.enable2xTooltipScaler) {
            MinecraftForge.EVENT_BUS.register(new com.vasmark.thaumicmadness.client.gui.ThaumcraftGuiHandler());
            com.vasmark.thaumicmadness.font.ThaumonomiconFontHandler.init();
        }

        // Node Tracker components
        if (Config.enableNodeTracker) {
            com.vasmark.thaumicmadness.nodetracker.KeyBindingsNodeTracker.init();
            com.vasmark.thaumicmadness.nodetracker.NodeScanEventHandler nodeScanHandler = new com.vasmark.thaumicmadness.nodetracker.NodeScanEventHandler();
            MinecraftForge.EVENT_BUS.register(nodeScanHandler);
            cpw.mods.fml.common.FMLCommonHandler.instance()
                .bus()
                .register(nodeScanHandler);
            cpw.mods.fml.common.FMLCommonHandler.instance()
                .bus()
                .register(new com.vasmark.thaumicmadness.nodetracker.KeyBindingsNodeTracker());
        }

        if (Config.enableNodeHUD) {
            MinecraftForge.EVENT_BUS.register(new com.vasmark.thaumicmadness.nodetracker.gui.NodeTrackerHUD());
        }

        if (Config.enableJourneyMapIntegration
            && com.vasmark.thaumicmadness.compat.journeymap.JourneyMapCompat.isJourneyMapLoaded()) {
            com.vasmark.thaumicmadness.compat.journeymap.JourneyMapNodeOverlay jmOverlay = new com.vasmark.thaumicmadness.compat.journeymap.JourneyMapNodeOverlay();
            MinecraftForge.EVENT_BUS.register(jmOverlay);
            cpw.mods.fml.common.FMLCommonHandler.instance()
                .bus()
                .register(jmOverlay);
        }

        if (Config.enableNEIWarp && cpw.mods.fml.common.Loader.isModLoaded("NotEnoughItems")) {
            com.vasmark.thaumicmadness.nei.NEIWarpHandler.init();
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        com.vasmark.thaumicmadness.resources.LocalizationManager.getInstance()
            .injectAll();
    }
}
