package com.zandgall.arvopia.tiles;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Bridge extends Tile {

	public Bridge(BufferedImage image, String id) {
		super(image, id);
		top_solid = true;
	}

	public static void loadAllBridgeTiles() {
		Assets sheet = new Assets(ImageLoader.loadImage("/textures/Tiles/Bridge.png"), 18, 18, "Bridge");
		Assets kernelSheet = new Assets(ImageLoader.loadImage("/textures/Tiles/bridgereference.png"), 3, 3, "bridgereference");
		for(int i = 0; i < 4; i++ ){
			for(int j = 0; j < 2; j++) {
				Bridge b = new Bridge(sheet.get(i, j), "BRIDGE" + (i*16 + j));
				BufferedImage k = kernelSheet.get(i, j);
				b.setValidityKernel(
						k.getRGB(0, 0), k.getRGB(1, 0), k.getRGB(2, 0),
						k.getRGB(0, 1), Color.YELLOW.getRGB(), k.getRGB(2, 1),
						k.getRGB(0, 2), k.getRGB(1, 2), k.getRGB(2, 2));
			}
		}
	}

	public Color getColor() {
		return Color.orange;
	}
}
