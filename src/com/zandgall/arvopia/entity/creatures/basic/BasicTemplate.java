package com.zandgall.arvopia.entity.creatures.basic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.entity.creatures.npcs.NPC;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.water.Water;
import com.zandgall.arvopia.worlds.World;

public abstract class BasicTemplate extends Creature {

	private static final long serialVersionUID = 4309370043116391037L;
	
	public static final int EVERYTHINGATTACKER = 6, HUNTER = 5, PLAYERPREDATOR = 4, PREDATOR = 3, PLAYERATTACKER = 2,
			DEFENSIVE = 1, PASSIVE = 0, FLEER = -1;

	boolean jumping = false;
	protected int widthFlip = 1;
	private long aitimer;
	private boolean u, d, l, r;
	protected boolean custState = false;

	Creature following = null, fleeing = null;

	int range, hostility;
	double strength, attackspeed, attacktimer = 0;

	public BasicTemplate(Handler handler, double x, double y, int width, int height, double speed, double acceleration,
			int maxSpeed, String name, int hostility, int range, int health, int maxHealth, double strength,
			double attackspeed) {
		super(handler, x, y, width, height, false, speed * Public.debugRandom(0.8, 1.8), acceleration, maxSpeed, false,
				false, Creature.DEFAULT_JUMP_FORCE, Creature.DEFAULT_JUMP_CARRY, name);

		this.health = health;
		this.MAX_HEALTH = maxHealth;
		this.hostility = hostility;
		this.range = range;

		this.strength = strength;
		this.attackspeed = attackspeed;
	}

	public void tick() {
		custTick();

		targeting();

		aiMove();

		move();
	}

	public void targeting() {
		switch (hostility) {
		case FLEER: {
			following = null;
			fleeing = null;
			if (getInRadius(x, y, range).size() > 0) {
				Creature b = getInRadius(x, y, range).get(0);
				for (Creature c : getInRadius(x, y, range)) {
					if (!(c instanceof BasicTemplate) || ((BasicTemplate) c).hostility != FLEER)
						if (Public.dist(c.centerX(), c.centerY(), centerX(), centerY()) < Public.dist(b.centerX(),
								b.centerY(), centerX(), centerY())) {
							fleeing = c;
							b = c;
						}
				}
			}
			break;
		}
		case PASSIVE: {
			following = null;
			fleeing = null;
			for (Creature c : getInRadius(x, y, range)) {
				if (c instanceof BasicTemplate) {
					if (((BasicTemplate) c).following == this)
						fleeing = c;
				}
			}
			break;
		}
		case DEFENSIVE: {
			following = null;
			fleeing = null;
			for (Creature c : getInRadius(x, y, range)) {
				if (c instanceof BasicTemplate) {
					if (((BasicTemplate) c).following == this) {
						if ((MAX_HEALTH - health) <= (c.MAX_HEALTH - c.health / 2) * 2) {
							fleeing = null;
						} else {
							fleeing = c;
						}
						following = c;
					}

				} else if (c instanceof Player && (((Player) c).targets.contains(this))) {
					if ((MAX_HEALTH - health) <= (c.MAX_HEALTH - c.health / 2) * 2) {
						fleeing = null;
					} else {
						fleeing = c;
					}
					following = c;
				}
			}
			break;
		}
		case PLAYERATTACKER: {
			following = null;
			fleeing = null;
			for (Creature c : getInRadius(x, y, range)) {
				if (c instanceof BasicTemplate) {
					if (((BasicTemplate) c).following == this) {
						if ((MAX_HEALTH - health) <= (c.MAX_HEALTH - c.health / 2) * 2) {
							fleeing = null;
						} else {
							fleeing = c;
						}
						following = c;
					}
				} else if (c instanceof Player || c instanceof NPC) {
					if ((MAX_HEALTH - health) <= (c.MAX_HEALTH - c.health / 2) * 2) {
						fleeing = null;
					} else {
						fleeing = c;
					}
					following = c;
				}
			}
			break;
		}
		case PREDATOR: {
			following = null;
			fleeing = null;
			for (Creature c : getInRadius(x, y, range)) {
				if (c instanceof BasicTemplate) {
					BasicTemplate b = (BasicTemplate) c;
					if (b.following == this || b.hostility == FLEER || b.hostility == PASSIVE) {
						if ((MAX_HEALTH - health) <= (c.MAX_HEALTH - c.health / 2) * 2 || b.hostility == FLEER
								|| b.hostility == PASSIVE) {
							fleeing = null;
						} else {
							fleeing = c;
						}
						following = c;
					}
				} else if (c instanceof Player && (((Player) c).targets.contains(this))) {
					System.out.println(
							this.name + " " + (MAX_HEALTH - health) + " " + ((c.MAX_HEALTH - c.health / 2) * 2));
					if ((MAX_HEALTH - health) <= (c.MAX_HEALTH - c.health / 2) * 2) {
						fleeing = null;
					} else {
						fleeing = c;
					}
					following = c;
				}
			}
			break;
		}
		case PLAYERPREDATOR: {
			following = null;
			fleeing = null;
			for (Creature c : getInRadius(x, y, range)) {
				if (c instanceof BasicTemplate) {
					BasicTemplate b = (BasicTemplate) c;
					if (b.following == this || b.hostility == FLEER || b.hostility == PASSIVE) {
						if ((MAX_HEALTH - health) <= (c.MAX_HEALTH - c.health / 2) * 2 || b.hostility == FLEER
								|| b.hostility == PASSIVE) {
							fleeing = null;
						} else {
							fleeing = c;
						}
						following = c;
					}
				} else if (c instanceof Player || c instanceof NPC) {
					if ((MAX_HEALTH - health) <= (c.MAX_HEALTH - c.health / 2) * 2) {
						fleeing = null;
					} else {
						fleeing = c;
					}
					following = c;
					if (c instanceof Player)
						break;
				}
			}
			break;
		}
		case HUNTER: {
			following = null;
			fleeing = null;
			for (Creature c : getInRadius(x, y, range)) {
				if (c instanceof BasicTemplate) {
					BasicTemplate b = (BasicTemplate) c;
					if (b.following == this || b.hostility == FLEER || b.hostility == PASSIVE
							|| b.hostility == DEFENSIVE) {
						if ((MAX_HEALTH - health) <= (c.MAX_HEALTH - c.health / 2) * 2 || b.hostility == FLEER
								|| b.hostility == PASSIVE) {
							fleeing = null;
						} else {
							fleeing = c;
						}
						following = c;
					}
				} else if (c instanceof Player && (((Player) c).targets.contains(this))) {
					if ((MAX_HEALTH - health) <= (c.MAX_HEALTH - c.health / 2) * 2) {
						fleeing = null;
					} else {
						fleeing = c;
					}
					following = c;
				}
			}
			break;
		}
		case EVERYTHINGATTACKER: {
			following = null;
			fleeing = null;
			for (Creature c : getInRadius(x, y, range)) {
				if (c instanceof BasicTemplate) {
					BasicTemplate b = (BasicTemplate) c;
					if (b.following == this || b.hostility == FLEER || b.hostility == PASSIVE
							|| b.hostility == DEFENSIVE) {
						if ((MAX_HEALTH - health) <= (c.MAX_HEALTH - c.health / 2) * 2 || b.hostility == FLEER
								|| b.hostility == PASSIVE) {
							fleeing = null;
						} else {
							fleeing = c;
						}
						following = c;
					}
				} else {
					if ((MAX_HEALTH - health) <= (c.MAX_HEALTH - c.health / 2) * 2) {
						fleeing = null;
					} else {
						fleeing = c;
					}
					following = c;
				}
			}
			break;
		}

		}

		if (following != null && attacktimer > attackspeed) {
			if (Public.dist(following.centerX(), following.centerY(), centerX(), centerY()) < range / 4) {

				following.damage(strength);
			}
			attacktimer = 0;
		}
		attacktimer++;

	}

	public void aiMove() {

		// Set velocity based on directions
		xMove = 0;
		yMove = 0;

		xMove += (xVol > 0 ? speed : xVol < 0 ? -speed : 0) + xVol;

		if (l) {
			if (!custState) {
				if ((xVol < maxSpeed) && (xVol > 0.0D))
					xVol -= FRICTION * 2.0D;
				xVol -= acceleration;
			} else {
				xVol = 0.0D;
			}
			widthFlip = 1;
		} else if (r) {
			widthFlip = -1;
			if (!custState) {
				if ((xVol < maxSpeed) && (xVol < 0.0D))
					xVol += FRICTION * 2.0D;
				xVol += acceleration;
			} else {
				xVol = 0.0D;
			}
		} else {
			float txv = (float) xVol;
			if (xVol < 0.0D) {
				xVol += acceleration + FRICTION;
			} else if (xVol > 0.0D)
				xVol -= acceleration + FRICTION;
			if (((txv > 0.0F) && (xVol < 0.0D)) || ((txv < 0.0F) && (xVol > 0.0D))) {
				xVol = 0.0D;
			}
		}

		boolean backleft = false;

		// If hitting wall, check space above to jump to
		if ((right) && (r)) {
			int tx = (int) ((x + getxMove() + bounds.x + bounds.width + 2.0D) / Tile.TILEWIDTH);

			if (emptySpaces(18, -108, 18, -18, false, true)) {
				u = true;
			} else {
				r = false;
				l = true;
				u = false;
				Console.log("Turn back left");
				backleft = true;
			}
		} else if ((left) && (l)) {
			int tx = (int) ((x + getxMove() + bounds.x) / Tile.TILEWIDTH + 2.0D);

			if (emptySpaces(-18, -108, -18, -18, true, true)) {
				u = true;
			} else {
				r = true;
				l = false;
				u = false;
				Console.log("Turn back right");
			}
		} else {
			u = false;
		}

		// Check if there's a pit in front of you

		if (!isSafe(-18, 18)) {
			if (isSafe(-108, 72)) {
				u = true;
			} else {
				r = true;
				l = false;
			}
		} else if (!isSafe(18 + width, 18)) {
			if (isSafe(36 + width, 72)) {
				u = true;
			} else {
				r = false;
				l = true;
			}
		}
		
		// Check if it's possible to jump somewhere
		if(Public.chance(0.1)||u) {
			if(checkDowns(-18, -108, 18, -18, false, true))
				u = true;
			if(platforms(-108, -108, 108, -18, false, true))
				u = true;
		}

		// Check jumping
		if (u) {
			if ((bottom) && (yVol > -jumpForce + GRAVITY * 2.0D)) {
				if ((!d) && (yVol > -jumpForce / 2.0D)) {
					yVol = ((float) (yVol - jumpForce));
				}
			}

			if (yVol < 0.0F) {
				yVol = ((float) (yVol - jumpCarry));
			}
		}

		// Check random moving
		if (aitimer >= 500L) {
			if (Math.random() < 0.3D) {
				l = true;
				r = false;
				custState = false;
			} else if (Math.random() < 0.3D) {
				r = true;
				l = false;
				custState = false;
			} else if (Math.random() < 0.2D) {
				r = false;
				l = false;
				custState = false;
			} else {
				r = false;
				l = false;
				custState = true;
			}
			aitimer = 0L;
		}

		if (fleeing != null) {
			boolean[] bs = follow(fleeing, this);

			r = !bs[0];
			l = !bs[1];

			Console.log("Fleeing", fleeing, r, l);

			aitimer = -500;
		} else if (following != null) {
			boolean[] bs = follow(following, this);
			r = bs[0];
			l = bs[1];
			u = bs[2];
		}

		aitimer += 1L;
	}
	
	public boolean platform(int tX, int tY, boolean lr, boolean tb) {
		return checkOff(tX, tY, lr, tb)&&emptySpace(tX, tY-18, lr, tb);
	}
	
	public boolean platforms(int tX1, int tY1, int tX2, int tY2, boolean lr, boolean tb) {

		for (int i = tX1; i <= tX2; i++) {
			for (int j = tY1; j <= tY2; j++) {
				if (platform(i, j, lr, tb))
					return true;
			}
		}

		return false;
	}

	public boolean checkOffs(int tX1, int tY1, int tX2, int tY2, boolean lr, boolean tb) {

		for (int i = tX1; i <= tX2; i++) {
			for (int j = tY1; j <= tY2; j++) {
				if (checkOff(i, j, lr, tb))
					return true;
			}
		}

		return false;
	}
	
	public boolean checkDowns(int tX1, int tY1, int tX2, int tY2, boolean lr, boolean tb) {

		for (int i = tX1; i <= tX2; i++) {
			for (int j = tY1; j <= tY2; j++) {
				if (checkDown(i, j, lr, tb))
					return true;
			}
		}

		return false;
	}
	
	public boolean emptySpaces(int tX1, int tY1, int tX2, int tY2, boolean lr, boolean tb) {

		for (int i = tX1; i <= tX2; i++) {
			for (int j = tY1; j <= tY2; j++) {
				if (!checkOff(i, j, lr, tb))
					return true;
			}
		}

		return false;
	}

	public boolean checkOffStairFormation(int tX1, int tY1, int tX2, int tY2, boolean lr, boolean tb, int xyrat) {
		for (int i = tX1; i <= tX2; i++) {
			for (int j = tY1; j <= tY2; j++) {
				if (checkOff(i, j + xyrat * i, lr, tb))
					return true;
			}
		}

		return false;
	}

	public boolean checkOff(int tX, int tY, boolean lr, boolean tb) {
		int ty = (int) ((y + yMove + bounds.y + bounds.height + tY) / Tile.TILEHEIGHT);
		if (tb)
			ty = (int) ((y + yMove + bounds.y + tY) / Tile.TILEHEIGHT);
		int tx = (int) ((x + bounds.x + bounds.width + tX) / Tile.TILEWIDTH);
		if (lr)
			tx = (int) ((x + bounds.x + tX) / Tile.TILEWIDTH);
		return collisionWithDown(tx, ty);
	}

	public boolean isSafe(int tX, int width) {
		return game.getWorld().SAFETOWALK(x + tX, y, width);
	}
	
	public boolean isSafe(int tX, int tY, int width) {
		return game.getWorld().SAFETOWALK(x + tX, y+tY, width);
	}

	public boolean checkDown(int tX, int tY, boolean lr, boolean tb) {
		int ty = (int) ((y + yMove + bounds.y + bounds.height + tY) / Tile.TILEHEIGHT);
		if (tb)
			ty = (int) ((y + yMove + bounds.y + tY) / Tile.TILEHEIGHT);
		int tx = (int) ((x + bounds.x + bounds.width + tX) / Tile.TILEWIDTH);
		if (lr)
			tx = (int) ((x + bounds.x + tX) / Tile.TILEWIDTH);
		return World.getTile(tx, ty).isTop()&&!World.getTile(tx, ty).isSolid();
	}
	
	public boolean emptySpace(int tX, int tY, boolean lr, boolean tb) {
		int ty = (int) ((y + yMove + bounds.y + bounds.height + tY) / Tile.TILEHEIGHT);
		if (tb)
			ty = (int) ((y + yMove + bounds.y + tY) / Tile.TILEHEIGHT);
		int tx = (int) ((x + bounds.x + bounds.width + tX) / Tile.TILEWIDTH);
		if (lr)
			tx = (int) ((x + bounds.x + tX) / Tile.TILEWIDTH);
		return !collisionWithDown(tx, ty);
	}

	public abstract void custTick();

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

		for (Water w : game.getWorld().getWaterManager().getWater()) {
			if (w.box().intersects(bounds)) {
				inWater = true;
			}
		}
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
	
	@Override
	public void render(Graphics2D g) {
		g.drawImage(com.zandgall.arvopia.gfx.transform.Tran.flip(getFrame(), widthFlip, 1),
				(int) (x - game.getGameCamera().getxOffset()), (int) (y - game.getGameCamera().getyOffset()), null);

		if (health < MAX_HEALTH) {
			showHealthBar(g);
		}
	}

	public abstract BufferedImage getFrame();

}
