package com.zandgall.arvopia.environment.weather;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SnowFlake {
	private final Handler game;

	public static int change = 1;

	private double gravity = 0.0D;

	private int timer = 0;
	public double x, y;
	private final double size;

	private final BufferedImage img;
	private final ArrayList<Entity> alreadyAffected = new ArrayList<>();

	public SnowFlake(Handler game, double x, double y, double size) {
		this.game = game;
		this.x = (x + game.xOffset());
		this.y = (y + game.yOffset());
		this.size = size;

		gravity += size / 3.0D;
		gravity += Public.expandedRand(-0.5D, 0.5D);

		if (gravity < 0.0D)
			gravity = 0.1D;
		
		img = new BufferedImage((int) size+14, (int) size+14, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = img.createGraphics();
		
		int i = (int) (size/2+7);
		
		g.setColor(new Color(200, 200, 255, 14));
		g.fillOval((int) (i - size / 2.0D) - 3, (int) (i - size / 2.0D) - 3, (int) size + 6, (int) size + 6);
		g.setColor(new Color(200, 200, 255, 14));
		g.fillOval((int) (i - size / 2.0D) - 6, (int) (i - size / 2.0D) - 6, (int) size + 12, (int) size + 12);

		g.setColor(new Color(200, 200, 255, 100));
		g.drawOval((int) (i - size / 2.0D), (int) (i - size / 2.0D), (int) size, (int) size);
		g.setColor(new Color(200, 200, 255, 200));
		g.fillOval((int) (i - size / 2.0D), (int) (i - size / 2.0D), (int) size, (int) size);
	}

	public void tick(int speed) {

		for(Entity e: game.getEntityManager().getEntities()) {
			if(!alreadyAffected.contains(e) && e.getCollision(0, 0).contains(x, y)) {
				e.interactWithSnowFlake(this);
				alreadyAffected.add(e);
			}
		}

		double xOff;
		
		if(x/18.0>0&&x/18.0<World.getWidth()&&y/18.0>0&&y/18.0<World.getHeight())
			Tile.updateSnowy((int)(x/18.0D), (int)(y/18.0D), 2);

		if ((y - size / 2.0D < World.getHeight() * 18 - 1) && (x < (World.getWidth() + 10) * 18 + 10) && (x > -10.0D)
				&& (!World.getTile((int)(x/18.0D), (int)(y/18.0D)).isTop())
				&& (!World.getTile((int)(x/18.0D), (int)(y/18.0D)).isSolid())) {
			xOff = Public.range(-change, change, game.getWind(x, y));

			x += xOff / 5.0D * speed;
			y += (gravity + 2.0D - Math.abs(xOff / 5.0D)) / 5.0D * speed;

			timer = ((int) (timer + 0.2D * speed));
		} else {
			y = game.yOffset();
			x = Public.expandedRand(-360.0F + game.xOffset(), 1080.0F + game.xOffset());
		}
	}

	public void render(Graphics2D g) {
		AffineTransform p = g.getTransform();
		g.translate(x, y);
		g.drawImage(img, 0, 0, null);
		g.setTransform(p);
	}
}
