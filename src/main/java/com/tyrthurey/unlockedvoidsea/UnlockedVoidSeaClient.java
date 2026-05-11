package com.tyrthurey.unlockedvoidsea;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = UnlockedVoidSea.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = UnlockedVoidSea.MODID, value = Dist.CLIENT)
public class UnlockedVoidSeaClient {
    public UnlockedVoidSeaClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // We use Catnip's BaseConfigScreen for a Create-style configuration menu.
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (modContainer, lastScreen) -> new BaseConfigScreen(lastScreen, UnlockedVoidSea.MODID)
        );
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        UnlockedVoidSea.LOGGER.info("HELLO FROM CLIENT SETUP");
        UnlockedVoidSea.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
