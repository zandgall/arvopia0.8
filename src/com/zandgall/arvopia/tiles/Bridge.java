package com.zandgall.arvopia.tiles;

import com.zandgall.arvopia.Handler;
import java.awt.Color;

public class Bridge extends Tile {
	public int index, gx = 0, gy = 0;

	public Bridge(int index, int id) {
		super(com.zandgall.arvopia.gfx.PublicAssets.bridge[index], id);
		this.index = index;
		if ((index == 2) || (index == 5) || (index == 7)) {
			TILEHEIGHT = 8;
		}
		
		switch(index) {
		case 0:
			gx=0;
			gy=0;
			break;
		case 1:
			gx=1;
			gy=0;
			break;
		case 2:
			gx=1;
			gy=1;
			break;
		case 3:
			gx=0;
			gy=1;
			break;
		case 4:
			gx=2;
			gy=0;
			break;
		case 5:
			gx=2;
			gy=1;
			break;
		case 6:
			gx=3;
			gy=0;
			break;
		case 7:
			gx=3;
			gy=1;
			break;
		}
			
	}

	public void tick(Handler game, int x, int y) {
	}

	public boolean isTop() {
		if ((index == 2) || (index == 5) || (index == 7)) {
			return true;
		}
		return false;
	}

	public void init() {
	}

	public void reset() {
	}

	public Color getColor() {
		if ((index == 2) || (index == 5) || (index == 7))
			return Color.orange;
		return new Color(100, 50, 10);
	}
}
