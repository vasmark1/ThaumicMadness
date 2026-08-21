package com.vasmark.thaumicmadness;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(
    modid = ThaumicMadness.MODID,
    version = Tags.VERSION,
    name = "Thaumic Madness",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:Thaumcraft@[4.2.3.5,);" + "after:AWWayofTime;"
        + "after:BloodArsenal;"
        + "after:gadomancy;"
        + "after:Automagy;"
        + "after:ThaumicExploration;"
        + "after:ThaumicBases;"
        + "after:WitchingGadgets;"
        + "after:WarpTheory;"
        + "after:NotEnoughItems;"
        + "after:Baubles;"
        + "after:TravellersGear;"
        + "after:tcinventoryscan;"
        + "after:falsepatternlib;"
        + "after:angelica;"
        + "after:tc4tweaks;"
        + "after:hodgepodge;"
        + "after:lwjgl3ify;",
    guiFactory = "com.vasmark.thaumicmadness.client.gui.ModGuiConfigFactory")
public class ThaumicMadness {

    public static final String MODID = "thaumicmadness";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @SidedProxy(
        clientSide = "com.vasmark.thaumicmadness.ClientProxy",
        serverSide = "com.vasmark.thaumicmadness.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
