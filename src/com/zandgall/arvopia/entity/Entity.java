package com.zandgall.arvopia.entity;

import com.zandgall.arvopia.ArvopiaLauncher;
import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Cannibal;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.entity.creatures.npcs.NPC;
import com.zandgall.arvopia.entity.moveableStatics.MoveableStatic;
import com.zandgall.arvopia.entity.statics.House;
import com.zandgall.arvopia.entity.statics.Shrubbery;
import com.zandgall.arvopia.entity.statics.Tree.Leaf;
import com.zandgall.arvopia.environment.weather.SnowFlake;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.ClassLoading;
import com.zandgall.arvopia.utils.Public;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public abstract class Entity implements Serializable, Cloneable {

	private static final long serialVersionUID = 2033520744584885386L;
	
	public String modIdentifier = "Vanilla", entityIdentifier = "entityName";

	public static boolean variety = true;
	protected Handler game;
	protected double x;
	protected double y;
	protected int width;
	protected int height;
	protected Rectangle bounds;
	public boolean isSolid;
	public double layer;
	public boolean creature;
	public boolean particle;
	public boolean staticEntity;
	public boolean NPC = false;

	public boolean dead;
	public int ticks = 0;

	public Entity(Handler handler, double x, double y, int width, int height, boolean solid, boolean creature,
			boolean particle, boolean staticEntity) {
		if(handler==null)
			game = ArvopiaLauncher.game.handler;
		else game = handler;
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		dead = false;

		this.creature = creature;
		this.particle = particle;
		this.staticEntity = staticEntity;

		isSolid = solid;

		bounds = new Rectangle(0, 0, width, height);
	}


	public boolean alwaysTick() {
		return false;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void tick() {
	}

	public abstract void render(Graphics2D g);

	public void renderLight(Graphics2D g, float opacity) {
		
	}
	
	public BufferedImage defaultLightEffect(BufferedImage texture) {
		
		BufferedImage bleach = Tran.bleachImage(texture, Color.orange);
		BufferedImage out = new BufferedImage(texture.getWidth()+2, texture.getHeight()+2, BufferedImage.TYPE_4BYTE_ABGR);
		
		Graphics2D g = out.createGraphics();
		
		g.drawImage(bleach, 0, 0, null);
		g.drawImage(bleach, 1, 0, null);
		g.drawImage(bleach, 2, 0, null);
		
		g.drawImage(bleach, 0, 1, null);
		g.drawImage(bleach, 2, 1, null);
		
		g.drawImage(bleach, 0, 2, null);
		g.drawImage(bleach, 1, 2, null);
		g.drawImage(bleach, 2, 2, null);
		
		g.dispose();
		
		return out;
		
	}
	
	public boolean checkCollision(double xOffset, double yOffset) {
		for (Entity e : game.getWorld().getEntityManager().getEntities()) {
			try {
				if (!e.equals(this)) {
					if ((e.isSolid) && (e.getCollision(0.0F, 0.0F).intersects(getCollision(xOffset, yOffset))))
						return true;
				}
			} catch(NullPointerException ex) {
				System.err.println("NullPointerException in collision finding!");
				ex.printStackTrace();
			}
		}
		return false;
	}

	public boolean colBottom(double xOffset, double yOffset) {
		for (Entity e : game.getWorld().getEntityManager().getEntities()) {
			try {
				if (!e.equals(this)) {
					if ((e.isSolid) && (e.getCollision(0.0F, 0.0F).intersects(bottom(xOffset, yOffset))))
						return true;
				}
			} catch(NullPointerException ex) {
				System.err.println("NullPointerException in collision finding!");
				ex.printStackTrace();
			}
		}
		return false;
	}

	public boolean colTop(double xOffset, double yOffset) {
		for (Entity e : game.getWorld().getEntityManager().getEntities()) {
			try {
				if (!e.equals(this)) {
					if ((e.isSolid) && (e.getCollision(0.0F, 0.0F).intersects(top(xOffset, yOffset))))
						return true;
				}
			} catch(NullPointerException ex) {
				System.err.println("NullPointerException in collision finding!");
				ex.printStackTrace();
			}
		}
		return false;
	}

	public boolean colRight(double xOffset, double yOffset) {
		for (Entity e : game.getWorld().getEntityManager().getEntities()) {
			try {
				if (!e.equals(this)) {
					if ((e.isSolid) && (e.getCollision(0.0F, 0.0F).intersects(right(xOffset, yOffset))))
						return true;
				}
			} catch(NullPointerException ex) {
				System.err.println("NullPointerException in collision finding!");
				ex.printStackTrace();
			}
		}
		return false;
	}

	public boolean colLeft(double xOffset, double yOffset) {
		for (Entity e : game.getWorld().getEntityManager().getEntities()) {
			try {
				if (!e.equals(this)) {
					if ((e.isSolid) && (e.getCollision(0.0F, 0.0F).intersects(left(xOffset, yOffset))))
						return true;
				}
			} catch(NullPointerException ex) {
				System.err.println("NullPointerException in collision finding!");
				ex.printStackTrace();
			}
		}
		return false;
	}

	public boolean checkTouching(double xOffset, double yOffset) {
		for (Entity e : game.getWorld().getEntityManager().getEntities()) {
			try {
				if (!e.equals(this)) {
					if (e.getCollision(0.0F, 0.0F).intersects(getCollision(xOffset, yOffset)))
						return true;
				}
			} catch(NullPointerException ex) {
				System.err.println("NullPointerException in collision finding!");
				ex.printStackTrace();
			}
		}
		return false;
	}

	public Entity getClosest(double x, double y) {
		ArrayList<Entity> e = game.getWorld().getEntityManager().getEntities();
		if (e.isEmpty())
			return null;
		Entity b = (Entity) e.get(0);

		for (Entity i : e) {
			if ((i != this) && !(i instanceof MoveableStatic) && !(i instanceof AreaAdder) && !(i instanceof Leaf)) {
//				if (distance(i.centerX(), i.centerY(), x, y) < distance(b.centerX(), b.centerY(), x, y)) {
				if (((x-i.centerX())*(x-i.centerX())+(y-i.centerY())*(y-i.centerY())) < ((x-b.centerX())*(x-b.centerX())+(y-b.centerY())*(y-b.centerY())))
					b = i;
//				}
			}
		}
		return b;
	}

	public Entity getClosestStatic(double x, double y) {
		if (game.getEntityManager().getEntities().isEmpty())
			return null;
		ArrayList<Entity> e = game.getEntityManager().getEntities();
		Entity b = null;
		for (int v = 0; v < e.size(); v++) {
			if (!e.get(v).creature && (e.get(v)!=this)&& e.get(v).x>game.xOffset()&&e.get(v).y>game.yOffset()&&e.get(v).x<game.xOffset()+game.width&&e.get(v).y<game.yOffset()+game.height && !(e.get(v) instanceof MoveableStatic) && !(e.get(v) instanceof AreaAdder) && !(e.get(v) instanceof House) && !(e.get(v) instanceof Leaf)) {
				if (b == null || ((x-e.get(v).centerX())*(x-e.get(v).centerX())+(y-e.get(v).centerY())*(y-e.get(v).centerY())) < ((x-b.centerX())*(x-b.centerX())+(y-b.centerY())*(y-b.centerY())))
					b = e.get(v);
			}
		}
		return b;
	}

	public static Entity precisionCollision(ArrayList<Entity> entities, int x, int y) {
		
		x = Math.max(x, 1);
		y = Math.max(y, 1);
		
		for(Entity e: entities) {
			int nx = (int) (x-e.getX());
			int ny = (int) (y-e.getY());
			if(nx>=0&&ny>=0&&nx<=e.getWidth()&&ny<=e.getHeight()) {
//				System.out.println("Found for snow " + e.getClass().getName());
				BufferedImage precision = new BufferedImage((int) (1+e.getWidth()), (int) (1+e.getHeight()), BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D g = precision.createGraphics();
				g.setColor(new Color(10, 20, 10));
				g.fillRect(0, 0, 720, 400);
				g.translate(-e.getX(), -e.getY());
				g.translate(e.game.xOffset(), e.game.yOffset());
				e.render(g);
				g.translate(e.getX(), e.getY());
				g.translate(-e.game.xOffset(), -e.game.yOffset());
				if(precision.getRGB(nx, ny)!=Tran.accurateColor(new Color(10, 20, 10)))
					return e;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static void loadJar(String jarfile) {
		try {
			ArrayList<Class<?>> objects = ClassLoading.getClasses(jarfile);

			for (Class<?> out : objects) {
				if(Entity.class.isAssignableFrom(out)) {
					EntityManager.entityEntries.put(out.getName(), new EntityEntry((Class<? extends Entity>) out));
//					System.out.println("Mod class " + out.getClass());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public NPC getClosestNPC(double x, double y) {
//		System.out.println(e.isEmpty());
		ArrayList<Entity> e = game.getEntityManager().getEntities();
		if (e.isEmpty())
			return null;
		NPC b = null;
		for (int v = 0; v < e.size(); v++) {
			if ((e.get(v) instanceof NPC) && (((NPC) e.get(v)).usingSpeech())&& e.get(v).x>game.xOffset()&&e.get(v).y>game.yOffset()&&e.get(v).x<game.xOffset()+game.width&&e.get(v).y<game.yOffset()+game.height) {
				if (b == null || ((x-e.get(v).centerX())*(x-e.get(v).centerX())+(y-e.get(v).centerY())*(y-e.get(v).centerY())) < ((x-b.centerX())*(x-b.centerX())+(y-b.centerY())*(y-b.centerY())))
					b = (NPC) e.get(v);
			}
		}
		return b;
	}

	public ArrayList<Creature> getClosest(final double x, double y, int amount) {
		ArrayList<Entity> e = game.getWorld().getEntityManager().getEntities();

		ArrayList<Creature> list = new ArrayList<Creature>();

		Comparator<Creature> c = (a, b) -> {
			if (distance(x, y, a.centerX() * Game.scale, a.centerY() * Game.scale) < distance(x, y,
					b.centerX() * Game.scale, b.centerY() * Game.scale)) {
				return -1;
			}
			return 1;
		};

		for (Entity i : e) {
			if ((i.creature) && (i != this)) {
				if (list.size() < amount) {
					list.add((Creature) i);
				} else {
					list.sort(c);
					Creature b = (Creature) list.get(list.size() - 1);
					if ((distance(i.centerX() * Game.scale, i.centerY() * Game.scale, x,
							y) < distance(b.centerX() * Game.scale, b.centerY() * Game.scale, x, y))) {
						list.add(list.indexOf(b), (Creature) i);
					}
				}
			}
		}

		if (list.size() > amount) {
			list.subList(amount, list.size()).clear();
		}
		return list;
	}

	public double centerX() {
		return x + bounds.x + bounds.width / 2.0;
	}

	public double centerY() {
		return y + bounds.y + bounds.height / 2.0;
	}

	public Creature getClosestFromList(double x, double y, ArrayList<Creature> e) {
		if (e.size() == 0)
			return null;

		Creature out = e.get(0);

		for (Creature i : e) {
			if (i != this) {
				Creature b = out;
				if ((distance(i.centerX() * Game.scale, i.centerY() * Game.scale, x,
						y) < distance(b.centerX() * Game.scale, b.centerY() * Game.scale, x, y))) {
					out = i;
				}
			}
		}
		return out;
	}
	
	public static Creature getClosestTo(double x, double y, ArrayList<Creature> e) {
		if (e.size() == 0)
			return null;

		Creature out = e.get(0);

		for (Creature i : e) {
			if ((i.distance(i.centerX() * Game.scale, i.centerY() * Game.scale, x,
					y) < i.distance(out.centerX() * Game.scale, out.centerY() * Game.scale, x, y))) {
				out = i;
			}
		}
		return out;
	}

	public ArrayList<Creature> getInRadius(double x, double y, int radius) {
		ArrayList<Entity> e = game.getWorld().getEntityManager().getEntities();

		ArrayList<Creature> list = new ArrayList<Creature>();

		for (Entity i : e) {
			if ((i.creature) && (i != this) && (Public.dist(centerX(), centerY(), i.centerX(), i.centerY()) < radius)) {
				list.add((Creature) i);
			}
		}

		return list;
	}

	public ArrayList<Cannibal> getClosestCannibal(final double x, double y, int amount) {
		ArrayList<Entity> e = game.getWorld().getEntityManager().getEntities();
		Cannibal b;
		if ((e.size() > 0) && (((Entity) e.get(0)).getClass() == Cannibal.class)) {
			b = (Cannibal) e.get(0);
		}
		ArrayList<Cannibal> list = new ArrayList<Cannibal>();

		Comparator<Cannibal> c = (a, b1) -> {
			if (distance(a.getX(), a.getY(), x, y) < distance(b1.getX(), b1.getY(), x, y)) {
				return -1;
			}
			return 1;
		};

		for (Entity i : e) {
			if (i.getClass() == Cannibal.class) {
				if (list.size() < amount) {
					list.add((Cannibal) i);
				} else {
					list.sort(c);
					b = (Cannibal) list.get(list.size() - 1);
					if (i.creature) {
						if (distance(i.x + i.getbounds().x + i.getbounds().getWidth() / 2.0D,
								i.getY() + i.getbounds().y + i.getbounds().getHeight() / 2.0D, x,
								y) < distance(b.getX() + b.getbounds().x + b.getbounds().getWidth() / 2.0D,
										b.getY() + b.getbounds().y + b.getbounds().getHeight() / 2.0D, x, y)) {
							list.add((Cannibal) i);
						}
					}
				}
			}
		}
		if (list.size() < amount) {
			for (int i = 0; i < amount - list.size(); i++) {
				list.add(null);
			}
		}

		return list;
	}

	public Cannibal getAlphaCannibal(double x, double y, int amount) {
		Cannibal output = null;

		for (Entity e : game.getWorld().getEntityManager().getEntities()) {
			if (e.getClass() == Cannibal.class) {
				Cannibal c = (Cannibal) e;
				if (c.alpha) {
					output = c;
				}
			}
		}
		return output;
	}

	public Entity getEntity(double xMove, double d) {
		for (Entity e : game.getWorld().getEntityManager().getEntities()) {
			if (!e.equals(this)) {
				if ((e.isSolid) && (e.getCollision(0.0F, 0.0F).intersects(getCollision(xMove, d))))
					return e;
			}
		}
		return this;
	}

	public Entity getTouched(double xOffset, double yOffset) {
		for (Entity e : game.getWorld().getEntityManager().getEntities()) {
			if (!e.equals(this)) {
				if (e.getCollision(0.0F, 0.0F).intersects(getCollision(xOffset, yOffset)))
					return e;
			}
		}
		return this;
	}

	public Entity getShrub(double xOffset, double yOffset) {
		for (Entity e : game.getWorld().getEntityManager().getEntities()) {
			if ((!e.equals(this)) && (e.getClass() == Shrubbery.class)) {
				if (e.getCollision(0.0F, 0.0F).intersects(getCollision(xOffset, yOffset)))
					return e;
			}
		}
		return this;
	}

	public Rectangle getCollision(double xOffset, double yOffset) {
		return new Rectangle((int) (x + bounds.x + xOffset), (int) (y + bounds.y + yOffset), bounds.width,
				bounds.height);
	}

	public Rectangle bottom(double xOffset, double yOffset) {
		return new Rectangle((int) (x + bounds.x + 2 + xOffset), (int) (y + bounds.y + bounds.height + yOffset - 1),
				bounds.width - 4, 1);
	}

	public Rectangle top(double xOffset, double yOffset) {
		return new Rectangle((int) (x + bounds.x + 2 + xOffset), (int) (y + bounds.y + yOffset), bounds.width - 4, 1);
	}

	public Rectangle right(double xOffset, double yOffset) {
		return new Rectangle((int) (x + bounds.x + bounds.width - 1 + xOffset), (int) (y + bounds.y + 2 + yOffset), 1,
				bounds.height - 4);
	}

	public Rectangle left(double xOffset, double yOffset) {
		return new Rectangle((int) (x + bounds.x + xOffset), (int) (y + bounds.y + 2 + yOffset), 1, bounds.height - 4);
	}

	public boolean stopsSnow() {
		return true;
	}
	
	public Rectangle getbounds() {
		return bounds;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void reset() {
	}

	public void showBox(Graphics2D g) {
		if (!creature) {
			if (isSolid) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.green);
			}

			g.drawRect((int) (x + bounds.x),
					(int) (y + bounds.y), bounds.width, bounds.height);
		}

		if (creature) {
			Creature c = (Creature) this;

			int left = (int) (x + bounds.x);
			int right = (int) (x + bounds.x + bounds.width);
			int top = (int) (y + bounds.y);
			int bottom = (int) (y + bounds.y + bounds.height);

			if (c.lefts) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.green);
			}
			g.fillRect(left, top, 2, bottom - top);
			if (c.rights) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.green);
			}
			g.fillRect(right, top, 2, bottom - top);
			if (c.tops) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.green);
			}
			g.fillRect(left, top, right - left, 2);
			if (c.bottoms) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.green);
			}
			g.fillRect(left, bottom, right - left + 2, 2);
		}
	}

	protected double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2.0D) + Math.pow(y2 - y1, 2.0D));
	}

	public void kill() {
		game.getWorld().getEntityManager().getEntities().remove(this);
	}

	public boolean intersects(int x, int y, int w, int h) {
		return new Rectangle((int) (this.x + bounds.x), (int) (this.y + bounds.y), bounds.width, bounds.height)
				.intersects(x, y, w, h);
	}

	public boolean mapable() {
		return false;
	}

	public Color mapColor() {
		return Color.black;
	}

	public Point mapSize() {
		return new Point(0, 0);
	}

	public String toString() {
		if(this.getClass().getName().startsWith("com.zandgall.arvopia"))
			return this.getClass().getSimpleName() + " (" + x + ", " + y + ") " + layer;
		else return this.getClass().getName()+" (" + x + ", " + y + ") " + layer;
	}

	public Entity clone() {
		try {
			return (Entity) super.clone();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean shouldRender() {
		return x + width >= game.xOffset() && y + height >= game.yOffset()
				&& x < game.xOffset() + game.width && y < game.yOffset() + game.width;
	}

	public boolean shouldTick() {
		return x + width >= game.xOffset() && y + height >= game.yOffset()
				&& x < game.xOffset() + game.width && y < game.yOffset() + game.width;
	}

	public boolean canCurrentlySpawn(Handler handler) {
		return true;
	}

	public void interactWithSnowFlake(SnowFlake snowFlake) {
	}
}
