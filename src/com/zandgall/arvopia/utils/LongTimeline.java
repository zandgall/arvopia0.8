package com.zandgall.arvopia.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.gfx.transform.Tran;

public class LongTimeline {

	Color c;
	int x, y, length;
	String name;

	ArrayList<Tracker> trackers;

	long largest = 0l;
	int largestPos = 0;

	public LongTimeline(Color[] cs, String[] names, String name, int x, int y, int length, int height) {
		this.x = x;
		this.y = y;
		this.name = name;

		this.length = length;

		trackers = new ArrayList<Tracker>();
		for (int i = 0; i < cs.length; i++) {
			trackers.add(new Tracker(cs[i], x, y, length, names[i]));
		}
	}

	public LongTimeline(double[] valsdontmatter, Color[] cs, String[] names, String name, int x, int y, int length,
			int height) {
		this.x = x;
		this.y = y;
		this.name = name;

		this.length = length;

		trackers = new ArrayList<Tracker>();
		for (int i = 0; i < cs.length; i++) {
			trackers.add(new Tracker(cs[i], x, y, length, names[i]));
		}
	}

	public void add(long value, int trackerID) {
		trackers.get(trackerID).add((value == 1 ? 0 : value));
	}

	public long getLargest() {

		long larg = 10l;

		for (Tracker t : trackers)
			if (t.getLargest() > larg)
				larg = t.getLargest();

		return larg;
	}

	public void render(Graphics g, long max, int width, int height) {

		g.setFont(Public.defaultBoldFont);

		g.setColor(Color.white);
		g.fillRect(x, y - 5, width + 50 + 5, height + 30 + 14 * trackers.size());
		g.setColor(Color.black);
		g.drawRect(x, y, width, height);

		g.setColor(c);
		for (int i = 0; i < trackers.size(); i++) {
			trackers.get(i).render(g, max, width, height);
			g.fillRect(x + 1, y + height + i * 14 + 24, 12, 12);
			g.setColor(Color.black);
			g.drawString(
					trackers.get(i).name + " " + trackers.get(i).values.get(trackers.get(i).values.size() - 1) / 2000,
					x + 16, y + height + i * 14 + 37);
		}

		g.setColor(Color.black);

		g.drawLine(x, y, x + width, y);
		g.drawLine(x, y + height / 2, x + width, y + height / 2);
		g.drawLine(x, y + height / 4, x + width, y + height / 4);
		g.drawLine(x, (int) (y + height * 0.75), x + width, (int) (y + height * 0.75));
		g.drawLine(x, y + height, x + width, y + height);

		g.drawString("" + max / 1000, x + width + 5, y + 5);
		g.drawString("" + (max / 2000), x + width + 5, y + 5 + height / 2);
		g.drawString("0", x + width + 5, y + 5 + height);
		g.drawString("" + (max / 4000), x + width + 5, 5 + y + (int) (((height) / 4) * 3));
		g.drawString("" + ((int) Math.floor(max * 0.75) / 1000), x + width + 5, 5 + y + height / 4);

		g.drawString(name, x + 10, y + height + 20);

	}

	public void render(Graphics g) {
		render(g, getLargest(), 100, 100);
	}

	public void export() {
		int size = (100 * length);

		BufferedImage out = new BufferedImage(Math.max(length * 100, 0), 100, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = out.createGraphics();

		g.setRenderingHints(Tran.mix(new RenderingHints[] { Tran.renderquality, Tran.antialias, Tran.textantialias }));

		g.setColor(Color.white);
		g.fillRect(0, 0, size, 100);

		for (int i = 0; i < trackers.size(); i++) {
			g.drawImage(trackers.get(i).outputIMG(getLargest(), 100 * length, 100), 0, 0, null);
		}

		g.dispose();

		try {
			ImageIO.write(out, "png", new File(Game.prefix + "/Arvopia/logs/" + name + ".png"));
			System.out.println("Wrote: " + Game.prefix + "/Arvopia/logs/" + name + ".png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

class Tracker {
	Color c;
	int x, y, length;
	ArrayList<Long> values;

	String name;

	long largest = 0l;
	int largestPos = 0;

	public Tracker(Color c, int x, int y, int length, String name) {
		this.c = c;
		this.x = x;
		this.y = y;

		this.name = name;

		this.length = length;

		values = new ArrayList<Long>();
		for (int i = 0; i < length; i++) {
			values.add(0l);
		}
	}

	public void add(long value) {
		ArrayList<Long> temp = values;
		if (values.size() > length) {
			for (int i = values.size() - length; i < values.size() - 1; i++) {
				values.set(i, temp.get(i + 1));
			}

			values.set(values.size() - 1, value);
		} else {
			values.add(value);
		}

		largestPos++;

		if (largestPos > length) {
			largest = 0;
			for (int i = values.size() - length; i < values.size(); i++) {
				if (values.get(i) > largest)
					largest = values.get(i);
			}
		}

		if (value > largest) {
			largest = value;
			largestPos = 0;
		}
	}

	public long getLargest() {
		return largest;
	}

	public void render(Graphics g, long max, int width, int height) {

		float rat = (float) ((1.0 / max) * (height + 0.0));

		g.setColor(c);
		for (int j = values.size() - length; j < values.size() - 1; j++) {
			int i = (j - values.size()) + length;
			int x1 = x + (int) ((i * width) / (length - 1.0));
			int x2 = x + (int) (((i + 1) * width) / (length - 1.0));

			int y1 = (y + height) - (int) (values.get(j) * rat);
			int y2 = (y + height) - (int) (values.get(j + 1) * rat);

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

	public BufferedImage outputIMG(long max, int width, int height) {
		BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = out.createGraphics();

//		g.setRenderingHints(Tran.mix(new RenderingHints[] {Tran.renderquality, Tran.antialias,Tran.textantialias}));

		float rat = (float) ((1.0 / max) * (height + 0.0));

		g.setColor(c);
		for (int i = 0; i < values.size() - 1; i++) {
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

		g.dispose();

		return out;
	}

}
