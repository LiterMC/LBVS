package com.github.litermc.lbvs.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.github.litermc.lbvs.util.Direction26;

public interface BlockConnectivityTester {
	/**
	 * indicate if the block can connect to given direction.
	 *
	 * @param level    the level the block is in
	 * @param pos      the block's position
	 * @param oldState the old {@link BlockState}
	 * @param newState the new {@link BlockState}
	 *
	 * @return {@link Boolean.TRUE} if the block should recheck connectivities,
	 *     {@link Boolean.FALSE} if should not,
	 *     or {@code null} if unknown (which means need to pass to parent tester).
	 */
	Boolean shouldCheckConnectivity(ServerLevel level, BlockPos pos, BlockState oldState, BlockState newState);

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
	Boolean canBlockConnect(ServerLevel level, BlockState state, BlockPos pos, Direction26 dir);
}
