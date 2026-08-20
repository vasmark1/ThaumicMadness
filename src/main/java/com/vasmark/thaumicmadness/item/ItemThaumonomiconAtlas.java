package com.vasmark.thaumicmadness.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.ModCreativeTabs;
import com.vasmark.thaumicmadness.nodetracker.gui.GuiNodeTracker;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IGoggles;
import thaumcraft.api.nodes.IRevealer;
import thaumcraft.client.gui.GuiResearchBrowser;

@Optional.InterfaceList({ @Optional.Interface(iface = "travellersgear.api.ITravellersGear", modid = "TravellersGear") })
public class ItemThaumonomiconAtlas extends Item implements IGoggles, IRevealer, travellersgear.api.ITravellersGear {

    public ItemThaumonomiconAtlas() {
        super();
        this.setMaxStackSize(1);
        this.setCreativeTab(ModCreativeTabs.tabMyMod);
        this.setUnlocalizedName("thaumonomicon_atlas");
        this.setTextureName("thaumicmadness:thaumonomicon_atlas");
    }

    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
        return false;
    }

    @Override
    @Optional.Method(modid = "TravellersGear")
    public int getSlot(ItemStack stack) {
        return -1; // Explicitly forbidden from any gear or bauble slot
    }

    @Override
    @Optional.Method(modid = "TravellersGear")
    public void onTravelGearTick(EntityPlayer player, ItemStack stack) {}

    @Override
    @Optional.Method(modid = "TravellersGear")
    public void onTravelGearEquip(EntityPlayer player, ItemStack stack) {}

    @Override
    @Optional.Method(modid = "TravellersGear")
    public void onTravelGearUnequip(EntityPlayer player, ItemStack stack) {}

    @Override
    public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            openClientGui(player);
        }
        return stack;
    }

    @SideOnly(Side.CLIENT)
    private void openClientGui(EntityPlayer player) {
        Minecraft mc = Minecraft.getMinecraft();
        if (player.isSneaking()) {
            mc.displayGuiScreen(new GuiNodeTracker());
            player.playSound("thaumcraft:page", 0.9F, 1.0F);
        } else {
            mc.displayGuiScreen(new GuiResearchBrowser());
            player.playSound("thaumcraft:page", 0.9F, 0.95F);
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return true;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("item.thaumonomicon_atlas.desc1"));
        list.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.thaumonomicon_atlas.desc2"));
        list.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("item.thaumonomicon_atlas.desc3"));
    }
}
