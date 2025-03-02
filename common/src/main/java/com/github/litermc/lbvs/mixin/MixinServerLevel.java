package com.github.litermc.lbvs.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import com.github.litermc.lbvs.impl.LevelListener;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class MixinServerLevel {
	// @Inject(
	// 	method = "tickChunk",
	// 	at = @At(value = "INVOKE_ASSIGN", target = "net.minecraft.world.level.Level;getBlockRandomPos", ordinal = 2)
	// )
	// private void onRandomTick(LevelChunk chunk, int randomTicks, CallbackInfo ci) {
	// 	ci.locals()
	// }
}
