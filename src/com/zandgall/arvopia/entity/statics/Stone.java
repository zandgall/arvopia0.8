package com.zandgall.arvopia.entity.statics;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.utils.Public;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Stone extends StaticEntity {
	private static final long serialVersionUID = 1L;
	
	public int type;
	private int orType;
	int widthflip = 1;
	
	BufferedImage outline, outline1, outline2, outline3, outline4;

	public Stone(Handler handler, double x, double y) {
		super(handler, x-9, y-18, 18, 18, false, 20, PlayerItem.PICKAXE);
		this.type = Public.randInt(3);
		this.maxHealth = 5 * type + 5;
		this.health = 5 * type + 5;
		orType = type;

		if (Math.random() < 0.5D) {
			widthflip = -1;
		}

		if (type == 0) {
			layer = Public.expandedRand(-0.5D, 0.0D);
			bounds.x = 2;
			bounds.y = 8;
			bounds.width = 15;
			bounds.height = 10;
		} else if (type == 1) {
			layer = Public.expandedRand(-1.0D, 0.0D);
			bounds.x = 2;
			bounds.y = 4;
			bounds.width = 15;
			bounds.height = 14;
		} else {
			layer = Public.expandedRand(-4.0D, 0.0D);
			bounds.x = 2;
			bounds.width = 15;
			bounds.y = 3;
			bounds.height = 15;
		}

		outline = defaultLightEffect(Tran.flip(PublicAssets.stone[type], widthflip, 1));

		outline1 = Tran.effectAlpha(outline, 200);
		outline2 = Tran.effectAlpha(outline, 150);
		outline3 = Tran.effectAlpha(outline, 100);
		outline4 = Tran.effectAlpha(outline, 50);

	}
	public Stone(Handler handler, double x, double y, int type) {
		super(handler, x, y, 18, 18, false, type * 5+5, PlayerItem.PICKAXE);
		this.type = type;
		orType = type;

		if (Math.random() < 0.5D) {
			widthflip = -1;
		}

		if (type == 0) {
			layer = Public.expandedRand(-0.5D, 0.0D);
			bounds.x = 2;
			bounds.y = 8;
			bounds.width = 15;
			bounds.height = 10;
		} else if (type == 1) {
			layer = Public.expandedRand(-1.0D, 0.0D);
			bounds.x = 2;
			bounds.y = 4;
			bounds.width = 15;
			bounds.height = 14;
		} else {
			layer = Public.expandedRand(-4.0D, 0.0D);
			bounds.x = 2;
			bounds.width = 15;
			bounds.y = 3;
			bounds.height = 15;
		}

		outline = defaultLightEffect(Tran.flip(PublicAssets.stone[type], widthflip, 1));

		outline1 = Tran.effectAlpha(outline, 200);
		outline2 = Tran.effectAlpha(outline, 150);
		outline3 = Tran.effectAlpha(outline, 100);
		outline4 = Tran.effectAlpha(outline, 50);

	}

	public boolean stopsSnow() {
		return false;
	}

	public int getType() {
		return type;
	}

	public int getOrType() {
		return orType;
	}

	public void setType(int type) {
		this.type = type;

		if (type == 0) {
			layer = Public.expandedRand(0.0D, 1.0D);
			bounds.x = 2;
			bounds.y = 8;
			bounds.width = 15;
			bounds.height = 10;
		} else if (type == 1) {
			layer = Public.expandedRand(-1.0D, 1.0D);
			bounds.x = 3;
			bounds.y = 4;
			bounds.width = 12;
			bounds.height = 16;
		} else {
			layer = Public.expandedRand(-1.0D, 0.0D);
			bounds.x = 3;
			bounds.width = 15;
			bounds.y = 3;
			bounds.height = 15;
		}
	}

	public void tick() {
	}

	public void render(Graphics2D g) {
		AffineTransform p = g.getTransform();
		g.translate(x, y);
		g.drawImage(Tran.flip(PublicAssets.stone[type], widthflip, 1),
				0, 0, width, height, null);
		g.setTransform(p);
	}
	
	public void renderLight(Graphics2D g, float opacity) {
		AffineTransform pre = g.getTransform();
		Composite pre_comp = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g.translate(x, y);
		g.drawImage(outline, -1, -1, null);
		g.setTransform(pre);
		g.setComposite(pre_comp);
	}

	public String toString() {
		return "Stone " + x + " " + y + " " + layer + " " + type;
	}

}
