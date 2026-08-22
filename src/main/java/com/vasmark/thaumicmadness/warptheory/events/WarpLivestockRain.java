package com.vasmark.thaumicmadness.warptheory.events;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
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

public class WarpLivestockRain extends IWarpEvent {

    public WarpLivestockRain() {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public String getName() {
        return "livestock";
    }

    @Override
    public int getSeverity() {
        return 32;
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        WarpTheoryHelper.sendChat(player, "§5§o" + StatCollector.translateToLocal("chat.warptheory.livestock"));
        WarpTheoryHelper.modEventInt(player, this.getName(), 5 + world.rand.nextInt(10));
        return true;
    }

    @SubscribeEvent
    public void onTick(WorldTickEvent e) {
        if (e.phase == Phase.END && e.side == Side.SERVER) {
            for (Object obj : e.world.playerEntities) {
                if (!(obj instanceof EntityPlayer)) continue;
                EntityPlayer player = (EntityPlayer) obj;
                NBTTagCompound tag = WarpTheoryHelper.getWarpTag(player);
                if (tag.hasKey("livestock")) {
                    int livestock = tag.getInteger("livestock");
                    for (int i = 0; i < 6; ++i) {
                        int tx = (int) player.posX + e.world.rand.nextInt(8) - e.world.rand.nextInt(8);
                        int ty = (int) player.posY + e.world.rand.nextInt(8) - e.world.rand.nextInt(8);
                        int tz = (int) player.posZ + e.world.rand.nextInt(8) - e.world.rand.nextInt(8);

                        boolean canDrop = true;
                        for (int y = ty; y < ty + 25; ++y) {
                            if (!e.world.isAirBlock(tx, y, tz)) {
                                canDrop = false;
                                break;
                            }
                        }

                        if (canDrop) {
                            ty += 25;
                            if (e.world.isAirBlock(tx, ty, tz)) {
                                EntityLiving entity;
                                switch (e.world.rand.nextInt(4)) {
                                    case 0:
                                        entity = new EntityCow(e.world);
                                        break;
                                    case 1:
                                        entity = new EntityPig(e.world);
                                        break;
                                    case 2:
                                        entity = new EntitySheep(e.world);
                                        break;
                                    default:
                                        entity = new EntityChicken(e.world);
                                        break;
                                }
                                entity.playLivingSound();
                                entity.setLocationAndAngles(
                                    tx + e.world.rand.nextDouble(),
                                    ty + e.world.rand.nextDouble(),
                                    tz + e.world.rand.nextDouble(),
                                    e.world.rand.nextFloat(),
                                    e.world.rand.nextFloat());
                                if (e.world.spawnEntityInWorld(entity)) {
                                    --livestock;
                                    tag.setInteger("livestock", livestock);
                                    if (livestock <= 0) {
                                        tag.removeTag("livestock");
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
}
