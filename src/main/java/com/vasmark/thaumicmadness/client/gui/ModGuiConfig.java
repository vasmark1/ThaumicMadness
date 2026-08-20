package com.vasmark.thaumicmadness.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;

import com.vasmark.thaumicmadness.Config;
import com.vasmark.thaumicmadness.ThaumicMadness;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

public class ModGuiConfig extends GuiConfig {

    public ModGuiConfig(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), ThaumicMadness.MODID, false, false, "Thaumic Madness Configuration");
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();
        if (Config.configuration != null) {
            for (String categoryName : Config.configuration.getCategoryNames()) {
                ConfigCategory category = Config.configuration.getCategory(categoryName);
                if (category != null && !category.isChild()) {
                    list.add(new ConfigElement(category));
                }
            }
        }
        return list;
    }
}
