package com.vasmark.thaumicmadness.nodetracker.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.vasmark.thaumicmadness.nodetracker.NodeData;
import com.vasmark.thaumicmadness.nodetracker.NodeTrackerManager;
import com.vasmark.thaumicmadness.nodetracker.NodeTrackerManager.SortMode;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiNodeTracker extends GuiScreen {

    private static final ResourceLocation TEXTURE_PARCHMENT = new ResourceLocation(
        "thaumcraft",
        "textures/gui/gui_researchback.png");

    private GuiTextField searchField;
    private GuiNodeSlotList slotList;

    private int selectedDimIndex = -1; // -1 means ALL
    private int selectedTypeIndex = 0;
    private int selectedSortIndex = 0;

    public String hoveredTooltip = null;

    private static final String[] TYPE_FILTERS = new String[] { "ALL", "NORMAL", "BRIGHT", "PALE", "FADING", "PURE",
        "DARK", "HUNGRY", "TAINTED", "UNSTABLE" };

    private static final SortMode[] SORT_MODES = new SortMode[] { SortMode.DISTANCE_ASC, SortMode.DISTANCE_DESC,
        SortMode.TOTAL_VIS_DESC, SortMode.TOTAL_VIS_ASC, SortMode.AER, SortMode.IGNIS, SortMode.AQUA, SortMode.TERRA,
        SortMode.ORDO, SortMode.PERDITIO };

    private GuiButton btnDimFilter;
    private GuiButton btnTypeFilter;
    private GuiButton btnSort;
    private GuiButton btnClearTarget;
    private GuiButton btnClose;

    private String toastNotification = "";
    private long toastExpiry = 0;

    public static final int PANEL_WIDTH = 320;

    public void setNotification(String text) {
        this.toastNotification = text;
        this.toastExpiry = System.currentTimeMillis() + 2500;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        int panelLeft = (this.width - PANEL_WIDTH) / 2;

        // Search Field
        this.searchField = new GuiTextField(this.fontRendererObj, panelLeft + 8, 23, 118, 14);
        this.searchField.setMaxStringLength(40);
        this.searchField.setFocused(false);

        // Top Filter Buttons
        btnDimFilter = new GuiButton(1, panelLeft + 130, 22, 58, 16, getDimFilterLabel());
        btnTypeFilter = new GuiButton(2, panelLeft + 190, 22, 58, 16, getTypeFilterLabel());
        btnSort = new GuiButton(3, panelLeft + 250, 22, 62, 16, getSortLabel());

        this.buttonList.add(btnDimFilter);
        this.buttonList.add(btnTypeFilter);
        this.buttonList.add(btnSort);

        // Bottom action buttons
        btnClearTarget = new GuiButton(
            4,
            panelLeft + 8,
            this.height - 24,
            100,
            18,
            StatCollector.translateToLocal("nodetracker.btn.clear_target"));
        btnClose = new GuiButton(
            5,
            panelLeft + PANEL_WIDTH - 78,
            this.height - 24,
            70,
            18,
            StatCollector.translateToLocal("gui.done"));

        this.buttonList.add(btnClearTarget);
        this.buttonList.add(btnClose);

        // Node Slot List
        this.slotList = new GuiNodeSlotList(this, 42, this.height - 28);
        refreshNodeList();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    public void refreshNodeList() {
        Minecraft mc = Minecraft.getMinecraft();
        double px = mc.thePlayer != null ? mc.thePlayer.posX : 0;
        double py = mc.thePlayer != null ? mc.thePlayer.posY : 0;
        double pz = mc.thePlayer != null ? mc.thePlayer.posZ : 0;

        String search = searchField != null ? searchField.getText() : "";
        Integer dim = null;
        List<Integer> dims = NodeTrackerManager.getInstance()
            .getAvailableDimensions();
        if (selectedDimIndex >= 0 && selectedDimIndex < dims.size()) {
            dim = dims.get(selectedDimIndex);
        }

        String type = TYPE_FILTERS[selectedTypeIndex % TYPE_FILTERS.length];
        SortMode sort = SORT_MODES[selectedSortIndex % SORT_MODES.length];

        List<NodeData> filtered = NodeTrackerManager.getInstance()
            .getFilteredAndSortedNodes(search, dim, type, sort, px, py, pz);
        if (this.slotList != null) {
            this.slotList.updateList(filtered);
        }
    }

    private String getDimFilterLabel() {
        if (selectedDimIndex < 0) return "Все миры";
        List<Integer> dims = NodeTrackerManager.getInstance()
            .getAvailableDimensions();
        if (selectedDimIndex < dims.size()) {
            int dim = dims.get(selectedDimIndex);
            return NodeTrackerManager.getInstance()
                .getDimensionName(dim);
        }
        return "Все миры";
    }

    private String getTypeFilterLabel() {
        String type = TYPE_FILTERS[selectedTypeIndex % TYPE_FILTERS.length];
        if ("ALL".equalsIgnoreCase(type)) return "Все типы";
        String key = "nodetracker.type." + type.toLowerCase();
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key);
        }
        return type;
    }

    private String getSortLabel() {
        SortMode sort = SORT_MODES[selectedSortIndex % SORT_MODES.length];
        switch (sort) {
            case DISTANCE_ASC:
                return "Дистанция ▲";
            case DISTANCE_DESC:
                return "Дистанция ▼";
            case TOTAL_VIS_DESC:
                return "Всего вис ▼";
            case TOTAL_VIS_ASC:
                return "Всего вис ▲";
            default:
                return sort.name() + " ▼";
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) { // Dim Filter
            List<Integer> dims = NodeTrackerManager.getInstance()
                .getAvailableDimensions();
            selectedDimIndex++;
            if (selectedDimIndex >= dims.size()) {
                selectedDimIndex = -1;
            }
            btnDimFilter.displayString = getDimFilterLabel();
            refreshNodeList();
        } else if (button.id == 2) { // Type Filter
            selectedTypeIndex = (selectedTypeIndex + 1) % TYPE_FILTERS.length;
            btnTypeFilter.displayString = getTypeFilterLabel();
            refreshNodeList();
        } else if (button.id == 3) { // Sort Mode
            selectedSortIndex = (selectedSortIndex + 1) % SORT_MODES.length;
            btnSort.displayString = getSortLabel();
            refreshNodeList();
        } else if (button.id == 4) { // Clear Target
            NodeTrackerManager.getInstance()
                .clearActiveTarget();
            setNotification("§e" + StatCollector.translateToLocal("nodetracker.target_cleared"));
        } else if (button.id == 5) { // Close
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (this.searchField.textboxKeyTyped(typedChar, keyCode)) {
            refreshNodeList();
            return;
        }

        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.slotList != null) {
            this.slotList.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which) {
        super.mouseMovedOrUp(mouseX, mouseY, which);
        if (this.slotList != null) {
            this.slotList.mouseMovedOrUp(mouseX, mouseY, which);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Dark translucent game background
        this.drawDefaultBackground();

        int panelLeft = (this.width - PANEL_WIDTH) / 2;

        // Draw Thaumcraft parchment tiled background
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        // Dark outer boundary
        drawRect(panelLeft - 3, 3, panelLeft + PANEL_WIDTH + 3, this.height - 3, 0xFF140F0A);
        drawRect(panelLeft - 2, 4, panelLeft + PANEL_WIDTH + 2, this.height - 4, 0xFF8A6536);
        drawRect(panelLeft - 1, 5, panelLeft + PANEL_WIDTH + 1, this.height - 5, 0xFF4A351C);

        // Bind parchment texture
        this.mc.getTextureManager()
            .bindTexture(TEXTURE_PARCHMENT);
        GL11.glColor4f(0.85F, 0.82F, 0.75F, 1.0F);
        drawTexturedModalRect(panelLeft, 6, 0, 0, PANEL_WIDTH, this.height - 12);

        // Semi-transparent parchment tint
        drawRect(panelLeft, 6, panelLeft + PANEL_WIDTH, this.height - 6, 0x882A2016);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

        // Reset tooltip before slot list draw
        this.hoveredTooltip = null;

        // Draw custom slot list with clean scissor clipping and custom scrollbar
        if (this.slotList != null) {
            this.slotList.draw(mouseX, mouseY, partialTicks);
        }

        // Draw solid Thaumcraft header bar to cleanly mask scrolled items
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        drawRect(panelLeft, 6, panelLeft + PANEL_WIDTH, 42, 0xF6201810);
        drawRect(panelLeft, 41, panelLeft + PANEL_WIDTH, 42, 0xFFA67C42);

        // Draw solid footer bar
        drawRect(panelLeft, this.height - 28, panelLeft + PANEL_WIDTH, this.height - 6, 0xF6201810);
        drawRect(panelLeft, this.height - 29, panelLeft + PANEL_WIDTH, this.height - 28, 0xFFA67C42);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

        // Draw header title and stats
        String title = "§6§l" + StatCollector.translateToLocal("nodetracker.title");
        this.fontRendererObj.drawStringWithShadow(title, panelLeft + 8, 9, 0xFFE5C16C);

        int totalCount = NodeTrackerManager.getInstance()
            .getNodes()
            .size();
        int filteredCount = this.slotList != null ? this.slotList.getCurrentList()
            .size() : 0;
        String stats = "§7(" + filteredCount + "/" + totalCount + ")";
        this.fontRendererObj
            .drawStringWithShadow(stats, panelLeft + this.fontRendererObj.getStringWidth(title) + 12, 9, 0xBBBBBB);

        // Draw search field
        this.searchField.drawTextBox();
        if (this.searchField.getText()
            .isEmpty() && !this.searchField.isFocused()) {
            this.fontRendererObj.drawString("§8Поиск аспектов, координат...", panelLeft + 12, 26, 0x888888);
        }

        // Draw buttons
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Toast notification
        if (System.currentTimeMillis() < toastExpiry && !toastNotification.isEmpty()) {
            int toastW = this.fontRendererObj.getStringWidth(toastNotification) + 16;
            int toastX = (this.width - toastW) / 2;
            int toastY = this.height - 52;

            drawRect(toastX, toastY, toastX + toastW, toastY + 16, 0xF0140F0A);
            drawRect(toastX + 1, toastY + 1, toastX + toastW - 1, toastY + 15, 0xFFE5C16C);
            this.fontRendererObj.drawStringWithShadow(toastNotification, toastX + 8, toastY + 4, 0xFFFFFF);
        }

        // Draw aspect hover tooltip
        if (this.hoveredTooltip != null && !this.hoveredTooltip.isEmpty()) {
            List<String> list = new ArrayList<String>();
            list.add(this.hoveredTooltip);
            this.drawHoveringText(list, mouseX, mouseY, this.fontRendererObj);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
