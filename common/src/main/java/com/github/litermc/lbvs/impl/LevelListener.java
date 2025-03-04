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
import com.github.litermc.lbvs.impl.attachment.ConnectivityDataHolder;
import com.github.litermc.lbvs.util.AssembleUtil;
import com.github.litermc.lbvs.util.Direction26;

import java.util.EnumSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LevelListener {
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
			final ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos(level, pos);
			if (ship != null) {
				onShipBlockUpdated(level, pos, ship, oldState, newState, moving);
			} else {
				onWorldBlockUpdated(level, pos, oldState, newState, moving);
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
		return;
	}

	private static void onShipBlockUpdated(
			final ServerLevel level, final BlockPos pos, final ServerShip ship,
			final BlockState oldState, final BlockState newState,
			final boolean moving) {
		final BlockConnectivityManager manager = BlockConnectivityManager.getInstance();
		final SplittingDisablerAttachment splitDisablerAttachment = ship.getAttachment(SplittingDisablerAttachment.class);
		if (splitDisablerAttachment != null && !splitDisablerAttachment.canSplit()) {
			return;
		}
		System.out.println("block updating: " + level + "@" + pos + " (" + ship + ") " + " old: " + oldState + ", new: " + newState);
		ConnectivityDataHolder holder = ship.getAttachment(ConnectivityDataHolder.class);
		if (holder == null) {
			holder = new ConnectivityDataHolder();
			ship.saveAttachment(ConnectivityDataHolder.class, holder);
		}
		final EnumSet<Direction26> anchables = holder.getAnchables(pos);
		if (manager.shouldCheckConnectivity(level, pos, oldState, newState)) {
			anchables.clear();
			Direction26.stream()
				.filter(dir -> manager.canAnchor(level, pos, dir))
				.forEach(anchables::add);
		}
		System.out.println("anchables: " + anchables);
	}

	private static void onWorldBlockRemoved(
			final ServerLevel level, final BlockPos pos,
			final BlockState oldState, final boolean moving) {
		if (oldState.is(BlockTags.OVERWORLD_NATURAL_LOGS)) {
			if (oldState.getOptionalValue(RotatedPillarBlock.AXIS).map(axis -> axis != Direction.Axis.Y).orElse(true) || moving) {
				return;
			}
			final BlockPos.MutableBlockPos leafPos = pos.mutable();
			final int maxY = Math.min(pos.getY() + 32, level.getMaxBuildHeight());
			for (int y = pos.getY() + 1; y <= maxY; y++) {
				leafPos.setY(y);
				final BlockState st = level.getBlockState(leafPos);
				if (st.is(BlockTags.LEAVES)) {
					if (!st.getOptionalValue(LeavesBlock.PERSISTENT).orElse(true)) {
						final ServerShip ship = AssembleUtil.assembleTree(level, pos, oldState);
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
