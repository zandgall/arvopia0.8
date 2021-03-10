package com.zandgall.arvopia.enviornment;

import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class LightManager {
	private ArrayList<Light> light;
	public Handler game;

	public BufferedImage mask;
	public BufferedImage lightfx;

	public static long EffectingLight = 1, coloringLight = 1, maskingImage = 1;

	public LightManager(Handler game) {
		light = new ArrayList<Light>();
		light = new ArrayList<Light>();
		this.game = game;
	}

	public void tick() {
	}

	public int getMax(int x, int y) {

		x = x * game.getEnviornment().lightQuality;
		y = y * game.getEnviornment().lightQuality;
		
		x+=game.xOffset();
		y+=game.yOffset();

		int i = 0;
		for (Light l : light) {
			if (l.isOn()) {
				double delta = Math.max(1 - (Public.dist(x, y, l.getX(), l.getY()) / (255 * (l.getStrength() / 10))), 0);
				i += l.getMax() * delta;
			}
		}
//		i/=light.size();
		return i;
	}

	public Color getColor(int x, int y) {

		Color c = new Color(0, 0, 50);
		
//		x+=game.xOffset();
//		y+=game.yOffset();
		
		String build = "Lights: ";
		
		x = x * game.getEnviornment().lightQuality;
		y = y * game.getEnviornment().lightQuality;
		
		x+=game.xOffset();
		y+=game.yOffset();
		
		try {
			for (Light l : light) {
				build+="("+l.getX()+", " + l.getY()+"), ";
				if (l.isOn()) {
					double delta = (double) l.colorDistribution()-(Public.dist(x, y, l.getX(), l.getY()) / (255 * (l.getStrength() / 10)));
					delta = Math.max(delta, 0);
					c = Public.blend(c, l.color, (float) delta);
				}
			}
		} catch(ConcurrentModificationException e) {
			e.printStackTrace();
			return getColor(x, y);
		}
//		Console.log(build, x, y);
		return c;
	}

	public double getLight(int x, int y) {

		x = x * game.getEnviornment().lightQuality;
		y = y * game.getEnviornment().lightQuality;
		
		x+=game.xOffset();
		y+=game.yOffset();

		double i = 0;
		for (Light l : light) {
			if (l.isOn()) {
				i += Math.max(1 - (Public.dist(x, y, l.getX(), l.getY()) / (255 * (l.getStrength() / 10))), 0);
			}
		}

		return i;
	}

	public Light getClosest(int x, int y) {
		if(light.size()==0)
			return null;
		Light i = (Light) light.get(0);
		for (Light l : light) {
			if (l.isOn()) {
				int x1 = (int) Public.range(l.getMax(), 255.0D,
						Public.dist(x, y, l.getX(), l.getY()) / (l.getStrength() / 10));
				int x2 = (int) Public.range(i.getMax(), 255.0D,
						Public.dist(x, y, i.getX(), i.getY()) / (i.getStrength() / 10));
				if (x1 < x2) {
					i = l;
				}
			}
		}

		return i;
	}

	public void render(Graphics2D g) {
		if (mask == null) {
			mask = new BufferedImage(World.getWidth() * 18, World.getHeight() * 18, BufferedImage.TYPE_4BYTE_ABGR);
			lightfx = new BufferedImage(World.getWidth() * 18, World.getHeight() * 18, BufferedImage.TYPE_4BYTE_ABGR);
			lightfx.getGraphics().setColor(new Color(0, 0, 50));
			lightfx.getGraphics().fillRect(0, 0, lightfx.getWidth(), lightfx.getHeight());
		}

		for (Light l : light) {
			if (l.isOn())
				l.render(g);
		}
	}

	public void addLight(Light light) {
		this.light.add(light);
		game.log("Light: " + light + " Added");
	}
	
	@SuppressWarnings("unchecked")
	public void updateLights() {
		
	}

	public ArrayList<Light> getList() {
		return light;
	}
}
