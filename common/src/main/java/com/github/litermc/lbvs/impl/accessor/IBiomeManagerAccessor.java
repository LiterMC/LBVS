package com.github.litermc.lbvs.impl.accessor;

import net.minecraft.world.level.biome.BiomeManager;

public interface IBiomeManagerAccessor {
	long getBiomeZoomSeed();

	static long getBiomeZoomSeedFrom(BiomeManager manager) {
		return ((IBiomeManagerAccessor) ((Object) (manager))).getBiomeZoomSeed();
	}
}
