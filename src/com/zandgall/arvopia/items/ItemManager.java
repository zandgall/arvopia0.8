package com.zandgall.arvopia.items;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Player;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class ItemManager implements Serializable {
	private static final long serialVersionUID = 7364862682722667321L;
	private Handler handler;
	private ArrayList<Item> items;

	public ItemManager(Handler handler, Player p) {
		Item.init(p);
		this.handler = handler;
		items = new ArrayList<Item>();
	}

	public void tick() {
		Iterator<Item> it = items.iterator();
		while (it.hasNext()) {
			Item i = (Item) it.next();
			i.tick();
			if (i.isPickedUp())
				it.remove();
		}
	}

	public void render(Graphics2D g, boolean box) {
		for (Item i : items)
			i.render(g, box);
	}

	public void addItem(Item i) {
		i.setHandler(handler);
		items.add(i);
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
