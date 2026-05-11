package com.tyrthurey.unlockedvoidsea;

import dev.simulated_team.simulated.content.end_sea.EndSeaPhysics;
import dev.simulated_team.simulated.content.end_sea.EndSeaPhysicsData;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.api.physics.object.ArbitraryPhysicsObject;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

@Mod(UnlockedVoidSea.MODID)
public class UnlockedVoidSea {
    public static final String MODID = "unlockedvoidsea";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static Config CONFIG;

    public UnlockedVoidSea(IEventBus modEventBus, ModContainer modContainer) {
        // Build the spec using the ConfigBase logic
        Pair<Config, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(builder -> {
            Config config = new Config();
            config.registerAll(builder);
            return config;
        });

        CONFIG = specPair.getLeft();
        CONFIG.specification = specPair.getRight();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onConfigEvent);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (UnlockedVoidSea) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.SERVER, CONFIG.specification);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void onConfigEvent(ModConfigEvent event) {
        if (event.getConfig().getModId().equals(MODID)) {
            injectVoidSea();
        }
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
        ResourceLocation dimLoc = ResourceLocation.parse(CONFIG.DIMENSION.get());
        ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, dimLoc);

        EndSeaPhysics configPhysics = new EndSeaPhysics(
                dimLoc,
                Optional.of(CONFIG.PRIORITY.get()),
                (double) CONFIG.START_Y.getF(),
                (double) CONFIG.DEPTH_GRADIENT.getF(),
                (double) CONFIG.DRAG.getF()
        );

        // Force update the physics map via reflection to bypass Simulated's priority check
        try {
            Field field = EndSeaPhysicsData.class.getDeclaredField("END_SEA_PHYSICS_DATA");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<ResourceKey<Level>, EndSeaPhysics> map = (Map<ResourceKey<Level>, EndSeaPhysics>) field.get(null);
            if (map != null) {
                synchronized (map) {
                    map.put(dimKey, configPhysics);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to force update EndSeaPhysicsData via reflection", e);
            // Fallback to standard method
            EndSeaPhysicsData.addKeyWithPriority(dimKey, configPhysics);
        }

        // Sync to clients if we are on a server
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            EndSeaPhysicsData.syncDataPacket(VeilPacketManager.all(server));

            // Wake up Sable contraptions to react to the new sea level
            ServerLevel level = server.getLevel(dimKey);
            if (level != null) {
                try {
                    ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
                    if (container != null) {
                        SubLevelPhysicsSystem physics = container.physicsSystem();
                        if (physics != null) {
                            PhysicsPipeline pipeline = physics.getPipeline();
                            if (pipeline != null) {
                                for (ServerSubLevel subLevel : container.getAllSubLevels()) {
                                    pipeline.wakeUp(subLevel);
                                }
                            }
                            for (ArbitraryPhysicsObject object : physics.getArbitraryObjects()) {
                                object.wakeUp();
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to wake up Sable contraptions", e);
                }
            }
        }

        LOGGER.info("Injected Void Sea for dimension: {}", dimLoc);
    }
}
