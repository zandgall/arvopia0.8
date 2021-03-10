package com.zandgall.arvopia.tiles;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import java.awt.Color;

public class WoodBackTile extends Tile {
	public WoodBackTile(int id) {
		super(ImageLoader.loadImage("/textures/Tiles/WoodBackTile.png"), id);
	}

	public void tick(Handler game, int x, int y) {
	}

	public void init() {
	}

	public boolean isSolid() {
		return false;
	}

	public void reset() {
	}

	public Color getColor() {
		return new Color(255, 220, 150);
	}
}
