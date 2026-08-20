package com.vasmark.thaumicmadness.compat.bloodmagic;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import WayofTime.alchemicalWizardry.api.tile.IBloodAltar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;

public class ItemFocusBloodSacrifice extends ItemFocusBasic {

    private static final AspectList COST = new AspectList().add(Aspect.ENTROPY, 10)
        .add(Aspect.WATER, 10)
        .add(Aspect.EARTH, 5);

    private static final AspectList SELF_COST = new AspectList().add(Aspect.WATER, 5)
        .add(Aspect.EARTH, 5);

    public ItemFocusBloodSacrifice() {
        super();
        this.setCreativeTab(com.vasmark.thaumicmadness.ModCreativeTabs.tabMyMod);
        this.setUnlocalizedName("focus_blood_sacrifice");
        this.setTextureName("thaumicmadness:focus_blood_sacrifice");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        this.icon = ir.registerIcon("thaumicmadness:focus_blood_sacrifice");
    }

    @Override
    public int getFocusColor(ItemStack focusstack) {
        return 0xB30000; // Bright Blood Crimson
    }

    @Override
    public AspectList getVisCost(ItemStack focusstack) {
        return COST;
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return 6;
    }

    @Override
    public ItemFocusBasic.WandFocusAnimation getAnimation(ItemStack focusstack) {
        return ItemFocusBasic.WandFocusAnimation.WAVE;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandstack, World world, EntityPlayer player,
        MovingObjectPosition mop) {
        ItemWandCasting wand = (ItemWandCasting) wandstack.getItem();
        String owner = player.getCommandSenderName();

        // 1. Sneaking: Self-Sacrifice Mode (Sacrificial Dagger equivalent)
        if (player.isSneaking()) {
            if (wand.consumeAllVis(wandstack, player, SELF_COST, true, false)) {
                if (!world.isRemote) {
                    // Deal 2.0F (1 heart) self sacrifice damage
                    player.attackEntityFrom(DamageSource.magic, 2.0F);

                    int lpAmount = 250;
                    IBloodAltar altar = findNearbyAltar(
                        world,
                        (int) Math.floor(player.posX),
                        (int) Math.floor(player.posY),
                        (int) Math.floor(player.posZ),
                        4);
                    if (altar != null) {
                        float mult = altar.getSelfSacrificeMultiplier();
                        int totalLp = (int) (lpAmount * (mult > 0 ? mult : 1.0F));
                        altar.sacrificialDaggerCall(totalLp, false);
                        altar.startCycle();
                    } else {
                        SoulNetworkHandler.addCurrentEssenceToMaximum(owner, lpAmount, 10000000);
                    }
                }

                // Visual and Sound Effects for Self Sacrifice
                if (world.isRemote) {
                    Thaumcraft.proxy.burst(world, player.posX, player.posY + 1.0, player.posZ, 1.2F);
                    for (int i = 0; i < 8; i++) {
                        double ox = (world.rand.nextDouble() - 0.5) * 0.8;
                        double oy = world.rand.nextDouble() * 1.5;
                        double oz = (world.rand.nextDouble() - 0.5) * 0.8;
                        Thaumcraft.proxy.wispFX2(
                            world,
                            player.posX + ox,
                            player.posY + oy,
                            player.posZ + oz,
                            0.4F,
                            6,
                            true,
                            true,
                            -0.02F);
                    }
                } else if (world instanceof WorldServer) {
                    ((WorldServer) world)
                        .func_147487_a("reddust", player.posX, player.posY + 1.0, player.posZ, 12, 0.3, 0.5, 0.3, 0.0);
                }

                world.playSoundAtEntity(player, "game.neutral.hurt", 0.6F, 1.2F);
                world.playSoundAtEntity(player, "thaumcraft:bubble", 0.8F, 0.9F);
                player.swingItem();
            }
            return wandstack;
        }

        // 2. Normal Mode: Blood Harvest Beam
        if (wand.consumeAllVis(wandstack, player, getVisCost(wandstack), true, false)) {
            Vec3 look = player.getLookVec();
            double reach = 22.0;
            Vec3 start = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight() - 0.1, player.posZ);
            Vec3 end = start.addVector(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach);

            // Check mob hit
            List<EntityLivingBase> list = world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                AxisAlignedBB.getBoundingBox(
                    Math.min(start.xCoord, end.xCoord) - 1.5,
                    Math.min(start.yCoord, end.yCoord) - 1.5,
                    Math.min(start.zCoord, end.zCoord) - 1.5,
                    Math.max(start.xCoord, end.xCoord) + 1.5,
                    Math.max(start.yCoord, end.yCoord) + 1.5,
                    Math.max(start.zCoord, end.zCoord) + 1.5));

            EntityLivingBase target = null;
            double closest = reach * reach;

            for (EntityLivingBase entity : list) {
                if (entity != player && entity.isEntityAlive()) {
                    AxisAlignedBB aabb = entity.boundingBox.expand(0.3, 0.3, 0.3);
                    MovingObjectPosition intercept = aabb.calculateIntercept(start, end);
                    if (intercept != null) {
                        double dist = start.squareDistanceTo(intercept.hitVec);
                        if (dist < closest) {
                            closest = dist;
                            target = entity;
                            end = intercept.hitVec;
                        }
                    }
                }
            }

            // Client Beam & Sparkle Rendering
            if (world.isRemote) {
                Thaumcraft.proxy.beam(
                    world,
                    start.xCoord,
                    start.yCoord,
                    start.zCoord,
                    end.xCoord,
                    end.yCoord,
                    end.zCoord,
                    1,
                    0xCC0000,
                    false,
                    2.5F,
                    8);
                Thaumcraft.proxy.wispFX2(world, end.xCoord, end.yCoord, end.zCoord, 0.8F, 6, true, true, 0.0F);
            } else if (world instanceof WorldServer) {
                WorldServer ws = (WorldServer) world;
                // Spawn red trail along the beam
                int steps = (int) Math.max(4, Math.sqrt(closest) * 2);
                for (int s = 0; s <= steps; s++) {
                    double t = (double) s / steps;
                    double px = start.xCoord + (end.xCoord - start.xCoord) * t;
                    double py = start.yCoord + (end.yCoord - start.yCoord) * t;
                    double pz = start.zCoord + (end.zCoord - start.zCoord) * t;
                    ws.func_147487_a("reddust", px, py, pz, 1, 0.02, 0.02, 0.02, 0.0);
                }
            }

            // Target hit handling
            if (target != null) {
                if (!world.isRemote) {
                    boolean wasAlive = target.isEntityAlive();
                    target.attackEntityFrom(DamageSource.causePlayerDamage(player), 9.0F);

                    int lpGained = (!target.isEntityAlive() && wasAlive) ? 600 : 180;
                    IBloodAltar altar = findNearbyAltar(
                        world,
                        (int) Math.floor(player.posX),
                        (int) Math.floor(player.posY),
                        (int) Math.floor(player.posZ),
                        5);
                    if (altar != null) {
                        float mult = altar.getSacrificeMultiplier();
                        int totalLp = (int) (lpGained * (mult > 0 ? mult : 1.0F));
                        altar.sacrificialDaggerCall(totalLp, true);
                        altar.startCycle();
                    } else {
                        SoulNetworkHandler.addCurrentEssenceToMaximum(owner, lpGained, 10000000);
                    }
                }

                if (world.isRemote) {
                    Thaumcraft.proxy.burst(world, target.posX, target.posY + target.height / 2.0, target.posZ, 1.8F);
                }
                world.playSoundAtEntity(target, "thaumcraft:zap", 0.7F, 0.8F);
                world.playSoundAtEntity(player, "alchemicalwizardry:bloodAltar", 0.9F, 1.1F);
            } else {
                world.playSoundAtEntity(player, "thaumcraft:zap", 0.5F, 1.4F);
            }

            player.swingItem();
        }

        return wandstack;
    }

    private IBloodAltar findNearbyAltar(World world, int x, int y, int z, int range) {
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    TileEntity te = world.getTileEntity(x + dx, y + dy, z + dz);
                    if (te instanceof IBloodAltar) {
                        return (IBloodAltar) te;
                    }
                }
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);
        list.add(EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("item.focus_blood_sacrifice.desc"));
    }
}
