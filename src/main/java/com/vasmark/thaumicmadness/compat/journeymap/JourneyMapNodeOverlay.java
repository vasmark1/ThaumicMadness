package com.vasmark.thaumicmadness.compat.journeymap;

import java.awt.geom.Point2D;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiScreenEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.vasmark.thaumicmadness.nodetracker.NodeData;
import com.vasmark.thaumicmadness.nodetracker.NodeTrackerManager;
import com.vasmark.thaumicmadness.nodetracker.gui.GuiNodeDetailPopup;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import journeymap.client.model.MapState;
import journeymap.client.render.draw.DrawStep;
import journeymap.client.render.map.GridRenderer;
import journeymap.client.ui.fullscreen.Fullscreen;

@SideOnly(Side.CLIENT)
public class JourneyMapNodeOverlay {

    private static final Logger LOGGER = LogManager.getLogger("ThaumicMadness-JMOverlay");
    private static final AuraNodeDrawStep DRAW_STEP = new AuraNodeDrawStep();

    private static Field gridRendererField = null;
    private static boolean fieldLookupDone = false;

    private static long lastClickTime = 0;
    private static boolean wasMouseDown = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!JourneyMapCompat.isJourneyMapLoaded()) return;

        try {
            MapState state = Fullscreen.state();
            if (state != null) {
                List<DrawStep> drawSteps = state.getDrawSteps();
                if (drawSteps != null && !drawSteps.contains(DRAW_STEP)) {
                    drawSteps.add(DRAW_STEP);
                }
            }
        } catch (Throwable ignored) {}
    }

    @SubscribeEvent
    public void onGuiDrawPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!JourneyMapCompat.isJourneyMapLoaded()) return;
        if (event.gui == null || !(event.gui instanceof Fullscreen)) return;

        Fullscreen fullscreen = (Fullscreen) event.gui;
        GridRenderer gridRenderer = getGridRenderer(fullscreen);
        if (gridRenderer == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        int currentDim = mc.thePlayer.dimension;

        List<NodeData> nodes = NodeTrackerManager.getInstance()
            .getNodes();
        if (nodes == null || nodes.isEmpty()) return;

        int mouseX = event.mouseX;
        int mouseY = event.mouseY;

        NodeData hoveredNode = null;
        double bestDistSq = 144.0; // 12 pixel radius

        for (NodeData node : nodes) {
            if (node.dim != currentDim) continue;
            if (!gridRenderer.isOnScreen(node.x, node.z)) continue;

            Point2D.Double p = gridRenderer.getBlockPixelInGrid(node.x, node.z);
            if (p == null) continue;

            double dx = mouseX - p.x;
            double dy = mouseY - p.y;
            double distSq = dx * dx + dy * dy;

            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                hoveredNode = node;
            }
        }

        boolean isMouseDown = Mouse.isButtonDown(0);

        if (hoveredNode != null) {
            drawNodeHoverCard(event.gui, mc.fontRenderer, hoveredNode, mouseX, mouseY);

            if (isMouseDown && !wasMouseDown) {
                long now = System.currentTimeMillis();
                if (now - lastClickTime > 250) {
                    lastClickTime = now;
                    mc.displayGuiScreen(new GuiNodeDetailPopup(hoveredNode, fullscreen));
                    wasMouseDown = true;
                    return;
                }
            }
        }

        wasMouseDown = isMouseDown;
    }

    private static GridRenderer getGridRenderer(Fullscreen fullscreen) {
        if (!fieldLookupDone) {
            fieldLookupDone = true;
            try {
                gridRendererField = Fullscreen.class.getDeclaredField("gridRenderer");
                gridRendererField.setAccessible(true);
            } catch (Throwable t) {
                // Fallback: search all fields of type GridRenderer
                for (Field f : Fullscreen.class.getDeclaredFields()) {
                    if (GridRenderer.class.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        gridRendererField = f;
                        break;
                    }
                }
            }
        }

        if (gridRendererField != null) {
            try {
                return (GridRenderer) gridRendererField.get(fullscreen);
            } catch (Throwable ignored) {}
        }
        return null;
    }

    private static void drawNodeHoverCard(GuiScreen gui, FontRenderer font, NodeData node, int mouseX, int mouseY) {
        List<String> textLines = new ArrayList<String>();

        String title = node.getTypeColorCode() + node.getFormattedType()
            + " "
            + StatCollector.translateToLocal("nodetracker.node_suffix");
        textLines.add(title);

        textLines.add("§7(" + node.x + ", " + node.y + ", " + node.z + ") §8| §d" + node.getTotalVis() + " Vis");

        StringBuilder aspectPreview = new StringBuilder("§e");
        int count = 0;
        for (Map.Entry<String, Integer> entry : node.aspects.entrySet()) {
            if (count > 0) aspectPreview.append(" ");
            aspectPreview.append(entry.getKey())
                .append(":")
                .append(entry.getValue());
            count++;
            if (count >= 4) {
                if (node.aspects.size() > 4) aspectPreview.append(" ...");
                break;
            }
        }
        textLines.add(aspectPreview.toString());
        textLines.add("§b▶ " + StatCollector.translateToLocal("nodetracker.jm.click_to_open"));

        // Render standard tooltip using GuiScreen.drawHoveringText via reflection or custom render
        int tooltipX = mouseX + 12;
        int tooltipY = mouseY - 12;

        int tooltipTextWidth = 0;
        for (String line : textLines) {
            int lineW = font.getStringWidth(line);
            if (lineW > tooltipTextWidth) tooltipTextWidth = lineW;
        }

        int tooltipHeight = textLines.size() * 10 + 4;
        if (tooltipX + tooltipTextWidth > gui.width) tooltipX = mouseX - 16 - tooltipTextWidth;
        if (tooltipY + tooltipHeight + 6 > gui.height) tooltipY = gui.height - tooltipHeight - 6;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glTranslatef(0, 0, 300.0F);

        // Dark background
        Gui.drawRect(
            tooltipX - 3,
            tooltipY - 4,
            tooltipX + tooltipTextWidth + 3,
            tooltipY + tooltipHeight + 4,
            0xF0100C08);
        Gui.drawRect(
            tooltipX - 4,
            tooltipY - 3,
            tooltipX + tooltipTextWidth + 4,
            tooltipY + tooltipHeight + 3,
            0xF0100C08);

        // Gold border
        Gui.drawRect(tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 2, 0xFF8A6536);
        Gui.drawRect(
            tooltipX - 3,
            tooltipY + tooltipHeight + 2,
            tooltipX + tooltipTextWidth + 3,
            tooltipY + tooltipHeight + 3,
            0xFF8A6536);
        Gui.drawRect(tooltipX - 3, tooltipY - 2, tooltipX - 2, tooltipY + tooltipHeight + 2, 0xFF8A6536);
        Gui.drawRect(
            tooltipX + tooltipTextWidth + 2,
            tooltipY - 2,
            tooltipX + tooltipTextWidth + 3,
            tooltipY + tooltipHeight + 2,
            0xFF8A6536);

        for (int i = 0; i < textLines.size(); i++) {
            font.drawStringWithShadow(textLines.get(i), tooltipX, tooltipY + i * 10, 0xFFFFFF);
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
