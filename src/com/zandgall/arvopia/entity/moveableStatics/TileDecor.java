package com.zandgall.arvopia.entity.moveableStatics;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.utils.Public;

public class TileDecor extends Entity {

	private static final long serialVersionUID = 9141671226893382340L;

	int type = 0;

	public TileDecor(Handler game, double x, double y) {
		super(game, x-9, y-18, 18, 18, false, false, false, false);
//		super(game, x, y, 18, 18, false);
		this.type = Public.randInt(6);
		ImageLoader.addRedirect("TileDecor" + type,
				ImageLoader.loadImage("/textures/Statics/Shrubbery/TileDecor.png").getSubimage(18 * type, 0, 18, 18));
		layer = Public.rand(2, 1);
	}

	public TileDecor(Handler game, double x, double y, int type) {
		super(game, x, y, 18, 18, false, false, false, false);
//		super(game, x, y, 18, 18, false);
		this.type = type;
		ImageLoader.addRedirect("TileDecor" + type,
				ImageLoader.loadImage("/textures/Statics/Shrubbery/TileDecor.png").getSubimage(18 * type, 0, 18, 18));
		layer = Public.rand(2, 1);
	}

	@Override
	public void render(Graphics2D g) {
		AffineTransform p = g.getTransform();
		g.translate(x, y);
		g.drawImage(ImageLoader.loadImage("TileDecor" + type), 0, 0, null);
		g.setTransform(p);
	}

	@Override
	public void tick() {

	}

}