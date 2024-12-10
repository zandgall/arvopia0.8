package com.zandgall.arvopia.utils;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.transform.Tran;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Slider {

	static Slider used = null;

	private Handler game;
	private double min;
	private double max;
	private double val;
	private boolean info;
	public boolean hovered, using;
	private int x;
	private int y;
	private int mouseX;
	private int mouseY;
	private boolean mouseLeft;
	private String name;

	BufferedImage track, trackh, tab, tabh;

	public Slider(Handler game, double min, double max, double val, boolean info, String name) {
		this.game = game;

		this.min = min;
		this.max = max;

		this.val = Public.Map(val, max, min, 100.0D, 0.0D);

		this.info = info;
		this.name = name;

		x = 0;
		y = 0;

		size(100, 30);

	}

	public Slider(Handler game, double min, double max, String name) {
		this.game = game;

		this.min = min;
		this.max = max;

		this.val = Public.Map(0.5, 1, 0, 100.0, 0.0);

		this.info = true;
		this.name = name;

		x = 0;
		y = 0;

		size(100, 30);

	}

	public Slider(Handler game, double min, double max, double val, String name) {
		this.game = game;

		this.min = min;
		this.max = max;

		this.val = Public.Map(val, max, min, 100.0D, 0.0D);

		this.info = true;
		this.name = name;

		x = 0;
		y = 0;

		size(100, 30);
	}

	public void size(int width, int height) {
		BevelIndent b = new BevelIndent(2, 2, width, height - 14, 255, 0);

		track = b.getImage();
		trackh = Tran.staticShader(track, 200, 0);

		b = new BevelIndent(2, 2, 16, height, 265, 10);

		tab = Tran.flip(b.getImage(), -1, -1);
		tabh = Tran.staticShader(tab, 277, 50);

	}

	public void tick(int x, int y) {
		mouseX = game.getMouse().rMouseX();
		mouseY = game.getMouse().rMouseY();
		mouseLeft = game.getMouse().isLeft();

		this.x = x;
		this.y = y;

		if ((mouseX > x - 1) && (mouseX < x + track.getWidth() + 1) && (mouseY > y - 8)
				&& (mouseY < y + tab.getHeight() - 8)) {
			hovered = true;
		} else {
			hovered = false;
		}

		if ((hovered && ((mouseLeft && game.getMouse().isDragged()) || (game.getMouse().isLeftClicked()))) || using) {
			using = (used == null || used == this);
			if (used == null)
				used = this;

			if ((using || hovered) && game.getMouse().fullLeft) {
				val = Public.range(0, track.getWidth(), mouseX - x);
			}

		}
		if (!hovered && !(game.getMouse().fullLeft) || used != this) {
			using = false;
			used = null;
		}
	}

	public void render(Graphics g) {
		/*
		 * g.setColor(Color.black); g.drawRect(x - 1, y - 1, 101, 16);
		 * g.setColor(Color.darkGray); g.fillRect(x, y, 100, 15); if (!hovered) {
		 * g.setColor(Color.lightGray); } else { g.setColor(Color.gray); } g.fillRect(x
		 * + 1, y + 1, 99, 14); if (!hovered) { g.setColor(Color.gray); } else {
		 * g.setColor(new Color(100, 100, 100)); } g.fillRect(x + 1, y + 1, 97, 12);
		 * 
		 * g.setColor(Color.black); if (max == 1 && min == 0) g.drawString(name + " " +
		 * getWholeValue(), x + 10, y + 30); else g.drawString(name + " " + getValue(),
		 * x + 10, y + 30);
		 */
		if (hovered)
			g.drawImage(trackh, x, y, null);
		else
			g.drawImage(track, x, y, null);

		if (hovered)
			g.drawImage(tabh, (int) (x + val - 7), y - 7, null);
		else
			g.drawImage(tab, (int) (x + val - 7), y - 7, null);

		if (info) {
			g.setFont(Public.defaultFont);
			g.setColor(Color.black);
			if (max == 1 && min == 0)
				g.drawString(name + " " + getWholeValue(), x + 10, y + 30);
			else
				g.drawString(name + " " + getValue(), x + 10, y + 30);
		}

//		if ((val > -1.0D) && (val < 101.0D)) {
//			g.setColor(Color.black);
//			g.drawRect((int) (x + val - 11.0D), y - 3, 21, 21);
//			g.setColor(Color.darkGray);
//			g.fillRect((int) (x + val - 10.0D), y - 2, 20, 20);
//			g.setColor(Color.lightGray);
//			g.fillRect((int) (x + val - 10.0D), y - 2, 18, 18);
//			g.setColor(Color.gray);
//			g.fillRect((int) (x + val - 9.0D), y - 1, 17, 17);
//		} else {
//			game.log("Error on Slider: " + name);
//		}
	}

	public int getValue() {
		return (int) Math.round(Public.Map(val, 100.0D, 0.0D, max, min));
	}

	public double getWholeValue() {
		return Public.Map(val, 100.0D, 0.0D, max, min);
	}

	public void setValue(double val) {
		this.val = Public.Map(val, (int) max, (int) min, 100.0D, 0.0D);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
