package com.github.litermc.lbvs.impl.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ServerTickListener;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import com.github.litermc.lbvs.api.attachment.ISplitListener;
import com.github.litermc.lbvs.util.Ref;
import com.github.litermc.lbvs.util.ShipRandomTickGenerator;

import com.google.common.collect.ImmutableMap;

public final class DecayAttachment extends AbstractShipAttachment implements ServerTickListener, ISplitListener {
	public static final String DECAY_PREFIX = "+decay+";
	private static final ImmutableMap<Block, Block> STAGED = new ImmutableMap.Builder<Block, Block>()
		.put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG)
		.put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG)
		.put(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG)
		.put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM)
		.put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG)
		.put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG)
		.put(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG)
		.put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG)
		.put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG)
		.put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM)
		.build();

	private final int decayRate = 5;
	@JsonProperty("forceDecayCounter")
	private int forceDecayCounter;
	@JsonIgnore
	private transient ShipRandomTickGenerator rnd;

	private DecayAttachment() {
		super();
		this.forceDecayCounter = 0;
	}

	public DecayAttachment(final ServerLevel level, final ServerShip ship, final int forceDecayCounter) {
		super(level, ship);
		this.forceDecayCounter = forceDecayCounter;
		this.rnd = new ShipRandomTickGenerator(level, ship, this.decayRate, DecayAttachment::onRandomTick);
	}

	@Override
	protected void afterInit() {
		this.rnd = new ShipRandomTickGenerator(this.getLevel(), this.getShip(), this.decayRate, DecayAttachment::onRandomTick);
	}

	@Override
	public void onServerTick() {
		final ServerLevel level = this.getLevel();
		final ServerShip ship = this.getShip();
		if (ship == null) {
			return;
		}
		final String slug = ship.getSlug();
		if (slug == null || !slug.startsWith(DECAY_PREFIX)) {
			ship.saveAttachment(this.getClass(), null);
			return;
		}
		this.forceDecayCounter--;
		if (this.forceDecayCounter < 0) {
			ship.saveAttachment(this.getClass(), null);
			VSGameUtilsKt.getShipObjectWorld(level).deleteShip(ship);
			return;
		}
		this.rnd.tick();
	}

	@Override
	public void onAfterShipSplit(final ServerShip oldShip, final ServerShip newShip) {
		newShip.saveAttachment(DecayAttachment.class, new DecayAttachment(this.getLevel(), newShip, this.forceDecayCounter));
	}

	private static void onRandomTick(final ServerLevel level, final ServerShip ship, final BlockPos pos, final BlockState state) {
		if (!state.isAir()) {
			destroyBlock(level, ship, pos);
		}
	}

	private static void destroyBlock(final ServerLevel level, final ServerShip ship, final BlockPos pos) {
		final BlockState state = level.getBlockState(pos);
		final Block decayedBlock = STAGED.get(state.getBlock());
		if (decayedBlock != null) {
			final Ref<BlockState> newState = new Ref<>(decayedBlock.defaultBlockState());
			state.getValues().forEach((k, v) -> {
				newState.set(DecayAttachment.trySetValue(newState.get(), k, v));
			});
			level.setBlock(pos, newState.get(), 11);
			return;
		}
		level.destroyBlock(pos, true);
	}

	private static <T extends Comparable<T>> BlockState trySetValue(
			final BlockState state, final Property<T> property, final Object value) {
		return state.trySetValue(property, (T) value);
	}
}
