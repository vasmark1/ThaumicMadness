package com.vasmark.thaumicmadness.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class NetworkHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("thaumicmadness");
    private static int packetId = 0;

    public static void init() {
        INSTANCE.registerMessage(PacketWarpControl.Handler.class, PacketWarpControl.class, packetId++, Side.SERVER);
    }
}
