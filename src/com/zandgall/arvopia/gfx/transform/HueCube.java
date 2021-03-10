package com.zandgall.arvopia.gfx.transform;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.input.MouseManager;
import com.zandgall.arvopia.utils.Public;

public class HueCube {

	private int x, y, width, height;

	private int sX, sY, hY;
	private float huy;

	private BufferedImage hueSelect, colSelect;

	public HueCube(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		sX = width;
		sY = 0;
		hY = 0;
		hueSelect = new BufferedImage(50, height, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics g = hueSelect.getGraphics();
		for (int i = 0; i < height; i++) {
			float hue = (float) ((i + 0.0) / (height + 0.0));
			g.setColor(Color.getHSBColor(hue, 1.0f, 1.0f));
			g.fillRect(0, i, width / 4, 1);
		}

		selectHue(0);
	}

	public void tick(MouseManager mouse) {
		if (mouse.getMouseX() > x && mouse.getMouseX() < x + width + width / 4 && mouse.getMouseY() > y
				&& mouse.getMouseY() < y + height && mouse.fullLeft) {
			if (mouse.getMouseX() > x + width && mouse.getMouseX() < x + width + width / 4) {
				selectHue(mouse.getMouseY() - y);
			} else if (mouse.getMouseX() > x) {
				selectColor(mouse.getMouseX() - x, mouse.getMouseY() - y);
			}
		}
	}

	public void selectHue(int ys) {
		hY = (int) Public.range(0, height, ys);
		huy = (float) ((hY + 0.0) / (height + 0.0));

		colSelect = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D i = colSelect.createGraphics();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				float sat = (float) ((x + 0.0) / (width + 0.0));
				float val = 1 - (float) ((y + 0.0) / (height + 0.0));

				i.setColor(Color.getHSBColor(huy, sat, val));

				i.fillRect(x, y, 1, 1);
			}
		}

	}

	public void selectColor(int x, int y) {
		sX = (int) Public.range(0, width, x);
		sY = (int) Public.range(0, height, y);
	}

	public Color getColor() {
		float sat = (float) ((sX + 0.0) / (width + 0.0));
		float val = 1 - (float) ((sY + 0.0) / (height + 0.0));
		return Color.getHSBColor(huy, sat, val);
	}

	public void setColor(Color c) {
		if (c == null) {
			c = Color.red;
		}
		float[] s = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), new float[] { 0.0f, 0.0f, 0.0f });
		selectHue((int) (s[0] * height));
		selectColor((int) (s[1] * width), (int) ((1 - s[2]) * height));
	}

	public void render(Graphics g) {
		g.drawImage(colSelect, x, y, null);
		g.drawImage(hueSelect, x + width, y, null);
		g.setColor(Color.black);
		g.drawRect(x - 1 + width, hY - 1 + y, 50, 3);

		g.drawOval(sX + x - 2, sY + y - 2, 4, 4);

		g.setColor(getColor());
		g.fillRect(x + width + width / 4, y, width / 4, height);

	}

}
