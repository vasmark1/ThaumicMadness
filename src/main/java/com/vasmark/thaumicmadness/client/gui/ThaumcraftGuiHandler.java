package com.vasmark.thaumicmadness.client.gui;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.client.gui.GuiResearchBrowser;
import thaumcraft.client.gui.GuiResearchRecipe;

@SideOnly(Side.CLIENT)
public class ThaumcraftGuiHandler {

    public static final int BACK_BUTTON_ID = 74431;
    private GuiResearchBackButton currentBackButton;

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.gui instanceof GuiResearchRecipe) {
            int left = (event.gui.width - 256) / 2;
            int top = (event.gui.height - 181) / 2;
            int btnX = left + 128 - 12; // Centered horizontally below the 256px book
            int btnY = top + 190;

            if (btnY + 16 > event.gui.height) {
                btnY = event.gui.height - 18;
            }

            currentBackButton = new GuiResearchBackButton(BACK_BUTTON_ID, btnX, btnY);
            event.buttonList.add(currentBackButton);
        } else {
            currentBackButton = null;
        }
    }

    @SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (event.gui instanceof GuiResearchRecipe && event.button.id == BACK_BUTTON_ID) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.getSoundHandler()
                .playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("thaumcraft", "page")));

            try {
                if (GuiResearchRecipe.history != null) {
                    GuiResearchRecipe.history.clear();
                }
            } catch (Throwable ignored) {}

            double mapX = 0;
            double mapY = 0;
            try {
                Field fX = GuiResearchRecipe.class.getDeclaredField("guiMapX");
                Field fY = GuiResearchRecipe.class.getDeclaredField("guiMapY");
                fX.setAccessible(true);
                fY.setAccessible(true);
                mapX = fX.getDouble(event.gui);
                mapY = fY.getDouble(event.gui);
            } catch (Throwable ignored) {}

            mc.displayGuiScreen(new GuiResearchBrowser(mapX, mapY));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.gui instanceof GuiResearchRecipe && currentBackButton != null && currentBackButton.visible) {
            // Explicitly render the Thaumcraft styled arrow on top of GuiResearchRecipe
            currentBackButton.drawArrow(Minecraft.getMinecraft(), event.mouseX, event.mouseY);
        }
    }
}
