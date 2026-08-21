package com.vasmark.thaumicmadness.font;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.client.gui.GuiResearchBrowser;
import thaumcraft.client.gui.GuiResearchRecipe;
import thaumcraft.client.lib.TCFontRenderer;
import thaumcraft.common.lib.research.ResearchManager;

@SideOnly(Side.CLIENT)
public class ThaumonomiconFontHandler {

    private static final Logger LOGGER = LogManager.getLogger("ThaumicMadness-FontHandler");
    private static ThaumonomiconFontHandler instance;

    private ResearchItem savedBrowserHighlight = null;
    private Object[] savedRecipeTooltip = null;

    public static void init() {
        if (instance == null) {
            instance = new ThaumonomiconFontHandler();
            MinecraftForge.EVENT_BUS.register(instance);
            FMLCommonHandler.instance()
                .bus()
                .register(instance);
            LOGGER.info("Thaumonomicon readable font and 2x tooltip scaler initialized.");
        }
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        applyReadableFont(event.gui);

        if (event.gui instanceof GuiResearchBrowser || event.gui instanceof GuiResearchRecipe) {
            // Add invisible interceptor button to buttonList
            boolean alreadyPresent = false;
            for (Object btn : event.buttonList) {
                if (btn instanceof TooltipInterceptorButton) {
                    alreadyPresent = true;
                    break;
                }
            }
            if (!alreadyPresent) {
                event.buttonList.add(new TooltipInterceptorButton(987654, -100, -100, event.gui));
            }
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui != null) {
            applyReadableFont(event.gui);
        }
    }

    @SubscribeEvent
    public void onGuiDrawPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
        applyReadableFont(event.gui);
    }

    @SubscribeEvent
    public void onGuiDrawPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRenderer;

        // Render upscaled 2x tooltip for GuiResearchBrowser
        if (event.gui instanceof GuiResearchBrowser && savedBrowserHighlight != null) {
            GuiResearchBrowser browser = (GuiResearchBrowser) event.gui;
            try {
                // Restore currentHighlight so click events work
                Field f = com.vasmark.thaumicmadness.compat.falsepattern.FalsePatternCompat
                    .findCachedField(GuiResearchBrowser.class, "currentHighlight", "currentHighlight");
                if (f != null) {
                    f.set(browser, savedBrowserHighlight);
                }

                List<String> textLines = buildBrowserTooltip(
                    savedBrowserHighlight,
                    mc.thePlayer.getCommandSenderName());
                if (!textLines.isEmpty()) {
                    drawScaledHoveringText(event.gui, textLines, event.mouseX, event.mouseY, fr);
                }
            } catch (Throwable ignored) {} finally {
                savedBrowserHighlight = null;
            }
        }

        // Render upscaled 2x tooltip for GuiResearchRecipe
        if (event.gui instanceof GuiResearchRecipe && savedRecipeTooltip != null) {
            try {
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>) savedRecipeTooltip[0];
                int x = (Integer) savedRecipeTooltip[1];
                int y = (Integer) savedRecipeTooltip[2];

                if (list != null && !list.isEmpty()) {
                    drawScaledHoveringText(event.gui, list, x, y, fr);
                }
            } catch (Throwable ignored) {} finally {
                savedRecipeTooltip = null;
            }
        }
    }

    private void applyReadableFont(GuiScreen gui) {
        if (gui instanceof GuiResearchRecipe) {
            try {
                Field frField = com.vasmark.thaumicmadness.compat.falsepattern.FalsePatternCompat
                    .findCachedField(GuiResearchRecipe.class, "fr", "fr");
                if (frField != null) {
                    TCFontRenderer tcfr = (TCFontRenderer) frField.get(gui);
                    if (tcfr != null && !tcfr.getUnicodeFlag()) {
                        tcfr.setUnicodeFlag(true);
                    }
                }
            } catch (Throwable ignored) {}
        }
    }

    private List<String> buildBrowserTooltip(ResearchItem ri, String player) {
        List<String> lines = new ArrayList<>();
        if (ri == null) return lines;

        boolean completed = ResearchManager.isResearchComplete(player, ri.key);

        // Title
        String title = (ri.isLost() ? EnumChatFormatting.DARK_PURPLE : EnumChatFormatting.GOLD) + ri.getName();
        if (completed) {
            title += " " + EnumChatFormatting.GREEN + "✔";
        }
        lines.add(title);

        // Subtitle / Description
        String text = ri.getText();
        if (text != null && !text.isEmpty()) {
            lines.add(EnumChatFormatting.GRAY + EnumChatFormatting.ITALIC.toString() + text);
        }

        // Lost Knowledge tag
        if (ri.isLost()) {
            lines.add(EnumChatFormatting.GOLD + "★ " + StatCollector.translateToLocal("tc.research.lost"));
        }

        // Warp / Forbidden Knowledge level display
        int warp = ThaumcraftApi.getWarp(ri.key);
        if (warp > 0) {
            String warpLevelStr = StatCollector.translateToLocal("tc.forbidden.level." + warp);
            if (warpLevelStr.startsWith("tc.forbidden.level.")) {
                warpLevelStr = String.valueOf(warp);
            }
            String forbiddenPattern = StatCollector.translateToLocal("tc.forbidden");
            if (forbiddenPattern.contains("%n")) {
                lines.add(EnumChatFormatting.DARK_PURPLE + forbiddenPattern.replace("%n", warpLevelStr));
            } else {
                lines.add(EnumChatFormatting.DARK_PURPLE + forbiddenPattern + " (" + warpLevelStr + ")");
            }
        }

        // Status & Parents
        if (!completed) {
            if (ri.isAutoUnlock()) {
                lines.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocal("tc.research.purchase"));
            } else {
                List<String> missingParents = new ArrayList<>();

                if (ri.parents != null && ri.parents.length > 0) {
                    for (String parentKey : ri.parents) {
                        if (!ResearchManager.isResearchComplete(player, parentKey)) {
                            ResearchItem parentItem = ResearchCategories.getResearch(parentKey);
                            if (parentItem != null) {
                                missingParents.add(parentItem.getName());
                            } else {
                                String alt = StatCollector.translateToLocal("tc.research_name." + parentKey);
                                if (!alt.equals("tc.research_name." + parentKey)) {
                                    missingParents.add(alt);
                                } else {
                                    missingParents.add(parentKey);
                                }
                            }
                        }
                    }
                }

                if (ri.parentsHidden != null && ri.parentsHidden.length > 0) {
                    for (String parentKey : ri.parentsHidden) {
                        if (!ResearchManager.isResearchComplete(player, parentKey)) {
                            ResearchItem parentItem = ResearchCategories.getResearch(parentKey);
                            if (parentItem != null) {
                                missingParents.add(parentItem.getName());
                            } else {
                                String alt = StatCollector.translateToLocal("tc.research_name." + parentKey);
                                if (!alt.equals("tc.research_name." + parentKey)) {
                                    missingParents.add(alt);
                                } else {
                                    missingParents.add(parentKey);
                                }
                            }
                        }
                    }
                }

                if (!missingParents.isEmpty()) {
                    lines.add(EnumChatFormatting.RED + StatCollector.translateToLocal("tc.researchmissing"));
                    for (String mp : missingParents) {
                        lines.add(EnumChatFormatting.DARK_GRAY + " • " + EnumChatFormatting.RED + mp);
                    }
                }
            }
        }

        return lines;
    }

    public static void drawScaledHoveringText(GuiScreen gui, List<String> textLines, int x, int y, FontRenderer font) {
        if (textLines.isEmpty()) return;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        int maxLineWidth = 0;
        Iterator<String> iterator = textLines.iterator();

        while (iterator.hasNext()) {
            String line = iterator.next();
            int lineWidth = font.getStringWidth(line);
            if (lineWidth > maxLineWidth) {
                maxLineWidth = lineWidth;
            }
        }

        int tooltipX = x + 12;
        int tooltipY = y - 12;
        int tooltipHeight = 8;

        if (textLines.size() > 1) {
            tooltipHeight += 2 + (textLines.size() - 1) * 10;
        }

        if (tooltipX + maxLineWidth > gui.width) {
            tooltipX -= 28 + maxLineWidth;
        }

        if (tooltipY + tooltipHeight + 6 > gui.height) {
            tooltipY = gui.height - tooltipHeight - 6;
        }

        int zLevel = 300;
        int bgColor = 0xF0100010;
        drawGradientRect(
            zLevel,
            tooltipX - 3,
            tooltipY - 4,
            tooltipX + maxLineWidth + 3,
            tooltipY - 3,
            bgColor,
            bgColor);
        drawGradientRect(
            zLevel,
            tooltipX - 3,
            tooltipY + tooltipHeight + 3,
            tooltipX + maxLineWidth + 3,
            tooltipY + tooltipHeight + 4,
            bgColor,
            bgColor);
        drawGradientRect(
            zLevel,
            tooltipX - 3,
            tooltipY - 3,
            tooltipX + maxLineWidth + 3,
            tooltipY + tooltipHeight + 3,
            bgColor,
            bgColor);
        drawGradientRect(
            zLevel,
            tooltipX - 4,
            tooltipY - 3,
            tooltipX - 3,
            tooltipY + tooltipHeight + 3,
            bgColor,
            bgColor);
        drawGradientRect(
            zLevel,
            tooltipX + maxLineWidth + 3,
            tooltipY - 3,
            tooltipX + maxLineWidth + 4,
            tooltipY + tooltipHeight + 3,
            bgColor,
            bgColor);

        int borderColorStart = 0x505000FF;
        int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
        drawGradientRect(
            zLevel,
            tooltipX - 3,
            tooltipY - 3 + 1,
            tooltipX - 3 + 1,
            tooltipY + tooltipHeight + 3 - 1,
            borderColorStart,
            borderColorEnd);
        drawGradientRect(
            zLevel,
            tooltipX + maxLineWidth + 2,
            tooltipY - 3 + 1,
            tooltipX + maxLineWidth + 3,
            tooltipY + tooltipHeight + 3 - 1,
            borderColorStart,
            borderColorEnd);
        drawGradientRect(
            zLevel,
            tooltipX - 3,
            tooltipY - 3,
            tooltipX + maxLineWidth + 3,
            tooltipY - 3 + 1,
            borderColorStart,
            borderColorStart);
        drawGradientRect(
            zLevel,
            tooltipX - 3,
            tooltipY + tooltipHeight + 2,
            tooltipX + maxLineWidth + 3,
            tooltipY + tooltipHeight + 3,
            borderColorEnd,
            borderColorEnd);

        for (int i = 0; i < textLines.size(); ++i) {
            String line = textLines.get(i);
            font.drawStringWithShadow(line, tooltipX, tooltipY, -1);

            if (i == 0) {
                tooltipY += 2;
            }

            tooltipY += 10;
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private static void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor,
        int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex(right, top, zLevel);
        tessellator.addVertex(left, top, zLevel);
        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex(left, bottom, zLevel);
        tessellator.addVertex(right, bottom, zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private class TooltipInterceptorButton extends GuiButton {

        private final GuiScreen parentGui;

        public TooltipInterceptorButton(int id, int x, int y, GuiScreen parentGui) {
            super(id, x, y, 0, 0, "");
            this.parentGui = parentGui;
            this.visible = false;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (parentGui instanceof GuiResearchBrowser) {
                try {
                    Field f = com.vasmark.thaumicmadness.compat.falsepattern.FalsePatternCompat
                        .findCachedField(GuiResearchBrowser.class, "currentHighlight", "currentHighlight");
                    if (f != null) {
                        ResearchItem highlight = (ResearchItem) f.get(parentGui);
                        if (highlight != null) {
                            savedBrowserHighlight = highlight;
                            // Set to null so vanilla drawScreen does not draw tiny vanilla tooltip
                            f.set(parentGui, null);
                        }
                    }
                } catch (Throwable ignored) {}
            } else if (parentGui instanceof GuiResearchRecipe) {
                try {
                    Field f = com.vasmark.thaumicmadness.compat.falsepattern.FalsePatternCompat
                        .findCachedField(GuiResearchRecipe.class, "tooltip", "tooltip");
                    if (f != null) {
                        @SuppressWarnings("unchecked")
                        List<String> list = (List<String>) f.get(parentGui);
                        if (list != null && !list.isEmpty()) {
                            savedRecipeTooltip = new Object[] { new ArrayList<>(list), mouseX, mouseY };
                            // Clear vanilla tooltip list so vanilla does not render tiny tooltip
                            list.clear();
                        }
                    }
                } catch (Throwable ignored) {}
            }
        }
    }
}
