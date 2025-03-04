package com.github.litermc.lbvs.mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.valkyrienskies.mod.common.util.SplitHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplitHandler.class)
public class MixinSplitHandler {
	@Inject(method = "split", at = @At("HEAD"), cancellable = true)
	private void split(
			final Level level,
			final int x, final int y, final int z,
			final BlockState oldState,
			final BlockState newState,
			final CallbackInfo ci) {
		ci.cancel();
	}
}
