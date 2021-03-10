package com.zandgall.arvopia.entity.creatures.basic;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;

public class Skunk extends BasicTemplate {

	private static final long serialVersionUID = 1L;
	
	Animation bearWalk, bearSit, bearStill;
	BufferedImage jump;
	
	public Skunk(Handler handler, double x, double y) {
		super(handler, x, y, 54, 36, Creature.DEFAULT_SPEED-0.2, Creature.DEFAULT_ACCELERATION*0.8, 3, "Skunk",
				BasicTemplate.FLEER, 200, 20, 20, 3.0, 60);

		layer = Math.random();
		bounds.x = 18;
		bounds.y = 0;
		bounds.width = 18;
		bounds.height = 35;
		BufferedImage img=ImageLoader.loadImage("/textures/Creatures/Skunk.png");
		Assets bear = new Assets(img, 54, 36, "Skunk");
		
		bearWalk = new Animation(250, new BufferedImage[] {bear.get(2, 0),bear.get(3, 0)}, "Walk", "Skunk");
		bearStill = new Animation(750, new BufferedImage[] {bear.get(0, 0),bear.get(1, 0)}, "Still", "Skunk");
		bearSit = new Animation(750, new BufferedImage[] {bear.get(0, 1),bear.get(1, 1)}, "Sit", "Skunk");
		jump = bear.get(2, 1);
		
	}

	@Override
	public void custTick() {
		bearWalk.tick();
		bearSit.tick();
		bearStill.tick();
	}

	public BufferedImage getFrame() {
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
