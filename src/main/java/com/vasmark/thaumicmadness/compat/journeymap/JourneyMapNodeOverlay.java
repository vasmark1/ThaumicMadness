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

        // Ensure nodes are drawn on the fullscreen map overlay
        DRAW_STEP.draw(0, 0, gridRenderer, 1.0F, 1.0, 0.0);

        int mouseX = event.mouseX;
        int mouseY = event.mouseY;

        double halfBlock = Math.pow(2.0, gridRenderer.getZoom()) / 2.0;

        NodeData hoveredNode = null;
        double bestDistSq = 225.0; // 15 pixel hover detection radius

        for (NodeData node : nodes) {
            if (node.dim != currentDim) continue;
            if (!gridRenderer.isOnScreen(node.x, node.z)) continue;

            Point2D.Double p = gridRenderer.getBlockPixelInGrid(node.x, node.z);
            if (p == null) continue;

            double nodeScreenX = p.x + halfBlock;
            double nodeScreenY = p.y + halfBlock;

            double dx = mouseX - nodeScreenX;
            double dy = mouseY - nodeScreenY;
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
            } catch (Throwable t) {
                LOGGER.error("Failed to access gridRenderer on Fullscreen map", t);
            }
        }
        return null;
    }

    private static void drawNodeHoverCard(GuiScreen gui, FontRenderer font, NodeData node, int mouseX, int mouseY) {
        List<String> lines = new ArrayList<String>();

        String typeName = node.type != null && !node.type.isEmpty() ? node.type : "Normal";
        String modifierName = node.modifier != null && !node.modifier.isEmpty() ? node.modifier + " " : "";
        lines.add("§6✦ " + modifierName + typeName + " " + StatCollector.translateToLocal("nodetracker.node_suffix"));
        lines.add("§7(" + node.x + ", " + node.y + ", " + node.z + ") §8| §d" + node.getTotalVis() + " Vis");

        if (node.aspects != null && !node.aspects.isEmpty()) {
            StringBuilder sb = new StringBuilder("§f");
            int count = 0;
            for (Map.Entry<String, Integer> e : node.aspects.entrySet()) {
                if (count > 0) sb.append("§7, §f");
                sb.append(e.getKey())
                    .append(": ")
                    .append(e.getValue());
                count++;
                if (count >= 4) {
                    if (node.aspects.size() > 4) sb.append(" §8+");
                    break;
                }
            }
            lines.add(sb.toString());
        }

        lines.add("§b▶ " + StatCollector.translateToLocal("nodetracker.jm.click_to_open"));

        int maxW = 0;
        for (String line : lines) {
            int w = font.getStringWidth(line);
            if (w > maxW) maxW = w;
        }

        int tooltipX = mouseX + 12;
        int tooltipY = mouseY - 12;
        int tooltipW = maxW + 12;
        int tooltipH = lines.size() * 11 + 8;

        if (tooltipX + tooltipW > gui.width) {
            tooltipX = mouseX - tooltipW - 8;
        }
        if (tooltipY + tooltipH > gui.height) {
            tooltipY = gui.height - tooltipH - 4;
        }

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glTranslatef(0, 0, 300.0F);

        // Arcane brass card background
        Gui.drawRect(tooltipX, tooltipY, tooltipX + tooltipW, tooltipY + tooltipH, 0xF018120B);
        Gui.drawRect(tooltipX + 1, tooltipY + 1, tooltipX + tooltipW - 1, tooltipY + tooltipH - 1, 0xF0241C13);

        // Gold border
        Gui.drawRect(tooltipX, tooltipY, tooltipX + tooltipW, tooltipY + 1, 0xFFE5C16C);
        Gui.drawRect(tooltipX, tooltipY + tooltipH - 1, tooltipX + tooltipW, tooltipY + tooltipH, 0xFFE5C16C);
        Gui.drawRect(tooltipX, tooltipY, tooltipX + 1, tooltipY + tooltipH, 0xFFE5C16C);
        Gui.drawRect(tooltipX + tooltipW - 1, tooltipY, tooltipX + tooltipW, tooltipY + tooltipH, 0xFFE5C16C);

        int textY = tooltipY + 4;
        for (String line : lines) {
            font.drawStringWithShadow(line, tooltipX + 6, textY, 0xFFFFFF);
            textY += 11;
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
