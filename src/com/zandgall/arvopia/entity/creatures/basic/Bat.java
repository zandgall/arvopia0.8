package com.zandgall.arvopia.entity.creatures.basic;

import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.utils.Public;

public class Bat extends BasicFlyTemplate {
	private static final long serialVersionUID = 1L;
	
	public Animation animation;
	
	public Bat(Handler handler, double x, double y) {
		super(handler, x, y, 36, 18, Creature.DEFAULT_SPEED, Creature.DEFAULT_ACCELERATION, (int) (Creature.DEFAULT_SPEED+2), "Bat", BasicTemplate.PASSIVE, 200, 10, 10, 2,
				50);
		
		layer = Public.random(-5, 5);
		
		bounds.x=10;
		bounds.width=16;
		bounds.y=4;
		bounds.height=10;
		
		BufferedImage img = ImageLoader.loadImage("/textures/Creatures/Bat.png");
		Assets assets = new Assets(img, 36, 18, "Bat");
		
		animation = new Animation(150, new BufferedImage[] {assets.get(0, 0), assets.get(1, 0), assets.get(2, 0), assets.get(1, 0)}, "Animation", "Bat");
		
	}

	@Override
	public void custTick() {
		animation.tick();
	}

	@Override
	public BufferedImage getFrame() {
		return animation.getFrame();
	}
	
}
