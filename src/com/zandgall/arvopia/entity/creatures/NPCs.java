package com.zandgall.arvopia.entity.creatures;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.water.Water;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class NPCs extends Creature {
	private static final long serialVersionUID = 1L;

	int widthFlip = -1;

	BufferedImage texture;

	Assets set;
	Animation jump;
	Animation still;
	Animation walk;
	Animation crouch;
	boolean jumping;
	Speech[] speech;
	Speech use;
	public int speechuse;
	boolean useSpeech = false;

	long timer = 0L;
	public boolean done = false;

	public NPCs(Handler handler, double x, double y) {
		super(handler, x-18, y-47, 18, 47, false, DEFAULT_SPEED, DEFAULT_ACCELERATION, (int) MAX_SPEED, false, false,
				DEFAULT_JUMP_FORCE, DEFAULT_JUMP_CARRY, "Unnamed");

		speech = new Speech[0];
		use = null;

		layer = 0.01D;

		MAX_HEALTH = 20;
		health = 20.0D;

		bounds.x = 12;
		bounds.y = 8;
		bounds.width = 10;
		bounds.height = 45;

//		jump = new Animation(1000, new BufferedImage[] { set.get(0, 1) }, "Jump", "Player");
//		still = new Animation(750, new BufferedImage[] { set.get(0, 0), set.get(1, 0) }, "Still", "Player");
//		walk = new Animation(250, new BufferedImage[] { set.get(1, 1), set.get(3, 1) }, "Walk", "Player");
//		crouch = new Animation(750, new BufferedImage[] { set.get(2, 0), set.get(3, 0) }, "Crouch", "Player");
	}

	public NPCs(Handler handler, double x, double y, double speed, String name, String[] speeches) {
		super(handler, x, y, 18, 47, false, speed, DEFAULT_ACCELERATION, (int) MAX_SPEED, false, false,
				DEFAULT_JUMP_FORCE, DEFAULT_JUMP_CARRY, name);

		speech = new Speech[speeches.length];
		for (int i = 0; i < speeches.length; i++) {
			speech[i] = new Speech(x, y - 25.0D, speeches[i]);
		}
		use = speech[0];

		texture = ImageLoader.loadImage("/textures/NPCs/" + name + "/" + name + ".png");

		set = new Assets(texture, 36, 54, "Player");

		layer = 0.01D;

		MAX_HEALTH = 20;
		health = 20.0D;

		bounds.x = 12;
		bounds.y = 8;
		bounds.width = 10;
		bounds.height = 45;

		jump = new Animation(1000, new BufferedImage[] { set.get(0, 1) }, "Jump", "Player");
		still = new Animation(750, new BufferedImage[] { set.get(0, 0), set.get(1, 0) }, "Still", "Player");
		walk = new Animation(250, new BufferedImage[] { set.get(1, 1), set.get(3, 1) }, "Walk", "Player");
		crouch = new Animation(750, new BufferedImage[] { set.get(2, 0), set.get(3, 0) }, "Crouch", "Player");
	}

	public void tick() {
		still.tick();
		walk.tick();
		crouch.tick();

		if ((useSpeech) && (use.done) && (game.getMouse().isRight())) {
			done = true;
		}

		if (useSpeech) {
			use.tick();
		}

		double prex = x;
		double prey = y;

		move();

		if (game.getEntityManager().getPlayer().getX() > x) {
			widthFlip = 1;
		} else {
			widthFlip = -1;
		}
		if ((prey != y) || (prex != x))
			for (int i = 0; i < speech.length; i++) {
				boolean b = use == speech[i];
				speech[i] = new Speech(x, y - 25.0D, speech[i].speech);
				if (b)
					setSpeech(i);
			}
	}

	public void useSpeech(boolean tf) {
		useSpeech = tf;
	}

	public Speech getSpeech() {
		return use;
	}

	public void setSpeech(int i) {
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
		int ty = (int) ((y + yMove + bounds.y + bounds.height) / Tile.HEIGHT);
		if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.WIDTH), ty))
				|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 3.0D) / Tile.WIDTH), ty))
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
		g.drawImage(Tran.flip(getFrame(), widthFlip, 1), 0,  0, null);
		g.setTransform(p);

		if (useSpeech) {
			use.render(g, game);
		}
	}

	public BufferedImage getFrame() {
		if (bottoms) {
			return still.getFrame();
		}
		return jump.getFrame();
	}

	public class Movement {
		public Movement() {
		}
	}

	public class Speech {
		public String speech;

		public String use = "";

		double i = 0.0D;

		double x;
		double y;
		public boolean done = false;

		public Speech(double x, double y, String speech) {
			this.speech = speech;
			this.x = x;
			this.y = y;
		}

		public void tick() {
			i += 0.3D;
			use = speech.substring(0, Math.min((int) i, speech.length()));

			if ((Math.min((int) i, speech.length()) == speech.length()) && (speech != "END")) {
				done = true;
			}
		}

		public void render(Graphics g, Handler game) {
			double nx = x * 1.1111D;
			double ny = y * 1.1111D;

			g.setColor(Color.white);
			g.fillRoundRect((int) (nx - game.xOffset() * 1.1111D - use.length() * 3 - 10.0D),
					(int) (ny - game.yOffset() * 1.1111D - 10.0D), use.length() * 6 + 20, 20, 20, 20);
			g.setColor(Color.black);
			g.drawRoundRect((int) (nx - game.xOffset() * 1.1111D - use.length() * 3 - 10.0D),
					(int) (ny - game.yOffset() * 1.1111D - 10.0D), use.length() * 6 + 20, 20, 20, 20);
			g.drawString(use, (int) (nx - game.xOffset() * 1.1111D - use.length() * 3),
					(int) (ny - game.yOffset() * 1.1111D) + 5);
		}
	}
}
