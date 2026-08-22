package com.vasmark.thaumicmadness.warptheory.events;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import com.vasmark.thaumicmadness.warptheory.WarpTheoryManager;
import com.vasmark.thaumicmadness.warptheory.util.WarpTheoryHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public class WarpDecay extends IWarpEvent {

    public WarpDecay() {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public String getName() {
        return "biomeDecay";
    }

    @Override
    public int getSeverity() {
        return 50;
    }

    @Override
    public boolean canDo(EntityPlayer player) {
        NBTTagCompound tag = WarpTheoryHelper.getWarpTag(player);
        for (Object key : tag.func_150296_c()) {
            if (key instanceof String) {
                String n = (String) key;
                if (n.startsWith("biome") && !n.equals(this.getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        WarpTheoryHelper.sendChat(player, "§5§o" + StatCollector.translateToLocal("chat.warptheory.decay"));
        WarpTheoryHelper.modEventInt(player, this.getName(), 512 + world.rand.nextInt(256));
        return true;
    }

    @SubscribeEvent
    public void onTick(WorldTickEvent e) {
        if (e.phase == Phase.END && e.side == Side.SERVER) {
            for (Object obj : e.world.playerEntities) {
                if (!(obj instanceof EntityPlayer)) continue;
                EntityPlayer player = (EntityPlayer) obj;
                NBTTagCompound tag = WarpTheoryHelper.getWarpTag(player);
                if (tag.hasKey("biomeDecay")) {
                    int decay = tag.getInteger("biomeDecay");
                    int tx = (int) player.posX + e.world.rand.nextInt(8) - e.world.rand.nextInt(8);
                    int ty = (int) player.posY + e.world.rand.nextInt(8) - e.world.rand.nextInt(8);
                    int tz = (int) player.posZ + e.world.rand.nextInt(8) - e.world.rand.nextInt(8);

                    if (!WarpTheoryHelper.hasNonSolidNeighbor(e.world, tx, ty, tz)) continue;

                    Block b = e.world.getBlock(tx, ty, tz);
                    int meta = e.world.getBlockMetadata(tx, ty, tz);
                    Block decayed = WarpTheoryManager.getInstance()
                        .getDecayBlock(b);

                    if (decayed != null || b instanceof IPlantable || b.getMaterial() == Material.leaves) {
                        if (decayed == null) decayed = Blocks.air;
                        if (e.world.setBlock(tx, ty, tz, decayed, 0, 3)) {
                            if (decayed == Blocks.air) {
                                e.world
                                    .playAuxSFXAtEntity(null, 2001, tx, ty, tz, Block.getIdFromBlock(b) + (meta << 12));
                            }
                            --decay;
                            tag.setInteger("biomeDecay", decay);
                            if (decay <= 0) {
                                tag.removeTag("biomeDecay");
                            }
                        }
                    }
                }
            }
        }
    }
}
