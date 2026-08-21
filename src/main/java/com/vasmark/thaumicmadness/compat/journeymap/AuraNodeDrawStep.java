package com.vasmark.thaumicmadness.compat.journeymap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;

import net.minecraft.client.Minecraft;

import com.vasmark.thaumicmadness.nodetracker.NodeData;
import com.vasmark.thaumicmadness.nodetracker.NodeTrackerManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import journeymap.client.render.draw.DrawStep;
import journeymap.client.render.draw.DrawUtil;
import journeymap.client.render.map.GridRenderer;
import journeymap.client.render.texture.TextureImpl;

@SideOnly(Side.CLIENT)
public class AuraNodeDrawStep implements DrawStep {

    private static TextureImpl orbTexture = null;

    private static synchronized TextureImpl getOrbTexture() {
        if (orbTexture == null) {
            int size = 64;
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            float center = size / 2.0F;
            float radius = size / 2.0F;

            // Pure white radial gradient from solid nucleus to soft translucent halo
            float[] dist = { 0.0f, 0.25f, 0.65f, 1.0f };
            Color[] colors = { new Color(255, 255, 255, 255), new Color(255, 255, 255, 230),
                new Color(255, 255, 255, 120), new Color(255, 255, 255, 0) };
            RadialGradientPaint p = new RadialGradientPaint(center, center, radius, dist, colors);
            g2d.setPaint(p);
            g2d.fillOval(0, 0, size, size);
            g2d.dispose();

            orbTexture = new TextureImpl(img, true);
            orbTexture.setDescription("thaumicmadness_aura_node_orb");
        }
        return orbTexture;
    }

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

        TextureImpl tex = getOrbTexture();
        if (tex == null) return;

        double halfBlock = Math.pow(2.0, gridRenderer.getZoom()) / 2.0;

        // 10x slower subtle pulsation (15 seconds per cycle, ±8% size variation)
        long time = System.currentTimeMillis();
        float pulse = (float) Math.sin((time % 15000) / 15000.0 * Math.PI * 2.0) * 0.08F + 0.92F;

        double texW = tex.getWidth();
        double texH = tex.getHeight();
        float scale = 0.25F * pulse; // 64 * 0.25 = 16 pixels diameter
        double renderW = texW * scale;
        double renderH = texH * scale;

        for (NodeData node : nodes) {
            if (node.dim != currentDim) continue;

            Point2D.Double p = gridRenderer.getBlockPixelInGrid(node.x, node.z);
            if (p == null) continue;
            if (!gridRenderer.isOnScreen(p)) continue;

            double drawX = p.x + halfBlock + xOffset;
            double drawY = p.y + halfBlock + yOffset;

            Color c = JourneyMapCompat.getNodeColor(node);
            int rgb = (c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue();

            // Render pure white radial gradient texture tinted 100% accurately with node type/aspect color
            DrawUtil.drawColoredImage(tex, 255, rgb, drawX - renderW / 2.0, drawY - renderH / 2.0, scale, 0.0);
        }
    }
}
