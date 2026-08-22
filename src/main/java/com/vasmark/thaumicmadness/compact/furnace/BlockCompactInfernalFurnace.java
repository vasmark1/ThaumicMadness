package com.vasmark.thaumicmadness.compact.furnace;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.ModCreativeTabs;
import com.vasmark.thaumicmadness.ThaumicMadness;
import com.vasmark.thaumicmadness.client.gui.ModGuiHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.items.wands.ItemWandCasting;

public class BlockCompactInfernalFurnace extends BlockContainer {

    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;
    @SideOnly(Side.CLIENT)
    private IIcon iconSide;
    @SideOnly(Side.CLIENT)
    private IIcon iconFrontOn;
    @SideOnly(Side.CLIENT)
    private IIcon iconFrontOff;

    private final Random rand = new Random();

    public BlockCompactInfernalFurnace() {
        super(Material.rock);
        this.setHardness(15.0F);
        this.setResistance(100.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setLightLevel(0.85F);
        this.setCreativeTab(ModCreativeTabs.tabMyMod);
        this.setBlockName("thaumicmadness.compact_infernal_furnace");
    }

    @Override
    public int getRenderType() {
        return 0; // Standard block renderer for perfect texture mapping, ambient occlusion & animations
    }

    @Override
    public boolean isOpaqueCube() {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCompactInfernalFurnace();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        int l = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (l == 0) world.setBlockMetadataWithNotify(x, y, z, 2, 2); // North
        if (l == 1) world.setBlockMetadataWithNotify(x, y, z, 5, 2); // East
        if (l == 2) world.setBlockMetadataWithNotify(x, y, z, 3, 2); // South
        if (l == 3) world.setBlockMetadataWithNotify(x, y, z, 4, 2); // West
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        TileEntity te = world.getTileEntity(x, y, z);
        ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() instanceof ItemWandCasting) {
            if (te instanceof IWandable) {
                IWandable wandable = (IWandable) te;
                int res = wandable.onWandRightClick(world, held, player, x, y, z, side, 0);
                if (res > 0) {
                    return true;
                }
            }
        }

        player.openGui(ThaumicMadness.instance, ModGuiHandler.GUI_COMPACT_INFERNAL_FURNACE, world, x, y, z);
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCompactInfernalFurnace) {
            TileCompactInfernalFurnace furnace = (TileCompactInfernalFurnace) te;
            for (int i = 0; i < furnace.getSizeInventory(); i++) {
                ItemStack itemstack = furnace.getStackInSlot(i);
                if (itemstack != null) {
                    float f = this.rand.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
                    float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.stackSize > 0) {
                        int j = this.rand.nextInt(21) + 10;
                        if (j > itemstack.stackSize) {
                            j = itemstack.stackSize;
                        }

                        itemstack.stackSize -= j;
                        EntityItem entityitem = new EntityItem(
                            world,
                            (double) ((float) x + f),
                            (double) ((float) y + f1),
                            (double) ((float) z + f2),
                            new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));

                        if (itemstack.hasTagCompound()) {
                            entityitem.getEntityItem()
                                .setTagCompound(
                                    (NBTTagCompound) itemstack.getTagCompound()
                                        .copy());
                        }

                        float f3 = 0.05F;
                        entityitem.motionX = (double) ((float) this.rand.nextGaussian() * f3);
                        entityitem.motionY = (double) ((float) this.rand.nextGaussian() * f3 + 0.2F);
                        entityitem.motionZ = (double) ((float) this.rand.nextGaussian() * f3);
                        world.spawnEntityInWorld(entityitem);
                    }
                }
            }
            world.func_147453_f(x, y, z, block);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.iconTop = register.registerIcon("thaumicmadness:compact_furnace_top");
        this.iconBottom = register.registerIcon("thaumicmadness:compact_furnace_bottom");
        this.iconSide = register.registerIcon("thaumicmadness:compact_furnace_side");
        this.iconFrontOn = register.registerIcon("thaumicmadness:compact_furnace_front_on");
        this.iconFrontOff = register.registerIcon("thaumicmadness:compact_furnace_front_off");
        this.blockIcon = this.iconSide;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 1) return this.iconTop != null ? this.iconTop : this.blockIcon;
        if (side == 0) return this.iconBottom != null ? this.iconBottom : this.blockIcon;
        // In inventory meta is 3
        if (side == 3 || (meta != 0 && side == meta)) {
            return this.iconFrontOn != null ? this.iconFrontOn : this.iconSide;
        }
        return this.iconSide != null ? this.iconSide : this.blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        if (side == 1) return this.iconTop;
        if (side == 0) return this.iconBottom;

        TileEntity te = world.getTileEntity(x, y, z);
        boolean burning = true;
        if (te instanceof TileCompactInfernalFurnace) {
            burning = ((TileCompactInfernalFurnace) te).isBurning();
        }

        if (side == meta) {
            return burning ? this.iconFrontOn : this.iconFrontOff;
        }
        return this.iconSide;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCompactInfernalFurnace) {
            TileCompactInfernalFurnace furnace = (TileCompactInfernalFurnace) te;
            if (furnace.isBurning()) {
                double px = (double) x + 0.5D + (rand.nextDouble() - 0.5D) * 0.4D;
                double py = (double) y + 0.5D + (rand.nextDouble() - 0.5D) * 0.3D;
                double pz = (double) z + 0.5D + (rand.nextDouble() - 0.5D) * 0.4D;
                world.spawnParticle("flame", px, py, pz, 0.0D, 0.02D, 0.0D);
                world.spawnParticle("lava", px, py + 0.2D, pz, 0.0D, 0.0D, 0.0D);
                if (rand.nextInt(10) == 0) {
                    world.playSound(
                        (double) x + 0.5D,
                        (double) y + 0.5D,
                        (double) z + 0.5D,
                        "liquid.lavapop",
                        0.2F + rand.nextFloat() * 0.2F,
                        0.9F + rand.nextFloat() * 0.15F,
                        false);
                }
            }
        }
    }
}
