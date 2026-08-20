package com.vasmark.thaumicmadness.nei;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import com.vasmark.thaumicmadness.network.NetworkHandler;
import com.vasmark.thaumicmadness.network.PacketWarpControl;

import codechicken.nei.Button;
import codechicken.nei.LayoutManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.Widget;
import codechicken.nei.api.LayoutStyle;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerDrawHandler;
import codechicken.nei.guihook.IContainerInputHandler;
import codechicken.nei.guihook.IContainerTooltipHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NEIWarpHandler implements IContainerDrawHandler, IContainerInputHandler, IContainerTooltipHandler {

    private static final NEIWarpHandler INSTANCE = new NEIWarpHandler();
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        GuiContainerManager.addDrawHandler(INSTANCE);
        GuiContainerManager.addInputHandler(INSTANCE);
        GuiContainerManager.addTooltipHandler(INSTANCE);
    }

    public static class NEIWarpButton extends Button {

        public final byte action;
        public final String tipKey;

        public NEIWarpButton(String label, String tipKey, byte action) {
            super(label);
            this.tipKey = tipKey;
            this.action = action;
            this.w = 16;
            this.h = 16;
        }

        @Override
        public boolean onButtonPress(boolean rightClick) {
            if (!rightClick) {
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
                NetworkHandler.INSTANCE.sendToServer(new PacketWarpControl(this.action));
                return true;
            }
            return false;
        }

        @Override
        public List<String> handleTooltip(int mouseX, int mouseY, List<String> currenttip) {
            if (contains(mouseX, mouseY)) {
                currenttip.add("§5§l" + StatCollector.translateToLocal("mymod.warp.btn." + tipKey + ".title"));
                currenttip.add("§7" + StatCollector.translateToLocal("mymod.warp.btn." + tipKey + ".desc"));
            }
            return currenttip;
        }
    }

    private final NEIWarpButton[] buttons = new NEIWarpButton[] {
        new NEIWarpButton("§e0", "clear", PacketWarpControl.ACTION_CLEAR),
        new NEIWarpButton("§5M", "max", PacketWarpControl.ACTION_SET_MAX),
        new NEIWarpButton("§c-", "sub", PacketWarpControl.ACTION_SUB_10),
        new NEIWarpButton("§a+", "add", PacketWarpControl.ACTION_ADD_10) };

    private boolean isNEIActive() {
        return NEIClientConfig.isEnabled() && !NEIClientConfig.isHidden();
    }

    private static class Box {

        final int x, y, w, h;

        Box(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        boolean intersects(int ox, int oy, int ow, int oh) {
            return x < ox + ow && x + w > ox && y < oy + oh && y + h > oy;
        }
    }

    private List<Box> getOccupiedBoxes(GuiContainer gui) {
        List<Box> boxes = new ArrayList<Box>();

        // 1. Container GUI window area
        try {
            int guiLeft = ReflectionHelper.findField(GuiContainer.class, "field_147003_i", "guiLeft")
                .getInt(gui);
            int guiTop = ReflectionHelper.findField(GuiContainer.class, "field_147009_r", "guiTop")
                .getInt(gui);
            int xSize = ReflectionHelper.findField(GuiContainer.class, "field_146999_f", "xSize")
                .getInt(gui);
            int ySize = ReflectionHelper.findField(GuiContainer.class, "field_147000_e", "ySize")
                .getInt(gui);
            boxes.add(new Box(guiLeft, guiTop, xSize, ySize));
        } catch (Throwable ignored) {}

        // 2. Native NEI top-left action widgets
        addWidgetBox(boxes, LayoutManager.gamemode);
        addWidgetBox(boxes, LayoutManager.rain);
        addWidgetBox(boxes, LayoutManager.magnet);
        addWidgetBox(boxes, LayoutManager.heal);
        addWidgetBox(boxes, LayoutManager.delete);

        if (LayoutManager.timeButtons != null) {
            for (Button b : LayoutManager.timeButtons) {
                addWidgetBox(boxes, b);
            }
        }

        // 3. Scan all active widgets registered in NEI LayoutManager
        try {
            Field drawWidgetsField = ReflectionHelper.findField(LayoutManager.class, "drawWidgets");
            drawWidgetsField.setAccessible(true);
            java.util.Set<?> set = (java.util.Set<?>) drawWidgetsField.get(null);
            if (set != null) {
                for (Object obj : set) {
                    if (obj instanceof Widget) {
                        addWidgetBox(boxes, (Widget) obj);
                    }
                }
            }
        } catch (Throwable ignored) {}

        return boxes;
    }

    private void addWidgetBox(List<Box> boxes, Widget widget) {
        if (widget != null && widget.w > 0 && widget.h > 0) {
            boxes.add(new Box(widget.x, widget.y, widget.w, widget.h));
        }
    }

    /**
     * Automatic collision detection and redistribution:
     * Scans NEI grid candidate cells and places each button into the first
     * available, non-colliding cell so buttons never overlap or hide behind other widgets.
     */
    private void updatePositions(GuiContainer gui) {
        List<Box> occupied = getOccupiedBoxes(gui);

        for (NEIWarpButton btn : buttons) {
            boolean placed = false;
            for (int row = 0; row < 15 && !placed; row++) {
                int candY = row * 18 + 3;
                for (int col = 0; col < 18 && !placed; col++) {
                    int candX = col * 20 + 6;
                    int candW = btn.w;
                    int candH = btn.h;

                    if (candX + candW > gui.width || candY + candH > gui.height) {
                        continue;
                    }

                    boolean collides = false;
                    for (Box box : occupied) {
                        if (box.intersects(candX, candY, candW, candH)) {
                            collides = true;
                            break;
                        }
                    }

                    if (!collides) {
                        btn.x = candX;
                        btn.y = candY;
                        occupied.add(new Box(candX, candY, candW, candH));
                        placed = true;
                    }
                }
            }
        }
    }

    // ========================================================
    // IContainerDrawHandler
    // ========================================================

    @Override
    public void onPreDraw(GuiContainer gui) {
        if (!isNEIActive()) return;
        updatePositions(gui);
    }

    @Override
    public void renderObjects(GuiContainer gui, int mouseX, int mouseY) {
        if (!isNEIActive()) return;
        updatePositions(gui);

        LayoutStyle style = LayoutManager.getLayoutStyle();
        if (style != null) {
            for (NEIWarpButton btn : buttons) {
                style.drawButton(btn, mouseX, mouseY);
            }
        }
    }

    @Override
    public void postRenderObjects(GuiContainer gui, int mouseX, int mouseY) {}

    @Override
    public void renderSlotUnderlay(GuiContainer gui, Slot slot) {}

    @Override
    public void renderSlotOverlay(GuiContainer gui, Slot slot) {}

    // ========================================================
    // IContainerInputHandler
    // ========================================================

    @Override
    public boolean mouseClicked(GuiContainer gui, int mouseX, int mouseY, int button) {
        if (!isNEIActive() || button != 0) return false;
        updatePositions(gui);

        for (NEIWarpButton btn : buttons) {
            if (btn.contains(mouseX, mouseY)) {
                btn.onButtonPress(false);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMouseClicked(GuiContainer gui, int mouseX, int mouseY, int button) {}

    @Override
    public void onMouseUp(GuiContainer gui, int mouseX, int mouseY, int button) {}

    @Override
    public boolean keyTyped(GuiContainer gui, char keyChar, int keyCode) {
        return false;
    }

    @Override
    public void onKeyTyped(GuiContainer gui, char keyChar, int keyCode) {}

    @Override
    public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyCode) {
        return false;
    }

    @Override
    public boolean mouseScrolled(GuiContainer gui, int mouseX, int mouseY, int scrolled) {
        return false;
    }

    @Override
    public void onMouseScrolled(GuiContainer gui, int mouseX, int mouseY, int scrolled) {}

    @Override
    public void onMouseDragged(GuiContainer gui, int mouseX, int mouseY, int button, long heldTime) {}

    // ========================================================
    // IContainerTooltipHandler
    // ========================================================

    @Override
    public List<String> handleTooltip(GuiContainer gui, int mouseX, int mouseY, List<String> currenttip) {
        if (!isNEIActive()) return currenttip;
        updatePositions(gui);

        for (NEIWarpButton btn : buttons) {
            btn.handleTooltip(mouseX, mouseY, currenttip);
        }
        return currenttip;
    }

    @Override
    public List<String> handleItemDisplayName(GuiContainer gui, ItemStack itemstack, List<String> currenttip) {
        return currenttip;
    }

    @Override
    public List<String> handleItemTooltip(GuiContainer gui, ItemStack itemstack, int mouseX, int mouseY,
        List<String> currenttip) {
        return currenttip;
    }
}
