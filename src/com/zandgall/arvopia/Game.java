package com.zandgall.arvopia;

import com.zandgall.arvopia.display.Display;
import com.zandgall.arvopia.entity.EntityManager;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.entity.moveableStatics.TileDecor;
import com.zandgall.arvopia.enviornment.Enviornment;
import com.zandgall.arvopia.enviornment.LightingEffects;
import com.zandgall.arvopia.gfx.GameCamera;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.SerialImage;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.guis.Dialogue;
import com.zandgall.arvopia.guis.basic.BasicGui;
import com.zandgall.arvopia.input.KeyManager;
import com.zandgall.arvopia.input.MouseManager;
import com.zandgall.arvopia.levelmaker.LevelMakerState;
import com.zandgall.arvopia.quests.AchievementManager;
import com.zandgall.arvopia.quests.Quest;
import com.zandgall.arvopia.quests.QuestManager;
import com.zandgall.arvopia.state.CustomizationState;
import com.zandgall.arvopia.state.GameState;
import com.zandgall.arvopia.state.GenerateWorldState;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.state.State;
import com.zandgall.arvopia.state.TitleState;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.Chart;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.LongTimeline;
import com.zandgall.arvopia.utils.Noise;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.StringTimeline;
import com.zandgall.arvopia.utils.ToggleButton;
import com.zandgall.arvopia.utils.Utils;
import com.zandgall.arvopia.worlds.World;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.File;
import java.util.ConcurrentModificationException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.text.html.Option;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Game implements Runnable {
	public void pause() {
		paused = true;
	}

	public void unPause() {
		paused = false;
	}

	public int width = 0;
	public int height = 0;

	public int fps = 60;
	public int tps = 60;
	public int ticks = 0;
	public double timePerTick = 1000000000 / tps;
	public double renderPerTick = 1000000000 / fps;

	public Display getDisplay() {
		return display;
	}

	public void setTps(int tps) {
		this.tps = tps;
		timePerTick = (1000000000 / tps);
	}

	public void setFps(int fps) {
		this.fps = fps;
		renderPerTick = (1000000000 / fps);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	private static boolean running = false;

	private boolean renOnce = false;
	public boolean paused;

	public static double scale, resizingx, resizingy;

	public boolean ticking, rendering, caughtException = false;
	long tickTicks, renders, creatingr, drawingr, disposingr, misc;

	public Chart gamelagt, overallt, worldt, gamelagr, overallr, worldr, enviornmentr, playertr;

	public LongTimeline tgamelagt, toverallt, tworldt, tgamelagr, toverallr, tworldr, tenviornmentr;

	public StringTimeline actions;

	public LongTimeline space;

	public Chart loaded;

	public LongTimeline spawning;

	private Display display;

	private Button weatherain, weatherstorm, weatherclear, timemorning, timemidnight, timenoon, timeevening, fullScreen,
			defScreen, increaseScreen, decreaseScreen;
	private ToggleButton record;
	private String recordPath = "";

	public AffineTransform getDefaultTransform() {
		return af;
	}

	public int fullTicks, fullRens, renTicks;

	public String title;
	public static String prefix = System.getProperty("user.home");
	public boolean resizable;

	private KeyManager keyManager;
	private MouseManager mouse;
	private GameCamera gameCamera;
	public Handler handler;
	private static Thread thread;

	public CustomizationState customizationState;
	public GenerateWorldState generationState;
	public LevelMakerState levelMakerState;

	public boolean useResize = true, preSize = true;

	public Game(String title, int width, int height, boolean resizable, Log log) {
		Reporter.init();

		Noise.init((long) Public.random(0, 100000));

		scale = 1.0D;

		Game.log = log;

		achievementsDisplay = new AchievementManager();
		questsDisplay = new QuestManager();

		this.width = width;
		this.height = height;
		this.title = title;
		this.resizable = resizable;
		keyManager = new KeyManager();
		mouse = new MouseManager(handler);

		recorder = new Recorder(handler);

		overallt = new Chart(new double[] { 1.0D, 1.0D }, new String[] { "Tick", "Render" },
				new Color[] { Color.red, Color.blue }, "Overall", 10, 0, 100, 100);
		overallr = new Chart(new double[] { 1.0D, 1.0D, 1.0D }, new String[] { "Creating", "Drawing", "Disposing" },
				new Color[] { Color.red, Color.blue, Color.green }, "Rendering", 10, 200, 100, 100);
		gamelagt = new Chart(new double[] { 1.0D, 1.0D, 1.0D, 1.0D },
				new String[] { "World Ticks", "UI Ticks", "Songs Ticks", "Other Ticks" },
				new Color[] { Color.blue, Color.orange, Color.pink, Color.green }, "In-Game Ticks", 120, 0, 100, 100);
		gamelagr = new Chart(new double[] { 1.0D, 1.0D, 1.0D },
				new String[] { "World Renders", "UI Renders", "Other Renders" },
				new Color[] { Color.blue, Color.orange, Color.green }, "In-Game Renders", 120, 200, 100, 100);
		worldt = new Chart(new double[] { 1.0D, 1.0D, 1.0D, 1.0D, 1.0D },
				new String[] { "Entities Tick", "Items Tick", "Enviornment Tick", "Water Tick", "Tiles Tick" },
				new Color[] { Color.blue, Color.green, Color.red, Color.cyan, Color.orange }, "World Tick", 230, 0, 100,
				100);
		worldr = new Chart(new double[] { 1.0D, 1.0D, 1.0D, 1.0D, 1.0D },
				new String[] { "Entities Ren", "Items Render", "Enviornment Ren", "Water Ren", "Tiles Render" },
				new Color[] { Color.blue, Color.green, Color.red, Color.cyan, Color.orange }, "World Render", 230, 200,
				100, 100);
		enviornmentr = new Chart(new double[] { 1.0D, 1.0D, 1.0D, 1.0D, 1.0D },
				new String[] { "Weather", "Lighting", "Coloring", "Gui/Time", "Sky" },
				new Color[] { Color.blue, Color.green, Color.red, Color.orange, Color.yellow }, "Enviornment", 340, 0,
				100, 100);
		playertr = new Chart(new double[] {1.0, 1.0, 1.0, 1.0, 1.0}, new String[] {"Animation", "Attack", "Mouse", "Tools", "Water"},
				new Color[] { Color.blue, Color.green, Color.red, Color.orange, Color.yellow }, "Player", 450, 200, 100, 100);
		loaded = new Chart(new double[] { 1, 1, 1, 1, 1, 1, 1 },
				new String[] { "Pre", "Vars", "Tiles", "Entities", "Generating", "Background", "Post" },
				new Color[] { Color.blue, Color.red, Color.green, Color.magenta, Color.orange, Color.pink,
						new Color(100, 0, 150) },
				"World Loading", 450, 0, 100, 100);

//		spawning = new LongTimeline(new double[] { 1.0D, 1.0D, 1.0D, 1.0D },
//				new Color[] { Color.blue, Color.green, Color.red, Color.orange },
//				new String[] { "Flowers", "Trees", "Stones", "Other" }, "Spawning", 140, 0, 100, 100);
		spawning = new LongTimeline(new double[] { 1.0D, 1.0D, 1.0D, 1.0D, 1.0D },
				new Color[] { Color.blue, Color.green, Color.red, Color.orange, Color.magenta },
				new String[] { "Player", "Bats", "Skunks", "Bears", "Other" }, "Entities", 140, 0, 100, 100);

		toverallt = new LongTimeline(new double[] { 1.0D, 1.0D }, new Color[] { Color.red, Color.blue },
				new String[] { "Tick", "Render" }, "Overall", 10, 5, 60, 100);
		toverallr = new LongTimeline(new double[] { 1.0D, 1.0D, 1.0D },
				new Color[] { Color.red, Color.blue, Color.green }, new String[] { "Creating", "Drawing", "Disposing" },
				"Rendering", 10, 205, 60, 100);
		tgamelagt = new LongTimeline(new double[] { 1.0D, 1.0D, 1.0D, 1.0D },
				new Color[] { Color.blue, Color.orange, Color.pink, Color.green },
				new String[] { "World Ticks", "UI Ticks", "Songs Ticks", "Other Ticks" }, "In-Game Ticks", 160, 5, 60,
				100);
		tgamelagr = new LongTimeline(new double[] { 1.0D, 1.0D, 1.0D },
				new Color[] { Color.blue, Color.orange, Color.green },
				new String[] { "World Renders", "UI Renders", "Other Renders" }, "In-Game Renders", 160, 205, 60, 100);
		tworldt = new LongTimeline(new double[] { 1.0D, 1.0D, 1.0D, 1.0D, 1.0D },
				new Color[] { Color.blue, Color.green, Color.red, Color.cyan, Color.orange },
				new String[] { "Entities Tick", "Items Tick", "Enviornment Tick", "Water Tick", "Tiles Tick" },
				"World Tick", 310, 5, 60, 100);
		tworldr = new LongTimeline(new double[] { 1.0D, 1.0D, 1.0D, 1.0D, 1.0D },
				new Color[] { Color.blue, Color.green, Color.red, Color.cyan, Color.orange },
				new String[] { "Entities Ren", "Items Render", "Enviornment Ren", "Water Ren", "Tiles Render" },
				"World Render", 310, 205, 60, 100);
		tenviornmentr = new LongTimeline(new double[] { 1.0D, 1.0D, 1.0D, 1.0D, 1.0D },
				new Color[] { Color.blue, Color.green, Color.red, Color.orange, Color.yellow },
				new String[] { "Weather", "Lighting", "Coloring", "Gui/Time", "Sky" }, "Enviornment", 460, 5, 60, 100);

		space = new LongTimeline(new Color[] { Color.red, Color.blue }, new String[] { "Space used", "Space left" },
				"Heap Space", 580, 245, 60, 100);

		actions = new StringTimeline(Color.black, 620, 60, 14, "Actions");

	}

	public static Log log;
	private BufferStrategy bf;

	public void setDimension(int width, int height) {
		this.width = width;
		this.height = height;
		display.getFrame().setSize(width, height);
		display.getCanvas().setSize(width, height);
	}

	private Graphics g;
	private Graphics2D g2d;

	private void init() {
		try {
			display = new Display(title, width, height);
			display.getFrame().addKeyListener(keyManager);
			display.getFrame().addMouseListener(mouse);
			display.getFrame().addMouseMotionListener(mouse);
			display.getFrame().setResizable(resizable);
			display.getCanvas().addMouseListener(mouse);
			display.getCanvas().addMouseMotionListener(mouse);
			display.getCanvas().addMouseWheelListener(mouse);

			display.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			display.getFrame().setIconImage(ImageLoader.loadImage("/Icon.png"));

			initMessage("Initiating handlers");

			handler = new Handler(this);

			initMessage("Starting handlers");

			handler.init();

			initMessage("Initiating game camera");

			gameCamera = new GameCamera(handler, 0.0F, 0.0F);

			initMessage("Initiating public math functions");

			Public.init(handler);

			initMessage("Initiating Achievements and Quest");

			com.zandgall.arvopia.quests.Achievement.init(handler, achievementsDisplay);
			com.zandgall.arvopia.quests.Quest.init(handler, questsDisplay);

			initMessage("Initiating...");

//		Cutscene.init(handler);

			initMessage("Initiating options");
			optionState = new com.zandgall.arvopia.state.OptionState(handler);
			initMessage("Initiating gamestate");
			gameState = new GameState(handler);
			initMessage("Initiating title");
			menuState = new TitleState(handler);
			initMessage("Initiating world loader");
			worldState = new com.zandgall.arvopia.state.WorldLoaderState(handler);
			initMessage("Initiating Reporter");
			reportingState = new com.zandgall.arvopia.state.ReportingState(handler);
			initMessage("Initiating Achievements");
			achievementsState = new com.zandgall.arvopia.state.AchievementsState(handler);
			initMessage("Initiating Customization Menu");
			customizationState = new CustomizationState(handler);
			initMessage("Initiating World Generation State");
			generationState = new GenerateWorldState(handler);
			initMessage("Initiating Level Creator State");
			levelMakerState = new LevelMakerState(handler);
			initMessage("Setting up Renderer");
			Renderer r = new Renderer();

			initMessage("Initiating logs");

			handler.log("Will open? : " + FileLoader.readFile(Game.prefix + "\\Arvopia\\DontShowThisAgain"));

			State.setState(menuState);
			State.getState().init();

			log.log("Successfully initiated " + title + "'s Game loop");

			initMessage("Finishing initation");

			weatherain = new Button(handler, 10, 10, "Set's the weather to rain", "Rainy");
			weatherstorm = new Button(handler, 10, 50, "Set's the weather to storms", "Stormy");
			weatherclear = new Button(handler, 10, 90, "Set's the weather to clear", "Clear");

			timemorning = new Button(handler, 10, 130, "Sets the time to ~8:00 AM", "Morning");
			timenoon = new Button(handler, 10, 170, "Sets the time to noon", "Noon");
			timeevening = new Button(handler, 10, 210, "Sets the time to ~8:00 PM", "Evening");
			timemidnight = new Button(handler, 10, 250, "Sets the time to midnight", "Midnight");

			fullScreen = new Button(handler, 10, 10, "Set's the game to full size", "Full Screen");
			defScreen = new Button(handler, 10, 50, "Set's the game to default size", "Default Screen");
			increaseScreen = new Button(handler, 10, 90, "Increases screen size", "Increase Screen");
			decreaseScreen = new Button(handler, 10, 130, "Decreases screen size", "Decrease Screen");

			record = new ToggleButton(handler, 10, 350, "Records what happens if on", "Record");

			width = display.getCanvas().getWidth();
			height = display.getCanvas().getHeight();

			customizationState.save();

			Initiator.initiations.add(new Initiator() {
				public void worldInit(Handler game, World w) {
					int times = (int) (World.getWidth() * Public.debugRandom(0.4, 0.8));
					for (int i = 0; i < times; i++) {
						int x = (int) (Public.random(0, World.getWidth()));
						int y = w.randomHeight(x) - 1;
						w.getEntityManager().addEntity(new TileDecor(game, x * 18, y * 18, (int) Public.random(0, 5)),
								true);
					}
				}

				@Override
				public void gameInit(Handler game) {
				}

				@Override
				public void itemInit(Handler game, Player p) {

				}

				@Override
				public void playerInit(Handler game, Player p) {

				}

				@Override
				public void savingWorld(Handler game, String pathToSave) {

				}

				@Override
				public void loadSave(Handler game, String pathToSave) {

				}

				@Override
				public void tick(Handler game) {

				}
			});
			Initiator.agameInit(handler);

			minScreen();

			LightingEffects.start();

		} catch (Exception e) {
			e.printStackTrace();
			bf = display.getCanvas().getBufferStrategy();
			if (bf == null) {
				display.getCanvas().createBufferStrategy(3);
				bf = display.getCanvas().getBufferStrategy();
			}

			g = bf.getDrawGraphics();
			g2d = ((Graphics2D) g);
			g2d.clearRect(0, 0, width, height);
			g2d.setColor(Color.black);
			g2d.fillRect(0, 0, width, height);
			g2d.setColor(Color.white);
			Tran.drawString(g2d, "There was a major error when loading the game" + System.lineSeparator()
					+ "Continuing may result in an unexpected crash:" + System.lineSeparator() + e.getStackTrace(), 10,
					10);
			g.dispose();
			g2d.dispose();
			bf.show();
//			g.dispose();
//			g2d.dispose();
			bf.show();

			Console.log("Showing message for 5000");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	/*
	 * private void reinit() {
	 * 
	 * initMessage("Initiating handlers");
	 * 
	 * handler = new Handler(this); handler.init(); gameCamera = new
	 * GameCamera(handler, 0.0F, 0.0F);
	 * 
	 * com.zandgall.arvopia.quests.Achievement.init(handler, achievementsDisplay);
	 * com.zandgall.arvopia.quests.Quest.init(handler, questsDisplay);
	 * Cutscene.init(handler);
	 * 
	 * Public.init(handler);
	 * 
	 * optionState.init(); gameState.init(); menuState.init();
	 * customizationState.init(); worldState.init(); reportingState.init();
	 * achievementsState.init();
	 * 
	 * handler.log("Will open? : " +
	 * FileLoader.readFile("C:\\Arvopia\\DontShowThisAgain"));
	 * 
	 * State.setState(menuState); State.getState().init();
	 * 
	 * log.log("Successfully initiated " + title + "'s Game loop");
	 * 
	 * }
	 */

	public void useResize(boolean tf) {
		preSize = useResize;
		useResize = tf;
	}

	public void fullScreen() {
		float ratio = 720.0f / 405.0f;

		Toolkit t = Toolkit.getDefaultToolkit();

		double height = t.getScreenSize().getHeight() - 65;

		double width = height * ratio;

		display.setSize((int) width, (int) height + 5);
	}

	public void minScreen() {
		display.setSize(720, 405);
	}

	public void decreaseScreen() {
		float ratio = 720.0f / 405.0f;
		int height = display.getCanvas().getHeight() - 10;
		int width = (int) (height * ratio);
		display.setSize(width, height);
	}

	public void increaseScreen() {
		float ratio = 720.0f / 405.0f;
		int height = display.getCanvas().getHeight() + 10;
		int width = (int) (height * ratio);
		display.setSize(width, height);
	}

	public void initMessage(String s) {
		Renderer.GATE = false;
		bf = display.getCanvas().getBufferStrategy();
		if (bf == null) {
			display.getCanvas().createBufferStrategy(3);
			bf = display.getCanvas().getBufferStrategy();
		}

		System.err.println(s);

		g = bf.getDrawGraphics();
		g2d = ((Graphics2D) g);
		g2d.clearRect(0, 0, width, height);
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(Color.white);
		g2d.drawString(s, 20, 30);
		bf.show();
		g.dispose();
		g2d.dispose();
		bf.show();
	}

	AffineTransform af;
	public State optionState;

	private void tickCharts() {
		GameState g = (GameState) gameState;
		toverallt.add(tickTicks, 0);
		toverallt.add(renders, 1);

		toverallr.add(creatingr, 0);
		toverallr.add(drawingr, 1);
		toverallr.add(disposingr, 2);

		tgamelagt.add(g.worldt, 0);
		tgamelagt.add(g.uit, 1);
		tgamelagt.add(g.songst, 2);
		tgamelagt.add(g.othert, 3);

		tgamelagr.add(g.worldtr, 0);
		tgamelagr.add(g.uir, 1);
		tgamelagr.add(g.otherr, 2);

		tworldt.add(handler.getWorld().entities, 0);
		tworldt.add(handler.getWorld().items, 1);
		tworldt.add(handler.getWorld().enviorn, 2);
		tworldt.add(handler.getWorld().water, 3);
		tworldt.add(handler.getWorld().tilet, 4);

		tworldr.add(handler.getWorld().entitiesr, 0);
		tworldr.add(handler.getWorld().itemsr, 1);
		tworldr.add(handler.getWorld().enviorr, 2);
		tworldr.add(handler.getWorld().waterr, 3);
		tworldr.add(handler.getWorld().tiler, 4);

		tenviornmentr.add(Enviornment.weather, 0);
		tenviornmentr.add(Enviornment.lighting, 1);
		tenviornmentr.add(Enviornment.lightcoloring, 2);
		tenviornmentr.add(Enviornment.guitime, 3);
		tenviornmentr.add(Enviornment.sky, 4);

//		spawning.add(handler.getWorld().sFlowers, 0);
//		spawning.add(handler.getWorld().sTrees, 1);
//		spawning.add(handler.getWorld().sStones, 2);
//		spawning.add(handler.getWorld().sOther, 3);
		spawning.add(EntityManager.playert, 0);
		spawning.add(EntityManager.bat, 1);
		spawning.add(EntityManager.skunk, 2);
		spawning.add(EntityManager.bear, 3);
		spawning.add(EntityManager.othert, 4);

		if (KeyManager.function[0]) {
			gamelagt.update(new double[] { g.worldt, g.uit, g.songst, g.othert });
			gamelagr.update(new double[] { g.worldtr, g.uir, g.otherr });
			overallt.update(new double[] { tickTicks, renders });
			overallr.update(new double[] { creatingr, drawingr, disposingr });
			worldt.update(new double[] { handler.getWorld().entities, handler.getWorld().items,
					handler.getWorld().enviorn, handler.getWorld().water, handler.getWorld().tilet });

			worldr.update(new double[] { handler.getWorld().entitiesr, handler.getWorld().itemsr,
					handler.getWorld().enviorr, handler.getWorld().waterr, handler.getWorld().tiler });

			enviornmentr.update(new double[] { Enviornment.weather, Enviornment.lighting, Enviornment.lightcoloring,
					Enviornment.guitime, Enviornment.sky });

			playertr.update(new double[] { Player.animationt, Player.attackt, Player.mouset, Player.toolst, Player.watert});

			loaded.update(new double[] { handler.getWorld().preload, handler.getWorld().varsload,
					handler.getWorld().tilesload, handler.getWorld().customload, handler.getWorld().addingload,
					handler.getWorld().backgroundload, handler.getWorld().postload });
		}

		/*
		 * if (keyManager.f12) { toverallt.export(); toverallr.export();
		 * tgamelagt.export(); tgamelagr.export(); tworldt.export(); tworldr.export();
		 * tenviornmentr.export(); }
		 */

		if (KeyManager.function[2]) {
			weatherain.tick();
			weatherstorm.tick();
			weatherclear.tick();

			timemorning.tick();
			timenoon.tick();
			timeevening.tick();
			timemidnight.tick();

			if (weatherain.on) {
				handler.getEnviornment().setWeather(1);
			} else if (weatherstorm.on) {
				handler.getEnviornment().setWeather(2);
			} else if (weatherclear.on) {
				handler.getEnviornment().setWeather(0);
			}

			if (timemorning.on) {
				handler.getEnviornment().setTime(420 * 60);
			} else if (timenoon.on) {
				handler.getEnviornment().setTime(11 * 60 * 60);
			} else if (timeevening.on) {
				handler.getEnviornment().setTime(1100 * 60);
			} else if (timemidnight.on) {
				handler.getEnviornment().setTime(0);
			}
		}

		if (KeyManager.function[3]) {
			fullScreen.tick();
			defScreen.tick();
			increaseScreen.tick();
			decreaseScreen.tick();

			record.tick();

			if (fullScreen.on) {
				fullScreen();
			} else if (defScreen.on) {
				minScreen();
			} else if (increaseScreen.on) {
				increaseScreen();
			} else if (decreaseScreen.on) {
				decreaseScreen();
			}
		}
	}

	private void tick() {

//		if (display.getCanvas().getWidth() != width || display.getCanvas().getHeight() != height) {
		width = display.getCanvas().getWidth();
		height = display.getCanvas().getHeight();
//		}

		resizingx = width / 720.0;
		resizingy = height / 400.0;

		tickCharts();

		space.add(Runtime.getRuntime().totalMemory(), 0);
		space.add(Runtime.getRuntime().maxMemory(), 1);

		Dialogue.tick(handler);
		achievementsDisplay.tick();
		questsDisplay.tick();

		keyManager.tick();

		mouse.changeCursor("");

		if ((State.getState() != null) && (!paused)) {
			State.getState().tick();
			handler.soundSystem.setVolume("music", OptionState.msVolume);
			for (String s : handler.sounds) {
				handler.soundSystem.setVolume(s, OptionState.fxVolume);
			}
		} else if (paused) {
			handler.getWorld().getEntityManager().getPlayer().tick();
		}

		Initiator.atick(handler);

		BasicGui.tickAll();

		mouse.tick();
	}

	public State gameState;
	public State menuState;

	public void forceRender() {
		render();
	}

	public void forceTick() {
		tick();
	}

	public State worldState;

	BufferedImage sized;

	public boolean drawing = false;

	private void render() {
		long pre = System.nanoTime(), pre2 = System.nanoTime();
		bf = display.getCanvas().getBufferStrategy();
		if (bf == null) {
			display.getCanvas().createBufferStrategy(3);
			return;
		}

//		if(unsized==null)
//			unsized = new BufferedImage(720, 405, BufferedImage.TYPE_4BYTE_ABGR);
		if (sized == null)
			sized = new BufferedImage(720, 405, BufferedImage.TYPE_4BYTE_ABGR);

//		BufferedImage unsized = new BufferedImage(720, 405, BufferedImage.TYPE_4BYTE_ABGR);

		g = bf.getDrawGraphics();
//		g = unsized.createGraphics();
		g2d = (Graphics2D) g;

		if (!renOnce) {
			af = g2d.getTransform();
			renOnce = true;
		}

//		g.clearRect(0, 0, width, height);

		String t = OptionState.RENDER_TYPES[OptionState.renType];

		g2d.setRenderingHints(Tran.mix(Tran.alias, Tran.textantialias,
				(t == "Speed" ? Tran.renderspeed : t == "Quality" ? Tran.renderquality : Tran.rendernormal),
				Tran.interneighbor));

		if (useResize)
			g2d.scale(width / 720.0, height / 400.0);

		creatingr = System.nanoTime() - pre;
		pre = System.nanoTime();

		if (State.getState() != null) {
			State.getState().render(g2d);
		} else {
			System.err.println("No State loaded");
			State.setState(menuState);
			State.getState().init();
		}

		renderGraphs(g2d, g);
		Dialogue.render(g2d, handler);
		achievementsDisplay.Render(g2d);
		questsDisplay.Render(g2d);

		BasicGui.renderAll(g2d);
		
		ToggleButton.renderDec(g2d);

		drawingr = System.nanoTime() - pre;
		pre = System.nanoTime();

		if (record != null)
			if (record.on) {
				if (!record.preOn) {
					recordPath = prefix + "\\Arvopia\\Recording\\" + Public.getCurrentDate() + " ("
							+ System.currentTimeMillis() / 1000 + ")";
					Utils.createDirectory(recordPath);
					System.out.println("Created " + recordPath);
				}
				if (new File(recordPath).exists()) {
					int length = new File(recordPath).list().length;
					ImageLoader.saveImage((BufferedImage) display.getCanvas().createImage(width, height),
							recordPath + "\\" + length + ".png");
				}
			}

		g.dispose();
		g2d.dispose();

//		if(Math.random()>0) {
//			sized = unsized;
//			sized.setRGB(0, 0, 720, 405, unsized.getRGB(0, 0, 720, 405, new int[720*405], 0, 720), 0, 720);
//			Console.log("Set");
//		}

//		Graphics fin = bf.getDrawGraphics();
//		
//		fin.drawImage(unsized, 0, 0, null);
//		if(Resizer.resized!=null)
//			fin.drawImage(Resizer.resized, 0, 0, null);
//		fin.dispose();

		bf.show();
		if (OptionState.renType > 0)
			bf.show();

		disposingr = System.nanoTime() - pre;

		renders = (System.nanoTime() - pre2);
	}

	public void pushToScreen(Image b) {
		bf = display.getCanvas().getBufferStrategy();
		if (bf == null) {
			display.getCanvas().createBufferStrategy(3);
			bf = display.getCanvas().getBufferStrategy();
		}

		g = bf.getDrawGraphics();
		g2d = ((Graphics2D) g);
		g2d.clearRect(0, 0, width, height);
		g2d.drawImage(b, 0, 0, null);
		bf.show();
		g.dispose();
		g2d.dispose();
		bf.show();
	}

	public void renderGraphs(Graphics2D g2d, Graphics g) {
		if (KeyManager.function[0]) {
			g2d.setColor(new Color(0, 0, 50, 100));
			g2d.fillRect(0, 0, width, height);
			mouse.visualize(g2d);
			overallt.render(g2d);
			overallr.render(g2d);
			gamelagt.render(g2d);
			gamelagr.render(g2d);
			worldt.render(g2d);
			worldr.render(g2d);
			enviornmentr.render(g2d);
			playertr.render(g2d);
			loaded.render(g2d);

			Enviornment.lightingBreakdown.render(g2d);

			g2d.setFont(Public.defaultBoldFont);
			Tran.drawOutlinedText(g2d, 720 - 50, 20, "TPS: " + fullTicks, 1.0, Color.black, Color.white);
			Tran.drawOutlinedText(g2d, 720 - 50, 35, "FPS: " + fullRens, 1.0, Color.black, Color.white);
			Tran.drawOutlinedText(g2d, 720 - 70, 50, stable ? "STABLE" : "NOT STABLE", 1.0, Color.black, Color.white);
		}

		if (KeyManager.function[4]) {
			g2d.setFont(Public.defaultBoldFont);
			Tran.drawOutlinedText(g2d, 720 - 50, 20, "TPS: " + fullTicks, 1.0, Color.black, Color.white);
			Tran.drawOutlinedText(g2d, 720 - 50, 35, "FPS: " + fullRens, 1.0, Color.black, Color.white);
			Tran.drawOutlinedText(g2d, 720 - 70, 50, stable ? "STABLE" : "NOT STABLE", 1.0, Color.black, Color.white);
		}

		if (KeyManager.function[1]) {
			g.setColor(new Color(0, 0, 50, 100));
			g.fillRect(0, 0, width, height);

//			toverallt.render(g2d);
//			toverallr.render(g2d);
//			tgamelagt.render(g2d);
//			tgamelagr.render(g2d);
//			tworldt.render(g2d);
//			tworldr.render(g2d);
//			tenviornmentr.render(g2d);
//
//			entitiel.render(g2d);
//
//			actions.render(g2d);
//
//			space.render(g2d);

			toverallt.render(g);
			toverallr.render(g);
			tgamelagt.render(g);
			tgamelagr.render(g);
			tworldt.render(g);
			tworldr.render(g);
			tenviornmentr.render(g);

			actions.render(g);

			space.render(g);

			g2d.setFont(Public.defaultBoldFont);
			g2d.setColor(Color.white);
			Tran.drawOutlinedText(g2d, 720 - 50, 20, "TPS: " + fullTicks, 1.0, Color.black, Color.white);
			Tran.drawOutlinedText(g2d, 720 - 50, 35, "FPS: " + fullRens, 1.0, Color.black, Color.white);
			Tran.drawOutlinedText(g2d, 720 - 70, 50, stable ? "STABLE" : "NOT STABLE", 1.0, Color.black, Color.white);
		}

		if (KeyManager.function[2]) {
			weatherain.render(g2d);
			weatherstorm.render(g2d);
			weatherclear.render(g2d);

			timemorning.render(g2d);
			timenoon.render(g2d);
			timeevening.render(g2d);
			timemidnight.render(g2d);
		}

		if (KeyManager.function[3]) {
			fullScreen.render(g2d);
			defScreen.render(g2d);
			increaseScreen.render(g2d);
			decreaseScreen.render(g2d);

			record.render(g2d);
		}

		if (KeyManager.function[11]) {
			long pre = System.nanoTime();
			int i = 0;
			Map<String, Long> t = handler.getEntityManager().totalr, a = handler.getEntityManager().averager,
					n = handler.getEntityManager().numberr;
			g2d.setFont(Public.defaultFont);
			if (t != null && !t.isEmpty())
				for (String e : t.keySet()) {
//				handler.getEntityManager().total.keySet();

//				Tran.drawOutlinedText(g, 4, 14 + i*14, e +" averages "+ a.get(e) +"mu with a total of " + t.get(e) +"mu for " + n.get(e) + " entities (" + e +")", 1, Color.black, Color.white);
					Tran.drawOutlinedText(g, 4, 14 + i * 14, e + " averages " + a.get(e) + "mu", 1, Color.black,
							Color.white);
					Tran.drawOutlinedText(g, 254, 14 + i * 14, "with a total of " + t.get(e) + "mu", 1, Color.black,
							Color.white);
					Tran.drawOutlinedText(g, 504, 14 + i * 14, "for " + n.get(e) + " entities (" + e + ")", 1,
							Color.black, Color.white);
					i++;
				}

			if (a != null && !a.isEmpty() && a.containsKey("TICK TIME")) {
				Tran.drawOutlinedText(g, 4, 24 + i * 14, "Time to calculate this was " + a.get("TICK TIME") + "mu", 1,
						Color.black, Color.white);
				i++;
			}
			Tran.drawOutlinedText(g, 4, 24 + i * 14,
					"Time took to render this was " + ((System.nanoTime() - pre) / 1000) + "mu", 1, Color.black,
					Color.white);

		}

	}

	public State reportingState;
	public State achievementsState;
	public AchievementManager achievementsDisplay;

	public void run() {
		init();
		optionState.tick();

		double delta = 0.0D;
		double renderDelta = 0.0D;

		long lastTime = System.nanoTime();
		long timer = 0L;
		
		boolean inlined = false;

		while (isRunning()) {
			long now = System.nanoTime();
//			long nowSec = Math.round((float) (System.currentTimeMillis() / 1000L));
			delta += (now - lastTime) / timePerTick;
			renderDelta += (now - lastTime) / renderPerTick;
			timer += now - lastTime;
			lastTime = now;
			
//			long preCheck = nowSec;

			if (delta >= 1.0D) {
				long pre = System.nanoTime();
				try {
					tick();
				} catch (ConcurrentModificationException e) {
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				ticks += 1;
				delta = 0.0D;
				tickTicks = (System.nanoTime() - pre);
				Renderer.GATE = true;
//				Console.log("Set GATE to TRUE");
			}

			if (renderDelta >= 1.0D || ((OptionState)optionState).getSlideri("Game control", 0)==1) {
				rendering = true;
				try {
					if(!OptionState.splitstreamrender) {
						render();
						inlined = true;
					} else {
						if(inlined) {
							inlined=false;
							Renderer r = new Renderer();
						}
					}
				} catch (Exception e) {
					System.err.println("What's with all these errors?");
					e.printStackTrace();
				}
				rendering = false;
				renTicks += 1;
				renderDelta = 0.0D;
			}

			if (timer >= 1000000000L) {
				fullTicks = ticks;
				fullRens = (int) Renderer.frames;
				if(!OptionState.splitstreamrender)
					fullRens = renTicks;
//				handler.saveFps(fullTicks);

				stable = (((Public.difference(preTicks, prepreTicks) < 2.0D)
						&& (Public.difference(preTicks, ticks) < 2.0D) && (Public.difference(ticks, fps) < 5.0D))
						|| (ticks < Public.range(0.0D, fps, fps - 20)));

//				prevCheck = nowSec;
//
//				if ((nowSec - prevCheck <= 2L) && ((Public.difference(renTicks, preRenTicks) > 2.0D)
//						|| (Public.difference(ticks, preTicks) > 2.0D))) {
//					log.out("FPS Fluctuating a little... : " + renTicks + " : " + ticks);
//				} else if (stable) {
//					log.out("FPS: " + renTicks + " : " + ticks);
//				} else {
//					log.out("FPS Not stable: " + renTicks + " : " + ticks);
//				}

				prepreTicks = preTicks;
				preTicks = ticks;
				preRenTicks = renTicks;

//				mouse.reset();

//				State.getState().timing();

				ticks = 0;
				renTicks = 0;
				timer = 0L;
				Renderer.frames = 0l;
			}
		}

		System.out.println("OOPSIES");

		stop();
	}

	public QuestManager questsDisplay;
	public Recorder recorder;

	public KeyManager getKeyManager() {
		return keyManager;
	}

	public int prepreTicks;
	public int preTicks;
	public int preRenTicks;
	public boolean stable;

	public MouseManager getMouse() {
		return mouse;
	}

	public GameCamera getGameCamera() {
		return gameCamera;
	}

	public synchronized void start() {
		if (isRunning())
			return;
		setRunning(true);
		thread = new Thread(this);
		thread.start();
	}

	public synchronized void stop() {
		if (!isRunning())
			return;
		log.out("Terminated");
		handler.soundSystem.cleanup();
		display.stop();

		System.exit(0);
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return;
	}

	public static boolean isRunning() {
		return running;
	}

	public static void setRunning(boolean running) {
		Game.running = running;
	}

	public Image makeImage(Shape s) {
		Rectangle r = s.getBounds();
		Image image = new java.awt.image.BufferedImage(width, height, 12);
		Graphics2D gr = (Graphics2D) image.getGraphics();

		gr.translate(-r.getX(), -r.getY());
		gr.draw(s);
		gr.dispose();
		return image;
	}

	public Graphics2D get2D() {
		return g2d;
	}

	public JFrame getFrame() {
		return display.getFrame();
	}

	public static void out(Object s) {
		System.out.println(s);
	}

	public static void err(Object s) {
		System.err.println(s);
	}

}

class Renderer implements Runnable {

	public static boolean GATE = false;

	public static long frames = 0;

	public Renderer() {
		Thread t = new Thread(this, "Rendering");
		t.start();
	}

	@Override
	public void run() {

		double timePerTick = 1000000000/60.0;
		
		double delta = 0.0D;

		long lastTime = System.nanoTime();
		long timer = 0L;

		Game g = ArvopiaLauncher.game;
		while (true) {
			
			long now = System.nanoTime();
			delta += (now - lastTime) / timePerTick;
			timer += now - lastTime;
			lastTime = now;


			if (g != null && g.getDisplay() != null && GATE && (delta>=1 || ((OptionState)g.optionState).getSlideri("Game control", 0)==1) && OptionState.splitstreamrender) {
				try {
					g.forceRender();
				} catch (ConcurrentModificationException e) {
				} catch (Exception e) {
					e.printStackTrace();
				}
				frames++;
				delta = 0;
				timePerTick = g.renderPerTick;
			} else {
				System.out.print("");
				g = ArvopiaLauncher.game;
				if(!OptionState.splitstreamrender)
					return;
			}
		}
	}

}
