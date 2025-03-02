package com.github.litermc.lbvs.impl;

import net.minecraft.server.level.ServerLevel;
import org.valkyrienskies.core.api.ships.ServerShip;

import com.github.litermc.lbvs.impl.attachment.DecayAttachment;

public final class EventHandler {
	public static void onTreeAssembled(final ServerLevel level, final ServerShip ship) {
		ship.setSlug(DecayAttachment.DECAY_PREFIX + "tree-" + System.identityHashCode(ship));
		ship.saveAttachment(DecayAttachment.class, new DecayAttachment(level, ship));
	}
}
