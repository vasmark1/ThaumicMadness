package com.vasmark.thaumicmadness.compact.furnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import com.vasmark.thaumicmadness.item.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerCompactInfernalFurnace extends Container {

    private final TileCompactInfernalFurnace tile;
    private int[] lastCookTimes = new int[TileCompactInfernalFurnace.SLOT_INPUT_COUNT];
    private int[] lastMaxCookTimes = new int[TileCompactInfernalFurnace.SLOT_INPUT_COUNT];
    private int lastXp;
    private int lastFilterFlux;

    public ContainerCompactInfernalFurnace(InventoryPlayer playerInv, TileCompactInfernalFurnace tile) {
        this.tile = tile;

        // 1. Upgrades (Slots 12..13) - Strictly limited to 1 item per slot!
        this.addSlotToContainer(new SlotUpgrade(tile, TileCompactInfernalFurnace.SLOT_UPGRADE_START, 16, 40));
        this.addSlotToContainer(new SlotUpgrade(tile, TileCompactInfernalFurnace.SLOT_UPGRADE_START + 1, 16, 62));

        // 2. Input 2x2 Grid (Slots 0..3) - Left Center (44, 40), (64, 40), (44, 62), (64, 62)
        this.addSlotToContainer(new Slot(tile, 0, 44, 40));
        this.addSlotToContainer(new Slot(tile, 1, 64, 40));
        this.addSlotToContainer(new Slot(tile, 2, 44, 62));
        this.addSlotToContainer(new Slot(tile, 3, 64, 62));

        // 3. Primary Output 2x2 Grid (Slots 4..7) - Center Right (138, 40), (158, 40), (138, 62), (158, 62)
        this.addSlotToContainer(new SlotFurnaceOutput(tile, 4, 138, 40));
        this.addSlotToContainer(new SlotFurnaceOutput(tile, 5, 158, 40));
        this.addSlotToContainer(new SlotFurnaceOutput(tile, 6, 138, 62));
        this.addSlotToContainer(new SlotFurnaceOutput(tile, 7, 158, 62));

        // 4. Bonus Nuggets / Rare Loot 2x2 Grid (Slots 8..11) - Far Right (180, 40), (200, 40), (180, 62), (200, 62)
        this.addSlotToContainer(new SlotFurnaceOutput(tile, 8, 180, 40));
        this.addSlotToContainer(new SlotFurnaceOutput(tile, 9, 200, 40));
        this.addSlotToContainer(new SlotFurnaceOutput(tile, 10, 180, 62));
        this.addSlotToContainer(new SlotFurnaceOutput(tile, 11, 200, 62));

        // 5. Filter Slots (under central flame): Slot 14 (Fresh, max 16) at (91, 68), Slot 15 (Tainted, max 64) at
        // (111, 68)
        this.addSlotToContainer(new SlotFilterIn(tile, TileCompactInfernalFurnace.SLOT_FILTER_IN, 91, 68));
        this.addSlotToContainer(new SlotFurnaceOutput(tile, TileCompactInfernalFurnace.SLOT_FILTER_OUT, 111, 68));

        // 6. Player Inventory (3 rows x 9) - Centered at x=29, y=102
        int xOffset = 29;
        int yOffset = 102;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, xOffset + col * 18, yOffset + row * 18));
            }
        }

        // 7. Player Hotbar (9 slots) - at y=160
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, xOffset + col * 18, 160));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.tile.isUseableByPlayer(player);
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafter) {
        super.addCraftingToCrafters(crafter);
        for (int i = 0; i < TileCompactInfernalFurnace.SLOT_INPUT_COUNT; i++) {
            crafter.sendProgressBarUpdate(this, i, this.tile.cookTimes[i]);
            crafter.sendProgressBarUpdate(this, i + 4, this.tile.maxCookTimes[i]);
        }
        crafter.sendProgressBarUpdate(this, 8, this.tile.xp & 0xFFFF);
        crafter.sendProgressBarUpdate(this, 9, (this.tile.xp >>> 16) & 0xFFFF);
        crafter.sendProgressBarUpdate(this, 10, this.tile.filterFluxAbsorbed);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (int c = 0; c < this.crafters.size(); c++) {
            ICrafting crafter = (ICrafting) this.crafters.get(c);

            for (int i = 0; i < TileCompactInfernalFurnace.SLOT_INPUT_COUNT; i++) {
                if (this.lastCookTimes[i] != this.tile.cookTimes[i]) {
                    crafter.sendProgressBarUpdate(this, i, this.tile.cookTimes[i]);
                }
                if (this.lastMaxCookTimes[i] != this.tile.maxCookTimes[i]) {
                    crafter.sendProgressBarUpdate(this, i + 4, this.tile.maxCookTimes[i]);
                }
            }

            if (this.lastXp != this.tile.xp) {
                crafter.sendProgressBarUpdate(this, 8, this.tile.xp & 0xFFFF);
                crafter.sendProgressBarUpdate(this, 9, (this.tile.xp >>> 16) & 0xFFFF);
            }

            if (this.lastFilterFlux != this.tile.filterFluxAbsorbed) {
                crafter.sendProgressBarUpdate(this, 10, this.tile.filterFluxAbsorbed);
            }
        }

        for (int i = 0; i < TileCompactInfernalFurnace.SLOT_INPUT_COUNT; i++) {
            this.lastCookTimes[i] = this.tile.cookTimes[i];
            this.lastMaxCookTimes[i] = this.tile.maxCookTimes[i];
        }
        this.lastXp = this.tile.xp;
        this.lastFilterFlux = this.tile.filterFluxAbsorbed;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id >= 0 && id < 4) {
            this.tile.cookTimes[id] = data;
        } else if (id >= 4 && id < 8) {
            this.tile.maxCookTimes[id - 4] = data;
        } else if (id == 8) {
            this.tile.xp = (this.tile.xp & 0xFFFF0000) | (data & 0xFFFF);
        } else if (id == 9) {
            this.tile.xp = (this.tile.xp & 0x0000FFFF) | ((data & 0xFFFF) << 16);
        } else if (id == 10) {
            this.tile.filterFluxAbsorbed = data;
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack current = slot.getStack();
            itemstack = current.copy();

            // From Furnace Slots -> to Player Inventory
            if (slotIndex < TileCompactInfernalFurnace.TOTAL_SLOTS) {
                if (!this.mergeItemStack(
                    current,
                    TileCompactInfernalFurnace.TOTAL_SLOTS,
                    this.inventorySlots.size(),
                    true)) {
                    return null;
                }
                slot.onSlotChange(current, itemstack);
            } else {
                // From Player Inventory -> to Furnace
                if (current.getItem() == ModItems.itemSilverwoodFilter) {
                    // Try to place into Filter In slot (container index 14)
                    Slot filterSlot = (Slot) this.inventorySlots.get(14);
                    if (!filterSlot.getHasStack()) {
                        ItemStack toMove = current.splitStack(Math.min(current.stackSize, 16));
                        filterSlot.putStack(toMove);
                        filterSlot.onSlotChanged();
                    } else if (filterSlot.getStack().stackSize < 16) {
                        int space = 16 - filterSlot.getStack().stackSize;
                        int added = Math.min(space, current.stackSize);
                        filterSlot.getStack().stackSize += added;
                        current.stackSize -= added;
                        filterSlot.onSlotChanged();
                    } else {
                        return null;
                    }
                } else if (TileCompactInfernalFurnace.isBellows(current)) {
                    // Try to place 1 Bellows in Upgrade Slot 0, or Upgrade Slot 1
                    Slot slot0 = (Slot) this.inventorySlots.get(0);
                    Slot slot1 = (Slot) this.inventorySlots.get(1);
                    if (!slot0.getHasStack()) {
                        ItemStack single = current.splitStack(1);
                        slot0.putStack(single);
                        slot0.onSlotChanged();
                    } else if (!slot1.getHasStack()) {
                        ItemStack single = current.splitStack(1);
                        slot1.putStack(single);
                        slot1.onSlotChanged();
                    } else {
                        return null;
                    }
                } else if (FurnaceRecipes.smelting()
                    .getSmeltingResult(current) != null) {
                        // Try input slots (indices 2..5 in container = slots 0..3 of tile)
                        if (!this.mergeItemStack(current, 2, 6, false)) {
                            return null;
                        }
                    } else if (slotIndex >= TileCompactInfernalFurnace.TOTAL_SLOTS
                        && slotIndex < this.inventorySlots.size() - 9) {
                            // From main player inventory to hotbar
                            if (!this.mergeItemStack(
                                current,
                                this.inventorySlots.size() - 9,
                                this.inventorySlots.size(),
                                false)) {
                                return null;
                            }
                        } else if (slotIndex >= this.inventorySlots.size() - 9) {
                            // From hotbar to main inventory
                            if (!this.mergeItemStack(
                                current,
                                TileCompactInfernalFurnace.TOTAL_SLOTS,
                                this.inventorySlots.size() - 9,
                                false)) {
                                return null;
                            }
                        }
            }

            if (current.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (current.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, current);
        }

        return itemstack;
    }

    public TileCompactInfernalFurnace getTile() {
        return this.tile;
    }

    public static class SlotUpgrade extends Slot {

        public SlotUpgrade(IInventory inv, int id, int x, int y) {
            super(inv, id, x, y);
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return TileCompactInfernalFurnace.isBellows(stack);
        }
    }

    public static class SlotFilterIn extends Slot {

        public SlotFilterIn(IInventory inv, int id, int x, int y) {
            super(inv, id, x, y);
        }

        @Override
        public int getSlotStackLimit() {
            return 16;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack != null && stack.getItem() == ModItems.itemSilverwoodFilter;
        }
    }

    private static class SlotFurnaceOutput extends Slot {

        public SlotFurnaceOutput(TileCompactInfernalFurnace tile, int slot, int x, int y) {
            super(tile, slot, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }
    }
}
