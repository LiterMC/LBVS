package com.github.litermc.lbvs.forge.platform;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.server.ServerLifecycleHooks;

import com.github.litermc.lbvs.platform.services.IPlatformHelper;

public final class ForgePlatformHelper implements IPlatformHelper {
	public ForgePlatformHelper() {}

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public MinecraftServer getMinecraftServer() {
		return ServerLifecycleHooks.getCurrentServer();
	}
}
