package com.vasmark.thaumicmadness.warptheory.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.warptheory.entity.EntityPassiveFriend;
import com.vasmark.thaumicmadness.warptheory.util.WarpTheoryHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public class WarpFriend extends IWarpEvent {

    public WarpFriend() {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public String getName() {
        return "friend";
    }

    @Override
    public int getSeverity() {
        return 26;
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        WarpTheoryHelper.sendChat(player, "§5§o" + StatCollector.translateToLocal("chat.warptheory.friend"));
        WarpTheoryHelper.modEventInt(player, "friend", 1);
        return true;
    }

    @SubscribeEvent
    public void onTick(WorldTickEvent e) {
        if (e.phase == Phase.END && e.side == Side.SERVER) {
            for (Object obj : e.world.playerEntities) {
                if (!(obj instanceof EntityPlayer)) continue;
                EntityPlayer player = (EntityPlayer) obj;
                NBTTagCompound tag = WarpTheoryHelper.getWarpTag(player);
                if (tag.hasKey("friend")) {
                    int friend = tag.getInteger("friend");
                    for (int i = 0; i < 6; ++i) {
                        int tx = (int) player.posX + e.world.rand.nextInt(4) - e.world.rand.nextInt(4);
                        int ty = (int) player.posY + e.world.rand.nextInt(4) - e.world.rand.nextInt(4);
                        int tz = (int) player.posZ + e.world.rand.nextInt(4) - e.world.rand.nextInt(4);
                        if (e.world.isAirBlock(tx, ty, tz) && e.world.isAirBlock(tx, ty + 1, tz)) {
                            EntityPassiveFriend creeper = new EntityPassiveFriend(e.world);
                            creeper.setCustomNameTag("Friend");
                            creeper.playLivingSound();
                            creeper.setLocationAndAngles(
                                tx + e.world.rand.nextDouble(),
                                ty + e.world.rand.nextDouble(),
                                tz + e.world.rand.nextDouble(),
                                e.world.rand.nextFloat(),
                                e.world.rand.nextFloat());
                            if (e.world.spawnEntityInWorld(creeper)) {
                                --friend;
                                tag.setInteger("friend", friend);
                                if (friend <= 0) {
                                    tag.removeTag("friend");
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
