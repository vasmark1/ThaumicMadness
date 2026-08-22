package com.vasmark.thaumicmadness.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class NetworkHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("thaumicmadness");
    private static int packetId = 0;

    public static void init() {
        INSTANCE.registerMessage(PacketWarpControl.Handler.class, PacketWarpControl.class, packetId++, Side.SERVER);
        INSTANCE.registerMessage(PacketWarpVelocity.Handler.class, PacketWarpVelocity.class, packetId++, Side.CLIENT);
        INSTANCE.registerMessage(PacketWarpBloodDrip.Handler.class, PacketWarpBloodDrip.class, packetId++, Side.CLIENT);
        INSTANCE.registerMessage(
            PacketWarpEnderParticles.Handler.class,
            PacketWarpEnderParticles.class,
            packetId++,
            Side.CLIENT);
        INSTANCE.registerMessage(
            PacketCompactInfusionAction.Handler.class,
            PacketCompactInfusionAction.class,
            packetId++,
            Side.SERVER);
        INSTANCE.registerMessage(
            PacketCompactEssentiaTrail.Handler.class,
            PacketCompactEssentiaTrail.class,
            packetId++,
            Side.CLIENT);
        INSTANCE.registerMessage(
            PacketCompactFurnaceAction.Handler.class,
            PacketCompactFurnaceAction.class,
            packetId++,
            Side.SERVER);
    }
}
