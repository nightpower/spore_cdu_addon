package com.Harbinger.sporeaddon.client.model;

import com.Harbinger.sporeaddon.SporeAddon;
import com.Harbinger.sporeaddon.client.item.SporeRadarItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SporeRadarItemModel extends GeoModel<SporeRadarItem> {
    @Override
    public ResourceLocation getModelResource(SporeRadarItem object) {
        return ResourceLocation.fromNamespaceAndPath(SporeAddon.MODID, "geo/radar.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SporeRadarItem object) {
        return ResourceLocation.fromNamespaceAndPath(SporeAddon.MODID, "textures/block/radar.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SporeRadarItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(SporeAddon.MODID, "geo/radar.geo.json");
    }
}
