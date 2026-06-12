package com.Harbinger.sporeaddon;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public class AddonEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SporeAddon.MODID);

    public static final Supplier<EntityType<CDUDecoyEntity>> CDU_DECOY = ENTITIES.register("cdu_decoy",
            () -> EntityType.Builder.<CDUDecoyEntity>of(CDUDecoyEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("cdu_decoy"));
}
