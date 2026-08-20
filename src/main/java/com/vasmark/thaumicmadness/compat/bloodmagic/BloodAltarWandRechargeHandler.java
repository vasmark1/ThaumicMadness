package com.vasmark.thaumicmadness.compat.bloodmagic;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

import WayofTime.alchemicalWizardry.common.tileEntity.TEAltar;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.particles.FXEssentiaTrail;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXEssentiaSource;

public class BloodAltarWandRechargeHandler {

    private int tickCounter = 0;
    private static final Aspect[] PRIMALS = new Aspect[] { Aspect.AIR, Aspect.FIRE, Aspect.WATER, Aspect.EARTH,
        Aspect.ORDER, Aspect.ENTROPY };

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.world == null || event.world.isRemote) return;

        // Process every 10 ticks (twice per second)
        tickCounter++;
        if (tickCounter % 10 != 0) return;

        WorldServer world = (WorldServer) event.world;
        List<TileEntity> tileEntities = world.loadedTileEntityList;

        for (int i = 0; i < tileEntities.size(); i++) {
            TileEntity te = tileEntities.get(i);
            if (te instanceof TEAltar && !te.isInvalid()) {
                TEAltar altar = (TEAltar) te;
                processAltarWand(world, altar);
            }
        }
    }

    private void processAltarWand(WorldServer world, TEAltar altar) {
        ItemStack stack = altar.getStackInSlot(0);
        if (stack == null || !(stack.getItem() instanceof ItemWandCasting)) return;

        ItemWandCasting wand = (ItemWandCasting) stack.getItem();
        // wand.getMaxVis(stack) returns centivis max (e.g., 5000 for 50 vis, 15000 for 150 vis, 25000 for 250 vis)
        int maxVis = wand.getMaxVis(stack);

        int currentBlood = altar.getCurrentBlood();
        if (currentBlood < 100) return; // Exactly 100 LP required for 1 vis

        // Find the FIRST aspect that needs recharge in fixed sequential order.
        // It charges this single aspect completely to maximum (100%) before advancing to the next.
        Aspect aspectToRecharge = null;
        for (Aspect asp : PRIMALS) {
            int currentVis = wand.getVis(stack, asp);
            if (currentVis < maxVis) {
                aspectToRecharge = asp;
                break;
            }
        }

        // When all 6 primal aspects are 100% full, stop completely (NO LP consumption, NO animation)
        if (aspectToRecharge == null) {
            return;
        }

        int currentVis = wand.getVis(stack, aspectToRecharge);
        int missing = maxVis - currentVis;
        // 100 centivis = 1 whole vis = 100 LP
        int visToAdd = Math.min(100, missing);
        int lpCost = visToAdd; // 1:1 in centivis to LP, meaning exactly 1 vis = 100 LP

        if (currentBlood >= lpCost && visToAdd > 0) {
            wand.addRealVis(stack, aspectToRecharge, visToAdd, true);
            altar.drain(ForgeDirection.UNKNOWN, lpCost, true);
            altar.markDirty();
            world.markBlockForUpdate(altar.xCoord, altar.yCoord, altar.zCoord);

            int color = aspectToRecharge.getColor();

            // 1. Send network packet for Thaumcraft infusion essentia stream of this specific aspect color
            try {
                PacketHandler.INSTANCE.sendToAllAround(
                    new PacketFXEssentiaSource(
                        altar.xCoord,
                        altar.yCoord + 1,
                        altar.zCoord,
                        (byte) 0,
                        (byte) -1,
                        (byte) 0,
                        color),
                    new TargetPoint(world.provider.dimensionId, altar.xCoord, altar.yCoord, altar.zCoord, 32.0));
            } catch (Throwable ignored) {}

            // 2. Spawn server-side particles and sound
            double startX = altar.xCoord + 0.5;
            double startY = altar.yCoord + 0.85;
            double startZ = altar.zCoord + 0.5;
            double endY = altar.yCoord + 1.45;

            // Spiral essentia particle trail rising from altar bowl to wand tip
            for (int step = 0; step < 8; step++) {
                double progress = (double) step / 8.0;
                double angle = progress * Math.PI * 4.0 + (world.rand.nextDouble() * 0.5);
                double radius = 0.25 * (1.0 - progress * 0.5);
                double px = startX + Math.cos(angle) * radius;
                double py = startY + (endY - startY) * progress;
                double pz = startZ + Math.sin(angle) * radius;

                world.func_147487_a("reddust", px, py, pz, 1, 0.0, 0.0, 0.0, 0.0);
            }

            // Sound feedback
            world.playSoundEffect(
                startX,
                startY,
                startZ,
                "thaumcraft:bubble",
                0.6F,
                0.9F + world.rand.nextFloat() * 0.3F);

            if (world.rand.nextInt(4) == 0) {
                world.playSoundEffect(
                    startX,
                    endY,
                    startZ,
                    "thaumcraft:wand",
                    0.5F,
                    1.2F + world.rand.nextFloat() * 0.2F);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void spawnEssentiaTrailClient(World world, double sx, double sy, double sz, double tx, double ty,
        double tz, int color) {
        if (world == null || !world.isRemote) return;
        try {
            FXEssentiaTrail trail = new FXEssentiaTrail(world, sx, sy, sz, tx, ty, tz, 12, color, 0.65F);
            Minecraft.getMinecraft().effectRenderer.addEffect(trail);
        } catch (Throwable ignored) {}
    }
}
