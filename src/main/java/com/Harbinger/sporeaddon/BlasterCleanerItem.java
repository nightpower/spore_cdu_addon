package com.Harbinger.sporeaddon;

import com.Harbinger.Spore.ExtremelySusThings.CustomJsonReader.SporeCduConversionData;
import com.Harbinger.Spore.ExtremelySusThings.Utilities;
import com.Harbinger.Spore.core.Sblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;

public class BlasterCleanerItem extends Item {
    public static final int MAX_ENERGY = 100000;
    public static final int ENERGY_PER_SHOT = 10000;
    public static final int RANGE = 64;

    public BlasterCleanerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            boolean hasAmmo = false;
            if (player.isCreative()) {
                hasAmmo = true;
            } else {
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack invStack = player.getInventory().getItem(i);
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(invStack.getItem());
                    if (itemId.getNamespace().equals("spore") && itemId.getPath().equals("ice_canister")) {
                        hasAmmo = true;
                        invStack.shrink(1);
                        break;
                    }
                }
            }

            if (!hasAmmo) {
                return InteractionResultHolder.fail(stack);
            }

            int energy = getEnergy(stack);
            if (energy < ENERGY_PER_SHOT && !player.isCreative()) {
                return InteractionResultHolder.fail(stack);
            }

            if (!player.isCreative()) {
                setEnergy(stack, energy - ENERGY_PER_SHOT);
            }

            // Sound like Warden
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 3.0F, 1.0F);

            Vec3 startPos = player.getEyePosition();
            Vec3 lookVec = player.getLookAngle();
            Vec3 endPos = startPos.add(lookVec.scale(RANGE));

            ServerLevel serverLevel = (ServerLevel) level;

            // Spawn Particles along the line
            for (int i = 1; i <= RANGE; i++) {
                Vec3 particlePos = startPos.add(lookVec.scale(i));
                serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, particlePos.x, particlePos.y, particlePos.z, 1, 0, 0, 0, 0.0);
            }

            // AABB around the beam
            AABB hitbox = new AABB(startPos, endPos).inflate(2.0D);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, hitbox);

            for (LivingEntity entity : entities) {
                if (entity == player) continue;
                // Basic beam intersection
                AABB entityBox = entity.getBoundingBox().inflate(1.0D);
                if (entityBox.clip(startPos, endPos).isPresent()) {
                    ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
                    if (entityId != null && entityId.getNamespace().equals("spore")) {
                        // Spore Mob Logic
                        boolean isBoss = entity.getMaxHealth() > 100; // Guessing boss by health, can be adjusted
                        if (entity.getTags().contains("boss") || entity.getType().getDescriptionId().toLowerCase().contains("boss") || isBoss) {
                            entity.hurt(level.damageSources().magic(), entity.getMaxHealth() / 2.0f);
                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2));
                        } else {
                            entity.hurt(level.damageSources().genericKill(), Float.MAX_VALUE); // Instakill
                        }
                    }
                }
            }

            // Clean blocks along the ray using a somewhat wide area (e.g. radius 2 around the ray)
            BlockPos.betweenClosedStream(
                    new BlockPos((int) Math.min(startPos.x, endPos.x) - 2, (int) Math.min(startPos.y, endPos.y) - 2, (int) Math.min(startPos.z, endPos.z) - 2),
                    new BlockPos((int) Math.max(startPos.x, endPos.x) + 2, (int) Math.max(startPos.y, endPos.y) + 2, (int) Math.max(startPos.z, endPos.z) + 2)
            ).forEach(pos -> {
                AABB blockBox = new AABB(pos);
                if (blockBox.clip(startPos, endPos).isPresent() || blockBox.distanceToSqr(startPos) < RANGE*RANGE) { // Distance check to line could be better
                     // Distance to line
                    Vec3 blockVec = Vec3.atCenterOf(pos);
                    Vec3 line = endPos.subtract(startPos);
                    double len = line.length();
                    Vec3 u = line.normalize();
                    Vec3 v = blockVec.subtract(startPos);
                    double proj = v.dot(u);
                    if (proj >= 0 && proj <= len) {
                        Vec3 closest = startPos.add(u.scale(proj));
                        if (closest.distanceToSqr(blockVec) <= 4.0D) { // radius 2 squared
                            cleanBlock(level, pos);
                        }
                    }
                }
            });

            player.getCooldowns().addCooldown(this, 40); // 2 seconds cooldown
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
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

        // Conversion data
        Block targetBlock = SporeCduConversionData.getResult(state.getBlock());
        if (targetBlock != null) {
            BlockState _bs = targetBlock.defaultBlockState();
            for (Map.Entry<Property<?>, Comparable<?>> entry : state.getValues().entrySet()) {
                Property<?> property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
                if (property != null) {
                    try {
                        _bs = _bs.setValue((Property) property, (Comparable) entry.getValue());
                    } catch (Exception ignored) {}
                }
            }
            level.setBlock(blockpos, _bs, 3);
        } else {
            ResourceLocation blockLoc = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            if (blockLoc != null && blockLoc.getNamespace().equals("spore")) {
                // Break spore plants/foliage
                if (state.is(net.minecraft.tags.BlockTags.LEAVES) || 
                    state.is(net.minecraft.tags.BlockTags.SMALL_FLOWERS) || 
                    state.is(net.minecraft.tags.BlockTags.TALL_FLOWERS) || 
                    state.is(net.minecraft.tags.BlockTags.REPLACEABLE) || 
                    state.is(net.minecraft.tags.TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.parse("spore:removable_foliage")))) {
                    level.removeBlock(blockpos, false);
                } else {
                    // Force clean infected blocks that might be missed by SporeCduConversionData
                    String path = blockLoc.getPath();
                    if (path.contains("stone")) {
                        level.setBlock(blockpos, net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), 3);
                    } else if (path.contains("sand")) {
                        level.setBlock(blockpos, net.minecraft.world.level.block.Blocks.SAND.defaultBlockState(), 3);
                    } else if (path.contains("dirt") || path.contains("soil")) {
                        level.setBlock(blockpos, net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState(), 3);
                    } else if (path.contains("gravel")) {
                        level.setBlock(blockpos, net.minecraft.world.level.block.Blocks.GRAVEL.defaultBlockState(), 3);
                    } else if (path.contains("deepslate")) {
                        level.setBlock(blockpos, net.minecraft.world.level.block.Blocks.DEEPSLATE.defaultBlockState(), 3);
                    } else if (path.contains("grass")) {
                        level.setBlock(blockpos, net.minecraft.world.level.block.Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                    } else if (!state.isSolid()) { // If it's some other non-solid spore block (like vines/plants), break it
                        level.removeBlock(blockpos, false);
                    }
                }
            }
        }
    }

    public static int getEnergy(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        return tag.getInt("Energy");
    }

    public static void setEnergy(ItemStack stack, int energy) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        tag.putInt("Energy", Math.max(0, Math.min(energy, MAX_ENERGY)));
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
    
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getEnergy(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getEnergy(stack) / MAX_ENERGY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FFFF; // Cyan color for energy
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<net.minecraft.network.chat.Component> tooltipComponents, net.minecraft.world.item.TooltipFlag tooltipFlag) {
        tooltipComponents.add(net.minecraft.network.chat.Component.translatable("tooltip.sporeaddon.blaster_cleaner.desc").withStyle(net.minecraft.ChatFormatting.AQUA));
        tooltipComponents.add(net.minecraft.network.chat.Component.literal("Energy: " + getEnergy(stack) + " / " + MAX_ENERGY).withStyle(net.minecraft.ChatFormatting.YELLOW));
    }
}
