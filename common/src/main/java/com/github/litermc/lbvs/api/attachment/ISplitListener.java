package com.github.litermc.lbvs.api.attachment;

import org.valkyrienskies.core.api.ships.ServerShip;

public interface ISplitListener {
	void onServerShipSplit(ServerShip oldShip, ServerShip newShip);
}
