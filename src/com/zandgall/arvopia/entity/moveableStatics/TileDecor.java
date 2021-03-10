package com.zandgall.arvopia.entity.moveableStatics;

import java.awt.Graphics;
import java.awt.Graphics2D;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Initiator;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;

public class TileDecor extends MoveableStatic {

	private static final long serialVersionUID = 9141671226893382340L;

	int type = 0;

	public TileDecor(Handler game, double x, double y, int type) {
		super(game, x, y, 18, 18, false);
		this.type = type;
		ImageLoader.addRedirect("TileDecor" + type,
				ImageLoader.loadImage("/textures/Statics/Shrubbery/TileDecor.png").getSubimage(18 * type, 0, 18, 18));
		layer = Public.debugRandom(2, 1);
	}

	@Override
	public void render(Graphics2D g) {
		g.drawImage(ImageLoader.loadImage("TileDecor" + type), (int) Public.xO(x), (int) Public.yO(y), null);
	}

	@Override
	public void tick() {

	}

	@Override
	public void init() {

	}

}