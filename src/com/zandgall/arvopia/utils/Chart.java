package com.zandgall.arvopia.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.zandgall.arvopia.gfx.transform.Tran;

public class Chart {
	double[] vals;
	String[] components;
	String name;
	int x;
	int y;
	int width;
	int height;
	Slice[] slices;

	public Chart(String[] components, java.awt.Color[] colors, String name, int x, int y, int width,
				 int height) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.components = components;

		slices = new Slice[components.length];
		for (int i = 0; i < slices.length; i++) {
			slices[i] = new Slice(1, components[i], colors[i]);
		}
	}

	public void update(double[] values) {
		for (int i = 0; i < slices.length; i++) {
			slices[i].value = values[i];
		}
	}

	public void render(Graphics2D g) {
		drawPie(g, new java.awt.Rectangle(x, y, width, height), slices);

		double max = 0;
		for (Slice i : slices) {
			max += i.value;
		}

		g.setFont(Public.defaultBoldFont);
		g.setColor(java.awt.Color.white);
		int nameoff = g.getFontMetrics().stringWidth(name);
		Tran.drawOutlinedText(g, x + width / 2.0 - nameoff / 2, y + height / 2, name, 1, Color.black, Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 10));
		for (int i = 0; i < slices.length; i++) {
			g.setColor(slices[i].color);
			g.fillRect(x, y + height + 7 + i * 16, width, 15);
			String s = slices[i].name + ": " + (Math.round((slices[i].value / max) * 1000) / 10) + "%";
			nameoff = g.getFontMetrics().stringWidth(s);
			Tran.drawOutlinedText(g, x + width / 2.0 - nameoff / 2, y + height + 20 + i * 16, s, 1, Color.black,
					Color.white);
		}
	}

	void drawPie(java.awt.Graphics2D g, java.awt.Rectangle area, Slice[] slices) {
		double total = 0.0D;
		for (int i = 0; i < slices.length; i++) {
			total += slices[i].value;
		}

		double curValue = 0.0D;
		int startAngle = 0;
		for (int i = 0; i < slices.length; i++) {
//			startAngle = (int) (curValue * 360.0D / total);
//			int arcAngle = (int) (slices[i].value * 360.0D / total);
//
			g.setColor(slices[i].color);
//			g.fillArc(x, y, width, height, startAngle, arcAngle);
//			curValue += slices[i].value;
			
			g.fillRect((int) (area.x+curValue), area.y, (int) (area.width*(slices[i].value/total)), area.height);
			curValue+=(area.width*(slices[i].value/total));
			
		}
	}

	class Slice {
		double value;
		java.awt.Color color;
		String name;

		public Slice(double value, String name, java.awt.Color color) {
			this.value = value;
			this.color = color;
			this.name = name;
		}
	}
}
