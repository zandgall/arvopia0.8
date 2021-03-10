package com.zandgall.arvopia.enviornment;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.ArvopiaLauncher;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.worlds.World;

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
