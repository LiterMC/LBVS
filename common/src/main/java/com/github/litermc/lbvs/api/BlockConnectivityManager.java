package com.github.litermc.lbvs.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
		connectivityTesters.put(blockClass, tester);
	}

	public boolean canBlockConnect(final ServerLevel level, final BlockPos pos, final Direction dir) {
		final BlockState state = level.getBlockState(pos);
		if (state == null) {
			return false;
		}
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
