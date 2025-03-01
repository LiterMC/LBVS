package com.github.litermc.lbvs.forge;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.github.litermc.lbvs.LBVSMod;

@Mod(LBVSMod.MOD_ID)
public class LBVSModForge {
	public LBVSModForge() {
		final IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
		MOD_BUS.addListener(this::clientSetup);
		LBVSMod.init();
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		LBVSMod.initClient();
	}
}
