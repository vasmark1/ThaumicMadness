package com.vasmark.thaumicmadness.compat.bloodmagic;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Loader;
import thaumcraft.api.research.ResearchCategories;

public class BloodMagicCompat {

    public static final Logger LOGGER = LogManager.getLogger("ThaumicMadness-BloodMagic");
    public static final String CATEGORY_BLOOD_MAGIC = "BLOOD_MAGIC";

    public static boolean isBloodMagicLoaded = false;
    public static boolean isBloodArsenalLoaded = false;

    public static void preInit() {
        if (!com.vasmark.thaumicmadness.Config.enableBloodMagicIntegration) return;
        isBloodMagicLoaded = Loader.isModLoaded("AWWayofTime");
        isBloodArsenalLoaded = Loader.isModLoaded("BloodArsenal");

        if (!isBloodMagicLoaded) {
            LOGGER.info("Blood Magic (AWWayofTime) is not loaded, skipping Blood Magic integration.");
            return;
        }

        LOGGER.info("Blood Magic detected! Initializing Blood Magic & Thaumcraft cross-mechanics...");
        if (isBloodArsenalLoaded) {
            LOGGER.info("Blood Arsenal detected! Initializing Blood Arsenal integration...");
        }

        BloodMagicItems.init();
    }

    public static void init() {
        if (!com.vasmark.thaumicmadness.Config.enableBloodMagicIntegration || !isBloodMagicLoaded) return;

        BloodMagicAspects.registerAspects();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new BloodWarpHandler());
        cpw.mods.fml.common.FMLCommonHandler.instance()
            .bus()
            .register(new BloodAltarWandRechargeHandler());
    }

    public static void postInit() {
        if (!com.vasmark.thaumicmadness.Config.enableBloodMagicIntegration || !isBloodMagicLoaded) return;

        registerResearchCategory();
        BloodMagicRecipes.registerRecipes();
        BloodMagicResearch.registerResearch();
        BloodMagicRecipeRemover.removeDuplicateRecipes();

        LOGGER.info("Blood Magic & Blood Arsenal integration and Thaumonomicon guide tab successfully loaded!");
    }

    private static void registerResearchCategory() {
        ResourceLocation icon = new ResourceLocation("alchemicalwizardry", "textures/items/ApprenticeBloodOrb.png");
        ResourceLocation background = new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png");

        ResearchCategories.registerCategory(CATEGORY_BLOOD_MAGIC, icon, background);
        LOGGER.info("Registered Thaumonomicon category: {}", CATEGORY_BLOOD_MAGIC);
    }
}
