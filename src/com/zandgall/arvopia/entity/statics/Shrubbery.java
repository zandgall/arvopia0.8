package com.zandgall.arvopia.entity.statics;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Shrubbery extends StaticEntity {
	private static final long serialVersionUID = 1L;
	
	public BufferedImage images;
	public BufferedImage snowi;
	public BufferedImage[] snow;
	BufferedImage outline, outline1, outline2, outline3, outline4;
	public int type;
	int widthflip = 1;

	public int xin = 0, index = 0;

	public Shrubbery(Handler handler, double x, double y, int type) {
		super(handler, x, y, 18, 18, false, 0, PlayerItem.SCYTHE);

		maxSnowy = 10;

		if (Math.random() < 0.5D && type>2) {
			widthflip = -1;
		}

		layer = Public.random(-0.5D, -1.0D);

		images = PublicAssets.shrubbery[type];
		
		if (type == 0) {
			snowi = PublicAssets.snowyGrassEntity.getSubimage(0, 0, 18, 18);
			layer = -2;
		}
		if (type == 1) {
			snowi = PublicAssets.snowyGrassEntity.getSubimage(18, 0, 18, 18);
			layer = -2;
		}
		if (type == 2) {
			snowi = PublicAssets.snowyGrassEntity.getSubimage(36, 0, 18, 18);
			layer = -2;
		}

		if ((type == 0) || (type == 1) || (type == 2)) {
			snow = new BufferedImage[10];
			snow[0] = Tran.effectAlpha(snowi, 50);
//			for (int i = 1; i < 10; i++) {
//				snow[i] = Tran.combine(snowi, snow[(i - 1)]);
//			}
		}

		this.type = type;
		
		outline = defaultLightEffect(Tran.flip(images, widthflip, 1));
		
		outline1 = Tran.effectAlpha(outline, 200);
		outline2 = Tran.effectAlpha(outline, 150);
		outline3 = Tran.effectAlpha(outline, 100);
		outline4 = Tran.effectAlpha(outline, 50);
	}

	public Shrubbery(Handler handler, double x, double y, int type, int xin, int index) {
		super(handler, x, y, 18, 18, false, 0, PlayerItem.SCYTHE);

		this.xin = xin;
		this.index = index;

		maxSnowy = 10;

		if (Math.random() < 0.5D && type>2) {
			widthflip = -1;
		}

		layer = Public.random(-0.5D, -1.0D);

		images = PublicAssets.shrubbery[type];

		if (type == 0) {
			snowi = PublicAssets.snowyGrassEntity.getSubimage(0, 0, 18, 18);
		}
		if (type == 1) {
			snowi = PublicAssets.snowyGrassEntity.getSubimage(18, 0, 18, 18);
		}
		if (type == 2) {
			snowi = PublicAssets.snowyGrassEntity.getSubimage(36, 0, 18, 18);
		}

		if ((type == 0) || (type == 1) || (type == 2)) {
			snow = new BufferedImage[10];
			snow[0] = Tran.effectAlpha(snowi, 50);
//			for (int i = 1; i < 10; i++) {
//				snow[i] = Tran.combine(snowi, snow[(i - 1)]);
//			}
		}

		this.type = type;
		
		BufferedImage bleach = Tran.flip(Tran.bleachImage(images, Color.orange), widthflip, 1);
		outline = new BufferedImage(bleach.getWidth()+2, bleach.getHeight()+2, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = outline.createGraphics();
		
		g.drawImage(bleach, 0, 0, null);
		g.drawImage(bleach, 1, 0, null);
		g.drawImage(bleach, 2, 0, null);
		
		g.drawImage(bleach, 0, 1, null);
		g.drawImage(bleach, 2, 1, null);
		
		g.drawImage(bleach, 0, 2, null);
		g.drawImage(bleach, 1, 2, null);
		g.drawImage(bleach, 2, 2, null);
		
		g.dispose();
		
		outline1 = Tran.effectAlpha(outline, 200);
		outline2 = Tran.effectAlpha(outline, 150);
		outline3 = Tran.effectAlpha(outline, 100);
		outline4 = Tran.effectAlpha(outline, 50);
		
	}

	public boolean stopsSnow() {
		return true;
	}
	
	public void render(Graphics2D g) {

		if ((variety) && (type != 0) && (type != 2))
			g.drawImage(Tran.flip(images, widthflip, 1), (int) (x) - (int) (game.xOffset()), (int) (y - game.yOffset()), null);
		else {
			g.drawImage(images, (int) (x - game.xOffset()), (int) (y - game.yOffset()), null);
		}
		if ((snowy > 0) && ((type == 0) || (type == 1) || (type == 2))) {
			Tile t = World.getTile((int) ((centerX())/18), (int) (centerY()/18)+1);
			int grassnow = t.snowyness((int) ((centerX())/18), (int) (centerY()/18)+1);
			t.setSnowy((int) ((centerX())/18), (int) (centerY()/18)+1, Math.max(snowy, grassnow));
			if ((variety) && (type != 0) && (type != 2)) {
				BufferedImage img = Tran.flip(snow[0], widthflip, 1);
				for (int i = 0; i < snowy; i++)
					g.drawImage(img, (int) (x - game.xOffset()), (int) (y - game.yOffset()), null);
			} else {
				for (int i = 0; i < snowy; i++)
					g.drawImage(snow[0], (int) (x - game.xOffset()), (int) (y - game.yOffset()), null);
			}
		}
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
		return "Shrubbery " + x + " " + y + " " + layer + " " + type;
	}

}
