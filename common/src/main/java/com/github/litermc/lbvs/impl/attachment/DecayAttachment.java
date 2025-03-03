package com.github.litermc.lbvs.impl.attachment;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ServerTickListener;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import com.github.litermc.lbvs.util.Ref;
import com.github.litermc.lbvs.util.ShipRandomTickGenerator;

import com.google.common.collect.ImmutableMap;

public final class DecayAttachment implements ServerTickListener {
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
		BlockState state = level.getBlockState(pos);
		Block decayedBlock = STAGED.get(state.getBlock());
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

	private static <T extends Comparable<T>> BlockState trySetValue(BlockState state, Property<T> property, Object value) {
		return state.trySetValue(property, (T) value);
	}
}
