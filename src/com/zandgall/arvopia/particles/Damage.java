package com.zandgall.arvopia.particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.Public;

public class Damage extends Particle {

	double value;

	double xMove, yMove;

	int transparancy = 300;

	public Damage(Handler game, double x, double y, double value) {
		super(game, x, y, 0);

		xMove = Public.expandedRand(0, 1);
		yMove = -1;

		this.value = Public.grid(value, 0.1, 0);

	}

	@Override
	public void tick() {
		yMove += 0.1;
		y += yMove;
		x += xMove;

		transparancy -= 10;

		if (transparancy < 0) {
			dead = true;
		}
	}

	@Override
	public void render(Graphics2D g) {
		AffineTransform p = g.getTransform();
		g.translate(x, y);

		g.setFont(Public.digital);
		if (value >= 0)
			Tran.drawOutlinedText(g, 0, 0, "" + value, 1.0,
					new Color(255, 255, 255, (int) Public.range(0, 255, transparancy)),
					new Color(0, 255, 0, (int) Public.range(0, 255, transparancy)));
		else
			Tran.drawOutlinedText(g, 0, 0, "" + value, 1.0,
					new Color(0, 0, 0, (int) Public.range(0, 255, transparancy)),
					new Color(255, 0, 0, (int) Public.range(0, 255, transparancy)));
		g.setTransform(p);
	}

}
