package com.zandgall.arvopia.entity.statics;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.tiles.Tile;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Soil extends StaticEntity {
	private static final long serialVersionUID = 1L;
	
	public BufferedImage texture;

	public Soil(Handler game, int x, int y, int id) {
		super(game, x * Tile.TILEWIDTH, y * Tile.TILEHEIGHT, 18, 18, false, 5, PlayerItem.SHOVEL); 
		texture = ImageLoader.loadImage("/textures/Statics/Soil.png").getSubimage(id * 18, 0, 18, 18);
	}

	public void tick() {
	}

	public void render(Graphics2D g) {
		g.drawImage(texture, (int) (x - game.xOffset()), (int) (y - game.yOffset()), null);
	}
}
