package com.zandgall.arvopia.guis;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.items.PlayerItem;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class CraftOutput {
	public String description = "unset";
	public String name = "unset";

	public CraftOutput(Handler handler, BufferedImage image, int x, int y, Recipe recipe) {
		this.image = image;

		this.recipe = recipe;

		this.x = x;
		this.y = y;
		ox = x;

		game = handler;
	}
	
	public CraftOutput(Handler handler, BufferedImage image, int x, int y, String[] names, int[] amounts, String name, String description) {
		this.image = image;

		this.recipe = new Recipe(getMap(names, amounts));

		this.x = x;
		this.y = y;
		ox = x;

		game = handler;
		
		setValues(name, description);
	}
	
	private static Map<String, Integer> getMap(String[] keys, int[] values) {
		Map<String, Integer> out = new HashMap<String, Integer>();
		for (int i = 0; i < keys.length; i++) {
			out.put(keys[i], Integer.valueOf(values[i]));
		}
		return out;
	}

	Recipe recipe;

	public void setValues(String name, String description) {
		this.name = name;
		this.description = description;
	}

	boolean clicked;
	Handler game;
	int ox;

	public void tick() {
		clicked = false;
		if ((game.getMouse().isLeft()) && (game.getMouse().rMouseX() > x)
				&& (game.getMouse().rMouseX() < x + image.getWidth()) && (game.getMouse().rMouseY() > y)
				&& (game.getMouse().rMouseY() < y + image.getHeight()))
			clicked = true;
	}
	
	public void tick(int width, int height) {
		clicked = false;
		if ((game.getMouse().isLeft()) && (game.getMouse().rMouseX() > x)
				&& (game.getMouse().rMouseX() < x + width) && (game.getMouse().rMouseY() > y)
				&& (game.getMouse().rMouseY() < y + height))
			clicked = true;
	}

	int y;
	int x;
	BufferedImage image;

	public boolean semiCraftable(Map<String, PlayerItem> list) {
		return recipe.semiCraftable(game, list);
	}
	
	public boolean craftable(Map<String, PlayerItem> list) {
		return recipe.craftable(game, list);
	}

	public void render(Graphics g) {
		g.drawImage(image, x, y, null);
	}
	
	public void render(Graphics g, int width, int height) {
		g.drawImage(image, x, y, width, height, null);
	}

	public void doSubtract() {
		for (String i : recipe.recipe.keySet()) {
			game.getWorld().getEntityManager().getPlayer().putItem(i, -recipe.recipe.get(i));
		}
	}

	public class Recipe {
		Map<String, Integer> recipe;

		public Recipe(Map<String, Integer> recipe) {
			this.recipe = recipe;
		}
		
		public boolean semiCraftable(Handler game, Map<String, PlayerItem> contents) {
			for (String o : recipe.keySet())
				for (String i : contents.keySet())
					if (i == o && contents.get(i).amount>0)
						return true;
			return false;
		}

		public boolean craftable(Handler game, Map<String, PlayerItem> contents) {
			boolean out = false;
			boolean thereishope = false;

			for (String o : recipe.keySet()) {
				thereishope = false;
				for (String i : contents.keySet())
					if (i == o) {
						thereishope = true;

						if ((contents.get(i).amount) >= ((Integer) recipe.get(o)).intValue()) {
							out = true;
						} else
							return false;
					}
				if (!thereishope) {
					return false;
				}
			}
			return out;
		}
	}
}
