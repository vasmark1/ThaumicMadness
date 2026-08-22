package com.vasmark.thaumicmadness.warptheory.events;

import net.minecraft.entity.effect.EntityLightningBolt;
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

public class WarpLightning extends IWarpEvent {

    public WarpLightning() {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public String getName() {
        return "lightning";
    }

    @Override
    public int getSeverity() {
        return 60;
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        WarpTheoryHelper.sendChat(player, "§5§o" + StatCollector.translateToLocal("chat.warptheory.lightning"));
        WarpTheoryHelper.modEventInt(player, "lightning", 5 + world.rand.nextInt(10));
        return true;
    }

    @SubscribeEvent
    public void onTick(WorldTickEvent e) {
        if (e.phase == Phase.END && e.side == Side.SERVER) {
            for (Object obj : e.world.playerEntities) {
                if (!(obj instanceof EntityPlayer)) continue;
                EntityPlayer player = (EntityPlayer) obj;
                NBTTagCompound tag = WarpTheoryHelper.getWarpTag(player);
                if (tag.hasKey("lightning")) {
                    int lightning = tag.getInteger("lightning");
                    int x = (int) player.posX + e.world.rand.nextInt(3) - e.world.rand.nextInt(3);
                    int y = (int) player.posY;
                    int z = (int) player.posZ + e.world.rand.nextInt(3) - e.world.rand.nextInt(3);
                    if (e.world.rand.nextInt(100) == 0 && e.world.canBlockSeeTheSky(x, y, z)) {
                        EntityLightningBolt bolt = new EntityLightningBolt(e.world, x, y, z);
                        e.world.addWeatherEffect(bolt);
                        --lightning;
                        tag.setInteger("lightning", lightning);
                        if (lightning <= 0) {
                            tag.removeTag("lightning");
                        }
                    }
                }
            }
        }
    }
}
