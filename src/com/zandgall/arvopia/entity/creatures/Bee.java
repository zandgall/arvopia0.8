package com.zandgall.arvopia.entity.creatures;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.items.Item;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.tiles.Tile;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Bee extends Creature {
	private static final long serialVersionUID = 1L;

	private long moveTimer;
	private long timer;
	public long prevMoveTime = System.currentTimeMillis();
	public long prevTime = System.currentTimeMillis();
	String buzz;

	public double angle = 0;

	public Bee(Handler handler, double x, double y) {
		super(null, x-3, y-3, 6, 6, true, Creature.DEFAULT_SPEED, Creature.DEFAULT_ACCELERATION, 5, true, false,
				0.0D, 0.0D, "Bee");
		prevTime = System.currentTimeMillis();
		timer = 10000;

		health = 1.0D;
		MAX_HEALTH = 1;

		bounds.x = -6;
		bounds.width = 18;
		bounds.y = -6;
		bounds.height = 18;

		layer = (Math.random() - 5.0D);
		buzz = "Buzz" + (((OptionState) handler.getGame().optionState).getToggle("Sound per layer") ? layer : "");
		game.addSound("Sounds/Bee.ogg", buzz, true, 0, 0, 0);
		//game.soundSystem.setVolume(buzz, 0.2f);
		timer = 100L;
		moveTimer = 0L;
	}

	public Bee(Handler handler, double x, double y, boolean direction, long timeMillis) {
		super(handler, x, y, 6, 6, direction, Creature.DEFAULT_SPEED, Creature.DEFAULT_ACCELERATION, 5, true, false,
				0.0D, 0.0D, "Bee");

		prevTime = System.currentTimeMillis();
		timer = timeMillis;

		health = 1.0D;
		MAX_HEALTH = 1;

		bounds.x = -6;
		bounds.width = 18;
		bounds.y = -6;
		bounds.height = 18;

		layer = (Math.random() - 5.0D);
		buzz = "Buzz" + (((OptionState) handler.getGame().optionState).getToggle("Sound per layer") ? layer : "");
		game.addSound("Sounds/Bee.ogg", buzz, true, 0, 0, 0);
		//game.soundSystem.setVolume(buzz, 0.2f);
		timer = 100L;
		moveTimer = 0L;
	}

	public void tick() {
		
		//if (!game.soundSystem.playing(buzz))
		//	game.soundSystem.play(buzz);

		if (Math.random() < 0.5) {
			if (Math.random() < 0.5) {
				angle += 0.2;
			} else {
				angle -= 0.2;
			}
		}
		
		angle=angle%(Math.PI*2);
		
		xMove = (float) Math.cos(this.angle);
		yMove = (float) Math.sin(this.angle);

		move();

		game.setPosition(buzz, (int) x, (int) y, 0);
	}

	public void render(Graphics2D g) {
		AffineTransform p = g.getTransform();
		g.translate(x, y);
		g.drawImage(getFrame(), 0, 0, null);
		g.setTransform(p);
	}

	private java.awt.image.BufferedImage getFrame() {
		
		double a = Math.toDegrees(angle);
		
		if((a>=360-22.5&&a<=22.5)||(a>=180-22.5&&a<=180+22.5))
			return PublicAssets.bee[0];
		if((a>=22.5&&a<=45+22.5)||(a>=215-22.5&&a<=215+22.5))
			return PublicAssets.bee[1];
		if((a>=90-22.5&&a<=90+22.5)||(a>=270-22.5&&a<=270+22.5))
			return PublicAssets.bee[2];
		if((a>=315-22.5&&a<=315+22.5)||(a>=135-22.5&&a<=135+22.5))
			return PublicAssets.bee[3];
		
		return com.zandgall.arvopia.gfx.PublicAssets.bee[1];
	}

	public void checkCol() {
		down = false;
		left = false;
		right = false;
		top = false;
		bottom = false;
		lefts = false;
		rights = false;
		tops = false;
		bottoms = false;
		inWater = false;

		for (com.zandgall.arvopia.water.Water w : game.getWorld().getWaterManager().getWater()) {
			if (w.box().intersects(bounds)) {
				inWater = true;
			}
		}
		int ty = (int) ((y + yMove + bounds.y + bounds.height) / Tile.HEIGHT);
		if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.WIDTH), ty))
				|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.WIDTH), ty))
				|| (checkCollision(0.0F, yMove))) {
			bottom = true;
		} else if ((collisionWithDown((int) ((x + bounds.x + 2.0D) / Tile.WIDTH), ty))
				|| (collisionWithDown((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.WIDTH), ty))) {
			if (y + bounds.y + bounds.height < ty * Tile.HEIGHT + 4) {
				down = true;
			}

			if ((y + bounds.y + bounds.height <= ty * Tile.HEIGHT + 1) && (yMove >= 0.0F)) {
				bottoms = true;
				bottom = true;
			}
		}
		ty = (int) ((y + yMove + bounds.y + bounds.height + 2.0D) / Tile.HEIGHT);
		if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.WIDTH), ty))
				|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.WIDTH), ty))
				|| (checkCollision(0.0F, yMove + 1.0F))
				|| (((collisionWithDown((int) ((x + bounds.x + 2.0D) / Tile.WIDTH), ty))
						|| (collisionWithDown((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.WIDTH), ty)))
						&& (y + bounds.y + bounds.height <= ty * Tile.HEIGHT + 1))) {
			bottoms = true;
		}

		ty = (int) ((y + yMove + bounds.y) / Tile.HEIGHT);
		if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.WIDTH), ty))
				|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.WIDTH), ty))
				|| (checkCollision(0.0F, yMove))) {
			top = true;
		}
		ty = (int) ((y + yMove + bounds.y - 2.0D) / Tile.HEIGHT);
		if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.WIDTH), ty))
				|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.WIDTH), ty))
				|| (checkCollision(0.0F, yMove - 1.0F))) {
			tops = true;
		}

		int tx = (int) ((x + getxMove() + bounds.x + bounds.width) / Tile.WIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.HEIGHT))
				|| (checkCollision(getxMove() + 1.0F, 0.0F))) {
			right = true;
		}
		tx = (int) ((x + getxMove() + bounds.x + bounds.width + 2.0D) / Tile.WIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.HEIGHT))
				|| (checkCollision(getxMove() + 1.0F, 0.0F))) {
			rights = true;
		}

		tx = (int) ((x + getxMove() + bounds.x) / Tile.WIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.HEIGHT))
				|| (checkCollision(getxMove(), 0.0F))) {
			left = true;
		}
		tx = (int) ((x + getxMove() + bounds.x - 2.0D) / Tile.WIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.HEIGHT))
				|| (checkCollision(getxMove() - 1.0F, 0.0F))) {
			lefts = true;
		}
	}

	public void kill() {

		//game.soundSystem.stop(buzz);
		game.removeSound("Sounds/BeeBuzz.ogg", buzz);

		game.getWorld().getItemManager().addItem(Item.honey.createNew((int) x, (int) y));

		game.getWorld().getEntityManager().getEntities().remove(this);
	}

}
