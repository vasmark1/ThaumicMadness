package com.vasmark.thaumicmadness.compact.infusion;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.InfusionRecipe;

public class ContainerCompactInfusionMatrix extends Container {

    public static final int[][] COMP_COORDS = new int[][] { { 152, 18 }, // 0: Top Center
        { 171, 22 }, // 1
        { 187, 33 }, // 2
        { 198, 49 }, // 3
        { 202, 68 }, // 4: Right Center
        { 198, 87 }, // 5
        { 187, 103 }, // 6
        { 171, 114 }, // 7
        { 152, 118 }, // 8: Bottom Center
        { 133, 114 }, // 9
        { 117, 103 }, // 10
        { 106, 87 }, // 11
        { 102, 68 }, // 12: Left Center
        { 106, 49 }, // 13
        { 117, 33 }, // 14
        { 133, 22 } // 15
    };

    private final TileCompactInfusionMatrix tile;
    private int lastCraftProgress;
    private int lastState;
    private int lastAutoStart;
    private int lastStability;

    public ContainerCompactInfusionMatrix(InventoryPlayer playerInv, TileCompactInfusionMatrix tile) {
        this.tile = tile;

        // 1. Central Input Slot (Slot 0) with widened spacing
        this.addSlotToContainer(new Slot(tile, TileCompactInfusionMatrix.SLOT_CENTRAL_IN, 128, 68));

        // 2. Central Output Slot (Slot 1) - Result Slot
        this.addSlotToContainer(new SlotResultOnly(tile, TileCompactInfusionMatrix.SLOT_CENTRAL_OUT, 176, 68));

        // 3. 16 Component Slots (Slots 2..17) in a mathematically perfect circular ring
        for (int i = 0; i < 16; i++) {
            this.addSlotToContainer(
                new Slot(
                    tile,
                    TileCompactInfusionMatrix.SLOT_COMPONENTS_START + i,
                    COMP_COORDS[i][0],
                    COMP_COORDS[i][1]));
        }

        // 4. 4 Stabilizer Upgrade Slots (Slots 18..21) on far right
        for (int i = 0; i < 4; i++) {
            this.addSlotToContainer(
                new Slot(tile, TileCompactInfusionMatrix.SLOT_STABILIZERS_START + i, 260, 30 + i * 22));
        }

        // 5. Player Main Inventory (3 rows x 9 columns)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 74 + col * 18, 168 + row * 18));
            }
        }

        // 6. Player Hotbar (9 slots)
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 74 + col * 18, 226));
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafter) {
        super.addCraftingToCrafters(crafter);
        crafter.sendProgressBarUpdate(this, 0, this.tile.craftProgress);
        crafter.sendProgressBarUpdate(this, 1, this.tile.state);
        crafter.sendProgressBarUpdate(this, 2, this.tile.autoStart ? 1 : 0);
        crafter.sendProgressBarUpdate(this, 3, this.tile.getStabilityPower());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (Object crafterObj : this.crafters) {
            ICrafting crafter = (ICrafting) crafterObj;
            if (this.lastCraftProgress != this.tile.craftProgress) {
                crafter.sendProgressBarUpdate(this, 0, this.tile.craftProgress);
            }
            if (this.lastState != this.tile.state) {
                crafter.sendProgressBarUpdate(this, 1, this.tile.state);
            }
            if (this.lastAutoStart != (this.tile.autoStart ? 1 : 0)) {
                crafter.sendProgressBarUpdate(this, 2, this.tile.autoStart ? 1 : 0);
            }
            int stability = this.tile.getStabilityPower();
            if (this.lastStability != stability) {
                crafter.sendProgressBarUpdate(this, 3, stability);
            }
        }

        this.lastCraftProgress = this.tile.craftProgress;
        this.lastState = this.tile.state;
        this.lastAutoStart = this.tile.autoStart ? 1 : 0;
        this.lastStability = this.tile.getStabilityPower();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        if (id == 0) {
            this.tile.craftProgress = value;
        } else if (id == 1) {
            this.tile.state = (byte) value;
        } else if (id == 2) {
            this.tile.autoStart = (value == 1);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.tile.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack currentStack = slot.getStack();
            itemstack = currentStack.copy();

            // From Matrix to Player Inventory
            if (slotIndex < TileCompactInfusionMatrix.TOTAL_SLOTS) {
                if (!this.mergeItemStack(
                    currentStack,
                    TileCompactInfusionMatrix.TOTAL_SLOTS,
                    this.inventorySlots.size(),
                    true)) {
                    return null;
                }
            }
            // From Player Inventory to Matrix
            else {
                // 1. If it is a stabilizer item, prioritize Stabilizer Slots (18..21)
                if (this.tile.isStabilizerItem(currentStack)) {
                    if (!this.mergeItemStack(
                        currentStack,
                        TileCompactInfusionMatrix.SLOT_STABILIZERS_START,
                        TileCompactInfusionMatrix.SLOT_STABILIZERS_START
                            + TileCompactInfusionMatrix.SLOT_STABILIZERS_COUNT,
                        false)) {
                        return null;
                    }
                }
                // 2. If Central Input (Slot 0) is empty, place into Central Input
                else if (this.getSlot(TileCompactInfusionMatrix.SLOT_CENTRAL_IN)
                    .getStack() == null) {
                        ItemStack single = currentStack.splitStack(1);
                        this.getSlot(TileCompactInfusionMatrix.SLOT_CENTRAL_IN)
                            .putStack(single);
                        if (currentStack.stackSize <= 0) {
                            slot.putStack(null);
                        }
                    }
                // 3. Central Input is occupied: check for matching recipe component slot
                else {
                    boolean placedInGhostSlot = false;
                    ItemStack central = this.getSlot(TileCompactInfusionMatrix.SLOT_CENTRAL_IN)
                        .getStack();
                    if (central != null) {
                        InfusionRecipe ir = findBestRecipeForCentral(central);
                        if (ir != null && ir.getComponents() != null) {
                            ItemStack[] reqComps = ir.getComponents();
                            int n = reqComps.length;
                            for (int i = 0; i < n && i < 16; i++) {
                                int targetSlotIndex = TileCompactInfusionMatrix.SLOT_COMPONENTS_START + ((i * 16) / n);
                                Slot targetSlot = this.getSlot(targetSlotIndex);
                                if (targetSlot != null && !targetSlot.getHasStack()
                                    && InfusionRecipe.areItemStacksEqual(currentStack, reqComps[i], true)) {
                                    ItemStack single = currentStack.splitStack(1);
                                    targetSlot.putStack(single);
                                    placedInGhostSlot = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (!placedInGhostSlot) {
                        // Place into first empty component slot (2..17)
                        if (!this.mergeItemStack(
                            currentStack,
                            TileCompactInfusionMatrix.SLOT_COMPONENTS_START,
                            TileCompactInfusionMatrix.SLOT_COMPONENTS_START
                                + TileCompactInfusionMatrix.SLOT_COMPONENTS_COUNT,
                            false)) {
                            return null;
                        }
                    }
                }
            }

            if (currentStack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (currentStack.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, currentStack);
        }

        return itemstack;
    }

    private InfusionRecipe findBestRecipeForCentral(ItemStack central) {
        if (central == null) return null;
        ArrayList<ItemStack> currentComps = this.tile.getComponentList();
        InfusionRecipe best = null;
        int maxMatches = -1;

        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (r instanceof InfusionRecipe) {
                InfusionRecipe ir = (InfusionRecipe) r;
                if (ir.getRecipeInput() != null
                    && InfusionRecipe.areItemStacksEqual(central, ir.getRecipeInput(), true)) {
                    int matches = 0;
                    if (ir.getComponents() != null) {
                        ArrayList<ItemStack> pool = new ArrayList<ItemStack>(currentComps);
                        for (ItemStack req : ir.getComponents()) {
                            for (int i = 0; i < pool.size(); i++) {
                                ItemStack cur = pool.get(i);
                                if (cur != null && InfusionRecipe.areItemStacksEqual(cur, req, true)) {
                                    matches++;
                                    pool.remove(i);
                                    break;
                                }
                            }
                        }
                    }
                    if (matches > maxMatches) {
                        maxMatches = matches;
                        best = ir;
                    }
                }
            }
        }
        return best;
    }

    public static class SlotResultOnly extends Slot {

        public SlotResultOnly(TileCompactInfusionMatrix inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }
    }
}
