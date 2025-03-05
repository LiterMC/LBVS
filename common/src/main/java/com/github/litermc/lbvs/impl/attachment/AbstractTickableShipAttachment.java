package com.github.litermc.lbvs.impl.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.minecraft.server.level.ServerLevel;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ServerTickListener;

import com.github.litermc.lbvs.platform.PlatformHelper;

public abstract class AbstractTickableShipAttachment extends AbstractShipAttachment implements ServerTickListener {
	@JsonProperty("new")
	private boolean isNew;

	protected AbstractTickableShipAttachment() {
		super();
		this.isNew = false;
	}

	public AbstractTickableShipAttachment(final ServerLevel level, final ServerShip ship) {
		super(level, ship);
		this.isNew = true;
	}

	public void onFirstTick(final ServerLevel level, final ServerShip ship) {}

	public abstract void onServerTick(final ServerLevel level, final ServerShip ship);

	@Override
	public final void onServerTick() {
		final ServerLevel level = this.getLevel();
		final ServerShip ship = this.getShip();
		if (ship == null) {
			return;
		}
		if (this.isNew) {
			this.isNew = false;
			PlatformHelper.queuePostTick(() -> this.onFirstTick(level, ship));
		}
		PlatformHelper.queuePostTick(() -> this.onServerTick(level, ship));
	}
}
