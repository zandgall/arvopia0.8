package com.zandgall.arvopia.entity;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.*;
import com.zandgall.arvopia.entity.creatures.basic.*;
import com.zandgall.arvopia.entity.creatures.npcs.*;
import com.zandgall.arvopia.entity.moveableStatics.*;
import com.zandgall.arvopia.entity.statics.*;
import com.zandgall.arvopia.input.KeyManager;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntityManager implements Serializable {
	private static final long serialVersionUID = -3564913896955024635L;
	private Handler handler;
	private Player player;
	private ArrayList<Entity> entities;
	public static HashMap<String, EntityEntry> entityEntries = new HashMap<>();
	private java.util.Comparator<Entity> sort;

	public Map<String, Long> total, number, average, totalr, numberr, averager;

	public static long playert = 1l, othert = 1l;

	public static long bat = 1l, bear = 1l, skunk = 1l;

	public EntityManager(Handler handler, Player player) {
		this.handler = handler;
		this.player = player;
		entities = new ArrayList<Entity>();
		addEntity(player, true);
		sort = (a, b) -> {
			if (a.layer < b.layer)
				return 1;
			return -1;
		};

	}

	public String saveString() {
		String content = entities.size() + System.lineSeparator();

		content = "";

		for (Entity e : entities) {

			if (e instanceof Player)
				continue;

			content += e.toString();

			/*
			 * Misc - x, y, layer Cannibal - x, y, layer, walkspeed, lives, alpha Cloud - x,
			 * y, layer, type, speed Shrubbery - x, y, layer, type Stone - x, y, layer, type
			 * Tree - x, y, layer, age Flower - x, y, layer, type House - x, y, layer, type
			 * Villager - x, y, layer, type
			 */

			content += System.lineSeparator();

		}

		return content;
	}

	public void loadEntities(String content) {

		String[] t = content.split(System.lineSeparator());

		for (String s : t) {
			String[] c = s.split("\\s+");
			c[0] = c[0] + " ";

//			System.out.println(c[0] + (c[0].contains("VillagerPlus ")));

			for (EntityEntry a : entityEntries.values()) {
//				a.add(c[0], 0, 0);
				if (c[0].contains(a.name + " ")) {
					addEntity(a.spawn(0, 0));
					break;
				}
			}

			// Space in front of name to only return true if it contains only the name and
			// not, for example, "VillagerPlus"

			if (c[0].contains("Bee "))
				entities.add(new Bee(handler, 0, 0, false, 1000));
			else if (c[0].contains("Butterfly "))
				entities.add(new Butterfly(handler, 0, 0, false, 1000));
			else if (c[0].contains("Fox "))
				entities.add(new Fox(handler, 0, 0));
			else if (c[0].contains("Fairy "))
				entities.add(new Fairy(handler, 0, 0, Utils.parseBoolean(c[4])));
			else if (c[0].contains("Wolf "))
				entities.add(new Wolf(handler, 0, 0));
			else if (c[0].contains("Bear "))
				entities.add(new Bear(handler, 0, 0));
			else if (c[0].contains("Bat "))
				entities.add(new Bat(handler, 0, 0));
			else if (c[0].contains("Skunk "))
				entities.add(new Skunk(handler, 0, 0));
			else if (c[0].contains("Lia "))
				entities.add(new Lia(handler, 0, 0));
			else if (c[0].contains("Cannibal "))
				entities.add(new Cannibal(handler, 0, 0, Utils.parseDouble(c[5]), Utils.parseInt(c[4]),
						Utils.parseBoolean(c[6])));
			else if (c[0].contains("Cloud "))
				entities.add(new Cloud(handler, 0, 0, Utils.parseInt(c[4]), Utils.parseDouble(c[5])));
			else if (c[0].contains("Shrubbery "))
				entities.add(new Shrubbery(handler, 0, 0, Utils.parseInt(c[4])));
			else if (c[0].contains("Stone "))
				entities.add(new Stone(handler, 0, 0, Utils.parseInt(c[4])));
			else if (c[0].contains("Tree "))
				entities.add(new Tree(handler, 0, 0, Utils.parseInt(c[4])));
			else if (c[0].contains("Flower "))
				entities.add(new Flower(handler, 0, 0, Utils.parseInt(c[4]), 0));
			else if (c[0].contains("House "))
				entities.add(new House(handler, 0, 0, Utils.parseInt(c[4])));
			else if (c[0].contains("Villager "))
				entities.add(new Villager(handler, 0, 0, Utils.parseInt(c[4])));
			else if (c[0].contains("Template ")) {
				int l = Utils.parseInt(c[6]);
				String[] speeches = new String[l];

				for (int j = 0; j < l; j++) {
					speeches[j] = c[7 + j].replaceAll("`", " ");
				}

				int l2 = Utils.parseInt(c[6 + l]);
				Map<String, String> quests = new HashMap<String, String>();
				for (int j = 0; j < l2 && j * 2 + 6 + l < c.length; j++) {
					quests.put(c[6 + l + j * 2].replaceAll("`", " "), c[6 + l + j * 2 + 1].replaceAll("`", " "));
				}

				entities.add(
						new Template(handler, 0, 0, Utils.parseInt(c[5]), c[4].replaceAll("~", " "), speeches, quests));
			}

			entities.get(entities.size() - 1).x = Utils.parseDouble(c[1]);
			entities.get(entities.size() - 1).y = Utils.parseDouble(c[2]);
			entities.get(entities.size() - 1).layer = Utils.parseDouble(c[3]);

		}

	}

	public void tick() {
		othert = 0;
		bat = 0;
		bear = 0;
		skunk = 0;

		boolean counting = KeyManager.checkBind("Debug");

		if (counting) {
			total = new HashMap<String, Long>();
			number = new HashMap<String, Long>();
			average = new HashMap<String, Long>();
		}

		handler.getWorld().shrubbery = 0;

		for (int i = 0; i < entities.size(); i++) {
			if (i >= entities.size())
				return;
			Entity e = entities.get(i);

			if (e instanceof Shrubbery && ((Shrubbery) e).type > 2)
				handler.getWorld().shrubbery++;

//      if ((e == player) || ((e.getX() + e.getWidth() > handler.xOffset()) && (e.getX() - e.getWidth() < handler.xOffset() + handler.getWidth()) && (e.getY() + e.getHeight() > handler.yOffset()) && (e.getY() - e.getHeight() < handler.yOffset() + handler.getHeight()))) {
			handler.getWorld().outOfBounds(e);
			if (i >= entities.size())
				return;
			if (e.shouldTick()) {
				
				long pre = System.nanoTime();

				if (counting) {
					String name = e.getClass().getSimpleName();
					if (!number.containsKey(name))
						number.put(name, 0l);
					if (!total.containsKey(name))
						total.put(name, 0l);
					number.put(name, number.get(name) + 1);
				}
				entities.get(i).ticks++;
//				if(entities.get(i).ticks>1)
//					System.out.println(entities.get(i).ticks);
				entities.get(i).tick();
				if (i >= entities.size())
					return;
				if (entities.get(i).creature) {
					Creature c = (Creature) entities.get(i);
					c.regen();
				}
				if (i >= entities.size())
					return;
				if (entities.get(i).NPC) {
					com.zandgall.arvopia.entity.creatures.npcs.NPC n = (com.zandgall.arvopia.entity.creatures.npcs.NPC) entities
							.get(i);
					n.uniTick();
				}

				if (counting) {
					String name = e.getClass().getSimpleName();
					total.put(name, total.get(name) + (System.nanoTime() - pre) / 1000);
				}
			}

		}

//		othert/=Math.max(entities.size(), 1);
		
		if(counting) {
			long pre = System.currentTimeMillis();
			for (String e : total.keySet()) {
				average.put(e, total.get(e) / number.get(e));
			}
			average.put("TICK TIME", System.currentTimeMillis() - pre);
		}
	}

	BufferedImage image;
	
	public void render(Graphics2D g, boolean tf) {

		boolean counting = KeyManager.checkBind("Debug");
		
		if(counting) {
			totalr = new HashMap<>();
			numberr = new HashMap<>();
			averager = new HashMap<>();
		}
		
//		handler.getWorld().center.tick();
		
		for (Entity e : entities) {
			e.ticks = 0;
			if(e.shouldRender()) {
				long pre = System.nanoTime();

				String name = e.getClass().getSimpleName();

				if(counting) {
					if (!numberr.containsKey(name))
						numberr.put(name, 0L);
					if (!totalr.containsKey(name))
						totalr.put(name, 0L);
					numberr.put(name, numberr.get(name) + 1);
				}
				
				e.render(g);
				if (e.creature) {
					Creature c = (Creature) e;

					if (c.health < c.MAX_HEALTH)
						c.showHealthBar(g);

					if (tf)
						e.showBox(g);
				}

				if(counting)
					totalr.put(name, totalr.get(name) + (System.nanoTime() - pre) / 1000);

			}
		}
		
		if(counting) {
			long pre = System.currentTimeMillis();
			for (String e : total.keySet())
				if (totalr.containsKey(e) && numberr.containsKey(e))
					averager.put(e, totalr.get(e) / numberr.get(e));
			averager.put("TICK TIME", System.currentTimeMillis() - pre);
		}
	}

	public void renderLight(Graphics2D g) {
		int sx = handler.getEnvironment().sunX() + 9;
		int sy = handler.getEnvironment().sunY() + 9;
		for (Entity e : getEntitiesTouching(sx - 5 * 18, sy - 5 * 18, 180, 180)) {
			e.renderLight(g, (float) Math.max(1 - Public.dist(e.centerX(), e.centerY(), sx, sy) / (5 * 18), 0));
		}
	}

	public void gui(Graphics2D g) {
		for (Entity e : entities)
			if (e instanceof NPC)
				((NPC) e).gui(g);

		player.renScreens(g);
		player.renderElse(g);
	}

	public void addEntity(Entity e, boolean tf) {
		boolean in = false;
		for(int i = 1; i < entities.size(); i++)
			if(entities.get(i-1).layer < e.layer && e.layer < entities.get(i).layer) {
				entities.add(i, e);
				in = true;
				break;
			}
		if(!in)
			entities.add(e);
		if (tf)
			handler.logWorld("Entity " + e + " added at (" + e.x + ", " + e.y + ", " + e.layer + ")");
	}

	public void addEntity(Entity e) {
		addEntity(e, false);
	}

	public ArrayList<Entity> getEntitiesTouching(int x, int y, int w, int h) {
		ArrayList<Entity> out = new ArrayList<Entity>();
		for (Entity e : entities) {
			if (e.intersects(x, y, w, h))
				out.add(e);
		}
		return out;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
		addEntity(player, true);
	}

	public ArrayList<Entity> getEntities() {
		return entities;
	}

	public void setEntities(ArrayList<Entity> entities) {
		this.entities = entities;
	}

	public void list() {
		for (Entity e : entities) {
			if (!(e instanceof Shrubbery))
				System.out.print(e + ", ");
		}

		System.out.println("end");
	}

}
