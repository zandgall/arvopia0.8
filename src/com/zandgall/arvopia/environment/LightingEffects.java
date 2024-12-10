package com.zandgall.arvopia.environment;

import java.awt.image.BufferedImage;

public class LightingEffects implements Runnable {

	public static BufferedImage effect = new BufferedImage(720, 405, BufferedImage.TYPE_4BYTE_ABGR);
	
	public static void start() {
		Thread t = new Thread(new LightingEffects());
		t.start();
	}
	
	@Override
	public void run() {
	}
	
}
