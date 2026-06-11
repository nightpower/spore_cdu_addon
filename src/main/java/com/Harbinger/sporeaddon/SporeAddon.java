package com.Harbinger.sporeaddon;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(SporeAddon.MODID)
public class SporeAddon {
    public static final String MODID = "sporeaddon";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SporeAddon(IEventBus modEventBus, ModContainer modContainer) {
        AddonBlocks.BLOCKS.register(modEventBus);
        AddonItems.ITEMS.register(modEventBus);
        AddonBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        AddonMenus.MENUS.register(modEventBus);
        AddonCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                AddonBlockEntities.MODULAR_CDU_BE.get(),
                (be, side) -> be.getEnergyStorage()
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                AddonBlockEntities.MODULAR_CDU_BE.get(),
                (be, side) -> be.getFluidHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                AddonBlockEntities.MODULAR_CDU_BE.get(),
                (be, side) -> be.getItemHandler()
        );
    }
}
