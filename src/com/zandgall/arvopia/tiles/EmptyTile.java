package com.zandgall.arvopia.tiles;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import java.awt.Color;
import java.awt.Graphics;

public class EmptyTile extends Tile {
	public EmptyTile() {
		super(ImageLoader.loadImage("/textures/Null.png"));
	}
	
	public Color getColor() {
		return new Color(100, 200, 255);
	}
}
