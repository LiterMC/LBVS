package com.github.litermc.lbvs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;

public class ShipRandomTickGenerator {
	private final Random rnd = new Random();
	private final ServerLevel level;
	private final ServerShip ship;
	private final int rate;
	private final Ticker blockTicker;

	public ShipRandomTickGenerator(final ServerLevel level, final ServerShip ship, final int rate, final Ticker blockTicker) {
		this.level = level;
		this.ship = ship;
		this.rate = rate;
		this.blockTicker = blockTicker;
	}

	public void tick() {
		final IShipActiveChunksSet chunksSet = this.ship.getActiveChunksSet();
		chunksSet.forEach((chunkX, chunkZ) -> {
			final LevelChunk chunk = level.getChunk(chunkX, chunkZ);
			final LevelChunkSection[] sections = chunk.getSections();
			for (int i = 0; i < sections.length; i++) {
				final LevelChunkSection section = sections[i];
				final SectionPos spos = SectionPos.of(chunk.getPos(), chunk.getSectionYFromSectionIndex(i));
				for (int j = 0; j < this.rate; j++) {
					// section size is 16*16*16
					final int n = this.rnd.nextInt();
					final int x = n & 0xf, z = (n >> 8) & 0xf, y = (n >> 16) & 0xf;
					final BlockState state = section.getBlockState(x, y, z);
					this.blockTicker.tick(this.level, this.ship, spos.origin().offset(x, y, z), state);
				}
			}
		});
	}

	@FunctionalInterface
	public static interface Ticker {
		void tick(ServerLevel level, ServerShip ship, BlockPos pos, BlockState state);
	}
}
