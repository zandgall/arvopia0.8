package com.zandgall.arvopia.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.EntityAdder;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.BevelPlatform;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Slider;

public class GenerateWorldState extends State {

	ArrayList<Slider> sliders;
	ArrayList<BevelPlatform> platforms;

	Slider width, height, foliage, stones, insects, creatures, cannibals, caves;
	Button confirm, back;

	double scroll = 0;

	Font f = new Font("Arial", Font.BOLD, 20);

	public GenerateWorldState(Handler handler) {
		super(handler);

		back = new Button(handler, 10, handler.getHeight() - 35, "Goes back without generating world", "Back");
		confirm = new Button(handler, 20 + back.getWidth(), handler.getHeight() - 35, "Confirms your settings",
				"Confirm");

		width = new Slider(handler, 40, 1000, 200, false, "Width");
		height = new Slider(handler, 40, 1000, 100, false, "Height");
		foliage = new Slider(handler, 0, 1, 0.5, false, "Foliage");
		stones = new Slider(handler, 0, 1, 0.5, false, "Stones");
		insects = new Slider(handler, 0, 1, 0.5, false, "Insects");
		creatures = new Slider(handler, 0, 1, 0.5, false, "Creatures");
		cannibals = new Slider(handler, 0, 1, 0.5, false, "Cannibals");
		caves = new Slider(handler, 0, 5, 1, false, "Caves");

		init();
	}

	@Override
	public void tick() {
		confirm.tick();
		if (confirm.on) {
			setState(handler.getGame().gameState);
			handler.getMouse().setClicked(false);
			((GameState) handler.getGame().gameState).generateWorld(sliders);
			return;
		}
		back.tick();
		if (back.on)
			setState(getPrev());

//		width.tick(10, 10);
//		height.tick(10, 50);
//		foliage.tick(10, 90);
//		stones.tick(10, 130);
//		insects.tick(10, 170);
//		creatures.tick(10, 210);
//		cannibals.tick(10, 250);
//		caves.tick(10, 290);

		for (Slider s : sliders) {
			s.tick(handler.getWidth() / 2 + 10, (int) (sliders.indexOf(s) * 40 + 10 - scroll));
		}

		scroll += handler.getMouse().getMouseScroll() * 15;

		scroll = Public.range(0, sliders.size() * 40 + 10 - handler.getHeight(), scroll);

	}

	@Override
	public void render(Graphics2D g) {
		g.setColor(new Color(120, 225, 255));
		g.fillRect(0, 0, handler.getWidth(), handler.getHeight());

		confirm.render(g);
		back.render(g);

		for (int i = 0; i < sliders.size(); i++) {
			sliders.get(i).render(g);
			platforms.get(i).render(g, handler.getWidth() / 2 - 10 - platforms.get(i).width(),
					(int) (i * 40 + 5 - scroll));
			Tran.TEXT_MODE = 1;
			g.setFont(f);
			g.setColor(Color.black);
			Tran.drawString(g, sliders.get(i).getName(), handler.getWidth() / 2 - 3 - platforms.get(i).width(),
					(int) (i * 40 + 7 - scroll));
			Tran.TEXT_MODE = 0;
		}
	}

	@Override
	public void init() {
		sliders = new ArrayList<Slider>();
		platforms = new ArrayList<BevelPlatform>();
		
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
		sliders.add(stones);
		platforms.add(new BevelPlatform(2, 2, Tran.measureString(stones.getName(), f).x+i,
				Tran.measureString(stones.getName(), f).y+i));
		sliders.add(insects);
		platforms.add(new BevelPlatform(2, 2, Tran.measureString(insects.getName(), f).x+i,
				Tran.measureString(insects.getName(), f).y+i));
		sliders.add(creatures);
		platforms.add(new BevelPlatform(2, 2, Tran.measureString(creatures.getName(), f).x+i,
				Tran.measureString(creatures.getName(), f).y+i));
		sliders.add(cannibals);
		platforms.add(new BevelPlatform(2, 2, Tran.measureString(cannibals.getName(), f).x+i,
				Tran.measureString(cannibals.getName(), f).y+i));
		sliders.add(caves);
		platforms.add(new BevelPlatform(2, 2, Tran.measureString(caves.getName(), f).x+i,
				Tran.measureString(caves.getName(), f).y+i));

		for (EntityAdder a : handler.getAdders()) {
			sliders.add(new Slider(handler, 0, 1, 0.5, false, a.name));
			platforms.add(new BevelPlatform(2, 2, Tran.measureString(a.name, f).x+i, Tran.measureString(a.name, f).y+i));
		}
	}

	@Override
	public void renderGUI(Graphics2D g2d) {
		
	}

}