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
        com.vasmark.thaumicmadness.warptheory.WarpTheoryManager.getInstance()
            .preInit();

        ThaumicMadness.LOG.info("I am Thaumic Madness at version " + Tags.VERSION);
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        cpw.mods.fml.common.network.NetworkRegistry.INSTANCE
            .registerGuiHandler(ThaumicMadness.instance, new com.vasmark.thaumicmadness.client.gui.ModGuiHandler());
        com.vasmark.thaumicmadness.warptheory.WarpTheoryEventHandler.init();
        ResearchSyncEventHandler.init();
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        com.vasmark.thaumicmadness.compact.infusion.CompactInfusionRecipes.init();
        com.vasmark.thaumicmadness.compact.furnace.CompactFurnaceRecipes.init();
        ThaumcraftCompat.init();
        com.vasmark.thaumicmadness.nodetracker.AtlasResearchAndRecipes.init();
        com.vasmark.thaumicmadness.warptheory.research.WarpTheoryRecipes.init();
        com.vasmark.thaumicmadness.warptheory.research.WarpTheoryResearch.init();
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}

    public void missingMappings(cpw.mods.fml.common.event.FMLMissingMappingsEvent event) {
        for (cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {
            if (mapping.name == null) continue;
            String nameLower = mapping.name.toLowerCase();

            if (mapping.type == cpw.mods.fml.common.registry.GameRegistry.Type.ITEM) {
                if (nameLower.startsWith("warptheory:")) {
                    if (nameLower.contains("tear") || nameLower.contains("cleanser")) {
                        if (com.vasmark.thaumicmadness.warptheory.WarpTheoryManager.itemPureTear != null) {
                            mapping.remap(com.vasmark.thaumicmadness.warptheory.WarpTheoryManager.itemPureTear);
                            ThaumicMadness.LOG
                                .info("Remapped legacy item {} -> thaumicmadness:pure_tear", mapping.name);
                        }
                    } else if (nameLower.contains("amulet")) {
                        if (com.vasmark.thaumicmadness.warptheory.WarpTheoryManager.itemPurificationAmulet != null) {
                            mapping
                                .remap(com.vasmark.thaumicmadness.warptheory.WarpTheoryManager.itemPurificationAmulet);
                            ThaumicMadness.LOG
                                .info("Remapped legacy item {} -> thaumicmadness:purification_amulet", mapping.name);
                        }
                    } else if (nameLower.contains("catalyst") || nameLower.contains("something")) {
                        if (com.vasmark.thaumicmadness.warptheory.WarpTheoryManager.itemUnstableCatalyst != null) {
                            mapping.remap(com.vasmark.thaumicmadness.warptheory.WarpTheoryManager.itemUnstableCatalyst);
                            ThaumicMadness.LOG
                                .info("Remapped legacy item {} -> thaumicmadness:unstable_catalyst", mapping.name);
                        }
                    } else if (nameLower.contains("parchment") || nameLower.contains("paper")) {
                        if (com.vasmark.thaumicmadness.warptheory.WarpTheoryManager.itemCursedParchment != null) {
                            mapping.remap(com.vasmark.thaumicmadness.warptheory.WarpTheoryManager.itemCursedParchment);
                            ThaumicMadness.LOG
                                .info("Remapped legacy item {} -> thaumicmadness:cursed_parchment", mapping.name);
                        }
                    }
                }
            } else if (mapping.type == cpw.mods.fml.common.registry.GameRegistry.Type.BLOCK) {
                if (nameLower.startsWith("warptheory:") && nameLower.contains("decay")) {
                    if (com.vasmark.thaumicmadness.warptheory.WarpTheoryManager.blockPhantomDecay != null) {
                        mapping.remap(com.vasmark.thaumicmadness.warptheory.WarpTheoryManager.blockPhantomDecay);
                        ThaumicMadness.LOG
                            .info("Remapped legacy block {} -> thaumicmadness:phantom_decay", mapping.name);
                    }
                }
            }
        }
    }
}
