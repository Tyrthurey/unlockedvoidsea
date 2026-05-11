package com.tyrthurey.unlockedvoidsea;

import dev.simulated_team.simulated.content.end_sea.EndSeaPhysics;
import dev.simulated_team.simulated.content.end_sea.EndSeaPhysicsData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.Optional;

@Mod(UnlockedVoidSea.MODID)
public class UnlockedVoidSea {
    public static final String MODID = "unlockedvoidsea";
    public static final Logger LOGGER = LogUtils.getLogger();

    public UnlockedVoidSea(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (UnlockedVoidSea) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    @SubscribeEvent
    public void onServerStart(ServerAboutToStartEvent event) {
        injectVoidSea();
    }

    @SubscribeEvent
    public void onDatapackSync(OnDatapackSyncEvent event) {
        injectVoidSea();
    }

    private void injectVoidSea() {
        ResourceLocation dimLoc = ResourceLocation.parse(Config.DIMENSION.get());
        ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, dimLoc);

        EndSeaPhysics configPhysics = new EndSeaPhysics(
                dimLoc,
                Optional.of(Config.PRIORITY.get()),
                Config.START_Y.get(),
                Config.DEPTH_GRADIENT.get(),
                Config.DRAG.get()
        );

        // Inject data into Simulated's system
        EndSeaPhysicsData.addKeyWithPriority(dimKey, configPhysics);
        LOGGER.info("Injected Void Sea for dimension: {}", dimLoc);
    }
}
