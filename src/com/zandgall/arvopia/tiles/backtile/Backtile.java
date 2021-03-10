package com.zandgall.arvopia.tiles.backtile;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.state.OptionState;

public abstract class Backtile {
	public static ArrayList<Backtile> tiles = new ArrayList<Backtile>();
	public static Backtile n0 = new EmptyTile(0);
	
	public static Backtile g0 = new BackGrassTile(1, 0, 0);
	public static Backtile g1 = new BackGrassTile(2, 1, 0);
	public static Backtile g2 = new BackGrassTile(3, 2, 0);
	public static Backtile g3 = new BackGrassTile(4, 0, 1);
	public static Backtile g4 = new BackGrassTile(5, 1, 1);
	public static Backtile g5 = new BackGrassTile(6, 2, 1);
	public static Backtile g6 = new BackGrassTile(7, 0, 2);
	public static Backtile g7 = new BackGrassTile(8, 1, 2);
	public static Backtile g8 = new BackGrassTile(9, 2, 2);
	public static Backtile g9 = new BackGrassTile(10, 0, 3);
	public static Backtile g10 = new BackGrassTile(11, 1, 3);
	public static Backtile g11 = new BackGrassTile(12, 2, 3);
	public static Backtile g12 = new BackGrassTile(13, 3, 3);
	
	public static int TILEWIDTH = 18;
	public static int TILEHEIGHT = 18;
	public static final int DEFAULT_WIDTH = 18;
	public static final int DEFAULT_HEIGHT = 18;
	protected int x;
	protected int y;
	protected static int[][] snowy;
	protected String id;
	private BufferedImage texture;

	public Backtile(BufferedImage texture, String id) {
		this.id = id;

		this.texture = texture;

		tiles.add(this);
	}

	public static Backtile getTile(String id) {
		for(Backtile t: tiles) {
			if(t.id.equals(id))
				return t;
		}
		return n0;
	}
	
	public static void set(int width, int height) {
		snowy = new int[width][height];
	}

	public abstract void tick(Handler paramHandler, int paramInt1, int paramInt2);

	public abstract void init();

	public abstract void reset();

	public void render(Graphics g, int x, int y, int gridx, int gridy) {
		if (OptionState.individualtilerender)
			g.drawImage(texture, x, y, TILEWIDTH, TILEHEIGHT, null);
	}
	
	public Image getImage() {
		return texture;
	}
	
	public void overRender(Graphics g, int x, int y) {
		g.drawImage(texture, x, y, TILEWIDTH, TILEHEIGHT, null);
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

	public String getId() {
		return id;
	}

	public abstract Color getColor();
	
}
