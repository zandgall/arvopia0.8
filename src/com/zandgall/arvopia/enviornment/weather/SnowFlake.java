package com.zandgall.arvopia.enviornment.weather;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

class SnowFlake {
	Handler game;
	ArrayList<Entity> entities = new ArrayList<Entity>();

	public static double xchange = 0.02D;
	public static int change = 1;

	double gravity = 0.0D;

	int timer = 0;
	double x;
	double y;
	double size;
	double xOff = 0.0D;
	
	BufferedImage img;

	public SnowFlake(Handler game, double x, double y, double size) {
		this.game = game;
		this.x = (x + game.xOffset());
		this.y = (y + game.yOffset());
		this.size = size;

		gravity += size / 3.0D;
		gravity += Public.random(-0.5D, 0.5D);

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

	public void tick(double wind, int speed) {
		ArrayList<Entity> out = new ArrayList<Entity>();
		for(Entity e: game.getEntityManager().getEntities()) {
			if(!entities.contains(e)) {
				out.add(e);
			}
		}
		
		Entity closest = Entity.precisionCollision(out, (int) x, (int) y);
		
		if (closest != null) {
			
			if ((closest.snowy < closest.maxSnowy))
				game.getWorld().getEntityManager().getPlayer().getClosest(x, y).snowy += 1;
			
			entities.add(closest);
			
			if(closest.stopsSnow()) {
				y = game.yOffset();
				x = Public.random(-360.0F + game.xOffset(), 1080.0F + game.xOffset());
				xOff = Public.random(-change, change);
			}
		}

		
		if(Public.grid(x, 18.0, 0)>0&&Public.grid(x, 18.0, 0)<World.getWidth()&&Public.grid(y, 18.0, 0)>0&&Public.grid(y, 18.0, 0)<World.getHeight())
			World.getTile((int) Public.range(0, World.getWidth(), Public.grid(x, 18.0D, 0.0D)), (int) Public.range(0, World.getHeight(), Public.grid(y, 18.0D, 0.0D))).updateSnowy(Public.grid(x, 18.0D, 0.0D),
					Public.grid(y, 18.0D, 0.0D), 2);

		if ((y - size / 2.0D < World.getHeight() * 18 - 1) && (x < (World.getWidth() + 10) * 18 + 10) && (x > -10.0D)
				&& (!World.getTile(Public.grid(x, 18.0D, 0.0D), Public.grid(y, 18.0D, 0.0D)).isTop())
				&& (!World.getTile(Public.grid(x, 18.0D, 0.0D), Public.grid(y, 18.0D, 0.0D)).isSolid())) {
			if (timer - change >= 0) {
				double offset = Public.debugRandom(-xchange, xchange);

				xOff += offset;

				xOff = (Public.range(-change, change, xOff) + wind);

				timer = 0;
			}

			x += xOff / 5.0D * speed;
			y += (gravity + 2.0D - Math.abs(xOff / 5.0D)) / 5.0D * speed;

			timer = ((int) (timer + 0.2D * speed));
		} else {
			if ((Public.identifyRange(0.0D, World.getWidth() * Tile.TILEWIDTH, x))
					&& (Public.identifyRange(0.0D, World.getHeight() * Tile.TILEHEIGHT, y))) {

			}
			entities.clear();

			y = game.yOffset();
			x = Public.random(-360.0F + game.xOffset(), 1080.0F + game.xOffset());
			xOff = Public.random(-change, change);
		}
	}

	public void render(Graphics g) {
		int x = (int) (this.x - game.xOffset());
		int y = (int) (this.y - game.yOffset());

		g.drawImage(img, x, y, null);
		
//		g.setColor(new Color(200, 200, 255, 14));
//		g.fillOval((int) (x - size / 2.0D) - 3, (int) (y - size / 2.0D) - 3, (int) size + 6, (int) size + 6);
//		g.setColor(new Color(200, 200, 255, 14));
//		g.fillOval((int) (x - size / 2.0D) - 6, (int) (y - size / 2.0D) - 6, (int) size + 12, (int) size + 12);
//
//		g.setColor(new Color(200, 200, 255, 100));
//		g.drawOval((int) (x - size / 2.0D), (int) (y - size / 2.0D), (int) size, (int) size);
//		g.setColor(new Color(200, 200, 255, 200));
//		g.fillOval((int) (x - size / 2.0D), (int) (y - size / 2.0D), (int) size, (int) size);
	}
}
