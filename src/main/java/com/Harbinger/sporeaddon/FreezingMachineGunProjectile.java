package com.Harbinger.sporeaddon;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.BuiltInRegistries;
import rbasamoyai.createbigcannons.munitions.autocannon.bullet.MachineGunProjectile;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;

import com.Harbinger.Spore.ExtremelySusThings.CustomJsonReader.SporeCduConversionData;
import com.Harbinger.Spore.ExtremelySusThings.Utilities;
import com.Harbinger.Spore.core.Sblocks;

public class FreezingMachineGunProjectile extends MachineGunProjectile {

    public FreezingMachineGunProjectile(EntityType<? extends MachineGunProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected boolean onHitEntity(net.minecraft.world.entity.Entity entity, ProjectileContext ctx) {
        boolean result = super.onHitEntity(entity, ctx);
        applyFreezingEffect(entity.position());
        return result;
    }

    @Override
    protected boolean onImpactFluid(ProjectileContext ctx, BlockState state, net.minecraft.world.level.material.FluidState fluidState, Vec3 startPos, BlockHitResult hitResult) {
        boolean result = super.onImpactFluid(ctx, state, fluidState, startPos, hitResult);
        applyFreezingEffect(hitResult.getLocation());
        return result;
    }

    // AbstractCannonProjectile's hit block is handled inside calculateBlockPenetration usually, or via onImpact
    // To be safe and capture all impacts (bounces, penetration stops, etc.), we can override onImpact or just apply it when it hits a block.
    // We'll apply it on entity hit and we can also override onImpact for general hits.
    
    @Override
    protected boolean onImpact(net.minecraft.world.phys.HitResult result, rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile.ImpactResult impactResult, ProjectileContext ctx) {
        boolean res = super.onImpact(result, impactResult, ctx);
        applyFreezingEffect(result.getLocation());
        return res;
    }

    private void applyFreezingEffect(Vec3 impactPos) {
        Level level = this.level();
        if (level.isClientSide) return;

        double radius = 2.0;
        AABB area = new AABB(impactPos.x - radius, impactPos.y - radius, impactPos.z - radius, 
                             impactPos.x + radius, impactPos.y + radius, impactPos.z + radius);

        // 1. Freeze/Kill Spore Mobs
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
            ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
            if (entityId != null && entityId.getNamespace().equals("spore")) {
                boolean isBoss = entity.getMaxHealth() > 100;
                if (entity.getTags().contains("boss") || entity.getType().getDescriptionId().toLowerCase().contains("boss") || isBoss) {
                    entity.hurt(level.damageSources().magic(), entity.getMaxHealth() / 2.0f);
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2));
                } else {
                    entity.hurt(level.damageSources().genericKill(), Float.MAX_VALUE); // Instakill
                }
            }
        }

        // 2. Clean Spore Blocks
        BlockPos.betweenClosedStream(
                new BlockPos((int) (impactPos.x - radius), (int) (impactPos.y - radius), (int) (impactPos.z - radius)),
                new BlockPos((int) (impactPos.x + radius), (int) (impactPos.y + radius), (int) (impactPos.z + radius))
        ).forEach(pos -> {
            Vec3 blockVec = Vec3.atCenterOf(pos);
            if (blockVec.distanceToSqr(impactPos) <= radius * radius) {
                cleanBlock(level, pos);
            }
        });
    }

    private void cleanBlock(Level level, BlockPos blockpos) {
        BlockState state = level.getBlockState(blockpos);
        if (state == Sblocks.REMAINS.get().defaultBlockState()) {
            level.setBlock(blockpos, Sblocks.FROZEN_REMAINS.get().defaultBlockState(), 3);
        }
        if (state.is(Utilities.biomass) || state.is(Sblocks.MEMBRANE_BLOCK)) {
            level.setBlock(blockpos, Sblocks.FROST_BURNED_BIOMASS.get().defaultBlockState(), 3);
        }
        if (state == Sblocks.BILE.get().defaultBlockState()) {
            level.setBlock(blockpos, Sblocks.CRUSTED_BILE.get().defaultBlockState(), 3);
        }

        Block targetBlock = SporeCduConversionData.getResult(state.getBlock());
        if (targetBlock != null) {
            BlockState _bs = targetBlock.defaultBlockState();
            level.setBlock(blockpos, _bs, 3);
        }
    }
}
