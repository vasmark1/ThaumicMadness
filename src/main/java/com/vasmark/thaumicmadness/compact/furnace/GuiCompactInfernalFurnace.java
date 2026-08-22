package com.vasmark.thaumicmadness.compact.furnace;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.vasmark.thaumicmadness.network.NetworkHandler;
import com.vasmark.thaumicmadness.network.PacketCompactFurnaceAction;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCompactInfernalFurnace extends GuiContainer {

    private final TileCompactInfernalFurnace tile;
    private GuiButton btnWithdrawLevel;
    private GuiButton btnWithdrawAll;

    public GuiCompactInfernalFurnace(InventoryPlayer playerInv, TileCompactInfernalFurnace tile) {
        super(new ContainerCompactInfernalFurnace(playerInv, tile));
        this.tile = tile;
        this.xSize = 220;
        this.ySize = 184;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.btnWithdrawLevel = new GuiButton(1, this.guiLeft + 158, this.guiTop + 8, 26, 14, "+1L");
        this.btnWithdrawAll = new GuiButton(2, this.guiLeft + 186, this.guiTop + 8, 24, 14, "MAX");

        this.buttonList.add(this.btnWithdrawLevel);
        this.buttonList.add(this.btnWithdrawAll);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            NetworkHandler.INSTANCE.sendToServer(
                new PacketCompactFurnaceAction(
                    this.tile.xCoord,
                    this.tile.yCoord,
                    this.tile.zCoord,
                    PacketCompactFurnaceAction.ACTION_WITHDRAW_1_LEVEL));
        } else if (button.id == 2) {
            NetworkHandler.INSTANCE.sendToServer(
                new PacketCompactFurnaceAction(
                    this.tile.xCoord,
                    this.tile.yCoord,
                    this.tile.zCoord,
                    PacketCompactFurnaceAction.ACTION_WITHDRAW_ALL));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Title
        String title = StatCollector.translateToLocal("tile.thaumicmadness.compact_infernal_furnace.name");
        this.fontRendererObj.drawString(title, 10, 11, 0xFFAA33);

        // XP Text indicator
        String xpText = "XP: §a" + this.tile.xp;
        this.fontRendererObj.drawString(xpText, 106, 11, 0xFFFFFF);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Tooltips
        int relX = mouseX - this.guiLeft;
        int relY = mouseY - this.guiTop;

        // Tooltip for Brain XP Area
        if (relX >= 100 && relX <= 156 && relY >= 6 && relY <= 24) {
            int estimatedLevels = calcLevelsFromXP(this.tile.xp);
            List<String> tooltip = new ArrayList<String>();
            tooltip.add(EnumChatFormatting.GOLD + "Мозг в банке (Резервуар опыта)");
            tooltip.add(
                EnumChatFormatting.GRAY + "Накоплено: "
                    + EnumChatFormatting.GREEN
                    + this.tile.xp
                    + " / "
                    + TileCompactInfernalFurnace.XP_MAX
                    + " XP");
            tooltip.add(
                EnumChatFormatting.GRAY + "Эквивалент: "
                    + EnumChatFormatting.YELLOW
                    + "~"
                    + estimatedLevels
                    + " уровней");
            tooltip.add(EnumChatFormatting.AQUA + "Кнопки справа позволяют извлечь опыт игроку.");
            this.drawHoveringText(tooltip, mouseX, mouseY, this.fontRendererObj);
        }

        // Tooltip for Filter In Slot
        if (relX >= 90 && relX <= 108 && relY >= 67 && relY <= 85) {
            List<String> tooltip = new ArrayList<String>();
            tooltip.add(EnumChatFormatting.AQUA + "Слот фильтров из серебряного дерева (макс. 16)");
            float currentFlux = this.tile.filterFluxAbsorbed / 4.0F;
            tooltip.add(
                EnumChatFormatting.GRAY + "Поглощение порчи: "
                    + EnumChatFormatting.YELLOW
                    + String.format(java.util.Locale.ROOT, "%.2f", currentFlux)
                    + " / 64.0 ед.");
            tooltip.add(
                EnumChatFormatting.DARK_AQUA + "Каждый предмет: "
                    + EnumChatFormatting.WHITE
                    + "0.25 ед. порчи (256 предметов на 1 фильтр)");
            if (this.tile.getStackInSlot(TileCompactInfernalFurnace.SLOT_FILTER_IN) == null) {
                tooltip.add(EnumChatFormatting.RED + "ВНИМАНИЕ: Без фильтра печь заражает биом и выбрасывает порчу!");
            }
            this.drawHoveringText(tooltip, mouseX, mouseY, this.fontRendererObj);
        }

        // Tooltip for Filter Out Slot
        if (relX >= 110 && relX <= 128 && relY >= 67 && relY <= 85) {
            List<String> tooltip = new ArrayList<String>();
            tooltip.add(EnumChatFormatting.DARK_PURPLE + "Слот отработанных заражённых фильтров (макс. 64)");
            tooltip.add(EnumChatFormatting.GRAY + "Содержит концентрированную эссенцию Vitium (заражение).");
            tooltip.add(EnumChatFormatting.LIGHT_PURPLE + "Можно перерабатывать в алхимической печи и тигеле.");
            this.drawHoveringText(tooltip, mouseX, mouseY, this.fontRendererObj);
        }

        // Tooltips for Upgrade slots
        if (relX >= 15 && relX <= 33 && relY >= 39 && relY <= 81) {
            if (this.tile.getStackInSlot(TileCompactInfernalFurnace.SLOT_UPGRADE_START) == null
                && this.tile.getStackInSlot(TileCompactInfernalFurnace.SLOT_UPGRADE_START + 1) == null) {
                List<String> tooltip = new ArrayList<String>();
                tooltip.add(EnumChatFormatting.LIGHT_PURPLE + "Слоты улучшений (макс. 1 мех на слот)");
                tooltip.add(
                    EnumChatFormatting.GRAY + "Поместите "
                        + EnumChatFormatting.YELLOW
                        + "Магические меха"
                        + EnumChatFormatting.GRAY
                        + " для ускорения плавки и бонусов!");
                this.drawHoveringText(tooltip, mouseX, mouseY, this.fontRendererObj);
            }
        }

        // Restore clean GL state for NEI
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int left = this.guiLeft;
        int top = this.guiTop;

        // 1. Dark obsidian / eldritch background frame (exact 220x184)
        drawRect(left, top, left + this.xSize, top + this.ySize, 0xFF140E16);
        drawRect(left + 2, top + 2, left + this.xSize - 2, top + this.ySize - 2, 0xFF281822);

        // 2. Header panel
        drawRect(left + 6, top + 6, left + this.xSize - 6, top + 26, 0xFF180F16);
        drawRect(left + 7, top + 7, left + this.xSize - 7, top + 25, 0xFF1E131C);

        // 3. Upper workspace panel
        drawRect(left + 6, top + 30, left + this.xSize - 6, top + 92, 0xFF180F16);
        drawRect(left + 7, top + 31, left + this.xSize - 7, top + 91, 0xFF1E131C);

        // 4. Central Inferno Flame Chamber (88, 36 to 130, 64)
        drawRect(left + 88, top + 36, left + 130, top + 64, 0xFF0E060A);
        drawRect(left + 89, top + 37, left + 129, top + 63, 0xFF180A10);

        // Render Animated Lava & Flame
        drawAnimatedInferno(left + 89, top + 37, 40, 26);

        // 5. Draw slot borders
        drawSlotFrames(left, top);

        // 6. Player inventory panel (centered at xOffset = 29)
        drawRect(left + 6, top + 96, left + this.xSize - 6, top + this.ySize - 6, 0xFF180F16);
        drawRect(left + 7, top + 97, left + this.xSize - 7, top + this.ySize - 7, 0xFF1E131C);
        drawPlayerSlotFrames(left, top);

        // Clean up GL state
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private void drawAnimatedInferno(int x, int y, int width, int height) {
        long time = System.currentTimeMillis();
        boolean burning = this.tile.isBurning();

        // Calculate max cook ratio across the 4 independent slots
        float maxProgress = 0.0F;
        for (int i = 0; i < TileCompactInfernalFurnace.SLOT_INPUT_COUNT; i++) {
            int cook = this.tile.cookTimes[i];
            int maxCook = this.tile.maxCookTimes[i] > 0 ? this.tile.maxCookTimes[i] : 26;
            if (cook > 0) {
                float p = (float) cook / (float) maxCook;
                if (p > maxProgress) maxProgress = p;
            }
        }

        // 1. Lava Bed at bottom
        int lavaTop = y + height - 8;
        drawRect(x, lavaTop, x + width, y + height, 0xFFCC3300);
        drawRect(x + 1, lavaTop + 1, x + width - 1, y + height - 1, 0xFFFF6600);

        // Boiling Lava Wave
        for (int px = 0; px < width; px++) {
            double wave = Math.sin((time / 150.0) + px * 0.6) * 1.5;
            int waveY = (int) (lavaTop + wave);
            drawRect(x + px, waveY, x + px + 1, y + height, 0xFFFF9900);
        }

        // 2. Active Blazing Flame Column
        if (burning || maxProgress > 0.0F) {
            int flameMaxH = height - 4;
            int activeH = Math.max(10, (int) (maxProgress * flameMaxH));
            if (!burning) activeH = 10;

            // Flame flicker
            double flicker = Math.sin(time / 80.0) * 2.0 + Math.cos(time / 120.0) * 1.5;
            int currentFlameH = Math.min(flameMaxH, (int) (activeH + flicker));
            int flameBaseY = y + height - 4;
            int flameStartY = Math.max(y + 2, flameBaseY - currentFlameH);

            // Outer red flame
            drawRect(x + 3, flameStartY, x + width - 3, flameBaseY, 0xFFDD2200);
            // Mid orange flame
            drawRect(x + 6, flameStartY + 2, x + width - 6, flameBaseY, 0xFFFF6600);
            // Core yellow heat
            drawRect(x + 10, flameStartY + 4, x + width - 10, flameBaseY, 0xFFFFEE33);

            // Embers floating upwards
            for (int i = 0; i < 4; i++) {
                int emberX = x + 4 + (int) ((time / (50 + i * 20) + i * 9) % (width - 8));
                int emberY = (int) (flameBaseY - ((time / (30 + i * 10) + i * 15) % Math.max(1, currentFlameH)));
                drawRect(emberX, emberY, emberX + 2, emberY + 2, 0xFFFFFF66);
            }
        } else {
            // Idle ember glow
            drawRect(x + 4, y + height - 6, x + width - 4, y + height - 2, 0xFF881100);
            drawRect(x + 8, y + height - 5, x + width - 8, y + height - 3, 0xFFCC3300);
        }
    }

    private void drawSlotFrames(int left, int top) {
        // Upgrade slots (16, 40) and (16, 62)
        drawSlotBox(left + 15, top + 39, 0xFF663366);
        drawSlotBox(left + 15, top + 61, 0xFF663366);

        // Input 2x2 grid (44, 40), (64, 40), (44, 62), (64, 62)
        drawSlotBox(left + 43, top + 39, 0xFF555555);
        drawSlotBox(left + 63, top + 39, 0xFF555555);
        drawSlotBox(left + 43, top + 61, 0xFF555555);
        drawSlotBox(left + 63, top + 61, 0xFF555555);

        // Filter Slots under Flame: Slot 14 (Fresh) at (91, 68), Slot 15 (Tainted) at (111, 68)
        drawSlotBox(left + 90, top + 67, 0xFF20B2AA); // Cyan/Teal border for fresh Silverwood filter
        drawSlotBox(left + 110, top + 67, 0xFF8A2BE2); // Purple/Violet border for Tainted filter

        // Primary Output 2x2 grid (138, 40), (158, 40), (138, 62), (158, 62)
        drawSlotBox(left + 137, top + 39, 0xFF995511);
        drawSlotBox(left + 157, top + 39, 0xFF995511);
        drawSlotBox(left + 137, top + 61, 0xFF995511);
        drawSlotBox(left + 157, top + 61, 0xFF995511);

        // Bonus Output 2x2 grid (180, 40), (200, 40), (180, 62), (200, 62)
        drawSlotBox(left + 179, top + 39, 0xFFB8860B);
        drawSlotBox(left + 199, top + 39, 0xFFB8860B);
        drawSlotBox(left + 179, top + 61, 0xFFB8860B);
        drawSlotBox(left + 199, top + 61, 0xFFB8860B);
    }

    private void drawPlayerSlotFrames(int left, int top) {
        int xOffset = left + 29;
        int yOffset = top + 102;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlotBox(xOffset + col * 18 - 1, yOffset + row * 18 - 1, 0xFF33222A);
            }
        }
        for (int col = 0; col < 9; col++) {
            drawSlotBox(xOffset + col * 18 - 1, top + 160 - 1, 0xFF442D38);
        }
    }

    private void drawSlotBox(int x, int y, int borderColor) {
        drawRect(x, y, x + 18, y + 18, borderColor);
        drawRect(x + 1, y + 1, x + 17, y + 17, 0xFF140D12);
    }

    private int calcLevelsFromXP(int totalXP) {
        if (totalXP <= 0) return 0;
        int level = 0;
        int remaining = totalXP;
        while (remaining > 0) {
            int cap = (level >= 30) ? (62 + (level - 30) * 7) : ((level >= 15) ? (17 + (level - 15) * 3) : 17);
            if (remaining >= cap) {
                remaining -= cap;
                level++;
            } else {
                break;
            }
        }
        return level;
    }
}
