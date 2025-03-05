package com.github.litermc.lbvs.forge.platform;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import com.github.litermc.lbvs.platform.services.IPlatformHelper;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod.EventBusSubscriber
public final class ForgePlatformHelper implements IPlatformHelper {
	private static final Queue<Runnable> preTickQueue = new ConcurrentLinkedQueue<>();
	private static final Queue<Runnable> postTickQueue = new ConcurrentLinkedQueue<>();

	public ForgePlatformHelper() {}

	@Override
	public boolean isModLoaded(final String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public MinecraftServer getMinecraftServer() {
		return ServerLifecycleHooks.getCurrentServer();
	}

	@Override
	public void queuePostTick(Runnable task) {
		postTickQueue.add(task);
	}

	@SubscribeEvent
	public static void serverTick(TickEvent.ServerTickEvent event) {
		switch (event.phase) {
			case START -> {}
			case END -> {
				for (int remain = postTickQueue.size(); remain > 0; remain--) {
					final Runnable task = postTickQueue.poll();
					task.run();
				}
			}
		}
	}
}
