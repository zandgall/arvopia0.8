package com.zandgall.arvopia.entity.statics;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.utils.Public;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class House extends StaticEntity {
	private static final long serialVersionUID = 1L;
	
	public int type;
	private int orType;
	int widthflip = 1;
	public boolean isStone = false;

	public House(Handler handler, double x, double y) {
		super(handler, x-54, y-162, 108, 162, false, 10000, PlayerItem.NONE);
		this.type = Public.randInt(2);

		if (Math.random() < 0.5D) {
			widthflip = -1;
		}

		if (type == 0) {
			layer = Public.expandedRand(8.0D, 9.0D);
			bounds.x = 0;
			bounds.y = 0;
			bounds.width = 126;
			bounds.height = 108;
		} else if (type == 1) {
			layer = Public.expandedRand(8.0D, 9.0D);
			bounds.x = 0;
			bounds.y = 0;
			bounds.width = 126;
			bounds.height = 108;
		} else if (type == 2) {
			layer = Public.expandedRand(8.0D, 9.0D);
			bounds.x = 0;
			bounds.y = 0;
			bounds.width = 126;
			bounds.height = 108;
		}
	}

	public House(Handler handler, double x, double y, int type) {
		super(handler, x, y, 108, 162, false, 10000, PlayerItem.NONE);
		this.type = type;

		if (Math.random() < 0.5D) {
			widthflip = -1;
		}

		if (type == 0) {
			layer = Public.expandedRand(8.0D, 9.0D);
			bounds.x = 0;
			bounds.y = 0;
			bounds.width = 126;
			bounds.height = 108;
		} else if (type == 1) {
			layer = Public.expandedRand(8.0D, 9.0D);
			bounds.x = 0;
			bounds.y = 0;
			bounds.width = 126;
			bounds.height = 108;
		} else if (type == 2) {
			layer = Public.expandedRand(8.0D, 9.0D);
			bounds.x = 0;
			bounds.y = 0;
			bounds.width = 126;
			bounds.height = 108;
		}
	}

	public int getType() {
		return type;
	}

	public int getOrType() {
		return orType;
	}

	public void tick() {
		
	}

	public boolean stopsSnow() {
		return false;
	}
	
	public void render(Graphics2D g) {
		AffineTransform p = g.getTransform();
		g.translate(x, y);

		BufferedImage img = type == 2 ? ImageLoader.loadImage("/textures/NPCs/Houses/FrizzysHouse.png") :
			ImageLoader.loadImage("/textures/NPCs/Houses/" + (isStone ? "Stone" : "") + "House"
				+ (type != 0 ? "2" : "") + ".png");
		if(variety)
			img = Tran.flip(img, widthflip, 1);
		g.drawImage(img, 0, 0, null);
		g.setTransform(p);
	}

	public boolean mapable() {
		return true;
	}

	public Color mapColor() {
		return new Color(150, 100, 0);
	}

	public Point mapSize() {
		return new Point(18, 18);
	}

	public String toString() {
		return "House " + x + " " + y + " " + layer + " " + type;
	}

}
