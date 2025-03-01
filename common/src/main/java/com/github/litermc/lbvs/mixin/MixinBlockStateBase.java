package com.github.litermc.lbvs.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import com.github.litermc.lbvs.impl.LevelListener;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockStateBase {
	@Shadow
	protected abstract BlockState asState();

	@Inject(
		method = "onRemove(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V",
		at = @At("TAIL")
	)
	private void onRemove(
			final Level level, final BlockPos pos,
			final BlockState newState, final boolean moving,
			final CallbackInfo ci) {
		if (level instanceof ServerLevel serverLevel) {
			BlockState oldState = this.asState();
			LevelListener.onBlockUpdated(serverLevel, pos, oldState, newState, moving);
		}
	}
}
