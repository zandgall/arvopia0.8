package com.zandgall.arvopia.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.EntityEntry;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.*;

public class GenerateWorldState extends State {

	private ArrayList<Slider> sliders;
	private ArrayList<BevelPlatform> platforms;

	private final Slider width, height, foliage, stones, insects, creatures, cannibals, caves;
	private final Button confirm, back, newSeed;
	private final BevelPlatform optionBackground;

	private double pan_x, pan_y, preview_zoom = 1;
	private double scroll = 0;
	private long seed;

	private final Font f = new Font("Arial", Font.BOLD, 20);

	private BufferedImage preview;

	public GenerateWorldState(Handler handler) {
		super(handler);

		back = new Button(handler, 5, 5, "Goes back without generating world", "Back");
		confirm = new Button(handler, back.getWidth()+15, 5, "Confirms your settings","Confirm");
		newSeed = new Button(handler, back.getWidth()+confirm.getWidth()+25, 5,
				"Don't like the current generation? Re-roll a new seed!", "Re-Roll");
		optionBackground = new BevelPlatform(2, 2, back.getWidth()+confirm.getWidth()+newSeed.getWidth()+70, 90);

		width = new Slider(handler, 40, 1000, 200, false, "Width");
		height = new Slider(handler, 40, 1000, 100, false, "Height");
		foliage = new Slider(handler, 0, 1, 0.5, false, "Foliage");
		stones = new Slider(handler, 0, 1, 0.5, false, "Stones");
		insects = new Slider(handler, 0, 1, 0.5, false, "Insects");
		creatures = new Slider(handler, 0, 1, 0.5, false, "Creatures");
		cannibals = new Slider(handler, 0, 1, 0.5, false, "Cannibals");
		caves = new Slider(handler, 0, 5, 1, false, "Caves");

		init();
		makePreviewImage();
	}

	@Override
	public void tick() {
		if(handler.getMouse().rMouseX()<125)
			scroll += handler.getMouse().getMouseScroll() * 15;
		scroll = Public.range(0, sliders.size() * 80 + 50 - handler.getHeight(), scroll);

		if(!confirm.hovered && !back.hovered)
			for (Slider s : sliders)
				s.tick(10, (int) (sliders.indexOf(s) * 80 + 100 - scroll));

		confirm.tick();
		if (confirm.on) {
			setState(handler.getGame().gameState);
			handler.getMouse().resets();
			handler.getGame().gameState.generateWorld(seed, sliders);
			return;
		}
		back.tick();
		if (back.on)
			setState(getPrev());

		newSeed.tick();
		if(newSeed.on)
			seed = System.nanoTime();

		if(handler.getMouse().rMouseX()>=125 && handler.getMouse().rMouseY()>=50) {
			preview_zoom -= preview_zoom * handler.getMouse().getMouseScroll() * 0.1;
			if(handler.getMouse().fullLeft||handler.getMouse().isLeft()) {
				pan_x += (handler.getMouse().rMouseX() - handler.getMouse().rPMouseX())/preview_zoom;
				pan_y += (handler.getMouse().rMouseY() - handler.getMouse().rPMouseY())/preview_zoom;
			}
		}
	}

	@Override
	public void render(Graphics2D g) {
		g.setColor(new Color(120, 225, 255));
		g.fillRect(0, 0, handler.getWidth(), handler.getHeight());

		makePreviewImage();
		g.setColor(new Color(110, 215, 245));
		g.fillRect(125, 50, handler.width-125, handler.height-50);
		g.setClip(125, 50, handler.width-125, handler.height-50);
		AffineTransform pre = g.getTransform();
		g.translate(50+handler.width/2.f, 25+handler.height/2.f);
		g.scale(preview_zoom, preview_zoom);
		g.translate(pan_x, pan_y);
		g.drawImage(preview, -width.getValue()/2, -height.getValue()/2, null);
		g.setTransform(pre);
		g.setClip(0, 0, handler.width, handler.height);

		g.setFont(f.deriveFont(16.f));
		Tran.drawOutlinedText(g, 270.0, 16.0, "*Preview doesn't necessarily show\naccurate entity spawn locations",
				1, Color.black, Color.white);

		for (int i = 0; i < sliders.size(); i++) {
			sliders.get(i).render(g);
			platforms.get(i).render(g, 5,
					(int) (i * 80 + 50 - scroll));
			Tran.TEXT_MODE = Tran.TEXT_MODE_TOP;
			g.setFont(f);
			g.setColor(Color.black);
			Tran.drawString(g, sliders.get(i).getName(), 13,
					(int) (i * 80 + 53 - scroll));
			Tran.TEXT_MODE = 0;
		}
		optionBackground.render(g, -38, -43);
		back.render(g);
		confirm.render(g);
		newSeed.render(g);
	}

	@Override
	public void init() {
		if(seed==0)
			seed = System.nanoTime();
		sliders = new ArrayList<>();
		platforms = new ArrayList<>();
		
		int i = 16;

		sliders.add(width);
		platforms.add(new BevelPlatform(2, 2, Tran.measureString(width.getName(), f).x+i,
				Tran.measureString(width.getName(), f).y+i));
		sliders.add(height);
		platforms.add(new BevelPlatform(2, 2, Tran.measureString(height.getName(), f).x+i,
				Tran.measureString(height.getName(), f).y+i));
		sliders.add(foliage);
		platforms.add(new BevelPlatform(2, 2, Tran.measureString(foliage.getName(), f).x+i,
				Tran.measureString(foliage.getName(), f).y+i));
		platforms.get(2).setImage(Tran.effectColor(platforms.get(2).h(), new Color(60, 220, 80)));
		sliders.add(stones);
		platforms.add(new BevelPlatform(2, 2, Tran.measureString(stones.getName(), f).x+i,
				Tran.measureString(stones.getName(), f).y+i));
		platforms.get(3).setImage(Tran.effectColor(platforms.get(3).h(), Color.lightGray));
		sliders.add(insects);
		platforms.add(new BevelPlatform(2, 2, Tran.measureString(insects.getName(), f).x+i,
				Tran.measureString(insects.getName(), f).y+i));
		platforms.get(4).setImage(Tran.effectColor(platforms.get(4).h(), new Color(255, 200, 180)));
		sliders.add(creatures);
		platforms.add(new BevelPlatform(2, 2, Tran.measureString(creatures.getName(), f).x+i,
				Tran.measureString(creatures.getName(), f).y+i));
		platforms.get(5).setImage(Tran.effectColor(platforms.get(5).h(), new Color(255, 100, 255)));
		sliders.add(cannibals);
		platforms.add(new BevelPlatform(2, 2, Tran.measureString(cannibals.getName(), f).x+i,
				Tran.measureString(cannibals.getName(), f).y+i));
		platforms.get(6).setImage(Tran.effectColor(platforms.get(6).h(), new Color(200, 20, 80)));
		sliders.add(caves);
		platforms.add(new BevelPlatform(2, 2, Tran.measureString(caves.getName(), f).x+i,
				Tran.measureString(caves.getName(), f).y+i));

		for (EntityEntry a : handler.getAdders()) {
			sliders.add(new Slider(handler, 0, 1, 0.5, false, a.name));
			platforms.add(new BevelPlatform(2, 2, Tran.measureString(a.name, f).x+i, Tran.measureString(a.name, f).y+i));
		}
	}

	@Override
	public void renderGUI(Graphics2D g2d) {
		
	}

	public void makePreviewImage() {
		preview = new BufferedImage(width.getValue(), height.getValue(), BufferedImage.TYPE_4BYTE_ABGR);
		Random rand = new Random(seed);
		Noise.init(seed);
		rand.nextInt(); // Cloud 0
		rand.nextInt(); // Cloud 1
		rand.nextInt(); // Cloud 2
		rand.nextInt(); // Cloud 3
		rand.nextInt(); // Cloud Y

		int bh = height.getValue() * 18;
		double c = rand.nextDouble(-1, 1);
		double y = (Noise.noise1(c) * 10 + (bh / 72.f));
		double speed = 5;
		int[][] caveTiles = new int[width.getValue()][height.getValue()];
		// CAVES
		// Done beforehand, both so that preview image in GenerateWorldState is easier to create,
		// But also so that caves are more consistent per world, rather than randomized if the width of a world is
		// changed, and thus rand.next gets more calls before caves are generated
		for (int i = 0; i < this.caves.getValue(); i++) {
			Noise n = new Noise(rand.nextLong());

			double radius = 0;
			double momentum = rand.nextDouble(1000);
			double xc = rand.nextDouble(width.getValue());
			double yc = 0;
			double r = 5;
			while (yc < height.getValue() - r - 5) {
				xc = Public.range(0, width.getValue(), xc);
				xc += n.get1(momentum) * 2 * speed;
				yc = Math.max(yc, 0);
				yc += (n.get1(-momentum) + 0.1) * 0.4 * speed;

				r = Public.range(1.75, 5.0, n.get1(radius)*5.0+5.0);
				int cx = (int) Public.range(0, width.getValue() - 1, xc);
				int cy = (int) Public.range(0, height.getValue() - 1, yc);
				for (double px = xc - r+1; px < xc + r-2; px++) { // +1 and -2 to shorten loop time at the cost of accuracy
					for (double py = yc - r+1; py < yc + r-2; py++) {
						int tx = (int) Public.range(0, width.getValue() - 1, px);
						int ty = (int) Public.range(0, height.getValue() - 1, py);

						if ((tx-cx)*(tx-cx)+(ty-cy)*(ty-cy) <= r*r)
							caveTiles[tx][ty] = 1;
					}
				}

				momentum += 0.0025 * speed;
				radius += 0.0025 * speed;
			}
		}

		int biome = 1;
		double offset = 0;
		Graphics2D g = preview.createGraphics();
		Color sky = new Color(109, 151, 238);
		g.setColor(sky);
		g.fillRect(0, 0, width.getValue(), height.getValue());
		int brown = new Color(70, 40, 10).getRGB();
		int darkBrown = new Color(35, 20, 5).getRGB();
		int midBrown = new Color(55, 30, 8).getRGB();
		int leafGreen = new Color(60, 220, 80).getRGB();
		for(int i = 0; i < width.getValue(); i++) {
			y = Public.range(0, height.getValue(), y);
			if(caveTiles[i][(int) y]==0)
				preview.setRGB(i, (int) y, Color.green.getRGB());

			for (int j = (int) y + 1; j < height.getValue(); j++)
				if(caveTiles[i][j]==0)
					preview.setRGB(i, j, brown);
				else preview.setRGB(i, j, darkBrown);

			c += 0.02;
			y = (Noise.noise1(c) * 10 * biome + (bh / 72.f)) + offset;

			if(rand.nextDouble() < 0.01) {
				biome = rand.nextInt(1, 6);
				double original = (y - (bh / 72.f));
				double newer = (Noise.noise1(c) * 20 * biome);
				offset = original - newer;
			}
		}

		ArrayList<ArrayList<Integer>> heights = new ArrayList<>();
		for(int i = 0; i < width.getValue(); i++) {
			heights.add(new ArrayList<>());
			for(int j = 0; j < height.getValue(); j++)
				if(preview.getRGB(i, j)==Color.green.getRGB() || (preview.getRGB(i, j) != darkBrown&&j > 0 && preview.getRGB(i, j-1) == darkBrown))
					heights.get(i).add(j);
		}
		rand = new Random(seed*100);
		for(int i = 0; i < stones.getWholeValue()*width.getValue()*0.3; i++) {
			int tile_x;
			do {
				tile_x = rand.nextInt(width.getValue());
			} while(heights.get(tile_x).size()==0);
			int tile_y = heights.get(tile_x).get(rand.nextInt(heights.get(tile_x).size())) - 1;
			if(tile_y<0)
				continue;
			preview.setRGB(tile_x, tile_y, Color.gray.getRGB());
		}
		rand = new Random(seed*100+1);
		for(int i = 0; i < foliage.getWholeValue()*width.getValue()*0.3; i++) {
			int tile_x;
			do {
				tile_x = rand.nextInt(width.getValue());
			} while(heights.get(tile_x).size()==0);
			int tile_y = heights.get(tile_x).get(rand.nextInt(heights.get(tile_x).size())) - 1;
			if(tile_y<0)
				continue;
			preview.setRGB(tile_x, tile_y, Color.yellow.getRGB());
		}
		rand = new Random(seed*100+2);
		for(int i = 0; i < foliage.getWholeValue()*width.getValue()*0.3; i++) {
			int tile_x;
			do {
				tile_x = rand.nextInt(width.getValue());
			} while(heights.get(tile_x).size()==0);
			int tile_y = heights.get(tile_x).get(rand.nextInt(heights.get(tile_x).size())) - 1;
			if(tile_y<0)
				continue;
			preview.setRGB(tile_x, tile_y, midBrown);
			if(tile_y>=1)
				preview.setRGB(tile_x, tile_y-1, midBrown);
			if(tile_y>=2)
				preview.setRGB(tile_x, tile_y-2, midBrown);
			if(tile_y>=3)
				preview.setRGB(tile_x, tile_y-3, leafGreen);
			if(tile_y>=2&&tile_x>=1)
				preview.setRGB(tile_x-1, tile_y-2, leafGreen);
			if(tile_y>=2&&tile_x<width.getValue()-1)
				preview.setRGB(tile_x+1, tile_y-2, leafGreen);
		}
		rand = new Random(seed*100+3);
		for(int i = 0; i < foliage.getWholeValue()*width.getValue()*0.3; i++) {
			int tile_x;
			do {
				tile_x = rand.nextInt(width.getValue());
			} while(heights.get(tile_x).size()==0);
			int tile_y = heights.get(tile_x).get(rand.nextInt(heights.get(tile_x).size())) - 1;
			if(tile_y<0)
				continue;
			preview.setRGB(tile_x, tile_y, leafGreen);
		}
		rand = new Random(seed*100+4);
		for(int i = 0; i < insects.getWholeValue()*width.getValue()*0.3; i++) {
			int tile_x;
			int tile_y;
			do {
				tile_x = rand.nextInt(width.getValue());
				tile_y = rand.nextInt(height.getValue());
			} while (preview.getRGB(tile_x, tile_y)!=sky.getRGB()&&preview.getRGB(tile_x, tile_y)!=darkBrown);
			preview.setRGB(tile_x, tile_y, 0xFFFFA090);
		}
		rand = new Random(seed*100+5);
		for(int i = 0; i < creatures.getWholeValue()*width.getValue()*0.5; i++) {
			int tile_x;
			do {
				tile_x = rand.nextInt(width.getValue());
			} while(heights.get(tile_x).size()==0);
			int tile_y = heights.get(tile_x).get(rand.nextInt(heights.get(tile_x).size())) - 1;
			if(tile_y<0)
				continue;
			preview.setRGB(tile_x, tile_y, Color.magenta.getRGB());
		}
		rand = new Random(seed*100+6);
		for(int i = 0; i < cannibals.getWholeValue()*width.getValue()*0.1; i++) {
			int tile_x;
			do {
				tile_x = rand.nextInt(width.getValue());
			} while(heights.get(tile_x).size()==0);
			int tile_y = heights.get(tile_x).get(rand.nextInt(heights.get(tile_x).size())) - 1;
			if(tile_y<0)
				continue;
			preview.setRGB(tile_x, tile_y, 0xFFA0<<16);
			if(tile_y<1)
				continue;
			preview.setRGB(tile_x, tile_y-1, 0xFFA0<<16);
		}
	}

}