package com.zandgall.arvopia.entity.creatures.npcs;

import com.zandgall.arvopia.Game;
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
import com.zandgall.arvopia.quests.Quest;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Public;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Lia extends NPC {
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

	public Lia(Handler handler, double x, double y) {
		super(handler, x, y, Creature.DEFAULT_SPEED, "Lia", new String[] { "Welcome to Arvopia " + Reporter.user + "! (CLICK)",
				"Please walk around and enjoy yourself!", "Hey, while you're at it,", "could you get me some wood?",
				"10 logs will do!", "Thanks so much!", "You see, that's how quests work", "Have fun exploring!" });

		if (FileLoader.readFile(Game.prefix + "\\Arvopia\\03.arv").contains(Quest.getWoodForLia.name)) {
			if (FileLoader.readFile(Game.prefix + "\\Arvopia\\04.arv").contains(Quest.getWoodForLia.name)) {
				resetSpeech(new String[] { "Welcome back to Arvopia " + Reporter.user + "!" });
			} else
				resetSpeech(new String[] { "Welcome back to Arvopia " + Reporter.user + "!",
						"Have you gotten around to getting me that wood?", "10 logs will do!", "Thanks so much!",
						"You see, that's how quests work", "Have fun exploring!" });
		}

		texture = ImageLoader.loadImage("/textures/NPCs/" + name + "/" + name + ".png");

		set = new Assets(texture, 36, 54, "Player");

		layer = 0.01D;

		MAX_HEALTH = 20;
		health = 20.0D;

		bounds.x = 12;
		bounds.y = 8;
		bounds.width = 10;
		bounds.height = 45;

		jump = new Animation(1000, new BufferedImage[] { set.get(0, 1) }, "Jump", "Lia");
		still = new Animation(750, new BufferedImage[] { set.get(0, 0), set.get(1, 0) }, "Still", "Lia");
		walk = new Animation(250, new BufferedImage[] { set.get(1, 1), set.get(3, 1) }, "Walk", "Lia");
		crouch = new Animation(750, new BufferedImage[] { set.get(2, 0), set.get(3, 0) }, "Crouch", "Lia");

		t = new Trading(handler,
				"Some... \nBody once told me the world was going to roll me, I ain't the sharpest tool in the shed",
				"Lia", ImageLoader.loadImage("/textures/NPCs/Lia/Lia.png").getSubimage(0, 0, 36, 36));

		t.voice=Trading.DEFAULT_FEMALE;
		
		TradingKeys sectionone = new TradingKeys();
		sectionone.put("Welcome to Arvopia!", "Hi! Who are you?",
				"Oh sorry, my name is Lia, You are " + Reporter.user + " correct?");
		sectionone.put("Oh sorry, my name is Lia, You are " + Reporter.user + " correct?", "Yeah that\'s me",
				"Take a look around, check out some other people and also try gathering materials!");
		sectionone.put("Oh sorry, my name is Lia, You are " + Reporter.user + " correct?", "No it\'s " + Reporter.user,
				"I see you're a comedian, well check out some other people and also try gathering materials!");
		sectionone.put("Oh sorry, my name is Lia, You are " + Reporter.user + " correct?", "Where am I?",
				"This is Arvopia, a mishmash of genres like combat, rpg, survival, adventure. This village is unnamed, but you can still explore it");
		sectionone.put("Oh sorry, my name is Lia, You are " + Reporter.user + " correct?", "Third option",
				"This is Arvopia, a mishmash of genres like combat, rpg, survival, adventure. This village is unnamed, but you can still explore it");
		sectionone.put("Take a look around, check out some other people and also try gathering materials!",
				"Ok, well bye then", "~end~");
		sectionone.put("I see you're a comedian, well check out some other people and also try gathering materials!",
				"Ok, well bye then", "~end~");
		sectionone.put(
				"This is Arvopia, a mishmash of genres like combat, rpg, survival, adventure. This village is unnamed, but you can still explore it",
				"Ok what can I do?",
				"You can go around and explore, gather materials and chat with some other villagers");
		sectionone.put("You can go around and explore, gather materials and chat with some other villagers",
				"Ok, well I\'ll go check that out", "~end~");

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
		t.setSpeeches(sectionone);
		t.useSpeech("Hey I got a question for you,");
		t.setQuests(questsmapper);
		t.setText("Go out and explore!");
		
		setUseSpeech(true);
	}
	
	public boolean alwaysTick() {
		return true;
	}

	public void tick() {
		if (start) {
			y = ((game.getWorld().getLowest(x) - 4) * Tile.TILEHEIGHT);
			start = false;
		}
		still.tick();
		walk.tick(); 
		crouch.tick();
		
//		System.out.println(t.fin);
		
		if (!usingSpeech()) {
			if (l)
				widthFlip = -1;
			if (r) {
				widthFlip = 1;
			}
			aiMove();

			if (l) {
				if(xVol>0)
					xVol-=0.15;
				xVol-=0.1;
			} else if (r) {
				if(xVol<0)
					xVol+=0.15;
				xVol+=0.1;
			} else {
				xVol/=2;
			}
			
			if(game.getPlayer().getxMove()==MAX_SPEED||game.getPlayer().getxMove()==-MAX_SPEED) {
				xVol = game.getPlayer().getxMove();
			}
			
			xVol = Public.range(-MAX_SPEED, MAX_SPEED, xVol);
			xMove  = (float) xVol;
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
			y = ((game.getWorld().getLowest(x) - 4) * Tile.TILEHEIGHT);
		}

		if (isDone()) {
			setDone(false);
			setSpeech(getSpeechuse() + 1);
		}

//		if ((game.getEntityManager().getPlayer().getCurrentTool() != null)
//				&& (game.getEntityManager().getPlayer().getCurrentTool()
//						.type==PlayerItem.UMBRELLA)
//				&& (game.getEnviornment().precipitation) && (game.getEnviornment().getTemp() >= 32.0D)
//				&& (game.getEntityManager().getPlayer().getX() >= x - 54.0D)
//				&& (game.getEntityManager().getPlayer().getX() <= x + width + 54.0D)) {
//			Achievement.award(Achievement.shieldethfairmaiden);
//		}
	}
	
	public void aiMove() {

		setxMove(0.0F);
		yMove = 0.0F;
		
		boolean[] bools = follow(game.getEntityManager().getPlayer(), this);

		if ((right) && (r)) {
			int tx = (int) ((x + getxMove() + bounds.x + bounds.width) / Tile.TILEWIDTH);

			if (!collisionWithTile(tx, (int) (y + bounds.y - 36.0D) / Tile.TILEHEIGHT)) {
				u = true;
			} else {
				r = false;
				l = true;
				u = false;
			}
		} else if ((left) && (l)) {
			int tx = (int) ((x + getxMove() + bounds.x) / Tile.TILEWIDTH);

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

		r = bools[0] && !l;
		l = bools[1] && !r;
		
		u = bools[2];
		
		if ((bottoms) && (jumping)) {
			jumping = false;
		}
		
		if(!game.getWorld().SAFETOWALK(x-20, y+height/2)) {
			if(game.getWorld().SAFETOWALK(x-110, y+height/2, 90)) {
				u = true;
			} else {
				l = false;
			}
		} else if(!game.getWorld().SAFETOWALK(x+20, y+height/2)) {
			if(game.getWorld().SAFETOWALK(x+20, y+height/2, 90)) {
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
		if(!bottoms)
			return jump.getFrame();
		if ((l) || (r))
			return walk.getFrame();
		if (bottoms) {
			return still.getFrame();
		}
		return jump.getFrame();
	}

	public void render(Graphics2D g) {
		g.drawImage(Tran.flip(getFrame(), (widthFlip == 0 ? 1:widthFlip), 1), (int) (x - game.xOffset()),
				(int) (y - game.yOffset()), null);
		
		if ((t.speeches.getSpeech(t.speechindex) != "~end~")
				&& (game.getEntityManager().getPlayer().closestNPC == this)) {

			if (Math.sin(game.getGameTime() / 200) > 0) {
				g.setFont(Public.runescape.deriveFont(20f));

				Tran.drawOutlinedText(g, Public.xO(x + width / 2 - 10), Public.yO(y), " C ", 1, Color.white,
						Color.black);
			}

			g.setFont(Public.defaultFont);
		}
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

	public String toString() {
		return "Lia " + x + " " + y + " " + use.use;
	}
}
