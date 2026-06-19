package com.Harbinger.sporeaddon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.lang.reflect.Method;

public class AddonEvents {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide) return;

        BlockPos pos = event.getPos();
        BlockState clickedState = level.getBlockState(pos);

        if (clickedState.getBlock() instanceof SporeRadarBlock) {
            ItemStack stack = event.getItemStack();
            if (stack.getItem().getClass().getName().equals("com.happysg.radar.block.datalink.DataLinkBlockItem")) {
                CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                CompoundTag tag = data.copyTag();
                if (tag.contains("SelectedFiltererPos")) {
                    BlockPos filtererPos = NbtUtils.readBlockPos(tag, "SelectedFiltererPos").orElse(null);
                    if (filtererPos != null) {
                        BlockPos placedPos = pos.relative(event.getFace());
                        if (level.getBlockState(placedPos).canBeReplaced()) {
                            try {
                                Block dataLinkBlock = ((BlockItem) stack.getItem()).getBlock();
                                BlockState placedState = dataLinkBlock.defaultBlockState();
                                
                                Direction facing = event.getFace();
                                for (Property<?> prop : placedState.getProperties()) {
                                    if (prop.getName().equals("facing") && prop instanceof DirectionProperty dirProp) {
                                        placedState = placedState.setValue(dirProp, facing);
                                        break;
                                    }
                                }

                                level.setBlock(placedPos, placedState, 3);

                                BlockEntity be = level.getBlockEntity(placedPos);
                                if (be != null && be.getClass().getName().equals("com.happysg.radar.block.datalink.DataLinkBlockEntity")) {
                                    Method targetMethod = be.getClass().getMethod("target", BlockPos.class);
                                    targetMethod.invoke(be, filtererPos);
                                }

                                tag.remove("SelectedFiltererPos");
                                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

                                event.getEntity().displayClientMessage(net.minecraft.network.chat.Component.translatable("display_link.success").withStyle(net.minecraft.ChatFormatting.GREEN), true);

                                event.setCanceled(true);
                                event.setCancellationResult(InteractionResult.SUCCESS);
                            } catch (Exception e) {
                                SporeAddon.LOGGER.error("Failed to place Data Link via reflection", e);
                            }
                        }
                    }
                }
            }
        }
    }
}
