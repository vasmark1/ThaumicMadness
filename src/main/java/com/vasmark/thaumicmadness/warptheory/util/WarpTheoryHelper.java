package com.vasmark.thaumicmadness.warptheory.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class WarpTheoryHelper {

    public static NBTTagCompound getWarpTag(EntityPlayer player) {
        if (!player.getEntityData()
            .hasKey("WarpTheory")) {
            NBTTagCompound tag = new NBTTagCompound();
            player.getEntityData()
                .setTag("WarpTheory", tag);
            return tag;
        }
        return player.getEntityData()
            .getCompoundTag("WarpTheory");
    }

    public static NBTTagCompound modEventInt(EntityPlayer player, String tagName, int amount) {
        NBTTagCompound tag = getWarpTag(player);
        tag.setInteger(tagName, tag.getInteger(tagName) + amount);
        return tag;
    }

    public static void sendChat(EntityPlayer player, String message) {
        if (player != null && message != null) {
            player.addChatMessage(new ChatComponentText(message));
        }
    }

    public static boolean hasNonSolidNeighbor(World world, int x, int y, int z) {
        int[][] offsets = { { 0, 1, 0 }, { 0, -1, 0 }, { 1, 0, 0 }, { -1, 0, 0 }, { 0, 0, 1 }, { 0, 0, -1 } };
        for (int[] off : offsets) {
            int nx = x + off[0];
            int ny = y + off[1];
            int nz = z + off[2];
            if (world.isAirBlock(nx, ny, nz) || !world.getBlock(nx, ny, nz)
                .isOpaqueCube()) {
                return true;
            }
        }
        return false;
    }
}
