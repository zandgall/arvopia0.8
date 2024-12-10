package com.zandgall.arvopia.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import com.zandgall.arvopia.input.MouseManager;

public class ArrowButton {

	int x, y, width, height;
	boolean dir;

	boolean hovered, on;

	Polygon shape, shade, outline;

	long last = 0;

	public ArrowButton(int x, int y, int width, int height, boolean dir) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.dir = dir;
		if (dir) {
			outline = new Polygon(new int[] { x - 1, x - 1, x + width + 1 },
					new int[] { y + height + 1, y - 1, y + height / 2 }, 3);
			shape = new Polygon(new int[] { x, x, x + width }, new int[] { y + height, y, y + height / 2 }, 3);
			shade = new Polygon(new int[] { x + 1, x + 1, x + width - 1 },
					new int[] { y + height, y + 1, y + height / 2 + 1 }, 3);
		} else {
			outline = new Polygon(new int[] { x - 1, x + width + 1, x + width + 1 },
					new int[] { y + height / 2, y - 1, y + height + 1 }, 3);
			shape = new Polygon(new int[] { x, x + width, x + width }, new int[] { y + height / 2, y, y + height }, 3);
			shade = new Polygon(new int[] { x + 1, x + width - 1, x + width - 1 },
					new int[] { y + height / 2 + 1, y + 1, y + height }, 3);
		}
	}

	public void tick(MouseManager mouse) {
		hovered = (mouse.getMouseX() > x && mouse.getMouseX() < x + 40 && mouse.getMouseY() > y
				&& mouse.getMouseY() < y + 40);
		on = (mouse.isLeftClicked() && hovered);
	}

	public boolean isOn() {
		return on;
	}

	public void render(Graphics g) {
		g.setColor(Color.black);
		g.fillPolygon(outline);
//		g.setColor((hovered ? new Color(230, 230, 230) : (on ? Color.gray : Color.lightGray)));
		g.setColor(on ? Color.gray : (hovered ? new Color(230, 230, 230) : Color.lightGray));
		g.fillPolygon(shape);
//		g.setColor((hovered ? Color.lightGray : (on ? Color.darkGray : Color.gray)));
		g.setColor(on ? Color.darkGray : (hovered ? Color.lightGray : Color.gray));
		g.fillPolygon(shade);

	}

}
