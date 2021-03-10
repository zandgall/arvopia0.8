package com.zandgall.arvopia.entity.creatures.basic;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.Public;
public class Wolf extends BasicTemplate {
	private static final long serialVersionUID = 1L;
	
	Animation wolfWalk, wolfSit, wolfStill;
	BufferedImage jump;

//	public static long 

	public Wolf(Handler handler, double x, double y) {
		super(handler, x, y, 72, 54, Creature.DEFAULT_SPEED - 0.2, Creature.DEFAULT_ACCELERATION * 0.8, 3, "Wolf",
				BasicTemplate.HUNTER, 200, 20, 20, 3.0, 60);

		layer = Math.random();
		bounds.x = 18;
		bounds.y = 0;
		bounds.width = 36;
		bounds.height = 53;
		BufferedImage img = ImageLoader.loadImage("/textures/Creatures/Wolf.png");
		double ratio = Public.debugRandom(0.8, 1.0);
		Color effect = new Color((int) (ratio * 255), (int) (ratio * 255), (int) (ratio * 255));
		img = Tran.effectColor(img, effect);
		Assets wolf = new Assets(img, 72, 54, "wolf");

		wolfWalk = new Animation(250, new BufferedImage[] { wolf.get(2, 0), wolf.get(3, 0) }, "Walk", "Wolf");
		wolfStill = new Animation(750, new BufferedImage[] { wolf.get(0, 0), wolf.get(1, 0) }, "Still", "Wolf");
		wolfSit = new Animation(750, new BufferedImage[] { wolf.get(0, 1), wolf.get(1, 1) }, "Sit", "Wolf");
		jump = wolf.get(2, 1);

	}

	@Override
	public void custTick() {
		wolfWalk.tick();
		wolfSit.tick();
		wolfStill.tick();
	}

	public BufferedImage getFrame() {
//		if (Math.round(yMove - speed + 0.49D) > 0L)
//			return jump; 
		if (!bottoms)
			return jump;
		if (custState)
			return wolfSit.getFrame();
		if (Math.round(Math.abs(getxMove()) - speed + 0.49D) > 0L) {
			return wolfWalk.getFrame();
		}
		return wolfStill.getFrame();
	}

	public boolean mapable() {
		return true;
	}

	public Color mapColor() {
		return Color.pink;
	}

	public Point mapSize() {
		return new Point(6, 3);
	}

}
