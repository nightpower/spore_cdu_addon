package com.Harbinger.sporeaddon;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;

public class ModularCDUBlock extends BaseEntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty INFECTED = BooleanProperty.create("infected");

    public ModularCDUBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(INFECTED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, INFECTED);
    }

    public static final MapCodec<ModularCDUBlock> CODEC = simpleCodec(ModularCDUBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ModularCDUBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && !state.getValue(INFECTED)) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof ModularCDUBlockEntity modularCDUBlockEntity) {
                player.openMenu(new net.minecraft.world.SimpleMenuProvider(
                        (id, inv, p) -> new ModularCDUMenu(id, inv, modularCDUBlockEntity, modularCDUBlockEntity.dataAccess),
                        net.minecraft.network.chat.Component.translatable("block.sporeaddon.modular_cdu")
                ), pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(INFECTED)) return;
        
        if (state.getValue(LIT)) {
            Vec3 localOffset = new Vec3(0.5, 1, 0.5);
            for (int i = 0; i < 360; i += 40) {
                double yy = Math.sin(i) * Math.cos(i) * 0.25d;
                level.addParticle(
                        net.minecraft.core.particles.ParticleTypes.SNOWFLAKE,
                        pos.getX() + localOffset.x, pos.getY() + localOffset.y, pos.getZ() + localOffset.z,
                        Math.cos(i) * 0.15d,
                        yy,
                        Math.sin(i) * 0.15d
                );
            }
            if (random.nextInt(40) == 0) {
                level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, com.Harbinger.Spore.core.Ssounds.CDU_AMBIENT.value(), net.minecraft.sounds.SoundSource.BLOCKS, 1f, 1f, false);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ModularCDUBlockEntity cdu) {
                for (int i = 0; i < cdu.getItemHandler().getSlots(); i++) {
                    net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), cdu.getItemHandler().getStackInSlot(i));
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return createTickerHelper(type, AddonBlockEntities.MODULAR_CDU_BE.get(), ModularCDUBlockEntity::serverTick);
    }
}
