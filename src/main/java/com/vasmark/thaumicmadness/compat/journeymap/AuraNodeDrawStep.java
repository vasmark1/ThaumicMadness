package com.vasmark.thaumicmadness.compat.journeymap;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.vasmark.thaumicmadness.nodetracker.NodeData;
import com.vasmark.thaumicmadness.nodetracker.NodeTrackerManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import journeymap.client.render.draw.DrawStep;
import journeymap.client.render.map.GridRenderer;

@SideOnly(Side.CLIENT)
public class AuraNodeDrawStep implements DrawStep {

    @Override
    public void draw(double xOffset, double yOffset, GridRenderer gridRenderer, float drawScale, double fontScale,
        double rotation) {
        if (gridRenderer == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        int currentDim = mc.thePlayer.dimension;
        List<NodeData> nodes = NodeTrackerManager.getInstance()
            .getNodes();
        if (nodes == null || nodes.isEmpty()) return;

        long time = System.currentTimeMillis();
        float pulse = (float) Math.sin((time % 2000) / 2000.0 * Math.PI * 2.0) * 0.25F + 0.75F;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); // Additive blending for magical glow

        Tessellator tessellator = Tessellator.instance;

        for (NodeData node : nodes) {
            if (node.dim != currentDim) continue;
            if (!gridRenderer.isOnScreen(node.x, node.z)) continue;

            Point2D.Double p = gridRenderer.getBlockPixelInGrid(node.x, node.z);
            if (p == null) continue;

            double drawX = p.x + xOffset;
            double drawY = p.y + yOffset;

            Color c = JourneyMapCompat.getNodeColor(node);
            float r = c.getRed() / 255.0F;
            float g = c.getGreen() / 255.0F;
            float b = c.getBlue() / 255.0F;

            // Outer Pulsating Glow
            float outerRadius = 8.0F * drawScale * pulse;
            drawCircle(tessellator, drawX, drawY, outerRadius, r, g, b, 0.45F);

            // Core Halo
            float coreRadius = 4.0F * drawScale;
            drawCircle(tessellator, drawX, drawY, coreRadius, r, g, b, 0.9F);

            // Bright Center Point
            drawCircle(tessellator, drawX, drawY, 1.5F * drawScale, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private static void drawCircle(Tessellator tessellator, double cx, double cy, double radius, float r, float g,
        float b, float a) {
        int segments = 16;
        tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
        tessellator.setColorRGBA_F(r, g, b, a);
        tessellator.addVertex(cx, cy, 0);

        for (int i = 0; i <= segments; i++) {
            double angle = (i * 2.0 * Math.PI) / segments;
            double dx = Math.cos(angle) * radius;
            double dy = Math.sin(angle) * radius;
            tessellator.setColorRGBA_F(r, g, b, 0.0F);
            tessellator.addVertex(cx + dx, cy + dy, 0);
        }
        tessellator.draw();
    }
}
