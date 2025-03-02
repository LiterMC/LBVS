package com.github.litermc.lbvs.impl.connectivity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

import com.github.litermc.lbvs.api.BlockConnectivityTester;
import com.github.litermc.lbvs.util.Direction26;

public class BlockTester implements BlockConnectivityTester {
	private static final double OFFSET_SCALE = 1 - 1e-6;

	@Override
	public Boolean shouldCheckConnectivity(
			final ServerLevel level, final BlockPos pos,
			final BlockState oldState, final BlockState newState) {
		if (oldState.isAir() != newState.isAir()) {
			return true;
		}
		return Shapes.joinIsNotEmpty(
			oldState.getShape(level, pos),
			newState.getShape(level, pos),
			BooleanOp.NOT_SAME
		);
	}

	@Override
	public Boolean canBlockConnect(final ServerLevel level, final BlockState state, final BlockPos pos, final Direction26 dir) {
		if (state.isAir()) {
			return false;
		}
		BlockPos otherPos = dir.offsetOf(pos);
		BlockState other = level.getBlockState(otherPos);
		if (other.isAir()) {
			return false;
		}
		return Shapes.joinIsNotEmpty(
			state.getShape(level, pos),
			other.getShape(level, otherPos).move(dir.getStepX() * OFFSET_SCALE, dir.getStepY() * OFFSET_SCALE, dir.getStepZ() * OFFSET_SCALE),
			BooleanOp.AND
		);
	}
}
