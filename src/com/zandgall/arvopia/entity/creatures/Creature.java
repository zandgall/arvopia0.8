package com.zandgall.arvopia.entity.creatures;

import com.zandgall.arvopia.ArvopiaLauncher;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.entity.EntityAdder;
import com.zandgall.arvopia.entity.EntityManager;
import com.zandgall.arvopia.entity.creatures.npcs.NPC;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.particles.Damage;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.tiles.Bridge;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.BevelIndent;
import com.zandgall.arvopia.utils.ClassLoading;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class Creature extends Entity {
	private static final long serialVersionUID = 16969696420L;

	public static long colt = 0, movext = 0, moveyt = 0, soundst = 0;

	public static double DEFAULT_SPEED = 2.0D;
	public static double DEFAULT_ACCELERATION = 0.01D;
	public static double GRAVITY = 0.1D;
	public static double MAX_SPEED = 2.0D;
	public static double DEFAULT_JUMP_FORCE = 2.0D;
	public static double DEFAULT_JUMP_CARRY = 0.05D;
	public static double FRICTION = 0.05D;

	public static final int DEFAULT_HEALTH = 10;
	public static final int DEFAULT_CREATURE_WIDTH = 18;
	public static final int DEFAULT_CREATURE_HEIGHT = 18;
	public double health;

	public String getName() {
		return name;
	}

	protected boolean direction;

	protected double speed;

	protected double orSpeed;

	protected double acceleration;

	protected String name;

	protected int maxSpeed;

	protected double xMove;

	protected double yMove;

	public double xVol;
	public double yVol, jumpForce, jumpCarry;
	protected boolean flies;
	public int MAX_HEALTH;
	protected long walkTimer;

	public BevelIndent healthBody, healthGreen;

	public Creature(Handler handler, double x, double y, int width, int height, boolean direction, double speed,
			double acceleration, int maxSpeed, boolean flies, boolean solid, double jumpForce, double jumpCarry,
			String name) {
		super(handler, x, y, width, height, solid, true, false, false);
		this.direction = direction;
		this.speed = speed;
		orSpeed = speed;
		this.acceleration = acceleration;
		this.maxSpeed = maxSpeed;
		this.name = name;
		this.jumpForce = jumpForce;
		this.jumpCarry = jumpCarry;

		this.flies = flies;

		setxMove(0.0F);
		yMove = 0.0F;
		xVol = 0.0D;
		yVol = 0.0F;
	}

	public boolean sounded = false;

	int soundIndex = (int) Public.random(0, 2);

	public void initSounds() {
		sounded = true;

		game.addSound("Sounds/GrassWalk1.ogg", "GrassWalk1" + name + soundIndex, false, (int) 0, (int) 0, 0);
		game.addSound("Sounds/GrassWalk2.ogg", "GrassWalk2" + name + soundIndex, false, (int) 0, (int) 0, 0);
		game.addSound("Sounds/SnowWalk1.ogg", "SnowWalk1" + name + soundIndex, false, (int) 0, (int) 0, 0);
		game.addSound("Sounds/SnowWalk2.ogg", "SnowWalk2" + name + soundIndex, false, (int) 0, (int) 0, 0);
		game.addSound("Sounds/WoodWalk1.ogg", "WoodWalk1" + name + soundIndex, false, (int) 0, (int) 0, 0);
		game.addSound("Sounds/WoodWalk2.ogg", "WoodWalk2" + name + soundIndex, false, (int) 0, (int) 0, 0);
	}

	public abstract void tick();

	public Tile getTile(int x, int y) {
		return World.getTile(x, y);
	}

	public static Tile Tile(int x, int y) {
		return World.getTile(x, y);
	}

	public boolean collisionWithTile(double x, double y) {

		Tile t = World.getTile((int) (x / 18), (int) (y / 18));

		int offX = (int) (x - (Math.floor(x / 18) * 18));
		int offY = (int) (y - (Math.floor(y / 18) * 18));

		boolean ycheck = offY > t.getY(offX);

		return t.isSolid() && ycheck;
	}

	public boolean collisionLeft() {

		boolean out = false;

		int offX = (int) ((x + bounds.x + xMove)
				- (Math.floor((x + bounds.x + xMove) / 1) * 18));

		for (double i = 0.1; i < 0.8; i += 0.1) {
			Tile t = World.getTile((int) ((x + bounds.x + xMove) / 18),
					(int) ((y + bounds.y + bounds.height * i) / 18));

			int offY = (int) ((y + bounds.y + bounds.height * i)
					- (Math.floor((y + bounds.y + bounds.height * i) / 18) * 18));

			boolean ycheck = offY > t.getY(offX);
			out = out || (t.isSolid() && ycheck);
		}
		return out;
	}

	public boolean collisionRight() {

		boolean out = false;

		int offX = (int) ((x + bounds.x + xMove + bounds.width)
				- (Math.floor((x + bounds.x + xMove + bounds.width) / 18) * 18));

		for (double i = 0.1; i < 0.8; i += 0.1) {
			Tile t = World.getTile((int) ((x + bounds.x + xMove + bounds.width) / 18),
					(int) ((y + bounds.y + bounds.height * i) / 18));

			int offY = (int) ((y + bounds.y + bounds.height * i)
					- (Math.floor((y + bounds.y + bounds.height * i) / 18) * 18));

			boolean ycheck = offY > t.getY(offX);
			out = out || (t.isSolid() && ycheck);
		}
		return out;
	}

	public boolean collisionBottom() {

		int tyv = (int) ((y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT);

		int x1 = (int) ((x + bounds.x) / 18);
		int x2 = (int) ((x + bounds.x + bounds.width) / 18);

		Tile t = World.getTile(x1, tyv);
		Tile t1 = World.getTile(x2, tyv);

		int offX = (int) (x + bounds.x - (x1 * 18));
		int offX1 = (int) (x + bounds.x + bounds.width - (x2 * 18));

//		int offY = Math.min(t.getY(offX), t1.getY(offX1));
//
//		if (!(t.isSolid() || t.isTop()))
//			offY = t1.getY(offX1);
//		if (!(t1.isSolid() || t1.isTop()))
//			offY = t.getY(offX);
//
//		boolean ycheck = y + yMove + bounds.y + bounds.height >= offY;

		return ((t.isSolid() || (t.isTop() && yMove >= 0)) && y + yMove + bounds.y + bounds.height >= t.getY(offX)) ||
				((t1.isSolid() || (t1.isTop() && yMove >= 0)) && y + yMove + bounds.y + bounds.height >= t1.getY(offX1));
	}

	public boolean collisionBottoms() {

		int tyv = (int) ((y + yMove + bounds.y + bounds.height + 2) / Tile.TILEHEIGHT);

		int x1 = (int) ((x + bounds.x) / 18);
		int x2 = (int) ((x + bounds.x + bounds.width) / 18);

		Tile t = World.getTile(x1, tyv);
		Tile t1 = World.getTile(x2, tyv);

		int offX = (int) (x + bounds.x - (x1 * 18));
		int offX1 = (int) (x + bounds.x + bounds.width - (x2 * 18));

//		int offY = Math.min(t.getY(offX), t1.getY(offX1));

//		if (!(t.isSolid() || t.isTop()))
//			offY = t1.getY(offX1);
//		if (!(t1.isSolid() || t1.isTop()))
//			offY = t.getY(offX);

//		boolean ycheck = y + yMove + bounds.y + bounds.height + 2 >= offY;

		return ((t.isSolid() || (t.isTop() && yMove >= 0)) && y + yMove + bounds.y + bounds.height+2 >= t.getY(offX)) ||
				((t1.isSolid() || (t1.isTop() && yMove >= 0)) && y + yMove + bounds.y + bounds.height+2 >= t1.getY(offX1));
	}

	public boolean collisionWithDown(double x, double y) {

		Tile t = World.getTile((int) (x / 18), (int) (y / 18));

		int offX = (int) (x - (Math.floor(x / 18) * 18));
		int offY = (int) (y - (Math.floor(y / 18) * 18));

		boolean ycheck = offY >= t.getY(offX);

		return (t.isSolid() || t.isTop()) && ycheck;
	}

	public boolean collisionWithTile(int x, int y) {
		return World.getTile(x, y).isSolid();
	}

	public static boolean collisionTile(int x, int y) {
		return World.getTile((int) Math.floor(x / Tile.TILEWIDTH), (int) Math.floor(y / Tile.TILEHEIGHT)).isTop();
	}

	public boolean collisionWithDown(int x, int y) {
		return (World.getTile(x, y).isSolid()) || (World.getTile(x, y).isTop());
	}

	public void regen() {
		int olHealth = (int) health;
		if ((int) (health + 0.005D) > (int) health)
			speed = orSpeed;
		if (health < MAX_HEALTH)
			health += 0.005D;
		if (health > MAX_HEALTH) {
			health = MAX_HEALTH;
		}

		if ((int) health != olHealth)
			game.getWorld().getParticleManager().add(new Damage(game, centerX(), centerY(), 1));
	}

	public long last = System.currentTimeMillis();
	
	public void move() {
		
		if (!sounded && game != null) {
			initSounds();
		}

		if (inWater) {
			FRICTION = 1.0E-4D;
			GRAVITY = 0.001D;
			DEFAULT_JUMP_FORCE = 1.0D;
		} else {
			FRICTION = 0.05D;
			GRAVITY = 0.1D;
			DEFAULT_JUMP_FORCE = 2.0D;
		}

		long pre = System.nanoTime();
		moveSoundsHandler();
		soundst = System.nanoTime()-pre;
		pre = System.nanoTime();

		checkCol();
		bottoms = bottom || forcedBottom;

		colt = System.nanoTime()-pre;
		pre = System.nanoTime();

		if ((flies) || (inWater)) {
			if (!flies) {
				yVol = ((double) (yVol * (1.0D - FRICTION * 50.0D)));
				yMove = ((double) Math.floor(yVol));
				if (xMove < 0.0F)
					xMove = ((double) (xMove * (1.0D - FRICTION)));
				else
					xMove = ((double) (xMove * (1.0D - FRICTION / 100.0D)));
			}
			moveX();
			flY();
		} else {
			moveY();
			moveyt =(System.nanoTime()-pre);
			pre = System.nanoTime();
			moveX();
			movext = (System.nanoTime()-pre);
		}
//		colt*=3;
		last = System.currentTimeMillis();
	}

	private void moveSoundsHandler() {

		if ((bottoms) && walkTimer > 20 && Math.abs(xMove) > 0.01) {
			Tile tile = (World.getTile(Public.grid(centerX(), 18.0D, 0.0D),
					Public.grid(y + bounds.y + bounds.height + 1.0D, 18.0D, 0.0D)));
			// Set the timer to zero so there isn't overlap of sounds
			walkTimer = 0L;

			// Choose 1 or 2 for the sound to play
			int i = (int) Public.random(1.0D, 2.0D);

			String file = "";

			// If this tile selected is solid
			if (tile.isSolid()) {
				if (getShrub(0.0F, 0.0F).snowy > 3) {
					file = "SnowWalk";
				} else {
					file = "GrassWalk";
				}
			} else if (tile instanceof Bridge) {
				file = "WoodWalk";
			}
			if (file != "") {
				//game.soundSystem.stop(file + i + name + soundIndex);
				if (false) {//game.soundSystem.getVolume(file + i + name + soundIndex) > 0) {
					game.setPosition(file + i + name + soundIndex, (int) x, (int) y, 0);
					game.play(file + i + name + soundIndex);
				}
			}
		}

		walkTimer += 1L;

	}

	public void moveX() {
		checkCol();
		
		double rat = 1;
		
//		if(game.getWorld().center==this)
//			rat = (System.currentTimeMillis()-last)/13.2;

		if (getxMove() > 0.0F) {
			int tx = (int) (x + getxMove() + bounds.x + bounds.width) / Tile.TILEWIDTH;
			if (!right) {
				x += (inWater ? xMove / 2.0 : xMove)*rat;
//				x = Math.round(x);
			} else {
				if (colRight(getxMove(), 0.0)) {
					x = getEntity(xMove, 0.0).getX() + getEntity(xMove, 0.0f).getbounds().x - bounds.x - bounds.width;
				} else if (right) {
					x = (tx * Tile.TILEWIDTH - bounds.x - bounds.width - 1);
				}
				xVol = 0.0D;
			}
		} else if (getxMove() < 0.0F) {
			int tx = (int) (x + getxMove() + bounds.x) / Tile.TILEWIDTH;
			if (!left) {
				x += (inWater ? xMove / 2.0F : xMove)*rat;
//				x = Math.round(x);
			} else {
				if (colLeft(getxMove(), 0.0F)) {
					x = getEntity(xMove, 0.0F).getX() + getEntity(xMove, 0.0F).getbounds().width - bounds.x;
				} else if (left) {
					x = (tx * Tile.TILEWIDTH + 18 - bounds.x + 1);
				}
				xVol = 0.0D;
			}
		}
	}

	protected int getTileYOffset() {
		int tyv = (int) (y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT;

		Tile t = World.getTile((int) ((x + bounds.x) / 18), tyv);
		Tile t1 = World.getTile((int) ((x + bounds.x + bounds.width) / 18), tyv);

		int offX = (int) (x + bounds.x - (Math.floor((x + bounds.x) / 18) * 18));
		int offX1 = (int) ((x + bounds.x + bounds.width) - (Math.floor((x + bounds.x + bounds.width) / 18) * 18));
		int offY = Math.min(t.getY(offX), t1.getY(offX1));
		return offY;
	}

	public void moveY() {
		
		double rat = 1;
		
		yMove = ((double) Math.floor(yVol));
		yMove*=rat;

		checkCol();

		int tyv = (int) (y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT;

		int offY = getTileYOffset();

		if (!bottom) {
			yVol = ((double) (yVol + GRAVITY));
		} else {
			if (yMove > 0 && colBottom(0.0F, yMove))
				y = (getEntity(0.0f, yMove).getY() + getEntity(0.0f, yMove).getbounds().y - bounds.y - bounds.height);
			else if (down) {
				y = (tyv * Tile.TILEHEIGHT - bounds.y - bounds.height + offY);
			} else if (bottom) {
				y = (tyv * Tile.TILEHEIGHT - bounds.y - bounds.height - 1 + offY);
			}
			yVol = 0.0F;
		}

		if (!bottom) {
			y += yMove;
		} else {
			if (yMove > 0 && colBottom(0.0F, yMove))
				y = (getEntity(0.0f, yMove).getY() + getEntity(0.0f, yMove).getbounds().y - bounds.y - bounds.height);
			else if (bottom) {
				y = (tyv * Tile.TILEHEIGHT - bounds.y - bounds.height - 1 + offY);
			}
			yVol = 0.0F;
		}

		if (yMove < 0.0F) {
			int ty = (int) (y + yMove + bounds.y) / Tile.TILEHEIGHT;
			if (!top) {
				y += yMove;
			} else {
				yVol = ((double) DEFAULT_JUMP_FORCE);
				if (colTop(0.0F, yMove)) {
					y = (getEntity(0.0f, yMove).getY() + getEntity(0.0f, yMove).getbounds().y
							+ getEntity(0.0f, yMove).getHeight() - bounds.y);
					yVol = Math.max(2, -yVol);
					yMove = Math.max(2, -yMove);
				} else if (top) {
					y = (ty * Tile.TILEHEIGHT + Tile.TILEHEIGHT - bounds.y);
				}
			}
		}
	}

	public boolean left;
	public boolean right;
	public boolean top;
	public boolean bottom, forcedBottom;

	public void flY() {
		checkCol();

		if (yMove > 0.0F) {
			int ty = (int) (y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT;
			if (!bottom) {
				y += (inWater ? yMove / 2.0F : yMove);
			} else {
				y = (ty * Tile.TILEHEIGHT - bounds.y - bounds.height - 1);
				yVol = 0.0F;
			}
		} else if (yMove < 0.0F) {
			int ty = (int) (y + yMove + bounds.y) / Tile.TILEHEIGHT;
			if (!top) {
				y += (inWater ? yMove / 2.0F : yMove);
			} else {
				y = (ty * Tile.TILEHEIGHT + Tile.TILEHEIGHT - bounds.y);
				yVol = 0.0F;
			}
		}
	}

	public boolean down;
	public boolean lefts;
	public boolean rights;

	public abstract void checkCol();

	public void reset() {
	}

	public void damage(double d) {
		speed = (orSpeed * 1.5D);
		health -= d;
		if (health < 1.0D) {
			health = 0.0D;
			game.getWorld().kill(this);
		} else {
			game.log("Damaged: " + health + " left for " + this);
		}
		game.getWorld().getParticleManager().add(new Damage(game, centerX(), centerY(), -d));
	}

	public boolean tops;

	public void setHealth(int health) {
		if (health > MAX_HEALTH) {
			this.health = MAX_HEALTH;
		} else
			this.health = health;
	}

	public boolean bottoms;

	public void addHealth(int amount) {
		if (health + amount > MAX_HEALTH) {
			health = MAX_HEALTH;
		} else
			health += amount;
		game.getWorld().getParticleManager().add(new Damage(game, centerX(), centerY(), amount));
	}

	public boolean inWater;
	public boolean touchingWater;

	public static void init() {
		foxWalk = new Animation(250, new BufferedImage[] { com.zandgall.arvopia.gfx.PublicAssets.fox[0],
				com.zandgall.arvopia.gfx.PublicAssets.fox[1] }, "Walk", "Fox");
		foxSit = new Animation(750, new BufferedImage[] { com.zandgall.arvopia.gfx.PublicAssets.fox[2],
				com.zandgall.arvopia.gfx.PublicAssets.fox[3] }, "Sit", "Fox");
		foxStill = new Animation(750, new BufferedImage[] { com.zandgall.arvopia.gfx.PublicAssets.fox[6],
				com.zandgall.arvopia.gfx.PublicAssets.fox[7] }, "Still", "Fox");
		butterflyForward = new Animation(100, new BufferedImage[] { com.zandgall.arvopia.gfx.PublicAssets.butterfly[0],
				com.zandgall.arvopia.gfx.PublicAssets.butterfly[1], com.zandgall.arvopia.gfx.PublicAssets.butterfly[2],
				com.zandgall.arvopia.gfx.PublicAssets.butterfly[3] }, "Forward", "Butterfly");
		butterflyBackward = new Animation(100, new BufferedImage[] { com.zandgall.arvopia.gfx.PublicAssets.butterfly[4],
				com.zandgall.arvopia.gfx.PublicAssets.butterfly[5], com.zandgall.arvopia.gfx.PublicAssets.butterfly[6],
				com.zandgall.arvopia.gfx.PublicAssets.butterfly[7] }, "Backward", "Butterfly");

	}

	public static Animation foxWalk;

	public void showHealthBar(Graphics g) {

		if (healthBody == null) {
			healthBody = new BevelIndent(2, 2, Math.min(Math.max(5 * MAX_HEALTH, 20), 50), 10, 400, 50);
			healthBody.affectImage(Color.red);
			healthGreen = new BevelIndent(2, 2, Math.min(Math.max(5 * MAX_HEALTH, 20), 50), 10, 400, 50);
			healthGreen.affectImage(Color.green);
		}

		int Y = (int) (y + bounds.y - game.getGameCamera().getyOffset());

//		int width = Math.min(Math.max(5 * MAX_HEALTH, 20), 50);
		int width = healthBody.getImage().getWidth();

		int X = (int) (x + bounds.x + bounds.width / 2 - game.getGameCamera().getxOffset() - width / 2 + 5.0D);

//		g.setColor(Color.red);
//		g.fillRect(X, Y - 20, width, 10);
//		g.setColor(Color.green);
//		g.fillRect(X, Y - 20, width * (int) health / MAX_HEALTH, 10);
//		g.drawRect(X, Y - 20, width, 10);
		healthBody.render((Graphics2D) g, X, Y - 20);
		g.drawImage(healthGreen.getImage().getSubimage(0, 0,
				(int) Public.range(1, width, width * (health / MAX_HEALTH)), 10), X, Y - 20, null);
		g.setColor(Color.black);
		g.drawString("" + (int) health, X + 2, Y - 10);
		g.drawString(name, X + 2, Y);
	}

	public static Animation foxSit;

	public boolean tileOffEmp(int xoff, int yoff, int width, int height) {
		boolean is = true;

		for (int h = xoff; h < width; h++) {
			for (int v = yoff; v < height; v++) {
				int tx = (int) ((x + bounds.x + bounds.width / 2) / Tile.TILEWIDTH) + h;
				int ty = (int) ((y + bounds.y + bounds.height) / Tile.TILEHEIGHT) + v;

				if (collisionWithDown(tx, ty)) {
					is = false;
				}
			}
		}

		return is;
	}

	public static Animation foxStill;

	public double getxMove() {
		return xMove;
	}

	public double getyMove() {
		return yMove;
	}

	static Animation butterflyForward;
	static Animation butterflyBackward;

	public void setxMove(double xMove) {
		this.xMove = xMove;
	}

	public void setyMove(double yMove) {
		this.yMove = yMove;
	}

	public static boolean[] follow(Creature following, Creature follower) {

		boolean r = false, l = false, u = false, d = false;

		if (following.getX() > follower.getX() + follower.width + 10)
			r = true;
		if (following.getX() + following.width < follower.getX() - 10)
			l = true;

		if (((following.getY() < follower.getY() - 36 || (following.getY() < follower.getY() && !follower.bottoms))
				&& (!(l || r)
						|| ArvopiaLauncher.game.handler.getWorld().SAFETOWALK(follower.getX() - 20,
								follower.getY() + follower.height, follower.height)
						|| ArvopiaLauncher.game.handler.getWorld().SAFETOWALK(follower.getX() + follower.width + 20,
								follower.getY() + follower.height, follower.height)))
				|| (follower.right && r) || (follower.left && l))
			u = true;

		return new boolean[] { r, l, u, d };
	}

	public static double[] flyFollow(Creature following, Creature follower) {

		double h, v;

		double angle = Math.atan2(following.centerY() - follower.centerY(), following.centerX() - follower.centerX());
		h = Math.cos(angle);
		v = Math.sin(angle);

		return new double[] { h, v };
	}

	public void kill() {
		game.getWorld().getEntityManager().getEntities().remove(this);

		//game.soundSystem.unloadSound("GrassWalk1" + layer);
		//game.soundSystem.unloadSound("GrassWalk2" + layer);
		//game.soundSystem.unloadSound("WoodWalk1" + layer);
		//game.soundSystem.unloadSound("WoodWalk2" + layer);
		//game.soundSystem.unloadSound("SnowWalk1" + layer);
		//game.soundSystem.unloadSound("SnowWalk2" + layer);
	}

	public static void loadJar(String jarfile, Handler handler, EntityManager e) {
		try {
			ArrayList<Object> objects = ClassLoading.loadObjects(jarfile);
			ArrayList<Creature> creatures = new ArrayList<Creature>();
			for (Object o : objects)
				if ((o instanceof Creature) && !(o instanceof NPC)) {
					Creature c = (Creature) o;
					c.game = handler;
					c.initSounds();
					creatures.add(c);
				}

			for (Creature out : creatures)
				e.adders.add(new EntityAdder(e, out));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void loadModCreature(String directory, Handler handler, EntityManager e) {
		try {
			long pre = System.currentTimeMillis();
			String name = FileLoader.readFile(directory + "/name.txt").replaceAll(" ", "");
			Creature out = (Creature) FileLoader.readObjects(directory + "/" + name + ".arv", 1)[0];
			ImageLoader.addRedirect(name, ImageLoader.loadImageEX(directory + "/assets.png"));
			out.name = name;
			out.game = handler;
			out.setup();
			out.initSounds();

//			e.adders.add(new EntityAdder(handler, directory, name));
			e.adders.add(new EntityAdder(e, out));

			pre = System.currentTimeMillis() - pre;
			handler.log("~~~~~~LOADED MOD NPC ~~~ " + name + " ~~~ ");
			handler.log("TIME TAKEN " + pre);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setup() {

	}

	public static Creature loadModCreature(String directory, Handler handler, int x, int y) {
		try {
			long pre = System.currentTimeMillis();
			String name = FileLoader.readFile(directory + "/name.txt").replaceAll(" ", "");
			Creature out = (Creature) FileLoader.readObjects(directory + "/" + name + ".arv", 1)[0];
			ImageLoader.addRedirect(name, ImageLoader.loadImageEX(directory + "/assets.png"));
			out.name = name;
			out.game = handler;
			out.x = x;
			out.y = y;
			out.setup();
			out.initSounds();

			pre = System.currentTimeMillis() - pre;
			handler.log("~~~~~~LOADED MOD CREATURE ~~~ " + name + " ~~~ ");
			handler.log("TIME TAKEN " + pre);

			return out;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
