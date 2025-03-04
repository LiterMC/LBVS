package com.github.litermc.lbvs.platform;

import com.github.litermc.lbvs.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public final class LBVSServices {
	private LBVSServices() {}

	public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

	private static <T> T load(Class<T> clazz) {
		final T loadedService = ServiceLoader.load(clazz)
			.findFirst()
			.orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
		return loadedService;
	}

	public static void noop() {}
}
