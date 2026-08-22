package com.vasmark.thaumicmadness.warptheory.events;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.network.NetworkHandler;
import com.vasmark.thaumicmadness.network.PacketWarpEnderParticles;
import com.vasmark.thaumicmadness.warptheory.WarpTheoryManager;
import com.vasmark.thaumicmadness.warptheory.block.TilePhantomDecay;
import com.vasmark.thaumicmadness.warptheory.util.WarpTheoryHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;

public class WarpFall extends IWarpEvent {

    private final Map<String, ChunkCoordinates> originalPositions = new HashMap<String, ChunkCoordinates>();
    private final Map<String, Long> returnTimes = new HashMap<String, Long>();

    public WarpFall() {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public String getName() {
        return "fall";
    }

    @Override
    public int getSeverity() {
        return 36;
    }

    @Override
    public boolean canDo(EntityPlayer player) {
        return !originalPositions.containsKey(player.getCommandSenderName());
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        WarpTheoryHelper.sendChat(player, "§5§o" + StatCollector.translateToLocal("chat.warptheory.fall"));
        WarpTheoryHelper.modEventInt(player, "fall", 4);
        return true;
    }

    @SubscribeEvent
    public void onTick(WorldTickEvent e) {
        if (e.phase == Phase.END && e.side == Side.SERVER) {
            for (Object obj : e.world.playerEntities) {
                if (!(obj instanceof EntityPlayer)) continue;
                EntityPlayer player = (EntityPlayer) obj;
                String name = player.getCommandSenderName();
                NBTTagCompound tag = WarpTheoryHelper.getWarpTag(player);

                if (tag.hasKey("fall")) {
                    if (!originalPositions.containsKey(name)) {
                        int durationSeconds = tag.getInteger("fall");
                        originalPositions
                            .put(name, new ChunkCoordinates((int) player.posX, (int) player.posY, (int) player.posZ));
                        long rTime = e.world.getTotalWorldTime() + (long) (durationSeconds * 20);
                        returnTimes.put(name, rTime);
                        e.world
                            .playSoundEffect(player.posX, player.posY, player.posZ, "mob.endermen.portal", 1.0F, 1.0F);

                        int px = (int) player.posX;
                        int pz = (int) player.posZ;
                        for (int dx = px - 4; dx <= px + 4; ++dx) {
                            for (int dy = 0; dy < e.world.getHeight(); ++dy) {
                                for (int dz = pz - 4; dz <= pz + 4; ++dz) {
                                    if (!e.world.isAirBlock(dx, dy, dz)) {
                                        TilePhantomDecay vanish = new TilePhantomDecay(e.world, dx, dy, dz, rTime);
                                        if (e.world.setBlock(dx, dy, dz, WarpTheoryManager.blockPhantomDecay, 0, 0)) {
                                            e.world.setTileEntity(dx, dy, dz, vanish);
                                        }
                                        e.world.markBlockForUpdate(dx, dy, dz);
                                    }
                                }
                            }
                        }
                    } else if (e.world.getTotalWorldTime() >= returnTimes.get(name)) {
                        ChunkCoordinates pos = originalPositions.get(name);
                        double nx = pos.posX + e.world.rand.nextDouble();
                        double nz = pos.posZ + e.world.rand.nextDouble();
                        player.setPositionAndUpdate(nx, pos.posY, nz);
                        NetworkHandler.INSTANCE.sendToAllAround(
                            new PacketWarpEnderParticles(nx, pos.posY, nz),
                            new TargetPoint(e.world.provider.dimensionId, nx, pos.posY, nz, 32.0D));
                        e.world.playSoundEffect(nx, pos.posY, nz, "mob.endermen.portal", 1.0F, 1.0F);
                        tag.removeTag("fall");
                        originalPositions.remove(name);
                        returnTimes.remove(name);
                    }
                }
            }
        }
    }
}
