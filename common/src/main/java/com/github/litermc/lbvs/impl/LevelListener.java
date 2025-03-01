package com.github.litermc.lbvs.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import com.github.litermc.lbvs.util.AssembleUtil;

public class LevelListener {
	private static boolean ignoreBlockUpdate = false;

	public static void onBlockUpdated(
			final ServerLevel level, final BlockPos pos,
			final BlockState oldState, final BlockState newState,
			final boolean moving) {
		if (oldState.isAir()) {
			return;
		}
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
			onWorldBlockRemoved(level, pos, oldState, moving);
		}
	}

	private static void onShipBlockUpdated(
			final ServerLevel level, final BlockPos pos, ServerShip ship,
			final BlockState oldState, final BlockState newState,
			final boolean moving) {
		System.out.println("block removed: " + level + "@" + pos + " (" + ship + ") " + " old: " + oldState + ", new: " + newState);
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
						AssembleUtil.assembleTree(level, pos, oldState);
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
