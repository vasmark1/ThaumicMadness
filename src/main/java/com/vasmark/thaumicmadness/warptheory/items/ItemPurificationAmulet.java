package com.vasmark.thaumicmadness.warptheory.items;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.vasmark.thaumicmadness.ModCreativeTabs;
import com.vasmark.thaumicmadness.compat.baubles.ItemBaubleBase;
import com.vasmark.thaumicmadness.warptheory.WarpTheoryManager;
import com.vasmark.thaumicmadness.warptheory.events.IWarpEvent;

import baubles.api.BaubleType;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPurificationAmulet extends ItemBaubleBase {

    public ItemPurificationAmulet() {
        super(BaubleType.AMULET, "amulet", "body", "charm", "universal");
        this.setMaxStackSize(1);
        this.setCreativeTab(ModCreativeTabs.tabMyMod);
        this.setUnlocalizedName("thaumicmadness.purification_amulet");
        this.setTextureName("thaumicmadness:itemAmulet");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onWornTick(ItemStack itemstack, EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;

        if (player.worldObj.isRemote) return;

        // Every 500 ticks (~25 seconds), chance to siphon and purge 1 warp
        if (player.ticksExisted % 500 == 0) {
            int totalWarp = WarpTheoryManager.getTotalWarp(player);
            if (totalWarp > 0) {
                if (player.worldObj.rand.nextInt(100) <= Math.sqrt(totalWarp)) {
                    IWarpEvent event = WarpTheoryManager.queueOneEvent(player, totalWarp);
                    if (event != null) {
                        WarpTheoryManager.removeWarp(player, event.getCost());
                    }
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean advanced) {
        infoList.add("§8§o" + StatCollector.translateToLocal("tooltip.warptheory.amulet"));
    }
}
