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
import com.github.litermc.lbvs.util.EnumSetProperty;

import java.util.stream.Stream;

public class LevelListener {
	private static final EnumSetProperty<Direction> VIOLATE_FACES = new EnumSetProperty<>("vs_violate_faces", Direction.class);

	private static final Vec3i[] CORNER_OFFSETS = new Vec3i[]{
		/*new Vec3i(0, 0, 0),*/ new Vec3i(0, 0, 1), new Vec3i(0, 0, -1),
		new Vec3i(0, 1, 0), new Vec3i(0, 1, 1), new Vec3i(0, 1, -1),
		new Vec3i(0, -1, 0), new Vec3i(0, -1, 1), new Vec3i(0, -1, -1),
		new Vec3i(1, 0, 0), new Vec3i(1, 0, 1), new Vec3i(1, 0, -1),
		new Vec3i(1, 1, 0), new Vec3i(1, 1, 1), new Vec3i(1, 1, -1),
		new Vec3i(1, -1, 0), new Vec3i(1, -1, 1), new Vec3i(1, -1, -1),
		new Vec3i(-1, 0, 0), new Vec3i(-1, 0, 1), new Vec3i(-1, 0, -1),
		new Vec3i(-1, 1, 0), new Vec3i(-1, 1, 1), new Vec3i(-1, 1, -1),
		new Vec3i(-1, -1, 0), new Vec3i(-1, -1, 1), new Vec3i(-1, -1, -1),
	};

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
			final ServerLevel level, final BlockPos pos, ServerShip ship,
			final BlockState oldState, final BlockState newState,
			final boolean moving) {
		BlockConnectivityManager manager = BlockConnectivityManager.getInstance();
		SplittingDisablerAttachment splitDisablerAttachment = ship.getAttachment(SplittingDisablerAttachment.class);
		if (splitDisablerAttachment != null && !splitDisablerAttachment.canSplit()) {
			return;
		}
		System.out.println("block updating: " + level + "@" + pos + " (" + ship + ") " + " old: " + oldState + ", new: " + newState);
		Direction.stream()
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

	private static Stream<BlockPos> streamOffsets(final BlockPos origin) {
		return Stream.of(CORNER_OFFSETS).map(origin::offset);
	}
}
