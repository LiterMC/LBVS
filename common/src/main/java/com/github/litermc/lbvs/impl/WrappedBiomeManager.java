package com.github.litermc.lbvs.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;

import org.valkyrienskies.mod.common.VSGameUtilsKt;

import com.github.litermc.lbvs.impl.accessor.IBiomeManagerAccessor;

public class WrappedBiomeManager extends BiomeManager {
	private final Level level;

	public WrappedBiomeManager(Level level, BiomeManager.NoiseBiomeSource source, long seed) {
		super(source, seed);
		this.level = level;
	}

	@Override
	public BiomeManager withDifferentSource(BiomeManager.NoiseBiomeSource source) {
		return new WrappedBiomeManager(source instanceof Level level ? level : this.level, source, IBiomeManagerAccessor.getBiomeZoomSeedFrom(this));
	}

	@Override
	public Holder<Biome> getBiome(BlockPos pos) {
		Vec3 worldPos = VSGameUtilsKt.toWorldCoordinates(this.level, Vec3.atCenterOf(pos));
		BlockPos blockPos = BlockPos.containing(worldPos);
		return super.getBiome(blockPos);
	}
}
