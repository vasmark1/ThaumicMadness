package com.vasmark.thaumicmadness.warptheory.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;

public class ItemUnstableCatalyst extends Item {

    @SideOnly(Side.CLIENT)
    private IIcon icon;

    public ItemUnstableCatalyst() {
        super();
        this.setMaxStackSize(64);
        this.setCreativeTab(ModCreativeTabs.tabMyMod);
        this.setUnlocalizedName("thaumicmadness.unstable_catalyst");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        this.icon = reg.registerIcon("thaumicmadness:itemSomething");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return this.icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.rare;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            player.addChatMessage(
                new ChatComponentText("§5§o" + StatCollector.translateToLocal("chat.warptheory.addwarp")));
            ThaumcraftApiHelper.addWarpToPlayer(player, 4 + world.rand.nextInt(4), false);
            ThaumcraftApiHelper.addWarpToPlayer(player, 5 + world.rand.nextInt(5), true);
            ThaumcraftApiHelper.addStickyWarpToPlayer(player, 5 + world.rand.nextInt(5));
        }

        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }

        return stack.stackSize <= 0 ? null : stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.eat;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean advanced) {
        infoList.add("§8§o" + StatCollector.translateToLocal("tooltip.warptheory.something"));
    }
}
