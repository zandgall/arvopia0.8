package com.zandgall.arvopia.entity.creatures.basic;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Log;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.entity.creatures.npcs.NPC;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Utils;
import com.zandgall.arvopia.utils.ValueAnimation;
import com.zandgall.arvopia.water.Water;

public class BasicModTemplate extends Creature {

	private static final long serialVersionUID = 4309370043116391037L;

	/*
	 * MAKE SURE THAT YOU DONT USE IMAGES, YOU AREN'T ABLE TO WRITE IMAGES INTO THE
	 * MOD FILE
	 * 
	 * Instead of using images, you can use ValueAnimations which only need a speed
	 * (in seconds) and a max value Then with those values, you can load up the
	 * image you need using ImageLoader.loadImage(name); where name is down below
	 * you can use .getSubimage(x, y, width, height) to get a single frame of your
	 * spritesheet
	 * 
	 * After writing this out, please put it in a folder with an image that's titled
	 * assets.png
	 * 
	 */

//	Name of this creature
	public static final String name = "basicmod";

	public Point[] stillframes, jumpframes, crouchframes, walkframes;

	public ValueAnimation stillvalues, jumpvalues, crouchvalues, walkvalues;

	// Different hostility levels
	public static final int EVERYTHINGATTACKER = 6, HUNTER = 5, PLAYERPREDATOR = 4, PREDATOR = 3, PLAYERATTACKER = 2,
			DEFENSIVE = 1, PASSIVE = 0, FLEER = -1;

	// Change these to your desire
	public static final int width = 18, height = 18, hostility = PASSIVE, range = 20, maxHealth = 10,
			maxSpeed = (int) Creature.MAX_SPEED;
	public static double strength = 4, attackspeed = 2, speed = Creature.DEFAULT_SPEED,
			acceleration = Creature.DEFAULT_ACCELERATION;

	boolean jumping = false;

	// if you're sprite is backwards, try switching this to (positive) 1;
	protected int fullflip = -1;

	protected int widthFlip = 1;
	private long aitimer;
	private boolean u, d, l, r;
	protected boolean custState = false;

	Creature following = null, fleeing = null;

	double attacktimer = 0;

	public BasicModTemplate() {
		super(null, 0, 0, width, height, false, speed * Public.debugRandom(0.8, 1.8), acceleration, maxSpeed, false,
				false, Creature.DEFAULT_JUMP_FORCE, Creature.DEFAULT_JUMP_CARRY, name);

		// Animation and frames
		stillframes = new Point[] { new Point(0, 0), new Point(1, 0) };
		jumpframes = new Point[] { new Point(2, 1) };
		crouchframes = new Point[] { new Point(0, 1), new Point(1, 1) };
		walkframes = new Point[] { new Point(2, 0), new Point(3, 0) };

		stillvalues = new ValueAnimation(0.6, stillframes.length);
		jumpvalues = new ValueAnimation(10, jumpframes.length);
		crouchvalues = new ValueAnimation(0.6, crouchframes.length);
		walkvalues = new ValueAnimation(0.4, walkframes.length);

		// Leave this, it's not important
		this.health = maxHealth;
		this.MAX_HEALTH = maxHealth;

		// Default stuff
		MAX_HEALTH = 20;
		health = 20.0D;

		bounds.x = 0;
		bounds.y = 0;
		bounds.width = 18;
		bounds.height = 18;

	}

	// Anything that's random goes in here
	public void setup() {
		layer = Public.debugRandom(-0.01D, 0.01D);
	}

	public void tick() {

		// Ticks value animations for your display animations
		stillvalues.tick();
		walkvalues.tick();
		jumpvalues.tick();
		crouchvalues.tick();

		targeting();

		aiMove();

		move();
	}

	// You're going to want to leave this alone, although look into it only if
	// you're absolutely sure
	// This checks for something to change, and/or something to run away depending
	// on it's hostility
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

				} else if (c instanceof Player && (((Player) c).targets.contains(c))) {
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
				} else if (c instanceof Player && (((Player) c).targets.contains(c))) {
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
				} else if (c instanceof Player && (((Player) c).targets.contains(c))) {
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

	// WARNING: THIS IS VERY CLUTTERED AND COMPLICATED, GO IN WITH CARE AND BE
	// CAREFUL WHAT YOU EDIT
	public void aiMove() {

		// Set velocity based on directions
		setxMove(0.0F);
		yMove = 0.0F;

		if (xVol > 0.0D) {
			setxMove((float) (getxMove() + (speed + xVol)));
		}
		if (xVol < 0.0D) {
			setxMove((float) (getxMove() + (-speed + xVol)));
		}
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

		// If hitting wall, check space above to jump to
		if ((right) && (r)) {
			int tx = (int) ((x + getxMove() + bounds.x + bounds.width + 2.0D) / Tile.TILEWIDTH);

			if (!collisionWithTile(tx, (int) (y + bounds.y - 36.0D) / Tile.TILEHEIGHT)) {
				u = true;
			} else {
				r = false;
				l = true;
				u = false;
			}
		} else if ((left) && (l)) {
			int tx = (int) ((x + getxMove() + bounds.x) / Tile.TILEWIDTH + 2.0D);

			if (!collisionWithTile(tx, (int) (y + bounds.y - 36.0D) / Tile.TILEHEIGHT)) {
				u = true;
			} else {
				r = true;
				l = false;
				u = false;
			}
		} else {
			u = false;
		}

		// Check jumping
		if (u) {
			if ((bottoms) && (yVol > -DEFAULT_JUMP_FORCE + GRAVITY * 2.0D)) {
				if ((!d) && (yVol > -DEFAULT_JUMP_FORCE / 2.0D)) {
					yVol = ((float) (yVol - DEFAULT_JUMP_FORCE));
				}
			} else if (yVol < 0.0F) {
				yVol = ((float) (yVol - DEFAULT_JUMP_CARRY));
			}
		}

		// Check random moving
		if (aitimer >= 500L) {
			if (Math.random() < 0.3D) {
				l = true;
				r = false;
				custState = false;
			} else if (Math.random() < 0.6D) {
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

			aitimer = -500;
		} else if (following != null) {
			boolean[] bs = follow(following, this);
			r = bs[0];
			l = bs[1];
			u = bs[2];
		}

		// Check if there's a pit in front of you
		int ty = (int) ((y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT);
		if (!checkOffStairFormation(-80, 72, -20, 144, true, false, -1)
				&& !checkOffStairFormation(-80, 72, -20, 144, false, false, -1)
				&& (y + bounds.y + bounds.height < ty * Tile.TILEHEIGHT + 4)) {
			r = true;
			l = false;
		} else if (!checkOffStairFormation(24, 0, 80, 72, true, false, 1)
				&& !checkOffStairFormation(24, 0, 80, 72, true, false, 1)
				&& (y + bounds.y + bounds.height < ty * Tile.TILEHEIGHT + 4)) {
			r = false;
			l = true;
		}

//		if (!checkOffs(-20, 0, -20, 72, true, false) && !checkOffs(-20, 0, -20, 72, false, false)
//				&& (y + bounds.y + bounds.height < ty * Tile.TILEHEIGHT + 4)) {
//			r = true;
//			l = false;
//		} else if (!checkOffs(24, 0, 24, 72, true, false) && !checkOffs(-24, 0, -24, 72, false, false)
//				&& (y + bounds.y + bounds.height < ty * Tile.TILEHEIGHT + 4)) {
//			r = false;
//			l = true;
//		}

		aitimer += 1L;
	}

	// LEAVE THIS
	public boolean checkOffs(int tX1, int tY1, int tX2, int tY2, boolean lr, boolean tb) {

		for (int i = tX1; i <= tX2; i++) {
			for (int j = tY1; j <= tY2; j++) {
				if (checkOff(i, j, lr, tb))
					return true;
			}
		}

		return false;
	}

	// LEAVE THIS
	public boolean checkOffStairFormation(int tX1, int tY1, int tX2, int tY2, boolean lr, boolean tb, int xyrat) {
		for (int i = tX1; i <= tX2; i++) {
			for (int j = tY1; j <= tY2; j++) {
				if (checkOff(i, j + xyrat * i, lr, tb))
					return true;
			}
		}

		return false;
	}

	// LEAVE THIS
	public boolean checkOff(int tX, int tY, boolean lr, boolean tb) {
		int ty = (int) ((y + yMove + bounds.y + bounds.height + tY) / Tile.TILEHEIGHT);
		if (tb)
			ty = (int) ((y + yMove + bounds.y + tY) / Tile.TILEHEIGHT);
		int tx = (int) ((x + bounds.x + bounds.width + tX) / Tile.TILEWIDTH);
		if (lr)
			tx = (int) ((x + bounds.x + tX) / Tile.TILEWIDTH);
		return collisionWithDown(tx, ty);
	}

	// This chacks the collision, you probably want to leave this alone
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
		g.drawImage(com.zandgall.arvopia.gfx.transform.Tran.flip(getFrame(), widthFlip * fullflip, 1),
				(int) (x - game.getGameCamera().getxOffset()), (int) (y - game.getGameCamera().getyOffset()), null);

		if (health < MAX_HEALTH) {
			showHealthBar(g);
		}
	}

	public BufferedImage getFrame() {
		if (d) {
			Point r = crouchframes[crouchvalues.intValue()];
			return ImageLoader.loadImage(name).getSubimage(r.x * width, r.y * height, width, height);
		}
		if ((l) || (r)) {
			Point r = walkframes[walkvalues.intValue()];
			return ImageLoader.loadImage(name).getSubimage(r.x * width, r.y * height, width, height);
		}
		if (bottoms) {
			Point r = stillframes[stillvalues.intValue()];
			return ImageLoader.loadImage(name).getSubimage(r.x * width, r.y * height, width, height);
		}
		Point r = jumpframes[jumpvalues.intValue()];
		return ImageLoader.loadImage(name).getSubimage(r.x * width, r.y * height, width, height);
	}
	
	public static void export() throws IOException {
		Utils.createDirectory("C:\\Arvopia");
		Utils.createDirectory("C:\\Arvopia\\Mod export");
		Utils.createDirectory("C:\\Arvopia\\Mod export\\Creatures");
		Utils.createDirectory("C:\\Arvopia\\Mod export\\Creatures\\"+BasicModTemplate.name);
		

		Log log = new Log("C:\\Arvopia\\Mod export\\Creatures\\"+BasicModTemplate.name+"\\Main", "Log");
		log.log("Writing...");

		Game game = new Game("Exporting...", 720, 400, false, log);

		Public.init(new Handler(game));

		Utils.fileWriter(BasicModTemplate.name, "C:\\Arvopia\\Mod export\\name.txt");
		FileLoader.writeObjects("C:\\Arvopia\\Mod export\\" + BasicModTemplate.name + ".arv",
				new Object[] { new BasicModTemplate() });

		log.log("Successfully wrote " + BasicModTemplate.name);
	}

	// Run this when you want to export the mod
	// Exports to "C:\\Arvopia\\Mod Export\\"
	// (Instructions on mod packing here)
	public static void main(String[] args) throws IOException {
		BasicModTemplate.export();
	}

}
