package com.zandgall.arvopia.tiles;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import java.awt.Color;
import java.awt.Graphics;

public class EmptyTile extends Tile {
	public EmptyTile(int id) {
		super(ImageLoader.loadImage("/textures/Null.png"), id);
	}

	public int getY(int xOff) {
		return 19;
	}
	
	public void init() {
	}

	public void reset() {
	}

	public void tick(Handler game, int x, int y) {
	}

	public void render(Graphics g, int x, int y, int gridX, int gridY) {
		
	}
	
	public Color getColor() {
		return new Color(100, 200, 255);
	}
}
