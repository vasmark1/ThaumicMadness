package com.vasmark.thaumicmadness.compact.infusion.client;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.vasmark.thaumicmadness.compact.infusion.TileCompactInfusionMatrix;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.client.renderers.tile.TileRunicMatrixRenderer;
import thaumcraft.common.tiles.TileInfusionMatrix;

@SideOnly(Side.CLIENT)
public class TileCompactInfusionMatrixRenderer extends TileEntitySpecialRenderer {

    private static final ResourceLocation TEX_GLASS_RUNE = new ResourceLocation(
        "thaumcraft",
        "textures/blocks/wardedglassrune.png");

    private final TileRunicMatrixRenderer originalRenderer = new TileRunicMatrixRenderer(0);
    private final TileInfusionMatrix dummyTile = new TileInfusionMatrix();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileCompactInfusionMatrix)) return;
        TileCompactInfusionMatrix tile = (TileCompactInfusionMatrix) te;

        boolean crafting = (tile.state != TileCompactInfusionMatrix.STATE_IDLE);

        // Configure dummy Thaumcraft TileInfusionMatrix
        dummyTile.active = true;
        dummyTile.crafting = crafting;
        dummyTile.startUp = 1.0F;
        dummyTile.craftCount = tile.craftProgress;
        dummyTile.instability = tile.instability;
        dummyTile.xCoord = tile.xCoord;
        dummyTile.yCoord = tile.yCoord;
        dummyTile.zCoord = tile.zCoord;
        dummyTile.setWorldObj(tile.getWorldObj());

        GL11.glPushMatrix();

        // 1. Render Scaled Compact Thaumcraft Runic Matrix (scaled down to 0.55 to hover inside)
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        GL11.glScaled(0.55D, 0.55D, 0.55D);
        GL11.glTranslatef(-((float) x + 0.5F), -((float) y + 0.5F), -((float) z + 0.5F));
        originalRenderer.renderTileEntityAt(dummyTile, x, y, z, partialTicks);
        GL11.glPopMatrix();

        // 2. Render Outer Prominent Arcane Glass Block
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_CULL_FACE);

        this.bindTexture(TEX_GLASS_RUNE);

        // Inner Back Faces (subtle interior glow)
        GL11.glCullFace(GL11.GL_FRONT);
        GL11.glColor4f(0.8F, 0.6F, 1.0F, 0.35F);
        renderGlassCube(0.499F);

        // Outer Front Faces (rich warded runic texture)
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.85F);
        renderGlassCube(0.499F);

        // 3. Crisp Glowing Arcane Borders/Edges
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(2.5F);
        GL11.glDepthMask(false);
        char lastLight = (char) OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        float edgeAlpha = crafting ? (0.75F + MathHelper.sin(tile.craftProgress * 0.15F) * 0.25F) : 0.65F;
        GL11.glColor4f(0.75F, 0.35F, 1.0F, edgeAlpha);
        renderCubeEdges(0.499F);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastLight % 65536, lastLight / 65536);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

        GL11.glPopMatrix();
    }

    private void renderGlassCube(float size) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();

        // Top Face
        tess.addVertexWithUV(-size, size, -size, 0.0D, 0.0D);
        tess.addVertexWithUV(-size, size, size, 0.0D, 1.0D);
        tess.addVertexWithUV(size, size, size, 1.0D, 1.0D);
        tess.addVertexWithUV(size, size, -size, 1.0D, 0.0D);

        // Bottom Face
        tess.addVertexWithUV(-size, -size, -size, 0.0D, 0.0D);
        tess.addVertexWithUV(size, -size, -size, 1.0D, 0.0D);
        tess.addVertexWithUV(size, -size, size, 1.0D, 1.0D);
        tess.addVertexWithUV(-size, -size, size, 0.0D, 1.0D);

        // North Face
        tess.addVertexWithUV(-size, -size, -size, 0.0D, 1.0D);
        tess.addVertexWithUV(-size, size, -size, 0.0D, 0.0D);
        tess.addVertexWithUV(size, size, -size, 1.0D, 0.0D);
        tess.addVertexWithUV(size, -size, -size, 1.0D, 1.0D);

        // South Face
        tess.addVertexWithUV(-size, -size, size, 0.0D, 1.0D);
        tess.addVertexWithUV(size, -size, size, 1.0D, 1.0D);
        tess.addVertexWithUV(size, size, size, 1.0D, 0.0D);
        tess.addVertexWithUV(-size, size, size, 0.0D, 0.0D);

        // West Face
        tess.addVertexWithUV(-size, -size, -size, 0.0D, 1.0D);
        tess.addVertexWithUV(-size, -size, size, 1.0D, 1.0D);
        tess.addVertexWithUV(-size, size, size, 1.0D, 0.0D);
        tess.addVertexWithUV(-size, size, -size, 0.0D, 0.0D);

        // East Face
        tess.addVertexWithUV(size, -size, -size, 1.0D, 1.0D);
        tess.addVertexWithUV(size, size, -size, 1.0D, 0.0D);
        tess.addVertexWithUV(size, size, size, 0.0D, 0.0D);
        tess.addVertexWithUV(size, -size, size, 0.0D, 1.0D);

        tess.draw();
    }

    private void renderCubeEdges(float size) {
        GL11.glBegin(GL11.GL_LINES);

        // Bottom 4 edges
        GL11.glVertex3f(-size, -size, -size);
        GL11.glVertex3f(size, -size, -size);
        GL11.glVertex3f(size, -size, -size);
        GL11.glVertex3f(size, -size, size);
        GL11.glVertex3f(size, -size, size);
        GL11.glVertex3f(-size, -size, size);
        GL11.glVertex3f(-size, -size, size);
        GL11.glVertex3f(-size, -size, -size);

        // Top 4 edges
        GL11.glVertex3f(-size, size, -size);
        GL11.glVertex3f(size, size, -size);
        GL11.glVertex3f(size, size, -size);
        GL11.glVertex3f(size, size, size);
        GL11.glVertex3f(size, size, size);
        GL11.glVertex3f(-size, size, size);
        GL11.glVertex3f(-size, size, size);
        GL11.glVertex3f(-size, size, -size);

        // Vertical 4 pillars
        GL11.glVertex3f(-size, -size, -size);
        GL11.glVertex3f(-size, size, -size);
        GL11.glVertex3f(size, -size, -size);
        GL11.glVertex3f(size, size, -size);
        GL11.glVertex3f(size, -size, size);
        GL11.glVertex3f(size, size, size);
        GL11.glVertex3f(-size, -size, size);
        GL11.glVertex3f(-size, size, size);

        GL11.glEnd();
    }
}
