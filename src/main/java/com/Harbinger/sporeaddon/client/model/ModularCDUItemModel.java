package com.Harbinger.sporeaddon.client.model;

import com.Harbinger.sporeaddon.SporeAddon;
import com.Harbinger.sporeaddon.client.item.ModularCDUItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ModularCDUItemModel extends GeoModel<ModularCDUItem> {
    @Override
    public ResourceLocation getModelResource(ModularCDUItem object) {
        return ResourceLocation.fromNamespaceAndPath(SporeAddon.MODID, "geo/block/cdu.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ModularCDUItem object) {
        return ResourceLocation.fromNamespaceAndPath(SporeAddon.MODID, "textures/block/cdu.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ModularCDUItem animatable) {
        return null;
    }
}
