package com.zandgall.arvopia.items;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.environment.Light;
import com.zandgall.arvopia.gfx.Accessory;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.Pos;
import com.zandgall.arvopia.gfx.SerialImage;
import com.zandgall.arvopia.utils.Public;

public class PlayerItem {

	public static final int NONE = 0, SWORD = 1, AXE = 2, PICKAXE = 3, SCYTHE = 4, TILLER = 5, SHOVEL = 6, UMBRELLA = 7;

	public Map<String, Integer> recipe;

	public ArrayList<Damager> damagers;

	Handler game;

	public Animation item, world;

	public boolean durable, hasLight, front = false;
	public double durability, damage, range, lightoffx, lightoffy, xoffset, yoffset;
	public int amount;
	public double delay;

	public String description;

	public int type;

	public Light light;

	public PlayerItem(Handler game, BufferedImage item, BufferedImage world, boolean durable, boolean hasLight,
			int lightoffx, int lightoffy, int durability, int damage, double attackswing, int range, int type,
			int xoffset, int yoffset) {
		this.game = game;
		this.item = Animation.getStill(item);
		this.world = Animation.getStill(world);
		this.durable = durable;
		this.durability = durability;
		this.damage = damage;
		this.delay = attackswing;
		this.range = range;
		this.hasLight = hasLight;
		this.lightoffx = lightoffx;
		this.lightoffy = lightoffy;
		this.type = type;
		light = new Light(game, lightoffx, lightoffy, 10, 50, Color.orange);
		
//		game.getEnviornment().getLightManager().addLight(light);
		if (!hasLight)
			turnOff();
		this.xoffset = xoffset;
		this.yoffset = yoffset;
		amount = 0;

		damagers = new ArrayList<Damager>();
	}

	public PlayerItem(Handler game, BufferedImage item, BufferedImage world, boolean durable, int durability,
			int damage, double attackswing, int range) {
		this(game, item, world, durable, durability, damage, attackswing, range, world.getWidth() / 2,
				world.getHeight() / 2);
	}

	public PlayerItem(Handler game, BufferedImage item, BufferedImage world, boolean durable, int durability,
			int damage, double attackswing, int range, int xoffset, int yoffset) {
		this(game, item, world, durable, durability, damage, attackswing, range, PlayerItem.NONE, xoffset, yoffset);
	}

	public PlayerItem(Handler game, BufferedImage item, BufferedImage world, boolean durable, int durability,
			int damage, double attackswing, int range, int type) {
		this(game, item, world, durable, durability, damage, attackswing, range, type, world.getWidth() / 2, 3);
	}

	public PlayerItem(Handler game, BufferedImage item, BufferedImage world, boolean durable, int durability,
			int damage, double attackswing, int range, int type, int xoffset, int yoffset) {
		this(game, item, world, durable, false, 0, 0, durability, damage, attackswing, range, type, xoffset, yoffset);
	}

	public void setRecipe(Map<String, Integer> recipe) {
		this.recipe = recipe;
	}

	public void turnOff() {
		light.turnOff();
	}

	public BufferedImage world() {
		return world.getFrame();
	}

	public BufferedImage item() {
		return item.getFrame();
	}

	public void tick(int worldx, int worldy) {

		world.tick();
		item.tick();
		if (hasLight) {
			if (!light.isOn()) {
				if (!game.getEnvironment().getLightManager().getList().contains(light))
					game.getEnvironment().getLightManager().addLight(light);
				light.turnOn();
			}
//			light.setMax(80);
			light.setX((int) (worldx + lightoffx));
			light.setY((int) (worldy + lightoffy));
		}
		if (durable)
			durability--;

		custTick(worldx, worldy);
	}

	public void custTick(int worldx, int worldy) {

	}

	public Pos offset(int widthflip) {
		if (widthflip > 0) {
			return new Pos(xoffset, yoffset);
		} else {
			return new Pos(world.getFrame().getWidth() - xoffset, yoffset);
		}
	}

	public void renderDamagers(Graphics g) {
//		for(Damager d: damagers)
//			d.render((Graphics2D) g);
	}

	// TODO
	public ArrayList<Creature> attack(int x, int y, Creature master, boolean stab1swing0, int widthFlip) {
		ArrayList<Creature> out = master.getInRadius(x, y,
				(int) ((this.world().getWidth() * 2.5 + this.world().getHeight() * 3) * (stab1swing0 ? 1 : 0.8)));

		Creature complete = Entity.getClosestTo(game.getMouse().fullMouseX(), game.getMouse().fullMouseY(), out);

		if(complete!=null) {
			complete.damage(stab1swing0 ? damage : damage * 1.3);
			float knockbackx = (float) (widthFlip * damage * 0.1f);
			
			if (stab1swing0) {
				complete.xVol += knockbackx;
				complete.yVol -= damage * 0.2f;
			}
		}

		return out;
	}

	public Accessory toAccessory() {
		return new Accessory(-5, 6, world().getWidth() - (int) xoffset, world().getHeight() - (int) yoffset, "rightforearm",
				new SerialImage(world()));
	}

}

class Damager {

	static BufferedImage image = ImageLoader.loadImage("/textures/Inventory/Tools/slash.png");

	ArrayList<Entity> hit;

	public static final int SLASH = 0, STAB = 1;

	boolean dead = false;

	int type = 0;

	int x, y;
	double xM, yM, strength, range, timer;

	public Damager(int type, int x, int y, double xM, double yM, double strength, double range) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.xM = xM;
		this.yM = yM;
		this.strength = strength;
		this.range = range;
		timer = 0;
		hit = new ArrayList<Entity>();
	}

	public void tick(Handler game) {
		timer++;
		if (timer > range) {
			dead = true;
		}
		if (this.type == STAB) {
			x += xM;
			y += yM;
			for (Entity e : game.getEntityManager().getEntities()) {
				if (e != game.getPlayer() && !hit.contains(e)) {
					if (e.creature) {
						if (e.getbounds().contains(x, y, 7, 24))
							((Creature) e).damage(strength);
						hit.add(e);
					}
				}
			}
		}
	}

	public void render(Graphics2D g) {
		g.drawImage(image, (int) Public.xO(x), (int) Public.yO(y), null);
	}

}
