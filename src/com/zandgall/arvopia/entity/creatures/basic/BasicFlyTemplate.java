package com.zandgall.arvopia.entity.creatures.basic;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.utils.Noise;

public abstract class BasicFlyTemplate extends BasicTemplate {

	private static final long serialVersionUID = 1202001019398455L;

	private double v, h;

	Noise n;
	double noiseIndex = 0;

	public BasicFlyTemplate(Handler handler, double x, double y, int width, int height, double speed,
			double acceleration, int maxSpeed, String name, int hostility, int range, int health, int maxHealth,
			double strength, double attackspeed) {
		super(handler, x, y, width, height, speed, acceleration, maxSpeed, name, hostility, range, health, maxHealth,
				strength, attackspeed);
		this.flies = true;
		n = new Noise((long) (Math.random() * 10000));
	}

	public void aiMove() {

		// Set velocity based on directions
		setxMove(0.0F);
		yMove = 0.0F;

		if (xVol > 0.0D) {
			setxMove((float) (getxMove() + (speed + xVol)));
		}
		if (xVol < 0.0D) {
			setxMove((float) (getxMove() + (-speed + xVol)));
		}

		if (yVol > 0.0D) {
			yMove = ((float) (yMove + (speed + yVol)));
		}
		if (yVol < 0.0D) {
			yMove = ((float) (yMove + (-speed + yVol)));
		}
		
		xMove = (float) (xVol*speed*6);
		yMove = (float) (yVol*speed*6);

		xVol = h;
		yVol = v;
		widthFlip = (h > 0 ? 1 : -1);

		v = n.get1(noiseIndex);
		h = n.get1(-noiseIndex);

		noiseIndex += 0.01;

		if (fleeing != null) {
			double[] bs = flyFollow(fleeing, this);

			h = -bs[0]*0.25;
			v = -bs[1]*0.25;
			
		} else if (following != null) {
			double[] bs = flyFollow(following, this);
			
			h = bs[0]*0.25;
			v = bs[1]*0.25;
		}

	}
}
