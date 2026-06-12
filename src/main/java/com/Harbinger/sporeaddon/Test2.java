
package com.Harbinger.sporeaddon;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Test2 extends Villager {
    public Test2(EntityType<? extends Villager> type, Level level) {
        super(type, level);
    }
    @Override
    public boolean isPickable() { return false; }
    @Override
    public boolean canBeHitByProjectile() { return true; }
}
