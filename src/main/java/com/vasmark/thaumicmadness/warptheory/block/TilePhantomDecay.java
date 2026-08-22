package com.vasmark.thaumicmadness.warptheory.block;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TilePhantomDecay extends TileEntity {

    private String oldBlockName = "minecraft:air";
    private int oldMeta = 0;
    private NBTTagCompound oldTag = null;
    private long returnTime = 0;

    public TilePhantomDecay() {}

    public TilePhantomDecay(World world, int x, int y, int z, long returnTime) {
        Block b = world.getBlock(x, y, z);
        this.oldBlockName = Block.blockRegistry.getNameForObject(b);
        this.oldMeta = world.getBlockMetadata(x, y, z);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null) {
            this.oldTag = new NBTTagCompound();
            te.writeToNBT(this.oldTag);
        }
        this.returnTime = returnTime;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        if (this.worldObj != null && !this.worldObj.isRemote) {
            if (this.worldObj.getTotalWorldTime() >= this.returnTime) {
                this.rebuildBlock();
            }
        }
    }

    private void rebuildBlock() {
        Block b = (Block) Block.blockRegistry.getObject(this.oldBlockName);
        if (b == null) return;
        this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, b, this.oldMeta, 3);
        if (this.oldTag != null) {
            TileEntity te = this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord);
            if (te != null) {
                te.readFromNBT(this.oldTag);
            }
        }
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString("oldName", this.oldBlockName);
        tag.setInteger("oldMeta", this.oldMeta);
        if (this.oldTag != null) {
            tag.setTag("oldTag", this.oldTag);
        }
        tag.setLong("returnTime", this.returnTime);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.oldBlockName = tag.getString("oldName");
        this.oldMeta = tag.getInteger("oldMeta");
        if (tag.hasKey("oldTag")) {
            this.oldTag = tag.getCompoundTag("oldTag");
        }
        this.returnTime = tag.getLong("returnTime");
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        this.readFromNBT(pkt.func_148857_g());
    }
}
