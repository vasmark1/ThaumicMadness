package com.vasmark.thaumicmadness.nei;

import com.vasmark.thaumicmadness.Tags;

import codechicken.nei.api.IConfigureNEI;

public class NEIModConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        NEIWarpHandler.init();
    }

    @Override
    public String getName() {
        return "Thaumic Madness NEI Integration";
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }
}
