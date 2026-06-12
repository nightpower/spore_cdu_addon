package com.Harbinger.sporeaddon.client.renderer;

import com.Harbinger.sporeaddon.ModularCDUBlockEntity;
import com.Harbinger.sporeaddon.client.model.ModularCDUModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ModularCDURenderer extends GeoBlockRenderer<ModularCDUBlockEntity> {
    public ModularCDURenderer() {
        super(new ModularCDUModel());
    }
}
