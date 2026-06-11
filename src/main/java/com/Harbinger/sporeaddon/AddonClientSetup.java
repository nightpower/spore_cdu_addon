package com.Harbinger.sporeaddon;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = SporeAddon.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AddonClientSetup {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(AddonMenus.MODULAR_CDU_MENU.get(), ModularCDUScreen::new);
    }
}
