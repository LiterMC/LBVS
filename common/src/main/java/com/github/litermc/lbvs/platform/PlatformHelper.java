package com.github.litermc.lbvs.platform;

import net.minecraft.server.MinecraftServer;

public final class PlatformHelper {
	private PlatformHelper() {}

	public static boolean isModLoaded(String modId) {
		return LBVSServices.PLATFORM.isModLoaded(modId);
	}

	public static MinecraftServer getMinecraftServer() {
		return LBVSServices.PLATFORM.getMinecraftServer();
	}
} 
