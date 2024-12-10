package com.zandgall.arvopia;

import com.zandgall.arvopia.display.*;
import com.zandgall.arvopia.entity.EntityManager;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.entity.moveableStatics.TileDecor;
import com.zandgall.arvopia.environment.Environment;
import com.zandgall.arvopia.environment.LightingEffects;
import com.zandgall.arvopia.gfx.GameCamera;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.guis.Dialogue;
import com.zandgall.arvopia.guis.basic.BasicGui;
import com.zandgall.arvopia.input.*;
import com.zandgall.arvopia.quests.*;
import com.zandgall.arvopia.state.*;
import com.zandgall.arvopia.tiles.Bridge;
import com.zandgall.arvopia.tiles.FenceTile;
import com.zandgall.arvopia.tiles.GrassTile;
import com.zandgall.arvopia.tiles.backtile.BackGrassTile;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.Chart;
import com.zandgall.arvopia.utils.LongTimeline;
import com.zandgall.arvopia.utils.Noise;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.StringTimeline;
import com.zandgall.arvopia.utils.ToggleButton;
import com.zandgall.arvopia.worlds.World;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Map;

import javax.swing.JFrame;

public class Game implements Runnable {

	// Global "Game.____" variables
	private static Thread thread;
	// public static final String prefix = System.getProperty("os.name").startsWith("Windows") ? System.getProperty("user.home") : "Game Files";
	public static final String prefix = System.getProperty("user.home")+"/Arvopia";
	public static Log log;
	public static double scale, resize_X, resize_Y;
	public static boolean running = false;
	public boolean paused;

	// Window properties
	private Display display;
	public final String title;
	public int width;
	public int height;
	public final boolean resizable;
	public boolean useResize = true;

	// Ticking, Framerate, and framerate stability
	public int fps = 60, tps = 60, ticks = 0;
	public double timePerTick = 1.0e+9 / tps, renderPerTick = 1.0e+9 / fps;
	public int fullTicks, fullRens, renTicks;
	public int prePreTicks, preTicks;
	public boolean stable;
	// Charts and Graphs

	long tickTicks, renders, time_render_creation, time_render_drawing, time_render_disposing;
	public Chart time_game_tick, time_overall, time_world_tick, time_game_render, time_overall_render, time_world_render, time_environment_render, time_player_tick;
	public LongTimeline timeline_game_tick, timeline_overall, timeline_world_tick, timeline_game_render, timeline_overall_render, timeline_world_render, timeline_environment_render;
	public StringTimeline actions;
	public LongTimeline space;
	public Chart loaded;
	public LongTimeline spawning;

	// Debug options and cheats
	private Button weather_rain, weather_storm, weather_clear, time_morning, time_midnight, time_noon, time_evening,
	fullScreen, defScreen, increaseScreen, decreaseScreen;

	// Managers and game objects
	private final KeyManager keyManager;
	private final MouseManager mouse;
	public final AchievementManager achievementsDisplay;
	public final QuestManager questsDisplay;
	private GameCamera gameCamera;
	public Handler handler;

	// Game states
	public CustomizationState customizationState;
	public GenerateWorldState generationState;
	public LevelMakerState levelMakerState;
	public OptionState optionState;
	public GameState gameState;
	public TitleState menuState;
	public WorldLoaderState worldState;
	public ReportingState reportingState;
	public AchievementsState achievementsState;

	// Graphics and graphical properties
	private Graphics2D g2d;
	private BufferStrategy bf;

	public Game(String title, int width, int height, boolean resizable, Log log) {
		Game.log = log;
		Reporter.init();
		Noise.init(System.nanoTime());

		keyManager = new KeyManager();
		mouse = new MouseManager(handler);
		achievementsDisplay = new AchievementManager();
		questsDisplay = new QuestManager();

		this.width = width;
		this.height = height;
		this.title = title;
		this.resizable = resizable;
		scale = 1.0D;

		initiateGraphs();
	}

	public void initMessage(String s) {
		Renderer.GATE = false;
		bf = display.getCanvas().getBufferStrategy();
		if (bf == null) {
			display.getCanvas().createBufferStrategy(3);
			bf = display.getCanvas().getBufferStrategy();
		}

		System.err.println(s);

		g2d = ((Graphics2D) bf.getDrawGraphics());
		g2d.clearRect(0, 0, width, height);
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(Color.white);
		g2d.drawString(s, 20, 30);
		bf.show();
		g2d.dispose();
	}

	private void init() {
		try {
			display = new Display(title, width, height);
			display.getFrame().setResizable(resizable);
			display.getFrame().addKeyListener(keyManager);
			display.getFrame().addMouseListener(mouse);
			display.getFrame().addMouseMotionListener(mouse);
			display.getCanvas().addMouseListener(mouse);
			display.getCanvas().addMouseMotionListener(mouse);
			display.getCanvas().addMouseWheelListener(mouse);

			display.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			display.getFrame().setIconImage(ImageLoader.loadImage("/Icon.png"));

			initMessage("Initiating handlers");

			handler = new Handler(this);

			initMessage("Starting handler");

			handler.init();

			initMessage("Initiating game camera");

			gameCamera = new GameCamera(handler, 0.0F, 0.0F);

			initMessage("Initiating public math functions");

			Public.init(handler);

			initMessage("Initiating Achievements and Quest");

			Achievement.init(handler, achievementsDisplay);
			Quest.init(handler, questsDisplay);

			initMessage("Initiating...");
			mouse.setHandler(handler);
			PublicAssets.init();
			SmartCostume.setDefault();
			Creature.init();
			GrassTile.loadAllGrassTiles();
			FenceTile.loadAllFenceTiles();
			Bridge.loadAllBridgeTiles();
			BackGrassTile.loadAllBackGrassTiles();

			initMessage("Initiating options");
			optionState = new OptionState(handler);
			handler.loadMods();
			initMessage("Initiating game state");
			gameState = new GameState(handler);
			initMessage("Initiating title");
			menuState = new TitleState(handler);
			initMessage("Initiating world loader");
			worldState = new WorldLoaderState(handler);
			initMessage("Initiating Reporter");
			reportingState = new ReportingState(handler);
			initMessage("Initiating Achievements");
			achievementsState = new AchievementsState(handler);
			initMessage("Initiating Customization Menu");
			customizationState = new CustomizationState(handler);
			initMessage("Initiating World Generation State");
			generationState = new GenerateWorldState(handler);
			initMessage("Initiating Level Creator State");
			levelMakerState = new LevelMakerState(handler);
			initMessage("Setting up Renderer");
			new Renderer();

			State.setState(menuState);
			State.getState().init();

			log.log("Successfully initiated " + title + "'s Game loop");

			initMessage("Finishing initiation");

			weather_rain = new Button(handler, 10, 10, "Set's the weather to rain", "Rainy");
			weather_storm = new Button(handler, 10, 50, "Set's the weather to storms", "Stormy");
			weather_clear = new Button(handler, 10, 90, "Set's the weather to clear", "Clear");

			time_morning = new Button(handler, 10, 130, "Sets the time to ~8:00 AM", "Morning");
			time_noon = new Button(handler, 10, 170, "Sets the time to noon", "Noon");
			time_evening = new Button(handler, 10, 210, "Sets the time to ~8:00 PM", "Evening");
			time_midnight = new Button(handler, 10, 250, "Sets the time to midnight", "Midnight");

			fullScreen = new Button(handler, 10, 10, "Set's the game to full size", "Full Screen");
			defScreen = new Button(handler, 10, 50, "Set's the game to default size", "Default Screen");
			increaseScreen = new Button(handler, 10, 90, "Increases screen size", "Increase Screen");
			decreaseScreen = new Button(handler, 10, 130, "Decreases screen size", "Decrease Screen");

			width = display.getCanvas().getWidth();
			height = display.getCanvas().getHeight();

			customizationState.save();

			// Add world initiator that adds decor to the world
			Initiator.initiations.add(new Initiator() {
				public void worldInit(Handler game, World w) {
					int times = (int) (World.getWidth() * Public.rand(0.4, 0.8));
					for (int i = 0; i < times; i++) {
						int x = (int) (Public.expandedRand(0, World.getWidth()));
						int y = w.randomHeight(x) - 1;
						w.getEntityManager().addEntity(new TileDecor(game, x * 18, y * 18, (int) Public.expandedRand(0, 5)),
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

			g2d = ((Graphics2D) bf.getDrawGraphics());
			g2d.clearRect(0, 0, width, height);
			g2d.setColor(Color.black);
			g2d.fillRect(0, 0, width, height);
			g2d.setColor(Color.white);
			Tran.drawString(g2d, "There was a major error when loading the game" + System.lineSeparator()
					+ "Continuing may result in an unexpected crash:" + System.lineSeparator() + Arrays.toString(e.getStackTrace()), 10,
					10);
			g2d.dispose();
			bf.show();
			bf.show();

			Console.log("Showing message for 5000");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void fullScreen() {
		float ratio = 720.0f / 405.0f;

		Toolkit t = Toolkit.getDefaultToolkit();

		double height = t.getScreenSize().getHeight() - 65;

		double width = height * ratio;

		display.setSize((int) width, (int) height + 5);
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

	private void tick() {

		width = display.getCanvas().getWidth();
		height = display.getCanvas().getHeight();

		resize_X = width / 720.0;
		resize_Y = height / 400.0;

		tickGraphs();
		space.add(Runtime.getRuntime().totalMemory(), 0);
		space.add(Runtime.getRuntime().maxMemory(), 1);

		Dialogue.tick(handler);
		achievementsDisplay.tick();
		questsDisplay.tick();
		keyManager.tick();
		mouse.changeCursor("");

		if (State.getState() != null && !paused)
			State.getState().tick();
		else if (paused)
			handler.getWorld().getEntityManager().getPlayer().tick();

		Initiator.atick(handler);

		BasicGui.tickAll();

		keyManager.postTick();
		mouse.tick();
	}

	private void render() {
		long pre = System.nanoTime(), pre2 = System.nanoTime();

		bf = display.getCanvas().getBufferStrategy();
		if (bf == null) {
			display.getCanvas().createBufferStrategy(3);
			bf = display.getCanvas().getBufferStrategy();
		}

		g2d = (Graphics2D) bf.getDrawGraphics();

		String t = OptionState.RENDER_TYPES[OptionState.renType];

		g2d.setRenderingHints(Tran.mix(Tran.antialias, Tran.textantialias,
				(t.equals("Speed") ? Tran.renderspeed : t.equals("Quality") ? Tran.renderquality : Tran.rendernormal),
				Tran.interneighbor));

		if (useResize)
			g2d.scale(width / 720.0, height / 400.0);

		time_render_creation = System.nanoTime() - pre;
		pre = System.nanoTime();

		if (State.getState() != null)
			State.getState().render(g2d);
		else {
			System.err.println("No State loaded");
			State.setState(menuState);
			State.getState().init();
		}

		renderGraphs(g2d);
		Dialogue.render(g2d, handler);
		achievementsDisplay.Render(g2d);
		questsDisplay.Render(g2d);
		BasicGui.renderAll(g2d);
		ToggleButton.renderDescription(g2d);

		time_render_drawing = System.nanoTime() - pre;
		pre = System.nanoTime();

		g2d.dispose();
		bf.show();
		if (OptionState.renType > 0)
			bf.show();

		time_render_disposing = System.nanoTime() - pre;

		renders = (System.nanoTime() - pre2);
	}

	public void forceRender() {
		render();
	}

	private void initiateGraphs() {
		time_overall = new Chart(
				new String[] { "Tick", "Render" },
				new Color[] { Color.red, Color.blue },
				"Overall", 10, 0, 100, 100);
		time_overall_render = new Chart(
				new String[] { "Creating", "Drawing", "Disposing" },
				new Color[] { Color.red, Color.blue, Color.green },
				"Rendering", 10, 200, 100, 100);
		time_game_tick = new Chart(
				new String[] { "World Ticks", "UI Ticks", "Songs Ticks", "Other Ticks" },
				new Color[] { Color.blue, Color.orange, Color.pink, Color.green },
				"In-Game Ticks", 120, 0, 100, 100);
		time_game_render = new Chart(
				new String[] { "World Renders", "UI Renders", "Other Renders" },
				new Color[] { Color.blue, Color.orange, Color.green },
				"In-Game Renders", 120, 200, 100, 100);
		time_world_tick = new Chart(
				new String[] { "Entities Tick", "Items Tick", "Environment Tick", "Water Tick", "Tiles Tick" },
				new Color[] { Color.blue, Color.green, Color.red, Color.cyan, Color.orange },
				"World Tick", 230, 0, 100, 100);
		time_world_render = new Chart(
				new String[] { "Entities Ren", "Items Render", "Environment Ren", "Water Ren", "Tiles Render" },
				new Color[] { Color.blue, Color.green, Color.red, Color.cyan, Color.orange },
				"World Render", 230, 200, 100, 100);
		time_environment_render = new Chart(
				new String[] { "Weather", "Lighting", "Coloring", "Gui/Time", "Sky" },
				new Color[] { Color.blue, Color.green, Color.red, Color.orange, Color.yellow },
				"Environment", 340, 0,100, 100);
		time_player_tick = new Chart(
				new String[] {"Animation", "Attack", "Mouse", "Tools", "Water"},
				new Color[] { Color.blue, Color.green, Color.red, Color.orange, Color.yellow },
				"Player", 450, 200, 100, 100);
		loaded = new Chart(
				new String[] { "Pre", "Vars", "Tiles", "Entities", "Generating", "Background", "Post" },
				new Color[] { Color.blue, Color.red, Color.green, Color.magenta, Color.orange, Color.pink,
						new Color(100, 0, 150) },
				"World Loading", 450, 0, 100, 100);

		spawning = new LongTimeline(
				new Color[] { Color.blue, Color.green, Color.red, Color.orange, Color.magenta },
				new String[] { "Player", "Bats", "Skunks", "Bears", "Other" }, "Entities", 140, 0, 100);
		timeline_overall = new LongTimeline(
				new Color[] { Color.red, Color.blue },
				new String[] { "Tick", "Render" }, "Overall", 10, 5, 60);
		timeline_overall_render = new LongTimeline(
				new Color[] { Color.red, Color.blue, Color.green },
				new String[] { "Creating", "Drawing", "Disposing" },
				"Rendering", 10, 205, 60);
		timeline_game_tick = new LongTimeline(
				new Color[] { Color.blue, Color.orange, Color.pink, Color.green },
				new String[] { "World Ticks", "UI Ticks", "Songs Ticks", "Other Ticks" },
				"In-Game Ticks", 160, 5, 60);
		timeline_game_render = new LongTimeline(
				new Color[] { Color.blue, Color.orange, Color.green },
				new String[] { "World Renders", "UI Renders", "Other Renders" },
				"In-Game Renders", 160, 205, 60);
		timeline_world_tick = new LongTimeline(
				new Color[] { Color.blue, Color.green, Color.red, Color.cyan, Color.orange },
				new String[] { "Entities Tick", "Items Tick", "Environment Tick", "Water Tick", "Tiles Tick" },
				"World Tick", 310, 5, 60);
		timeline_world_render = new LongTimeline(
				new Color[] { Color.blue, Color.green, Color.red, Color.cyan, Color.orange },
				new String[] { "Entities Ren", "Items Render", "Environment Ren", "Water Ren", "Tiles Render" },
				"World Render", 310, 205, 60);
		timeline_environment_render = new LongTimeline(
				new Color[] { Color.blue, Color.green, Color.red, Color.orange, Color.yellow },
				new String[] { "Weather", "Lighting", "Coloring", "Gui/Time", "Sky" },
				"Environment", 460, 5, 60);

		space = new LongTimeline(
				new Color[] { Color.red, Color.blue },
				new String[] { "Space used", "Space left" },
				"Heap Space", 580, 245, 60);

		actions = new StringTimeline(Color.black, 620, 60, 14, "Actions");
	}

	private void tickGraphs() {
		GameState g = gameState;
		timeline_overall.add(tickTicks, 0);
		timeline_overall.add(renders, 1);

		timeline_overall_render.add(time_render_creation, 0);
		timeline_overall_render.add(time_render_drawing, 1);
		timeline_overall_render.add(time_render_disposing, 2);

		timeline_game_tick.add(g.worldt, 0);
		timeline_game_tick.add(g.uit, 1);
		timeline_game_tick.add(g.songst, 2);
		timeline_game_tick.add(g.othert, 3);

		timeline_game_render.add(g.worldtr, 0);
		timeline_game_render.add(g.uir, 1);
		timeline_game_render.add(g.otherr, 2);

		timeline_world_tick.add(handler.getWorld().entities, 0);
		timeline_world_tick.add(handler.getWorld().items, 1);
		timeline_world_tick.add(handler.getWorld().enviorn, 2);
		timeline_world_tick.add(handler.getWorld().water, 3);
		timeline_world_tick.add(handler.getWorld().tilet, 4);

		timeline_world_render.add(handler.getWorld().entitiesr, 0);
		timeline_world_render.add(handler.getWorld().itemsr, 1);
		timeline_world_render.add(handler.getWorld().enviorr, 2);
		timeline_world_render.add(handler.getWorld().waterr, 3);
		timeline_world_render.add(handler.getWorld().tiler, 4);

		timeline_environment_render.add(Environment.weather, 0);
		timeline_environment_render.add(Environment.lighting, 1);
		timeline_environment_render.add(Environment.lightcoloring, 2);
		timeline_environment_render.add(Environment.guitime, 3);
		timeline_environment_render.add(Environment.sky, 4);

		spawning.add(EntityManager.playert, 0);
		spawning.add(EntityManager.bat, 1);
		spawning.add(EntityManager.skunk, 2);
		spawning.add(EntityManager.bear, 3);
		spawning.add(EntityManager.othert, 4);

		if (KeyManager.function[0]) {
			time_game_tick.update(new double[] { g.worldt, g.uit, g.songst, g.othert });
			time_game_render.update(new double[] { g.worldtr, g.uir, g.otherr });
			time_overall.update(new double[] { tickTicks, renders });
			time_overall_render.update(new double[] {time_render_creation, time_render_drawing, time_render_disposing});
			time_world_tick.update(new double[] { handler.getWorld().entities, handler.getWorld().items,
					handler.getWorld().enviorn, handler.getWorld().water, handler.getWorld().tilet });

			time_world_render.update(new double[] { handler.getWorld().entitiesr, handler.getWorld().itemsr,
					handler.getWorld().enviorr, handler.getWorld().waterr, handler.getWorld().tiler });

			time_environment_render.update(new double[] { Environment.weather, Environment.lighting, Environment.lightcoloring,
					Environment.guitime, Environment.sky });

			time_player_tick.update(new double[] { Player.animationt, Player.attackt, Player.mouset, Player.toolst, Player.watert});

			loaded.update(new double[] { handler.getWorld().preload, handler.getWorld().varsload,
					handler.getWorld().tilesload, handler.getWorld().customload, handler.getWorld().addingload,
					handler.getWorld().backgroundload, handler.getWorld().postload });
		}

		if (KeyManager.function[2]) {
			weather_rain.tick();
			weather_storm.tick();
			weather_clear.tick();

			time_morning.tick();
			time_noon.tick();
			time_evening.tick();
			time_midnight.tick();

			if (weather_rain.on) {
				handler.getEnvironment().setWeather(1);
			} else if (weather_storm.on) {
				handler.getEnvironment().setWeather(2);
			} else if (weather_clear.on) {
				handler.getEnvironment().setWeather(0);
			}

			if (time_morning.on) {
				handler.getEnvironment().setTime(420 * 60);
			} else if (time_noon.on) {
				handler.getEnvironment().setTime(11 * 60 * 60);
			} else if (time_evening.on) {
				handler.getEnvironment().setTime(1100 * 60);
			} else if (time_midnight.on) {
				handler.getEnvironment().setTime(0);
			}
		}

		if (KeyManager.function[3]) {
			fullScreen.tick();
			defScreen.tick();
			increaseScreen.tick();
			decreaseScreen.tick();

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

	public void renderGraphs(Graphics2D g) {
		if (KeyManager.function[0]) {
			g.setColor(new Color(0, 0, 50, 100));
			g.fillRect(0, 0, width, height);
			mouse.visualize(g);
			time_overall.render(g);
			time_overall_render.render(g);
			time_game_tick.render(g);
			time_game_render.render(g);
			time_world_tick.render(g);
			time_world_render.render(g);
			time_environment_render.render(g);
			time_player_tick.render(g);
			loaded.render(g);

			Environment.lightingBreakdown.render(g);

			g.setFont(Public.defaultBoldFont);
			Tran.drawOutlinedText(g, 720 - 50, 20, "TPS: " + fullTicks, 1.0, Color.black, Color.white);
			Tran.drawOutlinedText(g, 720 - 50, 35, "FPS: " + fullRens, 1.0, Color.black, Color.white);
			Tran.drawOutlinedText(g, 720 - 70, 50, stable ? "STABLE" : "NOT STABLE", 1.0, Color.black, Color.white);
		}

		if (KeyManager.function[4]) {
			g.setFont(Public.defaultBoldFont);
			Tran.drawOutlinedText(g, 720 - 50, 20, "TPS: " + fullTicks, 1.0, Color.black, Color.white);
			Tran.drawOutlinedText(g, 720 - 50, 35, "FPS: " + fullRens, 1.0, Color.black, Color.white);
			Tran.drawOutlinedText(g, 720 - 70, 50, stable ? "STABLE" : "NOT STABLE", 1.0, Color.black, Color.white);
		}

		if (KeyManager.function[1]) {
			g.setColor(new Color(0, 0, 50, 100));
			g.fillRect(0, 0, width, height);

			timeline_overall.render(g);
			timeline_overall_render.render(g);
			timeline_game_tick.render(g);
			timeline_game_render.render(g);
			timeline_world_tick.render(g);
			timeline_world_render.render(g);
			timeline_environment_render.render(g);

			actions.render(g);

			space.render(g);

			g.setFont(Public.defaultBoldFont);
			g.setColor(Color.white);
			Tran.drawOutlinedText(g, 720 - 50, 20, "TPS: " + fullTicks, 1.0, Color.black, Color.white);
			Tran.drawOutlinedText(g, 720 - 50, 35, "FPS: " + fullRens, 1.0, Color.black, Color.white);
			Tran.drawOutlinedText(g, 720 - 70, 50, stable ? "STABLE" : "NOT STABLE", 1.0, Color.black, Color.white);
		}

		if (KeyManager.function[2]) {
			weather_rain.render(g);
			weather_storm.render(g);
			weather_clear.render(g);

			time_morning.render(g);
			time_noon.render(g);
			time_evening.render(g);
			time_midnight.render(g);
		}

		if (KeyManager.function[3]) {
			fullScreen.render(g);
			defScreen.render(g);
			increaseScreen.render(g);
			decreaseScreen.render(g);
		}

		if (KeyManager.function[11]) {
			long pre = System.nanoTime();
			int i = 0;
			Map<String, Long> t = handler.getEntityManager().totalr, a = handler.getEntityManager().averager,
					n = handler.getEntityManager().numberr;
			g.setFont(Public.defaultFont);
			if (t != null && !t.isEmpty())
				for (String e : t.keySet()) {
					Tran.drawOutlinedText(g, 4.0, 14 + i * 14, e + " averages " + a.get(e) + "mu", 1, Color.black,
							Color.white);
					Tran.drawOutlinedText(g, 254.0, 14 + i * 14, "with a total of " + t.get(e) + "mu", 1, Color.black,
							Color.white);
					Tran.drawOutlinedText(g, 504.0, 14 + i * 14, "for " + n.get(e) + " entities (" + e + ")", 1,
							Color.black, Color.white);
					i++;
				}

			if (a != null && !a.isEmpty() && a.containsKey("TICK TIME")) {
				Tran.drawOutlinedText(g, 4.0, 24 + i * 14, "Time to calculate this was " + a.get("TICK TIME") + "mu", 1,
						Color.black, Color.white);
				i++;
			}
			Tran.drawOutlinedText(g, 4.0, 24 + i * 14,
					"Time took to render this was " + ((System.nanoTime() - pre) / 1000) + "mu", 1, Color.black,
					Color.white);

		}

	}

	public synchronized void start() {
		if (running)
			return;
		running = true;
		thread = new Thread(this, "Main");
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	public synchronized void stop() {
		if (!running)
			return;
		log.out("Terminated");
		display.stop();

		running = false;
		System.exit(0);
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		init();
		optionState.tick();

		double delta = 0.0D, renderDelta = 0.0D;

		long lastTime = System.nanoTime(), timer = 0L;

		while (running) {
			long now = System.nanoTime();

			delta += (now - lastTime) / timePerTick;
			renderDelta += (now - lastTime) / renderPerTick;
			timer += now - lastTime;
			lastTime = now;

			if (delta >= 1.0D) {
				long pre = System.nanoTime();
				try {
					tick();
				} catch (ConcurrentModificationException e) {
					// Concurrent modification exception is unimportant and can be ignored
				} catch (Exception e) {
					e.printStackTrace();
				}
				ticks += 1;
				delta = 0.0D;
				tickTicks = (System.nanoTime() - pre);
				Renderer.GATE = true;
			}

			if (renderDelta >= 1.0D || optionState.getIntSlider("Game control", 0)==1) {
				if(!OptionState.split_stream_render)
					try {
						render();
					} catch (Exception e) {
						System.err.println("Error in render exception");
						e.printStackTrace();
					}
				renTicks += 1;
				renderDelta = 0.0D;
			}

			if (timer >= 1000000000L) {
				fullTicks = ticks;
				fullRens = (int) Renderer.frames;
				if(!OptionState.split_stream_render)
					fullRens = renTicks;

				stable = (((Public.difference(preTicks, prePreTicks) < 2.0D)
						&& (Public.difference(preTicks, ticks) < 2.0D) && (Public.difference(ticks, fps) < 5.0D))
						|| (ticks < Public.range(0.0D, fps, fps - 20)));

				prePreTicks = preTicks;
				preTicks = ticks;

				ticks = 0;
				renTicks = 0;
				timer = 0L;
				Renderer.frames = 0L;
			}
		}

		System.out.println("OOPSIES");

		stop();
	}

	// Getters and setters (java moment)
	public Display getDisplay() {
		return display;
	}

	public void setTps(int tps) {
		this.tps = tps;
		timePerTick = (1000000000.0 / tps);
	}

	public void setFps(int fps) {
		this.fps = fps;
		renderPerTick = (1000000000.0 / fps);
	}

	public void useResize(boolean tf) {
		useResize = tf;
	}

	public void minScreen() {
		display.setSize(720, 405);
	}

	public KeyManager getKeyManager() {
		return keyManager;
	}

	public MouseManager getMouse() {
		return mouse;
	}

	public GameCamera getGameCamera() {
		return gameCamera;
	}

	public Graphics2D get2D() {
		return g2d;
	}

	public void pause() {
		paused = true;
	}

	public void unPause() {
		paused = false;
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

		Game g = ArvopiaLauncher.game;
		while (true) {
			
			long now = System.nanoTime();
			delta += (now - lastTime) / timePerTick;
			lastTime = now;


			if (g != null && g.getDisplay() != null && GATE && (delta>=1 || g.optionState.getIntSlider("Game control", 0)==1) && OptionState.split_stream_render) {
				try {
					g.forceRender();
				} catch (ConcurrentModificationException e) {
					// Concurrent Modification is unimportant and can be ignored
				} catch (Exception e) {
					e.printStackTrace();
				}
				frames++;
				delta = 0;
				timePerTick = g.renderPerTick;
			} else {
				System.out.print("");
				g = ArvopiaLauncher.game;
				if(!OptionState.split_stream_render)
					return;
			}
		}
	}

}
