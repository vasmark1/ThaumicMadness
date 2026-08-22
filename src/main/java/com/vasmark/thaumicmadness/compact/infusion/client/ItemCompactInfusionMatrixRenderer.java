package com.vasmark.thaumicmadness.compact.infusion.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.client.renderers.tile.TileRunicMatrixRenderer;
import thaumcraft.common.tiles.TileInfusionMatrix;

@SideOnly(Side.CLIENT)
public class ItemCompactInfusionMatrixRenderer implements IItemRenderer {

    private static final ResourceLocation TEX_GLASS_RUNE = new ResourceLocation(
        "thaumcraft",
        "textures/blocks/wardedglassrune.png");

    private final TileRunicMatrixRenderer originalRenderer = new TileRunicMatrixRenderer(0);
    private final TileInfusionMatrix dummyTile = new TileInfusionMatrix();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();

        if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        } else if (type == ItemRenderType.ENTITY) {
            GL11.glTranslatef(0.0F, 0.5F, 0.0F);
        }

        dummyTile.active = true;
        dummyTile.crafting = false;
        dummyTile.startUp = 1.0F;

        // 1. Render Scaled Thaumcraft Matrix (scaled down to 0.55)
        GL11.glPushMatrix();
        GL11.glScaled(0.55D, 0.55D, 0.55D);
        originalRenderer.renderTileEntityAt(dummyTile, -0.5D, -0.5D, -0.5D, 0.0F);
        GL11.glPopMatrix();

        // 2. Render Outer Prominent Arcane Glass Shell
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_CULL_FACE);

        Minecraft.getMinecraft().renderEngine.bindTexture(TEX_GLASS_RUNE);

        // Inner faces
        GL11.glCullFace(GL11.GL_FRONT);
        GL11.glColor4f(0.8F, 0.6F, 1.0F, 0.35F);
        renderGlassCube(0.499F);

        // Outer faces
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.85F);
        renderGlassCube(0.499F);

        // 3. Glowing Edges
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(2.5F);
        GL11.glDepthMask(false);
        char lastLight = (char) OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        GL11.glColor4f(0.75F, 0.35F, 1.0F, 0.65F);
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
