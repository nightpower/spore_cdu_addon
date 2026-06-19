package com.Harbinger.sporeaddon.client.model;

import com.Harbinger.sporeaddon.BlasterCleanerItem;
import com.Harbinger.sporeaddon.SporeAddon;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlasterCleanerModel extends GeoModel<BlasterCleanerItem> {
    @Override
    public ResourceLocation getModelResource(BlasterCleanerItem object) {
        return ResourceLocation.fromNamespaceAndPath(SporeAddon.MODID, "geo/item/blaster_cleaner.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlasterCleanerItem object) {
        return ResourceLocation.fromNamespaceAndPath(SporeAddon.MODID, "textures/item/blaster_cleaner.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlasterCleanerItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(SporeAddon.MODID, "animations/item/blaster_cleaner.animation.json");
    }
}
