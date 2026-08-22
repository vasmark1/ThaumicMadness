package com.vasmark.thaumicmadness.warptheory.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.warptheory.util.WarpTheoryHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public class WarpChests extends IWarpEvent {

    public WarpChests() {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public String getName() {
        return "chests";
    }

    @Override
    public int getSeverity() {
        return 35;
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        WarpTheoryHelper.sendChat(player, "§5§o" + StatCollector.translateToLocal("chat.warptheory.chests"));
        WarpTheoryHelper.modEventInt(player, "chests", 15 + world.rand.nextInt(30));
        return true;
    }

    @SubscribeEvent
    public void onTick(WorldTickEvent e) {
        if (e.phase == Phase.END && e.side == Side.SERVER && e.world.getTotalWorldTime() % 10L == 0L) {
            for (Object obj : e.world.playerEntities) {
                if (!(obj instanceof EntityPlayer)) continue;
                EntityPlayer player = (EntityPlayer) obj;
                NBTTagCompound tag = WarpTheoryHelper.getWarpTag(player);
                if (tag.hasKey("chests")) {
                    int chests = tag.getInteger("chests");
                    List<TileEntityChest> chestInventories = getNearbyChests(
                        e.world,
                        (int) player.posX,
                        (int) player.posY,
                        (int) player.posZ,
                        8);
                    if (chestInventories.isEmpty()) return;

                    TileEntityChest c1 = chestInventories.get(e.world.rand.nextInt(chestInventories.size()));
                    TileEntityChest c2 = chestInventories.get(e.world.rand.nextInt(chestInventories.size()));

                    if (e.world.rand.nextInt(10) == 0) {
                        if (e.world.rand.nextBoolean()) {
                            e.world.playSoundEffect(
                                c1.xCoord + 0.5D,
                                c1.yCoord + 0.5D,
                                c1.zCoord + 0.5D,
                                "random.chestopen",
                                0.5F,
                                e.world.rand.nextFloat() * 0.1F + 0.9F);
                        } else {
                            e.world.playSoundEffect(
                                c2.xCoord + 0.5D,
                                c2.yCoord + 0.5D,
                                c2.zCoord + 0.5D,
                                "random.chestclosed",
                                0.5F,
                                e.world.rand.nextFloat() * 0.1F + 0.9F);
                        }
                        --chests;
                        tag.setInteger("chests", chests);
                    }

                    shuffle(e.world.rand, c1, c2);

                    if (chests <= 0) {
                        tag.removeTag("chests");
                    }
                }
            }
        }
    }

    private List<TileEntityChest> getNearbyChests(World world, int px, int py, int pz, int radius) {
        List<TileEntityChest> list = new ArrayList<TileEntityChest>();
        for (int x = px - radius; x <= px + radius; x += 2) {
            for (int y = py - radius; y <= py + radius; y += 2) {
                for (int z = pz - radius; z <= pz + radius; z += 2) {
                    TileEntity te = world.getTileEntity(x, y, z);
                    if (te instanceof TileEntityChest) {
                        list.add((TileEntityChest) te);
                    }
                }
            }
        }
        return list;
    }

    private boolean shuffle(Random rand, IInventory inv1, IInventory inv2) {
        if (inv1.getSizeInventory() <= 0 || inv2.getSizeInventory() <= 0) return false;
        int firstSlot = rand.nextInt(inv1.getSizeInventory());
        int secondSlot = rand.nextInt(inv2.getSizeInventory());
        ItemStack firstContents = inv1.getStackInSlot(firstSlot);
        ItemStack secondContents = inv2.getStackInSlot(secondSlot);
        if (inv1.isItemValidForSlot(firstSlot, secondContents) && inv2.isItemValidForSlot(secondSlot, firstContents)) {
            inv1.setInventorySlotContents(firstSlot, secondContents);
            inv2.setInventorySlotContents(secondSlot, firstContents);
            return true;
        }
        return false;
    }
}
