package com.zandgall.arvopia.tiles;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import java.awt.Color;

public class WoodBackTile extends Tile {
	public WoodBackTile() {
		super(ImageLoader.loadImage("/textures/Tiles/WoodBackTile.png"));
	}

	public Color getColor() {
		return new Color(255, 220, 150);
	}
}
