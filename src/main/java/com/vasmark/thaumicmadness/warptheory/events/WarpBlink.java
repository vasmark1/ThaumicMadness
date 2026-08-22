package com.vasmark.thaumicmadness.warptheory.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.network.NetworkHandler;
import com.vasmark.thaumicmadness.network.PacketWarpEnderParticles;
import com.vasmark.thaumicmadness.warptheory.util.WarpTheoryHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;

public class WarpBlink extends IWarpEvent {

    public WarpBlink() {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public String getName() {
        return "blink";
    }

    @Override
    public int getSeverity() {
        return 30;
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        WarpTheoryHelper.sendChat(player, "§5§o" + StatCollector.translateToLocal("chat.warptheory.blink"));
        WarpTheoryHelper.modEventInt(player, "blink", 10 + world.rand.nextInt(20));
        return true;
    }

    @SubscribeEvent
    public void onTick(WorldTickEvent e) {
        if (e.phase == Phase.END && e.side == Side.SERVER) {
            for (Object obj : e.world.playerEntities) {
                if (!(obj instanceof EntityPlayer)) continue;
                EntityPlayer player = (EntityPlayer) obj;
                NBTTagCompound tag = WarpTheoryHelper.getWarpTag(player);
                if (tag.hasKey("blink") && e.world.getTotalWorldTime() % 20L == 0L) {
                    int blink = tag.getInteger("blink");
                    for (int i = 0; i < 8; ++i) {
                        int tx = (int) player.posX + e.world.rand.nextInt(16) - e.world.rand.nextInt(16);
                        int ty = (int) player.posY + e.world.rand.nextInt(16) - e.world.rand.nextInt(16);
                        int tz = (int) player.posZ + e.world.rand.nextInt(16) - e.world.rand.nextInt(16);
                        if (e.world.isAirBlock(tx, ty, tz) && e.world.isAirBlock(tx, ty + 1, tz)
                            && !e.world.isAirBlock(tx, ty - 1, tz)) {
                            player.rotationPitch = (float) e.world.rand.nextInt(90) + e.world.rand.nextFloat()
                                - ((float) e.world.rand.nextInt(90) + e.world.rand.nextFloat());
                            player.rotationYaw = (float) e.world.rand.nextInt(360) + e.world.rand.nextFloat()
                                - ((float) e.world.rand.nextInt(360) + e.world.rand.nextFloat());
                            double dX = tx + e.world.rand.nextDouble();
                            double dY = ty + e.world.rand.nextDouble();
                            double dZ = tz + e.world.rand.nextDouble();
                            player.setPositionAndUpdate(dX, dY, dZ);

                            NetworkHandler.INSTANCE.sendToAllAround(
                                new PacketWarpEnderParticles(dX, dY, dZ),
                                new TargetPoint(e.world.provider.dimensionId, dX, dY, dZ, 32.0D));
                            e.world.playSoundEffect(dX, dY, dZ, "mob.endermen.portal", 1.0F, 1.0F);

                            --blink;
                            tag.setInteger("blink", blink);
                            if (blink <= 0) {
                                tag.removeTag("blink");
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
