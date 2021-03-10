package com.zandgall.arvopia.tiles.build;

import com.zandgall.arvopia.Handler;

public abstract class Building {
	private java.awt.image.BufferedImage texture;
	protected static int[][] snowy;
	protected Handler game;
	protected int y;
	protected int x;
	public static final int DEFAULT_HEIGHT = 18;
	public static final int DEFAULT_WIDTH = 18;
	public static int TILEHEIGHT = 18;
	public static int TILEWIDTH = 18;

	public Building(java.awt.image.BufferedImage texture, Handler game, int x, int y) {
		this.texture = texture;

		this.game = game;

		this.x = x;
		this.y = y;
	}

	public static void set(int width, int height) {
		snowy = new int[width][height];
	}

	public abstract void tick(Handler paramHandler);

	public abstract void init();

	public abstract void reset();

	public void render(java.awt.Graphics g, java.util.ArrayList<Building> local) {
		x *= 18;
		y *= 18;
		g.drawImage(texture.getSubimage(x, y, TILEWIDTH - 1, TILEHEIGHT - 1), (int) (x * TILEWIDTH - game.xOffset()),
				(int) (y * TILEHEIGHT - game.yOffset()), TILEWIDTH, TILEHEIGHT, null);
	}

	public boolean isSolid() {
		return false;
	}

	public boolean varietable() {
		return false;
	}

	public boolean tickable() {
		return false;
	}

	public int snowyness(int x, int y) {
		return snowy[x][y];
	}

	public boolean isTop() {
		return false;
	}

	public abstract java.awt.Color getColor();

	public abstract java.awt.Point getType(java.util.ArrayList<Building> paramArrayList);

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
