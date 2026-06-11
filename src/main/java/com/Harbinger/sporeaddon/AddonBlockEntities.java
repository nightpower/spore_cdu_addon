package com.Harbinger.sporeaddon;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AddonBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SporeAddon.MODID);

    public static final Supplier<BlockEntityType<ModularCDUBlockEntity>> MODULAR_CDU_BE = BLOCK_ENTITIES.register("modular_cdu",
            () -> BlockEntityType.Builder.of((pos, state) -> new ModularCDUBlockEntity(pos, state), AddonBlocks.MODULAR_CDU.get()).build(null));
}
