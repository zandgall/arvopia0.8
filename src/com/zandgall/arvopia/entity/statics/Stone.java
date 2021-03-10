package com.zandgall.arvopia.entity.statics;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Asset;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.utils.Public;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Stone extends StaticEntity {
	private static final long serialVersionUID = 1L;
	
	public int type;
	private int orType;
	int widthflip = 1;
	
	BufferedImage outline, outline1, outline2, outline3, outline4;

	public Stone(Handler handler, double x, double y, int type) {
		super(handler, x, y, 18, 18, false, type * 5, PlayerItem.PICKAXE);
		this.type = type;
		orType = type;

		if (Math.random() < 0.5D) {
			widthflip = -1;
		}

		if (type == 0) {
			layer = Public.random(-0.5D, 0.0D);
			bounds.x = 2;
			bounds.y = 8;
			bounds.width = 15;
			bounds.height = 10;
		} else if (type == 1) {
			layer = Public.random(-1.0D, 0.0D);
			bounds.x = 2;
			bounds.y = 4;
			bounds.width = 15;
			bounds.height = 14;
		} else {
			layer = Public.random(-4.0D, 0.0D);
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
			layer = Public.random(0.0D, 1.0D);
			bounds.x = 2;
			bounds.y = 8;
			bounds.width = 15;
			bounds.height = 10;
		} else if (type == 1) {
			layer = Public.random(-1.0D, 1.0D);
			bounds.x = 3;
			bounds.y = 4;
			bounds.width = 12;
			bounds.height = 16;
		} else {
			layer = Public.random(-1.0D, 0.0D);
			bounds.x = 3;
			bounds.width = 15;
			bounds.y = 3;
			bounds.height = 15;
		}
	}

	public void tick() {
	}

	public void render(Graphics2D g) {
			g.drawImage(Tran.flip(com.zandgall.arvopia.gfx.PublicAssets.stone[type], widthflip, 1),
					(int) (x - game.getGameCamera().getxOffset()), (int) (y - game.getGameCamera().getyOffset()), width,
					height, null);
	}
	
	public void renderLight(Graphics2D g, int opacity) {
		if(opacity>200)
			g.drawImage(outline, (int) (Public.xO(x-1)), (int) (Public.yO(y-1)), null);
		else if(opacity>150)
			g.drawImage(outline1, (int) (Public.xO(x-1)), (int) (Public.yO(y-1)), null);
		else if(opacity>100)
			g.drawImage(outline2, (int) (Public.xO(x-1)), (int) (Public.yO(y-1)), null);
		else if(opacity>50)
			g.drawImage(outline3, (int) (Public.xO(x-1)), (int) (Public.yO(y-1)), null);
		else if(opacity>0)
			g.drawImage(outline4, (int) (Public.xO(x-1)), (int) (Public.yO(y-1)), null);
	}

	public String outString() {
		return "Stone " + x + " " + y + " " + layer + " " + type;
	}

}
