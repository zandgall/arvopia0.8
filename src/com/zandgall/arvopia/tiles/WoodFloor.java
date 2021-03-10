package com.zandgall.arvopia.tiles;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import java.awt.Color;

public class WoodFloor extends Tile {
	static Assets woodFloor = new Assets(ImageLoader.loadImage("/textures/Tiles/WoodFloor.png"), 18, 18, "WoodFloor");

	public int x, y;
	
	public WoodFloor(int id, int x, int y) {
		super(woodFloor.get(x, y), id);
		this.x=x;
		this.y=y;
	}

	public void tick(Handler game, int x, int y) {
	}

	public void init() {
	}

	public boolean isSolid() {
		return true;
	}

	public void reset() {
		woodFloor.reset(ImageLoader.loadImage("/textures/Tiles/WoodFloor.png"), 18, 18, "WoodFloor");
	}

	public Color getColor() {
		return new Color(50, 25, 0);
	}
}
