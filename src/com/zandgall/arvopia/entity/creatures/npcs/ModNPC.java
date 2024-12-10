package com.zandgall.arvopia.entity.creatures.npcs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Log;
import com.zandgall.arvopia.Reporter;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.guis.Trading;
import com.zandgall.arvopia.guis.trading.TradingKeys;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Mapper;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Utils;
import com.zandgall.arvopia.utils.ValueAnimation;

public class ModNPC extends NPC {
	private static final long serialVersionUID = 1L;

	Mapper<String, String> quests;

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

	// The name of this NPC
	public static final String name = "Informer";

	public Point[] stillframes, jumpframes, crouchframes, walkframes;

	public ValueAnimation stillvalues, jumpvalues, crouchvalues, walkvalues;

	TradingKeys firstcontact, asking;
	
	// Default stuff, maybe just leave this if you don't know what these are
	int widthFlip = 1;

	long aitimer;

	boolean l = false;
	boolean r = false;
	boolean u = false;
	boolean d = false;
	double bx;
	double by = -1.0D;

	public ModNPC() {
		super(new Handler(), 0, 0, Creature.DEFAULT_SPEED, name);
		
		// Animation and frames
		stillframes = new Point[] { new Point(0, 0), new Point(1, 0) };
		jumpframes = new Point[] { new Point(2, 1) };
		crouchframes = new Point[] { new Point(0, 1), new Point(1, 1) };
		walkframes = new Point[] { new Point(2, 0), new Point(3, 0) };

		stillvalues = new ValueAnimation(0.6, stillframes.length);
		jumpvalues = new ValueAnimation(10, jumpframes.length);
		crouchvalues = new ValueAnimation(0.6, crouchframes.length);
		walkvalues = new ValueAnimation(0.4, walkframes.length);

		// Default stuff
		bx = x;
		by = y;
		MAX_HEALTH = 20;
		health = 20.0D;

		bounds.x = 10;
		bounds.y = 20;
		bounds.width = 16;
		bounds.height = 34;

		// Set the width and height of your assets
		width = 36;
		height = 54;

//		initTradingGUI(); // Check this out for your interactions and just overall trading gui

	}
	
	//Anything that's random goes in here
	public void setup() {
		layer = Public.rand(-0.01D, 0.01D);
	}

	public void initTradingGUI() {
		// Trading gui (You might want to change the the .getSubimage() part to fit your
		// character)
		t = new Trading(game, "", name, ImageLoader.loadImage(name).getSubimage(0, 0, 36, 36));
		firstcontact = new TradingKeys();

		firstcontact.put("Hello! Welcome to Arvopia!", "Who are you?", "Well I'm just the tutorial guy, but more importantly, who are you?");
		
		firstcontact.put("Hello! Welcome to Arvopia!", "I've already been through the tutorial", "Oh... well come to me if you have any questions");
		
		firstcontact.put("Oh... well come to me if you have any questions", "Will do!", "~end~");

		firstcontact.put("Well I'm just the tutorial guy, but more importantly, who are you?", "Oh well I'm " + Reporter.user, "It's nice to meet you " + Reporter.user+", I'm here to teach you the basics and answer any questions you have");
		
		firstcontact.put("It's nice to meet you " + Reporter.user + ", I'm here to teach you the basics and answer any questions you have", "Well what's first?", "Firstly, you have W A S and D or Arrow Keys to move around, Spacebar can also be used to jump if you don't want to use W or UP");
		
		firstcontact.put("Firstly, you have W A S and D or Arrow Keys to move around, Spacebar can also be used to jump if you don't want to use W or UP", "Alright, I think I got that", "Ok good, next up is the world around you, you will see creatures, plants, and other \"Static Entities\" around");
		
		firstcontact.put("Ok good, next up is the world around you, you will see creatures, plants, and other \"Static Entities\" around", "What are those for?", "Well, you see, you should watch out for some creatures as they will attack you and attack eachother.\nApon killing them, some will give you items to use");
		
		firstcontact.put("Well, you see, you should watch out for some creatures as they will attack you and attack eachother.\nApon killing them, some will give you items to use", "What about those \"Static Entities\"?", "Well, static entities are all the plants, rocks, and things that usually don't move");
		
		firstcontact.put("Well, static entities are all the plants, rocks, and things that usually don't move", "Go on..", "These static entities mostly drop items that can be used in forging and crafting as well as some quests");
		
		firstcontact.put("These static entities mostly drop items that can be used in forging and crafting as well as some quests", "Quests?", "Oh yeah! Quests! Around the world you'll find NPCs that you can chat with, (Check for a flashing \"C\" over their head)\nChatting with these NPCs can lead to starting quests and getting achievements");

		firstcontact.put("Oh yeah! Quests! Around the world you'll find NPCs that you can chat with, (Check for a flashing \"C\" over their head)\nChatting with these NPCs can lead to starting quests and getting achievements", "Oh well that's cool, when can I get started?", "You can get started now if you like!");
		
		firstcontact.put("You can get started now if you like!", "Ok, I'll be off to", "Ok! Come back if you have any questions!");
		
		firstcontact.put("You can get started now if you like!", "Why does this game writer only give me one-choice responses and keeps me from actually saying anything I want?", "Ok! Come back if you have any questions!");
		
		firstcontact.put("Ok! Come back if you have any questions!", "Ok bye!", "~end~");
		
		asking = new TradingKeys();
		
		asking.put("Hi! What can I help you with?", "Goodbye", "~end~");
		
		asking.put("Hi! What can I help you with?", "Can you explain static entities again?", "Well, static entities are all the plants, rocks, and things that usually don't move");
		
		asking.put("Well, static entities are all the plants, rocks, and things that usually don't move", "Go on..", "These static entities mostly drop items that can be used in forging and crafting as well as some quests");
		
		asking.put("These static entities mostly drop items that can be used in forging and crafting as well as some quests", "Ok thanks", "Hi! What can I help you with?");
		
		asking.put("Hi! What can I help you with?", "Can you explain quests and NPCs again?", "Around the world you'll find NPCs that you can chat with, (Check for a flashing \"C\" over their head)\nChatting with these NPCs can lead to starting quests and getting achievements"); 
		
		asking.put("Oh yeah! Quests! Around the world you'll find NPCs that you can chat with, (Check for a flashing \"C\" over their head)\nChatting with these NPCs can lead to starting quests and getting achievements", "Ok thanks!", "Hi! What can I help you with?");
		
		asking.put("Hi! What can I help you with?", "Can you explain creatures again?", "Well, you see, you should watch out for some creatures as they will attack you and attack eachother.\nApon killing them, some will give you items to use");
		
		asking.put("Well, you see, you should watch out for some creatures as they will attack you and attack eachother.\nApon killing them, some will give you items to use", "Ok thanks!", "Hi! What can I help you with?");
		
		asking.put("Hi! What can I help you with?", "Can I get more info on this game?", "Absolutely! This game \"Arvopia\" was created in late 2017 by indie dev \"Zandgall\". You can contact him at AlexanderDGall@gmail.com, or visit his website at www.zandgall.com");
		
		asking.put("Absolutely! This game \"Arvopia\" was created in late 2017 by indie dev \"Zandgall\". You can contact him at AlexanderDGall@gmail.com, or visit his website at www.zandgall.com", "I have a bug to report", "You can report bugs at the Bug Report menu found on the title screen");
		
		asking.put("You can report bugs at the Bug Report menu found on the title screen", "Ok thanks!", "Hi! What can I help you with?");
		
		asking.put("Absolutely! This game \"Arvopia\" was created in late 2017 by indie dev \"Zandgall\". You can contact him at AlexanderDGall@gmail.com, or visit his website at www.zandgall.com", "I have a suggestion", "Suggestions can be emailed straight to the dev, (AlexanderDGall@gmail.com), although sending them through the Bug report menu on the title screen works as well");
		
		asking.put("Suggestions can be emailed straight to the dev, (AlexanderDGall@gmail.com), although sending them through the Bug report menu on the title screen works as well", "Ok thanks!", "Hi! What can I help you with?");
		
		asking.put("Absolutely! This game \"Arvopia\" was created in late 2017 by indie dev \"Zandgall\". You can contact him at AlexanderDGall@gmail.com, or visit his website at www.zandgall.com", "Can I participate in developing?", "Most likely not, however suggestions are accepted and you can make modifications");
		
		asking.put("Most likely not, however suggestions are accepted and you can make modifications", "Ok thanks!", "Hi! What can I help you with?");
		
		asking.put("Absolutely! This game \"Arvopia\" was created in late 2017 by indie dev \"Zandgall\". You can contact him at AlexanderDGall@gmail.com, or visit his website at www.zandgall.com", "Can I make mods?", "Yes! Check the website (www.zandgall.com) for information about modifying");
		
		asking.put("Yes! Check the website (www.zandgall.com) for information about modifying", "Ok thanks!", "Hi! What can I help you with?");
		
		asking.put("Absolutely! This game \"Arvopia\" was created in late 2017 by indie dev \"Zandgall\". You can contact him at AlexanderDGall@gmail.com, or visit his website at www.zandgall.com", "My question isn't listed", "Please email zandgall for any other questions you have (AlexanderDGall@gmail.com)");
		
		asking.put("Please email zandgall for any other questions you have (AlexanderDGall@gmail.com)", "Ok thanks!", "Hi! What can I help you with?");
		
//		asking.put("speech", "response", "next");
		
		useSpeech(true);
		
		t.useownSpeech(true);
		t.setSpeeches(firstcontact);
//		t.setQuests(quests);
		t.setText("Goodbye!"); // Sets default text for when you're done
	}

	public void tick() {

		// Ticks value animations for your display animations
		stillvalues.tick();
		walkvalues.tick();
		jumpvalues.tick();
		crouchvalues.tick();

		// If dead, will award [disrespectful] achievement and then respawn at
		// spawnpoint
		if (dead) {
			Achievement.award(Achievement.disrespectful);
			setHealth(20);
			dead = false;
			y = by;
			x = bx;
		}
		
		if(t.speeches==firstcontact && t.speeches.getSpeech(t.speechindex)=="~end~") {
			t.useownSpeech(true);
			t.setSpeeches(asking);
			t.setText("Goodbye!"); // Sets default text for when you're done
		}

		// It's probable best to leave this alone, it checks if it's talking with the
		// player then moves if it's not, or just stands still and looks at the player
		// if it's not
		if (!usingSpeech() || game.getEntityManager().getPlayer().closestNPC != this) {
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
			} else
				setxMove(0.0F);
			move();
		} else {
			setxMove(0.0F);
			if (game.getEntityManager().getPlayer().getX() > x) {
				widthFlip = 1;
			} else {
				widthFlip = -1;
			}
			move();
		}
	}

	// WARNING: THIS IS VERY CLUTTERED AND COMPLICATED, GO IN WITH CARE AND BE
	// CAREFUL WHAT YOU EDIT
	public void aiMove() {
		if ((right) && (r)) {
			int tx = (int) ((x + getxMove() + bounds.x + bounds.width + 2.0D) / Tile.WIDTH);

			if (!collisionWithTile(tx, (int) (y + bounds.y - 36.0D) / Tile.HEIGHT)) {
				u = true;
			} else {
				r = false;
				l = true;
				u = false;
			}
		} else if ((left) && (l)) {
			int tx = (int) ((x + getxMove() + bounds.x) / Tile.WIDTH + 2.0D);

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

		int ty = (int) ((y + yMove + bounds.y + bounds.height) / Tile.HEIGHT);
		if (!checkOffs(-20, 0, -20, 72, true, false) && !checkOffs(-20, 0, -20, 72, false, false)
				&& (y + bounds.y + bounds.height < ty * Tile.HEIGHT + 4)) {
			r = true;
			l = false;
		} else if (!checkOffs(24, 0, 24, 72, true, false) && !checkOffs(-24, 0, -24, 72, false, false)
				&& (y + bounds.y + bounds.height < ty * Tile.HEIGHT + 4)) {
			r = false;
			l = true;
		}

		aitimer += 1L;
	}

	// These next two boolean functions check tiles in relevance to this npc, if the
	// tile(s) it checks are/is solid, then it will return true
	public boolean checkOffs(int tX1, int tY1, int tX2, int tY2, boolean lr, boolean tb) {

		for (int i = tX1; i <= tX2; i++) {
			for (int j = tY1; j <= tY2; j++) {
				if (checkOff(i, j, lr, tb))
					return true;
			}
		}

		return false;
	}

	public boolean checkOff(int tX, int tY, boolean lr, boolean tb) {
		int ty = (int) ((y + yMove + bounds.y + bounds.height + tY) / Tile.HEIGHT);
		if (tb)
			ty = (int) ((y + yMove + bounds.y + tY) / Tile.HEIGHT);
		int tx = (int) ((x + bounds.x + bounds.width + tX) / Tile.WIDTH);
		if (lr)
			tx = (int) ((x + bounds.x + tX) / Tile.WIDTH);
		return collisionWithDown(tx, ty);
	}

	@Override
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
		Utils.createDirectory(Game.prefix);
		Utils.createDirectory(Game.prefix + "/Mod export");
		Utils.createDirectory(Game.prefix + "/Mod export/NPCs");
		Utils.createDirectory(Game.prefix + "/Mod export/NPCs/"+ModNPC.name);
		
		Log log = new Log(Game.prefix + "/Mod export/NPCs/"+ModNPC.name+"/Main", "Log");
		
		Game game = new Game("Exporting...", 720, 400, false, log);
		log.log("Writing...");
		
		Public.init(new Handler(game));
		
		Utils.fileWriter(ModNPC.name, Game.prefix + "/Mod export/NPCs/"+ModNPC.name+"/name.txt");
		FileLoader.writeObjects(Game.prefix + "/Mod export/NPCs/"+ModNPC.name+"/"+ModNPC.name+".arv", new Object[] {(NPC) new ModNPC()});
		
		log.log("Successfully wrote " + ModNPC.name);
	}
	
	//Run this when you want to export the mod
	//Exports to "C:/Arvopia/Mod Export/"
	//(Instructions on mod packing here)
	public static void main(String[] args) throws IOException {
		ModNPC.export();
	}

}
