package com.zandgall.arvopia.tiles.backtile;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import java.awt.Color;
import java.awt.Graphics;

public class EmptyTile extends Backtile {
	public EmptyTile() {
		super(ImageLoader.loadImage("/textures/Null.png"), "TILE0");
	}

	public void tick(Handler game, int x, int y) {
	}

	@Override
	public void init() {

	}

	@Override
	public void reset() {

	}

	public void render(Graphics g, int x, int y, int gridX, int gridY) {

	}
	
	public Color getColor() {
		return new Color(100, 200, 255);
	}
}
