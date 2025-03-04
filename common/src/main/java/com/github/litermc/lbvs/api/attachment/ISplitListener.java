package com.github.litermc.lbvs.api.attachment;

import net.minecraft.core.BlockPos;

import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.List;
import java.util.function.Consumer;

public interface ISplitListener {
	default void onBeforeShipSplit(ServerShip oldShip, List<BlockPos> splitting, Consumer<Consumer<ServerShip>> listenerConsumer) {}

	default void onAfterShipSplit(ServerShip oldShip, ServerShip newShip) {}
}
