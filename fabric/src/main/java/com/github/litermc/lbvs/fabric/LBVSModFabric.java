package com.github.litermc.lbvs.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

import com.github.litermc.lbvs.LBVSMod;

public class LBVSModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LBVSMod.init();
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {
        @Override
        public void onInitializeClient() {
            LBVSMod.initClient();
        }
    }
}
