package com.zandgall.arvopia.environment.weather;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.utils.Public;

import java.awt.*;
import java.util.ArrayList;

public class Snow {
	public ArrayList<SnowFlake> snowFlakes;
	public int amount;

	public Snow(Handler game, int amount) {
		snowFlakes = new ArrayList<>();
		this.amount = amount;
		for (int i = 0; i < amount; i++) {
			snowFlakes.add(new SnowFlake(game, Public.expandedRand(-360.0D, 1080.0D), Public.expandedRand(-400.0D, 0.0D),
					Public.expandedRand(0.1D, 5.0D)));
		}
	}

	public void tick(int speed) {
		for (SnowFlake s : snowFlakes) {
			s.tick(speed);
		}
	}

	public void melt() {
		if (Math.random() < 0.1D) {
			snowFlakes.remove(snowFlakes.size() - 1);
		}
	}

	public void add(Handler game) {
		if (Math.random() < 0.1D) {
			snowFlakes.add(new SnowFlake(game, Public.expandedRand(-360.0D, 1080.0D), Public.expandedRand(-400.0D, 0.0D),
					Public.expandedRand(0.1D, 5.0D)));
		}
	}

	public void render(Graphics2D g) {
		for (SnowFlake s : snowFlakes) {
			s.render(g);
		}
	}
}
