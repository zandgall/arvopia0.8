package com.zandgall.arvopia.tiles;

import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import java.awt.Color;

public class WoodWall extends Tile {
	static Assets woodWall = new Assets(ImageLoader.loadImage("/textures/Tiles/WoodWall.png"), 18, 18, "WoodWall");

	public WoodWall(int x, int y) {
		super(woodWall.get(x, y));
		solid = true;
	}

	public Color getColor() {
		return new Color(150, 75, 0);
	}
}
