package com.vasmark.thaumicmadness.compat.bloodmagic;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.vasmark.thaumicmadness.ModCreativeTabs;
import com.vasmark.thaumicmadness.compat.baubles.ItemBaubleBase;

import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import baubles.api.BaubleType;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBloodRing extends ItemBaubleBase {

    private static final int LP_PER_HEAL = 25;

    public ItemBloodRing() {
        super(BaubleType.RING, "ring", "charm", "amulet");
        setUnlocalizedName("blood_ring");
        setTextureName("thaumicmadness:blood_ring");
        setCreativeTab(ModCreativeTabs.tabMyMod);
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onWornTick(ItemStack itemstack, EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;

        if (player.worldObj.isRemote) return;

        if (player.ticksExisted % 40 == 0 && player.getHealth() < player.getMaxHealth()) {
            String owner = player.getCommandSenderName();
            int currentLp = SoulNetworkHandler.getCurrentEssence(owner);
            if (currentLp >= LP_PER_HEAL) {
                SoulNetworkHandler.syphonFromNetwork(owner, LP_PER_HEAL);
                player.heal(1.0F);
                player.worldObj.playSoundAtEntity(player, "thaumcraft:heal", 0.3F, 1.2F);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("item.blood_ring.desc"));
        super.addInformation(stack, player, list, advanced);
    }
}
