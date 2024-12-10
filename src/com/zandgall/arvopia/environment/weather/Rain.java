package com.zandgall.arvopia.environment.weather;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;

import java.awt.*;
import java.util.ArrayList;

public class Rain {
	ArrayList<Droplet> Droplets;

	public boolean stop;
	
	Handler game;

	public Rain(Handler game, int ammount) {
		this.game=game;
		Droplets = new ArrayList<Droplet>();
		for (int i = 0; i < ammount; i++) {
			Droplets.add(new Droplet(game, (int) Public.expandedRand(-360.0D, 1080.0D), (int) Public.expandedRand(-400.0D, 0.0D),
					Public.expandedRand(2.0D, 10.0D)));
		}
	}

	public void tick(int speed) {
		
		for (Droplet s : Droplets) {
			s.tick(speed, stop);
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

	public void render(Graphics2D g) {
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
			gravity += Public.rand(-1.0D, 1.0D);

			c = new Color((int) Public.expandedRand(0.0D, 10.0D), (int) Public.expandedRand(0.0D, 50.0D),
					(int) Public.expandedRand(50.0D, 255.0D), (int) Public.expandedRand(50.0D, 200.0D));
		}

		public void tick(double speed, boolean stop) {
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
					x = (Public.expandedRand(-360.0D, 1080.0D) + game.xOffset());
					start = false;
				} else {
					done = true;
				}
			}

			x += game.getWind(x, y) / 2.0D * speed;
			y += gravity / 2.0D * speed;
		}

		public void render(Graphics2D g) {
			int x = (int) (this.x - game.xOffset());
			int y = (int) (this.y - game.yOffset());

			g.setColor(c);
			g.fillRect(x, y, (int) Math.max(1.0D, size / 4.0D), (int) size);
		}
	}
}
