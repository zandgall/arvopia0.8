package com.zandgall.arvopia.entity.creatures.npcs;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Reporter;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.guis.Trading;
import com.zandgall.arvopia.guis.trading.TradingKeys;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Frizzy extends NPC {
	private static final long serialVersionUID = 1L;

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

	public Frizzy(Handler handler, double x, double y) {
		super(handler, x-18, y-54, Creature.DEFAULT_SPEED, "Frizzy",
				new String[] { "Welcome to Arvopia " + Reporter.user + "! (CLICK)",
						"Please walk around and enjoy yourself!", "Hey, while you're at it,",
						"could you get me some wood?", "10 logs will do!", "Thanks so much!",
						"You see, that's how quests work", "Have fun exploring!" });

		texture = ImageLoader.loadImage("/textures/NPCs/Villagers/Variant5.png");

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
				"Frizzy", ImageLoader.loadImage("/textures/NPCs/Villagers/Variant5.png").getSubimage(0, 0, 36, 36));

		Trading.addVoice(handler, "Sounds/Voices/Frizzy.ogg", "FrizzyVoice");
		t.voice="FrizzyVoice";

		TradingKeys one = new TradingKeys();
		// one: Designed in Trader Design - Zandgall
		one.put("Hello!! I'm Frizzy!  What's your name?", "My name's " + Reporter.user,
				"Well " + Reporter.user + ", it's nice to meet you!");
		one.put("Well " + Reporter.user + ", it's nice to meet you!", "Who's everyone here?",
				"Well there's a lot of people around here, but the main people you should at least know is Lia, Fawncier, and of course me!");
		one.put("Well there's a lot of people around here, but the main people you should at least know is Lia, Fawncier, and of course me!",
				"Who's Lia?",
				"Oh boy, she's the smartest gal I know! The first person to go to for information about Arvopia this village. She can answer any question you throw at her!");
		one.put("Oh boy, she's the smartest gal I know! The first person to go to for information about Arvopia this village. She can answer any question you throw at her!",
				"...",
				"Well there's a lot of people around here, but the main people you should at least know is Lia, Fawncier, and of course me!");
		one.put("Well there's a lot of people around here, but the main people you should at least know is Lia, Fawncier, and of course me!",
				"Who's Fawncier?",
				"He's the guy in the suit, a little stuck up and prude, but the best in business! Definitely head to Fawncier if you need work.");
		one.put("He's the guy in the suit, a little stuck up and prude, but the best in business! Definitely head to Fawncier if you need work.",
				"...",
				"Well there's a lot of people around here, but the main people you should at least know is Lia, Fawncier, and of course me!");
		one.put("Well there's a lot of people around here, but the main people you should at least know is Lia, Fawncier, and of course me!",
				"Who are you?",
				"Well as you know, my name's Frizzy. I'm the village artist.. well I mean I haven't really made art for anyone but I'm still an artist!");
		one.put("Well as you know, my name's Frizzy. I'm the village artist.. well I mean I haven't really made art for anyone but I'm still an artist!",
				"...",
				"Well there's a lot of people around here, but the main people you should at least know is Lia, Fawncier, and of course me!");
		one.put("Well as you know, my name's Frizzy. I'm the village artist.. well I mean I haven't really made art for anyone but I'm still an artist!",
				"Can you make an art piece for me?",
				"Wait, really?? Yeah of course! But.. can I get a favor from you real quick beforehand?");
		one.put("Wait, really?? Yeah of course! But.. can I get a favor from you real quick beforehand?",
				"Yeah of course! What is it?", "I just need some wood and flower petals to get started..");
		one.put("I just need some wood and flower petals to get started..", "Sure! I'll get those for you!",
				"Thank you so much! I'll need 3 wood and 40 flower petals");
		one.put("Thank you so much! I'll need 3 wood and 40 flower petals", "Here's your supplies!",
				"Thank you so much!! Check back in a day and I'll have your painting ready!");
		one.put("Thank you so much!! Check back in a day and I'll have your painting ready!", "Will do", "~end~");
		one.put("I just need some wood and flower petals to get started..",
				"On second thought, I don't think I need an art piece right now.",
				"Oh... Alright, just let me know if you change your mind!");
		one.put("Oh... Alright, just let me know if you change your mind!", "Will do", "~end~");
		one.put("Wait, really?? Yeah of course! But.. can I get a favor from you real quick beforehand?",
				"On second thought, I don't think I need an art piece right now.",
				"Oh... Alright, just let me know if you change your mind!");
		one.put("Hello!! I'm Frizzy!  What's your name?", "Who are you?",
				"Oh sorry, I'm the village artist! I haven't really gotten t make any art pieces for anyone, but that doesn't stop me from being an artist!");
		one.put("Oh sorry, I'm the village artist! I haven't really gotten t make any art pieces for anyone, but that doesn't stop me from being an artist!",
				"Can you make an art piece for me?",
				"Wait, really?? Yeah of course! But.. can I get a favor from you real quick beforehand?");
		one.put("Thank you so much! I'll need 3 wood and 40 flower petals", "Here's your supplies!",
				"Thank you so much!! Check back in a day and I'll have your painting ready!");
		one.put("Thank you so much!! Check back in a day and I'll have your painting ready!", "Will do", "~end~");
		one.put("Hello!! I'm Frizzy!  What's your name?", "Can you tell me a bit about this village?",
				"Well if it's information you need, I'd head to Lia! She's the smartest gal I know, she'd answer any question you could throw at her!");
		one.put("Well if it's information you need, I'd head to Lia! She's the smartest gal I know, she'd answer any question you could throw at her!",
				"Ok thanks!", "~end~");

		TradingKeys sectiontwo = new TradingKeys();
		sectiontwo.put("Hey I got a question for you,", "Yeah what's up?",
				"I've been looking for some wood lately, but I have to attend to the village");
		sectiontwo.put("I've been looking for some wood lately, but I have to attend to the village", "...",
				"Can you get me like 10 logs?");
		sectiontwo.put("Can you get me like 10 logs?", "Sure! Leave it to me!", "Thanks! 10 logs will do!");
		sectiontwo.put("Can you get me like 10 logs?", "No", "..Really? Are you sure?");
		sectiontwo.put("..Really? Are you sure?", "Ok I'll do it", "Thanks! 10 logs will do!");
		sectiontwo.put("..Really? Are you sure?", "I'm sure",
				"But it could increase your experience!!\nIt-It'll get you money..");
		sectiontwo.put("But it could increase your experience!!\nIt-It'll get you money..",
				"Money you say? Fine I'll do it", "Thanks! 10 logs will do!");
		sectiontwo.put("But it could increase your experience!!\nIt-It'll get you money..", "Doesn't interest me",
				"Oh, well come back if you change your mind");
		sectiontwo.put("Oh, well come back if you change your mind", "Ok bye", "~end~");
		sectiontwo.put("Thanks! 10 logs will do!", "Here you are! 10 logs as you requested", "Thank you so much!");
		sectiontwo.put("Thank you so much!", "So, any reward?", "Yeah, I'm getting to that");
		sectiontwo.put("Thank you so much!", "Great what's next?", "You see, that's how quests work");
		sectiontwo.put("Yeah, I'm getting to that", "...", "You see, that's how quests work");
		sectiontwo.put("You see, that's how quests work", "Quests?",
				"Yes, quests gain you experience and in game gold");
		sectiontwo.put("Yes, quests gain you experience and in game gold", "Gold you say?", "Yess... I said gold");
		sectiontwo.put("Yes, quests gain you experience and in game gold", "...",
				"Gold and experience can give you rewards");
		sectiontwo.put("Yess... I said gold", "...", "Gold and experience can give you rewards");
		sectiontwo.put("Gold and experience can give you rewards", "Like what?",
				"Well, gold gets you items and outfits, it also allows you to trade with other villager");
		sectiontwo.put("Well, gold gets you items and outfits, it also allows you to trade with other villager",
				"And experience?", "Experience can increase other villagers/NPCs opinions of you");
		sectiontwo.put("Experience can increase other villagers/NPCs opinions of you", "...",
				"For instance, if you go long distances, villagers will think of you as peserveering and will lower prices on gear for travel");
		sectiontwo.put(
				"For instance, if you go long distances, villagers will think of you as peserveering and will lower prices on gear for travel",
				"I think I get it",
				"Good, well I've got more stuff at the village to attend to, so I'll see you around");
		sectiontwo.put(
				"For instance, if you go long distances, villagers will think of you as peserveering and will lower prices on gear for travel",
				"Another example?",
				"Yeah, if you do a lot of fighting, you'll be targetted by weapon smiths for special discouts");
		sectiontwo.put("Yeah, if you do a lot of fighting, you'll be targetted by weapon smiths for special discouts",
				"Ok, well I get it",
				"Good, well I've got more stuff at the village to attend to, so I'll see you around");
		sectiontwo.put("Good, well I've got more stuff at the village to attend to, so I'll see you around",
				"Ok, bye then!", "~end~");

		Map<String, String> questsmapper = new HashMap<String, String>();
		questsmapper.put("Thanks! 10 logs will do!", "Get wood for Lia");

		t.useownSpeech(true);
		t.setSpeeches(one);
		t.setQuests(questsmapper);
		t.setText("Go out and explore!");
		t.setResetOnClose(true);

		setUseSpeech(true);
	}

	public boolean shouldTick() {
		return true;
	}

	public void tick() {
		still.tick();
		walk.tick();
		crouch.tick();

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
