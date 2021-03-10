package com.zandgall.arvopia.entity.statics;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.Item;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.utils.Public;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Flower extends StaticEntity {
	private static final long serialVersionUID = -5001443636584775793L;
	public int type;
	public int randomSpeed;
	public int widthflip = 1;
	double deathOffset;
	
	Animation outline, outline1, outline2, outline3, outline4;

	public Flower(Handler handler, double x, double y, int type, double layer) {
		super(handler, x, y, 18, 18, false, 1, PlayerItem.SCYTHE);
		this.type = type;

		this.layer = layer;

		if (Math.random() < 0.5D) {
			widthflip = -1;
		}

		randomSpeed = ((int) Public.random(-100.0D, 100.0D));

		deathOffset = Public.random(-10.0D, 0.0D);

		bounds.x = -4;
		bounds.width = 7;
		bounds.y = 5;
		bounds.height = 13;
		
		BufferedImage pre0 = defaultLightEffect(Tran.flip(PublicAssets.flower[type*3+1], widthflip, 1));
		BufferedImage pre1 = defaultLightEffect(Tran.flip(PublicAssets.flower[type*3+1], widthflip, 1));
		BufferedImage pre2 = defaultLightEffect(Tran.flip(PublicAssets.flower[type*3+2], widthflip, 1));
		
		BufferedImage p0 = pre0;
		BufferedImage p1 = pre1;
		BufferedImage p2 = pre2;
		outline = new Animation(400, new BufferedImage[] {p0, p1, p2, p1}, "", "");
		p0 = Tran.effectAlpha(pre0, 200);
		p1 = Tran.effectAlpha(pre1, 200);
		p2 = Tran.effectAlpha(pre2, 200);
		outline1 = new Animation(400, new BufferedImage[] {p0, p1, p2, p1}, "", "");
		p0 = Tran.effectAlpha(pre0, 150);
		p1 = Tran.effectAlpha(pre1, 150);
		p2 = Tran.effectAlpha(pre2, 150);
		outline2 = new Animation(400, new BufferedImage[] {p0, p1, p2, p1}, "", "");
		p0 = Tran.effectAlpha(pre0, 100);
		p1 = Tran.effectAlpha(pre1, 100);
		p2 = Tran.effectAlpha(pre2, 100);
		outline3 = new Animation(400, new BufferedImage[] {p0, p1, p2, p1}, "", "");
		p0 = Tran.effectAlpha(pre0, 50);
		p1 = Tran.effectAlpha(pre1, 50);
		p2 = Tran.effectAlpha(pre2, 50);
		outline4 = new Animation(400, new BufferedImage[] {p0, p1, p2, p1}, "", "");
		
//		outline1 = Tran.effectAlpha(outline, 200);
//		outline2 = Tran.effectAlpha(outline, 150);
//		outline3 = Tran.effectAlpha(outline, 100);
//		outline4 = Tran.effectAlpha(outline, 50);
		
	}

	public void tick() {
		PublicAssets.flowerFinal[type].setResetTime((int) Math.abs((400 + randomSpeed) / game.getWind()));

		PublicAssets.flowerFinal[type].tick();
		
		outline.frameInt=PublicAssets.flowerFinal[type].frameInt;
		outline1.frameInt=PublicAssets.flowerFinal[type].frameInt;
		outline2.frameInt=PublicAssets.flowerFinal[type].frameInt;
		outline3.frameInt=PublicAssets.flowerFinal[type].frameInt;
		outline4.frameInt=PublicAssets.flowerFinal[type].frameInt;

		if (snowy > 0) {
			game.getWorld().kill(this);
		}
	}

	public void render(Graphics2D g) {
		if (variety)
			g.drawImage(Tran.flip(PublicAssets.flowerFinal[type].getFrame(), widthflip, 1), (int) (Public.xO(x) - 9.0D),
					(int) (Public.yO(y)), width, height, null);
		else {
			g.drawImage(PublicAssets.flowerFinal[type].getFrame(), (int) (Public.xO(x) - 9.0D), (int) (Public.yO(y)),
					width, height, null);
		}
	}
	
	public void renderLight(Graphics2D g, int opacity) {
		if(opacity>200)
			g.drawImage(outline.getFrame(), (int) (Public.xO(x-10)), (int) (Public.yO(y-1)), null);
		else if(opacity>150)
			g.drawImage(outline1.getFrame(), (int) (Public.xO(x-10)), (int) (Public.yO(y-1)), null);
		else if(opacity>100)
			g.drawImage(outline2.getFrame(), (int) (Public.xO(x-10)), (int) (Public.yO(y-1)), null);
		else if(opacity>50)
			g.drawImage(outline3.getFrame(), (int) (Public.xO(x-10)), (int) (Public.yO(y-1)), null);
		else if(opacity>0)
			g.drawImage(outline4.getFrame(), (int) (Public.xO(x-10)), (int) (Public.yO(y-1)), null);
	}

	public int getType() {
		return type;
	}

	public String outString() {
		return "Flower " + x + " " + y + " " + layer + " " + type;
	}
	
	public void kill() {
		int items = (int) Math.ceil(Math.random() * 3.0D);
		for (int i = 0; i < items; i++) {
			if(type == 0)
				game.getWorld().getItemManager()
						.addItem(Item.whitePetal.createNew((int) (x+Public.random(-9, 9)), (int) (y+Public.random(-9, 9))));
			else if(type == 1)
				game.getWorld().getItemManager()
				.addItem(Item.pinkPetal.createNew((int) (x+Public.random(-9, 9)), (int) (y+Public.random(-9, 9))));
			else if(type == 2)
				game.getWorld().getItemManager()
				.addItem(Item.bluePetal.createNew((int) (x+Public.random(-9, 9)), (int) (y+Public.random(-9, 9))));
		}
		game.getEntityManager().getEntities().remove(this);
	}

}
