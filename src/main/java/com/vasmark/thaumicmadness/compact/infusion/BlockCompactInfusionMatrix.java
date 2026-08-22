package com.vasmark.thaumicmadness.compact.infusion;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.ModCreativeTabs;
import com.vasmark.thaumicmadness.ThaumicMadness;
import com.vasmark.thaumicmadness.client.gui.ModGuiHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.items.wands.ItemWandCasting;

public class BlockCompactInfusionMatrix extends BlockContainer {

    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;
    @SideOnly(Side.CLIENT)
    private IIcon iconSide;

    private final Random rand = new Random();

    public BlockCompactInfusionMatrix() {
        super(Material.glass);
        this.setHardness(15.0F);
        this.setResistance(100.0F);
        this.setStepSound(Block.soundTypeGlass);
        this.setLightLevel(0.8F);
        this.setCreativeTab(ModCreativeTabs.tabMyMod);
        this.setBlockName("thaumicmadness.compact_infusion_matrix");
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInPass(int pass) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCompactInfusionMatrix();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, net.minecraft.entity.EntityLivingBase entity,
        ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        if (!world.isRemote && entity instanceof EntityPlayer) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileCompactInfusionMatrix) {
                ((TileCompactInfusionMatrix) te).placerName = ((EntityPlayer) entity).getCommandSenderName();
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCompactInfusionMatrix) {
            ((TileCompactInfusionMatrix) te).placerName = player.getCommandSenderName();
        }

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

        player.openGui(ThaumicMadness.instance, ModGuiHandler.GUI_COMPACT_INFUSION_MATRIX, world, x, y, z);
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCompactInfusionMatrix) {
            TileCompactInfusionMatrix matrix = (TileCompactInfusionMatrix) te;
            for (int i = 0; i < matrix.getSizeInventory(); i++) {
                ItemStack itemstack = matrix.getStackInSlot(i);
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
        this.iconTop = register.registerIcon("thaumcraft:arcaneearbottom");
        this.iconBottom = register.registerIcon("thaumcraft:pedestal_top");
        this.iconSide = register.registerIcon("thaumcraft:infuser_side");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 1) return this.iconTop != null ? this.iconTop : this.blockIcon;
        if (side == 0) return this.iconBottom != null ? this.iconBottom : this.blockIcon;
        return this.iconSide != null ? this.iconSide : this.blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCompactInfusionMatrix) {
            TileCompactInfusionMatrix matrix = (TileCompactInfusionMatrix) te;
            if (matrix.state != TileCompactInfusionMatrix.STATE_IDLE) {
                double px = (double) x + 0.5D + (rand.nextDouble() - 0.5D) * 0.4D;
                double py = (double) y + 1.05D;
                double pz = (double) z + 0.5D + (rand.nextDouble() - 0.5D) * 0.4D;
                world.spawnParticle(
                    "portal",
                    px,
                    py,
                    pz,
                    (rand.nextDouble() - 0.5D) * 0.2D,
                    0.1D,
                    (rand.nextDouble() - 0.5D) * 0.2D);
            }
        }
    }
}
