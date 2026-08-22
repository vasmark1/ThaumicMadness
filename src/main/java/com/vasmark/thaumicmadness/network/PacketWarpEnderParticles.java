package com.vasmark.thaumicmadness.network;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class PacketWarpEnderParticles implements IMessage {

    public double x;
    public double y;
    public double z;

    public PacketWarpEnderParticles() {}

    public PacketWarpEnderParticles(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    public static class Handler implements IMessageHandler<PacketWarpEnderParticles, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketWarpEnderParticles message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null) {
                for (int i = 0; i < 32; ++i) {
                    mc.theWorld.spawnParticle(
                        "portal",
                        message.x + (mc.theWorld.rand.nextDouble() - 0.5D) * 1.5D,
                        message.y + mc.theWorld.rand.nextDouble() * 2.0D - 0.25D,
                        message.z + (mc.theWorld.rand.nextDouble() - 0.5D) * 1.5D,
                        (mc.theWorld.rand.nextDouble() - 0.5D) * 2.0D,
                        -mc.theWorld.rand.nextDouble(),
                        (mc.theWorld.rand.nextDouble() - 0.5D) * 2.0D);
                }
            }
            return null;
        }
    }
}
