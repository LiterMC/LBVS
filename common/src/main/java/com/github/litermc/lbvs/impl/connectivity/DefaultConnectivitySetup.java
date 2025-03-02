package com.github.litermc.lbvs.impl.connectivity;

import net.minecraft.world.level.block.Block;

import com.github.litermc.lbvs.api.BlockConnectivityManager;
import com.github.litermc.lbvs.api.BlockConnectivityTester;

public final class DefaultConnectivitySetup {
	private DefaultConnectivitySetup() {}

	public static void registerDefault() {
		register(Block.class, new BlockTester());
	}

	private static void register(final Class<? extends Block> blockClass, final BlockConnectivityTester tester) {
		BlockConnectivityManager.registerConnectivityTester(blockClass, tester);
	}
}
