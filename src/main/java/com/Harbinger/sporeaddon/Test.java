
package com.Harbinger.sporeaddon;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.npc.Villager;

public class Test extends Villager {
    public Test(EntityType<? extends Villager> type, Level level) {
        super(type, level);
    }
}
