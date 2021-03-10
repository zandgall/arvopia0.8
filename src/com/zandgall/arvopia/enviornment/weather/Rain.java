package com.zandgall.arvopia.enviornment.weather;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Rain {
	public static double wind = 0.0D;

	ArrayList<Droplet> Droplets;

	public boolean stop;
	
	Handler game;

	public Rain(Handler game, int ammount) {
		this.game=game;
		Droplets = new ArrayList<Droplet>();
		for (int i = 0; i < ammount; i++) {
			Droplets.add(new Droplet(game, (int) Public.random(-360.0D, 1080.0D), (int) Public.random(-400.0D, 0.0D),
					Public.random(2.0D, 10.0D)));
		}
	}

	public void tick(int speed) {
		
		for (Droplet s : Droplets) {
			s.tick(wind, speed, stop);
		}
	}

	public void stop() {
		System.out.println("STOPPED RAIN");
		stop = true;
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
//		game.soundSystem.play("Rain"); TODO
	}

	public void render(Graphics g) {
		for (Droplet s : Droplets) {
			s.render(g);
		}
	}

	public class Droplet {
		Handler game;
		double x;
		double y;
		double size;
		Color c;
		double gravity = 9.0D;

		boolean start = true;

		boolean done = false;

		public Droplet(Handler game, int x, int y, double size) {
			this.game = game;

			this.x = x;
			this.y = y;
			this.size = size;

			gravity += size / 5.0D;
			gravity += Public.debugRandom(-1.0D, 1.0D);

			c = new Color((int) Public.random(0.0D, 10.0D), (int) Public.random(0.0D, 50.0D),
					(int) Public.random(50.0D, 255.0D), (int) Public.random(50.0D, 200.0D));
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
							&& (x >= p.getX() - 10.0D) && (x <= p.getX() + p.getWidth() + 40.0D)
							&& (y >= p.getY() - 40.0D))) {
				if (!stop) {
					y = (game.yOffset() - size - (speed * Math.random()));
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
}
