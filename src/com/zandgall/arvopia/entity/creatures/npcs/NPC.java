package com.zandgall.arvopia.entity.creatures.npcs;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.EntityAdder;
import com.zandgall.arvopia.entity.EntityManager;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.guis.Trading;
import com.zandgall.arvopia.guis.trading.TradingKeys;
import com.zandgall.arvopia.input.KeyManager;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.ClassLoading;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.water.Water;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class NPC extends Creature implements Cloneable {
	private static final long serialVersionUID = 1L;
	public boolean jumping;
	private boolean useSpeech;
	private boolean done;
	public Speech use;
	private int speechuse;
	protected Speech[] speech;

	Speech blankSpeech;

	public Trading t;

	public NPC(Handler handler, double x, double y, double speed, String name, String[] speeches) {
		super(handler, x, y, 36, 54, true, speed, DEFAULT_ACCELERATION, (int) MAX_SPEED, false, false,
				DEFAULT_JUMP_FORCE, DEFAULT_JUMP_CARRY, name);

		t = new Trading(handler, "", name, ImageLoader.loadImage(""));
		
		useSpeech = false;

		speech = new Speech[speeches.length];
		for (int i = 0; i < speeches.length; i++) {
			speech[i] = new Speech(x + width / 2, y - 15.0D, speeches[i]);
		}

		TradingKeys trader = new TradingKeys();
		for (int i = 0; i < speeches.length - 1; i++) {
			trader.put(speeches[i], "...", speeches[i + 1]);
		}
		trader.put(speeches[speeches.length - 1], "Ok bye!", "~end~");
		t.useownSpeech(true);
		t.setSpeeches(trader);
		t.useSpeech(speeches[0]);
		t.setText("Goodbye!");

		blankSpeech = new Speech(x + width / 2, y - 15.0D, " ... ");

		if (speech.length > 0)
			use = speech[0];
		speechuse = 0;
		NPC = true;
	}

	public NPC(Handler handler, double x, double y, double speed, String name) {
		super(handler, x, y, 36, 54, true, speed, DEFAULT_ACCELERATION, (int) MAX_SPEED, false, false,
				DEFAULT_JUMP_FORCE, DEFAULT_JUMP_CARRY, name);

		t = new Trading(handler, "", name, ImageLoader.loadImage(""));

		speech = new Speech[0];
		
		useSpeech = false;
		
		blankSpeech = new Speech(x + width / 2, y - 15.0D, " ... ");

		if (speech.length > 0)
			use = speech[0];
		speechuse = 0;
		NPC = true;
	}

	public abstract void tick();

	public void gui(Graphics2D g) {
		if (t.shown)
			t.render(g);
	}

	public void uniTick() {

		if (KeyManager.checkBind("Interact") && KeyManager.preTyped)
			t.shown = !t.shown && game.getEntityManager().getPlayer().closestNPC == this;
		if (t.shown)
			t.tick();

		double prex = x;
		double prey = y;

		move();

		if ((prey != y) || (prex != x))
			for (int i = 0; i < speech.length; i++) {
				boolean b = use == speech[i];
				speech[i] = new Speech(x, y - 25.0D, speech[i].speech);
				if (b)
					setSpeech(i);
			}
	}
	
	public void uniTick_BACKUP() {
//		if ((useSpeech) && (use.done) && (game.getMouse().isLeft()) && (game.getMouse().isClicked())
//				&& (game.getEntityManager().getPlayer().closestNPC == this)) {
//			done = true;
//		}

		if (KeyManager.checkBind("Interact") && KeyManager.preTyped)
			t.shown = !t.shown && game.getEntityManager().getPlayer().closestNPC == this;
		if (t.shown)
			t.tick();

		if (useSpeech) {
//			use.x = x + width / 2;
//			use.y = y - 15;
//			blankSpeech.x = x + width / 2;
//			blankSpeech.y = y - 15;
//			use.tick();
//			if (Public.dist(x, y, game.getEntityManager().getPlayer().getX(),
//					game.getEntityManager().getPlayer().getY()) < 200)
//				blankSpeech.i = 0;
//			else {
//				blankSpeech.tick();
//				use.i = 0;
//			}
		}

		double prex = x;
		double prey = y;

		move();

		if ((prey != y) || (prex != x))
			for (int i = 0; i < speech.length; i++) {
				boolean b = use == speech[i];
				speech[i] = new Speech(x, y - 25.0D, speech[i].speech);
				if (b)
					setSpeech(i);
			}
	}

	protected void useSpeech(boolean tf) {
		useSpeech = tf;
	}

	protected void setSpeech(int i) {
		if (i >= speech.length)
			use = new Speech(x, y - 25.0D, "END");
		else
			use = speech[i];
		speechuse = i;
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
		touchingWater = false;

		for (Water w : game.getWorld().getWaterManager().getWater()) {
			if (w.box().contains(getCollision(0.0F, 0.0F))) {
				inWater = true;
			}
			if (w.box().intersects(getCollision(0.0F, 0.0F))) {
				touchingWater = true;
			}
		}
		int ty = (int) ((y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT);
		if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
				|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 3.0D) / Tile.TILEWIDTH), ty))
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
						|| (collisionWithDown((int) ((x + bounds.x + bounds.width + 2.0D) / Tile.TILEWIDTH), ty)))
						&& (y + bounds.y + bounds.height <= ty * Tile.TILEHEIGHT + 1) && (!jumping))) {
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
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 2) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 4) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height * 0.75D) / Tile.TILEHEIGHT))
				|| (checkCollision(getxMove() + 1.0F, 0.0F))) {
			right = true;
		}
		tx = (int) ((x + getxMove() + bounds.x + bounds.width + 2.0D) / Tile.TILEWIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 2) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 4) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height * 0.75D) / Tile.TILEHEIGHT))
				|| (checkCollision(getxMove() + 1.0F, 0.0F))) {
			rights = true;
		}

		tx = (int) ((x + getxMove() + bounds.x) / Tile.TILEWIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 2) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 4) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height * 0.75D) / Tile.TILEHEIGHT))
				|| (checkCollision(getxMove(), 0.0F))) {
			left = true;
		}
		tx = (int) ((x + getxMove() + bounds.x - 2.0D) / Tile.TILEWIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 2) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 4) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height * 0.75D) / Tile.TILEHEIGHT))
				|| (checkCollision(getxMove() - 1.0F, 0.0F))) {
			lefts = true;
		}
	}

	protected void resetSpeech(String[] speeches) {
		speech = new Speech[speeches.length];
		for (int i = 0; i < speeches.length; i++) {
			speech[i] = new Speech(x, y - 25.0D, speeches[i]);
		}

		if (speech.length > 0) {
			use = speech[0];
		}
	}

	public boolean isSpeaking() {
		return useSpeech;
	}

	public void kill() {
	}

	public boolean usingSpeech() {
		return t.ownSpeeches;
	}

	public void setUseSpeech(boolean useSpeech) {
		this.useSpeech = useSpeech;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public int getSpeechuse() {
		return speechuse;
	}

	public void setSpeechuse(int speechuse) {
		this.speechuse = speechuse;
	}

//	public static boolean[] follow(Creature following, NPC follower) {
//
//		boolean r = false, l = false, u = false, d = false;
//
//		if (following.getX() > follower.getX() + 10)
//			r = true;
//		if (following.getX() < follower.getX() - 10)
//			l = true;
//
//		following.checkCol();
//
//		if (following.getY() < follower.getY() - 36 || (following.rights && r) || (following.lefts && l))
//			u = true;
//
//		return new boolean[] { r, l, u, d };
//	}

	public void initTradingGUI() {

	}

	public class Speech implements Serializable {
		private static final long serialVersionUID = 1L;
		public String speech;
		public String use = "";

		public double i = 0.0D;

		double x;
		double y;
		public boolean done = false;

		public double speed = 0.3;

		public Speech(double x, double y, String speech) {
			this.speech = speech;
			this.x = x;
			this.y = y;
		}

		public void tick() {
			i += speed;
			use = speech.substring(0, Math.min((int) i, speech.length()));

			if ((Math.min((int) i, speech.length()) == speech.length()) && (speech != "END")) {
				done = true;
			}
		}

		public void render(Graphics g, Handler game) {

			double nx = (x - game.xOffset());
			double ny = (y - game.yOffset());

			int sLength = Tran.measureString(use, Public.defaultFont).x;

			g.setColor(Color.white);
			g.fillRoundRect((int) (nx - sLength / 2 - 10.0D), (int) (ny - 10.0D), sLength + 20, 20, 20, 20);
			g.setColor(Color.black);
			g.drawRoundRect((int) (nx - sLength / 2 - 10.0D), (int) (ny - 10.0D), sLength + 20, 20, 20, 20);
			g.drawString(use, (int) (nx - sLength / 2), (int) (ny) + 5);
		}
	}
	
	public static void loadJar(String jarfile, Handler handler, EntityManager e) {
		try {
			ArrayList<Object> objects = ClassLoading.loadObjects(jarfile);
			ArrayList<NPC> npcs = new ArrayList<NPC>();
			for (Object o : objects)
				if ((o instanceof NPC)) {
					NPC n = (NPC) o;
					n.game=handler;
					n.initTradingGUI();
					n.initSounds();
					npcs.add((NPC) o);
				}

			for (NPC out : npcs)
				e.adders.add(new EntityAdder(e, out));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void loadMODNPC(String directory, Handler handler, EntityManager e) {
		try {
			long pre = System.currentTimeMillis();
			String name = FileLoader.readFile(directory + "/name.txt").replaceAll(" ", "");
			NPC out = (NPC) FileLoader.readObjects(directory + "/" + name + ".arv", 1)[0];
			ImageLoader.addRedirect(name, ImageLoader.loadImageEX(directory + "/assets.png"));
			out.name = name;
			out.game = handler;
			out.initTradingGUI();
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

	public static NPC loadMODNPC(String directory, Handler handler, int x, int y) {
		try {
			long pre = System.currentTimeMillis();
			String name = FileLoader.readFile(directory + "/name.txt").replaceAll(" ", "");
			NPC out = (NPC) FileLoader.readObjects(directory + "/" + name + ".arv", 1)[0];
			ImageLoader.addRedirect(name, ImageLoader.loadImageEX(directory + "/assets.png"));
			out.name = name;
			out.game = handler;
			out.x = x;
			out.y = y;
			out.initTradingGUI();
			out.initSounds();

			pre = System.currentTimeMillis() - pre;
			handler.log("~~~~~~LOADED MOD NPC ~~~ " + name + " ~~~ ");
			handler.log("TIME TAKEN " + pre);

			return out;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
