package com.zandgall.arvopia.entity.creatures;

import java.awt.Graphics2D;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.items.Item;
import com.zandgall.arvopia.tiles.Tile;

public class Butterfly extends Creature {
	private static final long serialVersionUID = 1L;
	
	public long prevTime;
	public long prevMoveTime = System.currentTimeMillis();
	private long moveTimer;
	private long timer;

	public Butterfly(Handler handler, double x, double y, boolean direction, long timer) {
		super(handler, x, y, 18, 18, direction, 0.5D, Creature.DEFAULT_ACCELERATION, 5, true, false, 0.0D, 0.0D,
				"Butterfly");

		health = 2.0D;
		MAX_HEALTH = 2;

		this.timer = timer;
		prevTime = System.currentTimeMillis();

		layer = (Math.random() - 5.0D);
		moveTimer = 0L;
	}

	public void tick() {
		if (System.currentTimeMillis() - prevTime >= timer) {
			game.getWorld().kill(this);
		} else {
			butterflyForward.tick();
			butterflyBackward.tick();

			move();

			if (System.currentTimeMillis() - prevMoveTime >= moveTimer) {
				prevMoveTime = System.currentTimeMillis();

				xVol += (float) (Math.random() * speed - speed / 2.0D);
				yVol += (float) (Math.random() * speed * 4.0D - speed * 2.0D);

				if (Math.abs(xVol) > MAX_SPEED) {
					xVol -= xVol / 2.0D;
				}

				if (Math.abs(yVol) > MAX_SPEED) {
					yVol -= yVol / 2.0F;
				}
			}

			yMove = (float) yVol;

			if (xVol > 0.0D) {
				setxMove((float) (speed + xVol + game.getWind()));
			} else if (xVol < 0.0D) {
				setxMove((float) (-speed + xVol + game.getWind()));
			}
		}
	}

	public void render(Graphics2D g) {
		g.drawImage(getFrame(), (int) (x - game.getGameCamera().getxOffset()),
				(int) (y - game.getGameCamera().getyOffset()), null);

		if (health < MAX_HEALTH) {
			showHealthBar(g);
		}
	}

	private java.awt.image.BufferedImage getFrame() {
		if (Math.round(getxMove() - speed + 0.49D) > 0L)
			return butterflyForward.getFrame();
		if (Math.round(Math.abs(getxMove()) - speed + 0.49D) > 0L) {
			return butterflyBackward.getFrame();
		}
		return butterflyForward.getFrame();
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

		int ty = (int) ((y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT);
		if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
				|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty))
				|| (checkCollision(0.0F, yMove))) {
			bottom = true;
		} else if ((collisionWithDown((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
				|| (collisionWithDown((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty))) {
			if (y + bounds.y + bounds.height < ty * Tile.TILEHEIGHT + 4) {
				down = true;
			}

			if ((y + bounds.y + bounds.height <= ty * Tile.TILEHEIGHT + 1) && (yMove >= 0.0F)) {
				bottoms = true;
				bottom = true;
			}
		}
		ty = (int) ((y + yMove + bounds.y + bounds.height + 2.0D) / Tile.TILEHEIGHT);
		if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
				|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty))
				|| (checkCollision(0.0F, yMove + 1.0F))
				|| (((collisionWithDown((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
						|| (collisionWithDown((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty)))
						&& (y + bounds.y + bounds.height <= ty * Tile.TILEHEIGHT + 1))) {
			bottoms = true;
		}

		ty = (int) ((y + yMove + bounds.y) / Tile.TILEHEIGHT);
		if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
				|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty))
				|| (checkCollision(0.0F, yMove))) {
			top = true;
		}
		ty = (int) ((y + yMove + bounds.y - 2.0D) / Tile.TILEHEIGHT);
		if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
				|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty))
				|| (checkCollision(0.0F, yMove - 1.0F))) {
			tops = true;
		}

		int tx = (int) ((x + getxMove() + bounds.x + bounds.width) / Tile.TILEWIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.TILEHEIGHT))
				|| (checkCollision(getxMove() + 1.0F, 0.0F))) {
			right = true;
		}
		tx = (int) ((x + getxMove() + bounds.x + bounds.width + 2.0D) / Tile.TILEWIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.TILEHEIGHT))
				|| (checkCollision(getxMove() + 1.0F, 0.0F))) {
			rights = true;
		}

		tx = (int) ((x + getxMove() + bounds.x) / Tile.TILEWIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.TILEHEIGHT))
				|| (checkCollision(getxMove(), 0.0F))) {
			left = true;
		}
		tx = (int) ((x + getxMove() + bounds.x - 2.0D) / Tile.TILEWIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.TILEHEIGHT))
				|| (checkCollision(getxMove() - 1.0F, 0.0F))) {
			lefts = true;
		}
	}
	
	public void kill() {
		
		if(game.getPlayer().targets.contains(this))
			game.getWorld().getItemManager().addItem(Item.butterflyWing.createNew((int) x, (int) y));
		
		game.getWorld().getEntityManager().getEntities().remove(this);
	}
	
}
