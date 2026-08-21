package com.vasmark.thaumicmadness.item;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.vasmark.thaumicmadness.ModCreativeTabs;
import com.vasmark.thaumicmadness.compat.baubles.ItemBaubleBase;

import baubles.api.BaubleType;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IWarpingGear;

@Optional.Interface(iface = "thaumcraft.api.IWarpingGear", modid = "Thaumcraft")
public class ItemSanityCharm extends ItemBaubleBase implements IWarpingGear {

    public ItemSanityCharm() {
        super(BaubleType.AMULET, "charm", "head", "earring", "amulet");
        setUnlocalizedName("sanity_charm");
        setTextureName("thaumicmadness:sanity_charm");
        setCreativeTab(ModCreativeTabs.tabMyMod);
    }

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public int getWarp(ItemStack itemstack, EntityPlayer player) {
        return -2; // Negative warp value provides mental shield against warp events
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onWornTick(ItemStack itemstack, EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;

        if (player.worldObj.isRemote) return;

        // Cleanse warp debuffs faster
        if (player.ticksExisted % 40 == 0) {
            if (player.isPotionActive(Potion.confusion)) {
                player.removePotionEffect(Potion.confusion.id);
            }
            if (player.isPotionActive(Potion.blindness)) {
                player.removePotionEffect(Potion.blindness.id);
            }
            if (player.isPotionActive(Potion.hunger)) {
                player.removePotionEffect(Potion.hunger.id);
            }
            for (Potion potion : Potion.potionTypes) {
                if (potion != null && potion.getName() != null) {
                    String name = potion.getName()
                        .toLowerCase();
                    if (name.contains("unnaturalhunger") && player.isPotionActive(potion.id)) {
                        player.removePotionEffect(potion.id);
                    }
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("item.sanity_charm.desc"));
        list.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.sanity_charm.effect"));
        super.addInformation(stack, player, list, advanced);
    }
}
