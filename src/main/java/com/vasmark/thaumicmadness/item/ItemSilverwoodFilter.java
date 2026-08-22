package com.vasmark.thaumicmadness.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.vasmark.thaumicmadness.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSilverwoodFilter extends Item {

    public ItemSilverwoodFilter() {
        super();
        this.setMaxStackSize(16);
        this.setUnlocalizedName("thaumicmadness.silverwood_filter");
        this.setTextureName("thaumicmadness:filter_silverwood");
        this.setCreativeTab(ModCreativeTabs.tabMyMod);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(
            EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.thaumicmadness.silverwood_filter.desc1"));
        list.add(
            EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.thaumicmadness.silverwood_filter.desc2"));
    }
}
