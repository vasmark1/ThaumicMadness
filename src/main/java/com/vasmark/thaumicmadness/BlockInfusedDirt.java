package com.vasmark.thaumicmadness;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockInfusedDirt extends Block {

    public BlockInfusedDirt() {
        super(Material.ground);
        setHardness(0.6F);
        setResistance(1.0F);
        setStepSound(soundTypeGravel);
        setBlockName("thaumicmadness.infused_dirt");
        this.setBlockTextureName("thaumicmadness:infused_dirt");
        setCreativeTab(ModCreativeTabs.tabMyMod);
    }
}
