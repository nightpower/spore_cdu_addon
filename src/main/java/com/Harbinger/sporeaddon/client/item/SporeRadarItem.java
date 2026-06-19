package com.Harbinger.sporeaddon.client.item;

import com.Harbinger.sporeaddon.AddonBlocks;
import net.minecraft.world.item.BlockItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class SporeRadarItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SporeRadarItem(Properties properties) {
        super(AddonBlocks.SPORE_RADAR.get(), properties);
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
            private software.bernie.geckolib.renderer.GeoItemRenderer<SporeRadarItem> renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new software.bernie.geckolib.renderer.GeoItemRenderer<>(new com.Harbinger.sporeaddon.client.model.SporeRadarItemModel());
                }
                return this.renderer;
            }
        });
    }
}
