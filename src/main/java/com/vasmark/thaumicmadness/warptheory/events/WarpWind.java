package com.vasmark.thaumicmadness.warptheory.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.network.NetworkHandler;
import com.vasmark.thaumicmadness.network.PacketWarpVelocity;
import com.vasmark.thaumicmadness.warptheory.util.WarpTheoryHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;

public class WarpWind extends IWarpEvent {

    public WarpWind() {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public String getName() {
        return "wind";
    }

    @Override
    public int getSeverity() {
        return 35;
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        WarpTheoryHelper.sendChat(player, "§5§o" + StatCollector.translateToLocal("chat.warptheory.wind"));
        WarpTheoryHelper.modEventInt(player, "wind", 5 + world.rand.nextInt(10));
        return true;
    }

    @SubscribeEvent
    public void onTick(WorldTickEvent e) {
        if (e.phase == Phase.END && e.side == Side.SERVER) {
            for (Object obj : e.world.playerEntities) {
                if (!(obj instanceof EntityPlayer)) continue;
                EntityPlayer player = (EntityPlayer) obj;
                NBTTagCompound tag = WarpTheoryHelper.getWarpTag(player);
                if (tag.hasKey("wind") && e.world.rand.nextBoolean() && e.world.getTotalWorldTime() % 20L == 0L) {
                    int wind = tag.getInteger("wind");
                    double vx = (e.world.rand.nextDouble() - 0.5D) * 1.5D;
                    double vy = e.world.rand.nextDouble() * 0.5D;
                    double vz = (e.world.rand.nextDouble() - 0.5D) * 1.5D;
                    NetworkHandler.INSTANCE.sendToAllAround(
                        new PacketWarpVelocity(vx, vy, vz),
                        new TargetPoint(e.world.provider.dimensionId, player.posX, player.posY, player.posZ, 32.0D));
                    --wind;
                    tag.setInteger("wind", wind);
                    if (wind <= 0) {
                        tag.removeTag("wind");
                    }
                }
            }
        }
    }
}
