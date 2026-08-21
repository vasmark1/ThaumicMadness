package com.vasmark.thaumicmadness.nodetracker.gui;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.opengl.GL11;

import com.vasmark.thaumicmadness.nodetracker.NodeData;
import com.vasmark.thaumicmadness.nodetracker.NodeTrackerManager;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

@SideOnly(Side.CLIENT)
public class NodeTrackerHUD extends Gui {

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!com.vasmark.thaumicmadness.Config.enableNodeHUD) return;
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        NodeData target = NodeTrackerManager.getInstance()
            .getActiveTarget();
        if (target == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null || mc.currentScreen != null) return;

        ScaledResolution res = event.resolution;
        int screenWidth = res.getScaledWidth();
        FontRenderer font = mc.fontRenderer;

        // Position at top center
        int width = 168;
        int height = 36;
        int x = (screenWidth - width) / 2;
        int y = 4;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        // Frame background - antique parchment plate
        drawRect(x, y, x + width, y + height, 0xEE120E0A);
        drawRect(x + 1, y + 1, x + width - 1, y + height - 1, 0xFFA67C42);
        drawRect(x + 2, y + 2, x + width - 2, y + height - 2, 0xF41E1711);

        // Check dimension
        if (player.dimension != target.dim) {
            String dimMsg = "§c" + StatCollector.translateToLocal("nodetracker.hud.wrong_dim")
                + " ("
                + NodeTrackerManager.getInstance()
                    .getDimensionName(target.dim)
                + ")";
            font.drawStringWithShadow(dimMsg, x + (width - font.getStringWidth(dimMsg)) / 2, y + 6, 0xFFFFFF);
            String targetPos = "§7" + target.x + ", " + target.y + ", " + target.z;
            font.drawStringWithShadow(targetPos, x + (width - font.getStringWidth(targetPos)) / 2, y + 18, 0xCCCCCC);

            GL11.glPopAttrib();
            GL11.glPopMatrix();
            return;
        }

        double dx = target.x + 0.5 - player.posX;
        double dy = target.y + 0.5 - player.posY;
        double dz = target.z + 0.5 - player.posZ;
        double distance = com.vasmark.thaumicmadness.compat.falsepattern.FalsePatternCompat
            .fastSqrt(dx * dx + dy * dy + dz * dz);

        // Auto-arrival detection
        if (distance < 3.5) {
            String arrivedMsg = "§a★ " + StatCollector.translateToLocal("nodetracker.hud.arrived") + " ★";
            font.drawStringWithShadow(arrivedMsg, x + (width - font.getStringWidth(arrivedMsg)) / 2, y + 13, 0xFFFFFF);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            return;
        }

        // Compute angle diff from player's view using FastMath
        double targetAngle = Math
            .toDegrees(com.vasmark.thaumicmadness.compat.falsepattern.FalsePatternCompat.fastAtan2(dz, dx)) - 90.0;
        double angleDiff = MathHelper.wrapAngleTo180_double(targetAngle - player.rotationYaw);

        // Draw rotating Thaumcraft direction needle with brass dial
        int arrowCenterX = x + 16;
        int arrowCenterY = y + 18;

        // Dial base
        drawRect(arrowCenterX - 11, arrowCenterY - 11, arrowCenterX + 11, arrowCenterY + 11, 0xFF5A4222);
        drawRect(arrowCenterX - 10, arrowCenterY - 10, arrowCenterX + 10, arrowCenterY + 10, 0xFF140F0A);

        GL11.glPushMatrix();
        GL11.glTranslated(arrowCenterX, arrowCenterY, 0.0D);
        GL11.glRotated(angleDiff, 0.0D, 0.0D, 1.0D);

        // Draw arrow polygon
        drawArrowIcon();
        GL11.glPopMatrix();

        // Target info & distance
        String nameStr = target.getTypeColorCode() + target.getFormattedType()
            + " "
            + target.getModifierColorCode()
            + target.getFormattedModifier();
        font.drawStringWithShadow(nameStr, x + 32, y + 4, 0xFFFFFF);

        String distStr = "§6" + (int) distance + "м §7(" + target.x + ", " + target.y + ", " + target.z + ")";
        font.drawStringWithShadow(distStr, x + 32, y + 14, 0xFFE5C16C);

        // Mini aspect list
        int aspectX = x + 32;
        int count = 0;
        for (Map.Entry<String, Integer> entry : target.aspects.entrySet()) {
            if (count >= 7) break;
            Aspect aspect = Aspect.getAspect(entry.getKey());
            if (aspect != null) {
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                UtilsFX.drawTag(aspectX, y + 24, aspect, entry.getValue(), 0, 0.0D, 771, 1.0F, false);
                GL11.glPopAttrib();
                aspectX += 17;
                count++;
            }
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void drawArrowIcon() {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(0.95F, 0.78F, 0.35F, 1.0F); // Radiant Gold

        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2d(0.0D, -9.0D); // Tip
        GL11.glVertex2d(-5.0D, 5.0D); // Bottom left
        GL11.glVertex2d(0.0D, 2.0D); // Inner notch

        GL11.glVertex2d(0.0D, -9.0D); // Tip
        GL11.glVertex2d(0.0D, 2.0D); // Inner notch
        GL11.glVertex2d(5.0D, 5.0D); // Bottom right
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
