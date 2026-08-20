package com.vasmark.thaumicmadness;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.vasmark.thaumicmadness.item.ModItems;

public class ModCreativeTabs {

    public static final CreativeTabs tabMyMod = new CreativeTabs("thaumicmadness") {

        @Override
        public Item getTabIconItem() {
            return ModItems.itemThaumonomiconAtlas != null ? ModItems.itemThaumonomiconAtlas
                : (ModBlocks.infusedDirt != null ? Item.getItemFromBlock(ModBlocks.infusedDirt) : null);
        }

        @Override
        public ItemStack getIconItemStack() {
            if (ModItems.itemThaumonomiconAtlas != null) {
                return new ItemStack(ModItems.itemThaumonomiconAtlas);
            }
            return super.getIconItemStack();
        }
    };
}
