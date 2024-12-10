package com.zandgall.arvopia.entity.creatures;

import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.environment.Light;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.items.tools.Sword;
import com.zandgall.arvopia.items.tools.Torch;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.water.Water;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Cannibal extends Creature {
	private static final long serialVersionUID = 1L;
	
	private Torch torch;
	private Sword sword;
	private PlayerItem currentTool;
	Light light;
	private double lastHeight;
	private boolean attackReady;
	private Rectangle attack;
	private boolean attacking;
	private boolean primed;
	private boolean chasing;
	private int attackDelay;
	private int damage;
	private int delayRange;
	private long timer;
	private long attackTimer;
	private Animation jump;
	private Animation still;
	private Animation crouch;
	private Animation walk;
	private Animation punch;
	private Animation hold;
	private Animation stab;
	private BufferedImage airKick;
	private boolean jumping = false;

	int renderCount = 0;
	int widthFlip = 1;
	public int lives;
	private boolean rChange;
	private boolean lChange;
	private boolean r;
	private boolean l;
	private boolean d;
	public boolean alpha;
	public double walkSpeed;

	public Cannibal(Handler handler, double x, double y) {
		super(null, x-9, y-47, 18, 47, true, DEFAULT_SPEED, DEFAULT_ACCELERATION, (int) MAX_SPEED, false, false,
				DEFAULT_JUMP_FORCE, DEFAULT_JUMP_CARRY, "Cannibal");

		walkSpeed = speed;

		attack = new Rectangle((int) x - width / 2, (int) y, 72, 54);

		health = 10.0D;
		MAX_HEALTH = 10;

		layer = Public.expandedRand(-3.0D, -1.0D);

		attackReady = true;
		attacking = false;
		attackDelay = 60;

		timer = 0L;
		attackTimer = 0L;

		if (Public.chance(10)) {
			if(game.getWorld()!=null&&game.getEntityManager()!=null&&game.getPlayer()!=null)
				currentTool = game.getPlayer().getItem("Torch");
			delayRange = 15;
			attackDelay = 15;
			damage = 1;
		} else if (Math.random() < 0.2D) {
			if(game.getWorld()!=null&&game.getEntityManager()!=null&&game.getPlayer()!=null)
				currentTool = game.getPlayer().getItem("Sword");
			delayRange = 15;
			attackDelay = 15;
			damage = 2;
		} else {
			currentTool = null;
			delayRange = 15;
			attackDelay = 15;
			damage = 1;
		}

		this.lives = 1;

		torch = new Torch(handler);
		sword = new Sword(handler);

		light = new Light(game, 0, 0, 10, 50, Color.orange);

		bounds.x = 12;
		bounds.y = 8;
		bounds.width = 10;
		bounds.height = 45;

		jump = PublicAssets.cjump;
		still = PublicAssets.cstill;
		walk = PublicAssets.cwalk;
		crouch = PublicAssets.ccrouch;

		punch = PublicAssets.cpunch;
		airKick = PublicAssets.cairKick;

		hold = PublicAssets.chold;

		stab = PublicAssets.cstab;
	}

	public Cannibal(Handler handler, double x, double y, double speed, int lives, boolean alpha) {
		super(handler, x, y, 18, 47, true, speed, DEFAULT_ACCELERATION, (int) MAX_SPEED, false, false,
				DEFAULT_JUMP_FORCE, DEFAULT_JUMP_CARRY, "Cannibal");

		walkSpeed = speed;

		attack = new Rectangle((int) x - width / 2, (int) y, 72, 54);

		health = 10.0D;
		MAX_HEALTH = 10;

		layer = Public.expandedRand(-3.0D, -1.0D);

		attackReady = true;
		attacking = false;
		attackDelay = 60;

		timer = 0L;
		attackTimer = 0L;

		if (alpha) {
			currentTool = game.getPlayer().getItem("Torch");
			delayRange = 15;
			attackDelay = 15;
			damage = 1;
		} else if (Math.random() < 0.2D) {
			currentTool = game.getPlayer().getItem("Sword");
			delayRange = 15;
			attackDelay = 15;
			damage = 2;
		} else {
			currentTool = null;
			delayRange = 15;
			attackDelay = 15;
			damage = 1;
		}

		this.lives = lives;

		torch = new Torch(handler);
		sword = new Sword(handler);
		
		light = new Light(game, 0, 0, 10, 50, Color.orange);

		bounds.x = 12;
		bounds.y = 8;
		bounds.width = 10;
		bounds.height = 45;

		jump = PublicAssets.cjump;
		still = PublicAssets.cstill;
		walk = PublicAssets.cwalk;
		crouch = PublicAssets.ccrouch;

		punch = PublicAssets.cpunch;
		airKick = PublicAssets.cairKick;

		hold = PublicAssets.chold;

		stab = PublicAssets.cstab;
		
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

		for (Water w : game.getWorld().getWaterManager().getWater()) {
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
						|| (collisionWithDown((int) ((x + bounds.x + bounds.width + 2.0D) / Tile.WIDTH), ty)))
						&& (y + bounds.y + bounds.height <= ty * Tile.HEIGHT + 1) && (!jumping))) {
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
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 2) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 4) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height * 0.75D) / Tile.HEIGHT))
				|| (checkCollision(getxMove() + 1.0F, 0.0F))) {
			right = true;
		}
		tx = (int) ((x + getxMove() + bounds.x + bounds.width + 2.0D) / Tile.WIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 2) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 4) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height * 0.75D) / Tile.HEIGHT))
				|| (checkCollision(getxMove() + 1.0F, 0.0F))) {
			rights = true;
		}

		tx = (int) ((x + getxMove() + bounds.x) / Tile.WIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 2) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 4) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height * 0.75D) / Tile.HEIGHT))
				|| (checkCollision(getxMove(), 0.0F))) {
			left = true;
		}
		tx = (int) ((x + getxMove() + bounds.x - 2.0D) / Tile.WIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 2) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 4) / Tile.HEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height * 0.75D) / Tile.HEIGHT))
				|| (checkCollision(getxMove() - 1.0F, 0.0F))) {
			lefts = true;
		}
	}

	public void render(Graphics2D g) {
		AffineTransform p = g.getTransform();
		g.translate(x, y);

		if (currentTool != null && getFrame() != stab.getFrame())
				g.drawImage(Tran.flip(currentTool.world(), -widthFlip, 1),
						getToolxoffset(), getToolyoffset(), null);

		g.drawImage(Tran.flip(getFrame(), -widthFlip, 1), 0, 0, null);

		if (getFrame() == stab.getFrame())
			g.drawImage(Tran.flip(currentTool.world(), -widthFlip, 1), getToolxoffset(), getToolyoffset(), null);
		g.setTransform(p);
	}

	public int getToolxoffset() {
		if ((getFrame() == airKick) || (getFrame() == jump.getFrame())) {
			if (-widthFlip == 1) {
				return 33 - currentTool.world().getWidth() / 2;
			}
			return 3 - currentTool.world().getWidth() / 2;
		}
		if (getFrame() == crouch.getFrame()) {
			if (-widthFlip == 1) {
				return 28 - currentTool.world().getWidth() / 2;
			}
			return 8 - currentTool.world().getWidth() / 2;
		}
		if (getFrame() == walk.getFrame()) {
			if (walk.getFrame() == walk.getArray()[0]) {
				if (-widthFlip == 1) {
					return 33 - currentTool.world().getWidth() / 2;
				}
				return 3 - currentTool.world().getWidth() / 2;
			}
			if (walk.getFrame() == walk.getArray()[1]) {
				if (-widthFlip == 1) {
					return 26 - currentTool.world().getWidth() / 2;
				}
				return 10;
			}
			if (-widthFlip == 1) {
				return 25 - currentTool.world().getWidth() / 2;
			}
			return 14 - currentTool.world().getWidth() / 2;
		}
		if (getFrame() == hold.getFrame()) {
			if (-widthFlip == 1) {
				return 33 - currentTool.world().getWidth() / 2;
			}
			return 3 - currentTool.world().getWidth() / 2;
		}
		if (getFrame() == stab.getFrame()) {
			if (stab.getFrame() == stab.getArray()[0]) {
				if (-widthFlip == 1) {
					return 16;
				}
				return 20 - currentTool.world().getWidth();
			}
			if (stab.getFrame() == stab.getArray()[1]) {
				if (-widthFlip == 1) {
					return 13;
				}
				return 23 - currentTool.world().getWidth();
			}
			if (-widthFlip == 1) {
				return 33;
			}
			return 3 - currentTool.world().getWidth();
		}
		return 0;
	}

	public int getToolyoffset() {
		if ((getFrame() == airKick) || (getFrame() == jump.getFrame())) {
			return 40 - currentTool.world().getHeight();
		}
		if (getFrame() == crouch.getFrame()) {
			return 50 - currentTool.world().getHeight();
		}
		if (getFrame() == walk.getFrame()) {
			if (walk.getFrame() == walk.getArray()[0]) {
				return 40 - currentTool.world().getHeight();
			}
			if (walk.getFrame() == walk.getArray()[1]) {
				return 42 - currentTool.world().getHeight();
			}

			return 25 - currentTool.world().getHeight();
		}
		if (getFrame() == hold.getFrame()) {
			if (hold.getFrame() == hold.getArray()[0]) {
				return 40 - currentTool.world().getHeight();
			}
			return 41 - currentTool.world().getHeight();
		}
		if (getFrame() == stab.getFrame()) {
			if (stab.getFrame() == stab.getArray()[0]) {
				return (int) (40 - currentTool.yoffset);
			}
			if (stab.getFrame() == stab.getArray()[1]) {
				return (int) (42 - currentTool.yoffset);
			}
			return (int) (33 - currentTool.yoffset);
		}
		return 33 - currentTool.world().getHeight() / 2;
	}

	private BufferedImage getFrame() {
//		checkCol();

		if ((!bottoms) || (jumping)) {
			if ((attacking) && (punch.getFrame() == punch.getArray()[2]) && (attackReady)) {
				return airKick;
			}
			return jump.getFrame();
		}
		if (down)
			return crouch.getFrame();
		if (Math.round(Math.abs(getxMove()) - speed + 0.49D) > 0L)
			return walk.getFrame();
		if ((attacking) || (primed)) {
			if (currentTool != null)
				return stab.getFrame();
			return punch.getFrame();
		}
		if (currentTool != null)
			return hold.getFrame();
		return still.getFrame();
	}

	public void tick() {

		if (alpha) {
			name = "Alpha Cannibal";
		}
		if (!game.getGame().paused) {
			if (currentTool != null) {
//				currentTool.tick((int) (x + getToolxoffset()),
//						(int) (y + getToolyoffset()));
				light.setX((int) (x+getToolxoffset()));
				light.setY((int) (y+getToolyoffset()));
			}
			still.tick();
			walk.tick();
			crouch.tick();
			punch.tick();
			hold.tick();
			stab.tick();

			if (bottoms) {
				if (y - lastHeight > 180.0D) {
					damage((int) Math.floor((y - lastHeight) / 180.0D));
				}

				lastHeight = y;
			} else if (y < lastHeight) {
				lastHeight = y;
			}

			attack.x = ((int) x - 18);
			attack.y = ((int) y);

			if (timer >= 1000000L) {
				timer = 0L;
			}
			if (attackTimer == attackDelay) {
				attackReady = true;
			} else if ((attackTimer < attackDelay) || (attackTimer > attackDelay + delayRange)) {
				attackReady = false;
				if (attackTimer > attackDelay + delayRange) {
					attackTimer = 0L;
				}
			}
			if ((punch.getFrame() == punch.getArray()[2]) && (getFrame() == punch.getFrame()) && (bottoms)) {
				attacking = false;
			}

			if ((stab.getFrame() == stab.getArray()[0]) && (!game.getMouse().fullLeft) && (attacking)
					&& (getFrame() == stab.getFrame())) {
				attacking = false;
			}
			if ((bottoms) && (jumping)) {
				jumping = false;
			}

			Cannibal closest = this;
			if (!alpha) {
				closest = getAlphaCannibal(x, y, 2);

				if (closest == null) {
					alpha = true;
					closest = this;

					game.log("Im alpha now! MWAHAHAH!!");
					game.getPlayer().getItem("Torch").light.turnOn();
					currentTool = game.getPlayer().getItem("Torch");
					game.getEnvironment().getLightManager().addLight(light);
					delayRange = 15;
					attackDelay = 15;
					damage = 1;
				}
			}

			if ((Public.dist(x, y, game.getWorld().getEntityManager().getPlayer().getX(),
					game.getWorld().getEntityManager().getPlayer().getY()) < 180.0D)
					|| ((chasing) && (Public.dist(x, y, game.getWorld().getEntityManager().getPlayer().getX(),
							game.getWorld().getEntityManager().getPlayer().getY()) < 400.0D))) {
				if ((Public.dist(x, y, game.getWorld().getEntityManager().getPlayer().getX(),
						game.getWorld().getEntityManager().getPlayer().getY()) < 20.0D) && (attackReady)) {
					attacking = true;
					attackTimer = 0L;
					attackReady = false;

					chasing = true;

					speed = (walkSpeed + 1.0D);

					game.getWorld().getEntityManager().getPlayer().damage(damage);
				}
				if (x - 10.0D > game.getWorld().getEntityManager().getPlayer().getX()) {
					l = true;
					r = false;
					if (left)
						jumping = true;
				} else if (x + 10.0D < game.getWorld().getEntityManager().getPlayer().getX()) {
					l = false;
					r = true;
					if (right)
						jumping = true;
				} else {
					l = false;
					r = false;
				}
			} else if ((closest != this) && (Public.dist(x, y, x, y) > 20.0D)) {
				chasing = false;
				if (x < x) {
					l = true;
					r = false;
					if (left)
						jumping = true;
				} else if (x > x) {
					l = false;
					r = true;
					if (right)
						jumping = true;
				} else {
					l = false;
					r = false;
				}
			} else if (timer >= 100L) {
				chasing = false;
				if (d) {
					timer += 1L;
				}
				if (timer >= 10L) {
					d = false;
					timer = 0L;
				}

				if (Math.random() < 0.5D) {
					if (rChange) {
						l = true;
						r = false;
					} else {
						r = true;
						l = false;
					}
					if (right) {
						jumping = true;
						rChange = true;
					} else {
						jumping = false;
						rChange = false;
					}
				} else if (Math.random() > 0.5D) {
					if (lChange) {
						r = true;
						l = false;
					} else {
						l = true;
						r = false;
					}
					if (left) {
						jumping = true;
						lChange = true;
					} else {
						jumping = false;
						lChange = false;
					}
				} else {
					jumping = false;
				}

				if (Math.random() < 0.05D) {
					d = true;
				}

				timer = 0L;
			} else {
				timer += 1L;
				chasing = false;
			}

			if (!chasing) {
				speed = walkSpeed;
			}
			
			aiMove();

			move();
			
			game.getWorld().outOfBounds(this);

			attackTimer += 1L;
			
			if ((currentTool != null) && (currentTool.hasLight) && (!light.isOn())) {
				
				Console.log(game.getEnvironment().getLightManager().getList().contains(light), light.isOn(), light.getX(), light.getY());
				
				light.turnOn();
				
				if(!game.getEnvironment().getLightManager().getList().contains(light)) {
					game.getEnvironment().getLightManager().addLight(light);
					Console.log("Added light to list");
				}
				
			}
		}
	}

	private void aiMove() {
		setxMove(0.0F);
		yMove = 0.0F;

		if (xVol > 0.0D) {
			setxMove((float) (getxMove() + (speed + xVol)));
		}
		if (xVol < 0.0D) {
			setxMove((float) (getxMove() + (-speed + xVol)));
		}
		if (l) {
			if (!d) {
				if ((xVol < maxSpeed) && (xVol > 0.0D))
					xVol -= FRICTION * 2.0D;
				xVol -= acceleration;
			} else {
				xVol = 0.0D;
			}
			widthFlip = 1;
		} else if (r) {
			widthFlip = -1;
			if (!d) {
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

		if ((bottoms) && (jumping)) {
			yVol = ((float) -DEFAULT_JUMP_FORCE);
			jumping = false;
		} else if (yVol < 0.0F) {
			yVol = ((float) (yVol - DEFAULT_JUMP_CARRY));
		}
	}

	public void kill() {
		if ((currentTool != null) && (currentTool.hasLight)) {
			light.turnOff();
			light.setX(65336);
			light.setY(65336);
		}

		currentTool = null;
		game.getWorld().getEntityManager().getEntities().remove(this);
	}

	public String toString() {
		return "Cannibal " + x + " " + y + " " + layer + " " + walkSpeed + " " + lives + " " + alpha;
	}

}
