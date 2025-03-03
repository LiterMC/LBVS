package com.github.litermc.lbvs.mixin;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.storage.WritableLevelData;

import com.github.litermc.lbvs.impl.WrappedBiomeManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public class MixinLevel {
	@Shadow
	@Final
	@Mutable
	private BiomeManager biomeManager;

	@Inject(
		method = "<init>",
		at = @At("TAIL")
	)
	private void init(
		WritableLevelData levelData,
		ResourceKey levelId,
		RegistryAccess registry,
		Holder dimensionType,
		Supplier profiler,
		boolean isClient,
		boolean isDebug,
		long biomeSeed,
		int maxChainedUpdate,
		CallbackInfo ci
	) {
		this.biomeManager = new WrappedBiomeManager((Level)((Object)(this)), biomeSeed);
	}
}
