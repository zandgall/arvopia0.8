package com.zandgall.arvopia.entity.statics;

import com.zandgall.arvopia.ArvopiaLauncher;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.environment.weather.SnowFlake;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Noise;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;
import static java.lang.Math.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Random;

public class Tree extends StaticEntity {
	@Serial
	private static final long serialVersionUID = 1L;

	public static final double TEMP_DIFF = 2;
	private static final BufferedImage
		segment = ImageLoader.loadImage("/textures/Statics/Tree.png").getSubimage(0, 36, 18, 18),
		segmentTop = ImageLoader.loadImage("/textures/Statics/Tree.png").getSubimage(0, 18, 18, 18),
		segmentBottom = ImageLoader.loadImage("/textures/Statics/Tree.png").getSubimage(0, 54, 18, 18),
		segmentLight = Tran.litUp(segment),
		segmentTopLight = Tran.litUp(segmentTop);


	private final Branch branch;
	private final double TempOffset;

	private int age, growthTime;
	private long lastTime = 0L;

	private float snow = 0;

	public Tree(Handler handler, double x, double y) {
		this(handler, x, y, 0);
	}

	public Tree(Handler handler, double x, double y, int age) {
		super(handler, x, y, 36, 144, false, max(age * 2, 10), PlayerItem.AXE);

		layer = Public.expandedRand(-10.0D, 10.0D);

		this.age = age;
		branch = new Branch(age+3, 0.95f, 0, 0, (int)(age*0.25)+1, System.nanoTime());
		updateLeaves();
		float[] b = branch.bounds();
		bounds.x = (int) b[0];
		bounds.y = (int) b[1];
		bounds.height = -bounds.y;
		height = bounds.height;
		bounds.width = (int)(b[2]-bounds.x);
		width = bounds.width;
		// Go to center of spawn
		this.x -= bounds.width/2.0;

		growthTime = ((int) Public.expandedRand(-1000.0D, 5000.0D));

		TempOffset = Public.expandedRand(-10, 10);
	}

	private void updateLeaves() {
		if(game!=null&&game.getWorld()!=null&&game.getEnvironment()!=null) {
			double temp = game.getEnvironment().getTemp() + TempOffset;
			branch.updateLeaves(game.foliageColor(temp, snow));
		}
	}

	@Override
	public void interactWithSnowFlake(SnowFlake snowFlake) {
		snow+=0.01;
		snow = min(snow, 1);
		updateLeaves();
	}

	public void tick() {
		if (game.getWorld().getEnvironment().getTotalTime() - lastTime >= Math.pow(1.5, age+1)*1000 + growthTime) {
			lastTime = game.getWorld().getEnvironment().getTotalTime();
			age++;
			branch.generate(age+3, 0.95f, 0, 0, (int)(age*0.25)+1);
			growthTime = Public.randInt(-5000.0D - game.getWorld().getEnvironment().getHumidity() * 10.0D,
					10000.0D);
			updateLeaves();

			if (age == 32) {
				game.getWorld().kill(this);
				return;
			}

			float[] b = branch.bounds();
			bounds.x = (int) b[0];
			bounds.y = (int) b[1];
			bounds.height = -bounds.y;
			height = bounds.height;
			bounds.width = (int)(b[2]-bounds.x);
			width = bounds.width;
		}

		if((game.getEnvironment().getTemp() + TempOffset*2) % TEMP_DIFF == 0)
			updateLeaves();

		double temp = game.getEnvironment().getTemp()+TempOffset;
		if(temp < 25)
			snow+=0.0001;
		if (temp > 20 && Public.chance(age/16.0*temp*(-0.01*temp+1.2)-40))
			game.getWorld().getEntityManager()
					.addEntity(new Leaf(game, Public.rand(x + bounds.x, x + bounds.x + bounds.width),
							Public.rand(y+bounds.y, y + bounds.y + bounds.height),
							game.foliageColor(game.getEnvironment().getTemp()+TempOffset, snow)), false);
	}
	public void render(Graphics2D g) {
		AffineTransform pre = g.getTransform();
		g.translate(x, y);
		g.scale(age+3, age+3);
		branch.renderLeavesOutline(g, x, y, 0);
		branch.renderLeaves(g, x, y, 0);
		try {
			int sx = game.getEnvironment().sunX() + 9;
			int sy = game.getEnvironment().sunY() + 9;
			Composite pre_comp = g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) Math.max(1 - Public.dist(centerX(), centerY(), sx, sy) / (5 * 18), 0)));
			branch.renderLeavesLight(g, x, y, 0);
			g.setComposite(pre_comp);
		} catch(NullPointerException e) {
			// ignore
		}

		branch.render(g, x, y, 0);
		g.setTransform(pre);
	}
	
	public void renderLight(Graphics2D g, float opacity) {
		AffineTransform pre = g.getTransform();
		g.translate(x, y);
		g.scale(age+3, age+3);
		Composite pre_comp = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		if(game.getEnvironment().getTemp() + TempOffset > 20)
			branch.renderLeavesOutlineLight(g, x, y, 0);
		branch.renderLight(g, x, y, 0);
		g.setComposite(pre_comp);
		g.setTransform(pre);
	}

	public int getAge() {
		return age;
	}

	@Override
	public boolean canCurrentlySpawn(Handler game) {
		return game.getEnvironment().getTemp() > 32.0D;
	}

	public static class Leaf extends Entity {
		@Serial
		private static final long serialVersionUID = 1L;

		private final int widthFlip;
		private final BufferedImage leaf;
		private final long start;
		private final Noise walker;

		private double rotation = 0, carriedXVel = 0, carriedYVel = 0;

		public Leaf(Handler handler, double x, double y, Color color) {
			super(handler, x, y, 12, 12, false, false, false, false);
			walker = new Noise(System.nanoTime());
			start = System.currentTimeMillis();

			leaf = Tran.effectColor(ImageLoader.loadImage("/textures/Statics/Shrubbery/Leaves.png"), color).getSubimage(0,0,12,12);
			layer = -1;
			widthFlip = Public.chance(50) ? - 1 : 1;
			bounds.x = -6;
			bounds.y = -6;
		}

		public void render(Graphics2D g) {
			AffineTransform p = g.getTransform();
			g.translate(x, y);
			g.rotate(rotation);
			g.drawImage(Tran.flip(leaf, widthFlip, 1), -6, -6, null);
			g.setTransform(p);
		}

		public void tick() {
			if (System.currentTimeMillis()-start > 10000) {
				game.getWorld().kill(this);
			}

			double wind = game.getWind(x, y);
			double currentRotation = (walker.get1(System.nanoTime()*1.0e-9))*PI*2;
			double xVel = cos(currentRotation)*max(wind, 0.1);
			double yVel = sin(currentRotation)*max(wind, 0.1)+0.4;
			carriedXVel+=cos(currentRotation)*0.01+wind*0.01;
			carriedYVel+=sin(currentRotation)*0.01;
			for(Entity e : game.getEntityManager().getEntities()) {
				if(e instanceof Creature&&e.getCollision(0, 1+((Creature) e).getyMove()).contains(x, y)) {
					carriedXVel += ((Creature) e).getxMove();
					carriedYVel += -0.5 + ((Creature) e).getyMove();
				}
			}
			if(yVel != 0 && (World.getTile((int)((x+xVel)/Tile.WIDTH), (int)((y+yVel)/Tile.HEIGHT)).isSolid() ||
					(World.getTile((int)((x+xVel)/Tile.WIDTH), (int)((y+yVel)/Tile.HEIGHT)).isTop() && yVel > 0))) {
				xVel*=0.8; // Slower on ground
				if(yVel > 0) {
					y = floor((y + yVel) / Tile.HEIGHT) * Tile.HEIGHT;
					carriedYVel	= min(carriedYVel, 0);
				}
				else {
					y = floor((y + yVel) / Tile.HEIGHT + 1) * Tile.HEIGHT;
					carriedYVel	= max(carriedYVel, 0);
				}
				yVel = 0;
				currentRotation = 0;
			} else if(xVel!= 0 && World.getTile((int)((x+xVel)/Tile.WIDTH), (int)((y+yVel)/Tile.HEIGHT)).isSolid()) {
				yVel*=0.8; // Slower on wall
				if(xVel > 0) {
					carriedXVel	= min(carriedXVel, 0);
					x = floor((x + xVel) / Tile.HEIGHT) * Tile.HEIGHT;
				}
				else {
					carriedXVel	= max(carriedXVel, 0);
					x = floor((x + xVel) / Tile.HEIGHT + 1) * Tile.HEIGHT;
				}
				xVel = 0;
				currentRotation = PI*0.5;
			}
			carriedXVel*=0.8;
			carriedYVel*=0.8;
			rotation = rotation * 0.9 + currentRotation * 0.1;
			x+=xVel+carriedXVel;
			y+=yVel+carriedYVel;

		}
	}

	public static class Branch {
		private ArrayList<Branch> children;
		private float globalScale, localScale, rotation, offset;
		private Random random;
		private final long seed;
		private BufferedImage leaves, leavesOutline, lightLeaves, lightLeavesOutline;
		public Branch(float globalScale, float localScale, float offset, float rotation, int steps, long seed) {
			this.seed = seed;
			generate(globalScale, localScale, offset, rotation, steps);
		}

		public void generate(float globalScale, float localScale, float offset, float rotation, int steps) {
			random = new Random(seed);
			children = new ArrayList<>();
			this.globalScale = globalScale;
			this.localScale = localScale;
			this.rotation = rotation;
			this.offset = offset;
			if(steps < -4)
				return;
			float nextRotRange = 23.f;
			if(steps > 0) { // Main trunk
				children.add(new Branch(globalScale * 0.95f, 0.95f, random.nextFloat(-0.02f, 0.04f), random.nextFloat(-1.f, 2.f), steps-1, seed+1));
				if(random.nextDouble()<0.1f) {
					boolean right = random.nextBoolean();
					children.add(new Branch(globalScale * 0.8f, 0.8f, right? 0.6f : -0.6f, (right ? nextRotRange : -nextRotRange) + random.nextFloat(-1.f, 2.f), steps - 1, seed+2));
				}
			} else {
				float newScale = 0.95f;

				for(int i = 0, n = random.nextDouble()<(-4-steps)*-0.04 ? 2 : 1; i < n; i++) {
					float newRot = random.nextFloat(0.1f);
					newRot*=newRot;
					newRot = (random.nextBoolean() ? -45 : 45) * (0.1f-newRot);
					if(i == 1)
						newRot*=10;

					children.add(new Branch(globalScale * newScale, newScale, newRot/90.f*newScale*0.4f, newRot, steps - 1, seed+1+i));
					newScale = random.nextFloat(0.4f, 0.5f);
				}
			}
		}

		public float[] bounds(float rotation) {
			float[] out = new float[3];
			float up = globalScale * (float)abs(cos((rotation+this.rotation)*PI/180.0)*1);
			float right = globalScale * (float)abs(cos((rotation+this.rotation)*PI/180.0)+sin((rotation+this.rotation)*PI/180.0));
			out[0] = -right;
			out[1] = -up;
			out[2] = right;
			for(Branch b: children) {
				float[] res = b.bounds(rotation+this.rotation);
				out[0] = min(out[0], res[0]);
				out[1] = min(out[1], -up + res[1]);
				out[2] = max(out[2], res[2]);
			}
			return out;
		}

		public float[] bounds() {
			return bounds(0);
		}

		public void updateLeaves(Color color) {
			if(children.isEmpty())
				drawLeaves(color);
			else for(Branch c : children)
				c.updateLeaves(color);
		}

		public void drawLeaves(Color color) {
			float scale = this.globalScale/18.f;
			random.setSeed(this.seed);
			leaves = new BufferedImage((int)(18*10*scale), (int)(18*10*scale), BufferedImage.TYPE_4BYTE_ABGR);
			leavesOutline = new BufferedImage((int)(18*10*scale), (int)(18*10*scale), BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = leaves.createGraphics();
			Graphics2D o = leavesOutline.createGraphics();
			o.setColor(new Color(150.f/65536.f*color.getRed(), 150.f/65536.f*color.getGreen(), 150.f/65536.f*color.getBlue()));
			g.translate(18*5*scale, 18*5*scale);
			o.translate(18*5*scale, 18*5*scale);
			ArrayList<Double> points = new ArrayList<>();
			for(int i = 0, n = random.nextInt(15, 20); i < n; i++) {
				double radius = random.nextDouble(27*scale, 45*scale);
				double angle = random.nextDouble(PI*2.0);
				double v = (45 * scale) - radius;
				double x = cos(angle) * 2 * v - radius;
				double y = sin(angle) * 2 * v - radius;
				points.add(x);
				points.add(y);
				points.add(radius);
				o.fillOval((int)(x-2), (int)(y-2), (int)(radius*2)+4, (int)(radius*2)+4);
			}
			for(int i = 0; i < points.size(); i+=3) {
				int x = points.get(i).intValue();
				int y = points.get(i+1).intValue();
				int radius = points.get(i+2).intValue();
				g.setColor(new Color(197.f/65536.f*color.getRed(), 197.f/65536.f*color.getGreen(), 197.f/65536.f*color.getBlue()));
				g.fillOval(x, y, radius*2, radius*2);
			}
			for(int i = 0; i < points.size(); i+=3) {
				int x = points.get(i).intValue();
				int y = points.get(i+1).intValue();
				int radius = points.get(i+2).intValue();
				g.setColor(new Color(211.f/65536.f*color.getRed(), 211.f/65536.f*color.getGreen(), 211.f/65536.f*color.getBlue()));
				g.fillOval(x+1, y+1, radius*2-5, radius*2-5);
			}
			for(int i = 0; i < points.size(); i+=3) {
				int x = points.get(i).intValue();
				int y = points.get(i+1).intValue();
				int radius = points.get(i+2).intValue();
				g.setColor(new Color(231.f/65536.f*color.getRed(), 231.f/65536.f*color.getGreen(), 231.f/65536.f*color.getBlue()));
				g.fillOval(x+2, y+2, radius*2-10, radius*2-10);
			}
			for(int i = 0; i < points.size(); i+=3) {
				int x = points.get(i).intValue();
				int y = points.get(i+1).intValue();
				int radius = points.get(i+2).intValue();
				g.setColor(new Color(240.f/65536.f*color.getRed(), 240.f/65536.f*color.getGreen(), 240.f/65536.f*color.getBlue()));
				g.fillOval(x+4, y+4, radius*2-20, radius*2-20);
			}
			g.dispose();
			o.dispose();

			lightLeaves = Tran.litUp(leaves);
			lightLeavesOutline = Tran.litUp(leavesOutline);
		}

		public void renderLeavesOutline(Graphics2D g, double worldX, double worldY, double globalRot) {
			AffineTransform pre = g.getTransform();
			globalRot+=rotation;
			worldX+=cos(globalRot)*globalScale*0.875;
			worldY+=sin(globalRot)*globalScale*0.875;
			transformation(g, worldX, worldY);
			if(children.isEmpty()) {
				g.translate(-0.5, -1);
				g.drawImage(leavesOutline, -6, -6, 12, 12, null);
				g.translate(0.5, 1);
			}
			g.translate(0, -0.875);
			for(Branch b : children)
				b.renderLeavesOutline(g, worldX, worldY, globalRot);
			g.setTransform(pre);
		}
		public void renderLeavesOutlineLight(Graphics2D g, double worldX, double worldY, double globalRot) {
			AffineTransform pre = g.getTransform();
			globalRot+=rotation;
			worldX+=cos(globalRot)*globalScale*0.875;
			worldY+=sin(globalRot)*globalScale*0.875;
			transformation(g,worldX, worldY);
			if(children.isEmpty()) {
				g.translate(-0.5, -1);
				g.drawImage(lightLeavesOutline, -6, -6, 12, 12, null);
				g.translate(0.5, 1);
			}
			g.translate(0, -0.875);
			for(Branch b : children)
				b.renderLeavesOutlineLight(g, worldX, worldY, globalRot);
			g.setTransform(pre);
		}
		public void renderLeaves(Graphics2D g, double worldX, double worldY, double globalRot) {
			AffineTransform pre = g.getTransform();
			globalRot+=rotation;
			worldX+=cos(globalRot)*globalScale*0.875;
			worldY+=sin(globalRot)*globalScale*0.875;
			transformation(g, worldX, worldY);
			if(children.isEmpty()) {
				g.translate(-0.5, -1);
				g.drawImage(leaves, -6, -6, 12, 12, null);
				g.translate(0.5, 1);
			}
			g.translate(0, -0.875);
			for(Branch b : children)
				b.renderLeaves(g, worldX, worldY, globalRot);
			g.setTransform(pre);
		}

		public void renderLeavesLight(Graphics2D g, double worldX, double worldY, double globalRot) {
			AffineTransform pre = g.getTransform();
			globalRot+=rotation;
			worldX+=cos(globalRot)*globalScale*0.875;
			worldY+=sin(globalRot)*globalScale*0.875;
			transformation(g, worldX, worldY);
			if(children.isEmpty()) {
				g.translate(-0.5, -1);
				g.drawImage(lightLeaves, -6, -6, 12, 12, null);
				g.translate(0.5, 1);
			}
			g.translate(0, -0.875);
			for(Branch b : children)
				b.renderLeavesLight(g, worldX, worldY, globalRot);
			g.setTransform(pre);
		}
		public void render(Graphics2D g, double worldX, double worldY, double globalRot) {
			AffineTransform pre = g.getTransform();
			globalRot+=rotation;
			worldX+=cos(globalRot)*globalScale*0.875;
			worldY+=sin(globalRot)*globalScale*0.875;
			transformation(g, worldX, worldY);
			g.translate(-0.5, -1);
			g.drawImage(segmentBottom, 0, 1, 1, 1, null);
			g.drawImage(segment, 0, 0, 1, 1, null);
			g.drawImage(segmentTop, 0, -1, 1, 1, null);
			g.translate(0.5, 1-0.875);
			for(Branch b : children)
				b.render(g, worldX, worldY, globalRot);
			g.setTransform(pre);
		}

		public void renderLight(Graphics2D g, double worldX, double worldY, double globalRot) {
			AffineTransform pre = g.getTransform();
			globalRot+=rotation;
			worldX+=cos(globalRot)*globalScale*0.875;
			worldY+=sin(globalRot)*globalScale*0.875;
			transformation(g, worldX, worldY);
			g.translate(-0.5-1.0/20.0, -1-1.0/20.0);
			g.drawImage(segmentLight, 0, 0, 1, 1, null);
			g.drawImage(segmentTopLight, 0, -1, 1, 1, null);
			g.translate(0.5+1.0/20.0, 1+1.0/20.0-0.875);
			for(Branch b : children)
				b.renderLight(g, worldX, worldY, globalRot);
			g.setTransform(pre);
		}

		private void transformation(Graphics2D g, double x, double y) {
			g.translate(offset*0.5, 0);
			g.scale(localScale, localScale);
			g.rotate(rotation*PI/180.0);
			g.rotate(ArvopiaLauncher.game.handler.getWind(x, y)*0.015);
		}
	}

	public String toString() {
		return "Tree " + x + " " + y + " " + layer + " " + age;
	}

}
