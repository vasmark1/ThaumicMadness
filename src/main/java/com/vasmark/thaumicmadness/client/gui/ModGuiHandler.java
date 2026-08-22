package com.vasmark.thaumicmadness.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.compact.infusion.ContainerCompactInfusionMatrix;
import com.vasmark.thaumicmadness.compact.infusion.GuiCompactInfusionMatrix;
import com.vasmark.thaumicmadness.compact.infusion.TileCompactInfusionMatrix;

import cpw.mods.fml.common.network.IGuiHandler;

public class ModGuiHandler implements IGuiHandler {

    public static final int GUI_COMPACT_INFUSION_MATRIX = 10;
    public static final int GUI_COMPACT_INFERNAL_FURNACE = 11;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_COMPACT_INFUSION_MATRIX) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileCompactInfusionMatrix) {
                return new ContainerCompactInfusionMatrix(player.inventory, (TileCompactInfusionMatrix) te);
            }
        } else if (ID == GUI_COMPACT_INFERNAL_FURNACE) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof com.vasmark.thaumicmadness.compact.furnace.TileCompactInfernalFurnace) {
                return new com.vasmark.thaumicmadness.compact.furnace.ContainerCompactInfernalFurnace(
                    player.inventory,
                    (com.vasmark.thaumicmadness.compact.furnace.TileCompactInfernalFurnace) te);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_COMPACT_INFUSION_MATRIX) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileCompactInfusionMatrix) {
                return new GuiCompactInfusionMatrix(player.inventory, (TileCompactInfusionMatrix) te);
            }
        } else if (ID == GUI_COMPACT_INFERNAL_FURNACE) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof com.vasmark.thaumicmadness.compact.furnace.TileCompactInfernalFurnace) {
                return new com.vasmark.thaumicmadness.compact.furnace.GuiCompactInfernalFurnace(
                    player.inventory,
                    (com.vasmark.thaumicmadness.compact.furnace.TileCompactInfernalFurnace) te);
            }
        }
        return null;
    }
}
