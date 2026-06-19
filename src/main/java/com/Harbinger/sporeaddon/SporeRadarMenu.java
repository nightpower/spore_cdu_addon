package com.Harbinger.sporeaddon;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public class SporeRadarMenu extends AbstractContainerMenu {
    private final ContainerData data;

    public SporeRadarMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, new SimpleContainerData(2));
    }

    public SporeRadarMenu(int containerId, Inventory playerInventory, ContainerData data) {
        super(AddonMenus.SPORE_RADAR_MENU.get(), containerId);
        this.data = data;
        checkContainerDataCount(data, 2);
        this.addDataSlots(data);
    }

    public int getDetectedMobs() {
        return this.data.get(0);
    }

    public int getRadarRange() {
        return this.data.get(1);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
