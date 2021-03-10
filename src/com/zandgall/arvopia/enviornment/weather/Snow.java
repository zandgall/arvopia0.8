package com.zandgall.arvopia.enviornment.weather;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.utils.Public;
import java.awt.Graphics;
import java.util.ArrayList;

public class Snow {
	public static double wind = 0.0D;
	public ArrayList<SnowFlake> snowFlakes;
	public int ammount;

	public Snow(Handler game, int ammount) {
		snowFlakes = new ArrayList<SnowFlake>();
		this.ammount = ammount;
		for (int i = 0; i < ammount; i++) {
			snowFlakes.add(new SnowFlake(game, Public.random(-360.0D, 1080.0D), Public.random(-400.0D, 0.0D),
					Public.random(0.1D, 5.0D)));
		}
	}

	public void tick(int speed) {
		for (SnowFlake s : snowFlakes) {
			s.tick(wind, speed);
		}
	}

	public void melt() {
		if (Math.random() < 0.1D) {
			snowFlakes.remove(snowFlakes.size() - 1);
		}
	}

	public void add(Handler game) {
		if (Math.random() < 0.1D) {
			snowFlakes.add(new SnowFlake(game, Public.random(-360.0D, 1080.0D), Public.random(-400.0D, 0.0D),
					Public.random(0.1D, 5.0D)));
		}
	}

	public void render(Graphics g) {
		for (SnowFlake s : snowFlakes) {
			s.render(g);
		}
	}
}
