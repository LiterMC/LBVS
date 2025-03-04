package com.github.litermc.lbvs.platform.services;

import net.minecraft.server.MinecraftServer;

public interface IPlatformHelper {
	/**
	 * Checks if a mod with given id is loaded.
	 *
	 * @param modId The mod's id to check if it is loaded.
	 * @return {@code true} if the mod is loaded, {@code false} otherwise.
	 */
	boolean isModLoaded(String modId);

	/**
	 * Get global {@link MinecraftServer} instance
	 *
	 * @return The global {@link MinecraftServer} instance, {@code null} if the environment does not contain a server.
	 */
	MinecraftServer getMinecraftServer();
}
