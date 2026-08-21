package com.vasmark.thaumicmadness.nodetracker.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.vasmark.thaumicmadness.nodetracker.NodeData;
import com.vasmark.thaumicmadness.nodetracker.NodeTrackerManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

@SideOnly(Side.CLIENT)
public class GuiNodeSlotList {

    private final GuiNodeTracker parent;
    private List<NodeData> currentList = new ArrayList<NodeData>();

    public static final int CARD_WIDTH = 296;
    public static final int CARD_HEIGHT = 38;
    public static final int CARD_SPACING = 4;

    private final int top;
    private final int bottom;
    private float scrollY = 0;
    private boolean isDraggingScrollbar = false;
    private int dragClickY = 0;
    private float dragInitialScroll = 0;

    public GuiNodeSlotList(GuiNodeTracker parent, int top, int bottom) {
        this.parent = parent;
        this.top = top;
        this.bottom = bottom;
    }

    public void updateList(List<NodeData> list) {
        this.currentList = list != null ? list : new ArrayList<NodeData>();
        clampScroll();
    }

    public List<NodeData> getCurrentList() {
        return currentList;
    }

    public int getContentHeight() {
        return currentList.size() * (CARD_HEIGHT + CARD_SPACING);
    }

    public int getMaxScroll() {
        int viewHeight = bottom - top;
        return Math.max(0, getContentHeight() - viewHeight + 4);
    }

    private void clampScroll() {
        scrollY = MathHelper.clamp_float(scrollY, 0, getMaxScroll());
    }

    public void handleMouseInput() {
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            scrollY -= (wheel > 0 ? 1 : -1) * 20.0F;
            clampScroll();
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton != 0) return;

        int panelLeft = (parent.width - GuiNodeTracker.PANEL_WIDTH) / 2;
        int scrollBarX = panelLeft + GuiNodeTracker.PANEL_WIDTH - 9;
        int viewHeight = bottom - top;

        // Check scrollbar click
        if (mouseX >= scrollBarX - 2 && mouseX <= scrollBarX + 7 && mouseY >= top && mouseY <= bottom) {
            isDraggingScrollbar = true;
            dragClickY = mouseY;
            dragInitialScroll = scrollY;
            return;
        }

        // Check card clicks
        if (mouseY < top || mouseY > bottom) return;
        int cardX = (parent.width - CARD_WIDTH) / 2;
        int cardRight = cardX + CARD_WIDTH;

        if (mouseX < cardX || mouseX > cardRight) return;

        int relativeY = mouseY - top + (int) scrollY;
        int index = relativeY / (CARD_HEIGHT + CARD_SPACING);

        if (index >= 0 && index < currentList.size()) {
            NodeData node = currentList.get(index);
            int cardY = top + index * (CARD_HEIGHT + CARD_SPACING) - (int) scrollY;

            int btnDelX = cardRight - 15;
            int btnCopyX = cardRight - 32;
            int btnJmX = cardRight - 55;
            int btnTrackX = cardRight - 86;
            int btnY = cardY + 3;
            int btnH = 14;

            // 1. Track Button (GPS)
            if (mouseX >= btnTrackX && mouseX <= btnTrackX + 29 && mouseY >= btnY && mouseY <= btnY + btnH) {
                NodeTrackerManager.getInstance()
                    .setActiveTarget(node);
                parent.setNotification(
                    "§e" + StatCollector.translateToLocal(
                        "nodetracker.hud.tracking") + " (" + node.x + ", " + node.y + ", " + node.z + ")");
                return;
            }

            // 2. JourneyMap Waypoint Button (JM)
            if (com.vasmark.thaumicmadness.compat.journeymap.JourneyMapCompat.isJourneyMapLoaded() && mouseX >= btnJmX
                && mouseX <= btnJmX + 21
                && mouseY >= btnY
                && mouseY <= btnY + btnH) {
                boolean created = com.vasmark.thaumicmadness.compat.journeymap.JourneyMapCompat.toggleWaypoint(node);
                if (created) {
                    parent.setNotification("§a" + StatCollector.translateToLocal("nodetracker.jm.created"));
                } else {
                    parent.setNotification("§c" + StatCollector.translateToLocal("nodetracker.jm.removed"));
                }
                return;
            }

            // 3. Copy Button (CPY)
            if (mouseX >= btnCopyX && mouseX <= btnCopyX + 15 && mouseY >= btnY && mouseY <= btnY + btnH) {
                try {
                    String text = node.x + " "
                        + node.y
                        + " "
                        + node.z
                        + " ("
                        + NodeTrackerManager.getInstance()
                            .getDimensionName(node.dim)
                        + ")";
                    StringSelection selection = new StringSelection(text);
                    Toolkit.getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(selection, selection);
                    parent.setNotification("§a" + StatCollector.translateToLocal("nodetracker.copied"));
                } catch (Throwable ignored) {}
                return;
            }

            // 4. Delete Button (✕)
            if (mouseX >= btnDelX && mouseX <= btnDelX + 13 && mouseY >= btnY && mouseY <= btnY + btnH) {
                NodeTrackerManager.getInstance()
                    .deleteNode(node);
                parent.refreshNodeList();
                return;
            }

            // Click anywhere else on card: select as target
            NodeTrackerManager.getInstance()
                .setActiveTarget(node);
        }
    }

    public void mouseMovedOrUp(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            isDraggingScrollbar = false;
        }
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        handleMouseInput();

        int viewHeight = bottom - top;
        int maxScroll = getMaxScroll();

        if (isDraggingScrollbar && maxScroll > 0) {
            int scrollbarTrackHeight = viewHeight - 20;
            int deltaY = mouseY - dragClickY;
            scrollY = dragInitialScroll + ((float) deltaY / scrollbarTrackHeight) * maxScroll;
            clampScroll();
        }

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRenderer;
        int cardX = (parent.width - CARD_WIDTH) / 2;
        int cardRight = cardX + CARD_WIDTH;

        // Enable GL Scissor to cleanly clip cards inside the parchment viewport
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int scale = res.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(cardX * scale, (mc.displayHeight - bottom * scale), CARD_WIDTH * scale, (bottom - top) * scale);

        NodeData activeTarget = NodeTrackerManager.getInstance()
            .getActiveTarget();

        // Render visible cards
        for (int i = 0; i < currentList.size(); i++) {
            int cardY = top + i * (CARD_HEIGHT + CARD_SPACING) - (int) scrollY;

            // Cull offscreen cards
            if (cardY + CARD_HEIGHT < top || cardY > bottom) {
                continue;
            }

            NodeData node = currentList.get(i);
            boolean isHovered = mouseX >= cardX && mouseX <= cardRight
                && mouseY >= cardY
                && mouseY <= cardY + CARD_HEIGHT
                && mouseY >= top
                && mouseY <= bottom;
            boolean isTarget = activeTarget != null && activeTarget.equals(node);

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);

            // Card background
            int bgColor = isTarget ? 0xF02A2412 : (isHovered ? 0xEE221B13 : 0xDD18130E);
            int borderColor = isTarget ? 0xFFE5C16C : (isHovered ? 0xFFA67C42 : 0xFF5A4222);

            Gui.drawRect(cardX, cardY, cardRight, cardY + CARD_HEIGHT, borderColor);
            Gui.drawRect(cardX + 1, cardY + 1, cardRight - 1, cardY + CARD_HEIGHT - 1, bgColor);

            // Header: Type, Mod, Coords, Distance
            String header = node.getTypeColorCode() + node.getFormattedType();
            if (node.modifier != null && !node.modifier.isEmpty()) {
                header += " " + node.getModifierColorCode() + node.getFormattedModifier();
            }
            font.drawStringWithShadow(header, cardX + 5, cardY + 3, 0xFFFFFF);

            double dist = node.getDistance(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            String subInfo = "§7(" + node.x
                + ", "
                + node.y
                + ", "
                + node.z
                + ") §6"
                + (int) dist
                + "м §8["
                + NodeTrackerManager.getInstance()
                    .getDimensionName(node.dim)
                + "]";
            font.drawStringWithShadow(subInfo, cardX + 5, cardY + 13, 0xCCCCCC);

            // Action buttons
            int btnDelX = cardRight - 15;
            int btnCopyX = cardRight - 32;
            int btnJmX = cardRight - 55;
            int btnTrackX = cardRight - 86;
            int btnY = cardY + 3;
            int btnH = 14;

            // 1. Track Button (GPS)
            boolean hoverTrack = mouseX >= btnTrackX && mouseX <= btnTrackX + 29
                && mouseY >= btnY
                && mouseY <= btnY + btnH;
            Gui.drawRect(
                btnTrackX,
                btnY,
                btnTrackX + 29,
                btnY + btnH,
                hoverTrack ? 0xFFE5C16C : (isTarget ? 0xFF8A6536 : 0xFF3D2C15));
            Gui.drawRect(btnTrackX + 1, btnY + 1, btnTrackX + 28, btnY + btnH - 1, isTarget ? 0xF03B3015 : 0xE01A150E);
            font.drawStringWithShadow(isTarget ? "§a★GPS" : "§eGPS", btnTrackX + 3, btnY + 3, 0xFFFFFF);

            // 2. JourneyMap Waypoint Button (JM)
            if (com.vasmark.thaumicmadness.compat.journeymap.JourneyMapCompat.isJourneyMapLoaded()) {
                boolean hasJmWp = com.vasmark.thaumicmadness.compat.journeymap.JourneyMapCompat.hasWaypoint(node);
                boolean hoverJm = mouseX >= btnJmX && mouseX <= btnJmX + 21 && mouseY >= btnY && mouseY <= btnY + btnH;
                Gui.drawRect(
                    btnJmX,
                    btnY,
                    btnJmX + 21,
                    btnY + btnH,
                    hoverJm ? 0xFF55FFFF : (hasJmWp ? 0xFF00AAAA : 0xFF3D2C15));
                Gui.drawRect(btnJmX + 1, btnY + 1, btnJmX + 20, btnY + btnH - 1, hasJmWp ? 0xF00A2E33 : 0xE01A150E);
                font.drawStringWithShadow(hasJmWp ? "§b●JM" : "§7JM", btnJmX + 2, btnY + 3, 0xFFFFFF);

                if (isHovered && hoverJm) {
                    parent.hoveredTooltip = StatCollector
                        .translateToLocal(hasJmWp ? "nodetracker.jm.remove_tip" : "nodetracker.jm.add_tip");
                }
            }

            // 3. Copy Button (CPY)
            boolean hoverCopy = mouseX >= btnCopyX && mouseX <= btnCopyX + 15
                && mouseY >= btnY
                && mouseY <= btnY + btnH;
            Gui.drawRect(btnCopyX, btnY, btnCopyX + 15, btnY + btnH, hoverCopy ? 0xFFE5C16C : 0xFF3D2C15);
            Gui.drawRect(btnCopyX + 1, btnY + 1, btnCopyX + 14, btnY + btnH - 1, 0xE01A150E);
            font.drawStringWithShadow("§b⎘", btnCopyX + 4, btnY + 3, 0xFFFFFF);

            // 4. Delete Button (✕)
            boolean hoverDel = mouseX >= btnDelX && mouseX <= btnDelX + 13 && mouseY >= btnY && mouseY <= btnY + btnH;
            Gui.drawRect(btnDelX, btnY, btnDelX + 13, btnY + btnH, hoverDel ? 0xFFFF4444 : 0xFF3D2C15);
            Gui.drawRect(btnDelX + 1, btnY + 1, btnDelX + 12, btnY + btnH - 1, 0xE01A150E);
            font.drawStringWithShadow("§c✕", btnDelX + 3, btnY + 3, 0xFFFFFF);

            // Aspects row
            int aspectX = cardX + 5;
            for (Map.Entry<String, Integer> entry : node.aspects.entrySet()) {
                Aspect aspect = Aspect.getAspect(entry.getKey());
                if (aspect != null) {
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    UtilsFX.drawTag(aspectX, cardY + 23, aspect, entry.getValue(), 0, 0.0D, 771, 1.0F, false);
                    GL11.glPopAttrib();

                    // Check hover for tooltip
                    if (isHovered && mouseX >= aspectX
                        && mouseX <= aspectX + 16
                        && mouseY >= cardY + 23
                        && mouseY <= cardY + 36) {
                        parent.hoveredTooltip = aspect.getName() + " (" + entry.getValue() + ")";
                    }

                    aspectX += 18;
                }
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        // Draw Thaumcraft scrollbar if content overflows
        if (maxScroll > 0) {
            int panelLeft = (parent.width - GuiNodeTracker.PANEL_WIDTH) / 2;
            int scrollBarX = panelLeft + GuiNodeTracker.PANEL_WIDTH - 8;
            int scrollBarW = 4;

            // Track
            Gui.drawRect(scrollBarX, top, scrollBarX + scrollBarW, bottom, 0x88140F0A);

            // Thumb
            int thumbH = Math.max(16, (viewHeight * viewHeight) / getContentHeight());
            int thumbY = top + (int) ((scrollY / (float) maxScroll) * (viewHeight - thumbH));

            Gui.drawRect(scrollBarX, thumbY, scrollBarX + scrollBarW, thumbY + thumbH, 0xFFE5C16C);
            Gui.drawRect(scrollBarX + 1, thumbY + 1, scrollBarX + scrollBarW - 1, thumbY + thumbH - 1, 0xFFA67C42);
        }
    }
}
