package com.vasmark.thaumicmadness.warptheory.events;

import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.warptheory.util.WarpTheoryHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public class WarpBats extends IWarpEvent {

    public WarpBats() {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public String getName() {
        return "bats";
    }

    @Override
    public int getSeverity() {
        return 15;
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        WarpTheoryHelper.sendChat(player, "§5§o" + StatCollector.translateToLocal("chat.warptheory.bats"));
        WarpTheoryHelper.modEventInt(player, "bats", 15 + world.rand.nextInt(30));
        return true;
    }

    @SubscribeEvent
    public void onTick(WorldTickEvent e) {
        if (e.phase == Phase.END && e.side == Side.SERVER) {
            for (Object obj : e.world.playerEntities) {
                if (!(obj instanceof EntityPlayer)) continue;
                EntityPlayer player = (EntityPlayer) obj;
                NBTTagCompound tag = WarpTheoryHelper.getWarpTag(player);
                if (tag.hasKey("bats")) {
                    int bats = tag.getInteger("bats");
                    for (int i = 0; i < 6; ++i) {
                        int tx = (int) player.posX + e.world.rand.nextInt(8) - e.world.rand.nextInt(8);
                        int ty = (int) player.posY + e.world.rand.nextInt(8) - e.world.rand.nextInt(8);
                        int tz = (int) player.posZ + e.world.rand.nextInt(8) - e.world.rand.nextInt(8);
                        if (e.world.isAirBlock(tx, ty, tz)) {
                            EntityBat bat = new EntityBat(e.world);
                            bat.playLivingSound();
                            bat.setLocationAndAngles(
                                tx + e.world.rand.nextDouble(),
                                ty + e.world.rand.nextDouble(),
                                tz + e.world.rand.nextDouble(),
                                e.world.rand.nextFloat(),
                                e.world.rand.nextFloat());
                            if (e.world.spawnEntityInWorld(bat)) {
                                --bats;
                                tag.setInteger("bats", bats);
                                if (bats <= 0) {
                                    tag.removeTag("bats");
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
