package com.zandgall.arvopia.items.tools;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public abstract class Tool {
	protected Handler game;
	double damage;
	int delay;
	int range;
	BufferedImage thumbnail;

	public static enum tools {
		AXE, PICK, SCYTHE, TILLER, SHOVEL, NONE;
	}

	public Tool(Handler game, BufferedImage thumbnail, boolean weapon, double damage, int delay, int range) {
		this.game = game;
		this.damage = damage;
		this.delay = delay;
		this.range = range;
		this.thumbnail = thumbnail;
	}

	public int getDelay() {
		return delay;
	}

	public int getRange() {
		return range;
	}

	public boolean front() {
		return false;
	}

	public Tool(Handler game, String name, boolean weapon, double damage, int delay, int range) {
		this.game = game;
		this.damage = damage;
		this.damage = damage;
		this.delay = delay;
		this.range = range;
		thumbnail = getImage(name, "Thumbnail");
	}

	public BufferedImage getThumbnail() {
		return thumbnail;
	}

	public double getDamage() {
		return damage;
	}

	public abstract tools Type();

	public abstract void tick();

	public abstract void render(Graphics paramGraphics, int paramInt1, int paramInt2);

	public abstract void custom1(int paramInt1, int paramInt2);

	public abstract void custom2(int paramInt);

	public BufferedImage texture() {
		return null;
	}

	public abstract BufferedImage getFrame();

	public abstract int getYOffset();

	public abstract int getXOffset();

	public boolean hasLight() {
		return false;
	}

	public com.zandgall.arvopia.environment.Light getLight() {
		return null;
	}

	public abstract boolean smashOrStab();

	public abstract void setFrame(int paramInt);

	public BufferedImage getImage(String name, String variation) {
		return ImageLoader.loadImage("/textures/Inventory/Tools/" + name + "/" + name + variation + ".png");
	}
}
