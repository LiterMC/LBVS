package com.github.litermc.lbvs;

import com.github.litermc.lbvs.impl.connectivity.DefaultConnectivitySetup;

public class LBVSMod {
	public static final String MOD_ID = "lbvs";

	public static void init() {
		DefaultConnectivitySetup.registerDefault();
	}

	public static void initClient() {
		// ...
	}
}
