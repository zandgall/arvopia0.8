package com.zandgall.arvopia.entity.statics;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.environment.weather.SnowFlake;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.SimplexNoise;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import static java.lang.Math.abs;

public class Shrubbery extends StaticEntity {
	private static final long serialVersionUID = 1L;
	
	private BufferedImage images;
	private final BufferedImage source, outline;
	public int type, xin = 0, index = 0;
	private double last_temp = -100, snow = 0;
	private int width_flip = 1;

	public Shrubbery(Handler handler, double x, double y) {
		super(handler, x-9, y-18, 18, 18, false, 0, PlayerItem.SCYTHE);
		this.type = Public.randInt(4, 6);

		if (Math.random() < 0.5D)
			width_flip = -1;

		layer = Public.expandedRand(-0.5D, -1.0D);

		source = PublicAssets.shrubbery[type];
		images = PublicAssets.shrubbery[type];

		outline = Tran.litUp(Tran.flip(images, width_flip, 1));
	}

	public Shrubbery(Handler handler, double x, double y, int type) {
		super(handler, x, y, 18, 18, false, 0, PlayerItem.SCYTHE);

//		maxSnowy = 10;

		if (Math.random() < 0.5D && type>2)
			width_flip = -1;

		layer = Public.expandedRand(-0.5D, -1.0D);

		source = PublicAssets.shrubbery[type];
		images = PublicAssets.shrubbery[type];

		this.type = type;
		
		outline = defaultLightEffect(Tran.flip(images, width_flip, 1));
	}

	public Shrubbery(Handler handler, double x, double y, int type, int xin, int index) {
		super(handler, x, y, 18, 18, false, 0, PlayerItem.SCYTHE);

		this.xin = xin;
		this.index = index;

//		maxSnowy = 10;

		if (Math.random() < 0.5D && type>2)
			width_flip = -1;

		layer = Public.expandedRand(-0.5D, -1.0D);

		source = PublicAssets.shrubbery[type];
		images = PublicAssets.shrubbery[type];

		this.type = type;
		outline = Tran.litUp(Tran.flip(images, width_flip, 1));
	}

	@Override
	public void interactWithSnowFlake(SnowFlake snowFlake) {
		snow+=0.1;
	}

	public boolean stopsSnow() {
		return true;
	}
	
	public void render(Graphics2D g) {
		double temp = game.getEnvironment().getTemp() + SimplexNoise.noise(x/18.0*0.02, y/18.0*0.02, 123456)*5;
		if(temp < 25)
			snow+=0.001;
		if(abs(last_temp - temp)>=2) {
			Color c = game.foliageColor(temp, snow);
			images = Tran.effectImage(new Tran.ShadowBrightColorEffect(new Color(50, 50, 100), c), source);
			last_temp = temp;
		}


		AffineTransform p = g.getTransform();
		g.translate(x, y);

		if (variety && type != 0 && type != 2)
			g.drawImage(Tran.flip(images, width_flip, 1), 0, 0, null);
		else
			g.drawImage(images, 0, 0, null);
		if(type==5)
			g.drawImage(Tran.flip(PublicAssets.shrubbery[12], width_flip, 1), 0, 0, null);

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
		return "Shrubbery " + x + " " + y + " " + layer + " " + type;
	}

}
