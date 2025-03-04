package com.github.litermc.lbvs.platform;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.stream.StreamSupport;

public final class PlatformHelper {
	private PlatformHelper() {}

	public static boolean isModLoaded(final String modId) {
		return LBVSServices.PLATFORM.isModLoaded(modId);
	}

	public static MinecraftServer getMinecraftServer() {
		return LBVSServices.PLATFORM.getMinecraftServer();
	}

	public static ServerLevel getLevelByVSDimension(final MinecraftServer server, final String id) {
		return StreamSupport.stream(server.getAllLevels().spliterator(), false)
			.filter(lvl -> id.equals(VSGameUtilsKt.getDimensionId(lvl)))
			.findFirst()
			.orElse(null);
	}
}
