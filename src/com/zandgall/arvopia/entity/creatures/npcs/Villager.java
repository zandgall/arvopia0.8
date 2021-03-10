package com.zandgall.arvopia.entity.creatures.npcs;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Reporter;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.guis.Trading;
import com.zandgall.arvopia.guis.trading.TradingKeys;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class Villager extends NPC {
	private static final long serialVersionUID = 1L;
	
	int type;
	BufferedImage texture;
	Assets set;
	Animation jump;
	Animation still;
	Animation walk;
	Animation crouch;
	int widthFlip = 1;

	long aitimer;

	boolean l = false;
	boolean r = false;
	boolean u = false;
	boolean d = false;
	double bx;
	double by;
	TradingKeys sec1, sec2, sec3, sec4, sec5, sec6; 
	public Villager(Handler handler, double x, double y, int type) {
		super(handler, x, y, 1.0D, "Villager " + type, new String[] { "Goodbye!" });
		this.type = type;

		bx = x;
		by = y;

		useSpeech(false);

		texture = ImageLoader.loadImage("/textures/NPCs/Villagers/Varient" + type + ".png");

		set = new Assets(texture, 36, 54, "Player");

		layer = com.zandgall.arvopia.utils.Public.debugRandom(-0.01D, 0.01D);

		MAX_HEALTH = 20;
		health = 20.0D;

		bounds.x = 12;
		bounds.y = 8;
		bounds.width = 10;
		bounds.height = 45;

		jump = new Animation(1000, new BufferedImage[] { set.get(0, 1) }, "Jump", "Villager");
		still = new Animation(750, new BufferedImage[] { set.get(0, 0), set.get(1, 0) }, "Still", "Villager");
		walk = new Animation(250, new BufferedImage[] { set.get(1, 1), set.get(3, 1) }, "Walk", "Villager");
		crouch = new Animation(750, new BufferedImage[] { set.get(2, 0), set.get(3, 0) }, "Crouch", "Villager");
		
		t = new Trading(handler, "", name, still.getArray()[0].getSubimage(0, 0, 36, 36));
		
		sec1 = new TradingKeys();
		sec1.put("Hello!", "Hi how are you?", "Good thanks, and you?");
		sec1.put("Good thanks, and you?", "I'm doing good", "Good to hear! Goodbye");
		sec1.put("Good to hear! Goodbye", "Bye!", "~end~");
		
		 sec2 = new TradingKeys();
		sec2.put("Hey! How's it going?", "It's going great, what about you?", "I'm doing pretty fantastic");
		sec2.put("I'm doing pretty fantastic", "Good to hear, well I'll be off", "Ok see you!");
		sec2.put("Ok see you!", "Bye!", "~end~");
		
		 sec3 = new TradingKeys();
		sec3.put("Good day " + Reporter.user+"!", "Good day to you as well", "~end~");
		
		 sec4 = new TradingKeys();
		sec4.put("Good evening " + Reporter.user, "Good evening to you as well", "~end~");
		
		 sec5 = new TradingKeys();
		sec5.put("Good afternoon " + Reporter.user, "Good afternoon to you as well", "~end~");
		
		 sec6 = new TradingKeys();
		sec6.put("Check back later!", "Okie dokie!", "~end~");
		
		if(Public.chance(25)) {
			t.setSpeeches(sec1);
		} else {
			if(Public.chance(25)) {
				t.setSpeeches(sec2);
			} else if(Public.chance(25)) {
				t.setSpeeches(sec6);
			} else if(Public.chance(25)) {
				t.setSpeeches(sec3);
			}
		}
		
	}

	public void tick() {
		still.tick();
		walk.tick();

		if (l)
			widthFlip = -1;
		if (r) {
			widthFlip = 1;
		}
		aiMove();

		if (l) {
			setxMove(-1.0F);
		} else if (r) {
			setxMove(1.0F);
		} else {
			setxMove(0.0F);
		}
		move();

		if (dead) {
			Achievement.award(Achievement.disrespectful);
			setHealth(20);
			dead = false;
			x = bx;
			y = by;
		}
	}

	public void aiMove() {
		if ((right) && (r)) {
			int tx = (int) ((x + getxMove() + bounds.x + bounds.width) / Tile.TILEWIDTH);

			if (!collisionWithTile(tx, (int) (y + bounds.y - 18.0D) / Tile.TILEHEIGHT)) {
				u = true;
			} else {
				r = false;
				l = true;
				u = false;
			}
		} else if ((left) && (l)) {
			int tx = (int) ((x + getxMove() + bounds.x) / Tile.TILEWIDTH);

			if (!collisionWithTile(tx, (int) (y + bounds.y - 18.0D) / Tile.TILEHEIGHT)) {
				u = true;
			} else {
				r = true;
				l = false;
				u = false;
			}
		} else {
			u = false;
		}

		if (u) {
			if ((bottoms) && (yVol > -DEFAULT_JUMP_FORCE + GRAVITY * 2.0D)) {
				if ((!d) && (yVol > -DEFAULT_JUMP_FORCE / 2.0D)) {
					yVol = ((float) (yVol - DEFAULT_JUMP_FORCE));
				}
			} else if (yVol < 0.0F) {
				yVol = ((float) (yVol - DEFAULT_JUMP_CARRY));
			}
		}

		if (aitimer >= 100L) {
			if (Math.random() < 0.15D) {
				l = true;
				r = false;
			} else if (Math.random() < 0.3D) {
				r = true;
				l = false;
			} else {
				r = false;
				l = false;
			}
			aitimer = 0L;
		}

		int ty = (int) ((y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT) + 6;
		if ((!collisionWithDown((int) ((x + bounds.x - 20.0D) / Tile.TILEWIDTH), ty))
				&& (!collisionWithDown((int) ((x + bounds.x + bounds.width - 20.0D) / Tile.TILEWIDTH), ty))
				&& (y + bounds.y + bounds.height < ty * Tile.TILEHEIGHT + 4)) {
			r = true;
			l = false;
		} else if ((!collisionWithDown((int) ((x + bounds.x + 24.0D) / Tile.TILEWIDTH), ty))
				&& (!collisionWithDown((int) ((x + bounds.x + bounds.width - 24.0D) / Tile.TILEWIDTH), ty))
				&& (y + bounds.y + bounds.height < ty * Tile.TILEHEIGHT + 4)) {
			r = false;
			l = true;
		}

		aitimer += 1L;
	}

	public void render(Graphics2D g) {
		g.drawImage(com.zandgall.arvopia.gfx.transform.Tran.flip(getFrame(), widthFlip, 1), (int) (x - game.xOffset()),
				(int) (y - game.yOffset()), null);
	}

	public BufferedImage getFrame() {
		if (d) {
			return crouch.getFrame();
		}
		if ((l) || (r))
			return walk.getFrame();
		if (bottoms) {
			return still.getFrame();
		}
		return jump.getFrame();
	}

	public boolean mapable() {
		return true;
	}

	public Color mapColor() {
		return Color.yellow;
	}

	public Point mapSize() {
		return new Point(3, 9);
	}

	public String outString() {
		return "Villager " + x + " " + y + " " + layer + " " + type;
	}

}
