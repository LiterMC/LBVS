package com.github.litermc.lbvs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.litermc.lbvs.impl.connectivity.DefaultConnectivitySetup;
import com.github.litermc.lbvs.platform.LBVSServices;

public class LBVSMod {
	public static final String MOD_ID = "lbvs";
	public static final String MOD_NAME = "LBVS";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static void init() {
		LBVSServices.noop();
		DefaultConnectivitySetup.registerDefault();
	}

	public static void initClient() {
		// ...
	}
}
