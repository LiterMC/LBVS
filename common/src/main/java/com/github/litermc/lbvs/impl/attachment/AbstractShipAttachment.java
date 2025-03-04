package com.github.litermc.lbvs.impl.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.mod.common.IShipObjectWorldServerProvider;

import com.github.litermc.lbvs.platform.PlatformHelper;

public abstract class AbstractShipAttachment {
	private ResourceKey<Level> levelId;
	@JsonProperty("ship")
	private long shipId;
	@JsonIgnore
	private transient boolean inited = false;
	@JsonIgnore
	private transient ServerLevel level;
	@JsonIgnore
	private transient ServerShip ship;

	protected AbstractShipAttachment() {
		this.levelId = null;
		this.shipId = 0;
		this.level = null;
		this.ship = null;
	}

	public AbstractShipAttachment(ServerLevel level, ServerShip ship) {
		this.level = level;
		this.levelId = level.dimension();
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
		final ServerShipWorldCore core = ((IShipObjectWorldServerProvider)(server)).getShipObjectWorld();
		this.level = server.getLevel(this.levelId);
		this.ship = core.getAllShips().getById(this.shipId);
		if (this.ship != null) {
			this.inited = true;
			this.afterInit();
			return true;
		}
		return false;
	}

	@JsonProperty("level")
	public final String getLevelId() {
		return this.levelId.registry().toString() + "/" + this.levelId.location().toString();
	}

	@JsonProperty("level")
	public final void setLevelId(final String level) {
		int i = level.indexOf('/');
		this.levelId = ResourceKey.create(
			ResourceKey.createRegistryKey(new ResourceLocation(level.substring(0, i))),
			new ResourceLocation(level.substring(i + 1)));
	}

	public final ServerLevel getLevel() {
		this.tryInit();
		return this.level;
	}

	public final ServerShip getShip() {
		this.tryInit();
		return this.ship;
	}
}
