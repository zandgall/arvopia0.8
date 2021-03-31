package com.zandgall.arvopia.state;

import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Reporter;
import com.zandgall.arvopia.SmartCostume;
import com.zandgall.arvopia.enviornment.Enviornment;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.guis.Trading;
import com.zandgall.arvopia.quests.QuestManager;
import com.zandgall.arvopia.utils.BevelIndent;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.Mapper;
import com.zandgall.arvopia.worlds.World;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TitleState extends State {
	public static String appearance = "Male";

	private Button start;

	private Button quit;
	private Button options;
	private Button playerCustomization;
	private Button report;
	private BufferedImage title;

	Trading tradeTest;

	int selected = 0;

	BevelIndent body;

	BufferedImage bgimg = ImageLoader.loadImage("/textures/Bgimg.png");
	Enviornment enviornment;
	
	private double bgslide = 19;
	
	private boolean bgdirection = false;
	
	public TitleState(Handler handler) {
		super(handler);

		PublicAssets.init();

		title = ImageLoader.loadImage("/textures/Title.png");

		start = new Button(handler, handler.getWidth() / 2, handler.getHeight() / 2,
				"Starts the game by loading in the world", "Start", 1);
		options = new Button(handler, handler.getWidth() / 2, handler.getHeight() / 2 + 40, "Takes you to the options menu",
				"Options", 1);
		playerCustomization = new Button(handler, handler.getWidth() / 2, handler.getHeight() / 2 + 80, "View the changelog",
				"Changelog", 1);
		report = new Button(handler, handler.getWidth() / 2, handler.getHeight() / 2 + 120, "Opens a bug report",
				"Report a bug", 1);
		quit = new Button(handler, handler.getWidth() / 2, handler.getHeight() / 2 + 160, "Quits the application",
				"Quit", 1);

		handler.getMouse().setHandler(handler);

		body = new BevelIndent(0, 0, handler.getWidth(), handler.getHeight(), 355, 100);

		body.affectImage(new Color(120, 225, 255));

		enviornment = new Enviornment(handler, 1, 1, 1);
		//handler.soundSystem.stop("Wind");
		enviornment.setupStars();
		World.setWidth((int) (handler.getWidth()/9.0)+1);
		World.setHeight((int) (handler.getHeight()/18.0)+1);

		this.clip=true;

		SmartCostume.setDefault();
	}

	public void tick() {
		options.tick();
		start.tick();
		quit.tick();
		playerCustomization.tick();
		report.tick();

		if(QuestManager.al.size()>0)
			QuestManager.al.clear();
		if(QuestManager.alfin.size()>0)
			QuestManager.alfin.clear();

		if(handler.currentMusic.equals("empty"))
			handler.musicNative("/Songs/Title.ogg", true);

		if(handler.currentMusic!="/Songs/Title.ogg") {
			
			handler.fadeOutIn("/Songs/Title.ogg", 2000);
			
			enviornment.getLightManager().getList().clear();
			
			enviornment = new Enviornment(handler, 1, 1, 1);
			enviornment.setupStars();
			
		}
		enviornment.tick();
		enviornment.precipitation=false;
		enviornment.setTimeSpeed(((OptionState) handler.getGame().optionState).getSlideri("Enviornment", "Time Speed"));
		enviornment.lightQuality=((OptionState) handler.getGame().optionState).getSlideri("Enviornment", "Light Quality");
		//if(handler.soundSystem.playing("Rain"))
		//	handler.soundSystem.stop("Rain");
		//if(handler.soundSystem.playing("Wind"))
		//	handler.soundSystem.stop("Wind");
		
		if(bgdirection) {
			bgslide-=0.5;
			if(bgslide <= 20)
				bgdirection=false;
		} else {
			bgslide+=0.5;
			if(bgslide >= bgimg.getWidth()-handler.getWidth()-10)
				bgdirection=true;
		}
		
		if (report.on) {
			State.setState(handler.getGame().reportingState);
		}

		if (playerCustomization.on) {
			State.setState(handler.getGame().customizationState);
		}

		if (options.on) {
			State.setState(handler.getGame().optionState);
		}
		
		if (quit.on) {
			handler.getGame().stop();
		}
		if (start.on) {
			Reporter.save();

			State.setState(handler.getGame().worldState);
		}
		
	}

	public void render(Graphics2D g) {
		
		enviornment.renderSunMoon(g);
		enviornment.renderStars(g); 
		g.drawImage(bgimg.getSubimage((int) bgslide, 360, handler.getWidth(), handler.getHeight()), 0, 0, null);
		enviornment.render((Graphics) g, (Graphics2D) (Graphics) g);
		
		g.drawImage(title, handler.getWidth() / 2 - title.getWidth() / 2, 10, null);

		options.render(g);
		start.render(g);
		quit.render(g);
		playerCustomization.render(g);
		report.render(g);
		
	}
	
	public void renderGUI(Graphics2D g) {
		
	}

	public void init() {

		start = new Button(handler, handler.getWidth() / 2, handler.getHeight() / 2,
				"Starts the game by loading in the world", "Start", 1);
		playerCustomization = new Button(handler, handler.getWidth() / 2, handler.getHeight() / 2 + 40, "Change the player's experience",
				"Player Customization", 1);
		options = new Button(handler, handler.getWidth() / 2, handler.getHeight() / 2 + 80, "Takes you to the options menu",
				"Options", 1);
		report = new Button(handler, handler.getWidth() / 2, handler.getHeight() / 2 + 120, "Opens a bug report",
				"Report a bug", 1);
		quit = new Button(handler, handler.getWidth() / 2, handler.getHeight() / 2 + 160, "Quits the application",
				"Quit", 1);
		
//		handler.quickie("Sounds/grass1.ogg", "grass1", true, 0, 0, 0);

	}
}
