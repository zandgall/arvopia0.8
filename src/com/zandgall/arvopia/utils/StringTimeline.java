package com.zandgall.arvopia.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import com.zandgall.arvopia.gfx.transform.Tran;

public class StringTimeline {

	Color c;
	int x, y, length;
	String name;

	ArrayList<String> values;

	public StringTimeline(Color c, int x, int y, int length, String name) {
		this.c = c;
		this.x = x;
		this.y = y;
		this.name = name;

		this.length = length;

		values = new ArrayList<String>();
		for (int i = 0; i < length; i++) {
			values.add("Empty");
		}
	}

	public void add(String value) {
		ArrayList<String> temp = values;
		for (int i = 0; i < length - 1; i++) {
			values.set(i, temp.get(i + 1));
		}

		values.set(length - 1, value);
	}

	public void render(Graphics g) {

		g.setColor(Color.lightGray);

		g.setColor(c);
		g.setFont(Public.defaultBoldFont);
		for (int i = 0; i < length - 1; i++) {
//			g.drawString(values.get(i), x, y+i*14);
			Tran.drawOutlinedText(g, x, y + i * 14, values.get(i), 1, Public.invert(c), c);
		}

	}

}
