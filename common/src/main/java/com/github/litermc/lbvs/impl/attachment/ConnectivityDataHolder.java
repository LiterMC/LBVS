package com.github.litermc.lbvs.impl.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import com.github.litermc.lbvs.api.attachment.ISplitListener;
import com.github.litermc.lbvs.util.Direction26;
import com.github.litermc.lbvs.util.Ref;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

public final class ConnectivityDataHolder implements ISplitListener {
	private final Long2ObjectOpenHashMap<EnumSet<Direction26>> anchables = new Long2ObjectOpenHashMap<>();

	public void onBeforeShipSplit(ServerShip oldShip, List<BlockPos> splitting, Consumer<Consumer<ServerShip>> listener) {
		listener.accept((newShip) -> {
			//
		});
	}

	public EnumSet<Direction26> getAnchables(final BlockPos pos) {
		return this.anchables.computeIfAbsent(pos.asLong(), (p) -> EnumSet.noneOf(Direction26.class));
	}

	public void setAnchables(final BlockPos pos, EnumSet<Direction26> set) {
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
}
