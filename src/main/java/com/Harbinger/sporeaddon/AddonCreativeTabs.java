package com.Harbinger.sporeaddon;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AddonCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SporeAddon.MODID);

    public static final Supplier<CreativeModeTab> SPORE_ADDON_TAB = CREATIVE_MODE_TABS.register("sporeaddon_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.sporeaddon_tab"))
                    .icon(() -> new ItemStack(AddonItems.MODULAR_CDU.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(AddonItems.MODULAR_CDU.get());
                        output.accept(AddonItems.REINFORCED_COMPOUND_PLATE.get());
                        output.accept(AddonItems.RADIUS_MODIFIER.get());
                        output.accept(AddonItems.SPEED_MODIFIER.get());
                        output.accept(AddonItems.EFFICIENCY_MODIFIER.get());
                        output.accept(AddonItems.FIRE_DAMAGE_MODIFIER.get());
                        output.accept(AddonItems.SUFFOCATION_DAMAGE_MODIFIER.get());
                        output.accept(AddonItems.FROST_DAMAGE_MODIFIER.get());
                        output.accept(AddonItems.DESTRUCTION_DAMAGE_MODIFIER.get());
                        output.accept(AddonItems.IMMUNITY_MODIFIER.get());
                    }).build());
}
