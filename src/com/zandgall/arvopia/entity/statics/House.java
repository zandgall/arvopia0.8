package com.zandgall.arvopia.entity.statics;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.utils.Public;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

public class House extends StaticEntity {
	private static final long serialVersionUID = 1L;
	
	public int type;
	private int orType;
	int widthflip = 1;
	public boolean isStone = false;

	public House(Handler handler, double x, double y, int type) {
		super(handler, x, y, 108, 162, false, 1000, PlayerItem.NONE);
		this.type = type;

		if (Math.random() < 0.5D) {
			widthflip = -1;
		}

		if (type == 0) {
			layer = Public.random(8.0D, 9.0D);
			bounds.x = 0;
			bounds.y = 0;
			bounds.width = 126;
			bounds.height = 108;
		} else if (type == 1) {
			layer = Public.random(8.0D, 9.0D);
			bounds.x = 0;
			bounds.y = 0;
			bounds.width = 126;
			bounds.height = 108;
		} else if (type == 2) {
			layer = Public.random(8.0D, 9.0D);
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
		if (variety) {
			if (type == 2)
				g.drawImage(Tran.flip(ImageLoader.loadImage("/textures/NPCs/Houses/FrizzysHouse.png"), widthflip, 1),
						(int) (x - game.getGameCamera().getxOffset()), (int) (y - game.getGameCamera().getyOffset()),
						null);
			else
				g.drawImage(
						Tran.flip(ImageLoader.loadImage("/textures/NPCs/Houses/" + (isStone ? "Stone" : "") + "House"
								+ (type != 0 ? "2" : "") + ".png"), widthflip, 1),
						(int) (x - game.getGameCamera().getxOffset()), (int) (y - game.getGameCamera().getyOffset()),
						null);
		} else if (type == 2)
			g.drawImage(ImageLoader.loadImage("/textures/NPCs/Houses/FrizzysHouse.png"),
					(int) (x - game.getGameCamera().getxOffset()), (int) (y - game.getGameCamera().getyOffset()), null);
		else
			g.drawImage(
					ImageLoader.loadImage("/textures/NPCs/Houses/" + (isStone ? "Stone" : "") + "House"
							+ (type != 0 ? "2" : "") + ".png"),
					(int) (x - game.getGameCamera().getxOffset()), (int) (y - game.getGameCamera().getyOffset()), null);
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

	public String outString() {
		return "House " + x + " " + y + " " + layer + " " + type;
	}

}
