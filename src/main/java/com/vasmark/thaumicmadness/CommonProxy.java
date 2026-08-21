package com.vasmark.thaumicmadness;

import com.vasmark.thaumicmadness.network.NetworkHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        cpw.mods.fml.common.FMLCommonHandler.instance()
            .bus()
            .register(new ModConfigEventHandler());
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new ModConfigEventHandler());

        NetworkHandler.init();
        com.vasmark.thaumicmadness.compat.falsepattern.FalsePatternCompat.init();
        com.vasmark.thaumicmadness.compat.baubles.BaublesCompat.init();
        ModBlocks.init();
        com.vasmark.thaumicmadness.item.ModItems.init();
        com.vasmark.thaumicmadness.compat.bloodmagic.BloodMagicCompat.preInit();

        ThaumicMadness.LOG.info("I am Thaumic Madness at version " + Tags.VERSION);
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        com.vasmark.thaumicmadness.compat.bloodmagic.BloodMagicCompat.init();
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        ThaumcraftCompat.init();
        com.vasmark.thaumicmadness.nodetracker.AtlasResearchAndRecipes.init();
        com.vasmark.thaumicmadness.compat.bloodmagic.BloodMagicCompat.postInit();
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}
}
