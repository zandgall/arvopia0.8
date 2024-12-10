package com.zandgall.arvopia.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;

public class TabMenu {

	BevelPlatform body;

	Handler game;

	ArrayList<Tab> tabs;

	int x, y, yOffset;

	boolean topbottom;

	public TabMenu(Handler game, String[] menu, int x, int y, int width, int height, boolean tabsOnTop) {
		yOffset = Tran.measureString(menu[0], Public.fipps).y + 6;
		body = new BevelPlatform(10, 10, width, height - yOffset);
		this.x = x;
		this.y = y;
		this.game = game;
		tabs = new ArrayList<Tab>();

		this.topbottom = tabsOnTop;

		int offset = 6;

		for (String m : menu) {
			if (tabsOnTop)
				tabs.add(new Tab(m, x + offset, y, tabsOnTop));
			else
				tabs.add(new Tab(m, x + offset, y + height - 6 - yOffset, tabsOnTop));
			offset += tabs.get(tabs.size() - 1).width + 6;
		}

		tabs.get(0).selected = true;

	}

	public String getTab() {
		for (Tab t : tabs) {
			if (t.selected)
				return t.name;
		}
		return "Null";
	}

	public void tick() {
		boolean isSelected = false;
		for (Tab t : tabs) {
			t.tick(game);
			isSelected = isSelected || t.selected;
		}

		if (!isSelected)
			tabs.get(0).selected = true;
	}

	public void render(Graphics2D g) {
		for (Tab t : tabs)
			t.render(g);

		if (topbottom)
			body.render(g, x, y + yOffset);
		else
			body.render(g, x, y);
	}

}

class Tab {

	String name;

	public int x, y;

	public int width, height;

	BufferedImage d, h;

	boolean selected = false;

	public Tab(String name, int x, int y, boolean topbottom) {
		this.x = x;
		this.y = y;
		this.name = name;

		width = Tran.measureString(name, Public.fipps).x + 6;
		height = Tran.measureString(name, Public.fipps).y + 12;

		initImage(topbottom);
	}

	public void tick(Handler game) {
		int mouseX = game.getMouse().rMouseX();
		int mouseY = game.getMouse().rMouseY();
		boolean mouseLeft = game.getMouse().isLeft();
		if ((mouseLeft) && mouseY > y && mouseY < y + height - 6) {
			game.getMouse().setLeftClicked(false);
			selected = ((mouseX > x - 1) && (mouseX < x + width + 1) && (mouseY > y - 1) && (mouseY < y + height + 1));
		}

	}

	public void initImage(boolean topbottom) {
		BufferedImage corners = ImageLoader.loadImage("/textures/Gui/sign2.png");

		int i = corners.getWidth() / 2;

		h = new BufferedImage(width + 6, height + 6, BufferedImage.TYPE_4BYTE_ABGR);
//		h = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = h.createGraphics();
		
		g.setRenderingHints(Tran.antialias);

		int j = h.getWidth(), k = h.getHeight();

		if (topbottom) {
			g.drawImage(corners.getSubimage(0, 0, i, i), 0, 0, null);
			g.drawImage(corners.getSubimage(i, 0, i, i), j - i, 0, null);
			g.drawImage(corners.getSubimage(i - 1, 0, 1, i), i, 0, j - i * 2, i, null);
			g.drawImage(corners.getSubimage(0, i - 1, i, 1), 0, i, i, k - i, null);
			g.drawImage(corners.getSubimage(i, i - 1, i, 1), j - i, i, i, k - i, null);
			g.drawImage(corners.getSubimage(i - 1, i - 1, 2, 2), i, i, j - i * 2, k - i, null);
		} else {
			g.drawImage(corners.getSubimage(i, i, i, i), j - i, k - i, null);
			g.drawImage(corners.getSubimage(0, i, i, i), 0, k - i, null);
			g.drawImage(corners.getSubimage(i-1, i, 1, i), i, k - i, j - i * 2, i, null);
			g.drawImage(corners.getSubimage(0, i - 1, i, 1), 0, 0, i, k - i, null);
			g.drawImage(corners.getSubimage(i, i - 1, i, 1), j - i, 0, i, k - i, null);
			g.drawImage(corners.getSubimage(i - 1, i - 1, 2, 2), i, 0, j - i * 2, k - i, null);
		}

		g.dispose();

//		o = Tran.bevelShade(h, 5, 150, 50);
//		d = Tran.bevelShade(h, 5, 250, 100);
//		h = Tran.bevelShade(h, 5, 255, 220);

		d = Tran.staticShader(h, 100, 0);
		h = Tran.staticShader(h, 200, -50);
	}

	public void render(Graphics2D g) {
		if (selected) {
			g.drawImage(d, x - 3, y - 3, d.getWidth() + 6, d.getHeight() + 6, null);
			g.setColor(Color.white);
		} else {
			g.drawImage(h, x, y, null);
			g.setColor(Color.black);
		}

		g.setFont(Public.fipps);
		g.drawString(name, x + 6, y + height - 6);
	}

}
