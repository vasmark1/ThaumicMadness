package com.vasmark.thaumicmadness.warptheory.events;

import net.minecraft.block.BlockSapling;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import com.vasmark.thaumicmadness.warptheory.util.WarpTheoryHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public class WarpSwamp extends IWarpEvent {

    public WarpSwamp() {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public String getName() {
        return "biomeSwamp";
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
        WarpTheoryHelper.sendChat(player, "§5§o" + StatCollector.translateToLocal("chat.warptheory.swamp"));
        WarpTheoryHelper.modEventInt(player, "biomeSwamp", 256 + world.rand.nextInt(256));
        return true;
    }

    @SubscribeEvent
    public void onTick(WorldTickEvent e) {
        if (e.phase == Phase.END && e.side == Side.SERVER) {
            for (Object obj : e.world.playerEntities) {
                if (!(obj instanceof EntityPlayer)) continue;
                EntityPlayer player = (EntityPlayer) obj;
                NBTTagCompound tag = WarpTheoryHelper.getWarpTag(player);
                if (tag.hasKey("biomeSwamp")) {
                    int biomeSwamp = tag.getInteger("biomeSwamp");
                    int tx = (int) player.posX + e.world.rand.nextInt(8) - e.world.rand.nextInt(8);
                    int ty = (int) player.posY + e.world.rand.nextInt(8) - e.world.rand.nextInt(8);
                    int tz = (int) player.posZ + e.world.rand.nextInt(8) - e.world.rand.nextInt(8);

                    if (!WarpTheoryHelper.hasNonSolidNeighbor(e.world, tx, ty, tz)) continue;

                    boolean grown = false;
                    if (e.world.getBlock(tx, ty, tz) == Blocks.water) {
                        if (e.world.isAirBlock(tx, ty + 1, tz)
                            && e.world.setBlock(tx, ty, tz, Blocks.waterlily, 0, 3)) {
                            grown = true;
                        }
                    } else if (e.world.getBlock(tx, ty, tz) == Blocks.sapling) {
                        ((BlockSapling) e.world.getBlock(tx, ty, tz)).func_149878_d(e.world, tx, ty, tz, e.world.rand);
                        grown = true;
                    } else if (e.world.getBlock(tx, ty, tz)
                        .getMaterial() != Material.leaves && e.world.getBlock(tx, ty, tz) != Blocks.log
                        && e.world.getBlock(tx, ty, tz) != Blocks.log2) {
                            if (e.world.rand.nextBoolean() && e.world.getBlock(tx, ty, tz)
                                .canSustainPlant(e.world, tx, ty, tz, ForgeDirection.UP, (IPlantable) Blocks.sapling)) {
                                if (e.world.rand.nextBoolean()) {
                                    if (e.world.isAirBlock(tx, ty + 1, tz)
                                        || (e.world.getBlock(tx, ty, tz) instanceof IPlantable
                                            && e.world.getBlock(tx, ty, tz) != Blocks.sapling)) {
                                        e.world.setBlock(tx, ty, tz, Blocks.sapling, e.world.rand.nextInt(6), 3);
                                        grown = true;
                                    }
                                } else if (e.world.rand.nextBoolean()) {
                                    if (e.world.isAirBlock(tx, ty + 1, tz)
                                        && e.world.getBlock(tx, ty, tz) instanceof IGrowable) {
                                        ((IGrowable) e.world.getBlock(tx, ty, tz))
                                            .func_149853_b(e.world, e.world.rand, tx, ty, tz);
                                        grown = true;
                                    }
                                }
                            }
                        }

                    if (grown) {
                        --biomeSwamp;
                        tag.setInteger("biomeSwamp", biomeSwamp);
                        if (biomeSwamp <= 0) {
                            tag.removeTag("biomeSwamp");
                        }
                    }
                }
            }
        }
    }
}
