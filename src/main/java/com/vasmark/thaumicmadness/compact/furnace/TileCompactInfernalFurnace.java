package com.vasmark.thaumicmadness.compact.furnace;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.OreDictionary;

import com.vasmark.thaumicmadness.item.ModItems;

import thaumcraft.api.ItemApi;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class TileCompactInfernalFurnace extends TileThaumcraft implements ISidedInventory, IWandable {

    public static final int SLOT_INPUT_START = 0;
    public static final int SLOT_INPUT_COUNT = 4;
    public static final int SLOT_OUTPUT_START = 4;
    public static final int SLOT_OUTPUT_COUNT = 4;
    public static final int SLOT_BONUS_START = 8;
    public static final int SLOT_BONUS_COUNT = 4;
    public static final int SLOT_UPGRADE_START = 12;
    public static final int SLOT_UPGRADE_COUNT = 2;
    public static final int SLOT_FILTER_IN = 14;
    public static final int SLOT_FILTER_OUT = 15;
    public static final int TOTAL_SLOTS = 16;

    public static final int FILTER_CAPACITY = 256; // 256 points = 64.0 flux units (0.25 flux per item)

    private static final int[] SLOTS_TOP = new int[] { 0, 1, 2, 3 };
    private static final int[] SLOTS_SIDES = new int[] { 0, 1, 2, 3, 14 };
    private static final int[] SLOTS_BOTTOM = new int[] { 4, 5, 6, 7, 8, 9, 10, 11, 15 };

    private ItemStack[] inventory = new ItemStack[TOTAL_SLOTS];

    // 4 Independent Cook Times for the 4 input slots
    public int[] cookTimes = new int[SLOT_INPUT_COUNT];
    public int[] maxCookTimes = new int[SLOT_INPUT_COUNT];

    // Filter Flux Absorption (0 to 64)
    public int filterFluxAbsorbed = 0;

    public int xp = 0;
    public static final int XP_MAX = 30000;

    private final Random rand = new Random();

    public TileCompactInfernalFurnace() {
        super();
        for (int i = 0; i < SLOT_INPUT_COUNT; i++) {
            maxCookTimes[i] = 26;
        }
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (this.worldObj == null) return;

        boolean wasBurning = isBurning();
        boolean isDirty = false;

        int bellows = getBellowsCount();
        int cookTimePerItem = calcCookTime(bellows);

        if (!this.worldObj.isRemote) {
            // Process all 4 input slots simultaneously & independently
            for (int slot = 0; slot < SLOT_INPUT_COUNT; slot++) {
                maxCookTimes[slot] = cookTimePerItem;

                if (canSmelt(slot)) {
                    cookTimes[slot]++;
                    if (cookTimes[slot] >= cookTimePerItem) {
                        smeltItem(slot, bellows);
                        cookTimes[slot] = 0;
                        isDirty = true;
                    }
                } else {
                    if (cookTimes[slot] != 0) {
                        cookTimes[slot] = 0;
                        isDirty = true;
                    }
                }
            }

            if (isBurning() != wasBurning) {
                isDirty = true;
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            }

            if (isDirty) {
                this.markDirty();
            }
        }
    }

    public boolean isBurning() {
        for (int i = 0; i < SLOT_INPUT_COUNT; i++) {
            if (cookTimes[i] > 0 || canSmelt(i)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBellows(ItemStack stack) {
        if (stack == null) return false;
        ItemStack bellows = ItemApi.getBlock("blockWoodenDevice", 0);
        if (bellows != null && stack.getItem() == bellows.getItem() && stack.getItemDamage() == 0) {
            return true;
        }
        if (ConfigBlocks.blockWoodenDevice != null
            && stack.getItem() == Item.getItemFromBlock(ConfigBlocks.blockWoodenDevice)
            && stack.getItemDamage() == 0) {
            return true;
        }
        return false;
    }

    public int getBellowsCount() {
        int count = 0;
        for (int i = SLOT_UPGRADE_START; i < SLOT_UPGRADE_START + SLOT_UPGRADE_COUNT; i++) {
            ItemStack stack = inventory[i];
            if (isBellows(stack)) {
                count++;
            }
        }
        return Math.min(2, count);
    }

    public int calcCookTime(int bellows) {
        if (bellows >= 2) {
            return 13; // 0.65 seconds (exact max speed)
        } else if (bellows == 1) {
            return 18; // 0.9 seconds
        }
        return 26; // 1.3 seconds base time
    }

    private boolean canSmelt(int slot) {
        if (inventory[slot] == null) {
            return false;
        }
        ItemStack result = FurnaceRecipes.smelting()
            .getSmeltingResult(inventory[slot]);
        if (result == null) {
            return false;
        }

        // Check if primary output slots have space
        return hasSpaceFor(result, SLOT_OUTPUT_START, SLOT_OUTPUT_COUNT);
    }

    private boolean hasSpaceFor(ItemStack result, int start, int count) {
        if (result == null) return true;
        int needed = result.stackSize;
        for (int i = start; i < start + count; i++) {
            ItemStack stack = inventory[i];
            if (stack == null) {
                return true;
            }
            if (stack.isItemEqual(result) && ItemStack.areItemStackTagsEqual(stack, result)) {
                int space = stack.getMaxStackSize() - stack.stackSize;
                if (space >= needed) return true;
                needed -= space;
            }
        }
        return false;
    }

    private void insertOutput(ItemStack result, int start, int count) {
        if (result == null) return;
        ItemStack toInsert = result.copy();
        for (int i = start; i < start + count && toInsert.stackSize > 0; i++) {
            ItemStack stack = inventory[i];
            if (stack == null) {
                inventory[i] = toInsert.copy();
                toInsert.stackSize = 0;
                break;
            } else if (stack.isItemEqual(toInsert) && ItemStack.areItemStackTagsEqual(stack, toInsert)) {
                int space = stack.getMaxStackSize() - stack.stackSize;
                int added = Math.min(space, toInsert.stackSize);
                stack.stackSize += added;
                toInsert.stackSize -= added;
            }
        }
    }

    private void smeltItem(int slot, int bellows) {
        if (!canSmelt(slot)) return;

        ItemStack input = inventory[slot];
        ItemStack result = FurnaceRecipes.smelting()
            .getSmeltingResult(input);
        if (result == null) return;

        // 1. Determine Native Cluster multiplier (2x ingots for native clusters)
        boolean isNativeCluster = isCluster(input);
        ItemStack finalOutput = result.copy();
        if (isNativeCluster) {
            finalOutput.stackSize *= 2;
        }

        // 2. Insert primary output
        insertOutput(finalOutput, SLOT_OUTPUT_START, SLOT_OUTPUT_COUNT);

        // 3. Roll for bonus nuggets / rare drops with max 70% chance
        ItemStack bonus = getSmeltingBonus(input, result);
        if (bonus != null) {
            int chance = (bellows >= 2) ? 70 : (bellows == 1 ? 50 : 30);
            if (this.rand.nextInt(100) < chance) {
                int bonusCount = 1;
                ItemStack bonusStack = bonus.copy();
                bonusStack.stackSize = bonusCount;
                insertOutput(bonusStack, SLOT_BONUS_START, SLOT_BONUS_COUNT);
            }
        }

        // 4. Brain in a Jar XP accumulation
        float xpGained = FurnaceRecipes.smelting()
            .func_151398_b(result);
        if (xpGained <= 0.0F) {
            xpGained = 0.2F;
        }
        if (isNativeCluster) {
            xpGained *= 2.0F;
        }

        int xpPoints = (int) xpGained;
        if (this.rand.nextFloat() < (xpGained - xpPoints)) {
            xpPoints++;
        }
        if (xpPoints < 1) xpPoints = 1;

        this.xp = Math.min(XP_MAX, this.xp + xpPoints);

        // 5. Flux Generation & Silverwood Filter / Taint Mechanics
        int fluxGen = isNativeCluster ? 2 : 1;
        handleFluxEmission(fluxGen);

        // 6. Decrement input
        inventory[slot].stackSize--;
        if (inventory[slot].stackSize <= 0) {
            inventory[slot] = null;
        }
    }

    private void handleFluxEmission(int fluxUnits) {
        ItemStack filterStack = inventory[SLOT_FILTER_IN];

        if (filterStack != null && filterStack.getItem() == ModItems.itemSilverwoodFilter) {
            // Fresh filter absorbs the flux
            this.filterFluxAbsorbed += fluxUnits;

            if (this.filterFluxAbsorbed >= FILTER_CAPACITY) {
                this.filterFluxAbsorbed -= FILTER_CAPACITY;

                // Consume 1 Silverwood Filter
                this.decrStackSize(SLOT_FILTER_IN, 1);

                // Insert 1 Tainted Filter into output slot
                ItemStack taintedStack = new ItemStack(ModItems.itemTaintedFilter, 1);
                if (inventory[SLOT_FILTER_OUT] == null) {
                    inventory[SLOT_FILTER_OUT] = taintedStack;
                } else if (inventory[SLOT_FILTER_OUT].isItemEqual(taintedStack)
                    && inventory[SLOT_FILTER_OUT].stackSize < 64) {
                        inventory[SLOT_FILTER_OUT].stackSize++;
                    } else {
                        // Overflow vents if output is completely full
                        corruptEnvironment();
                    }

                if (this.worldObj != null) {
                    this.worldObj.playSoundEffect(
                        this.xCoord + 0.5D,
                        this.yCoord + 0.5D,
                        this.zCoord + 0.5D,
                        "thaumcraft:bubble",
                        0.4F,
                        1.1F + this.rand.nextFloat() * 0.2F);
                }
            }
        } else {
            // NO FILTER: Corrupt biome and vent flux/taint into the world!
            corruptEnvironment();
        }
    }

    private void corruptEnvironment() {
        if (this.worldObj == null || this.worldObj.isRemote) return;

        // 1. Corrupt Biome to Tainted Land (spread radius 0..4)
        int rx = this.xCoord + (this.rand.nextInt(9) - 4);
        int rz = this.zCoord + (this.rand.nextInt(9) - 4);

        if (ThaumcraftWorldGenerator.biomeTaint != null) {
            if (this.worldObj.getBiomeGenForCoords(rx, rz) != ThaumcraftWorldGenerator.biomeTaint) {
                Utils.setBiomeAt(this.worldObj, rx, rz, ThaumcraftWorldGenerator.biomeTaint);
            }
        }

        // 2. Spread Taint Fibres or Flux Gas
        if (this.rand.nextInt(4) == 0) {
            int bx = this.xCoord + (this.rand.nextInt(7) - 3);
            int by = this.yCoord + (this.rand.nextInt(3) - 1);
            int bz = this.zCoord + (this.rand.nextInt(7) - 3);

            if (this.worldObj.isAirBlock(bx, by, bz)) {
                if (ConfigBlocks.blockFluxGas != null && this.rand.nextBoolean()) {
                    this.worldObj.setBlock(bx, by, bz, ConfigBlocks.blockFluxGas, 0, 3);
                } else if (ConfigBlocks.blockTaintFibres != null && this.worldObj.getBlock(bx, by - 1, bz)
                    .isOpaqueCube()) {
                        this.worldObj.setBlock(bx, by, bz, ConfigBlocks.blockTaintFibres, 0, 3);
                    }
            }
        }

        // 3. Sound effect
        this.worldObj.playSoundEffect(
            this.xCoord + 0.5D,
            this.yCoord + 0.8D,
            this.zCoord + 0.5D,
            "random.fizz",
            0.6F,
            1.2F + this.rand.nextFloat() * 0.3F);
    }

    public static ItemStack getSmeltingBonus(ItemStack input, ItemStack output) {
        if (input == null) return null;

        // Check official Thaumcraft bonus map
        ItemStack bonus = ThaumcraftApi.getSmeltingBonus(input);
        if (bonus != null) {
            return bonus.copy();
        }

        // Check OreDictionary for ore -> nugget matching
        if (output != null) {
            int[] ids = OreDictionary.getOreIDs(input);
            for (int id : ids) {
                String name = OreDictionary.getOreName(id);
                if (name != null && name.startsWith("ore")) {
                    String metal = name.substring(3);
                    List<ItemStack> nuggets = OreDictionary.getOres("nugget" + metal);
                    if (nuggets != null && !nuggets.isEmpty()) {
                        return nuggets.get(0)
                            .copy();
                    }
                }
            }

            // Fallback for vanilla iron and gold ingots
            if (output.getItem() == net.minecraft.init.Items.iron_ingot) {
                ItemStack ironNugget = ItemApi.getItem("itemResource", 0);
                if (ironNugget != null) return ironNugget.copy();
            }
            if (output.getItem() == net.minecraft.init.Items.gold_ingot) {
                return new ItemStack(net.minecraft.init.Items.gold_nugget);
            }
        }
        return null;
    }

    private boolean isCluster(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        String unloc = stack.getItem()
            .getUnlocalizedName(stack);
        if (unloc != null && unloc.toLowerCase()
            .contains("cluster")) {
            return true;
        }
        ItemStack cluster = ItemApi.getItem("itemResource", 6);
        if (cluster != null && stack.getItem() == cluster.getItem()
            && stack.getItemDamage() >= 6
            && stack.getItemDamage() <= 12) {
            return true;
        }
        return false;
    }

    public void withdrawXP(EntityPlayer player, int amount) {
        if (amount <= 0 || this.xp <= 0 || player == null) return;
        int toWithdraw = Math.min(amount, this.xp);
        this.xp -= toWithdraw;

        if (!this.worldObj.isRemote) {
            player.addExperience(toWithdraw);
            this.worldObj.playSoundAtEntity(player, "random.orb", 0.5F, 0.9F + this.rand.nextFloat() * 0.2F);
            this.markDirty();
        }
    }

    public void withdrawLevels(EntityPlayer player, int levels) {
        if (player == null || this.xp <= 0 || levels <= 0) return;
        for (int i = 0; i < levels && this.xp > 0; i++) {
            int level = player.experienceLevel;
            int xpNeeded = (level >= 30) ? (62 + (level - 30) * 7) : ((level >= 15) ? (17 + (level - 15) * 3) : 17);
            int currentLevelXP = (int) (player.experience * xpNeeded);
            int toNextLevel = Math.max(1, xpNeeded - currentLevelXP);
            withdrawXP(player, toNextLevel);
        }
    }

    public void withdraw1Level(EntityPlayer player) {
        withdrawLevels(player, 1);
    }

    public void withdrawAll(EntityPlayer player) {
        if (player == null || this.xp <= 0) return;
        withdrawXP(player, this.xp);
    }

    public void withdrawAllXP(EntityPlayer player) {
        withdrawAll(player);
    }

    @Override
    public int onWandRightClick(net.minecraft.world.World world, ItemStack wandstack, EntityPlayer player, int x, int y,
        int z, int side, int wandtype) {
        if (this.xp > 0) {
            withdrawAll(player);
            return 1;
        }
        return 0;
    }

    @Override
    public ItemStack onWandRightClick(net.minecraft.world.World world, ItemStack wandstack, EntityPlayer player) {
        return wandstack;
    }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {}

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, net.minecraft.world.World world, EntityPlayer player,
        int count) {}

    // IInventory & ISidedInventory Implementation
    @Override
    public int getSizeInventory() {
        return TOTAL_SLOTS;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (this.inventory[slot] != null) {
            ItemStack itemstack;
            if (this.inventory[slot].stackSize <= amount) {
                itemstack = this.inventory[slot];
                this.inventory[slot] = null;
                this.markDirty();
                return itemstack;
            } else {
                itemstack = this.inventory[slot].splitStack(amount);
                if (this.inventory[slot].stackSize == 0) {
                    this.inventory[slot] = null;
                }
                this.markDirty();
                return itemstack;
            }
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.inventory[slot] != null) {
            ItemStack itemstack = this.inventory[slot];
            this.inventory[slot] = null;
            return itemstack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot >= SLOT_UPGRADE_START && slot < SLOT_UPGRADE_START + SLOT_UPGRADE_COUNT) {
            if (stack != null && stack.stackSize > 1) {
                stack.stackSize = 1;
            }
        }
        if (slot == SLOT_FILTER_IN) {
            if (stack != null && stack.stackSize > 16) {
                stack.stackSize = 16;
            }
        }
        this.inventory[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    @Override
    public String getInventoryName() {
        return "container.compact_infernal_furnace";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        if (this.worldObj == null) return false;
        if (this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this) return false;
        return player
            .getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D)
            <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot >= SLOT_INPUT_START && slot < SLOT_INPUT_START + SLOT_INPUT_COUNT) {
            return stack != null && FurnaceRecipes.smelting()
                .getSmeltingResult(stack) != null;
        }
        if (slot >= SLOT_UPGRADE_START && slot < SLOT_UPGRADE_START + SLOT_UPGRADE_COUNT) {
            return isBellows(stack);
        }
        if (slot == SLOT_FILTER_IN) {
            return stack != null && stack.getItem() == ModItems.itemSilverwoodFilter;
        }
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (side == 1) {
            return SLOTS_TOP;
        } else if (side == 0) {
            return SLOTS_BOTTOM;
        }
        return SLOTS_SIDES;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return (slot >= SLOT_OUTPUT_START && slot < SLOT_UPGRADE_START) || slot == SLOT_FILTER_OUT;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        NBTTagList tagList = nbt.getTagList("Items", 10);
        this.inventory = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            byte slot = tag.getByte("Slot");
            if (slot >= 0 && slot < this.inventory.length) {
                this.inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }

        NBTTagList cookList = nbt.getTagList("CookTimes", 10);
        for (int i = 0; i < cookList.tagCount() && i < SLOT_INPUT_COUNT; i++) {
            NBTTagCompound cTag = cookList.getCompoundTagAt(i);
            int idx = cTag.getByte("Idx");
            if (idx >= 0 && idx < SLOT_INPUT_COUNT) {
                this.cookTimes[idx] = cTag.getInteger("Cook");
                this.maxCookTimes[idx] = cTag.getInteger("Max");
            }
        }

        this.filterFluxAbsorbed = nbt.getInteger("FilterFlux");
        this.xp = nbt.getInteger("XP");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        NBTTagList tagList = new NBTTagList();
        for (int i = 0; i < this.inventory.length; i++) {
            if (this.inventory[i] != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(tag);
                tagList.appendTag(tag);
            }
        }
        nbt.setTag("Items", tagList);

        NBTTagList cookList = new NBTTagList();
        for (int i = 0; i < SLOT_INPUT_COUNT; i++) {
            NBTTagCompound cTag = new NBTTagCompound();
            cTag.setByte("Idx", (byte) i);
            cTag.setInteger("Cook", this.cookTimes[i]);
            cTag.setInteger("Max", this.maxCookTimes[i]);
            cookList.appendTag(cTag);
        }
        nbt.setTag("CookTimes", cookList);

        nbt.setInteger("FilterFlux", this.filterFluxAbsorbed);
        nbt.setInteger("XP", this.xp);
    }
}
