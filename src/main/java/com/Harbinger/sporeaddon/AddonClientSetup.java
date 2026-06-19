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
        event.register(AddonMenus.SPORE_RADAR_MENU.get(), SporeRadarScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(AddonBlockEntities.MODULAR_CDU_BE.get(), context -> new com.Harbinger.sporeaddon.client.renderer.ModularCDURenderer());
        event.registerBlockEntityRenderer(AddonBlockEntities.SPORE_RADAR_BE.get(), com.Harbinger.sporeaddon.client.renderer.SporeRadarRenderer::new);
        event.registerEntityRenderer(AddonEntities.CDU_DECOY.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
    }
}
