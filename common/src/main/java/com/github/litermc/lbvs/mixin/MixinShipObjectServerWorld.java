package com.github.litermc.lbvs.mixin;

import net.minecraft.server.level.ServerLevel;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.game.ships.ShipData;
import org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld;

import org.joml.Vector3ic;

import com.github.litermc.lbvs.impl.EventHandler;
import com.github.litermc.lbvs.platform.PlatformHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShipObjectServerWorld.class)
public class MixinShipObjectServerWorld {
	@Inject(method = "createNewShipAtBlock", at = @At("RETURN"), remap = false)
	private void createNewShipAtBlock(
			final Vector3ic worldPos,
			final boolean createShipObjectImmediately,
			final double scale,
			final String levelId,
			final CallbackInfoReturnable<ShipData> ci) {
		final ServerShip ship = ci.getReturnValue();
		final ServerLevel level = PlatformHelper.getLevelByVSDimension(PlatformHelper.getMinecraftServer(), levelId);
		EventHandler.onShipCreated(level, ship);
	}
}
