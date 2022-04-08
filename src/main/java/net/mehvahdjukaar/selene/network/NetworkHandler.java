package net.mehvahdjukaar.selene.network;

import net.mehvahdjukaar.selene.Selene;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;


public class NetworkHandler {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;
    private static final String PROTOCOL_VERSION = "1";

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Selene.MOD_ID, "network"), () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

        INSTANCE.registerMessage(nextID(), ClientBoundSyncCustomMapDecorationPacket.class, ClientBoundSyncCustomMapDecorationPacket::buffer,
                ClientBoundSyncCustomMapDecorationPacket::new, ClientBoundSyncCustomMapDecorationPacket::handler);

        INSTANCE.registerMessage(nextID(), ClientBoundSyncFluidsPacket.class, ClientBoundSyncFluidsPacket::buffer,
                ClientBoundSyncFluidsPacket::new, ClientBoundSyncFluidsPacket::handler);
    }
}