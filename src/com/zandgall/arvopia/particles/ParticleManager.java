package com.zandgall.arvopia.particles;

import java.awt.Graphics2D;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;

public class ParticleManager {

	ArrayList<Particle> particles;

	Handler game;

	public ParticleManager(Handler game) {
		particles = new ArrayList<Particle>();
		this.game = game;
	};

	public void tick() {
		for (Particle p : particles) {
			if (p.dead) {
				p = null;
				continue;
			}
			if (p != null)
				p.tick();
		}
	}

	public void render(Graphics2D g) {
		for (Particle p : particles) {
			if (p.x > game.xOffset() && p.y > game.yOffset() && p.x < game.xOffset() + game.getWidth()
					&& p.y < game.yOffset() + game.getHeight())
				if (p != null)
					p.render(g);
		}
	}

	public void add(Particle p) {
		particles.add(p);
	}

}
