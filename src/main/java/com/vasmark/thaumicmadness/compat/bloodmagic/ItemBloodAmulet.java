package com.vasmark.thaumicmadness.compat.bloodmagic;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.vasmark.thaumicmadness.ModCreativeTabs;
import com.vasmark.thaumicmadness.compat.baubles.ItemBaubleBase;

import baubles.api.BaubleType;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;

@Optional.Interface(iface = "thaumcraft.api.IVisDiscountGear", modid = "Thaumcraft")
public class ItemBloodAmulet extends ItemBaubleBase implements IVisDiscountGear {

    public ItemBloodAmulet() {
        super(BaubleType.AMULET, "amulet", "body", "charm");
        setUnlocalizedName("blood_amulet");
        setTextureName("thaumicmadness:blood_amulet");
        setCreativeTab(ModCreativeTabs.tabMyMod);
    }

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        return 8; // 8% vis discount
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onWornTick(ItemStack itemstack, EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;

        // When wearing the amulet, mitigate environmental damage / burning slightly
        if (player.isBurning() && player.ticksExisted % 30 == 0) {
            player.extinguish();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("item.blood_amulet.desc"));
        list.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("tc.visdiscount") + ": 8%");
        super.addInformation(stack, player, list, advanced);
    }
}
