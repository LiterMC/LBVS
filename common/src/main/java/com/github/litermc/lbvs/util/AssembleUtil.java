package com.github.litermc.lbvs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.valkyrienskies.mod.common.assembly.ShipAssemblyKt;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class AssembleUtil {
	private AssembleUtil() {}

	private static final Direction[] NO_UP_DIRECTIONS = new Direction[]{
		Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST,
	};
	private static final Direction[] NO_DOWN_DIRECTIONS = new Direction[]{
		Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST,
	};
	private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{
		Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST,
	};
	private static final Vec3i[] NO_DOWN_CORNER_OFFSETS = Direction26.streamAllOffsets().filter(p -> p.getY() >= 0).toArray(Vec3i[]::new);

	public static void assembleTree(final ServerLevel level, final BlockPos pos, final BlockState original) {
		final List<BlockPos> blocks = findTreeBlocks(level, pos, original.getBlock());
		if (blocks.size() == 0) {
			return;
		}
		DenseBlockPosSet blockSet = new DenseBlockPosSet();
		blocks.forEach(p -> {
			blockSet.add(p.getX(), p.getY(), p.getZ());
		});
		ShipAssemblyKt.createNewShipWithBlocks(blocks.get(0), blockSet, level);
	}

	private static List<BlockPos> findTreeBlocks(final ServerLevel level, final BlockPos pos, final Block block) {
		final List<BlockPos> blocks = findTreeLogs(level, pos, block);
		final Set<BlockPos> logs = new HashSet<>(blocks);
		final Set<BlockPos> accessed = new HashSet<>(logs);
		final Queue<Pair<BlockPos, Integer>> queue = new ArrayDeque<>();
		accessed.add(pos);
		Stream.of(HORIZONTAL_DIRECTIONS)
			.map(pos::relative)
			.filter(Predicate.not(accessed::contains))
			.peek(accessed::add)
			.map(p -> new Pair(p, 0))
			.forEach(queue::add);
		for (BlockPos logPos : blocks) {
			Stream.of(NO_DOWN_DIRECTIONS)
				.map(logPos::relative)
				.filter(Predicate.not(accessed::contains))
				.peek(accessed::add)
				.map(p -> new Pair(p, 0))
				.forEach(queue::add);
		}
		while (!queue.isEmpty()) {
			Pair<BlockPos, Integer> pair = queue.remove();
			BlockPos p = pair.left();
			int dist = pair.right() + 1;
			BlockState st = level.getBlockState(p);
			if (!st.is(BlockTags.LEAVES) || st.getOptionalValue(LeavesBlock.PERSISTENT).orElse(true)) {
				continue;
			}
			boolean invalid = Direction.stream()
				.map(p::relative)
				.filter(Predicate.not(logs::contains))
				.map(level::getBlockState)
				.anyMatch(s1 -> s1.is(BlockTags.OVERWORLD_NATURAL_LOGS));
			if (invalid) {
				continue;
			}
			blocks.add(p);
			if (dist <= 7) {
				Direction.stream()
					.map(p::relative)
					.filter(Predicate.not(accessed::contains))
					.peek(accessed::add)
					.map(p1 -> new Pair(p1, dist))
					.forEach(queue::add);
			}
		}
		return blocks;
	}

	private static List<BlockPos> findTreeLogs(final ServerLevel level, final BlockPos pos, final Block block) {
		final Set<BlockPos> accessed = new HashSet<>();
		final List<BlockPos> blocks = new ArrayList<>();
		final Deque<BlockPos> deque = new ArrayDeque<>();
		deque.addLast(pos.above());
		while (!deque.isEmpty()) {
			BlockPos p = deque.removeLast();
			BlockState st = level.getBlockState(p);
			if (st.getBlock() != block) {
				continue;
			}
			blocks.add(p);
			Stream.of(NO_DOWN_CORNER_OFFSETS)
				.map(p::offset)
				.filter(Predicate.not(accessed::contains))
				.peek(accessed::add)
				.forEach(deque::addLast);
		}
		return blocks;
	}
}
