package com.vasmark.thaumicmadness.warptheory.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.network.NetworkHandler;
import com.vasmark.thaumicmadness.network.PacketWarpBloodDrip;
import com.vasmark.thaumicmadness.warptheory.util.WarpTheoryHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;

public class WarpBlood extends IWarpEvent {

    public WarpBlood() {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public String getName() {
        return "blood";
    }

    @Override
    public int getSeverity() {
        return 25;
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        WarpTheoryHelper.sendChat(player, "§5§o" + StatCollector.translateToLocal("chat.warptheory.blood"));
        WarpTheoryHelper.modEventInt(player, "blood", 64 + world.rand.nextInt(128));
        return true;
    }

    @SubscribeEvent
    public void onTick(WorldTickEvent e) {
        if (e.phase == Phase.END && e.side == Side.SERVER) {
            for (Object obj : e.world.playerEntities) {
                if (!(obj instanceof EntityPlayer)) continue;
                EntityPlayer player = (EntityPlayer) obj;
                NBTTagCompound tag = WarpTheoryHelper.getWarpTag(player);
                if (tag.hasKey("blood")) {
                    int blood = tag.getInteger("blood");
                    for (int i = 0; i < 6; ++i) {
                        int tx = (int) player.posX + e.world.rand.nextInt(8) - e.world.rand.nextInt(8);
                        int ty = (int) player.posY + e.world.rand.nextInt(8) - e.world.rand.nextInt(8);
                        int tz = (int) player.posZ + e.world.rand.nextInt(8) - e.world.rand.nextInt(8);
                        if (e.world.isAirBlock(tx, ty - 1, tz) && !e.world.isAirBlock(tx, ty, tz)
                            && e.world.getBlock(tx, ty, tz)
                                .getMaterial()
                                .blocksMovement()) {
                            NetworkHandler.INSTANCE.sendToAllAround(
                                new PacketWarpBloodDrip(tx + 0.5D, ty, tz + 0.5D),
                                new TargetPoint(e.world.provider.dimensionId, tx, ty, tz, 32.0D));
                            --blood;
                            tag.setInteger("blood", blood);
                            if (blood <= 0) {
                                tag.removeTag("blood");
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
