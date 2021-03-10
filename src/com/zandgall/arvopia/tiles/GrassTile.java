package com.zandgall.arvopia.tiles;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GrassTile extends Tile {
	private static Assets grassTileset = new Assets(ImageLoader.loadImage("/textures/Tiles/DirtTileset.png"), 18, 18,
			"GrassTile");
	private static BufferedImage grassImageset = ImageLoader.loadImage("/textures/Tiles/DirtTileset.png");
	private static BufferedImage shadowTileset = ImageLoader.loadImage("/textures/Tiles/DirtTilesetShadowMap.png");
	private static BufferedImage snowSet = ImageLoader.loadImage("/textures/Tiles/SnowyDirtTileset.png");
	BufferedImage grass;
	BufferedImage snow;
	BufferedImage shadow;
	BufferedImage[] snowOverlay = new BufferedImage[6];
	ArrayList<Integer> offset;
	ArrayList<Integer> timeTakes;
	ArrayList<Integer> timer;
	ArrayList<Integer> xpos;
	ArrayList<Integer> ypos;
	Map<Integer, Map<Integer, Integer>> posFinder = new HashMap<Integer, Map<Integer, Integer>>();
	int gx;
	int gy;
	Handler game;

	public GrassTile(int id, int x, int y) {
		super(grassTileset.get(x, y), id);

		gx = x;
		gy = y;

		grass = grassTileset.get(x, y);
		snow = snowSet.getSubimage(x * 18, y * 18, 18, 18);
		shadow = shadowTileset.getSubimage(x * 18, y * 18, 18, 18);

		offset = new ArrayList<Integer>();
		timeTakes = new ArrayList<Integer>();
		timer = new ArrayList<Integer>();
		xpos = new ArrayList<Integer>();
		ypos = new ArrayList<Integer>();

//		snowOverlay[0] = snow;
		for (int i = 0; i < 6; i++) {
			snowOverlay[i] = Tran.effectAlpha(snow, (int) Math.min((i+1)*255.0/5.0, 255.0));
		}
	}
	
	public GrassTile(int x, int y) {
		super(grassImageset.getSubimage(x*18, y*18, 18, 18));

		gx = x;
		gy = y;

		grass = grassImageset.getSubimage(x*18, y*18, 18, 18);
		snow = snowSet.getSubimage(x * 18, y * 18, 18, 18);
		shadow = shadowTileset.getSubimage(x * 18, y * 18, 18, 18);

		offset = new ArrayList<Integer>();
		timeTakes = new ArrayList<Integer>();
		timer = new ArrayList<Integer>();
		xpos = new ArrayList<Integer>();
		ypos = new ArrayList<Integer>();

//		snowOverlay[0] = snow;
		for (int i = 0; i < 6; i++) {
			snowOverlay[i] = Tran.effectAlpha(snow, (int) Math.min((i+1)*255.0/5.0, 255.0));
		}
	}

	public int gridX() {
		return gx;
	}

	public int gridY() {
		return gy;
	}
	
	public int getY(int xOff) {
		if(gx==7&&gy==0)
			return xOff;
		if(gx==7&&gy==2)
			return 18-xOff;
		return 0;
	}

	public boolean isSolid() {
		if ((id == "TILE"+19) || (id == "TILE"+20) || (id == "TILE"+23))
			return false;
		return true;
	}

	public void init() {
	}

	public void tick(Handler game, int x, int y) {
		this.game = game;
		if ((!posFinder.containsKey(Integer.valueOf(x)))
				|| (!((Map<?, ?>) posFinder.get(Integer.valueOf(x))).containsKey(Integer.valueOf(y)))) {
			if (posFinder.containsKey(Integer.valueOf(x))) {
				if (!((Map<?, ?>) posFinder.get(Integer.valueOf(x))).containsKey(Integer.valueOf(y))) {
					((Map<Integer, Integer>) posFinder.get(Integer.valueOf(x))).put(Integer.valueOf(y), Integer.valueOf(xpos.size()));
				}
			} else {
				posFinder.put(Integer.valueOf(x), new HashMap<Integer, Integer>()); 
				((Map<Integer, Integer>) posFinder.get(Integer.valueOf(x))).put(Integer.valueOf(y), Integer.valueOf(xpos.size()));
			}

			xpos.add(Integer.valueOf(x));
			ypos.add(Integer.valueOf(y));
			offset.add(Integer.valueOf((int) Public.random(0.0D, 10.0D)));
			timeTakes.add(Integer.valueOf((int) Public.random(0.0D, 20.0D)));
			timer.add(Integer.valueOf(0));
		}

		int off = ((Integer) ((Map<?, ?>) posFinder.get(Integer.valueOf(x))).get(Integer.valueOf(y))).intValue();

		if (game.getWorld().getEnviornment().getTemp() - 32.0D > ((Integer) offset.get(off)).intValue()) {
			if (((Integer) timer.get(off)).intValue() >= ((Integer) timeTakes.get(off)).intValue()) {
				snowy[x][y] -= 1;
				timer.set(off, Integer.valueOf(0));
			} else {
				timer.set(off, Integer.valueOf(((Integer) timer.get(off)).intValue() + 1));
			}
		} else {
			timer.set(off, Integer.valueOf(0));
		}
	}
	
	public boolean supportsShrubbery() {
		return true;
	}

	public boolean tickable() {
		return true;
	}

	public void render(Graphics2D g, int x, int y, int gridx, int gridy) {
//		g.drawImage(grass, x, y, null);
		if (snowy.length < World.getWidth() || snowy[0].length < World.getHeight())
			set(World.getWidth(), World.getHeight());

		if (snowy[gridx][gridy] > 0)
			g.drawImage(snowOverlay[((int) Public.range(0.0D, 5.9D, snowy[gridx][gridy] - 1))], x, y, null);
	}
	
	public boolean customRender() {
		return false;
	}

	public Image getImage() {
		return grass;
	}

	public Image getSnowy() {
		return snowOverlay[0];
	}

	public void reset() {
//		grassTileset.reset(ImageLoader.loadImage("/textures/DirtTileset.png"), 18, 18, "GrassTile");
	}

	public Color getColor() {
		if ((id == "TILE"+1) || (id == "TILE"+2) || (id == "TILE"+3) || (id == "TILE"+10) || (id == "TILE"+11) || (id == "TILE"+12) || (id == "TILE"+13) || (id == "TILE"+16))
			return Color.green;
		return new Color(50, 25, 0);
	}

	public BufferedImage shadowMap() {
		return shadow;
	}

}
