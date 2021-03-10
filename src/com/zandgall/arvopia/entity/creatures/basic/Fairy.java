package com.zandgall.arvopia.entity.creatures.basic;

import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.utils.Public;

public class Fairy extends BasicFlyTemplate {
	private static final long serialVersionUID = 1L;
	
	public Animation animation;
	
	public Fairy(Handler handler, double x, double y) {
		super(handler, x, y, 18, 18, Creature.DEFAULT_SPEED, Creature.DEFAULT_ACCELERATION, (int) (Creature.DEFAULT_SPEED+2), "Fairy", BasicTemplate.FLEER, 100, 10, 10, 2,
				50);
		
		int i = 0;
		
		if(Public.chance(20)) {
			this.hostility=BasicTemplate.PLAYERPREDATOR;
			i = 1;
		}
		
		layer = Public.random(-5, 5);
		
		bounds.x=10;
		bounds.width=16;
		bounds.y=4;
		bounds.height=10;
		
		BufferedImage img = ImageLoader.loadImage("/textures/Creatures/Fairy.png");
		Assets assets = new Assets(img, 18, 18, "Fairy");
		
		animation = new Animation(150, new BufferedImage[] {assets.get(0, i), assets.get(1, i), assets.get(2, i), assets.get(1, i)}, "Animation", "Fairy");
		
	}
	
	public Fairy(Handler handler, double x, double y, boolean hostile) {
		super(handler, x, y, 18, 18, Creature.DEFAULT_SPEED, Creature.DEFAULT_ACCELERATION, (int) (Creature.DEFAULT_SPEED+2), "Fairy", BasicTemplate.FLEER, 100, 10, 10, 2,
				50);
		
		int i = hostile ? 1:0;
		
		layer = Public.random(-5, 5);
		
		bounds.x=10;
		bounds.width=16;
		bounds.y=4;
		bounds.height=10;
		
		BufferedImage img = ImageLoader.loadImage("/textures/Creatures/Fairy.png");
		Assets assets = new Assets(img, 18, 18, "Fairy");
		
		animation = new Animation(150, new BufferedImage[] {assets.get(0, i), assets.get(1, i), assets.get(2, i), assets.get(1, i)}, "Animation", "Fairy");
		
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
