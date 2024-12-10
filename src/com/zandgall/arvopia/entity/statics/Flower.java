package com.zandgall.arvopia.entity.statics;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.Item;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.utils.Public;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Flower extends StaticEntity {
	private static final long serialVersionUID = -5001443636584775793L;
	private final int type, randomSpeed, widthflip;

	private final Animation outline;

	public Flower(Handler handler, double x, double y) {
		super(handler, x-9, y-18, 18, 18, false, 10, PlayerItem.SCYTHE);
		this.type = Public.randInt(3);

		this.layer = Public.rand(-1, 1);

		if (Math.random() < 0.5D)
			widthflip = -1;
		else widthflip = 1;

		randomSpeed = ((int) Public.expandedRand(-100.0D, 100.0D));

		bounds.x = -4;
		bounds.width = 7;
		bounds.y = 5;
		bounds.height = 13;

		BufferedImage pre0 = Tran.litUp(Tran.flip(PublicAssets.flower[type*3+1], widthflip, 1));
		BufferedImage pre1 = Tran.litUp(Tran.flip(PublicAssets.flower[type*3+1], widthflip, 1));
		BufferedImage pre2 = Tran.litUp(Tran.flip(PublicAssets.flower[type*3+2], widthflip, 1));

		outline = new Animation(400, new BufferedImage[] {pre0, pre1, pre2, pre1}, "", "");

	}

	public Flower(Handler handler, double x, double y, int type, double layer) {
		super(handler, x, y, 18, 18, false, 10, PlayerItem.SCYTHE);
		this.type = type;

		this.layer = layer;

		if (Math.random() < 0.5D)
			widthflip = -1;
		else widthflip = 1;

		randomSpeed = ((int) Public.expandedRand(-100.0D, 100.0D));

		bounds.x = 5;
		bounds.width = 7;
		bounds.y = 5;
		bounds.height = 13;
		
		BufferedImage pre0 = Tran.litUp(Tran.flip(PublicAssets.flower[type*3+1], widthflip, 1));
		BufferedImage pre1 = Tran.litUp(Tran.flip(PublicAssets.flower[type*3+1], widthflip, 1));
		BufferedImage pre2 = Tran.litUp(Tran.flip(PublicAssets.flower[type*3+2], widthflip, 1));

		outline = new Animation(400, new BufferedImage[] {pre0, pre1, pre2, pre1}, "", "");
		
	}

	public void tick() {
		PublicAssets.flowerFinal[type].setResetTime((int) Math.abs((400 + randomSpeed) / game.getWind(x, y)));

		PublicAssets.flowerFinal[type].tick();
		
		outline.frameInt=PublicAssets.flowerFinal[type].frameInt;

//		if (snowy > 0)
//			game.getWorld().kill(this);
	}

	public void render(Graphics2D g) {
		AffineTransform p = g.getTransform();
		g.translate(x, y);

		if (variety)
			g.drawImage(Tran.flip(PublicAssets.flowerFinal[type].getFrame(), widthflip, 1), 0,
					0, width, height, null);
		else
			g.drawImage(PublicAssets.flowerFinal[type].getFrame(), 0, 0,
					width, height, null);

		g.setTransform(p);
	}
	
	public void renderLight(Graphics2D g, float opacity) {
		AffineTransform pre = g.getTransform();
		Composite pre_comp = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g.translate(x, y);
		g.drawImage(outline.getFrame(), -1, -1, null);
		g.setTransform(pre);
		g.setComposite(pre_comp);
	}

	public int getType() {
		return type;
	}

	public String toString() {
		return "Flower " + x + " " + y + " " + layer + " " + type;
	}
	
	public void kill() {
		int items = (int) Math.ceil(Math.random() * 3.0D);
		for (int i = 0; i < items; i++) {
			if(type == 0)
				game.getWorld().getItemManager()
						.addItem(Item.whitePetal.createNew((int) (x+Public.expandedRand(-9, 9)), (int) (y+Public.expandedRand(-9, 9))));
			else if(type == 1)
				game.getWorld().getItemManager()
				.addItem(Item.pinkPetal.createNew((int) (x+Public.expandedRand(-9, 9)), (int) (y+Public.expandedRand(-9, 9))));
			else if(type == 2)
				game.getWorld().getItemManager()
				.addItem(Item.bluePetal.createNew((int) (x+Public.expandedRand(-9, 9)), (int) (y+Public.expandedRand(-9, 9))));
		}
		game.getEntityManager().getEntities().remove(this);
	}

	@Override
	public boolean canCurrentlySpawn(Handler game) {
		return (game.getEnvironment().getTemp() > 32.0D) && (game.getEnvironment().getHumidity() > 2.0D);
	}
}
