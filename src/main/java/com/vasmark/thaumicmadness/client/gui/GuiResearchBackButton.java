package com.vasmark.thaumicmadness.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiResearchBackButton extends GuiButton {

    private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation(
        "thaumcraft",
        "textures/gui/gui_researchbook.png");

    public GuiResearchBackButton(int id, int x, int y) {
        super(id, x, y, 24, 16, "");
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return this.visible && mouseX >= this.xPosition
            && mouseY >= this.yPosition
            && mouseX < this.xPosition + this.width
            && mouseY < this.yPosition + this.height;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        drawArrow(mc, mouseX, mouseY);
    }

    public void drawArrow(Minecraft mc, int mouseX, int mouseY) {
        if (!this.visible) return;

        boolean hovered = isMouseOver(mouseX, mouseY);
        this.field_146123_n = hovered;

        mc.renderEngine.bindTexture(TEXTURE_BOOK);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        float scale = 2.0F;
        if (hovered && mc.thePlayer != null) {
            scale += MathHelper.sin(mc.thePlayer.ticksExisted / 3.0F) * 0.2F;
        }

        // Draw Thaumcraft left page turn arrow: u = 0, v = 184, w = 12, h = 8, scaled 2x
        drawTexturedModalRectScaled(this.xPosition, this.yPosition, 0, 184, 12, 8, scale);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private static void drawTexturedModalRectScaled(double x, double y, int u, int v, int width, int height,
        float scale) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0.0D);
        GL11.glScalef(scale, scale, 1.0F);

        float f = 0.00390625F; // 1 / 256
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0, height, 0, (double) ((float) u * f), (double) ((float) (v + height) * f));
        tessellator
            .addVertexWithUV(width, height, 0, (double) ((float) (u + width) * f), (double) ((float) (v + height) * f));
        tessellator.addVertexWithUV(width, 0, 0, (double) ((float) (u + width) * f), (double) ((float) v * f));
        tessellator.addVertexWithUV(0, 0, 0, (double) ((float) u * f), (double) ((float) v * f));
        tessellator.draw();

        GL11.glPopMatrix();
    }
}
