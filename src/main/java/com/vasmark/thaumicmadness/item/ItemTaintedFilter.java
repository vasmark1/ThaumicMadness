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

public class ItemTaintedFilter extends Item {

    public ItemTaintedFilter() {
        super();
        this.setMaxStackSize(64);
        this.setUnlocalizedName("thaumicmadness.tainted_filter");
        this.setTextureName("thaumicmadness:filter_tainted");
        this.setCreativeTab(ModCreativeTabs.tabMyMod);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(
            EnumChatFormatting.DARK_PURPLE
                + StatCollector.translateToLocal("item.thaumicmadness.tainted_filter.desc1"));
        list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.thaumicmadness.tainted_filter.desc2"));
    }
}
