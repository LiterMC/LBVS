package com.github.litermc.lbvs.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.SplittingDisablerAttachment;

import com.github.litermc.lbvs.api.BlockConnectivityManager;
import com.github.litermc.lbvs.util.AssembleUtil;
import com.github.litermc.lbvs.util.Direction26;
import com.github.litermc.lbvs.util.EnumSetProperty;

import java.util.stream.Stream;

public class LevelListener {
	private static final EnumSetProperty<Direction26> VIOLATE_DIRS = new EnumSetProperty<>("vs_violate_dirs", Direction26.class);

	private static boolean ignoreBlockUpdate = false;

	public static void onBlockUpdated(
			final ServerLevel level, final BlockPos pos,
			final BlockState oldState, final BlockState newState,
			final boolean moving) {
		if (ignoreBlockUpdate) {
			return;
		}
		ignoreBlockUpdate = true;
		try {
			ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos(level, pos);
			if (ship == null) {
				onWorldBlockUpdated(level, pos, oldState, newState, moving);
			} else {
				onShipBlockUpdated(level, pos, ship, oldState, newState, moving);
			}
		} finally {
			ignoreBlockUpdate = false;
		}
	}

	private static void onWorldBlockUpdated(
			final ServerLevel level, final BlockPos pos,
			final BlockState oldState, final BlockState newState,
			final boolean moving) {
		if (newState.isAir()) {
			if (oldState.isAir()) {
				return;
			}
			onWorldBlockRemoved(level, pos, oldState, moving);
		}
	}

	private static void onShipBlockUpdated(
			final ServerLevel level, final BlockPos pos, final ServerShip ship,
			final BlockState oldState, final BlockState newState,
			final boolean moving) {
		BlockConnectivityManager manager = BlockConnectivityManager.getInstance();
		SplittingDisablerAttachment splitDisablerAttachment = ship.getAttachment(SplittingDisablerAttachment.class);
		if (splitDisablerAttachment != null && !splitDisablerAttachment.canSplit()) {
			return;
		}
		System.out.println("block updating: " + level + "@" + pos + " (" + ship + ") " + " old: " + oldState + ", new: " + newState);
		if (!manager.shouldCheckConnectivity(level, pos, oldState, newState)) {
			System.out.println("shouldn't check connectivity");
			return;
		}
		Direction26.stream()
			.forEach(dir -> {
				boolean connectable = manager.canBlockConnect(level, pos, dir);
				System.out.println("connectable: " + dir + ": " + connectable);
			});
	}

	private static void onWorldBlockRemoved(
			final ServerLevel level, final BlockPos pos,
			final BlockState oldState, final boolean moving) {
		if (oldState.is(BlockTags.OVERWORLD_NATURAL_LOGS)) {
			if (oldState.getOptionalValue(RotatedPillarBlock.AXIS).map(axis -> axis != Direction.Axis.Y).orElse(true) || moving) {
				return;
			}
			BlockPos.MutableBlockPos leafPos = pos.mutable();
			int maxY = Math.min(pos.getY() + 32, level.getMaxBuildHeight());
			for (int y = pos.getY() + 1; y <= maxY; y++) {
				leafPos.setY(y);
				BlockState st = level.getBlockState(leafPos);
				if (st.is(BlockTags.LEAVES)) {
					if (!st.getOptionalValue(LeavesBlock.PERSISTENT).orElse(true)) {
						ServerShip ship = AssembleUtil.assembleTree(level, pos, oldState);
						if (ship != null) {
							EventHandler.onTreeAssembled(level, ship);
						}
					}
					return;
				} else if (st.is(BlockTags.OVERWORLD_NATURAL_LOGS)) {
					if (st.getOptionalValue(RotatedPillarBlock.AXIS).map(axis -> axis != Direction.Axis.Y).orElse(true)) {
						return;
					}
				} else {
					return;
				}
			}
		}
	}
}
