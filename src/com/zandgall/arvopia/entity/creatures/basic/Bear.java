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

public class Bear extends BasicTemplate {
	private static final long serialVersionUID = 1L;
	
	Animation bearWalk, bearSit, bearStill;
	BufferedImage jump;
	
	public static final Color[] types = new Color[] {new Color(255, 255, 255), new Color(150, 200, 0)};


	public Bear(Handler handler, double x, double y) {
		super(handler, x, y-72, 108, 72, Creature.DEFAULT_SPEED-0.2, Creature.DEFAULT_ACCELERATION*0.8, 3, "Bear",
				BasicTemplate.PREDATOR, 200, 20, 20, 3.0, 60);

		layer = Math.random();
		bounds.x = 18;
		bounds.y = 0;
		bounds.width = 72;
		bounds.height = 71;
		BufferedImage img=ImageLoader.loadImage("/textures/Creatures/Bear.png");
		double ratio = Public.rand(0.8, 1.0);
		int i = (int) Public.expandedRand(0, types.length-1);
		Color effect = new Color((int) (ratio*types[i].getRed()), (int) (ratio*types[i].getGreen()), (int) (ratio*types[i].getBlue()));
		img = Tran.effectColor(img, effect);
		Assets bear = new Assets(img, 108, 72, "Bear");
		
		bearWalk = new Animation(250, new BufferedImage[] {bear.get(2, 0),bear.get(3, 0)}, "Walk", "Bear");
		bearStill = new Animation(750, new BufferedImage[] {bear.get(0, 0),bear.get(1, 0)}, "Still", "Bear");
		bearSit = new Animation(750, new BufferedImage[] {bear.get(0, 1),bear.get(1, 1)}, "Sit", "Bear");
		jump = bear.get(2, 1);
		
	}

	@Override
	public void custTick() {
		bearWalk.tick();
		bearSit.tick();
		bearStill.tick();
	}

	public BufferedImage getFrame() {
//		if (Math.round(yMove - speed + 0.49D) > 0L)
//			return jump; 
		if (!bottoms)
			return jump;
		if (custState)
			return bearSit.getFrame();
		if (Math.round(Math.abs(getxMove()) - speed + 0.49D) > 0L) {
			return bearWalk.getFrame();
		}
		return bearStill.getFrame();
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
