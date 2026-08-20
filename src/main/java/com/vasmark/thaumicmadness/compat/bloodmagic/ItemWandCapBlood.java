package com.vasmark.thaumicmadness.compat.bloodmagic;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWandCapBlood extends Item {

    public IIcon[] icon = new IIcon[2];

    public ItemWandCapBlood() {
        super();
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(com.vasmark.thaumicmadness.ModCreativeTabs.tabMyMod);
        this.setUnlocalizedName("wand_cap_blood");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        this.icon[0] = ir.registerIcon("thaumicmadness:cap_blood");
        this.icon[1] = ir.registerIcon("thaumicmadness:cap_blood_inert");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return this.icon[damage % this.icon.length];
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + (stack.getItemDamage() == 1 ? "inert" : "charged");
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        if (stack.getItemDamage() == 0) {
            list.add("§4" + StatCollector.translateToLocal("item.wand_cap_blood.charged.desc"));
        } else {
            list.add("§7" + StatCollector.translateToLocal("item.wand_cap_blood.inert.desc"));
        }
    }
}
