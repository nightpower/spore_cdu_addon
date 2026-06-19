package com.Harbinger.sporeaddon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.bullet.MachineGunRoundItem;

public class FreezingMachineGunRoundItem extends MachineGunRoundItem {

    public FreezingMachineGunRoundItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        EntityType type = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(net.minecraft.resources.ResourceLocation.parse("createbigcannons:machine_gun_bullet"));
        return new FreezingMachineGunProjectile(type, level);
    }

    @Override
    public EntityType<?> getEntityType(ItemStack stack) {
        return net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(net.minecraft.resources.ResourceLocation.parse("createbigcannons:machine_gun_bullet"));
    }
}
