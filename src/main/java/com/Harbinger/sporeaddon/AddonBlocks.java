package com.Harbinger.sporeaddon;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AddonBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, SporeAddon.MODID);

    public static final Supplier<Block> MODULAR_CDU = BLOCKS.register("modular_cdu",
            () -> new ModularCDUBlock(BlockBehaviour.Properties.of().strength(3.0F, 6.0F).requiresCorrectToolForDrops()));
}
