package com.vasmark.thaumicmadness.compat.baubles;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.expanded.IBaubleExpanded;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Base accessory item supporting both Baubles-Expanded (multi-slot) and legacy Baubles.
 */
@Optional.InterfaceList({ @Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles"),
    @Optional.Interface(iface = "baubles.api.expanded.IBaubleExpanded", modid = "Baubles") })
public abstract class ItemBaubleBase extends Item implements IBauble, IBaubleExpanded {

    protected final String[] expandedSlots;
    protected final BaubleType legacyType;

    public ItemBaubleBase(BaubleType legacyType, String... expandedSlots) {
        this.legacyType = legacyType;
        this.expandedSlots = (expandedSlots != null && expandedSlots.length > 0) ? expandedSlots
            : new String[] { legacyType.name()
                .toLowerCase() };
        this.maxStackSize = 1;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public BaubleType getBaubleType(ItemStack itemstack) {
        return this.legacyType;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public String[] getBaubleTypes(ItemStack itemstack) {
        return this.expandedSlots;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        // Subclasses override
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
        // Subclasses override
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        // Subclasses override
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);
        if (expandedSlots != null && expandedSlots.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < expandedSlots.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(StatCollector.translateToLocal("baubles.slot." + expandedSlots[i]));
            }
            list.add(
                EnumChatFormatting.BLUE + StatCollector.translateToLocal("item.bauble.slots")
                    + ": "
                    + EnumChatFormatting.GRAY
                    + sb.toString());
        }
    }
}
