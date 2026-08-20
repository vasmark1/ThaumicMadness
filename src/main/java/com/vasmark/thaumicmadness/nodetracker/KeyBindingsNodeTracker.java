package com.vasmark.thaumicmadness.nodetracker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.vasmark.thaumicmadness.nodetracker.gui.GuiNodeTracker;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBindingsNodeTracker {

    public static KeyBinding keyOpenTracker;

    public static void init() {
        keyOpenTracker = new KeyBinding("key.nodetracker.open", Keyboard.KEY_N, "key.categories.thaumcraft");
        ClientRegistry.registerKeyBinding(keyOpenTracker);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (keyOpenTracker != null && keyOpenTracker.isPressed()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.currentScreen == null) {
                mc.displayGuiScreen(new GuiNodeTracker());
            }
        }
    }
}
