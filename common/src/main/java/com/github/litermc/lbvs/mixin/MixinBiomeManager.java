package com.github.litermc.lbvs.mixin;

import net.minecraft.world.level.biome.BiomeManager;

import com.github.litermc.lbvs.impl.accessor.IBiomeManagerAccessor;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BiomeManager.class)
public abstract class MixinBiomeManager implements IBiomeManagerAccessor {
	@Shadow
	@Final
	private long biomeZoomSeed;

	@Override
	public long getBiomeZoomSeed() {
		return this.biomeZoomSeed;
	}
}
