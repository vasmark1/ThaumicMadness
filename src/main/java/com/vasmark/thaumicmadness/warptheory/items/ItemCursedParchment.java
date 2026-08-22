package com.vasmark.thaumicmadness.warptheory.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.ModCreativeTabs;
import com.vasmark.thaumicmadness.warptheory.WarpTheoryManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCursedParchment extends Item {

    @SideOnly(Side.CLIENT)
    private IIcon icon;

    public ItemCursedParchment() {
        super();
        this.setMaxStackSize(64);
        this.setCreativeTab(ModCreativeTabs.tabMyMod);
        this.setUnlocalizedName("thaumicmadness.cursed_parchment");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        this.icon = reg.registerIcon("thaumicmadness:itemPaper");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return this.icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            int totalWarp = WarpTheoryManager.getTotalWarp(player);
            int[] individualWarps = WarpTheoryManager.getIndividualWarps(player);
            String severity;
            if (totalWarp <= 10) {
                severity = StatCollector.translateToLocal("chat.warptheory.minorwarp");
            } else if (totalWarp <= 25) {
                severity = StatCollector.translateToLocal("chat.warptheory.averagewarp");
            } else if (totalWarp <= 50) {
                severity = StatCollector.translateToLocal("chat.warptheory.majorwarp");
            } else {
                severity = StatCollector.translateToLocal("chat.warptheory.deadlywarp");
            }

            player.addChatMessage(new ChatComponentText("§5§o" + severity));
            player.addChatMessage(
                new ChatComponentText(
                    "§7 (" + individualWarps[0]
                        + " "
                        + StatCollector.translateToLocal("chat.warptheory.permanentwarp")
                        + ", "
                        + individualWarps[1]
                        + " "
                        + StatCollector.translateToLocal("chat.warptheory.normalwarp")
                        + ", "
                        + individualWarps[2]
                        + " "
                        + StatCollector.translateToLocal("chat.warptheory.tempwarp")
                        + ")"));
        }

        if (!player.capabilities.isCreativeMode && WarpTheoryManager.getTotalWarp(player) <= 10) {
            --stack.stackSize;
        }

        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean advanced) {
        infoList.add("§8§o" + StatCollector.translateToLocal("tooltip.warptheory.paper"));
    }
}
