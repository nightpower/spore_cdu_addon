package com.Harbinger.sporeaddon;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AddonMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, SporeAddon.MODID);

    public static final Supplier<MenuType<ModularCDUMenu>> MODULAR_CDU_MENU = MENUS.register("modular_cdu",
            () -> IMenuTypeExtension.create(ModularCDUMenu::new));

    public static final Supplier<MenuType<SporeRadarMenu>> SPORE_RADAR_MENU = MENUS.register("spore_radar",
            () -> IMenuTypeExtension.create(SporeRadarMenu::new));
}
