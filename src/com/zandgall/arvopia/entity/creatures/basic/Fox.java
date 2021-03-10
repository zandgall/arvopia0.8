package com.zandgall.arvopia.entity.creatures.basic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.Item;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.utils.Public;

public class Fox extends BasicTemplate {
	
	private static final long serialVersionUID = 1L;

	private static BufferedImage body = null, body2, tail;

	Animation foxWalk, foxSit, foxStill;

	String yip;

	double timer = 200, timed = 0;

	public Fox(Handler handler, double x, double y) {
		super(handler, x, y, 54, 36, Creature.DEFAULT_SPEED, Creature.DEFAULT_ACCELERATION, 3, "Fox",
				BasicTemplate.PASSIVE, 200, 10, 10, 3.0, 60);

		layer = Math.random();
		bounds.x = 18;
		bounds.y = 19;
		bounds.width = 26;
		bounds.height = 16;

		foxWalk = new Animation(250, new BufferedImage[] { PublicAssets.fox[0],
				PublicAssets.fox[1] }, "Walk", "Fox");;
		foxStill = Creature.foxStill;
		foxSit = Creature.foxSit;

		if(body==null) {
			body = ImageLoader.loadImage("/textures/Creatures/Fox.png").getSubimage(0, 72, 54, 36);
			body2 = ImageLoader.loadImage("/textures/Creatures/Fox.png").getSubimage(54, 72, 54, 36);
			tail = ImageLoader.loadImage("/textures/Creatures/Fox.png").getSubimage(108, 72, 36, 36);
		}

		String addition = "" + (((OptionState) game.getGame().optionState).getToggle("Sound per layer") ? Public.random(0, 2): "");
		yip = "Yip" + addition;
		game.addSound("Sounds/FoxBark.ogg", yip, false, 0, 0, 0);
	}
	
	@Override
	public void custTick() {
		foxWalk.setSpeed(xMove/8.0);
		foxWalk.tick();
		foxSit.tick();
		foxStill.tick();

		// TODO play yip sound

		if (timed > timer) {
			game.setPosition(yip, (int) x, (int) y, 0);
			game.soundSystem.stop(yip);
			if (game.soundSystem.getVolume(yip) > 0)
				game.play(yip);
			timed = 0;
		}

		timed+=Math.random();

	}

	@Override
	public void render(Graphics2D g) {
//		g.drawImage(com.zandgall.arvopia.gfx.transform.Tran.flip(getFrame(), widthFlip, 1),
//				(int) (x - game.getGameCamera().getxOffset()), (int) (y - game.getGameCamera().getyOffset()), null);
		if  (!(custState || (Math.round(yMove - speed + 0.49D) > 0L) || !bottoms)) {
			g.drawImage(Tran.flip(System.currentTimeMillis() % 2000 < 1200 ? body : body2, widthFlip, 1), (int) Public.xO(x), (int) Public.yO(y), null);
		}
		g.drawImage(Tran.flip(getFrame(), widthFlip, 1), (int) Public.xO(x), (int) Public.yO(y), null);
		AffineTransform pre = g.getTransform();
		double xo = Public.xO(x + (widthFlip==1 ? 40 : 14) + (custState ? widthFlip*-5 : (Math.round(yMove - speed + 0.49D) > 0L) ? widthFlip*-2 : 0));
		double yo = Public.yO(y + (custState ? 32 : (Math.round(yMove - speed + 0.49D) > 0L) ? 14 : 22));
		g.rotate(Math.sin(System.currentTimeMillis() / 500.0 + layer * 10) * 0.4 + widthFlip*0.4 + (custState ? widthFlip * 0.6 : 0), (int) xo, (int) yo);
		g.drawImage(Tran.flip(tail, widthFlip, 1), (int) xo-18, (int) yo-18, null);
		g.setTransform(pre);
		if (health < MAX_HEALTH) {
				showHealthBar(g);
			}
	}

	public BufferedImage getFrame() {
		if (Math.round(yMove - speed + 0.49D) > 0L)
			return com.zandgall.arvopia.gfx.PublicAssets.fox[5];
		if (!bottoms)
			return com.zandgall.arvopia.gfx.PublicAssets.fox[4];
		if (custState)
			return foxSit.getFrame();
		if (Math.round(Math.abs(getxMove()) - speed + 0.49D) > 0L) {
			return PublicAssets.fox[((int)(x/28.0))%2];
		}
		return PublicAssets.fox[6];
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

	public void kill() {
		
		game.soundSystem.stop(yip);
		game.removeSound("Sounds/FoxBark.ogg", yip);
		
		if (game.getPlayer().targets.contains(this))
			game.getWorld().getItemManager().addItem(Item.foxFur.createNew((int) x, (int) y));

		game.getWorld().getEntityManager().getEntities().remove(this);
	}

}
