package com.Harbinger.sporeaddon.client.renderer;

import com.Harbinger.sporeaddon.SporeAddon;
import com.Harbinger.sporeaddon.SporeRadarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SporeRadarModel extends GeoModel<SporeRadarBlockEntity> {
    @Override
    public ResourceLocation getModelResource(SporeRadarBlockEntity object) {
        return ResourceLocation.fromNamespaceAndPath(SporeAddon.MODID, "geo/radar.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SporeRadarBlockEntity object) {
        return ResourceLocation.fromNamespaceAndPath(SporeAddon.MODID, "textures/block/radar.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SporeRadarBlockEntity animatable) {
        // Return animation json if you have one, else same geo file or dummy
        return ResourceLocation.fromNamespaceAndPath(SporeAddon.MODID, "geo/radar.geo.json");
    }
}
