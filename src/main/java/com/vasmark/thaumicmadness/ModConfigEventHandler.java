package com.vasmark.thaumicmadness;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ModConfigEventHandler {

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (ThaumicMadness.MODID.equals(event.modID)) {
            Config.syncConfigValues();
        }
    }
}
