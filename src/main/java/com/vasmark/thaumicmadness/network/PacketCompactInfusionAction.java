package com.vasmark.thaumicmadness.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.compact.infusion.TileCompactInfusionMatrix;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketCompactInfusionAction implements IMessage {

    public static final byte ACTION_START = 0;
    public static final byte ACTION_CANCEL = 1;
    public static final byte ACTION_TOGGLE_AUTO = 2;

    public int x;
    public int y;
    public int z;
    public byte action;

    public PacketCompactInfusionAction() {}

    public PacketCompactInfusionAction(int x, int y, int z, byte action) {
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

    public static class Handler implements IMessageHandler<PacketCompactInfusionAction, IMessage> {

        @Override
        public IMessage onMessage(PacketCompactInfusionAction message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) return null;

            World world = player.worldObj;
            if (world != null && world.blockExists(message.x, message.y, message.z)) {
                TileEntity te = world.getTileEntity(message.x, message.y, message.z);
                if (te instanceof TileCompactInfusionMatrix) {
                    TileCompactInfusionMatrix matrix = (TileCompactInfusionMatrix) te;
                    switch (message.action) {
                        case ACTION_START:
                            matrix.startCrafting(player);
                            break;
                        case ACTION_CANCEL:
                            matrix.cancelCrafting();
                            break;
                        case ACTION_TOGGLE_AUTO:
                            matrix.toggleAutoStart();
                            break;
                    }
                }
            }
            return null;
        }
    }
}
