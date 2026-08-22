package com.vasmark.thaumicmadness.compact.infusion;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileJarFillable;

public class TileCompactInfusionMatrix extends TileThaumcraft implements ISidedInventory, IWandable, IAspectContainer {

    public static final int SLOT_CENTRAL_IN = 0;
    public static final int SLOT_CENTRAL_OUT = 1;
    public static final int SLOT_COMPONENTS_START = 2;
    public static final int SLOT_COMPONENTS_COUNT = 16;
    public static final int SLOT_STABILIZERS_START = 18;
    public static final int SLOT_STABILIZERS_COUNT = 4;
    public static final int TOTAL_SLOTS = 22;

    public static final byte STATE_IDLE = 0;
    public static final byte STATE_DRAINING_ESSENTIA = 1;
    public static final byte STATE_ABSORBING_COMPONENTS = 2;
    public static final byte STATE_FINALIZING = 3;

    private ItemStack[] inventory = new ItemStack[TOTAL_SLOTS];

    public byte state = STATE_IDLE;
    public int craftProgress = 0;
    public int craftTotalTime = 100;
    public boolean autoStart = false;

    public AspectList aspectsNeeded = new AspectList();
    public AspectList aspectsRemaining = new AspectList();

    private Object matchedRecipe = null;
    public int instability = 0;
    private int tickCounter = 0;

    // Component tracking for current infusion
    private ArrayList<ItemStack> componentsToConsume = new ArrayList<ItemStack>();

    // Client-side visual suction streaming
    public net.minecraft.util.ChunkCoordinates activeSourcePos = null;
    public int activeSourceColor = 0;
    public int activeSourceTicks = 0;

    public TileCompactInfusionMatrix() {
        super();
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    public void setActiveEssentiaSource(int sx, int sy, int sz, int color, int ticks) {
        this.activeSourcePos = new net.minecraft.util.ChunkCoordinates(sx, sy, sz);
        this.activeSourceColor = color;
        this.activeSourceTicks = ticks;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj != null && worldObj.isRemote) {
            if (this.state != STATE_IDLE) {
                // Swirling smoke & magic energy around the matrix
                double px = xCoord + 0.5D + (worldObj.rand.nextDouble() - 0.5D) * 0.4D;
                double py = yCoord + 0.5D + (worldObj.rand.nextDouble() - 0.5D) * 0.4D;
                double pz = zCoord + 0.5D + (worldObj.rand.nextDouble() - 0.5D) * 0.4D;
                worldObj.spawnParticle("smoke", px, py, pz, 0.0D, 0.02D, 0.0D);
                if (worldObj.rand.nextInt(4) == 0) {
                    worldObj.spawnParticle(
                        "portal",
                        px,
                        py,
                        pz,
                        (worldObj.rand.nextDouble() - 0.5D) * 0.2D,
                        -0.1D,
                        (worldObj.rand.nextDouble() - 0.5D) * 0.2D);
                }

                // Authentic Thaumcraft matrix central runes
                if (worldObj.rand.nextInt(4) == 0) {
                    thaumcraft.common.Thaumcraft.proxy.blockRunes(
                        worldObj,
                        xCoord + 0.5D,
                        yCoord + 0.5D,
                        zCoord + 0.5D,
                        0.8F + worldObj.rand.nextFloat() * 0.2F,
                        0.2F + worldObj.rand.nextFloat() * 0.2F,
                        0.9F + worldObj.rand.nextFloat() * 0.1F,
                        20,
                        -0.03F);
                }
            }

            // Continuous essentia streaming from active suction sources
            if (this.activeSourceTicks > 0 && this.activeSourcePos != null) {
                this.activeSourceTicks--;

                for (int p = 0; p < 2; p++) {
                    thaumcraft.common.Thaumcraft.proxy.essentiaTrailFx(
                        worldObj,
                        this.activeSourcePos.posX,
                        this.activeSourcePos.posY,
                        this.activeSourcePos.posZ,
                        xCoord,
                        yCoord,
                        zCoord,
                        15,
                        this.activeSourceColor,
                        0.25F);
                }
                if (worldObj.rand.nextInt(8) == 0) {
                    worldObj.playSound(
                        xCoord + 0.5D,
                        yCoord + 0.5D,
                        zCoord + 0.5D,
                        "thaumcraft:bubble",
                        0.15F,
                        1.0F + worldObj.rand.nextFloat() * 0.4F,
                        false);
                }
            }
            return;
        }

        if (worldObj == null) return;

        tickCounter++;

        if (state == STATE_IDLE) {
            if (autoStart && tickCounter % 20 == 0) {
                checkAndAutoStart();
            }
            return;
        }

        if (state == STATE_DRAINING_ESSENTIA) {
            handleEssentiaDrain();
        } else if (state == STATE_ABSORBING_COMPONENTS) {
            handleComponentAbsorption();
        } else if (state == STATE_FINALIZING) {
            handleFinalizing();
        }
    }

    public void checkAndAutoStart() {
        if (state != STATE_IDLE || inventory[SLOT_CENTRAL_IN] == null || inventory[SLOT_CENTRAL_OUT] != null) {
            return;
        }

        ArrayList<ItemStack> comps = getComponentList();
        Object recipe = findMatchingRecipe(comps, inventory[SLOT_CENTRAL_IN], null);
        if (recipe != null) {
            startCraftingWithRecipe(recipe, comps);
        }
    }

    public boolean startCrafting(EntityPlayer player) {
        if (state != STATE_IDLE) return false;
        if (inventory[SLOT_CENTRAL_IN] == null) return false;
        if (inventory[SLOT_CENTRAL_OUT] != null) return false;

        ArrayList<ItemStack> comps = getComponentList();
        Object recipe = findMatchingRecipe(comps, inventory[SLOT_CENTRAL_IN], player);
        if (recipe == null) return false;

        startCraftingWithRecipe(recipe, comps);
        return true;
    }

    private void startCraftingWithRecipe(Object recipe, ArrayList<ItemStack> comps) {
        this.matchedRecipe = recipe;
        this.aspectsNeeded = new AspectList();
        this.aspectsRemaining = new AspectList();
        this.componentsToConsume = new ArrayList<ItemStack>();

        if (recipe instanceof InfusionRecipe) {
            InfusionRecipe ir = (InfusionRecipe) recipe;
            this.instability = ir.getInstability();
            AspectList al = ir.getAspects();
            if (al != null) {
                for (Aspect a : al.getAspects()) {
                    this.aspectsNeeded.add(a, al.getAmount(a));
                    this.aspectsRemaining.add(a, al.getAmount(a));
                }
            }
            if (ir.getComponents() != null) {
                for (ItemStack c : ir.getComponents()) {
                    if (c != null) this.componentsToConsume.add(c.copy());
                }
            }
        } else if (recipe instanceof InfusionEnchantmentRecipe) {
            InfusionEnchantmentRecipe ier = (InfusionEnchantmentRecipe) recipe;
            this.instability = ier.calcInstability(inventory[SLOT_CENTRAL_IN]);
            AspectList al = ier.aspects;
            if (al != null) {
                for (Aspect a : al.getAspects()) {
                    this.aspectsNeeded.add(a, al.getAmount(a));
                    this.aspectsRemaining.add(a, al.getAmount(a));
                }
            }
            if (ier.components != null) {
                for (ItemStack c : ier.components) {
                    if (c != null) this.componentsToConsume.add(c.copy());
                }
            }
        }

        if (this.aspectsRemaining.visSize() > 0) {
            this.state = STATE_DRAINING_ESSENTIA;
        } else if (!this.componentsToConsume.isEmpty()) {
            this.state = STATE_ABSORBING_COMPONENTS;
        } else {
            this.state = STATE_FINALIZING;
        }

        this.craftProgress = 0;
        this.craftTotalTime = 100;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void cancelCrafting() {
        this.state = STATE_IDLE;
        this.matchedRecipe = null;
        this.aspectsNeeded = new AspectList();
        this.aspectsRemaining = new AspectList();
        this.componentsToConsume.clear();
        this.craftProgress = 0;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void toggleAutoStart() {
        this.autoStart = !this.autoStart;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    private void handleEssentiaDrain() {
        Aspect targetAspect = null;
        for (Aspect a : aspectsRemaining.getAspects()) {
            if (aspectsRemaining.getAmount(a) > 0) {
                targetAspect = a;
                break;
            }
        }

        if (targetAspect != null) {
            // Drain 1 essentia every 16 ticks (measured, ceremonial pace)
            if (tickCounter % 16 == 0) {
                if (drainEssentiaFromSources(targetAspect, 1)) {
                    aspectsRemaining.reduce(targetAspect, 1);
                    craftProgress++;
                    markDirty();
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                }
            } else if (tickCounter % 4 == 0) {
                // Stream trail particles continuously during the suction phase
                streamActiveEssentiaFX(targetAspect);
            }
        }

        if (aspectsRemaining.visSize() <= 0) {
            if (!componentsToConsume.isEmpty()) {
                state = STATE_ABSORBING_COMPONENTS;
            } else {
                state = STATE_FINALIZING;
            }
            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    private void streamActiveEssentiaFX(Aspect aspect) {
        // Ping nearby jar for visible particle stream
        int radius = 12;
        for (int y = -5; y <= 5; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    TileEntity te = worldObj.getTileEntity(xCoord + x, yCoord + y, zCoord + z);
                    if (te instanceof TileJarFillable) {
                        TileJarFillable jar = (TileJarFillable) te;
                        if (jar.aspect == aspect && jar.amount > 0) {
                            spawnEssentiaTrailFX(xCoord + x, yCoord + y, zCoord + z, aspect);
                            return;
                        }
                    }
                }
            }
        }
    }

    public int getAvailableEssentiaInJars(Aspect aspect) {
        if (worldObj == null || aspect == null) return 0;
        int total = 0;
        int radius = 12;
        for (int y = -5; y <= 5; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    TileEntity te = worldObj.getTileEntity(xCoord + x, yCoord + y, zCoord + z);
                    if (te instanceof TileJarFillable) {
                        TileJarFillable jar = (TileJarFillable) te;
                        if (jar.aspect == aspect) {
                            total += jar.amount;
                        }
                    } else if (te instanceof IAspectSource) {
                        IAspectSource src = (IAspectSource) te;
                        total += src.containerContains(aspect);
                    }
                }
            }
        }
        return total;
    }

    private boolean drainEssentiaFromSources(Aspect aspect, int amount) {
        // 1. Check adjacent blocks first
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    TileEntity te = worldObj.getTileEntity(xCoord + x, yCoord + y, zCoord + z);
                    if (te instanceof TileJarFillable) {
                        TileJarFillable jar = (TileJarFillable) te;
                        if (jar.aspect == aspect && jar.amount >= amount) {
                            jar.takeFromContainer(aspect, amount);
                            spawnEssentiaTrailFX(xCoord + x, yCoord + y, zCoord + z, aspect);
                            return true;
                        }
                    } else if (te instanceof IAspectSource) {
                        IAspectSource src = (IAspectSource) te;
                        if (src.doesContainerContainAmount(aspect, amount) && src.takeFromContainer(aspect, amount)) {
                            spawnEssentiaTrailFX(xCoord + x, yCoord + y, zCoord + z, aspect);
                            return true;
                        }
                    }
                }
            }
        }

        // 2. Scan jars in radius 12 blocks
        int radius = 12;
        for (int y = -5; y <= 5; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    TileEntity te = worldObj.getTileEntity(xCoord + x, yCoord + y, zCoord + z);
                    if (te instanceof TileJarFillable) {
                        TileJarFillable jar = (TileJarFillable) te;
                        if (jar.aspect == aspect && jar.amount >= amount) {
                            jar.takeFromContainer(aspect, amount);
                            spawnEssentiaTrailFX(xCoord + x, yCoord + y, zCoord + z, aspect);
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private void spawnEssentiaTrailFX(int sourceX, int sourceY, int sourceZ, Aspect aspect) {
        if (worldObj == null || worldObj.isRemote) return;
        try {
            com.vasmark.thaumicmadness.network.NetworkHandler.INSTANCE.sendToAllAround(
                new com.vasmark.thaumicmadness.network.PacketCompactEssentiaTrail(
                    sourceX,
                    sourceY,
                    sourceZ,
                    xCoord,
                    yCoord,
                    zCoord,
                    aspect.getColor(),
                    5),
                new cpw.mods.fml.common.network.NetworkRegistry.TargetPoint(
                    worldObj.provider.dimensionId,
                    xCoord,
                    yCoord,
                    zCoord,
                    32.0D));
        } catch (Throwable ignored) {}
    }

    private void handleComponentAbsorption() {
        if (tickCounter % 15 == 0) {
            if (!componentsToConsume.isEmpty()) {
                ItemStack targetComp = componentsToConsume.get(0);
                boolean foundAndConsumed = false;

                for (int i = SLOT_COMPONENTS_START; i < SLOT_COMPONENTS_START + SLOT_COMPONENTS_COUNT; i++) {
                    ItemStack slotStack = inventory[i];
                    if (slotStack != null && InfusionRecipe.areItemStacksEqual(slotStack, targetComp, true)) {
                        decrStackSize(i, 1);
                        componentsToConsume.remove(0);
                        foundAndConsumed = true;
                        craftProgress += 5;
                        markDirty();
                        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                        break;
                    }
                }

                if (!foundAndConsumed) {
                    // Ingredient missing - pause or cancel
                }
            }

            if (componentsToConsume.isEmpty()) {
                state = STATE_FINALIZING;
                markDirty();
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
    }

    private void handleFinalizing() {
        if (inventory[SLOT_CENTRAL_IN] == null) {
            cancelCrafting();
            return;
        }

        if (matchedRecipe instanceof InfusionRecipe) {
            InfusionRecipe ir = (InfusionRecipe) matchedRecipe;
            Object out = ir.getRecipeOutput();
            ItemStack resultStack = null;
            if (out instanceof ItemStack) {
                resultStack = ((ItemStack) out).copy();
            } else {
                Object dynamicOut = ir.getRecipeOutput(inventory[SLOT_CENTRAL_IN]);
                if (dynamicOut instanceof ItemStack) {
                    resultStack = ((ItemStack) dynamicOut).copy();
                }
            }

            if (resultStack != null) {
                decrStackSize(SLOT_CENTRAL_IN, 1);
                inventory[SLOT_CENTRAL_OUT] = resultStack;
            }
        } else if (matchedRecipe instanceof InfusionEnchantmentRecipe) {
            InfusionEnchantmentRecipe ier = (InfusionEnchantmentRecipe) matchedRecipe;
            ItemStack enchanted = inventory[SLOT_CENTRAL_IN].copy();
            if (ier.getEnchantment() != null) {
                int curLvl = net.minecraft.enchantment.EnchantmentHelper
                    .getEnchantmentLevel(ier.getEnchantment().effectId, enchanted);
                enchanted.addEnchantment(ier.getEnchantment(), curLvl + 1);
            }
            decrStackSize(SLOT_CENTRAL_IN, 1);
            inventory[SLOT_CENTRAL_OUT] = enchanted;
        }

        this.state = STATE_IDLE;
        this.matchedRecipe = null;
        this.aspectsNeeded = new AspectList();
        this.aspectsRemaining = new AspectList();
        this.componentsToConsume.clear();
        this.craftProgress = 0;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public int getStabilityPower() {
        int power = 0;
        for (int i = SLOT_STABILIZERS_START; i < SLOT_STABILIZERS_START + SLOT_STABILIZERS_COUNT; i++) {
            ItemStack stack = inventory[i];
            if (stack == null) continue;
            if (stack.getItem() == net.minecraft.item.Item.getItemFromBlock(ConfigBlocks.blockCandle)) {
                power += 3 * stack.stackSize;
            } else if (stack.getItem() == net.minecraft.init.Items.skull) {
                power += 5 * stack.stackSize;
            } else if (stack.getItem() == ConfigItems.itemSanitySoap) {
                power += 6 * stack.stackSize;
            } else if (stack.getItem() == ConfigItems.itemResource
                && (stack.getItemDamage() == 17 || stack.getItemDamage() == 15)) {
                    power += 8 * stack.stackSize; // Void seed or Primal charm
                } else {
                    power += 2 * stack.stackSize;
                }
        }
        return power;
    }

    public String placerName = "";

    public ArrayList<ItemStack> getComponentList() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (int i = SLOT_COMPONENTS_START; i < SLOT_COMPONENTS_START + SLOT_COMPONENTS_COUNT; i++) {
            if (inventory[i] != null) {
                list.add(inventory[i]);
            }
        }
        return list;
    }

    private Object findMatchingRecipe(ArrayList<ItemStack> components, ItemStack central, EntityPlayer player) {
        if (central == null) return null;

        EntityPlayer effectivePlayer = player;
        if (effectivePlayer == null && worldObj instanceof net.minecraft.world.WorldServer) {
            if (placerName != null && !placerName.isEmpty()) {
                effectivePlayer = worldObj.getPlayerEntityByName(placerName);
            }
            if (effectivePlayer == null) {
                try {
                    effectivePlayer = net.minecraftforge.common.util.FakePlayerFactory
                        .getMinecraft((net.minecraft.world.WorldServer) worldObj);
                } catch (Throwable ignored) {}
            }
        }

        try {
            for (Object r : thaumcraft.api.ThaumcraftApi.getCraftingRecipes()) {
                if (r instanceof InfusionRecipe) {
                    InfusionRecipe ir = (InfusionRecipe) r;
                    if (matchesRecipe(ir, components, central, effectivePlayer)) {
                        return ir;
                    }
                } else if (r instanceof InfusionEnchantmentRecipe) {
                    InfusionEnchantmentRecipe ier = (InfusionEnchantmentRecipe) r;
                    if (matchesEnchantmentRecipe(ier, components, central, effectivePlayer)) {
                        return ier;
                    }
                }
            }
        } catch (Throwable t) {
            // Safe fallback
        }

        return null;
    }

    private boolean matchesRecipe(InfusionRecipe recipe, ArrayList<ItemStack> components, ItemStack central,
        EntityPlayer player) {
        if (recipe == null || recipe.getRecipeInput() == null || central == null) return false;

        // 1. Research check if player is available
        if (recipe.getResearch() != null && recipe.getResearch()
            .length() > 0 && player != null) {
            try {
                if (!thaumcraft.api.ThaumcraftApiHelper
                    .isResearchComplete(player.getCommandSenderName(), recipe.getResearch())) {
                    return false;
                }
            } catch (Throwable ignored) {}
        }

        // 2. Central item match
        ItemStack centralCopy = central.copy();
        if (recipe.getRecipeInput()
            .getItemDamage() == 32767) {
            centralCopy.setItemDamage(32767);
        }
        if (!InfusionRecipe.areItemStacksEqual(centralCopy, recipe.getRecipeInput(), true)) {
            return false;
        }

        // 3. Components match
        ItemStack[] recipeComps = recipe.getComponents();
        if (recipeComps == null || recipeComps.length == 0) {
            return components.isEmpty();
        }

        if (components.size() != recipeComps.length) {
            return false;
        }

        ArrayList<ItemStack> available = new ArrayList<ItemStack>();
        for (ItemStack is : components) {
            if (is != null) available.add(is.copy());
        }

        for (ItemStack required : recipeComps) {
            if (required == null) continue;
            boolean found = false;
            for (int i = 0; i < available.size(); i++) {
                ItemStack av = available.get(i);
                if (av != null && InfusionRecipe.areItemStacksEqual(av, required, true)) {
                    available.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }

        return available.isEmpty();
    }

    private boolean matchesEnchantmentRecipe(InfusionEnchantmentRecipe recipe, ArrayList<ItemStack> components,
        ItemStack central, EntityPlayer player) {
        if (recipe == null || central == null || recipe.getEnchantment() == null) return false;

        if (!recipe.getEnchantment()
            .canApply(central)) {
            return false;
        }

        if (recipe.getResearch() != null && recipe.getResearch()
            .length() > 0 && player != null) {
            try {
                if (!thaumcraft.api.ThaumcraftApiHelper
                    .isResearchComplete(player.getCommandSenderName(), recipe.getResearch())) {
                    return false;
                }
            } catch (Throwable ignored) {}
        }

        ItemStack[] recipeComps = recipe.components;
        if (recipeComps == null || recipeComps.length == 0) {
            return components.isEmpty();
        }

        if (components.size() != recipeComps.length) {
            return false;
        }

        ArrayList<ItemStack> available = new ArrayList<ItemStack>();
        for (ItemStack is : components) {
            if (is != null) available.add(is.copy());
        }

        for (ItemStack required : recipeComps) {
            if (required == null) continue;
            boolean found = false;
            for (int i = 0; i < available.size(); i++) {
                ItemStack av = available.get(i);
                if (av != null && InfusionRecipe.areItemStacksEqual(av, required, true)) {
                    available.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }

        return available.isEmpty();
    }

    // --- IWandable Implementation ---
    @Override
    public int onWandRightClick(net.minecraft.world.World world, ItemStack wandstack, EntityPlayer player, int x, int y,
        int z, int side, int md) {
        if (world.isRemote) return 0;
        if (state == STATE_IDLE) {
            if (startCrafting(player)) {
                player.swingItem();
                return 1;
            }
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

    // --- IAspectContainer Implementation ---
    @Override
    public AspectList getAspects() {
        return aspectsRemaining;
    }

    @Override
    public void setAspects(AspectList aspects) {
        this.aspectsRemaining = aspects != null ? aspects : new AspectList();
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return false;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return amount;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }

    @Override
    public int containerContains(Aspect tag) {
        return aspectsRemaining != null ? aspectsRemaining.getAmount(tag) : 0;
    }

    // --- ISidedInventory Implementation ---
    @Override
    public int getSizeInventory() {
        return TOTAL_SLOTS;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (index >= 0 && index < inventory.length) {
            return inventory[index];
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.inventory[index] != null) {
            ItemStack itemstack;
            if (this.inventory[index].stackSize <= count) {
                itemstack = this.inventory[index];
                this.inventory[index] = null;
                this.markDirty();
                return itemstack;
            } else {
                itemstack = this.inventory[index].splitStack(count);
                if (this.inventory[index].stackSize == 0) {
                    this.inventory[index] = null;
                }
                this.markDirty();
                return itemstack;
            }
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (this.inventory[index] != null) {
            ItemStack itemstack = this.inventory[index];
            this.inventory[index] = null;
            return itemstack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.inventory[index] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    @Override
    public String getInventoryName() {
        return "container.compact_infusion_matrix";
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
        if (this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this) {
            return false;
        }
        return player
            .getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D)
            <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    public boolean isStabilizerItem(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        net.minecraft.block.Block b = net.minecraft.block.Block.getBlockFromItem(stack.getItem());
        if (b instanceof thaumcraft.api.crafting.IInfusionStabiliser) return true;
        if (stack.getItem() instanceof thaumcraft.api.crafting.IInfusionStabiliser) return true;
        if (b != null) {
            String name = b.getUnlocalizedName()
                .toLowerCase();
            if (name.contains("candle") || name.contains("skull")
                || name.contains("crystal")
                || name.contains("stabiliz")) {
                return true;
            }
        }
        String iname = stack.getUnlocalizedName()
            .toLowerCase();
        return iname.contains("candle") || iname.contains("skull")
            || iname.contains("crystal")
            || iname.contains("stabiliz");
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == SLOT_CENTRAL_OUT) return false;
        if (index >= SLOT_STABILIZERS_START && index < SLOT_STABILIZERS_START + SLOT_STABILIZERS_COUNT) {
            return isStabilizerItem(stack);
        }
        return true;
    }

    private static final int[] SLOTS_TOP = new int[] { SLOT_CENTRAL_IN };
    private static final int[] SLOTS_BOTTOM = new int[] { SLOT_CENTRAL_OUT };
    private static final int[] SLOTS_SIDES = new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 };

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (side == 1) return SLOTS_TOP;
        if (side == 0) return SLOTS_BOTTOM;
        return SLOTS_SIDES;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, int side) {
        if (index == SLOT_CENTRAL_OUT) return false;
        if (side == 1 && index == SLOT_CENTRAL_IN) return true;
        if (side != 0 && side != 1
            && index >= SLOT_COMPONENTS_START
            && index < SLOT_COMPONENTS_START + SLOT_COMPONENTS_COUNT) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, int side) {
        if (side == 0 && index == SLOT_CENTRAL_OUT) return true;
        return false;
    }

    // --- NBT Serialization ---
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList tagList = nbt.getTagList("Items", 10);
        this.inventory = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            byte slot = tag.getByte("Slot");
            if (slot >= 0 && slot < this.inventory.length) {
                this.inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }

        this.state = nbt.getByte("State");
        this.craftProgress = nbt.getInteger("CraftProgress");
        this.craftTotalTime = nbt.getInteger("CraftTotalTime");
        this.autoStart = nbt.getBoolean("AutoStart");
        this.instability = nbt.getInteger("Instability");
        this.placerName = nbt.getString("PlacerName");

        this.aspectsNeeded.readFromNBT(nbt, "AspectsNeeded");
        this.aspectsRemaining.readFromNBT(nbt, "AspectsRemaining");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
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

        nbt.setByte("State", this.state);
        nbt.setInteger("CraftProgress", this.craftProgress);
        nbt.setInteger("CraftTotalTime", this.craftTotalTime);
        nbt.setBoolean("AutoStart", this.autoStart);
        nbt.setInteger("Instability", this.instability);
        if (this.placerName != null) {
            nbt.setString("PlacerName", this.placerName);
        }

        this.aspectsNeeded.writeToNBT(nbt, "AspectsNeeded");
        this.aspectsRemaining.writeToNBT(nbt, "AspectsRemaining");
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        if (pkt != null && pkt.func_148857_g() != null) {
            readFromNBT(pkt.func_148857_g());
        }
    }
}
