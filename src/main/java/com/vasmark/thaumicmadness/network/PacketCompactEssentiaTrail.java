package com.vasmark.thaumicmadness.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import thaumcraft.common.Thaumcraft;

public class PacketCompactEssentiaTrail implements IMessage {

    private int sourceX;
    private int sourceY;
    private int sourceZ;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int color;
    private int count;

    public PacketCompactEssentiaTrail() {}

    public PacketCompactEssentiaTrail(int sourceX, int sourceY, int sourceZ, int targetX, int targetY, int targetZ,
        int color, int count) {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.sourceZ = sourceZ;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.color = color;
        this.count = count;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.sourceX = buf.readInt();
        this.sourceY = buf.readInt();
        this.sourceZ = buf.readInt();
        this.targetX = buf.readInt();
        this.targetY = buf.readInt();
        this.targetZ = buf.readInt();
        this.color = buf.readInt();
        this.count = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.sourceX);
        buf.writeInt(this.sourceY);
        buf.writeInt(this.sourceZ);
        buf.writeInt(this.targetX);
        buf.writeInt(this.targetY);
        buf.writeInt(this.targetZ);
        buf.writeInt(this.color);
        buf.writeInt(this.count);
    }

    public static class Handler implements IMessageHandler<PacketCompactEssentiaTrail, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketCompactEssentiaTrail message, MessageContext ctx) {
            World world = Minecraft.getMinecraft().theWorld;
            if (world != null) {
                net.minecraft.tileentity.TileEntity te = world
                    .getTileEntity(message.targetX, message.targetY, message.targetZ);
                if (te instanceof com.vasmark.thaumicmadness.compact.infusion.TileCompactInfusionMatrix) {
                    ((com.vasmark.thaumicmadness.compact.infusion.TileCompactInfusionMatrix) te)
                        .setActiveEssentiaSource(
                            message.sourceX,
                            message.sourceY,
                            message.sourceZ,
                            message.color,
                            message.count);
                }
                for (int i = 0; i < 3; i++) {
                    Thaumcraft.proxy.essentiaTrailFx(
                        world,
                        message.sourceX,
                        message.sourceY,
                        message.sourceZ,
                        message.targetX,
                        message.targetY,
                        message.targetZ,
                        15,
                        message.color,
                        0.25F);
                }
            }
            return null;
        }
    }
}
