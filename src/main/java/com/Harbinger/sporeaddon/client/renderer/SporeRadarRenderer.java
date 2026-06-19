package com.Harbinger.sporeaddon.client.renderer;

import com.Harbinger.sporeaddon.SporeRadarBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SporeRadarRenderer extends GeoBlockRenderer<SporeRadarBlockEntity> {
    public SporeRadarRenderer(BlockEntityRendererProvider.Context context) {
        super(new SporeRadarModel());
    }
}
