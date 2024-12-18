package com.zandgall.arvopia.entity.moveableStatics;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.tiles.Tile;

public class Platform extends MoveableStatic {
	
	private static final long serialVersionUID = 1L;

	public static final int STATIC = 0, FALLING = 1;
	
	BufferedImage texture;
	
	Entity topEntity;
	
	int type;
	
	double yOffset, ORIGINY, yMomentum;

	public Platform(Handler game, double x, double y) {
		super(game, x-9, y-9, 18, 18, true);
		texture = new BufferedImage(18, 18, BufferedImage.TYPE_4BYTE_ABGR);

		this.type = 0;

		ArrayList<ArrayList<String>> tiles = new ArrayList<>();

		ORIGINY = y;

		tiles.add(new ArrayList<>());
		tiles.get(0).add("TILE5");

		texture.getGraphics().drawImage(Tile.getTile("TILE5").getImage(), 0, 0, null);
		texture.getGraphics().dispose();
	}

	public Platform(Handler game, int x, int y, int w, int h, int type) {
		super(game, x, y, w*18, h*18, true);
		texture = new BufferedImage(w*18, h*18, BufferedImage.TYPE_4BYTE_ABGR);

		this.type = type;

		ArrayList<ArrayList<String>> tiles = new ArrayList<>();

		ORIGINY = y;

		for(int i = 0; i<w; i++) {
			tiles.add(new ArrayList<>());
			for(int j = 0; j<h; j++) {
				tiles.get(i).add("TILE5");
			}
		}
		Tile.formatTiles(tiles, 0, 0, w, h);

		for(int i = 0; i<w; i++) {
			for(int j = 0; j<h; j++) {
				texture.getGraphics().drawImage(Tile.getTile(tiles.get(i).get(j)).getImage(), i*18, j*18, null);
			}
		}
		texture.getGraphics().dispose();
	}

	@Override
	public void tick() {
		if(checkTouching(0, -2)) {
			topEntity = getTouched(0, -20);
		} else {
			topEntity = null;
		}
		double preoffset = yOffset;
		if(type==STATIC) {
		yOffset = (Math.sin(game.getGameTime()*0.005)*5);
		} else if(type == FALLING) {
			if(topEntity == null)
				yMomentum = 0;
			else yMomentum+=0.05;
			yOffset+=yMomentum;
		}
		
		y = ORIGINY + yOffset;
		
		if(topEntity!=null) {
			topEntity.setY(topEntity.getY()+(yOffset-preoffset));
			if(topEntity.creature)              
				((Creature) topEntity).forcedBottom = true;
		}
	}

	@Override
	public void init() {
		
	}

	@Override
	public void render(Graphics2D g) {
		AffineTransform p = g.getTransform();
		g.translate(x, y);
		g.drawImage(texture, 0, 0, null);
		g.setTransform(p);
	}
	
}
