package com.zandgall.arvopia.state;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Reporter;
import com.zandgall.arvopia.environment.Environment;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.quests.QuestManager;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.worlds.World;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TitleState extends State {

	private Button start, quit, options, playerCustomization;
	private final BufferedImage title;

	private final BufferedImage background_image = ImageLoader.loadImage("/textures/Bgimg.png");
	private Environment environment;
	
	private double background_slide = 19;
	private boolean background_direction = false;
	
	public TitleState(Handler handler) {
		super(handler);

		title = ImageLoader.loadImage("/textures/Title.png");

		environment = new Environment(handler, 1, 1, 1);
		environment.setupStars();
		World.setWidth((int) (handler.getWidth()/9.0)+1);
		World.setHeight((int) (handler.getHeight()/18.0)+1);
	}

	public void tick() {
		options.tick();
		start.tick();
		quit.tick();
		playerCustomization.tick();

		if(QuestManager.al.size()>0)
			QuestManager.al.clear();
		if(QuestManager.alfin.size()>0)
			QuestManager.alfin.clear();

		if(handler.currentMusic.equals("empty"))
			handler.musicNative("/Songs/Title.ogg", true);

		if(!handler.currentMusic.equals("/Songs/Title.ogg")) {
			handler.fadeOutIn("/Songs/Title.ogg", 2000);
			
			environment.getLightManager().getList().clear();
			
			environment = new Environment(handler, 1, 1, 1);
			environment.setupStars();
		}
		environment.tick();
		environment.precipitation=false;
		environment.setTimeSpeed(handler.getGame().optionState.getIntSlider("Environment", "Time Speed"));
		environment.lightQuality= handler.getGame().optionState.getIntSlider("Environment", "Light Quality");
		
		if(background_direction) {
			background_slide -=0.5;
			if(background_slide <= 20)
				background_direction =false;
		} else {
			background_slide +=0.5;
			if(background_slide >= background_image.getWidth()-handler.getWidth()-10)
				background_direction =true;
		}

		if (playerCustomization.on)
			State.setState(handler.getGame().customizationState);
		else if (options.on)
			State.setState(handler.getGame().optionState);
		else if (quit.on)
			handler.getGame().stop();
		else if (start.on) {
			Reporter.save();
			State.setState(handler.getGame().worldState);
		}
		
	}

	public void render(Graphics2D g) {
		
		environment.renderSunMoon(g);
		environment.renderStars(g);
		g.drawImage(background_image.getSubimage((int) background_slide, 360, handler.getWidth(), handler.getHeight()), 0, 0, null);
		environment.render(g);
		
		g.drawImage(title, handler.getWidth() / 2 - title.getWidth() / 2, 10, null);

		options.render(g);
		start.render(g);
		quit.render(g);
		playerCustomization.render(g);
		
	}
	
	public void renderGUI(Graphics2D g) {
		
	}

	public void init() {

		start = new Button(handler, handler.getWidth() / 2, handler.getHeight() / 2,
				"Starts the game by loading in the world", "Start", 1);
		playerCustomization = new Button(handler, handler.getWidth() / 2, handler.getHeight() / 2 + 40, "Change the player's experience",
				"Player", 1);
		options = new Button(handler, handler.getWidth() / 2, handler.getHeight() / 2 + 80, "Takes you to the options menu",
				"Options", 1);
		quit = new Button(handler, handler.getWidth() / 2, handler.getHeight() / 2 + 160, "Quits the application",
				"Quit", 1);

	}
}
