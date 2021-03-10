package com.zandgall.arvopia.items.tools;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.SpriteSheet;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Axe extends Tool {
	BufferedImage texture;
	Animation smash;
	private int xOff = 3;

	public Axe(Handler game) {
		super(game, "Axe", true, 4.0D, 15, 20);
		texture = ImageLoader.loadImage("/textures/Inventory/Tools/Axe/Axe.png");
		SpriteSheet s = new SpriteSheet(ImageLoader.loadImage("/textures/Inventory/Tools/Axe/AxeSmash.png"));
		smash = new Animation(1000, new BufferedImage[] { s.get(0, 0, 13, 36), s.get(14, 0, 30, 36),
				s.get(0, 36, 36, 13), s.get(0, 49, 36, 30) }, "Smash", "Axe");
	}

	public void tick() {
	}

	public void render(Graphics g, int x, int y) {
	}

	public void custom1(int x, int y) {
	}

	public BufferedImage texture() {
		return texture;
	}

	public BufferedImage getFrame() {
		return smash.getFrame();
	}

	public int getYOffset() {
		return 8;
	}

	public int getXOffset() {
		return xOff;
	}

	public boolean smashOrStab() {
		return true;
	}

	public void setFrame(int frameInt) {
		smash.setFrame(frameInt);
	}

	public Tool.tools Type() {
		return Tool.tools.AXE;
	}

	public void custom2(int i) {
		if (i < 0)
			xOff = 10;
		else {
			xOff = 3;
		}
	}
}
