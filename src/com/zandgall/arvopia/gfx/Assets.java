package com.zandgall.arvopia.gfx;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import java.awt.image.BufferedImage;

public class Assets {
	private Game g;
	Handler game = new Handler(g);
	private int width;
	private int height;
	BufferedImage sheetfile;
	SpriteSheet sheet;

	public Assets(BufferedImage file, int Width, int Height, String name) {
		sheet = new SpriteSheet(file);
		sheetfile = file;
		width = Width;
		height = Height;

		game.logPlayer("Assets created: " + name);
	}

	public BufferedImage get(int x, int y) {
		return sheet.get(x * width, y * height, width, height);
	}

	public BufferedImage get() {
		return sheetfile;
	}

	public void reset(BufferedImage file, int Width, int Height, String name) {
		sheet = new SpriteSheet(file);
		sheetfile = file;
		width = Width;
		height = Height;

		game.logPlayer("Assets re-created: " + name);
	}
}
