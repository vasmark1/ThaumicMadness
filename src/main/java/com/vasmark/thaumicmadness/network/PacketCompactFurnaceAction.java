package com.vasmark.thaumicmadness.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

import com.vasmark.thaumicmadness.compact.furnace.TileCompactInfernalFurnace;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketCompactFurnaceAction implements IMessage {

    public static final byte ACTION_WITHDRAW_1_LEVEL = 0;
    public static final byte ACTION_WITHDRAW_10_LEVELS = 1;
    public static final byte ACTION_WITHDRAW_ALL = 2;

    public int x;
    public int y;
    public int z;
    public byte action;

    public PacketCompactFurnaceAction() {}

    public PacketCompactFurnaceAction(int x, int y, int z, byte action) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.action = action;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.action = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeByte(this.action);
    }

    public static class Handler implements IMessageHandler<PacketCompactFurnaceAction, IMessage> {

        @Override
        public IMessage onMessage(PacketCompactFurnaceAction message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player != null && player.worldObj != null) {
                TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
                if (te instanceof TileCompactInfernalFurnace) {
                    TileCompactInfernalFurnace furnace = (TileCompactInfernalFurnace) te;
                    if (message.action == ACTION_WITHDRAW_1_LEVEL) {
                        furnace.withdrawLevels(player, 1);
                    } else if (message.action == ACTION_WITHDRAW_10_LEVELS) {
                        furnace.withdrawLevels(player, 10);
                    } else if (message.action == ACTION_WITHDRAW_ALL) {
                        furnace.withdrawAllXP(player);
                    }
                }
            }
            return null;
        }
    }
}
