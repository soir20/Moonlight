package net.mehvahdjukaar.moonlight.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidRegistry;
import net.mehvahdjukaar.moonlight.api.fluids.fabric.SoftFluidRegistryImpl;
import net.mehvahdjukaar.moonlight.api.item.additional_placements.AdditionalItemPlacement;
import net.mehvahdjukaar.moonlight.api.item.additional_placements.AdditionalItemPlacementsAPI;
import net.mehvahdjukaar.moonlight.api.map.MapDecorationRegistry;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.configs.fabric.FabricConfigSpec;
import net.mehvahdjukaar.moonlight.api.platform.fabric.RegHelperImpl;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkDir;
import net.mehvahdjukaar.moonlight.api.platform.setup.fabric.SetupHelperImpl;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.MoonlightClient;
import net.mehvahdjukaar.moonlight.core.network.ClientBoundSendLoginPacket;
import net.mehvahdjukaar.moonlight.core.network.ModMessages;
import net.mehvahdjukaar.moonlight.core.network.fabric.ClientBoundOpenScreenMessage;
import net.minecraft.server.MinecraftServer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MoonlightFabric implements ModInitializer, DedicatedServerModInitializer {

    private static boolean isInit = true;
    private static MinecraftServer currentServer;

    @Override
    public void onInitialize() {

        DynamicRegistries.registerSynced(SoftFluidRegistry.KEY, SoftFluid.CODEC,SoftFluid.CODEC, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
        DynamicRegistries.registerSynced(MapDecorationRegistry.KEY, MapDecorationRegistry.CODEC, MapDecorationRegistry.NETWORK_CODEC, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);

        Moonlight.commonInit();
        //client init
        if (PlatHelper.getPhysicalSide().isClient()) {
             MoonlightClient.initClient();
        }

        ModMessages.CHANNEL.register(NetworkDir.PLAY_TO_CLIENT,
                ClientBoundOpenScreenMessage.class, ClientBoundOpenScreenMessage::new);

        ServerPlayConnectionEvents.JOIN.register((l, s, m) -> ModMessages.CHANNEL.sendToClientPlayer(l.player,
                new ClientBoundSendLoginPacket()));
        ServerLifecycleEvents.SERVER_STARTING.register(s -> currentServer = s);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(SoftFluidRegistry::onDataSyncToPlayer);
        ServerLifecycleEvents.SERVER_STARTED.register((s) -> SoftFluidRegistry.onDataLoad()); //need this too because fabric is stupid
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((a, b, c) -> SoftFluidRegistry.onDataLoad()); //only fire after reload command
        ServerPlayerEvents.COPY_FROM.register(Moonlight::onPlayerCloned);

        ResourceConditionsBridge.init();
    }

    //called after all other mod initialize have been called.
    // we can register extra stuff here that depends on those before client and server common setup is fired
    static void commonSetup() {


        RegHelperImpl.lateRegisterEntries();
        FabricConfigSpec.loadAllConfigs();
        MLFabricSetupCallbacks.COMMON_SETUP.forEach(Runnable::run);
        MLFabricSetupCallbacks.COMMON_SETUP.clear();

        isInit = false;

        PRE_SETUP_WORK.forEach(Runnable::run);
        COMMON_SETUP_WORK.forEach(Runnable::run);
        AFTER_SETUP_WORK.forEach(Runnable::run);
        PRE_SETUP_WORK.clear();
        COMMON_SETUP_WORK.clear();
        AFTER_SETUP_WORK.clear();

        SetupHelperImpl.run();
    }

    @Override
    public void onInitializeServer() {
        commonSetup();
    }


    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }

    public static boolean isInitializing() {
        return isInit;
    }

    public static final Queue<Runnable> COMMON_SETUP_WORK = new ConcurrentLinkedQueue<>();
    public static final Queue<Runnable> PRE_SETUP_WORK = new ConcurrentLinkedQueue <>();
    public static final Queue<Runnable> AFTER_SETUP_WORK = new ConcurrentLinkedQueue <>();


}
