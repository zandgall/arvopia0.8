package com.zandgall.arvopia.water;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.Serializable;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.utils.Public;

public class Water implements Serializable {
	private static final long serialVersionUID = -1241915458166316877L;
	Handler game;
	public double calmness = 0.4D;
	public boolean calmer = true;
	int x;
	int y;
	int width;
	int height;
	int resolution;
	double[] xP;
	double[] yP;
	double[] mP;
	int source = 1;
	long sourceTimer = 0L;

	Polygon water;
	Color color;

	public Water(Handler game, int x, int y, int width, int height, int resolution, Color color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.game = game;

		this.resolution = resolution;

		this.color = color;

		source = (resolution / 2 + 1);

		xP = new double[resolution + 2];
		yP = new double[resolution + 2];
		mP = new double[resolution + 2];

		xP[0] = x;
		yP[0] = (y + height);
		xP[(resolution + 1)] = (x + width);
		yP[(resolution + 1)] = (y + height);

		for (int k = 1; k < resolution; k++) {
			yP[k] = y;
		}

		int k = 1;
		for (int j = 0; j < width; j += width / resolution) {
			xP[((int) Public.range(0.0D, resolution, k))] = (j + x);
			k++;
		}

		xP[resolution] = (x + width);
		yP[resolution] = y;

		yP[source] = (y + Public.expandedRand(-5.0D, 5.0D));

		for (int i = 0; i < mP.length; i++) {
			mP[i] = 0.0D;
		}
		mP[source] = Public.expandedRand(-0.5D, 0.5D);

		int[] ix = new int[resolution + 2];
		int[] iy = new int[resolution + 2];

		for (int i = 0; i < resolution + 2; i++) {
			ix[i] = ((int) xP[i]);
			iy[i] = ((int) yP[i]);
		}

		water = new Polygon(ix, iy, resolution + 2);
	}

	public int getNewSource(int x, int y, int radius) {
		for (int i = 1; i < resolution; i++) {
			if (Public.dist(x, y, xP[i], yP[i]) < radius) {
				return i;
			}
		}

		return -1;
	}

	public Rectangle box() {
		return new Rectangle(x, y, width, height);
	}

	public void collision(int x, int y, int width, int height, double force, boolean canComeUp) {
		for (int i = x; i < x + width; i++)
			if ((getNewSource(i, y, 2) != -1) && ((canComeUp) || (y > yP[getNewSource(i, y, 2)]))) {
				calmness = Math.max(calmness, force);
				if (yP[getNewSource(i, y, 10)] < this.y + height * 2) {
					yP[getNewSource(i, y, 10)] = y;
					source = ((int) Public.range(1.0D, resolution, getNewSource(i + width / 2, y, 2)));
					sourceTimer = 0L;
				}
			}
	}

	public void collision(Rectangle bounds) {
		collision(x, y, width, height, 0.5D, true);
	}

	public void tick() {
		double wind = game.getWind(x, y) / 10.0D;
		if (sourceTimer > 100L) {
			if (calmness > Math.max(Math.abs(wind), 0.1D)) {
				calmness -= 0.005D;
				if (Public.difference(calmness, Math.max(Math.abs(wind), 0.1D)) < 0.2D)
					calmness = Math.max(Math.abs(wind), 0.1D);
			} else if (calmness < Math.max(Math.abs(wind), 0.1D)) {
				calmness = Math.max(Math.abs(wind), 0.1D);
			} else if (calmness == Math.max(Math.abs(wind), 0.1D)) {
				if (wind > 0.0D) {
					source = 2;
				} else
					source = resolution;
			}
		}
		if (Public.difference(mP[source], 0.0D) == 0.1D) {
			yP[source] = y;
			mP[source] = 0.0D;
		}

		if ((yP[source] > y) && (mP[source] > -3.0D)) {
			mP[source] -= 0.1D;
		} else if ((yP[source] < y) && (mP[source] < 3.0D)) {
			mP[source] += 0.1D;
		}

		if (Public.difference(yP[source], y) < Math.abs(mP[source])) {
			yP[source] = y;
		} else {
			yP[source] += mP[source];
		}
		for (int i = source + 1; i < resolution; i++) {
			if ((yP[(i - 1)] > yP[i]) && (mP[i] < 3.0D)) {
				mP[i] += Math.min(calmness, Public.difference(yP[i], yP[(i - 1)] - 0.5D));
			} else if ((yP[(i - 1)] < yP[i]) && (mP[i] > -3.0D)) {
				mP[i] -= Math.min(calmness, Public.difference(yP[i], yP[(i - 1)] - 0.5D));
			}

			yP[i] += mP[i];
			mP[i] *= 0.5D;
		}

		for (int k = source - 1; k > 1; k--) {
			if ((yP[(k + 1)] > yP[k]) && (mP[k] < 3.0D)) {
				mP[k] += Math.min(calmness, Public.difference(yP[k], yP[(k + 1)] - 0.5D));
			} else if ((yP[(k + 1)] < yP[k]) && (mP[k] > -3.0D)) {
				mP[k] -= Math.min(calmness, Public.difference(yP[k], yP[(k + 1)] - 0.5D));
			}

			yP[k] += mP[k];
			mP[k] *= 0.5D;
		}

		int[] ix = new int[resolution + 2];
		int[] iy = new int[resolution + 2];

		for (int i = 0; i < resolution + 2; i++) {
			ix[i] = ((int) xP[i]);
			iy[i] = ((int) yP[i]);
		}
		water = new Polygon(ix, iy, resolution + 2);

		sourceTimer += 1L;
	}

	public void render(Graphics g) {
		if (!game.getGame().paused) {
			water.translate((int) -game.xOffset(), (int) -game.yOffset());
		}
		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() - 100));

		if (!game.getGame().paused) {
			Polygon water2 = water;
			water2.translate(0, -1);
			g.drawPolygon(water2);
		}

		g.setColor(color);
		g.fillPolygon(water);
	}
}
