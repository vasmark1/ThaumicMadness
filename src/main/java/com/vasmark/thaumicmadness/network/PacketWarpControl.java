package com.vasmark.thaumicmadness.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;
import thaumcraft.common.lib.research.PlayerKnowledge;

public class PacketWarpControl implements IMessage {

    public static final byte ACTION_CLEAR = 0;
    public static final byte ACTION_ADD_10 = 1;
    public static final byte ACTION_SUB_10 = 2;
    public static final byte ACTION_SET_MAX = 3;

    private byte action;

    public PacketWarpControl() {}

    public PacketWarpControl(byte action) {
        this.action = action;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.action = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.action);
    }

    public static class Handler implements IMessageHandler<PacketWarpControl, IMessage> {

        @Override
        public IMessage onMessage(PacketWarpControl message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) return null;

            String username = player.getCommandSenderName();
            PlayerKnowledge pk = Thaumcraft.proxy.getPlayerKnowledge();
            if (pk == null) return null;

            switch (message.action) {
                case ACTION_CLEAR:
                    pk.setWarpPerm(username, 0);
                    pk.setWarpSticky(username, 0);
                    pk.setWarpTemp(username, 0);
                    player.addChatMessage(new ChatComponentTranslation("mymod.warp.msg.cleared"));
                    break;

                case ACTION_ADD_10:
                    pk.addWarpTemp(username, 10);
                    player.addChatMessage(new ChatComponentTranslation("mymod.warp.msg.added", 10));
                    break;

                case ACTION_SUB_10:
                    int temp = pk.getWarpTemp(username);
                    int sticky = pk.getWarpSticky(username);
                    int perm = pk.getWarpPerm(username);
                    int toRemove = 10;

                    if (temp > 0) {
                        int rem = Math.min(temp, toRemove);
                        pk.setWarpTemp(username, temp - rem);
                        toRemove -= rem;
                    }
                    if (toRemove > 0 && sticky > 0) {
                        int rem = Math.min(sticky, toRemove);
                        pk.setWarpSticky(username, sticky - rem);
                        toRemove -= rem;
                    }
                    if (toRemove > 0 && perm > 0) {
                        int rem = Math.min(perm, toRemove);
                        pk.setWarpPerm(username, perm - rem);
                        toRemove -= rem;
                    }
                    player.addChatMessage(new ChatComponentTranslation("mymod.warp.msg.reduced", 10));
                    break;

                case ACTION_SET_MAX:
                    pk.setWarpSticky(username, 50);
                    pk.setWarpPerm(username, 50);
                    player.addChatMessage(new ChatComponentTranslation("mymod.warp.msg.max"));
                    break;
            }

            // Synchronize updated warp levels with the client
            thaumcraft.common.lib.network.PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte) 0), player);
            thaumcraft.common.lib.network.PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte) 1), player);
            thaumcraft.common.lib.network.PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte) 2), player);

            return null;
        }
    }
}
