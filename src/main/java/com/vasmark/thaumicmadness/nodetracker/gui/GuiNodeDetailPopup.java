package com.vasmark.thaumicmadness.nodetracker.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.vasmark.thaumicmadness.nodetracker.NodeData;
import com.vasmark.thaumicmadness.nodetracker.NodeTrackerManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

@SideOnly(Side.CLIENT)
public class GuiNodeDetailPopup extends GuiScreen {

    private static final ResourceLocation TEXTURE_PARCHMENT = new ResourceLocation(
        "thaumcraft",
        "textures/gui/gui_researchback.png");

    private final NodeData node;
    private final GuiScreen parentScreen;

    public static final int POPUP_WIDTH = 260;
    public static final int POPUP_HEIGHT = 220;

    private GuiButton btnTrackGPS;
    private GuiButton btnOpenAtlas;
    private GuiButton btnCopyCoords;
    private GuiButton btnDelete;
    private GuiButton btnBack;

    private String toastNotification = "";
    private long toastExpiry = 0;

    public GuiNodeDetailPopup(NodeData node, GuiScreen parentScreen) {
        this.node = node;
        this.parentScreen = parentScreen;
    }

    public void setNotification(String text) {
        this.toastNotification = text;
        this.toastExpiry = System.currentTimeMillis() + 2500;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        int left = (this.width - POPUP_WIDTH) / 2;
        int top = (this.height - POPUP_HEIGHT) / 2;

        boolean isTarget = node.equals(
            NodeTrackerManager.getInstance()
                .getActiveTarget());

        btnTrackGPS = new GuiButton(
            1,
            left + 14,
            top + 130,
            110,
            20,
            isTarget ? ("§a★ " + StatCollector.translateToLocal("nodetracker.popup.tracking"))
                : ("§e" + StatCollector.translateToLocal("nodetracker.popup.track_gps")));

        btnOpenAtlas = new GuiButton(
            2,
            left + 134,
            top + 130,
            112,
            20,
            "§6" + StatCollector.translateToLocal("nodetracker.popup.open_atlas"));

        btnCopyCoords = new GuiButton(
            3,
            left + 14,
            top + 156,
            110,
            20,
            "§b" + StatCollector.translateToLocal("nodetracker.popup.copy_coords"));

        btnDelete = new GuiButton(
            4,
            left + 134,
            top + 156,
            112,
            20,
            "§c" + StatCollector.translateToLocal("nodetracker.popup.delete"));

        btnBack = new GuiButton(
            5,
            left + (POPUP_WIDTH - 120) / 2,
            top + 188,
            120,
            20,
            StatCollector.translateToLocal("nodetracker.popup.back_to_map"));

        this.buttonList.add(btnTrackGPS);
        this.buttonList.add(btnOpenAtlas);
        this.buttonList.add(btnCopyCoords);
        this.buttonList.add(btnDelete);
        this.buttonList.add(btnBack);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) { // Track GPS
            NodeTrackerManager.getInstance()
                .setActiveTarget(node);
            setNotification(
                "§e" + StatCollector.translateToLocal(
                    "nodetracker.hud.tracking") + " (" + node.x + ", " + node.y + ", " + node.z + ")");
            btnTrackGPS.displayString = "§a★ " + StatCollector.translateToLocal("nodetracker.popup.tracking");
        } else if (button.id == 2) { // Open in Atlas
            GuiNodeTracker atlasGui = new GuiNodeTracker();
            this.mc.displayGuiScreen(atlasGui);
        } else if (button.id == 3) { // Copy Coords
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
                setNotification("§a" + StatCollector.translateToLocal("nodetracker.copied"));
            } catch (Throwable ignored) {}
        } else if (button.id == 4) { // Delete
            NodeTrackerManager.getInstance()
                .deleteNode(node);
            this.mc.displayGuiScreen(parentScreen);
        } else if (button.id == 5) { // Back to Map
            this.mc.displayGuiScreen(parentScreen);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.displayGuiScreen(parentScreen);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Draw dark tinted backdrop
        drawDefaultBackground();

        int left = (this.width - POPUP_WIDTH) / 2;
        int top = (this.height - POPUP_HEIGHT) / 2;

        // Draw Thaumcraft Parchment panel
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(TEXTURE_PARCHMENT);
        drawTexturedModalRect(left, top, 0, 0, POPUP_WIDTH, POPUP_HEIGHT);

        // Ornate Brass Outer Border
        Gui.drawRect(left, top, left + POPUP_WIDTH, top + 2, 0xFF8A6536);
        Gui.drawRect(left, top + POPUP_HEIGHT - 2, left + POPUP_WIDTH, top + POPUP_HEIGHT, 0xFF8A6536);
        Gui.drawRect(left, top, left + 2, top + POPUP_HEIGHT, 0xFF8A6536);
        Gui.drawRect(left + POPUP_WIDTH - 2, top, left + POPUP_WIDTH, top + POPUP_HEIGHT, 0xFF8A6536);

        // Inner dark border
        Gui.drawRect(left + 2, top + 2, left + POPUP_WIDTH - 2, top + 3, 0xFF2A1E11);
        Gui.drawRect(left + 2, top + POPUP_HEIGHT - 3, left + POPUP_WIDTH - 2, top + POPUP_HEIGHT - 2, 0xFF2A1E11);

        // Header Background Banner
        Gui.drawRect(left + 4, top + 4, left + POPUP_WIDTH - 4, top + 34, 0xCC1A120B);

        // Node Title
        String typeColor = node.getTypeColorCode();
        String title = typeColor + node.getFormattedType()
            + " "
            + StatCollector.translateToLocal("nodetracker.node_suffix");
        int titleW = this.fontRendererObj.getStringWidth(title);
        this.fontRendererObj.drawStringWithShadow(title, left + (POPUP_WIDTH - titleW) / 2, top + 8, 0xFFFFFF);

        // Subtitle: Dimension & Coordinates
        String dimName = NodeTrackerManager.getInstance()
            .getDimensionName(node.dim);
        String sub = "§7(" + node.x + ", " + node.y + ", " + node.z + ") §8| §6" + dimName;
        int subW = this.fontRendererObj.getStringWidth(sub);
        this.fontRendererObj.drawStringWithShadow(sub, left + (POPUP_WIDTH - subW) / 2, top + 20, 0xDDDDDD);

        // Distance & Vis Total Banner
        double px = mc.thePlayer != null ? mc.thePlayer.posX : 0;
        double py = mc.thePlayer != null ? mc.thePlayer.posY : 0;
        double pz = mc.thePlayer != null ? mc.thePlayer.posZ : 0;
        double dist = node.getDistanceSq(px, py, pz);
        String distInfo = "§e" + StatCollector.translateToLocal("nodetracker.distance")
            + ": §f"
            + (int) dist
            + "м  §8|  §d"
            + StatCollector.translateToLocal("nodetracker.total_vis")
            + ": §f"
            + node.getTotalVis();
        this.fontRendererObj.drawStringWithShadow(distInfo, left + 14, top + 40, 0x222222);

        // Aspects Panel Box
        Gui.drawRect(left + 10, top + 54, left + POPUP_WIDTH - 10, top + 120, 0x88000000);
        Gui.drawRect(left + 10, top + 54, left + POPUP_WIDTH - 10, top + 55, 0xFF5D4020);
        Gui.drawRect(left + 10, top + 119, left + POPUP_WIDTH - 10, top + 120, 0xFF5D4020);

        // Render Aspects Grid
        int aspectStartX = left + 16;
        int aspectY = top + 62;
        int rowCount = 0;
        String hoveredAspectTooltip = null;

        for (Map.Entry<String, Integer> entry : node.aspects.entrySet()) {
            Aspect aspect = Aspect.getAspect(entry.getKey());
            if (aspect != null) {
                int ax = aspectStartX + (rowCount % 11) * 21;
                int ay = aspectY + (rowCount / 11) * 26;

                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                UtilsFX.drawTag(ax, ay, aspect, entry.getValue(), 0, 0.0D, 771, 1.0F, false);
                GL11.glPopAttrib();
                GL11.glPopMatrix();

                // Aspect Amount Text
                String amt = String.valueOf(entry.getValue());
                int amtW = this.fontRendererObj.getStringWidth(amt);
                this.fontRendererObj.drawStringWithShadow(amt, ax + 8 - amtW / 2, ay + 17, 0xFFFF88);

                // Hover check
                if (mouseX >= ax && mouseX <= ax + 16 && mouseY >= ay && mouseY <= ay + 24) {
                    hoveredAspectTooltip = aspect.getName() + " §7(" + entry.getValue() + " Vis)";
                }

                rowCount++;
            }
        }

        // Draw buttons
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Draw Aspect Tooltip if hovered
        if (hoveredAspectTooltip != null) {
            List<String> tip = new ArrayList<String>();
            tip.add(hoveredAspectTooltip);
            drawHoveringText(tip, mouseX, mouseY, fontRendererObj);
        }

        // Toast notification
        if (System.currentTimeMillis() < toastExpiry && !toastNotification.isEmpty()) {
            int toastW = this.fontRendererObj.getStringWidth(toastNotification);
            int toastX = (this.width - toastW) / 2;
            int toastY = top - 18;

            Gui.drawRect(toastX - 6, toastY - 3, toastX + toastW + 6, toastY + 11, 0xEE111111);
            Gui.drawRect(toastX - 5, toastY - 2, toastX + toastW + 5, toastY + 10, 0xCC332211);
            this.fontRendererObj.drawStringWithShadow(toastNotification, toastX, toastY, 0xFFFFFF);
        }
    }
}
