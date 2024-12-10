package com.zandgall.arvopia.entity.moveableStatics;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Cloud extends MoveableStatic {
	private static final long serialVersionUID = 1L;
	
	public double speed;
	public int type;
	private BufferedImage cloud;
	int widthflip = 1;
	int heightflip;

	public Cloud(Handler handler, double x, double y) {
		super(handler, x-27, y-18, 54, 36, false);
		this.speed = Public.rand(-2, 2);
		this.type = Public.randInt(4);

		if (Math.random() < 0.5D) {
			widthflip = -1;
		}

		if ((Math.random() < 0.25D) || (Math.random() > 0.75D)) {
			heightflip = -1;
		}

		layer = (Math.random() * 4.0D - 2.0D);

		cloud = Tran.flip(com.zandgall.arvopia.gfx.PublicAssets.cloud[type], widthflip, heightflip);
	}

	public Cloud(Handler handler, double x, double y, int type, double speed) {
		super(handler, x, y, 54, 36, false);
		this.speed = speed;
		this.type = type;

		if (Math.random() < 0.5D) {
			widthflip = -1;
		}

		if ((Math.random() < 0.25D) || (Math.random() > 0.75D)) {
			heightflip = -1;
		}

		layer = (Math.random() * 4.0D - 2.0D);

		cloud = Tran.flip(com.zandgall.arvopia.gfx.PublicAssets.cloud[type], widthflip, heightflip);
	}

	public void tick() {
		x += speed + game.getWorld().getEnvironment().getWind(x, y);
		if (x > World.getWidth() * Tile.WIDTH + game.getWidth() / 2.0) {
			x = -54.0D;
		}
	}

	public void init() {
	}

	public void render(Graphics2D g) {
		AffineTransform p = g.getTransform();
		g.translate(x, y);
		g.drawImage(cloud, 0, 0, null);
		g.setTransform(p);
	}

	public boolean shouldTick() {
		return true;
	}

	public String toString() {
		return "Cloud " + x + " " + y + " " + layer + " " + type + " " + speed;
	}

}
