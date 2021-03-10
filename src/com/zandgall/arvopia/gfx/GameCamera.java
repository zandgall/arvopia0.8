package com.zandgall.arvopia.gfx;

import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.worlds.World;

public class GameCamera {
	private Handler handler;
	public double xOffset, yOffset;
	public double NcenterOffx, NcenterOffy;
	public double centerOffx, centerOffy;
	private float amplitude;

	public GameCamera(Handler handler, float xOffset, float yOffset) {
		this.handler = handler;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public void checkBlankSpace() {
		if (xOffset < -(Game.scale - 1.0D) * handler.getWidth() / (2.0D * Game.scale))
			xOffset = ((float) (-(Game.scale - 1.0D) * handler.getWidth() / (2.0D * Game.scale)));
		if (xOffset > World.getWidth() * Tile.TILEWIDTH - handler.getWidth()
				+ handler.getWidth() * (Game.scale - 1.0D) / 2.75D)
			xOffset = ((float) (World.getWidth() * Tile.TILEWIDTH - handler.getWidth()
					+ handler.getWidth() * (Game.scale - 1.0D) / 2.75D));
		if (yOffset > -(8 - (World.getHeight() - 15)) * Tile.TILEHEIGHT
				+ (Game.scale - 1.0D) * handler.getHeight() / 2.0D)
			yOffset = ((float) (-(8 - (World.getHeight() - 15)) * Tile.TILEHEIGHT
					+ (Game.scale - 1.0D) * handler.getHeight() / 2.0D));
		if (yOffset < -(Game.scale - 1.0D) * handler.getHeight() / (2.0D * Game.scale))
			yOffset = ((float) (-(Game.scale - 1.0D) * handler.getHeight() / (2.0D * Game.scale)));
	}

	public void setCenter(Entity e) {
		centerOffx = ((e.getX() - handler.getWidth() / 2 + e.getWidth() / 2));
		centerOffy = ((e.getY() - handler.getHeight() / 2 + e.getHeight() / 2));
	}
	
	public void apply() {
		
		float ratio = 0.01f;
		
		xOffset = (xOffset*(1f-ratio)+centerOffx*ratio);
		yOffset = (yOffset*(1f-ratio)+centerOffy*ratio);
		
		if((""+xOffset).equals("NaN")) {
			xOffset = centerOffx;
			yOffset = centerOffy;
		}
		checkBlankSpace();
	}
	
	public void centerOnEntity(Entity e) {
		double nxOffset = ((e.getX() - handler.getWidth() / 2 + e.getWidth() / 2));
		double nyOffset = ((e.getY() - handler.getHeight() / 2 + e.getHeight() / 2));
		
		float ratio = 0.05f;
		
		xOffset = (xOffset*(1f-ratio)+nxOffset*ratio);
		yOffset = (yOffset*(1f-ratio)+nyOffset*ratio);
		
		if((""+xOffset).equals("NaN")) {
			xOffset = nxOffset;
			yOffset = nyOffset;
		}
		move((float)(Math.random()-0.5)*amplitude, (float)(Math.random()-0.5)*amplitude);
		checkBlankSpace();
	}

	public void move(float x, float y) {
		xOffset += x;
		yOffset += y;
		checkBlankSpace();
	}

	public double getxOffset() {
		return (int) Math.round(xOffset);
//		return 0;
	}

	public void setxOffset(double xOffset) {
		this.xOffset = xOffset;
	}

	public double getyOffset() {
		return (int) Math.round(yOffset);
//		return 0;
	}

	public void setyOffset(double yOffset) {
		this.yOffset = yOffset;
	}

	public void setRumble(float amplitude) {
		this.amplitude = amplitude;
	}
}
