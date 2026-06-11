package com.Harbinger.sporeaddon;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AddonItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, SporeAddon.MODID);

    public static final Supplier<Item> MODULAR_CDU = ITEMS.register("modular_cdu",
            () -> new BlockItem(AddonBlocks.MODULAR_CDU.get(), new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltipComponents, net.minecraft.world.item.TooltipFlag tooltipFlag) {
                    tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.modular_cdu.desc1").withStyle(net.minecraft.ChatFormatting.GRAY));
                    tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.modular_cdu.desc2").withStyle(net.minecraft.ChatFormatting.GRAY));
                }
            });

    public static final Supplier<Item> RADIUS_MODIFIER = ITEMS.register("radius_modifier",
            () -> new Item(new Item.Properties().stacksTo(64)) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltipComponents, net.minecraft.world.item.TooltipFlag tooltipFlag) {
                    tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.radius_modifier.desc").withStyle(net.minecraft.ChatFormatting.GREEN));
                    tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.module.cost").withStyle(net.minecraft.ChatFormatting.RED));
                }
            });

    public static final Supplier<Item> SPEED_MODIFIER = ITEMS.register("speed_modifier",
            () -> new Item(new Item.Properties().stacksTo(64)) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltipComponents, net.minecraft.world.item.TooltipFlag tooltipFlag) {
                    tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.speed_modifier.desc").withStyle(net.minecraft.ChatFormatting.YELLOW));
                    tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.module.cost").withStyle(net.minecraft.ChatFormatting.RED));
                }
            });

    public static final Supplier<Item> EFFICIENCY_MODIFIER = ITEMS.register("efficiency_modifier",
            () -> new Item(new Item.Properties().stacksTo(64)) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltipComponents, net.minecraft.world.item.TooltipFlag tooltipFlag) {
                    tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.efficiency_modifier.desc").withStyle(net.minecraft.ChatFormatting.AQUA));
                }
            });

    public static final Supplier<Item> FIRE_DAMAGE_MODIFIER = ITEMS.register("fire_damage_modifier",
            () -> new Item(new Item.Properties().stacksTo(64)) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltipComponents, net.minecraft.world.item.TooltipFlag tooltipFlag) {
                    tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.fire_damage_modifier.desc").withStyle(net.minecraft.ChatFormatting.GOLD));
                    tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.module.cost").withStyle(net.minecraft.ChatFormatting.RED));
                }
            });

    public static final Supplier<Item> SUFFOCATION_DAMAGE_MODIFIER = ITEMS.register("suffocation_damage_modifier",
            () -> new Item(new Item.Properties().stacksTo(64)) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltipComponents, net.minecraft.world.item.TooltipFlag tooltipFlag) {
                    tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.suffocation_damage_modifier.desc").withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
                    tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.module.cost").withStyle(net.minecraft.ChatFormatting.RED));
                }
            });

    public static final Supplier<Item> FROST_DAMAGE_MODIFIER = ITEMS.register("frost_damage_modifier",
            () -> new Item(new Item.Properties().stacksTo(64)) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltipComponents, net.minecraft.world.item.TooltipFlag tooltipFlag) {
                    tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.frost_damage_modifier.desc").withStyle(net.minecraft.ChatFormatting.BLUE));
                    tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.module.cost").withStyle(net.minecraft.ChatFormatting.RED));
                }
            });

    public static final Supplier<Item> DESTRUCTION_DAMAGE_MODIFIER = ITEMS.register("destruction_damage_modifier",
            () -> new Item(new Item.Properties().stacksTo(64)) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltipComponents, net.minecraft.world.item.TooltipFlag tooltipFlag) {
                    tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.destruction_damage_modifier.desc").withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
                    tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.module.cost").withStyle(net.minecraft.ChatFormatting.RED));
                }
            });

    public static final Supplier<Item> REINFORCED_COMPOUND_PLATE = ITEMS.register("reinforced_compound_plate",
            () -> new Item(new Item.Properties().stacksTo(64)));
}
