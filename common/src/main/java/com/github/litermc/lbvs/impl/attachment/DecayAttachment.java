package com.github.litermc.lbvs.impl.attachment;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ServerTickListener;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import com.github.litermc.lbvs.util.ShipRandomTickGenerator;

public final class DecayAttachment implements ServerTickListener {
	public static final String DECAY_PREFIX = "+decay+";

	private final int decayRate = 5;
	private int forceDecayCounter;
	private transient ServerLevel level;
	private transient ServerShip ship;
	private transient ShipRandomTickGenerator rnd;

	public DecayAttachment() {
		this(null, null);
	}

	public DecayAttachment(ServerLevel level, ServerShip ship) {
		this.forceDecayCounter = 20 * 60 * 10; // 10 min
		this.setShip(level, ship);
	}

	public void setShip(ServerLevel level, ServerShip ship) {
		this.level = level;
		this.ship = ship;
		this.rnd = ship == null ? null : new ShipRandomTickGenerator(level, ship, this.decayRate, DecayAttachment::onRandomTick);
	}

	@Override
	public void onServerTick() {
		ServerShip ship = this.ship;
		if (ship == null) {
			return;
		}
		String slug = ship.getSlug();
		if (slug == null || !slug.startsWith(DECAY_PREFIX)) {
			ship.saveAttachment(this.getClass(), null);
			return;
		}
		this.forceDecayCounter--;
		if (this.forceDecayCounter <= 0) {
			ship.saveAttachment(this.getClass(), null);
			VSGameUtilsKt.getShipObjectWorld(this.level).deleteShip(ship);
			return;
		}
		this.rnd.tick();
	}

	private static void onRandomTick(ServerLevel level, ServerShip ship, BlockPos pos, BlockState state) {
		if (!state.isAir()) {
			destroyBlock(level, ship, pos);
		}
	}

	private static void destroyBlock(ServerLevel level, ServerShip ship, BlockPos pos) {
		level.destroyBlock(pos, true);
	}
}
