package com.vasmark.thaumicmadness.warptheory.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.warptheory.util.WarpTheoryHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public class WarpFakeSoundBehind extends IWarpEvent {

    private final String name;
    private final String sound;
    private int distance = 16;

    public WarpFakeSoundBehind(String name, String sound) {
        this.name = name;
        this.sound = sound;
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    public WarpFakeSoundBehind(String name, String sound, int distance) {
        this.name = name;
        this.sound = sound;
        this.distance = distance;
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getSeverity() {
        return 10;
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        WarpTheoryHelper.modEventInt(player, this.getName(), 1);
        return true;
    }

    @SubscribeEvent
    public void onTick(WorldTickEvent e) {
        if (e.phase == Phase.END && e.side == Side.SERVER) {
            for (Object obj : e.world.playerEntities) {
                if (!(obj instanceof EntityPlayer)) continue;
                EntityPlayer player = (EntityPlayer) obj;
                NBTTagCompound tag = WarpTheoryHelper.getWarpTag(player);
                int fakesound = tag.getInteger(this.getName());
                if (fakesound > 0 && e.world.getTotalWorldTime() % 20L == 0L) {
                    double yaw = player.getRotationYawHead();
                    double tx = player.posX
                        - this.distance * Math.sin(Math.toRadians(90.0D - yaw)) * Math.sin(Math.toRadians(yaw));
                    double tz = player.posZ
                        - this.distance * Math.sin(Math.toRadians(90.0D - yaw)) * Math.cos(Math.toRadians(yaw));
                    e.world.playSoundEffect(tx, player.posY, tz, this.sound, 1.0F, 1.0F);
                    --fakesound;
                    tag.setInteger(this.getName(), fakesound);
                }
            }
        }
    }
}
