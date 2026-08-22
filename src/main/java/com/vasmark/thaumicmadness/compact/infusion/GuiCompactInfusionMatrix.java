package com.vasmark.thaumicmadness.compact.infusion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.vasmark.thaumicmadness.network.NetworkHandler;
import com.vasmark.thaumicmadness.network.PacketCompactInfusionAction;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.UtilsFX;

@SideOnly(Side.CLIENT)
public class GuiCompactInfusionMatrix extends GuiContainer {

    private final TileCompactInfusionMatrix tile;
    private GuiButton btnStart;
    private GuiButton btnAuto;

    private float rotationAngle = 0.0F;
    private AspectList currentDisplayAspects = null;
    private thaumcraft.api.crafting.InfusionRecipe currentDetectedRecipe = null;
    private final ItemStack[] ghostComponents = new ItemStack[16];

    public GuiCompactInfusionMatrix(InventoryPlayer playerInv, TileCompactInfusionMatrix tile) {
        super(new ContainerCompactInfusionMatrix(playerInv, tile));
        this.tile = tile;
        this.xSize = 310;
        this.ySize = 254;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        // Button 0: Start / Cancel Infusion
        this.btnStart = new GuiButton(
            0,
            this.guiLeft + 114,
            this.guiTop + 142,
            42,
            18,
            StatCollector.translateToLocal("gui.compact_infusion.start"));
        this.buttonList.add(this.btnStart);

        // Button 1: Toggle Auto Start
        this.btnAuto = new GuiButton(
            1,
            this.guiLeft + 164,
            this.guiTop + 142,
            32,
            18,
            StatCollector.translateToLocal("gui.compact_infusion.auto_off"));
        this.buttonList.add(this.btnAuto);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        rotationAngle += 1.5F;
        if (rotationAngle >= 360.0F) {
            rotationAngle -= 360.0F;
        }

        if (this.tile.state != TileCompactInfusionMatrix.STATE_IDLE) {
            this.btnStart.displayString = EnumChatFormatting.RED
                + StatCollector.translateToLocal("gui.compact_infusion.cancel");
        } else {
            this.btnStart.displayString = EnumChatFormatting.LIGHT_PURPLE
                + StatCollector.translateToLocal("gui.compact_infusion.start");
        }

        if (this.tile.autoStart) {
            this.btnAuto.displayString = EnumChatFormatting.GREEN
                + StatCollector.translateToLocal("gui.compact_infusion.auto_on");
        } else {
            this.btnAuto.displayString = EnumChatFormatting.GRAY
                + StatCollector.translateToLocal("gui.compact_infusion.auto_off");
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            byte act = (this.tile.state != TileCompactInfusionMatrix.STATE_IDLE)
                ? PacketCompactInfusionAction.ACTION_CANCEL
                : PacketCompactInfusionAction.ACTION_START;
            NetworkHandler.INSTANCE.sendToServer(
                new PacketCompactInfusionAction(this.tile.xCoord, this.tile.yCoord, this.tile.zCoord, act));
        } else if (button.id == 1) {
            NetworkHandler.INSTANCE.sendToServer(
                new PacketCompactInfusionAction(
                    this.tile.xCoord,
                    this.tile.yCoord,
                    this.tile.zCoord,
                    PacketCompactInfusionAction.ACTION_TOGGLE_AUTO));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        // Check if player clicked the NEI progress arrow
        int arrowX = this.guiLeft + 148;
        int arrowY = this.guiTop + 68;
        if (mouseX >= arrowX && mouseX <= arrowX + 24 && mouseY >= arrowY && mouseY <= arrowY + 16) {
            try {
                if (cpw.mods.fml.common.Loader.isModLoaded("NotEnoughItems")) {
                    codechicken.nei.recipe.GuiCraftingRecipe.openRecipeGui("infusionCrafting");
                }
            } catch (Throwable ignored) {}
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = StatCollector.translateToLocal("tile.thaumicmadness.compact_infusion_matrix.name");
        this.fontRendererObj.drawString(
            EnumChatFormatting.DARK_PURPLE + title,
            this.xSize / 2 - this.fontRendererObj.getStringWidth(title) / 2,
            6,
            0x404040);

        // Left Panel Header (Essentia)
        this.fontRendererObj.drawString(EnumChatFormatting.AQUA + "Эссенция", 14, 18, 0x555555);

        // Right Panel Header (Stability)
        this.fontRendererObj.drawString(EnumChatFormatting.GOLD + "Стабильность", 242, 18, 0x555555);

        // Draw Stability Rating
        int stab = this.tile.getStabilityPower();
        String stabStr = (stab >= 10) ? EnumChatFormatting.GREEN + "100% (+" + stab + ")"
            : EnumChatFormatting.YELLOW + "+" + stab;
        this.fontRendererObj.drawString(stabStr, 244, 120, 0x555555);

        // Determine which AspectList and Ghost Components to show
        this.currentDisplayAspects = null;
        this.currentDetectedRecipe = null;
        Arrays.fill(this.ghostComponents, null);
        boolean isCrafting = (this.tile.state != TileCompactInfusionMatrix.STATE_IDLE);

        if (isCrafting && this.tile.aspectsNeeded != null && this.tile.aspectsNeeded.size() > 0) {
            this.currentDisplayAspects = this.tile.aspectsNeeded;
        } else if (this.tile.getStackInSlot(TileCompactInfusionMatrix.SLOT_CENTRAL_IN) != null) {
            // Find best matching recipe for the central item
            this.currentDetectedRecipe = findBestMatchingRecipe(
                this.tile.getStackInSlot(TileCompactInfusionMatrix.SLOT_CENTRAL_IN));
            if (this.currentDetectedRecipe != null) {
                this.currentDisplayAspects = this.currentDetectedRecipe.getAspects();
                ItemStack[] reqComps = this.currentDetectedRecipe.getComponents();
                if (reqComps != null && reqComps.length > 0) {
                    int n = reqComps.length;
                    for (int i = 0; i < n && i < 16; i++) {
                        int slotIdx = (i * 16) / n;
                        this.ghostComponents[slotIdx] = reqComps[i];
                    }
                }
            }
        }

        // Render Symmetrical Ghost Component Silhouettes in empty slots
        for (int i = 0; i < 16; i++) {
            if (this.ghostComponents[i] != null
                && this.tile.getStackInSlot(TileCompactInfusionMatrix.SLOT_COMPONENTS_START + i) == null) {
                int sx = ContainerCompactInfusionMatrix.COMP_COORDS[i][0];
                int sy = ContainerCompactInfusionMatrix.COMP_COORDS[i][1];
                GL11.glPushMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                itemRender.renderItemAndEffectIntoGUI(
                    this.fontRendererObj,
                    this.mc.getTextureManager(),
                    this.ghostComponents[i],
                    sx,
                    sy);
                RenderHelper.disableStandardItemLighting();

                // Distinct translucent shading overlay
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                drawRect(sx, sy, sx + 16, sy + 16, 0xAA140F22);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }

        if (this.currentDisplayAspects != null && this.currentDisplayAspects.size() > 0) {
            int y = 30;
            for (Aspect a : this.currentDisplayAspects.getAspects()) {
                if (a == null) continue;
                int total = this.currentDisplayAspects.getAmount(a);
                int count;
                String countStr;

                if (isCrafting) {
                    int rem = (this.tile.aspectsRemaining != null) ? this.tile.aspectsRemaining.getAmount(a) : 0;
                    count = total - rem;
                    countStr = (rem <= 0 ? EnumChatFormatting.GREEN : EnumChatFormatting.WHITE) + ""
                        + count
                        + "/"
                        + total;
                } else {
                    int avail = this.tile.getAvailableEssentiaInJars(a);
                    if (avail >= total) {
                        countStr = EnumChatFormatting.GREEN + "" + avail + "/" + total;
                    } else {
                        countStr = EnumChatFormatting.RED + "" + avail + "/" + total;
                    }
                }

                GL11.glPushMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                UtilsFX.drawTag(12, y, a);
                GL11.glPopMatrix();

                this.fontRendererObj.drawString(countStr, 30, y + 4, 0xFFFFFF);
                y += 20;
                if (y > 130) break;
            }
        } else {
            this.fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + "Нет рецепта", 14, 34, 0x888888);
        }
    }

    private thaumcraft.api.crafting.InfusionRecipe findBestMatchingRecipe(ItemStack central) {
        if (central == null) return null;
        ArrayList<ItemStack> currentComps = this.tile.getComponentList();
        thaumcraft.api.crafting.InfusionRecipe best = null;
        int maxMatches = -1;

        for (Object r : thaumcraft.api.ThaumcraftApi.getCraftingRecipes()) {
            if (r instanceof thaumcraft.api.crafting.InfusionRecipe) {
                thaumcraft.api.crafting.InfusionRecipe ir = (thaumcraft.api.crafting.InfusionRecipe) r;
                if (ir.getRecipeInput() != null
                    && thaumcraft.api.crafting.InfusionRecipe.areItemStacksEqual(central, ir.getRecipeInput(), true)) {
                    int matches = countMatchingComponents(ir.getComponents(), currentComps);
                    if (matches > maxMatches) {
                        maxMatches = matches;
                        best = ir;
                    }
                }
            }
        }
        return best;
    }

    private int countMatchingComponents(ItemStack[] required, ArrayList<ItemStack> current) {
        if (required == null) return 0;
        int matches = 0;
        ArrayList<ItemStack> pool = new ArrayList<ItemStack>(current);
        for (ItemStack req : required) {
            for (int i = 0; i < pool.size(); i++) {
                ItemStack cur = pool.get(i);
                if (cur != null && thaumcraft.api.crafting.InfusionRecipe.areItemStacksEqual(cur, req, true)) {
                    matches++;
                    pool.remove(i);
                    break;
                }
            }
        }
        return matches;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);

        // 1. Dark background panel (310 x 254)
        drawRect(this.guiLeft, this.guiTop, this.guiLeft + this.xSize, this.guiTop + this.ySize, 0xEE110E1A);
        drawRect(
            this.guiLeft + 2,
            this.guiTop + 2,
            this.guiLeft + this.xSize - 2,
            this.guiTop + this.ySize - 2,
            0xFF1E172E);

        // Header and separator borders
        drawRect(this.guiLeft + 6, this.guiTop + 16, this.guiLeft + this.xSize - 6, this.guiTop + 17, 0xFF5B3B8C);
        drawRect(this.guiLeft + 6, this.guiTop + 164, this.guiLeft + this.xSize - 6, this.guiTop + 165, 0xFF5B3B8C);

        // Left Panel (Essentia Tracker Background Box)
        drawRect(this.guiLeft + 8, this.guiTop + 26, this.guiLeft + 84, this.guiTop + 158, 0xFF140F21);
        drawSlotRect(this.guiLeft + 8, this.guiTop + 26, 76, 132, 0xFF352B47);

        // Right Panel (Stabilizer Background Box)
        drawRect(this.guiLeft + 236, this.guiTop + 26, this.guiLeft + 302, this.guiTop + 158, 0xFF140F21);
        drawSlotRect(this.guiLeft + 236, this.guiTop + 26, 66, 132, 0xFF4A3860);

        // 2. Center Runic Circle Animation
        int cx = this.guiLeft + 160;
        int cy = this.guiTop + 76;

        GL11.glPushMatrix();
        GL11.glTranslatef(cx, cy, 0);
        GL11.glRotatef(rotationAngle, 0, 0, 1);
        drawCircleRuneOutline(0, 0, 50, 0x888A2BE2);
        GL11.glPopMatrix();

        // 3. Draw Slot Backgrounds with widened altar spacing
        drawSlotRect(this.guiLeft + 127, this.guiTop + 67, 18, 18, 0xFF8A2BE2); // Central in (Slot 0)
        drawSlotRect(this.guiLeft + 175, this.guiTop + 67, 18, 18, 0xFFFFD700); // Result out (Slot 1)

        // Connecting arrow / progress
        drawProgressArrow(this.guiLeft + 148, this.guiTop + 72, this.tile.craftProgress);

        // 16 Component slots
        for (int i = 0; i < ContainerCompactInfusionMatrix.COMP_COORDS.length; i++) {
            int[] pos = ContainerCompactInfusionMatrix.COMP_COORDS[i];
            ItemStack slotItem = this.tile.getStackInSlot(TileCompactInfusionMatrix.SLOT_COMPONENTS_START + i);
            int borderColor = 0xFF4A3860;
            if (this.ghostComponents[i] != null && slotItem == null) {
                borderColor = 0xFF8B263E; // Highlight slot expecting ghost item
            }
            drawSlotRect(this.guiLeft + pos[0] - 1, this.guiTop + pos[1] - 1, 18, 18, borderColor);
        }

        // 4 Stabilizers slots
        for (int i = 0; i < 4; i++) {
            drawSlotRect(this.guiLeft + 259, this.guiTop + 29 + i * 22, 18, 18, 0xFFDAA520);
        }

        // Player Inventory slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlotRect(this.guiLeft + 73 + col * 18, this.guiTop + 167 + row * 18, 18, 18, 0xFF352B47);
            }
        }
        for (int col = 0; col < 9; col++) {
            drawSlotRect(this.guiLeft + 73 + col * 18, this.guiTop + 225, 18, 18, 0xFF423759);
        }
    }

    private void drawSlotRect(int x, int y, int w, int h, int borderColor) {
        drawRect(x, y, x + w, y + h, 0xFF0D0B14);
        drawRect(x, y, x + w, y + 1, borderColor);
        drawRect(x, y + h - 1, x + w, y + h, borderColor);
        drawRect(x, y, x + 1, y + h, borderColor);
        drawRect(x + w - 1, y, x + w, y + h, borderColor);
    }

    private void drawProgressArrow(int x, int y, int progress) {
        drawRect(x, y + 2, x + 24, y + 6, 0xFF2A203B);
        int fill = (progress > 0) ? (progress % 24) : 0;
        if (fill > 0) {
            drawRect(x, y + 2, x + fill, y + 6, 0xFF8A2BE2);
        }
    }

    private void drawCircleRuneOutline(int x, int y, int radius, int color) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(1.5F);
        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        GL11.glColor4f(r, g, b, a);

        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < 32; i++) {
            double angle = 2.0 * Math.PI * i / 32;
            GL11.glVertex2d(x + radius * Math.cos(angle), y + radius * Math.sin(angle));
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Tooltip for Aspect List on the left
        if (this.currentDisplayAspects != null) {
            boolean isCrafting = (this.tile.state != TileCompactInfusionMatrix.STATE_IDLE);
            int y = this.guiTop + 30;
            for (Aspect a : this.currentDisplayAspects.getAspects()) {
                if (a == null) continue;
                if (mouseX >= this.guiLeft + 12 && mouseX <= this.guiLeft + 28 && mouseY >= y && mouseY <= y + 16) {
                    List<String> list = new ArrayList<String>();
                    list.add(a.getName() != null ? a.getName() : "");
                    String desc = a.getLocalizedDescription();
                    if (desc != null && !desc.isEmpty()) {
                        list.add(EnumChatFormatting.GRAY + desc);
                    }
                    int total = this.currentDisplayAspects.getAmount(a);
                    int rem = (isCrafting && this.tile.aspectsRemaining != null)
                        ? this.tile.aspectsRemaining.getAmount(a)
                        : 0;
                    if (isCrafting) {
                        list.add(EnumChatFormatting.DARK_PURPLE + "Требуется: " + (total - rem) + " / " + total);
                    } else {
                        list.add(EnumChatFormatting.DARK_PURPLE + "Требуется: " + total);
                    }
                    int inJars = this.tile.getAvailableEssentiaInJars(a);
                    list.add(
                        (inJars >= total ? EnumChatFormatting.GREEN : EnumChatFormatting.RED) + "В банках рядом: "
                            + inJars);
                    drawHoveringText(list, mouseX, mouseY, fontRendererObj);
                    break;
                }
                y += 20;
                if (y > this.guiTop + 130) break;
            }
        }

        // Tooltip for NEI Progress Arrow
        int arrowX = this.guiLeft + 148;
        int arrowY = this.guiTop + 68;
        if (mouseX >= arrowX && mouseX <= arrowX + 24 && mouseY >= arrowY && mouseY <= arrowY + 16) {
            List<String> list = new ArrayList<String>();
            list.add(EnumChatFormatting.GOLD + "Рецепты алтаря наполнения");
            list.add(EnumChatFormatting.GRAY + "Нажмите, чтобы открыть NEI");
            drawHoveringText(list, mouseX, mouseY, fontRendererObj);
        }

        // Tooltip for Ghost Component items in empty slots
        for (int i = 0; i < 16; i++) {
            if (this.ghostComponents[i] != null
                && this.tile.getStackInSlot(TileCompactInfusionMatrix.SLOT_COMPONENTS_START + i) == null) {
                int sx = this.guiLeft + ContainerCompactInfusionMatrix.COMP_COORDS[i][0];
                int sy = this.guiTop + ContainerCompactInfusionMatrix.COMP_COORDS[i][1];
                if (mouseX >= sx && mouseX <= sx + 16 && mouseY >= sy && mouseY <= sy + 16) {
                    List<String> list = new ArrayList<String>();
                    list.add(EnumChatFormatting.LIGHT_PURPLE + "Требуется для рецепта:");
                    list.add(EnumChatFormatting.WHITE + this.ghostComponents[i].getDisplayName());
                    drawHoveringText(list, mouseX, mouseY, fontRendererObj);
                    break;
                }
            }
        }
    }
}
