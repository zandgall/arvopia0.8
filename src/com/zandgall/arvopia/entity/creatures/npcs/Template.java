package com.zandgall.arvopia.entity.creatures.npcs;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.guis.Trading;
import com.zandgall.arvopia.guis.trading.TradingKeys;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.quests.Quest;
import com.zandgall.arvopia.quests.QuestManager;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

public class Template extends NPC {
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
	double by = -1.0D;

	Map<String, String> quests;

	public Template(Handler handler, double x, double y, int type, String name, String[] speeches,
			Map<String, String> quests) {
		super(handler, x, y, 1.0D, name, speeches);
		this.type = type;

		bx = x;
		by = y;

		this.quests = quests;

		texture = com.zandgall.arvopia.gfx.ImageLoader.loadImage("/textures/NPCs/Villagers/Variant" + type + ".png");

		set = new Assets(texture, 36, 54, name);

		layer = com.zandgall.arvopia.utils.Public.rand(-0.01D, 0.01D);

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
		
		t = new Trading(handler, "", name, texture.getSubimage(0, 0, 36, 36));
		TradingKeys trader = new TradingKeys();
		for(int i = 0; i<speeches.length-1; i++) {
			trader.put(speeches[i], "...", speeches[i+1]);
		}
		trader.put(speeches[speeches.length-1], "Ok bye!", "~end~");
		t.useownSpeech(true);
		t.setSpeeches(trader);
		t.setQuests(quests);
		t.useSpeech(speeches[0]);
		t.setText("Goodbye!");
		useSpeech(true);
		
		if(name.equals("Frizzy")) {
			Trading.addVoice(handler, "Sounds/Voices/Frizzy.ogg", "FrizzyVoice");
			t.voice="FrizzyVoice";
		}
	}
	
	public Template(Handler handler, double x, double y, int type, String name, String[] speeches, String[] responses, String[] next,
			Map<String, String> quests) {
		super(handler, x, y, 1.0D, name, speeches);
		this.type = type;

		bx = x;
		by = y;
		
		this.quests = quests;

		texture = com.zandgall.arvopia.gfx.ImageLoader.loadImage("/textures/NPCs/Villagers/Variant" + type + ".png");

		set = new Assets(texture, 36, 54, name);

		layer = com.zandgall.arvopia.utils.Public.rand(-0.01D, 0.01D);

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
		
		t = new Trading(handler, "", name, texture.getSubimage(0, 0, 36, 36));
		TradingKeys trader = new TradingKeys();
		for(int i = 0; i<speeches.length-1; i++) {
			trader.put(speeches[i], responses[i], next[i]);
		}
		trader.put(speeches[speeches.length-1], "Ok bye!", "~end~");
		t.useownSpeech(true);
		t.setSpeeches(trader);
		t.useSpeech(speeches[0]);
		t.setQuests(quests);
		t.setText("Goodbye!");
		useSpeech(true);
	}

	public void tick() {
		still.tick();
		walk.tick();
		crouch.tick();

//		t.setText(use.speech);
		
		if (dead) {
			Achievement.award(Achievement.disrespectful);
			setHealth(20);
			dead = false;
			y = by;
			x = bx;
		}

		if (isDone()) {
			setDone(false);
			if ((quests.containsKey(use.speech)) && (Quest.questcompletable(Quest.getQuest(quests.get(use.speech)))
					|| QuestManager.alfin.contains(Quest.getQuest(quests.get(use.speech))))) {
				Quest.finish(Quest.getQuest(quests.get(use.speech)));
				setSpeech(getSpeechuse() + 1);
			} else if (!quests.containsKey(use.speech)) {
				setSpeech(getSpeechuse() + 1);
			}
			if ((quests.containsKey(use.speech))) {
				if ((Quest.getQuest(quests.get(use.speech)) != null))
					Quest.begin(Quest.getQuest(quests.get(use.speech)));
				System.out.println(
						Quest.getQuest(quests.get(use.speech)) + " " + use.speech + " " + quests.get(use.speech));
			}
		}

		if (use.speech == "END") {
			useSpeech(false);
		}
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

	public String toString() {
		String content = "Template " + x + " " + y + " " + type + " " + name.replaceAll(" ", "~") + " " + layer + " "
				+ speech.length + " ";
 
		for (int i = 0; i < speech.length; i++) {
			content += speech[i].speech.replaceAll(" ", "`") + " ";
		}

		content += quests.keySet().size() + " ";

		ArrayList<String> keys = new ArrayList<String>();
		keys.addAll(quests.keySet());
		
		for (int i = 0; i < quests.keySet().size(); i++) {
			content += (keys.get(i)).replaceAll(" ", "`") + " ";
			content += quests.get(keys.get(i)).replaceAll(" ", "`") + " ";
		}

		return content;
	}

}
