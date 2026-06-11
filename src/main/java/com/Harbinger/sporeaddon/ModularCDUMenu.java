package com.Harbinger.sporeaddon;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ModularCDUMenu extends AbstractContainerMenu {
    public final ModularCDUBlockEntity blockEntity;
    private final ContainerData data;

    public ModularCDUMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, (ModularCDUBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public ModularCDUMenu(int id, Inventory inv, ModularCDUBlockEntity entity, ContainerData data) {
        super(AddonMenus.MODULAR_CDU_MENU.get(), id);
        this.blockEntity = entity;
        this.data = data;

        // Mutated Fiber slot (Slot 0)
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 44, 35));

        // Modifier slots (Slot 1, 2, 3)
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 116, 17));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 116, 35));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 3, 116, 53));

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addDataSlots(data);
    }

    public int getEnergy() {
        return this.data.get(0);
    }

    public int getFluidAmount() {
        return this.data.get(1);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 4) {
                if (!this.moveItemStackTo(itemstack1, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 4, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
