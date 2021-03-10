package com.zandgall.arvopia.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Timeline {

	Color c;
	int x, y, length;
	String name;

	ArrayList<Float> values;

	public Timeline(Color c, int x, int y, int length, String name) {
		this.c = c;
		this.x = x;
		this.y = y;
		this.name = name;

		this.length = length;

		values = new ArrayList<Float>();
		for (int i = 0; i < length; i++) {
			values.add(0.0f);
		}
	}

	public void add(float value) {
		ArrayList<Float> temp = values;
		for (int i = 0; i < length - 1; i++) {
			values.set(i, temp.get(i + 1));
		}

		values.set(length - 1, value);
	}

	public void render(Graphics g, double max, int width, int height) {

		float rat = (float) ((1.0 / max) * (height + 0.0));

		g.setColor(Color.lightGray);
		g.drawRect(x, y, width, height);

		g.setColor(c);
		for (int i = 0; i < length - 1; i++) {
			int x1 = x + (int) ((i * width) / (length - 1.0));
			int x2 = x + (int) (((i + 1) * width) / (length - 1.0));

			int y1 = (y + height) - (int) (values.get(i) * rat);
			int y2 = (y + height) - (int) (values.get(i + 1) * rat);

			g.drawLine(x1, y1, x2, y2);
			g.drawLine(x1 + 1, y1 + 1, x2 + 1, y2 + 1);
			g.drawLine(x1, y1 + 1, x2, y2 + 1);
			g.drawLine(x1 - 1, y1 + 1, x2 - 1, y2 + 1);
			g.drawLine(x1 - 1, y1, x2 - 1, y2);
			g.drawLine(x1 - 1, y1 - 1, x2 - 1, y2 - 1);
			g.drawLine(x1, y1 - 1, x2, y2 - 1);
			g.drawLine(x1 + 1, y1 - 1, x2 + 1, y2 - 1);
			g.drawLine(x1 + 1, y1, x2 + 1, y2);
		}

	}

}
