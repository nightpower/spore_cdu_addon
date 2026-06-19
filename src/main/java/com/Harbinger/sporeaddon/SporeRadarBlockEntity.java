package com.Harbinger.sporeaddon;

import com.Harbinger.Spore.Sentities.BaseEntities.UtilityEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.energy.EnergyStorage;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;

public class SporeRadarBlockEntity extends BlockEntity implements GeoBlockEntity, net.minecraft.world.MenuProvider {
    public static final int HIGHLIGHT_DURATION_TICKS = 100; // 5 seconds
    public static final int BASE_ENERGY_PER_ROTATION = 5000;
    public static final int ENERGY_PER_BLOCK_RANGE = 100;
    public static final int MAX_ENERGY = 100000;

    private final EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private float lastRadarAngle = -1;
    private float accumulatedAngle = 0;

    private int detectedMobs = 0;
    private int radarRange = 0;

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> SporeRadarBlockEntity.this.detectedMobs;
                case 1 -> SporeRadarBlockEntity.this.radarRange;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> SporeRadarBlockEntity.this.detectedMobs = value;
                case 1 -> SporeRadarBlockEntity.this.radarRange = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public SporeRadarBlockEntity(BlockPos pos, BlockState state) {
        super(AddonBlockEntities.SPORE_RADAR_BE.get(), pos, state);
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SporeRadarBlockEntity entity) {
        if (level.isClientSide()) return;

        BlockEntity adjacentRadar = entity.findAdjacentRadar(level, pos);
        if (adjacentRadar != null) {
            entity.processRadarRotation(level, pos, adjacentRadar);
        } else {
            entity.lastRadarAngle = -1;
            entity.accumulatedAngle = 0;
        }
    }

    private BlockEntity findAdjacentRadar(Level level, BlockPos pos) {
        // First check a 3-block radius for direct connection (legacy behavior)
        for (BlockPos offset : BlockPos.betweenClosed(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            BlockEntity be = level.getBlockEntity(offset);
            if (be != null && be.getClass().getName().equals("com.happysg.radar.block.radar.bearing.RadarBearingBlockEntity")) {
                return be;
            }
        }
        
        // Then check adjacent blocks for Data Link
        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(pos.relative(dir));
            if (be != null && be.getClass().getName().contains("DataLinkBlockEntity")) {
                try {
                    Method getTargetPositionMethod = be.getClass().getMethod("getTargetPosition");
                    BlockPos targetPos = (BlockPos) getTargetPositionMethod.invoke(be);
                    if (targetPos != null) {
                        BlockEntity targetBe = level.getBlockEntity(targetPos);
                        if (targetBe != null && (targetBe.getClass().getName().contains("RadarBearingBlockEntity") || targetBe.getClass().getName().contains("NetworkFiltererBlockEntity"))) {
                            return targetBe;
                        }
                    }
                } catch (Exception e) {
                    // Ignore reflection errors
                }
            }
        }
        return null;
    }

    private void processRadarRotation(Level level, BlockPos pos, BlockEntity radarEntity) {
        try {
            Method getGlobalAngleMethod = radarEntity.getClass().getMethod("getGlobalAngle");
            float currentAngle = (float) getGlobalAngleMethod.invoke(radarEntity);

            if (lastRadarAngle == -1) {
                lastRadarAngle = currentAngle;
                return;
            }

            float diff = currentAngle - lastRadarAngle;
            // Handle angle wrap-around (0 to 360)
            if (diff < -180) {
                diff += 360;
            } else if (diff > 180) {
                diff -= 360;
            }

            accumulatedAngle += Math.abs(diff);
            lastRadarAngle = currentAngle;

            if (accumulatedAngle >= 360) {
                accumulatedAngle -= 360;
                onFullRotation(level, pos, radarEntity);
            }

        } catch (Exception e) {
            // Silently fail or log if needed, reflection failed
        }
    }

    private void onFullRotation(Level level, BlockPos pos, BlockEntity radarEntity) {
        try {
            Method getRangeMethod = radarEntity.getClass().getMethod("getRange");
            float range = (float) getRangeMethod.invoke(radarEntity);
            this.radarRange = (int) range;
            
            int requiredEnergy = BASE_ENERGY_PER_ROTATION + (int) (range * ENERGY_PER_BLOCK_RANGE);

            if (energyStorage.getEnergyStored() >= requiredEnergy) {
                // Consume energy
                energyStorage.extractEnergy(requiredEnergy, false);
                setChanged();

                // Scan for Spore mobs
                scanAndHighlight(level, pos, range);
            }
        } catch (Exception e) {
            // Ignore exception
        }
    }

    private void scanAndHighlight(Level level, BlockPos pos, float range) {
        AABB scanBox = new AABB(pos).inflate(range);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, scanBox, entity -> {
            return entity.getType().getDescriptionId().contains("spore") || entity instanceof UtilityEntity;
        });

        int count = 0;
        for (LivingEntity entity : entities) {
            entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, HIGHLIGHT_DURATION_TICKS));
            count++;
        }
        this.detectedMobs = count;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.sporeaddon.spore_radar");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new SporeRadarMenu(id, inventory, this.containerData);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Energy", energyStorage.getEnergyStored());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Energy")) {
            energyStorage.receiveEnergy(tag.getInt("Energy"), false);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, event -> {
            // Play an idle animation if one exists in the geo.json, e.g., "animation.radar.idle"
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.radar.idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
