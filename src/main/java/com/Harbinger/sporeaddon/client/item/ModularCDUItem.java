package com.Harbinger.sporeaddon.client.item;

import com.Harbinger.sporeaddon.AddonBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class ModularCDUItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ModularCDUItem(Properties properties) {
        super(AddonBlocks.MODULAR_CDU.get(), properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.sporeaddon.modular_cdu.desc1").withStyle(net.minecraft.ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.sporeaddon.modular_cdu.desc2").withStyle(net.minecraft.ChatFormatting.GRAY));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createGeoRenderer(Consumer<software.bernie.geckolib.animatable.client.GeoRenderProvider> consumer) {
        consumer.accept(new software.bernie.geckolib.animatable.client.GeoRenderProvider() {
            private software.bernie.geckolib.renderer.GeoItemRenderer<ModularCDUItem> renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new software.bernie.geckolib.renderer.GeoItemRenderer<>(new com.Harbinger.sporeaddon.client.model.ModularCDUItemModel());
                }
                return this.renderer;
            }
        });
    }
}
