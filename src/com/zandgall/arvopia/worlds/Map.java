package com.zandgall.arvopia.worlds;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.tiles.Tile;
import java.awt.Graphics;
import java.io.Serializable;

public class Map implements Serializable {

	private static final long serialVersionUID = 5820504743698422105L;

	Handler game;

	public Map(Handler game) {
		this.game = game;
	}

	public void render(Graphics g) {
		int xOff = (int) (game.getWidth() / 2 - World.getWidth() * 1.5D);
		int yOff = (int) (game.getHeight() / 2 - World.getHeight() * 1.5D);

		for (int x = 0; x < World.getWidth(); x++) {
			for (int y = 0; y < World.getHeight(); y++) {
				g.setColor(World.getTile(x, y).getColor());
				g.fillRect(xOff + x * 3, yOff + y * 3, 3, 3);
			}
		}

		for (Entity e : game.getEntityManager().getEntities()) {
			if (e.mapable()) {
				int px = (int) e.getX();
				int py = (int) e.getY();
				g.setColor(e.mapColor());
				g.fillRect(xOff + px / Tile.WIDTH * 3 + 3, yOff + py / Tile.HEIGHT * 3, e.mapSize().x,
						e.mapSize().y);
			}
		}

		int px = (int) game.getEntityManager().getPlayer().getX();
		int py = (int) game.getEntityManager().getPlayer().getY();

		g.setColor(java.awt.Color.red);
		g.fillRect(xOff + px / Tile.WIDTH * 3 + 3, yOff + py / Tile.HEIGHT * 3, 3, 9);

		g.setColor(java.awt.Color.black);
		g.drawRect(xOff, yOff, World.getWidth() * 3, World.getHeight() * 3);
	}
}
