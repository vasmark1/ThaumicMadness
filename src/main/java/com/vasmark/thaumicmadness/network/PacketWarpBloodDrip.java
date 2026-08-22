package com.vasmark.thaumicmadness.network;

import net.minecraft.client.Minecraft;

import com.vasmark.thaumicmadness.warptheory.entity.EntityBloodDripFX;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class PacketWarpBloodDrip implements IMessage {

    public double x;
    public double y;
    public double z;

    public PacketWarpBloodDrip() {}

    public PacketWarpBloodDrip(double x, double y, double z) {
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

    public static class Handler implements IMessageHandler<PacketWarpBloodDrip, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketWarpBloodDrip message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null) {
                EntityBloodDripFX fx = new EntityBloodDripFX(
                    mc.theWorld,
                    message.x,
                    message.y,
                    message.z,
                    0.55F,
                    0.0F,
                    0.0F);
                mc.effectRenderer.addEffect(fx);
            }
            return null;
        }
    }
}
