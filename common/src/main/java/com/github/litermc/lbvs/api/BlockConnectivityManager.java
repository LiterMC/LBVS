package com.github.litermc.lbvs.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.github.litermc.lbvs.util.Direction26;

import java.util.Map;
import java.util.HashMap;

public class BlockConnectivityManager {
	private static final BlockConnectivityManager INSTANCE = new BlockConnectivityManager();

	private static final Map<Class<? extends Block>, BlockConnectivityTester> connectivityTesters = new HashMap<>();

	private BlockConnectivityManager() {}

	public static BlockConnectivityManager getInstance() {
		return INSTANCE;
	}

	public static void registerConnectivityTester(final Class<? extends Block> blockClass, final BlockConnectivityTester tester) {
		if (connectivityTesters.containsKey(blockClass)) {
			throw new IllegalStateException("Block " + blockClass + " is already registered");
		}
		connectivityTesters.put(blockClass, tester);
	}

	public static void forceRegisterConnectivityTester(final Class<? extends Block> blockClass, final BlockConnectivityTester tester) {
		connectivityTesters.put(blockClass, tester);
	}

	public boolean shouldCheckConnectivity(
			final ServerLevel level, final BlockPos pos,
			final BlockState oldState, final BlockState newState) {
		final Block block = oldState.getBlock();
		if (block != newState.getBlock()) {
			return true;
		}
		for (Class<?> blockClass = block.getClass();
				Block.class.isAssignableFrom(blockClass);
				blockClass = blockClass.getSuperclass()) {
			final BlockConnectivityTester tester = connectivityTesters.get(blockClass);
			if (tester != null) {
				final Boolean res = tester.shouldCheckConnectivity(level, pos, oldState, newState);
				if (res != null) {
					return res;
				}
			}
		}
		return true;
	}

	public boolean canBlockConnect(final ServerLevel level, final BlockPos pos, final Direction26 dir) {
		final BlockState state = level.getBlockState(pos);
		final Block block = state.getBlock();
		for (Class<?> blockClass = block.getClass();
				Block.class.isAssignableFrom(blockClass);
				blockClass = blockClass.getSuperclass()) {
			final BlockConnectivityTester tester = connectivityTesters.get(blockClass);
			if (tester != null) {
				final Boolean res = tester.canBlockConnect(level, state, pos, dir);
				if (res != null) {
					return res;
				}
			}
		}
		return false;
	}
}
