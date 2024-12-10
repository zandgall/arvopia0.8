package com.zandgall.arvopia.entity.creatures.npcs;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Reporter;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.entity.statics.House;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.Pos;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.guis.Trading;
import com.zandgall.arvopia.guis.trading.TradingKeys;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.quests.Quest;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Fawncier extends NPC {
	private static final long serialVersionUID = 1L;

	private static final Pos newHouses[] = {
		new Pos(2230, 234),
		new Pos(2568, 270),
		new Pos(2720, 252),
		new Pos(2885, 270),
		new Pos(3045, 288),
	};
	
	private int hIndex = 0;
	
	BufferedImage texture;
	Assets set;
	Animation jump;
	Animation still;
	Animation walk;
	Animation crouch;
	int widthFlip;
	long aitimer;
	boolean start = true;

	boolean l = false;
	boolean r = false;
	boolean u = false;
	boolean d = false;
	
	TradingKeys two = new TradingKeys();

	public Fawncier(Handler handler, double x, double y) {
		super(handler, x-18, y-54, Creature.DEFAULT_SPEED, "Fawncier",
				new String[] { "Welcome to Arvopia " + Reporter.user + "! (CLICK)",
						"Please walk around and enjoy yourself!", "Hey, while you're at it,",
						"could you get me some wood?", "10 logs will do!", "Thanks so much!",
						"You see, that's how quests work", "Have fun exploring!" });

		texture = ImageLoader.loadImage("/textures/NPCs/Villagers/Variant3.png");

		set = new Assets(texture, 36, 54, "Player");

		layer = 0.01D;

		MAX_HEALTH = 20;
		health = 20.0D;

		bounds.x = 12;
		bounds.y = 8;
		bounds.width = 10;
		bounds.height = 45;

		jump = new Animation(1000, new BufferedImage[] { set.get(0, 1) }, "Jump", "Frizzy");
		still = new Animation(750, new BufferedImage[] { set.get(0, 0), set.get(1, 0) }, "Still", "Frizzy");
		walk = new Animation(250, new BufferedImage[] { set.get(1, 1), set.get(3, 1) }, "Walk", "Frizzy");
		crouch = new Animation(750, new BufferedImage[] { set.get(2, 0), set.get(3, 0) }, "Crouch", "Frizzy");

		t = new Trading(handler,
				"Some... \nBody once told me the world was going to roll me, I ain't the sharpest tool in the shed",
				"Fawncier", ImageLoader.loadImage("/textures/NPCs/Villagers/Variant3.png").getSubimage(0, 0, 36, 36));

		t.voice = Trading.DEFAULT_MALE;

		TradingKeys one = new TradingKeys();
		// one: Designed in Trader Design - Zandgall
		one.put("Hello there lad, how's your day been going?", "It's been great! Who are you?",
				"Oh, well my name's Fawncier, I'm known as the big business man at this village");
		one.put("Oh, well my name's Fawncier, I'm known as the big business man at this village",
				"Could you tell me about the people here?",
				"Well lad, when traveling through this village there are a few people you'll need to know. Lia, myself of course, and Frizzy.");
		one.put("Well lad, when traveling through this village there are a few people you'll need to know. Lia, myself of course, and Frizzy.",
				"Who's Lia?",
				"Lia is a bright young lad, one of the most hard working and intellegent people I know. Head to her if you have any questions");
		one.put("Lia is a bright young lad, one of the most hard working and intellegent people I know. Head to her if you have any questions",
				"Who are you?",
				"I'm a business man, top here in this village. Come to me if you ever need anything done, or if you want to help get something done.");
		one.put("I'm a business man, top here in this village. Come to me if you ever need anything done, or if you want to help get something done.",
				"Thank you", "Anytime! Come to me if you have work, or if you want to work.");
		one.put("Anytime! Come to me if you have work, or if you want to work.", "...", "~end~");
		one.put("Lia is a bright young lad, one of the most hard working and intellegent people I know. Head to her if you have any questions",
				"Who's Frizzy?",
				"Frizzy is.. well a self declared artist. She hasn't even made an art piece before! However she's pretty good when you need support, no matter how annoyingly optimistic one might find her");
		one.put("Frizzy is.. well a self declared artist. She hasn't even made an art piece before! However she's pretty good when you need support, no matter how annoyingly optimistic one might find her",
				"Thank you", "Anytime! Come to me if you have work, or if you want to work.");
		one.put("Well lad, when traveling through this village there are a few people you'll need to know. Lia, myself of course, and Frizzy.",
				"Who's Frizzy?",
				"Frizzy is.. well a self declared artist. She hasn't even made an art piece before! However she's pretty good when you need support, no matter how annoyingly optimistic one might find her");
		one.put("Frizzy is.. well a self declared artist. She hasn't even made an art piece before! However she's pretty good when you need support, no matter how annoyingly optimistic one might find her",
				"Who are you?",
				"I'm a business man, top here in this village. Come to me if you ever need anything done, or if you want to help get something done.");
		one.put("Frizzy is.. well a self declared artist. She hasn't even made an art piece before! However she's pretty good when you need support, no matter how annoyingly optimistic one might find her",
				"Who's Lia?",
				"Lia is a bright young lad, one of the most hard working and intellegent people I know. Head to her if you have any questions");
		one.put("Lia is a bright young lad, one of the most hard working and intellegent people I know. Head to her if you have any questions",
				"Thank you", "Anytime! Come to me if you have work, or if you want to work.");
		one.put("Well lad, when traveling through this village there are a few people you'll need to know. Lia, myself of course, and Frizzy.",
				"Who are you?",
				"I'm a business man, top here in this village. Come to me if you ever need anything done, or if you want to help get something done.");
		one.put("I'm a business man, top here in this village. Come to me if you ever need anything done, or if you want to help get something done.",
				"Who's Frizzy?",
				"Frizzy is.. well a self declared artist. She hasn't even made an art piece before! However she's pretty good when you need support, no matter how annoyingly optimistic one might find her");
		one.put("I'm a business man, top here in this village. Come to me if you ever need anything done, or if you want to help get something done.",
				"Who's Lia?",
				"Lia is a bright young lad, one of the most hard working and intellegent people I know. Head to her if you have any questions");
		one.put("Hello there lad, how's your day been going?", "Can you tell me a bit about this village?",
				"When looking for information, I'd advise visiting Lia. She's a bright young lad who could tell you just about anything");
		one.put("When looking for information, I'd advise visiting Lia. She's a bright young lad who could tell you just about anything",
				"Could you tell me about the people here?",
				"Well lad, when traveling through this village there are a few people you'll need to know. Lia, myself of course, and Frizzy.");
		one.put("When looking for information, I'd advise visiting Lia. She's a bright young lad who could tell you just about anything",
				"Alright I'll head to Lia", "~end~");
		
		if(Quest.getQuest("Make a new house")==null)
			Quest.addQuest(new Quest("Make a new house", "Gather 40 logs and 10 stones to create a house for Fawncier", 100) {
				public boolean completable(Handler game) {
					return game.getPlayer().items.get("Wood").amount>=40 && game.getPlayer().items.get("Stone").amount>=10;
				}
				
				public void complete(Handler game) {
					game.getPlayer().items.get("Wood").amount-=40;
					game.getPlayer().items.get("Stone").amount-=10;
					
					if(hIndex<newHouses.length) {
						game.getEntityManager().addEntity(new House(game, newHouses[hIndex].x, newHouses[hIndex].y, 0), false);
					}
						
					hIndex++;
					
					if(hIndex>=newHouses.length) {
						t.speeches.removeResponse("Splendid, lucky I have a few jobs for you", "(Build a house)");
					}
				}
			});
		if(Quest.getQuest("Upgrade a house")==null)
			Quest.addQuest(new Quest("Upgrade a house", "Gather 60 stones to upgrade a house for Fawncier", 100) {
				public boolean completable(Handler game) {
					return game.getPlayer().items.get("Stone").amount>=60;
				}
				
				public void complete(Handler game) {
					game.getPlayer().items.get("Stone").amount-=60;
					
					for(Entity e : game.getEntityManager().getEntities()) {
						if(e instanceof House) {
							
						}
					}
				}
			});

		//two: Designed in Trader Design - Zandgall
		two.put("Hello again lad! What can I do for you?", "I'd like to work for you", "Splendid, lucky I have a few jobs for you");
		two.put("Splendid, lucky I have a few jobs for you", "(Build a house)", "You can build a house with 40 logs and 10 stones");
		two.put("You can build a house with 40 logs and 10 stones", "Nevermind", "Splendid, lucky I have a few jobs for you");
		two.put("You can build a house with 40 logs and 10 stones", "Got it", "Thank you dear boy, a new home lives within this village because of you");
		two.put("Thank you dear boy, a new home lives within this village because of you", "Thanks", "~end~");
		two.put("Splendid, lucky I have a few jobs for you", "(Upgrade a house)", "To upgrade a house, you need 60 stones");
		two.put("To upgrade a house, you need 60 stones", "Nevermind", "Splendid, lucky I have a few jobs for you");
		two.put("To upgrade a house, you need 60 stones", "Here you go", "Wonderful. This house could withstand anything now");
		two.put("Wonderful. This house could withstand anything now", "Thanks", "~end~");
		//Diggy diggy
//		two.put("Splendid, lucky I have a few jobs for you", "(Dig part of a new village)", "For digging another part of the village, me and my team need 10 shovels. We'll take care of everything from there");
//		two.put("For digging another part of the village, me and my team need 10 shovels. We'll take care of everything from there", "Nevermind", "Splendid, lucky I have a few jobs for you");
//		two.put("For digging another part of the village, me and my team need 10 shovels. We'll take care of everything from there", "Take these", "Excellent, excellent, we've been working towards getting this project done for a long time now. Can't wait to finally get it done!");
//		two.put("Excellent, excellent, we've been working towards getting this project done for a long time now. Can't wait to finally get it done!", "Thanks", "~end~");
		two.put("Splendid, lucky I have a few jobs for you", "I'll get back to you", "~end~");
		two.put("Hello again lad! What can I do for you?", "Just saying hello", "~end~");

		Map<String, String> questsmapper = new HashMap<String, String>();
		questsmapper.put("You can build a house with 40 logs and 10 stones~`~Got it", "Make a new house");
		questsmapper.put("To upgrade a house, you need 60 stones~`~Here you go", "Upgrade a house");

		t.useownSpeech(true);
		t.setSpeeches(one);
		t.setQuests(questsmapper);
		t.setText("Go out and explore!");

		setUseSpeech(true);
	}

	public boolean shouldTick() {
		return true;
	}

	public void tick() {
		still.tick();
		walk.tick();
		crouch.tick();
		
		if(!t.ownSpeeches && !t.resetOnClose) {
			if(!t.speeches.getSpeech(t.speechindex).equals("When looking for information, I'd advise visiting Lia. She's a bright young lad who could tell you just about anything")) {
				t.ownSpeeches = true;
				t.speechindex = 0;
				t.minorindex = 0;
				t.setSpeeches(two);
				t.setResetOnClose(true);
			} else {
				t.ownSpeeches = true;
				t.speechindex = 0;
				t.minorindex = 0;
				t.setSpeeches(t.speeches);
			}
			
		}

		if (game.getPlayer().closestNPC != this) {
			if (l)
				widthFlip = -1;
			if (r) {
				widthFlip = 1;
			}
			aiMove();

			if (l) {
				if (xVol > 0)
					xVol -= 0.15;
				xVol -= 0.1;
			} else if (r) {
				if (xVol < 0)
					xVol += 0.15;
				xVol += 0.1;
			} else {
				xVol /= 2;
			}

			if (game.getPlayer().getxMove() == MAX_SPEED || game.getPlayer().getxMove() == -MAX_SPEED) {
				xVol = game.getPlayer().getxMove();
			}

			xVol = Public.range(-MAX_SPEED, MAX_SPEED, xVol);
			xMove = (float) xVol;
		} else {
			l = false;
			r = false;
			setxMove(0.0F);

			if (game.getEntityManager().getPlayer().getX() > x) {
				widthFlip = 1;
			} else {
				widthFlip = -1;
			}
		}

		if (dead) {
			Achievement.award(Achievement.disrespectful);
			setHealth(20);
			dead = false;
			y = ((game.getWorld().getLowest(x) - 4) * Tile.HEIGHT);
		}

	}

	public void aiMove() {

		setxMove(0.0F);
		yMove = 0.0F;

		boolean[] bools = follow(game.getEntityManager().getPlayer(), this);

		if ((right) && (r)) {
			int tx = (int) ((x + getxMove() + bounds.x + bounds.width) / Tile.WIDTH);

			if (!collisionWithTile(tx, (int) (y + bounds.y - 36.0D) / Tile.HEIGHT)) {
				u = true;
			} else {
				r = false;
				l = true;
				u = false;
			}
		} else if ((left) && (l)) {
			int tx = (int) ((x + getxMove() + bounds.x) / Tile.WIDTH);

			if (!collisionWithTile(tx, (int) (y + bounds.y - 36.0D) / Tile.HEIGHT)) {
				u = true;
			} else {
				r = true;
				l = false;
				u = false;
			}
		} else {
			u = false;
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

//		r = bools[0] && !l;
//		l = bools[1] && !r;
//
//		u = bools[2];

		if ((bottoms) && (jumping)) {
			jumping = false;
		}

		if (!game.getWorld().safeToWalk(x - 20, y + height / 2)) {
			if (game.getWorld().safeToWalk(x - 110, y + height / 2, 90)) {
				u = true;
			} else {
				l = false;
			}
		} else if (!game.getWorld().safeToWalk(x + 20, y + height / 2)) {
			if (game.getWorld().safeToWalk(x + 20, y + height / 2, 90)) {
				u = true;
			} else {
				r = false;
			}
		}

		if (u) {
			if ((bottoms) && (!jumping)) {
				yVol = ((float) -DEFAULT_JUMP_FORCE);
				jumping = true;
			} else if (touchingWater) {
				yVol -= 1.0F;
				yVol = ((float) Math.max(yVol, -speed));
			}

			if (yVol < 0.0F) {
				yVol = ((float) (yVol - DEFAULT_JUMP_CARRY));
			}
		}

//		int ty = (int) ((y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT) + 6;
//		if ((!collisionWithDown((int) ((x + bounds.x - 20.0D) / Tile.TILEWIDTH), ty))
//				&& (!collisionWithDown((int) ((x + bounds.x + bounds.width - 20.0D) / Tile.TILEWIDTH), ty))
//				&& (y + bounds.y + bounds.height < ty * Tile.TILEHEIGHT + 4)) {
//			r = true;
//			l = false;
//		} else if ((!collisionWithDown((int) ((x + bounds.x + 24.0D) / Tile.TILEWIDTH), ty))
//				&& (!collisionWithDown((int) ((x + bounds.x + bounds.width - 24.0D) / Tile.TILEWIDTH), ty))
//				&& (y + bounds.y + bounds.height < ty * Tile.TILEHEIGHT + 4)) {
//			r = false;
//			l = true;
//		}

		aitimer += 1L;
	}

	public BufferedImage getFrame() {
		if (d) {
			return crouch.getFrame();
		}
		if (!bottoms)
			return jump.getFrame();
		if ((l) || (r))
			return walk.getFrame();
		if (bottoms) {
			return still.getFrame();
		}
		return jump.getFrame();
	}

	public void render(Graphics2D g) {
		AffineTransform p = g.getTransform();
		g.translate(x, y);

		g.drawImage(Tran.flip(getFrame(), widthFlip, 1), 0, 0, null);

		if (t.speeches.getSpeech(t.speechindex)!="~end~" && game.getEntityManager().getPlayer().closestNPC == this) {

			if(Math.sin(game.getGameTime()/200.0)>0) {
				g.setFont(Public.runescape.deriveFont(20f));

				Tran.drawOutlinedText(g, width/2.0-10, 0, " C ", 1, Color.white, Color.black);
			}

			g.setFont(Public.defaultFont);
		}
		g.setTransform(p);
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
}
