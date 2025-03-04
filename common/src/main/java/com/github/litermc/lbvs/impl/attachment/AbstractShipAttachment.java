package com.github.litermc.lbvs.impl.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.game.ships.ShipObject;
import org.valkyrienskies.mod.common.IShipObjectWorldServerProvider;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import com.github.litermc.lbvs.platform.PlatformHelper;

public abstract class AbstractShipAttachment {
	@JsonProperty("ship")
	private long shipId;
	@JsonIgnore
	private transient boolean inited = false;
	@JsonIgnore
	private transient ServerLevel level;
	@JsonIgnore
	private transient ServerShip ship;

	protected AbstractShipAttachment() {
		this.shipId = 0;
		this.level = null;
		this.ship = null;
	}

	public AbstractShipAttachment(final ServerLevel level, final ServerShip ship) {
		this.level = level;
		this.ship = ship;
		this.shipId = ship.getId();
		this.inited = true;
	}

	protected void afterInit() {}

	protected final boolean tryInit() {
		if (this.inited) {
			return true;
		}
		final MinecraftServer server = PlatformHelper.getMinecraftServer();
		final ServerShipWorldCore core = ((IShipObjectWorldServerProvider) (server)).getShipObjectWorld();
		this.ship = core.getAllShips().getById(this.shipId);
		if (this.ship != null) {
			final String levelId = this.getShipDimensionId();
			this.level = PlatformHelper.getLevelByVSDimension(server, levelId);
			this.inited = true;
			this.afterInit();
			return true;
		}
		return false;
	}

	private String getShipDimensionId() {
		return ((ShipObject) (this.ship)).getChunkClaimDimension();
	}

	public final ServerLevel getLevel() {
		if (this.tryInit()) {
			final String levelId = this.getShipDimensionId();
			if (!levelId.equals(VSGameUtilsKt.getDimensionId(this.level))) {
				this.level = PlatformHelper.getLevelByVSDimension(PlatformHelper.getMinecraftServer(), levelId);
			}
		}
		return this.level;
	}

	public final ServerShip getShip() {
		this.tryInit();
		return this.ship;
	}
}
