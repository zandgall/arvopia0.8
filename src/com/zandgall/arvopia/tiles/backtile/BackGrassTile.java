package com.zandgall.arvopia.tiles.backtile;

import java.awt.Color;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;

public class BackGrassTile extends Backtile {

	public static final Assets assets = new Assets(ImageLoader.loadImage("/textures/Tiles/DirtBacktile.png"), 18, 18, "Dirt Backtile");
	
	int gx, gy;
	
	public BackGrassTile(int id, int x, int y) {
		super(assets.get(x, y), "TILE"+id);
		gx=x;
		gy=y;
	}
	
	public int gridX() {
		return gx;
	}
	
	public int gridY() {
		return gy;
	}

	@Override
	public void tick(Handler paramHandler, int paramInt1, int paramInt2) {

	}

	@Override
	public void init() {
		
	}

	@Override
	public void reset() {
		
	}

	@Override
	public Color getColor() {
		return null;
	} 
	
}
