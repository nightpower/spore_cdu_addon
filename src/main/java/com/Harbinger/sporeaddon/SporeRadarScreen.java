package com.Harbinger.sporeaddon;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SporeRadarScreen extends AbstractContainerScreen<SporeRadarMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SporeAddon.MODID, "textures/gui/cdu_radar_gui.png");

    public SporeRadarScreen(SporeRadarMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, Component.translatable("block.sporeaddon.spore_radar"));
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = 6;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        
        guiGraphics.drawString(this.font, Component.translatable("gui.sporeaddon.radar.mobs", this.menu.getDetectedMobs()), 8, 30, 4210752, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.sporeaddon.radar.range", this.menu.getRadarRange()), 8, 45, 4210752, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
