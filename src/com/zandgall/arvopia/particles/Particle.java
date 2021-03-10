package com.zandgall.arvopia.particles;

import java.awt.Graphics2D;

import com.zandgall.arvopia.Handler;

public abstract class Particle {

	double x, y;

	int id;

	protected Handler game;

	boolean dead = false;

	public Particle(Handler game, double x, double y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;

		this.game = game;
	}

	public abstract void tick();

	public abstract void render(Graphics2D g);

}
