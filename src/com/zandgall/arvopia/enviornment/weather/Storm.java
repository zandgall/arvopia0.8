package com.zandgall.arvopia.enviornment.weather;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Storm {
	public static double wind = 0.0D;

	Handler game;

	ArrayList<Droplet> Droplets;

	ArrayList<Cloud> clouds;

	Lightning l;

	long lTimer = 0L;
	long lTimed = (long) Public.random(100.0D, 1000.0D);

	boolean stop = false;

	public Storm(Handler game, int ammount) {

		this.game = game;
		Droplets = new ArrayList<Droplet>();
		for (int i = 0; i < ammount; i++) {
			Droplets.add(new Droplet(game, (int) Public.random(-360.0D, 1080.0D), (int) Public.random(-400.0D, 0.0D),
					Public.random(2.0D, 10.0D)));
		}
		clouds = new ArrayList<Cloud>();
		for (int i = 0; i < ammount; i++) {
			clouds.add(new Cloud(game, (int) Public.random(-360.0D, 1080.0D), (int) Public.random(-400.0D, 0.0D),
					(int) Public.random(0.0D, 3.0D), Public.random(0.5D, 2.0D)));
		}
	}

	public void tick(int speed) {
		game.soundSystem.setVolume("Rain", OptionState.fxVolume/100.0f);

		if ((lTimer >= lTimed) && (l == null)) {
			l = new Lightning(game, (int) Public.random(0.0D, World.getWidth() * Tile.TILEWIDTH));
			lTimed = (long) (Public.random(100.0D, 1000.0D));
			lTimer = 0L;
		}

		lTimer += 1L;

		for (Droplet s : Droplets) {
			s.tick(wind, speed, stop);
		}
		for (Cloud c : clouds)
			c.tick();
		if (l != null) {
			l.tick();
			if (l.alpha == 0.0D)
				l = null;
		}
	}

	public void stop() {
		System.out.println("STOPPED RAIN");
		stop = true;
		game.soundSystem.stop("Rain");
	}

	public boolean done() {
		boolean out = false;

		for (Droplet s : Droplets) {
			if ((s.start) || (s.done))
				out = true;
			else {
				out = false;
			}
		}
		return out;
	}

	public void start() {
		stop = false;
		game.soundSystem.play("Rain");
	}

	public void render(Graphics g, Graphics2D g2d) {
		for (Droplet s : Droplets) {
			s.render(g);
		}
		for (Cloud c : clouds)
			c.render(g);
		g.drawImage(ImageLoader.loadImage("/textures/StormOverlay.png"), 0, 0, null);
		if (l != null) {
			l.render(g, g2d);
		}
	}

	public class Droplet {
		Handler game;
		double x;
		double y;
		double size;
		Color c;
		boolean done = false;

		double gravity = 16.0D;

		boolean start = true;

		public Droplet(Handler game, int x, int y, double size) {
			this.game = game;

			this.x = x;
			this.y = y;
			this.size = size;

			gravity += size / 5.0D;
			gravity += Public.debugRandom(-1.0D, 1.0D);

			c = new Color((int) Public.random(0.0D, 5.0D), (int) Public.random(0.0D, 20.0D),
					(int) Public.random(20.0D, 100.0D), (int) Public.random(50.0D, 200.0D));
		}

		public void tick(double wind, double speed, boolean stop) {
			Player p = game.getEntityManager().getPlayer();

			speed = speed / 5;

			if ((y - size / 2.0D > World.getHeight() * 18 - 1)
					|| ((x > (World.getWidth() + 10) * 18 + 10) && (x < -10.0D))
					|| (World.getTile(Public.grid(x, 18.0D, 0.0D), Public.grid(y + size, 18.0D, 0.0D)).isTop())
					|| (World.getTile(Public.grid(x, 18.0D, 0.0D), Public.grid(y + size, 18.0D, 0.0D)).isSolid())
					|| ((p.getCurrentTool() != null)
							&& (p.getCurrentTool().type == PlayerItem.UMBRELLA)
							&& (x >= p.getX() - 10.0D) && (x <= p.getX() + p.getWidth() + 10.0D)
							&& (y >= p.getY() - 30.0D))) {
				if (!stop) {
					y = (game.yOffset() - size);
					x = (Public.random(-360.0D, 1080.0D) + game.xOffset());
					start = false;
				} else {
					done = true;
				}
			}

			x += wind / 2.0D * speed;
			y += gravity / 2.0D * speed;
		}

		public void render(Graphics g) {
			int x = (int) (this.x - game.xOffset());
			int y = (int) (this.y - game.yOffset());

			g.setColor(c);
			g.fillRect(x, y, (int) Math.max(1.0D, size / 4.0D), (int) size);
		}
	}

	public class Cloud {
		public double speed;
		public int type;
		double x;
		double y;
		int width;
		int height;
		private BufferedImage cloud;
		int widthflip = 1;
		int heightflip;

		public Cloud(Handler handler, double x, double y, int type, double speed) {
			this.x = x;
			this.y = y;
			this.speed = speed;
			this.type = type;

			if (Math.random() < 0.5D) {
				widthflip = -1;
			}

			if ((Math.random() < 0.25D) || (Math.random() > 0.75D)) {
				heightflip = -1;
			}

			cloud = ImageLoader.loadImage("/textures/Statics/DarkCloud.png").getSubimage(0, type * 36, 54, 36);
			double d = Public.debugRandom(1.0D, 2.0D);
			width = ((int) (54.0D * (d + Public.debugRandom(0.0D, 0.5D))));
			height = ((int) (36.0D * (d + Public.debugRandom(0.0D, 0.5D))));
		}

		public void tick() {
			x += speed + game.getWorld().getEnviornment().getWind();
			if (x > World.getWidth() * Tile.TILEWIDTH + game.getWidth() / 2) {
				x = -54.0D;
			}
		}

		public void render(Graphics g) {
			g.drawImage(Tran.flip(cloud, widthflip, heightflip), (int) (x - game.getGameCamera().getxOffset()),
					(int) (y - game.getGameCamera().getyOffset()), width, height, null);
		}
	}

	public class Lightning {
		Handler game;
		
		int x;

		int[] xs;
		int[] ys;
		int touchPoint;
		double alpha = 255.0D;
		Polygon p;

		public Lightning(Handler game, int x) {
			this.x = x;

			this.game = game;

			int bends = 1;

			touchPoint = (game.getWorld().getHighest(x) * Tile.TILEHEIGHT);

			if (bends == 1) {
				create1();
			}
			game.quickPlay("Sounds/Lightning.ogg", "Lightning", false, x, (int) game.soundSystem.getListenerData().position.y, 0);
		}

		private void create1() {
			int bendOff = (int) Public.random(x - 20, x + 20);

			int yOff = game.getHeight();

			int[] xp = { x, x - 4, bendOff - 4, x - 4, x, bendOff };
			int[] yp = { -yOff, -yOff, touchPoint / 2 - yOff / 2, touchPoint, touchPoint, touchPoint / 2 - yOff / 2 };

			p = new Polygon(xp, yp, 6);
		}

		public void tick() {
			alpha -= 5.0D;
			alpha = Public.range(0.0D, 255.0D, alpha);
		}

		public void render(Graphics g, Graphics2D g2d) {
			g.setColor(new Color(200, 255, 255, (int) alpha));

			g2d.translate(-game.xOffset(), -game.yOffset());

			g.fillPolygon(p);

			g2d.setTransform(game.getGame().getDefaultTransform());
		}
	}
}
