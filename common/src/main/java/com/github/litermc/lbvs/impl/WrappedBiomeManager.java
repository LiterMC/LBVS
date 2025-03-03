package com.github.litermc.lbvs.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;

import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class WrappedBiomeManager extends BiomeManager {
	private Level level;

	public WrappedBiomeManager(Level level, long seed) {
		super(level, seed);
		this.level = level;
	}

	@Override
	public Holder<Biome> getBiome(BlockPos pos) {
		Vec3 worldPos = VSGameUtilsKt.toWorldCoordinates(this.level, Vec3.atCenterOf(pos));
		return super.getBiome(BlockPos.containing(worldPos));
	}
}
