package com.vasmark.thaumicmadness;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiScreenEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.relics.ItemSanityChecker;

@SideOnly(Side.CLIENT)
public class WarpInventoryOverlay {

    private static final ResourceLocation HUD_TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/hud.png");

    private static Field getGuiLeftField() {
        return com.vasmark.thaumicmadness.compat.falsepattern.FalsePatternCompat
            .findCachedField(GuiContainer.class, "guiLeft", "field_147003_i");
    }

    private static Field getGuiTopField() {
        return com.vasmark.thaumicmadness.compat.falsepattern.FalsePatternCompat
            .findCachedField(GuiContainer.class, "guiTop", "field_147009_r");
    }

    private static Field getYSizeField() {
        return com.vasmark.thaumicmadness.compat.falsepattern.FalsePatternCompat
            .findCachedField(GuiContainer.class, "ySize", "field_147000_e");
    }

    // Check if player has Sanity Checker in main inventory or Baubles
    private static boolean hasSanityChecker(EntityPlayer player) {
        if (player == null || player.inventory == null) return false;

        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() instanceof ItemSanityChecker) {
                return true;
            }
        }

        try {
            IInventory baublesInv = baubles.api.BaublesApi.getBaubles(player);
            if (baublesInv != null) {
                for (int i = 0; i < baublesInv.getSizeInventory(); i++) {
                    ItemStack stack = baublesInv.getStackInSlot(i);
                    if (stack != null && stack.getItem() instanceof ItemSanityChecker) {
                        return true;
                    }
                }
            }
        } catch (Throwable ignored) {}

        return false;
    }

    // Check if player has completed research 'CONSTANT_VIGILANCE'
    private static boolean hasConstantVigilanceResearch(EntityPlayer player) {
        if (player == null) return false;
        String username = player.getCommandSenderName();
        return ThaumcraftApiHelper.isResearchComplete(username, ThaumcraftCompat.RESEARCH_CONSTANT_VIGILANCE);
    }

    @SubscribeEvent
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!com.vasmark.thaumicmadness.Config.enableSanityWarpOverlay) return;
        if (!(event.gui instanceof GuiContainer)) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null || Thaumcraft.proxy == null || Thaumcraft.proxy.getPlayerKnowledge() == null) {
            return;
        }

        // Warp HUD scale strictly requires Constant Vigilance research + Sanity Checker in inventory/baubles
        if (!hasConstantVigilanceResearch(player) || !hasSanityChecker(player)) {
            return;
        }

        GuiContainer container = (GuiContainer) event.gui;
        int guiLeft, guiTop, ySize;
        try {
            Field fLeft = getGuiLeftField();
            Field fTop = getGuiTopField();
            Field fYSize = getYSizeField();
            guiLeft = fLeft != null ? fLeft.getInt(container) : (container.width - 176) / 2;
            guiTop = fTop != null ? fTop.getInt(container) : (container.height - 166) / 2;
            ySize = fYSize != null ? fYSize.getInt(container) : 166;
        } catch (Exception e) {
            guiLeft = (container.width - 176) / 2;
            ySize = 166;
            guiTop = (container.height - ySize) / 2;
        }

        // Dynamic anchoring to player inventory slots:
        // Thaumcraft meter is 20px wide by 76px tall (exact height of player 4-row inventory section)
        int xOffset = 22;
        if (event.gui.getClass()
            .getName()
            .contains("GuiPlayerExpanded")) {
            int columns = 1;
            try {
                int used = baubles.api.expanded.BaubleExpandedSlots.slotsCurrentlyUsed();
                int maxCols = 4;
                try {
                    maxCols = baubles.common.BaublesConfig.maxColumns;
                } catch (Throwable ignored) {}
                columns = Math.min(Math.max(1, (used + 7 - 1) / 7), maxCols);
            } catch (Throwable t) {
                columns = 2;
            }
            xOffset = 50 + (columns - 1) * 18;
        }

        int barX = guiLeft - xOffset;
        int barY = guiTop + ySize - 84;
        int barWidth = 20;
        int barHeight = 76;

        String username = player.getCommandSenderName();
        int perm = Thaumcraft.proxy.getPlayerKnowledge()
            .getWarpPerm(username);
        int sticky = Thaumcraft.proxy.getPlayerKnowledge()
            .getWarpSticky(username);
        int temp = Thaumcraft.proxy.getPlayerKnowledge()
            .getWarpTemp(username);
        int total = perm + sticky + temp;

        float totalF = (float) total;
        float ratio = 1.0F;
        if (totalF > 100.0F) {
            ratio = 100.0F / totalF;
            totalF = 100.0F;
        }

        int emptyH = (int) Math.round(((100.0F - totalF) / 100.0F) * 48.0F);
        int tempH = (int) Math.round((temp / 100.0F) * 48.0F * ratio);
        int stickyH = (int) Math.round((sticky / 100.0F) * 48.0F * ratio);
        int permH = (int) Math.round((perm / 100.0F) * 48.0F * ratio);

        if (emptyH + tempH + stickyH + permH > 48) {
            permH = 48 - emptyH - tempH - stickyH;
            if (permH < 0) permH = 0;
        }

        int tubeX = barX + 6;
        int tubeY = barY + 20;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mc.renderEngine.bindTexture(HUD_TEXTURE);

        // 1. Temporary Warp liquid layer
        if (temp > 0 && tempH > 0) {
            GL11.glColor4f(1.0F, 0.5F, 1.0F, 1.0F);
            container.drawTexturedModalRect(tubeX, tubeY + emptyH, 200, emptyH, 8, tempH);
        }

        // 2. Sticky / Normal Warp liquid layer
        if (sticky > 0 && stickyH > 0) {
            GL11.glColor4f(0.75F, 0.0F, 0.75F, 1.0F);
            container.drawTexturedModalRect(tubeX, tubeY + emptyH + tempH, 200, emptyH + tempH, 8, stickyH);
        }

        // 3. Permanent Warp liquid layer
        if (perm > 0 && permH > 0) {
            GL11.glColor4f(0.5F, 0.0F, 0.5F, 1.0F);
            container.drawTexturedModalRect(
                tubeX,
                tubeY + emptyH + tempH + stickyH,
                200,
                emptyH + tempH + stickyH,
                8,
                permH);
        }

        // 4. Thaumcraft Sanity Meter Frame
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        container.drawTexturedModalRect(barX, barY, 176, 0, 20, 76);

        // 5. Eldritch Warning Skull Topper (if total warp >= 100)
        if (total >= 100) {
            container.drawTexturedModalRect(barX, barY, 216, 0, 20, 16);
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();

        // 6. Tooltip display on mouse hover
        FontRenderer font = mc.fontRenderer;
        int mouseX = event.mouseX;
        int mouseY = event.mouseY;

        if (mouseX >= barX && mouseX <= barX + barWidth && mouseY >= barY && mouseY <= barY + barHeight) {
            List<String> tooltip = new ArrayList<String>();
            tooltip.add("§5§l" + StatCollector.translateToLocal("mymod.warp.title") + ": §d" + total);
            tooltip.add("§7" + StatCollector.translateToLocal("mymod.warp.perm") + ": §8" + perm);
            tooltip.add("§7" + StatCollector.translateToLocal("mymod.warp.sticky") + ": §5" + sticky);
            tooltip.add("§7" + StatCollector.translateToLocal("mymod.warp.temp") + ": §b" + temp);

            if (total == 0) {
                tooltip.add("§8§o" + StatCollector.translateToLocal("mymod.warp.sane"));
            } else if (total < 25) {
                tooltip.add("§a§o" + StatCollector.translateToLocal("mymod.warp.minor"));
            } else if (total < 50) {
                tooltip.add("§e§o" + StatCollector.translateToLocal("mymod.warp.moderate"));
            } else {
                tooltip.add("§c§o" + StatCollector.translateToLocal("mymod.warp.severe"));
            }

            drawCustomHoveringText(tooltip, mouseX, mouseY, font, event.gui.width, event.gui.height);
        }
    }

    private void drawCustomHoveringText(List<String> textLines, int x, int y, FontRenderer font, int width,
        int height) {
        if (textLines.isEmpty()) return;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        int tooltipWidth = 0;

        for (String s : textLines) {
            int strWidth = font.getStringWidth(s);
            if (strWidth > tooltipWidth) {
                tooltipWidth = strWidth;
            }
        }

        int tooltipX = x + 12;
        int tooltipY = y - 12;
        int tooltipHeight = 8;

        if (textLines.size() > 1) {
            tooltipHeight += 2 + (textLines.size() - 1) * 10;
        }

        if (tooltipX + tooltipWidth > width) {
            tooltipX -= 28 + tooltipWidth;
        }

        if (tooltipY + tooltipHeight + 6 > height) {
            tooltipY = height - tooltipHeight - 6;
        }

        int bgColor = 0xF0100010;
        int borderColorStart = 0x505000FF;
        int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;

        Gui.drawRect(tooltipX - 3, tooltipY - 4, tooltipX + tooltipWidth + 3, tooltipY - 3, bgColor);
        Gui.drawRect(
            tooltipX - 3,
            tooltipY + tooltipHeight + 3,
            tooltipX + tooltipWidth + 3,
            tooltipY + tooltipHeight + 4,
            bgColor);
        Gui.drawRect(tooltipX - 3, tooltipY - 3, tooltipX + tooltipWidth + 3, tooltipY + tooltipHeight + 3, bgColor);
        Gui.drawRect(tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, bgColor);
        Gui.drawRect(
            tooltipX + tooltipWidth + 3,
            tooltipY - 3,
            tooltipX + tooltipWidth + 4,
            tooltipY + tooltipHeight + 3,
            bgColor);

        Gui.drawRect(
            tooltipX - 3,
            tooltipY - 3 + 1,
            tooltipX - 3 + 1,
            tooltipY + tooltipHeight + 3 - 1,
            borderColorStart);
        Gui.drawRect(
            tooltipX + tooltipWidth + 2,
            tooltipY - 3 + 1,
            tooltipX + tooltipWidth + 3,
            tooltipY + tooltipHeight + 3 - 1,
            borderColorStart);
        Gui.drawRect(tooltipX - 3, tooltipY - 3, tooltipX + tooltipWidth + 3, tooltipY - 3 + 1, borderColorStart);
        Gui.drawRect(
            tooltipX - 3,
            tooltipY + tooltipHeight + 2,
            tooltipX + tooltipWidth + 3,
            tooltipY + tooltipHeight + 3,
            borderColorEnd);

        for (int i = 0; i < textLines.size(); ++i) {
            String line = textLines.get(i);
            font.drawStringWithShadow(line, tooltipX, tooltipY, -1);
            if (i == 0) {
                tooltipY += 2;
            }
            tooltipY += 10;
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
