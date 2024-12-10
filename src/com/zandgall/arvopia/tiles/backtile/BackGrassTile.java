package com.zandgall.arvopia.tiles.backtile;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.tiles.GrassTile;

public class BackGrassTile extends Backtile {

	public static final Assets assets = new Assets(ImageLoader.loadImage("/textures/Tiles/DirtBacktile.png"), 18, 18, "Dirt Backtile");
	
	int gx, gy;
	
	public BackGrassTile(BufferedImage image, String id) {
		super(image, id);
	}

	public static void loadAllBackGrassTiles() {
		Assets sheet = new Assets(ImageLoader.loadImage("/textures/Tiles/DirtBacktile.png"), 18, 18, "dirtbacktileset");
		Assets kernelSheet = new Assets(ImageLoader.loadImage("/textures/Tiles/tile_reference.png"), 3, 3, "tile_reference");
		for(int i = 0; i < 16; i++) {
			for(int j = 0; j < 16; j++) {
				BackGrassTile g = new BackGrassTile(sheet.get(i, j), "BACKGRASS"+(i*16+j)+"");
				BufferedImage k = kernelSheet.get(i,j);
				g.setValidityKernel(
						k.getRGB(0, 0), k.getRGB(1, 0), k.getRGB(2, 0),
						k.getRGB(0, 1), Color.GREEN.getRGB(), k.getRGB(2, 1),
						k.getRGB(0, 2), k.getRGB(1, 2), k.getRGB(2, 2));
				g.gx = i;
				g.gy = j;
			}
		}
	}
	
	public int gridX() {
		return gx;
	}
	
	public int gridY() {
		return gy;
	}

	@Override
	public void tick(Handler paramHandler, int paramInt1, int paramInt2) {

	}

	@Override
	public void init() {
		
	}

	@Override
	public void reset() {
		
	}

	@Override
	public Color getColor() {
		return null;
	} 
	
}
