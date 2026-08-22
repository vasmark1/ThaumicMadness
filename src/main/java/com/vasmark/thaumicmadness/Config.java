package com.vasmark.thaumicmadness;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {

    public static Configuration configuration;

    // --- Category: General / Atlas ---
    public static final String CATEGORY_ATLAS = "thaumaturgical_atlas";
    public static boolean enableAtlas = true;
    public static boolean enableAtlasPassiveRevealing = true;
    public static boolean enableNodeTracker = true;
    public static boolean enableNodeHUD = true;
    public static boolean enableJourneyMapIntegration = true;
    public static boolean enableJourneyMapAutoWaypoint = false;

    // --- Category: Visuals & Font ---
    public static final String CATEGORY_VISUALS = "visuals";
    public static boolean enableThaumonomiconHDText = true;
    public static boolean enable2xTooltipScaler = true;
    public static boolean enableSanityWarpOverlay = true;

    // --- Category: Integrations & Blocks ---
    public static final String CATEGORY_EXTRAS = "extras";
    public static boolean enableNEIWarp = true;
    public static boolean enableInfusedDirt = true;

    public static void synchronizeConfiguration(File configFile) {
        configuration = new Configuration(configFile);
        syncConfigValues();
    }

    public static void syncConfigValues() {
        // Atlas & Node Tracker
        Property propAtlas = configuration
            .get(CATEGORY_ATLAS, "enableAtlas", true, "Enable the All-Seeing Thaumonomicon (Thaumaturgical Atlas).");
        propAtlas.setRequiresMcRestart(true);
        enableAtlas = propAtlas.getBoolean();

        Property propPassiveRevealing = configuration.get(
            CATEGORY_ATLAS,
            "enableAtlasPassiveRevealing",
            true,
            "Enable passive Goggles of Revealing vision while holding the All-Seeing Thaumonomicon in inventory.");
        enableAtlasPassiveRevealing = propPassiveRevealing.getBoolean();

        Property propNodeTracker = configuration.get(
            CATEGORY_ATLAS,
            "enableNodeTracker",
            true,
            "Enable Aura Node scanning and tracking features in the Atlas.");
        enableNodeTracker = propNodeTracker.getBoolean();

        Property propNodeHUD = configuration
            .get(CATEGORY_ATLAS, "enableNodeHUD", true, "Enable the on-screen waypoint HUD for tracked aura nodes.");
        enableNodeHUD = propNodeHUD.getBoolean();

        Property propJM = configuration.get(
            CATEGORY_ATLAS,
            "enableJourneyMapIntegration",
            true,
            "Enable JourneyMap waypoint buttons and integration in the Thaumaturgical Atlas.");
        enableJourneyMapIntegration = propJM.getBoolean();

        Property propJMAuto = configuration.get(
            CATEGORY_ATLAS,
            "enableJourneyMapAutoWaypoint",
            false,
            "Automatically create JourneyMap waypoints whenever a new aura node is scanned.");
        enableJourneyMapAutoWaypoint = propJMAuto.getBoolean();

        // Visuals & Typography
        Property propHDText = configuration.get(
            CATEGORY_VISUALS,
            "enableThaumonomiconHDText",
            true,
            "Enable crisp, readable HD typography in Thaumonomicon book pages.");
        enableThaumonomiconHDText = propHDText.getBoolean();

        Property prop2xTooltip = configuration.get(
            CATEGORY_VISUALS,
            "enable2xTooltipScaler",
            true,
            "Enable 2x scaling for small aspect and node tooltips.");
        enable2xTooltipScaler = prop2xTooltip.getBoolean();

        Property propSanity = configuration.get(
            CATEGORY_VISUALS,
            "enableSanityWarpOverlay",
            true,
            "Display the Sanity Checker / Warp meter in player inventory GUI when carrying the Sanity Checker.");
        enableSanityWarpOverlay = propSanity.getBoolean();

        // Extras
        Property propNEI = configuration
            .get(CATEGORY_EXTRAS, "enableNEIWarp", true, "Enable NEI recipe and warp information handler.");
        enableNEIWarp = propNEI.getBoolean();

        Property propInfusedDirt = configuration
            .get(CATEGORY_EXTRAS, "enableInfusedDirt", true, "Enable Infused Dirt block and its crafting mechanics.");
        propInfusedDirt.setRequiresMcRestart(true);
        enableInfusedDirt = propInfusedDirt.getBoolean();

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
