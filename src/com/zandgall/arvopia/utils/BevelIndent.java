package com.zandgall.arvopia.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;

public class BevelIndent {

	BufferedImage h, o, d;

	public BevelIndent(int bevel, int round, int width, int height) {

		BufferedImage corners = ImageLoader.loadImage("/textures/Gui/sign3.png");
		int i = corners.getWidth() / 2;
		h = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = h.createGraphics();

		g.setRenderingHints(Tran.antialias);

		g.drawImage(corners.getSubimage(0, 0, i, i), 0, 0, null);
		g.drawImage(corners.getSubimage(i, 0, i, i), width - i, 0, null);
		g.drawImage(corners.getSubimage(i, i, i, i), width - i, height - i, null);
		g.drawImage(corners.getSubimage(0, i, i, i), 0, height - i, null);
		g.drawImage(corners.getSubimage(i-1, 0, 1, i), i, 0, width - i * 2, i, null);
		g.drawImage(corners.getSubimage(i-1, i, 1, i), i, height - i, width - i * 2, i, null);
		g.drawImage(corners.getSubimage(0, i - 1, i, 1), 0, i, i, height - i * 2, null);
		g.drawImage(corners.getSubimage(i, i - 1, i, 1), width - i, i, i, height - i * 2, null);
		g.drawImage(corners.getSubimage(i - 1, i - 1, 2, 2), i, i, width - i * 2, height - i * 2, null);
		
		g.dispose();

		h = Tran.staticShader(h, 150, 50);
	}
	
	public BevelIndent(int bevel, int round, int width, int height, int max, int min) {

		BufferedImage corners = ImageLoader.loadImage("/textures/Gui/sign3.png");
		int i = corners.getWidth() / 2;
		h = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = h.createGraphics();

		g.setRenderingHints(Tran.antialias);

		g.drawImage(corners.getSubimage(0, 0, i, i), 0, 0, null);
		g.drawImage(corners.getSubimage(i, 0, i, i), width - i, 0, null);
		g.drawImage(corners.getSubimage(i, i, i, i), width - i, height - i, null);
		g.drawImage(corners.getSubimage(0, i, i, i), 0, height - i, null);
		g.drawImage(corners.getSubimage(i-1, 0, 1, i), i, 0, width - i * 2, i, null);
		g.drawImage(corners.getSubimage(i-1, i, 1, i), i, height - i, width - i * 2, i, null);
		g.drawImage(corners.getSubimage(0, i - 1, i, 1), 0, i, i, height - i * 2, null);
		g.drawImage(corners.getSubimage(i, i - 1, i, 1), width - i, i, i, height - i * 2, null);
		g.drawImage(corners.getSubimage(i - 1, i - 1, 2, 2), i, i, width - i * 2, height - i * 2, null);

		g.dispose();

//		h = Tran.bevelShade(h, bevel, max, min);
		h = Tran.staticShader(h, max, min);
	}
	
	public void affectImage(Color c) {
		h = Tran.effectColor(h, c);
	}

	public void render(Graphics2D g, int x, int y) {
		g.drawImage(h, x, y, null);
	}

	public BufferedImage getImage() {
		return h;
	}
	
}
