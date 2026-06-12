package com.Harbinger.sporeaddon.client.model;

import com.Harbinger.sporeaddon.ModularCDUBlockEntity;
import com.Harbinger.sporeaddon.SporeAddon;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ModularCDUModel extends GeoModel<ModularCDUBlockEntity> {
    @Override
    public ResourceLocation getModelResource(ModularCDUBlockEntity object) {
        return ResourceLocation.fromNamespaceAndPath(SporeAddon.MODID, "geo/block/cdu.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ModularCDUBlockEntity object) {
        return ResourceLocation.fromNamespaceAndPath(SporeAddon.MODID, "textures/block/cdu.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ModularCDUBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SporeAddon.MODID, "animations/block/cdu.animation.json");
    }
}
