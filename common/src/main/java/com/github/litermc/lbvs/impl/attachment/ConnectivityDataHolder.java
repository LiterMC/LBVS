package com.github.litermc.lbvs.impl.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.util.datastructures.IBlockPosSet;

import com.github.litermc.lbvs.api.BlockConnectivityManager;
import com.github.litermc.lbvs.api.attachment.ISplitListener;
import com.github.litermc.lbvs.util.Direction26;
import com.github.litermc.lbvs.util.Ref;

import org.joml.Vector3ic;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public final class ConnectivityDataHolder extends AbstractTickableShipAttachment implements ISplitListener {
	@JsonIgnore
	private final Long2ObjectOpenHashMap<EnumSet<Direction26>> anchables;
	// TODO: save ship connectivity status
	private boolean needInit = true;

	private ConnectivityDataHolder() {
		super();
		this.anchables = new Long2ObjectOpenHashMap<>();
	}

	private ConnectivityDataHolder(
			final ServerLevel level,
			final ServerShip ship,
			final Long2ObjectOpenHashMap<EnumSet<Direction26>> anchables) {
		super(level, ship);
		this.anchables = anchables;
	}

	public ConnectivityDataHolder(final ServerLevel level, final ServerShip ship) {
		this(level, ship, new Long2ObjectOpenHashMap<>());
	}

	public void onBeforeShipSplit(
			final ServerShip oldShip,
			final IBlockPosSet splitting,
			final Consumer<Consumer<ServerShip>> onAfterSplit) {
		final Long2ObjectOpenHashMap<EnumSet<Direction26>> splited = new Long2ObjectOpenHashMap<>(splitting.size());
		for (final Vector3ic pos : splitting) {
			final long key = BlockPos.asLong(pos.x(), pos.y(), pos.z());
			final EnumSet<Direction26> set = this.anchables.remove(key);
			if (set != null) {
				splited.put(key, set);
			}
		}
		onAfterSplit.accept((newShip) -> {
			final ConnectivityDataHolder newHolder = new ConnectivityDataHolder(this.getLevel(), newShip, splited);
			newHolder.needInit = false;
			newShip.saveAttachment(ConnectivityDataHolder.class, newHolder);
		});
	}

	@JsonProperty("anchables")
	public String serializeAnchables() {
		return "";
	}

	@JsonProperty("anchables")
	public void deserializeAnchables(final String str) {
		//
	}

	public EnumSet<Direction26> getAnchables(final BlockPos pos) {
		return this.anchables.computeIfAbsent(pos.asLong(), (p) -> EnumSet.noneOf(Direction26.class));
	}

	public void setAnchables(final BlockPos pos, final EnumSet<Direction26> set) {
		this.anchables.put(pos.asLong(), set);
	}

	public boolean getAnchableAt(final BlockPos pos, final Direction26 dir) {
		final EnumSet<Direction26> set = this.anchables.get(pos.asLong());
		return set != null ? set.contains(dir) : false;
	}

	public void setAnchableAt(final BlockPos pos, final Direction26 dir, final boolean value) {
		final EnumSet<Direction26> set = this.getAnchables(pos);
		if (value) {
			set.add(dir);
		} else {
			set.remove(dir);
		}
	}

	@Override
	public void onServerTick(final ServerLevel level, final ServerShip ship) {
		if (this.needInit) {
			this.onShipCreated(level, ship);
		}
	}

	public void onShipCreated(final ServerLevel level, final ServerShip ship) {
		this.needInit = false;
		final BlockConnectivityManager manager = BlockConnectivityManager.getInstance();
		final Ref<Integer> count = new Ref<>(0);
		ship.getShipActiveChunksSet().forEach((cx, cz) -> {
			final LevelChunk chunk = level.getChunk(cx, cz);
			final LevelChunkSection[] sections = chunk.getSections();
			for (int i = 0; i < sections.length; i++) {
				final LevelChunkSection section = sections[i];
				if (section.hasOnlyAir()) {
					continue;
				}
				final SectionPos spos = SectionPos.of(cx, chunk.getSectionYFromSectionIndex(i), cz);
				final BlockPos origin = spos.origin();
				for (int x = 0; x < LevelChunkSection.SECTION_WIDTH; x++) {
					for (int z = 0; z < LevelChunkSection.SECTION_WIDTH; z++) {
						for (int y = 0; y < LevelChunkSection.SECTION_WIDTH; y++) {
							final BlockState state = section.getBlockState(x, y, z);
							if (state.isAir()) {
								continue;
							}
							count.set(count.get() + 1);
							final EnumSet<Direction26> set = EnumSet.noneOf(Direction26.class);
							final BlockPos pos = origin.offset(x, y, z);
							this.anchables.put(pos.asLong(), set);
							Direction26.stream()
								.filter(dir -> manager.canAnchor(level, pos, dir))
								.forEach(set::add);
						}
					}
				}
			}
		});
	}
}
