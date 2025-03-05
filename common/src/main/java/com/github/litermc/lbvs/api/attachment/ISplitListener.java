package com.github.litermc.lbvs.api.attachment;

import net.minecraft.core.BlockPos;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;
import org.valkyrienskies.core.util.datastructures.IBlockPosSet;

import java.util.List;
import java.util.function.Consumer;

public interface ISplitListener {
	default void onBeforeShipSplit(ServerShip oldShip, IBlockPosSet splitting, Consumer<Consumer<ServerShip>> onAfterSplit) {}

	default void onAfterShipSplit(ServerShip oldShip, ServerShip newShip) {}
}
