package com.vasmark.thaumicmadness.warptheory.events;

import net.minecraft.entity.boss.EntityWither;
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

public class WarpWither extends IWarpEvent {

    public WarpWither() {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public String getName() {
        return "wither";
    }

    @Override
    public int getSeverity() {
        return 70;
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        WarpTheoryHelper.sendChat(player, "§5§o" + StatCollector.translateToLocal("chat.warptheory.wither"));
        WarpTheoryHelper.modEventInt(player, "wither", 1);
        return true;
    }

    @SubscribeEvent
    public void onTick(WorldTickEvent e) {
        if (e.phase == Phase.END && e.side == Side.SERVER) {
            for (Object obj : e.world.playerEntities) {
                if (!(obj instanceof EntityPlayer)) continue;
                EntityPlayer player = (EntityPlayer) obj;
                NBTTagCompound tag = WarpTheoryHelper.getWarpTag(player);
                if (tag.hasKey("wither")) {
                    int wither = tag.getInteger("wither");
                    for (int i = 0; i < 6; ++i) {
                        int tx = (int) player.posX + e.world.rand.nextInt(4) - e.world.rand.nextInt(4);
                        int ty = (int) player.posY + e.world.rand.nextInt(4) - e.world.rand.nextInt(4);
                        int tz = (int) player.posZ + e.world.rand.nextInt(4) - e.world.rand.nextInt(4);

                        if (!e.world.getBlock(tx, ty - 1, tz)
                            .getMaterial()
                            .blocksMovement()) {
                            continue;
                        }

                        EntityLightningBolt bolt = new EntityLightningBolt(e.world, tx, ty, tz);
                        e.world.addWeatherEffect(bolt);
                        e.world.playSoundEffect(
                            tx,
                            ty,
                            tz,
                            "random.explode",
                            4.0F,
                            (1.0F + (e.world.rand.nextFloat() - e.world.rand.nextFloat()) * 0.2F) * 0.7F);
                        e.world.spawnParticle("hugeexplosion", tx, ty, tz, 1.0D, 0.0D, 0.0D);

                        EntityWither entityWither = new EntityWither(e.world);
                        entityWither.setLocationAndAngles(
                            tx + 0.5D,
                            ty + 0.5D,
                            tz + 0.5D,
                            e.world.rand.nextFloat(),
                            e.world.rand.nextFloat());
                        entityWither.func_82206_m(); // Invulnerability initialization
                        if (e.world.spawnEntityInWorld(entityWither)) {
                            --wither;
                            tag.setInteger("wither", wither);
                            if (wither <= 0) {
                                tag.removeTag("wither");
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
