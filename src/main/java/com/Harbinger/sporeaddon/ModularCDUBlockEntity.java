package com.Harbinger.sporeaddon;

import com.Harbinger.Spore.ExtremelySusThings.CustomJsonReader.SporeCduConversionData;
import com.Harbinger.Spore.ExtremelySusThings.Utilities;

import com.Harbinger.Spore.core.Sblocks;
import com.Harbinger.Spore.core.Seffects;
import com.Harbinger.Spore.Sentities.Utility.InfectionTendril;
import com.Harbinger.Spore.Sentities.Utility.ScentEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.world.inventory.ContainerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ModularCDUBlockEntity extends BlockEntity implements GeoBlockEntity {
    private static final TagKey<Block> foliage = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.parse("spore:removable_foliage"));
    public static final TagKey<Item> fungalItems = ItemTags.create(ResourceLocation.parse("spore:weapons"));

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int RADIUS_MODIFIER = 25;

    private final ModEnergyStorage energyStorage = new ModEnergyStorage(100000, 5000, 0);
    private final FluidTank fluidTank = new FluidTank(10000, stack -> stack.getFluid() == net.minecraft.world.level.material.Fluids.WATER) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };
    private final ItemStackHandler itemHandler = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            nbt.putInt("Size", 6);
            super.deserializeNBT(provider, nbt);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            Item item = stack.getItem();
            return item == AddonItems.SPEED_MODIFIER.get() ||
                   item == AddonItems.RADIUS_MODIFIER.get() ||
                   item == AddonItems.EFFICIENCY_MODIFIER.get() ||
                   item == AddonItems.FIRE_DAMAGE_MODIFIER.get() ||
                   item == AddonItems.SUFFOCATION_DAMAGE_MODIFIER.get() ||
                   item == AddonItems.FROST_DAMAGE_MODIFIER.get() ||
                   item == AddonItems.DESTRUCTION_DAMAGE_MODIFIER.get() ||
                   item == AddonItems.IMMUNITY_MODIFIER.get();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    public int currentEnergyCost = 0;
    public int currentWaterCost = 0;

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0: return energyStorage.getEnergyStored();
                case 1: return fluidTank.getFluidAmount();
                case 2: return currentEnergyCost;
                case 3: return currentWaterCost;
                default: return 0;
            }
        }
        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: energyStorage.setEnergy(value); break;
                case 1: 
                    if (value > 0) {
                        fluidTank.setFluid(new FluidStack(net.minecraft.world.level.material.Fluids.WATER, value)); 
                    } else {
                        fluidTank.setFluid(FluidStack.EMPTY);
                    }
                    break;
                case 2: currentEnergyCost = value; break;
                case 3: currentWaterCost = value; break;
            }
        }
        @Override
        public int getCount() {
            return 4;
        }
    };

    private final List<StoreDouble> blockMap;
    public int blockHealth = 20;
    public CDUDecoyEntity decoy;

    public ModularCDUBlockEntity(BlockPos pos, BlockState state) {
        super(AddonBlockEntities.MODULAR_CDU_BE.get(), pos, state);
        blockMap = fabricateBlocks();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new software.bernie.geckolib.animation.AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new software.bernie.geckolib.animation.AnimationController<>(this, "infected_controller", 0, this::infectedPredicate));
    }

    private software.bernie.geckolib.animation.PlayState predicate(software.bernie.geckolib.animation.AnimationState<ModularCDUBlockEntity> event) {
        if (this.getBlockState().hasProperty(ModularCDUBlock.LIT) && this.getBlockState().getValue(ModularCDUBlock.LIT)) {
            event.getController().setAnimation(software.bernie.geckolib.animation.RawAnimation.begin().thenLoop("animation"));
            return software.bernie.geckolib.animation.PlayState.CONTINUE;
        }
        event.getController().forceAnimationReset();
        return software.bernie.geckolib.animation.PlayState.STOP;
    }

    private software.bernie.geckolib.animation.PlayState infectedPredicate(software.bernie.geckolib.animation.AnimationState<ModularCDUBlockEntity> event) {
        if (this.getBlockState().hasProperty(ModularCDUBlock.INFECTED) && this.getBlockState().getValue(ModularCDUBlock.INFECTED)) {
            event.getController().setAnimation(software.bernie.geckolib.animation.RawAnimation.begin().thenLoop("infected"));
            return software.bernie.geckolib.animation.PlayState.CONTINUE;
        }
        event.getController().forceAnimationReset();
        return software.bernie.geckolib.animation.PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public IEnergyStorage getEnergyStorage() { return energyStorage; }
    public IFluidHandler getFluidHandler() { return fluidTank; }
    public IItemHandler getItemHandler() { return itemHandler; }

    record StoreDouble(Block value1, Block value2){}

    private List<StoreDouble> fabricateBlocks(){
        List<StoreDouble> blocks = new ArrayList<>();
        for (String str : SConfig.DATAGEN.block_cleaning.get()){
            String[] string = str.split("\\|" );
            Block blockCon1 = Utilities.tryToCreateBlock(ResourceLocation.parse(string[0]));
            Block blockCon2 = Utilities.tryToCreateBlock(ResourceLocation.parse(string[1]));
            if (blockCon1 != Blocks.AIR && blockCon2 != Blocks.AIR){
                blocks.add(new StoreDouble(blockCon1,blockCon2));
            }
        }
        return blocks;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ModularCDUBlockEntity be) {
        if (state.getValue(ModularCDUBlock.INFECTED)) return;

        // Spore AI attraction and block breaking
        if (level.getGameTime() % 20 == 0) {
            AABB aggroBox = new AABB(pos).inflate(32);
            List<com.Harbinger.Spore.Sentities.BaseEntities.UtilityEntity> infectedList = level.getEntitiesOfClass(com.Harbinger.Spore.Sentities.BaseEntities.UtilityEntity.class, aggroBox);
            
            if (!infectedList.isEmpty() && state.getValue(ModularCDUBlock.LIT)) {
                if (be.decoy == null || be.decoy.isRemoved()) {
                    List<CDUDecoyEntity> existing = level.getEntitiesOfClass(CDUDecoyEntity.class, new AABB(pos));
                    if (!existing.isEmpty()) {
                        be.decoy = existing.get(0);
                    }
                    if (be.decoy == null || be.decoy.isRemoved()) {
                        be.decoy = new CDUDecoyEntity(AddonEntities.CDU_DECOY.get(), level);
                        be.decoy.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                        level.addFreshEntity(be.decoy);
                    }
                }
            } else if (be.decoy != null) {
                be.decoy.discard();
                be.decoy = null;
            }

            if (be.decoy != null && be.decoy.isAlive()) {
                float damageTaken = be.decoy.getMaxHealth() - be.decoy.getHealth();
                if (damageTaken > 0) {
                    be.blockHealth -= damageTaken;
                    be.decoy.setHealth(be.decoy.getMaxHealth());
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, 1f);
                    be.setChanged();
                    
                    if (be.blockHealth <= 0) {
                        int immunityMods = countModifier(be, AddonItems.IMMUNITY_MODIFIER.get());
                        if (immunityMods == 0) {
                            level.setBlock(pos, state.setValue(ModularCDUBlock.INFECTED, true).setValue(ModularCDUBlock.LIT, false), 3);
                            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, net.minecraft.sounds.SoundSource.BLOCKS, 1f, 1f);
                        } else {
                            level.destroyBlock(pos, true);
                        }
                        if (be.decoy != null) {
                            be.decoy.discard();
                            be.decoy = null;
                        }
                        return;
                    }
                }
            }

            for (com.Harbinger.Spore.Sentities.BaseEntities.UtilityEntity infected : infectedList) {
                if (be.decoy != null && (infected.getTarget() == null || infected.distanceToSqr(Vec3.atCenterOf(pos)) < infected.distanceToSqr(infected.getTarget()))) {
                    infected.setTarget(be.decoy);
                    infected.getNavigation().moveTo(be.decoy, 1.2D);
                }
            }
        }

        int speedMods = countModifier(be, AddonItems.SPEED_MODIFIER.get());
        int radiusMods = countModifier(be, AddonItems.RADIUS_MODIFIER.get());
        int efficiencyMods = countModifier(be, AddonItems.EFFICIENCY_MODIFIER.get());
        int fireMods = countModifier(be, AddonItems.FIRE_DAMAGE_MODIFIER.get());
        int suffocationMods = countModifier(be, AddonItems.SUFFOCATION_DAMAGE_MODIFIER.get());
        int frostMods = countModifier(be, AddonItems.FROST_DAMAGE_MODIFIER.get());
        int destructionMods = countModifier(be, AddonItems.DESTRUCTION_DAMAGE_MODIFIER.get());

        int tickRate = Math.max(20, 200 - (speedMods * 40));

        int totalActiveMods = speedMods + radiusMods + fireMods + suffocationMods + frostMods + destructionMods;
        int energyCostPerSecond = Math.max(50, 400 + (totalActiveMods * 50) - (efficiencyMods * 150));
        if (frostMods > 0) {
            energyCostPerSecond *= (int) Math.pow(2, frostMods);
        }
        int energyCost = energyCostPerSecond * (tickRate / 20);
        int waterCost = Math.max(25, 150 + (totalActiveMods * 100) + (frostMods * 600) - (efficiencyMods * 50));

        be.currentEnergyCost = energyCostPerSecond;
        be.currentWaterCost = waterCost / (tickRate / 20);

        if (level.getGameTime() % tickRate != 0) return;

        boolean canRun = be.energyStorage.getEnergyStored() >= energyCost &&
                         be.fluidTank.getFluidAmount() >= waterCost &&
                         be.fluidTank.getFluid().is(net.minecraft.world.level.material.Fluids.WATER);

        if (state.hasProperty(ModularCDUBlock.LIT) && state.getValue(ModularCDUBlock.LIT) != canRun) {
            level.setBlock(pos, state.setValue(ModularCDUBlock.LIT, canRun), 3);
        }

        if (!canRun) return;

        // Execute cleaning
        be.energyStorage.extractEnergyInternal(energyCost, false);
        be.fluidTank.drain(waterCost, IFluidHandler.FluidAction.EXECUTE);

        int range = be.RADIUS_MODIFIER + (radiusMods * 4);
        be.cleanInfection(pos, range, fireMods, suffocationMods, frostMods, destructionMods);
        be.setChanged();
    }

    private static int countModifier(ModularCDUBlockEntity be, Item modifierItem) {
        int count = 0;
        int slots = be.itemHandler.getSlots();
        for (int i = 0; i < slots; i++) {
            ItemStack stack = be.itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == modifierItem) {
                count += stack.getCount();
            }
        }
        return Math.min(count, 6); // Cap max modifiers effect
    }

    public void cleanInfection(BlockPos blockPos, int range, int fireMods, int suffocationMods, int frostMods, int destructionMods) {
        AABB aabb = AABB.ofSize(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), range, range, range);
        if (level == null) return;

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb);

        for(BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
            BlockState state = level.getBlockState(blockpos);
            if (state.is(foliage)){
                if (Math.random() < 0.2) level.removeBlock(blockpos,false);
            }
            if (state == Sblocks.REMAINS.get().defaultBlockState()){
                level.setBlock(blockpos,Sblocks.FROZEN_REMAINS.get().defaultBlockState(),3);
            }
            if (Math.random() < 0.2 && !blockMap.isEmpty()){
                for (StoreDouble storeDouble : blockMap){
                    if (storeDouble.value1 == state.getBlock()){
                        level.setBlock(blockpos,storeDouble.value2.defaultBlockState(),3);
                    }
                }
                convertFromJson(level,state,blockpos);
            }
            if (Math.random() < 0.1){
                if (state.is(Utilities.biomass) || state.is(Sblocks.MEMBRANE_BLOCK)){
                    level.setBlock(blockpos,Sblocks.FROST_BURNED_BIOMASS.get().defaultBlockState(),3);
                }
                if (state == Sblocks.BILE.get().defaultBlockState()){
                    level.setBlock(blockpos,Sblocks.CRUSTED_BILE.get().defaultBlockState(),3);
                }
            }
        }
        for (Entity entity : entities){
            if (entity instanceof LivingEntity livingEntity && !(entity instanceof Player)) {
                // Default frostbite logic from original CDU
                if (livingEntity.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)){
                    MobEffectInstance instance = livingEntity.getEffect(Seffects.FROSTBITE);
                    int intensity = instance == null ? 0 : instance.getAmplifier()+1;
                    livingEntity.addEffect(new MobEffectInstance(Seffects.FROSTBITE,1200,intensity));
                }

                boolean isSporeMob = BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).getNamespace().equals("spore");
                
                if (isSporeMob) {
                    // Custom Damage Modifiers
                    if (fireMods > 0) {
                        livingEntity.hurt(level.damageSources().inFire(), fireMods * 2.0F);
                        livingEntity.igniteForSeconds(fireMods * 3);
                    }
                    if (suffocationMods > 0) {
                        livingEntity.hurt(level.damageSources().drown(), suffocationMods * 2.0F);
                    }
                    if (frostMods > 0) {
                        livingEntity.hurt(level.damageSources().freeze(), frostMods * 5.0F);
                        livingEntity.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 200, frostMods));
                    }
                    if (destructionMods > 0) {
                        livingEntity.hurt(level.damageSources().magic(), destructionMods * 2.0F);
                    }
                }
            }
            if (entity instanceof Player player){
                boolean be = false;
                for (ItemStack stack : player.getArmorSlots()){
                    if (stack.is(fungalItems)){ be = true; break; }
                }
                if (be){
                    MobEffectInstance instance = player.getEffect(Seffects.FROSTBITE);
                    int intensity = instance == null ? 0 : instance.getAmplifier()+1;
                    player.addEffect(new MobEffectInstance(Seffects.FROSTBITE,600,intensity));
                }
            }
            if (entity instanceof ScentEntity || entity instanceof InfectionTendril){
                entity.discard();
            }
        }
    }

    private void convertFromJson(Level level, BlockState blockstate, BlockPos blockpos) {
        Block targetBlock = SporeCduConversionData.getResult(blockstate.getBlock());
        if (targetBlock == null) return;
        BlockState _bs = targetBlock.defaultBlockState();
        for (Map.Entry<Property<?>, Comparable<?>> entry : blockstate.getValues().entrySet()) {
            Property<?> property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
            if (property != null) {
                try {
                    _bs = _bs.setValue((Property) property, (Comparable) entry.getValue());
                } catch (Exception ignored) {}
            }
        }
        level.setBlock(blockpos, _bs, 3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Energy", energyStorage.getEnergyStored());
        tag.put("Inventory", itemHandler.serializeNBT(registries));
        CompoundTag fluidTag = new CompoundTag();
        fluidTank.writeToNBT(registries, fluidTag);
        tag.put("Fluid", fluidTag);
        tag.putInt("BlockHealth", blockHealth);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Energy")) energyStorage.setEnergy(tag.getInt("Energy"));
        if (tag.contains("Inventory")) itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("Fluid")) fluidTank.readFromNBT(registries, tag.getCompound("Fluid"));
        if (tag.contains("BlockHealth")) blockHealth = tag.getInt("BlockHealth");
    }

    private class ModEnergyStorage extends EnergyStorage {
        public ModEnergyStorage(int capacity, int maxReceive, int maxExtract) { super(capacity, maxReceive, maxExtract); }
        public void extractEnergyInternal(int maxExtract, boolean simulate) {
            int energyExtracted = Math.min(energy, maxExtract);
            if (!simulate) energy -= energyExtracted;
        }
        public void setEnergy(int energy) { this.energy = energy; }
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) setChanged();
            return received;
        }
    }

    @Override
    public void setRemoved() {
        if (decoy != null) {
            decoy.discard();
            decoy = null;
        }
        super.setRemoved();
    }
}
