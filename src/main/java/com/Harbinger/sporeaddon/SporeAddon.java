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
        AddonEntities.ENTITIES.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerAttributes);
    }

    private void registerAttributes(net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(AddonEntities.CDU_DECOY.get(), CDUDecoyEntity.createAttributes().build());
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                AddonBlockEntities.MODULAR_CDU_BE.get(),
                (be, side) -> be.getEnergyStorage()
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                AddonBlockEntities.SPORE_RADAR_BE.get(),
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
        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, ctx) -> new net.neoforged.neoforge.energy.IEnergyStorage() {
                    @Override
                    public int receiveEnergy(int maxReceive, boolean simulate) {
                        int currentEnergy = BlasterCleanerItem.getEnergy(stack);
                        int energyReceived = Math.min(BlasterCleanerItem.MAX_ENERGY - currentEnergy, maxReceive);
                        if (!simulate) {
                            BlasterCleanerItem.setEnergy(stack, currentEnergy + energyReceived);
                        }
                        return energyReceived;
                    }
                    @Override
                    public int extractEnergy(int maxExtract, boolean simulate) {
                        int currentEnergy = BlasterCleanerItem.getEnergy(stack);
                        int energyExtracted = Math.min(currentEnergy, maxExtract);
                        if (!simulate) {
                            BlasterCleanerItem.setEnergy(stack, currentEnergy - energyExtracted);
                        }
                        return energyExtracted;
                    }
                    @Override
                    public int getEnergyStored() { return BlasterCleanerItem.getEnergy(stack); }
                    @Override
                    public int getMaxEnergyStored() { return BlasterCleanerItem.MAX_ENERGY; }
                    @Override
                    public boolean canExtract() { return true; }
                    @Override
                    public boolean canReceive() { return true; }
                },
                AddonItems.BLASTER_CLEANER.get()
        );
    }
}
