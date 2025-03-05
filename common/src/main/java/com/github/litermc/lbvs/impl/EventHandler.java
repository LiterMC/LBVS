package com.github.litermc.lbvs.impl;

import net.minecraft.server.level.ServerLevel;
import org.valkyrienskies.core.api.ships.ServerShip;

import com.github.litermc.lbvs.impl.attachment.ConnectivityDataHolder;
import com.github.litermc.lbvs.impl.attachment.DecayAttachment;

public final class EventHandler {
	public static void onShipCreated(final ServerLevel level, final ServerShip ship) {
		ship.saveAttachment(ConnectivityDataHolder.class, new ConnectivityDataHolder(level, ship));
	}

	public static void onTreeAssembled(final ServerLevel level, final ServerShip ship) {
		final int DEFAULT_FORCE_DECAY_TIMEOUT = 20 * 60 * 10; // 10 min
		ship.setSlug(DecayAttachment.DECAY_PREFIX + "tree-" + System.identityHashCode(ship));
		ship.saveAttachment(DecayAttachment.class, new DecayAttachment(level, ship, DEFAULT_FORCE_DECAY_TIMEOUT));
	}
}
