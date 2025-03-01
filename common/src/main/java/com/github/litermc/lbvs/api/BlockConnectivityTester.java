package com.github.litermc.lbvs.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface BlockConnectivityTester {
	/**
	 * test if the block can connect to given direction.
	 *
	 * @param level the level the block is in
	 * @param state the {@link BlockState}
	 * @param pos   the block's position
	 * @param dir   the direction need to test on
	 *
	 * @return {@link Boolean.TRUE} if the block can connect,
	 *     {@link Boolean.FALSE} if cannot connect,
	 *     or {@code null} if unknown (which means need to pass to parent tester).
	 */
	Boolean canBlockConnect(ServerLevel level, BlockState state, BlockPos pos, Direction dir);
}
