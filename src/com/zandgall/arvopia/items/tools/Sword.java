package com.zandgall.arvopia.items.tools;

import com.zandgall.arvopia.Handler;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Sword extends Tool {
	private BufferedImage texture;
	private BufferedImage stab;

	public Sword(Handler game) {
		super(game, "Sword", false, 3.0D, 15, 15);

		texture = com.zandgall.arvopia.gfx.PublicAssets.sword;
		stab = com.zandgall.arvopia.gfx.PublicAssets.swordStab;
	}

	public void tick() {
	}

	public void custom1(int x, int y) {
	}

	public BufferedImage texture() {
		return texture;
	}

	public BufferedImage getFrame() {
		return stab;
	}

	public void render(Graphics g, int x, int y) {
	}

	public int getYOffset() {
		return 8;
	}

	public int getXOffset() {
		return texture.getWidth() / 2;
	}

	public boolean smashOrStab() {
		return false;
	}

	public void setFrame(int frameInt) {
	}

	public Tool.tools Type() {
		return Tool.tools.NONE;
	}

	public void custom2(int i) {
	}
}
