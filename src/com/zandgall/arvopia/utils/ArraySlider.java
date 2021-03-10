package com.zandgall.arvopia.utils;

import com.zandgall.arvopia.Handler;
import java.awt.Color;
import java.awt.Graphics;

public class ArraySlider {
	private Handler game;
	private double val;
	private boolean hv;
	public boolean hovered;
	private int x;
	private int y;
	private int mouseX;
	private int mouseY;
	private boolean mouseLeft;
	private String name;
	private String[] array;

	public ArraySlider(Handler game, String[] array, double val, boolean hv, String name) {
		this.game = game;

		this.array = array;

		this.val = Public.Map(val, array.length - 1, 0.0D, 100.0D, 0.0D);

		this.hv = hv;
		this.name = name;

		x = 0;
		y = 0;
	}

	public void tick(int x, int y) {
		mouseX = game.getMouse().rMouseX();
		mouseY = game.getMouse().rMouseY();
		mouseLeft = game.getMouse().isLeft();

		this.x = x;
		this.y = y;

		if ((mouseX > x - 1) && (mouseX < x + 101) && (mouseY > y - 5) && (mouseY < y + 10)) {
			hovered = true;
		} else {
			hovered = false;
		}

		if ((mouseLeft) && ((game.getMouse().isDragged()) || (game.getMouse().isClicked()))) {
			if (hv) {
				if ((mouseX > x - 1) && (mouseX < x + 101) && (mouseY > y - 5) && (mouseY < y + 10)) {
					val = (mouseX - x);
				}
			} else if ((mouseY > y - 1) && (mouseY < y + 101) && (mouseX > x - 5) && (mouseX < x + 10)) {
				val = (mouseY - y);
			}
		}
	}

	public void render(Graphics g) {
		g.setFont(Public.defaultFont);
		if (hv) {
			g.setColor(Color.black);
			g.drawRect(x - 1, y - 1, 101, 16);
			g.setColor(Color.darkGray);
			g.fillRect(x, y, 100, 15);
			if (!hovered) {
				g.setColor(Color.lightGray);
			} else {
				g.setColor(Color.gray);
			}
			g.fillRect(x + 1, y + 1, 99, 14);
			if (!hovered) {
				g.setColor(Color.gray);
			} else {
				g.setColor(new Color(100, 100, 100));
			}
			g.fillRect(x + 1, y + 1, 97, 12);

			g.drawString(name + " " + getValue(), x + 10, y + 30);
		} else {
			g.setColor(Color.black);
			g.drawRect(x - 1, y - 1, 16, 101);
			g.setColor(Color.darkGray);
			g.fillRect(x, y, 15, 101);
			g.setColor(Color.lightGray);
			g.fillRect(x + 1, y + 1, 14, 99);
			g.setColor(Color.gray);
			g.fillRect(x + 1, y + 1, 12, 97);
		}

		if ((val > -1.0D) && (val < 101.0D)) {
			g.setColor(Color.black);
			g.drawRect((int) (x + val - 11.0D), y - 3, 21, 21);
			g.setColor(Color.darkGray);
			g.fillRect((int) (x + val - 10.0D), y - 2, 20, 20);
			g.setColor(Color.lightGray);
			g.fillRect((int) (x + val - 10.0D), y - 2, 18, 18);
			g.setColor(Color.gray);
			g.fillRect((int) (x + val - 9.0D), y - 1, 17, 17);
		} else {
			game.log("Error on Slider: " + name);
		}
	}

	public String getValue() {
		return array[((int) Math.round(Public.Map(val, 100.0D, 0.0D, array.length - 1, 0.0D)))];
	}

	public void setValue(double val) {
		this.val = Public.Map(val, array.length - 1, 0.0D, 100.0D, 0.0D);
	}
}
