package com.github.litermc.lbvs.impl.connectivity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.github.litermc.lbvs.api.BlockConnectivityTester;

public class BlockTester implements BlockConnectivityTester {
	@Override
	public Boolean canBlockConnect(final ServerLevel level, final BlockState state, final BlockPos pos, final Direction dir) {
		if (state.isAir()) {
			return false;
		}
		BlockPos otherPos = pos.relative(dir);
		BlockState other = level.getBlockState(otherPos);
		if (other.isAir()) {
			return false;
		}
		return Shapes.joinIsNotEmpty(
			state.getShape(level, pos),
			other.getShape(level, otherPos).move(dir.getStepX() * 0.999999, dir.getStepY() * 0.999999, dir.getStepZ() * 0.999999),
			BooleanOp.AND);
	}
}
