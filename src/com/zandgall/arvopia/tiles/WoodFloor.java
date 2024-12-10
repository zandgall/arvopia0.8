package com.zandgall.arvopia.tiles;

import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import java.awt.Color;

public class WoodFloor extends Tile {
	static Assets woodFloor = new Assets(ImageLoader.loadImage("/textures/Tiles/WoodFloor.png"), 18, 18, "WoodFloor");

	public WoodFloor(int x, int y) {
		super(woodFloor.get(x, y));
		solid = true;
	}

	public Color getColor() {
		return new Color(50, 25, 0);
	}
}
