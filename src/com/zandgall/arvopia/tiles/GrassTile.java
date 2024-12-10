package com.zandgall.arvopia.tiles;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.SimplexNoise;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.*;

public class GrassTile extends Tile {
	public BufferedImage grass;
	public GrassTile(BufferedImage image, BufferedImage grass) {
		super(image);
		this.grass = grass;
		organic = true;
		solid = true;
	}

	public static void loadAllGrassTiles() {
		Assets sheet = new Assets(ImageLoader.loadImage("/textures/Tiles/fulldirttileset.png"), 18, 18, "fullgrasstileset");
		Assets grass_sheet = new Assets(ImageLoader.loadImage("/textures/Tiles/grass.png"), 18, 18, "grasstileset");
		Assets kernelSheet = new Assets(ImageLoader.loadImage("/textures/Tiles/fulldirtconnectionkernel.png"), 3, 3, "fulldirtconnectionkernel");
		for(int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				// ID = "TILE" + (2 + j + i * 16)
				GrassTile g = new GrassTile(sheet.get(i, j), grass_sheet.get(i, j));
				BufferedImage k = kernelSheet.get(i, j);
				g.setValidityKernel(
						k.getRGB(0, 0), k.getRGB(1, 0), k.getRGB(2, 0),
						k.getRGB(0, 1), Color.YELLOW.getRGB(), k.getRGB(2, 1),
						k.getRGB(0, 2), k.getRGB(1, 2), k.getRGB(2, 2));
				if (k.getRGB(1, 1) == Color.magenta.getRGB())
					g.solid = false;
			}
		}
	}

	public void tick(Handler handler, int x, int y) {
		double temp = handler.getEnvironment().getTemp() + SimplexNoise.noise(x*0.02, y*0.02, 123456)*5;
		if(temp < 25)
			updateSnowy(x, y, 0.01);
	}

	@Override
	public void render(Handler handler, Graphics2D g, int x, int y) {
		super.render(handler, g, x, y);
		double temp = handler.getEnvironment().getTemp() + SimplexNoise.noise(x*0.02, y*0.02, 123456)*5;
		double snow = min(snowiness(x, y)*0.1, 1);
		Color c = handler.foliageColor(temp, snow);
		g.drawImage(Tran.effectImage(new Tran.ShadowBrightColorEffect(new Color(50, 50, 100), c), grass), x*18, y*18, null);
	}

	public Color getColor() {
		if (validityKernel[1][0].equals(VALID_STATES.ANYTHING))
			return Color.green;
		return new Color(50, 25, 0);
	}

}
