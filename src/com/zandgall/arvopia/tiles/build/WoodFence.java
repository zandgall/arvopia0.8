package com.zandgall.arvopia.tiles.build;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class WoodFence extends Building {
	public WoodFence(Handler game, int x, int y) {
		super(ImageLoader.loadImage("/textures/Tiles/WoodFenceTileset.png"), game, x, y);
	}

	public void tick(Handler game) {
	}

	public void init() {
	}

	public void reset() {
	}

	public Color getColor() {
		return new Color(100, 50, 0);
	}

	public Point getType(ArrayList<Building> types) {
		boolean left = false;
		boolean right = false;
		boolean top = false;
		boolean bottom = false;

		for (Building b : types) {
			if ((b.getX() == x - 1) && (b.getY() == y)) {
				left = true;
			} else if ((b.getX() == x + 1) && (b.getY() == y)) {
				right = true;
			}
			if ((b.getY() == y - 1) && (b.getX() == x)) {
				top = true;
			} else if ((b.getY() == y + 1) && (b.getX() == x)) {
				bottom = true;
			}
		}
		int nx = 1;
		int ny = 1;

		if (!bottom)
			ny = 2;
		if (!top)
			ny = 0;
		if (!left)
			nx = 0;
		if (!right) {
			nx = 2;
		}
		return new Point(nx, ny);
	}
}
