package com.zandgall.arvopia.tiles;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import java.awt.Color;

public class WoodWall extends Tile {
	static Assets woodWall = new Assets(ImageLoader.loadImage("/textures/Tiles/WoodWall.png"), 18, 18, "WoodWall");

	public int x, y;
	
	public WoodWall(int id, int x, int y) {
		super(woodWall.get(x, y), id);
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
		woodWall.reset(ImageLoader.loadImage("/textures/Tiles/WoodFloor.png"), 18, 18, "WoodFloor");
	}

	public Color getColor() {
		return new Color(150, 75, 0);
	}
}
