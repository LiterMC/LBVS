package com.github.litermc.lbvs.mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;

import com.github.litermc.lbvs.impl.WrappedBiomeManager;
import com.github.litermc.lbvs.impl.accessor.IBiomeManagerAccessor;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public class MixinLevel {
	@ModifyExpressionValue(
		method = "<init>",
		at = @At(value = "NEW", target = "Lnet/minecraft/world/level/biome/BiomeManager;")
	)
	private BiomeManager newBiomeManager(final BiomeManager biomeManager) {
		final Level thisLevel = (Level)((Object)(this));
		return new WrappedBiomeManager(thisLevel, thisLevel, IBiomeManagerAccessor.getBiomeZoomSeedFrom(biomeManager));
	}
}
