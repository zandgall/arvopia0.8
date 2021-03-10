package com.zandgall.arvopia.worlds;

import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Initiator;
import com.zandgall.arvopia.Reporter;
import com.zandgall.arvopia.entity.AreaAdders;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.entity.EntityAdder;
import com.zandgall.arvopia.entity.EntityManager;
import com.zandgall.arvopia.entity.creatures.Bee;
import com.zandgall.arvopia.entity.creatures.Butterfly;
import com.zandgall.arvopia.entity.creatures.Cannibal;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.entity.creatures.basic.*;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.entity.creatures.npcs.Fawncier;
import com.zandgall.arvopia.entity.creatures.npcs.Frizzy;
import com.zandgall.arvopia.entity.creatures.npcs.Template;
import com.zandgall.arvopia.entity.moveableStatics.Cloud;
import com.zandgall.arvopia.entity.statics.Flower;
import com.zandgall.arvopia.entity.statics.Shrubbery;
import com.zandgall.arvopia.entity.statics.Stone;
import com.zandgall.arvopia.entity.statics.Tree;
import com.zandgall.arvopia.enviornment.Enviornment;
import com.zandgall.arvopia.enviornment.LightingEffects;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.input.KeyManager;
import com.zandgall.arvopia.items.Item;
import com.zandgall.arvopia.items.ItemManager;
import com.zandgall.arvopia.particles.ParticleManager;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.quests.AchievementManager;
import com.zandgall.arvopia.quests.Quest;
import com.zandgall.arvopia.quests.QuestManager;
import com.zandgall.arvopia.state.GameState;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.state.State;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.tiles.backtile.Backtile;
import com.zandgall.arvopia.tiles.build.Building;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Noise;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Slider;
import com.zandgall.arvopia.utils.Utils;
import com.zandgall.arvopia.water.WaterManager;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class World implements Serializable {

	private static final long serialVersionUID = 1125716483561321653L;

	public int state = 0;

	public long entities = 1;
	public long items = 1;
	public long enviorn = 1;
	public long water = 1;
	public long tilet = 1;
	public long entitiesr = 1;
	public long itemsr = 1;
	public long enviorr = 1;
	public long waterr = 1;
	public long tiler = 1;

	public long preload = 1;
	public long varsload = 1;
	public long tilesload = 1;
	public long customload = 1;
	public long backgroundload = 1;
	public long addingload = 1;
	public long postload = 1;

	public long sFlowers = 1, sStones = 1, sTrees = 1, sOther = 1;

	private Enviornment enviornment;
	private Handler handler;
	private static int width;
	private static int height;
	private int spawnx;
	private int spawny;
	private static String[][] tiles;
	private static String[][] backtiles;
	public int bee;
	public int butterfly;
	public int fox, wolf, bear, bat, skunk, fairy;
	public int stone0;
	public int stone1;
	public int stone2;
	public int flower0;
	public int flower1;
	public int flower2;
	public int youngTrees;
	public int midTrees;
	public int oldTrees;
	public int cloud0;
	public int cloud1;
	public int cloud2;
	public int cloud3;
	public int cloudY;
	public int cannibal;
	private int maxBee;
	private int maxButterfly;
	private int maxFox, maxWolf, maxBear, maxBat, maxSkunk, maxFairy;
	private int maxStone;
	private int maxFlower;
	private int maxTrees;
	private int maxCannibals;
	
	public int shrubbery = 0;
	int rencount = 0;

	boolean waitingForCreature;

	private Button respawn;

	private boolean dead;

	private EntityManager entityManager;
//	Mapper<String, Integer> entitiesData;
	java.util.Map<String, Integer> maxEntities, origEntities;

	public Entity center;

	private boolean Box = false;

	private boolean loading;
	public double percentDone = 0.0D;

	private ArrayList<ArrayList<Integer>> heights;

	private ArrayList<ArrayList<Integer>> tops;

	private ArrayList<ArrayList<ArrayList<Integer>>> shrubs; // x-->index--> y, id

	com.zandgall.arvopia.entity.creatures.npcs.Lia Lia;

	private ItemManager itemManager;

	private WaterManager waterManager;

	private ParticleManager particleManager;

	public String save = "";

	Map map;

	BufferedImage background;

	ArrayList<BufferedImage> backgroundImages;
	ArrayList<BufferedImage> backgroundSnowOverlays;
	BufferedImage foreground;

	BufferedImage backgroundSnowOverlay;

//	ArrayList<Building> builtTiles;

	String type = "World", path = "";

	public World(Handler handler, String path) {
		
		handler.getGame().initMessage("Starting world...");
		
		long pre = System.nanoTime();
		this.handler = handler;
		percentDone = 0.0D;
		
		map = new Map(handler);
		
		handler.getGame().initMessage("Creating player...");
		
		Player p = new Player(handler, 100.0D, 0.0D, false, 2.0D, 3);
		
		handler.getGame().initMessage("Starting Entity managing...");
		
		entityManager = new EntityManager(handler, p);
		
//		entitiesData = new Mapper<String, Integer>();
		maxEntities = new HashMap<String, Integer>();
		origEntities = new HashMap<String, Integer>();
		
		handler.getGame().initMessage("Starting Items, water, and particles...");
		
		itemManager = new ItemManager(handler, entityManager.getPlayer());
		waterManager = new WaterManager(handler);
		particleManager = new ParticleManager(handler);

//		builtTiles = new ArrayList<Building>();
		
		handler.getGame().initMessage("...");

		center = entityManager.getPlayer();
		waitingForCreature = false;

		loading = true;

		respawn = new Button(handler, handler.getWidth() / 2, handler.getHeight() / 2, "Respawns the character",
				"Respawn", 1);

		com.zandgall.arvopia.entity.creatures.Creature.init();

		handler.getGame().initMessage("Loading mods...");
		
		handler.loadMods(entityManager);

		handler.getGame().initMessage("Loading world...");
		preload = System.nanoTime() - pre;
		handler.log("\t\t\t" + entityManager.adders.size());
		this.path = path;
		System.out.println("\t\t\tPacking " + path);
		if (path == "generation") {
			generateWorld(true, 200, 100, 1f, 1f, 1f, 1f, 0.2f, 2);
			type = "Generation";
		} else if (path.contains("\\Pack")) {
			handler.loadMod(path + "\\mods", entityManager);
			loadWorld(path + "\\world.arv");
			type = "Pack";
		} else {
			loadWorld(path);
		}

		enviornment.setupStars();

		pre = System.nanoTime();
		Tile.set(width, height);

//		entityManager.addEntity(GnomePackage.addGnome(handler, (width - 20) * 18,
//				getLowest(Public.range(0.0D, width * Tile.TILEWIDTH, (width - 20) * Tile.TILEWIDTH)) * Tile.TILEHEIGHT), true);

		handler.getGame().initMessage("Loading Custom Entities...");
		postload = System.nanoTime() - pre;
		pre = System.nanoTime();

		addShrubbery(10);

		addTrees(youngTrees, 0, 5);
		addTrees(midTrees, 6, 10);
		addTrees(oldTrees, 11, 15);

		addCloud(cloud0, 0);
		addCloud(cloud1, 1);
		addCloud(cloud2, 2);
		addCloud(cloud3, 3);
		addFox(fox);
		addWolf(wolf);
		addBear(bear);
		addBat(bat);
		addSkunk(skunk);
		addFairy(fairy);
		addBee(bee, 100000L);
		addButterfly(butterfly, 100000L);
		addStone(stone0, 0);
		addStone(stone1, 1);
		addStone(stone2, 2);
		addFlower(flower0, 2);
		addFlower(flower1, 1);
		addFlower(flower2, 0);
		addCannibalTribe(cannibal, (int) (Math.random()*width*18));

		for (EntityAdder a : entityManager.adders) {
			if (origEntities.get(a.name) != null) {
				addEntities(origEntities.get(a.name), a);
			}
		}

		entityManager.getPlayer().setX(spawnx);
		entityManager.getPlayer().setY(spawny);

		handler.getGame().initMessage("Loading Background...");
		addingload = System.nanoTime() - pre;
		pre = System.nanoTime();

//		entityManager.addEntity(NPC.loadMODNPC("C:\\Arvopia\\Mods", handler, spawnx, spawny - 40), true);

		backgroundImages = new ArrayList<BufferedImage>();
		backgroundSnowOverlays = new ArrayList<BufferedImage>();

		BufferedImage[] bg1 = createBackground(3);
		backgroundImages.add(bg1[0]);
		backgroundSnowOverlays.add(bg1[1]);
		Noise.init((long) Public.random(0, 100000));
		BufferedImage[] bg2 = createBackground(4);
		backgroundImages.add(bg2[0]);
		backgroundSnowOverlays.add(bg2[1]);
		Noise.init((long) Public.random(0, 100000));
		BufferedImage[] bg3 = createBackground(5);
		backgroundImages.add(bg3[0]);
		backgroundSnowOverlays.add(bg3[1]);
		backgroundload = System.nanoTime() - pre;
	}

	public World(Handler handler, int width, int height, float foliage, float stones, float insects, float creatures,
			float cannibals, int caves) {
		long pre = System.nanoTime();
		this.handler = handler;
		percentDone = 0.0D;

		map = new Map(handler);

		entityManager = new EntityManager(handler, new Player(handler, 100.0D, 0.0D, false, 2.0D, 3));
//		entitiesData = new Mapper<String, Integer>();
		maxEntities = new HashMap<String, Integer>();
		origEntities = new HashMap<String, Integer>();
		itemManager = new ItemManager(handler, entityManager.getPlayer());
		waterManager = new WaterManager(handler);
		particleManager = new ParticleManager(handler);

//		builtTiles = new ArrayList<Building>();

		center = entityManager.getPlayer();
		waitingForCreature = false;

		loading = true;

		respawn = new Button(handler, handler.getWidth() / 2 - 50, handler.getHeight() / 2 - 25, 100, 25,
				"Respawns the character", "Respawn");

		com.zandgall.arvopia.entity.creatures.Creature.init();

		preload = System.nanoTime() - pre;

		type = "Generation";
		generateWorld(true, width, height, foliage, stones, insects, creatures, cannibals, caves);

		pre = System.nanoTime();
		Tile.set(width, height);

		enviornment.setupStars();

		highestTile();

		handler.loadMods(entityManager);

//		entityManager.addEntity(GnomePackage.addGnome(handler, (width - 20) * 18,
//				getLowest(Public.range(0.0D, width * Tile.TILEWIDTH, (width - 20) * Tile.TILEWIDTH)) * Tile.TILEHEIGHT), true);

		handler.getGame().initMessage("Loading Custom Entities...");
		postload = System.nanoTime() - pre;
		pre = System.nanoTime();

		addShrubbery(10);

		addTrees(youngTrees, 0, 5);
		addTrees(midTrees, 6, 10);
		addTrees(oldTrees, 11, 15);

		addCloud(cloud0, 0);
		addCloud(cloud1, 1);
		addCloud(cloud2, 2);
		addCloud(cloud3, 3);
		addFox(fox);
		addWolf(wolf);
		addBear(bear);
		addBat(bat);
		addSkunk(skunk);
		addFairy(fairy);
		addBee(bee, 100000L);
		addButterfly(butterfly, 100000L);
		addStone(stone0, 0);
		addStone(stone1, 1);
		addStone(stone2, 2);
		addFlower(flower0, 2);
		addFlower(flower1, 1);
		addFlower(flower2, 0);
		entityManager.getPlayer().setX(spawnx);
		entityManager.getPlayer().setY(spawny);

		for (EntityAdder a : entityManager.adders) {
			if (origEntities.containsKey(a.name)) {
				addEntities(origEntities.get(a.name), a);
			}
		}

		handler.getGame().initMessage("Making Background...");
		addingload = System.nanoTime() - pre;
		pre = System.nanoTime();

		backgroundImages = new ArrayList<BufferedImage>();
		backgroundSnowOverlays = new ArrayList<BufferedImage>();

		BufferedImage[] bg1 = createBackground(3);
		backgroundImages.add(bg1[0]);
		backgroundSnowOverlays.add(bg1[1]);
		Noise.init((long) Public.random(0, 100000));
		BufferedImage[] bg2 = createBackground(4);
		backgroundImages.add(bg2[0]);
		backgroundSnowOverlays.add(bg2[1]);
		Noise.init((long) Public.random(0, 100000));
		BufferedImage[] bg3 = createBackground(5);
		backgroundImages.add(bg3[0]);
		backgroundSnowOverlays.add(bg3[1]);

		backgroundload = System.nanoTime() - pre;
	}

	public World(Handler handler, ArrayList<Slider> sliders) {
		long pre = System.nanoTime();
		this.handler = handler;
		percentDone = 0.0D;

		map = new Map(handler);

		entityManager = new EntityManager(handler, new Player(handler, 100.0D, 0.0D, false, 2.0D, 3));
//		entitiesData = new Mapper<String, Integer>();
		maxEntities = new HashMap<String, Integer>();
		origEntities = new HashMap<String, Integer>();
		itemManager = new ItemManager(handler, entityManager.getPlayer());
		waterManager = new WaterManager(handler);
		particleManager = new ParticleManager(handler);

//		builtTiles = new ArrayList<Building>();

		center = entityManager.getPlayer();
		waitingForCreature = false;

		loading = true;

		respawn = new Button(handler, handler.getWidth() / 2 - 50, handler.getHeight() / 2 - 25, 100, 25,
				"Respawns the character", "Respawn");

		com.zandgall.arvopia.entity.creatures.Creature.init();

		preload = System.nanoTime() - pre;

		type = "Generation";
		generateWorld(true, sliders);

		pre = System.nanoTime();
		Tile.set(width, height);

		enviornment.setupStars();

		highestTile();

		handler.loadMods(entityManager);

//		entityManager.addEntity(GnomePackage.addGnome(handler, (width - 20) * 18,
//				getLowest(Public.range(0.0D, width * Tile.TILEWIDTH, (width - 20) * Tile.TILEWIDTH)) * Tile.TILEHEIGHT), true);

		handler.getGame().initMessage("Loading Custom Entities...");
		postload = System.nanoTime() - pre;
		pre = System.nanoTime();

		addShrubbery(10);

		addTrees(youngTrees, 0, 5);
		addTrees(midTrees, 6, 10);
		addTrees(oldTrees, 11, 15);

		addCloud(cloud0, 0);
		addCloud(cloud1, 1);
		addCloud(cloud2, 2);
		addCloud(cloud3, 3);
		addFox(fox);
		addWolf(wolf);
		addBear(bear);
		addBat(bat);
		addSkunk(skunk);
		addFairy(fairy);
		addBee(bee, 100000L);
		addButterfly(butterfly, 100000L);
		addStone(stone0, 0);
		addStone(stone1, 1);
		addStone(stone2, 2);
		addFlower(flower0, 2);
		addFlower(flower1, 1);
		addFlower(flower2, 0);
		entityManager.getPlayer().setX(spawnx);
		entityManager.getPlayer().setY(spawny);

		for (EntityAdder a : entityManager.adders) {
			if (origEntities.containsKey(a.name)) {
				addEntities(origEntities.get(a.name), a);
			}
		}

		handler.getGame().initMessage("Making Background...");
		addingload = System.nanoTime() - pre;
		pre = System.nanoTime();

		backgroundImages = new ArrayList<BufferedImage>();
		backgroundSnowOverlays = new ArrayList<BufferedImage>();

		BufferedImage[] bg1 = createBackground(3);
		backgroundImages.add(bg1[0]);
		backgroundSnowOverlays.add(bg1[1]);
		Noise.init((long) Public.random(0, 100000));
		BufferedImage[] bg2 = createBackground(4);
		backgroundImages.add(bg2[0]);
		backgroundSnowOverlays.add(bg2[1]);
		Noise.init((long) Public.random(0, 100000));
		BufferedImage[] bg3 = createBackground(5);
		backgroundImages.add(bg3[0]);
		backgroundSnowOverlays.add(bg3[1]);

		backgroundload = System.nanoTime() - pre;
	}

	public World(Handler handler) {
		long pre = System.nanoTime();
		this.handler = handler;
		percentDone = 0.0D;

		map = new Map(handler);

		entityManager = new EntityManager(handler, new Player(handler));
//		entitiesData = new Mapper<String, Integer>();
		maxEntities = new HashMap<String, Integer>();
		origEntities = new HashMap<String, Integer>();
//		itemManager = new ItemManager(handler);
//		waterManager = new WaterManager(handler);
//		particleManager = new ParticleManager(handler);

		handler.loadMods(entityManager);

//		builtTiles = new ArrayList<Building>();

//		center = entityManager.getPlayer();
		waitingForCreature = false;

		loading = true;

		respawn = new Button(handler, handler.getWidth() / 2 - 50, handler.getHeight() / 2 - 25, 100, 25,
				"Respawns the character", "Respawn");

//		com.zandgall.arvopia.entity.creatures.Creature.init();

		preload = System.nanoTime() - pre;

		enviornment = new Enviornment(handler);
	}

	public void generateWorld(boolean beginning) {

		if (beginning) {
			GameState gset = (GameState) State.getState();
			gset.setLoadingPhase(1);
			State.setState(gset);
		}
		handler.getGame().initMessage("Loading Variables...");

		long pre = System.nanoTime();

		width = (int) Public.random(100.0D, 300.0D);
		height = (int) Public.random(50.0D, 100.0D);

		stone0 = ((int) Public.random(width / 50, width / 40));
		stone1 = ((int) Public.random(width / 50, width / 40));
		stone2 = ((int) Public.random(width / 50, width / 40));
		flower0 = ((int) Public.random(width / 50, width / 40));
		flower1 = ((int) Public.random(width / 50, width / 40));
		flower2 = ((int) Public.random(width / 50, width / 40));
		youngTrees = ((int) Public.random(width / 50, width / 40));
		midTrees = ((int) Public.random(width / 50, width / 40));
		oldTrees = ((int) Public.random(width / 45, width / 30));

		bee = ((int) Public.random(width / 50, width / 40));
		butterfly = ((int) Public.random(width / 50, width / 40));
		fox = ((int) Public.random(width / 40, width / 10));
		cannibal = ((int) Public.random(width / 100, width / 50));

		maxStone = ((int) Public.random(width, width * 2));
		maxFlower = ((int) Public.random(width, width * 2));
		maxTrees = ((int) Public.random(width, width * 2));

		maxBee = ((int) Public.random(width / 20, width / 10));
		maxButterfly = ((int) Public.random(width / 20, width / 10));
		maxFox = ((int) Public.random(width / 10, width / 5));
		maxCannibals = ((int) Public.random(width / 50, width / 40));

		cloud0 = ((int) Public.random(width / 50, width / 20));
		cloud1 = ((int) Public.random(width / 50, width / 20));
		cloud2 = ((int) Public.random(width / 50, width / 20));
		cloud3 = ((int) Public.random(width / 50, width / 20));
		cloudY = ((int) Public.random(height / 4, height / 2));

		varsload = System.nanoTime() - pre;
		pre = System.nanoTime();

		handler.getGame().initMessage("Loading Tiles...");

		double y = cloudY;

		tiles = new String[width][height];

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = "TILE" + 0;
			}
		}

		int biome = 1;

		for (int i = 0; i < width; i++) {
			tiles[i][((int) Math.max(0.0D, y))] = "TILE" + 2;

			for (int j = (int) y + 1; j < height; j++) {
				tiles[i][Math.max(0, j)] = "TILE" + 5;
			}

			y = Noise.noise1(i);

			if (Public.chance(1.0D)) {
				biome = (int) Public.random(1.0D, 5.0D);
				System.out.println("Biome: " + biome);
			}
		}

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = formatTiles(tiles, i, j);
			}
		}
		tilesload = System.nanoTime() - pre;

		highestTile();

		spawnx = 180;
		spawny = getHighest(180.0D);

		enviornment = new Enviornment(handler, Public.random(1.0D, 5.0D), Public.random(1.0D, 5.0D),
				Public.random(50.0D, 100.0D));
	}

	public void generateWorld(boolean beginning, int width, int height, float foliation, float stones, float insects,
			float creatures, float cannibals, int caves) {

		if (beginning) {
			GameState gset = (GameState) State.getState();
			gset.setLoadingPhase(1);
			State.setState(gset);
		}
		handler.getGame().initMessage("Loading Variables...");

		long pre = System.nanoTime();

		World.width = width;
		World.height = height;

		stone0 = (int) (width * stones * 0.1);
		stone1 = (int) (width * stones * 0.1);
		stone2 = (int) (width * stones * 0.1);
		flower0 = (int) (width * foliation * 0.1);
		flower1 = (int) (width * foliation * 0.1);
		flower2 = (int) (width * foliation * 0.1);
		youngTrees = (int) (width * foliation * 0.1);
		midTrees = (int) (width * foliation * 0.1);
		oldTrees = (int) ((width / 45) * foliation * 0.1);

		bee = (int) (width * insects * 0.1);
		butterfly = (int) (width * insects * 0.1);
		fairy = (int) (width * insects * 0.1);

		fox = (int) (width * creatures * 0.1);
		wolf = (int) (width * creatures * 0.1);
		bear = (int) (width * creatures * 0.1);
		bat = (int) (width * creatures * 0.1);
		skunk = (int) (width * creatures * 0.1);

		cannibal = (int) ((width / 100) * cannibals * 0.1);

		maxStone = (int) (width * stones * 0.1);
		maxFlower = (int) (width * stones * 0.1);
		maxTrees = (int) (width * stones * 0.1);

		maxBee = (int) (width * insects * 0.1);
		maxButterfly = (int) (width * insects * 0.1);
		maxFairy = (int) (width * insects * 0.1);

		maxFox = (int) (width * creatures * 0.1);

		maxCannibals = (int) (width * creatures * 0.1);

		cloud0 = ((int) Public.random(width / 50, width / 20));
		cloud1 = ((int) Public.random(width / 50, width / 20));
		cloud2 = ((int) Public.random(width / 50, width / 20));
		cloud3 = ((int) Public.random(width / 50, width / 20));
		cloudY = ((int) Public.random(height / 4, height / 2));

		varsload = System.nanoTime() - pre;
		pre = System.nanoTime();

		handler.getGame().initMessage("Loading Tiles...");

		int bh = height * 18;
		double c = Public.debugRandom(-1.0D, 1.0D);
		double y = (Noise.noise1(c) * 10 + (bh / 72));

		tiles = new String[width][height];
		backtiles = new String[width][height];

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = "TILE0";
				backtiles[i][j] = "TILE0";
			}
		}

		// CAVES

		int biome = 1;
		double offset = 0;

		for (int i = 0; i < width; i++) {
			tiles[i][((int) Math.max(2.0D, Math.min(y, tiles[i].length - 2)))] = "TILE2";

			for (int j = (int) y + 1; j < height; j++) {
				tiles[i][Math.max(0, j)] = "TILE5";
			}

			c += 0.02;

			y = (Noise.noise1(c) * 10 * biome + (bh / 72)) + offset;

			if (Public.chance(1.0D)) {
				biome = (int) Public.random(1.0D, 5.0D);
				double original = (y - (bh / 72));
				double newer = (Noise.noise1(c) * 20 * biome);
				offset = original - newer;
				System.out.println("Biome: " + biome);
			}
		}

		for (int i = 0; i < caves; i++) {
			Noise n = new Noise((long) (Math.random() * 10000));

			double radius = 0;
			double momentum = Math.random() * 1000;
			double xc = Math.random() * (width);
			double yc = 0;
			while (radius < 10) {
				xc = Public.range(0, width, xc);
				xc += n.get1(momentum) * 2;
				yc = Math.max(yc, 0);
				yc += (n.get1(-momentum) + 0.1) * 0.4;

				// int r = (n.get1(radius)>-0.25 ? 3 : 0);
				int r = 5;
				for (double px = xc - r; px < xc + r; px++) {
					for (double py = yc - r; py < yc + r; py++) {
						int tx = (int) Public.range(0, tiles.length - 1, px);
						int ty = (int) Public.range(0, tiles[0].length - 1, py);
						int cx = (int) Public.range(0, tiles.length - 1, xc);
						int cy = (int) Public.range(0, tiles[0].length - 1, yc);

						if (tiles[tx][ty] != "TILE0")
							backtiles[tx][ty] = "TILE5";
						if (Public.dist(tx, ty, cx, cy) <= r - 2)
							tiles[tx][ty] = "TILE0";
					}
				}

				momentum += 0.0025;
				radius += 0.0025;
			}
		}

		// tiles[i][((int) Math.max(0.0D, Math.min(y, tiles[i].length - 2)))] = 2;
		//
		// for (int j = (int) y + 1; j < height; j++) {
		// tiles[i][Math.max(0, j)] = 5;
		// }
		//
		// }

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = formatTiles(tiles, i, j);
				backtiles[i][j] = formatBacktiles(backtiles, i, j);
			}
		}
		tilesload = System.nanoTime() - pre;

		highestTile();

		spawnx = 180;
		spawny = getHighest(180.0D);

		enviornment = new Enviornment(handler, Public.random(1.0D, 5.0D), Public.random(1.0D, 5.0D),
				Public.random(50.0D, 100.0D));
	}

	public void generateWorld(boolean beginning, ArrayList<Slider> sliders) {

		if (beginning) {
			GameState gset = (GameState) State.getState();
			gset.setLoadingPhase(1);
			State.setState(gset);
		}
		handler.getGame().initMessage("Loading Variables...");

		long pre = System.nanoTime();

		World.width = 200;
		World.height = 100;

		int caves = 0;

		for (Slider s : sliders) {
			if (s.getName().contains("Width"))
				World.width = s.getValue();
			else if (s.getName().contains("Height"))
				World.height = s.getValue();
			else if (s.getName().contains("Caves"))
				caves = s.getValue();
		}

		float stones = 0, foliation = 0, insects = 0, creatures = 0, cannibals = 0;

		cloud0 = ((int) Public.random(width / 50, width / 20));
		cloud1 = ((int) Public.random(width / 50, width / 20));
		cloud2 = ((int) Public.random(width / 50, width / 20));
		cloud3 = ((int) Public.random(width / 50, width / 20));
		cloudY = ((int) Public.random(height / 4, height / 2));

		varsload = System.nanoTime() - pre;
		pre = System.nanoTime();

		handler.getGame().initMessage("Loading Tiles...");

		int bh = height * 18;
		double c = Public.debugRandom(-1.0D, 1.0D);
		double y = (Noise.noise1(c) * 10 + (bh / 72));

		tiles = new String[width][height];
		backtiles = new String[width][height];

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = "TILE0";
			}
		}

		// CAVES

		int biome = 1;
		double offset = 0;

		for (int i = 0; i < width; i++) {
			tiles[i][((int) Math.max(2.0D, Math.min(y, tiles[i].length - 2)))] = "TILE" + 2;

			for (int j = (int) y + 1; j < height; j++) {
				tiles[i][Math.max(0, j)] = "TILE" + 5;
			}

			c += 0.02;

			y = (Noise.noise1(c) * 10 * biome + (bh / 72)) + offset;

			if (Public.chance(1.0D)) {
				biome = (int) Public.random(1.0D, 5.0D);
				double original = (y - (bh / 72));
				double newer = (Noise.noise1(c) * 20 * biome);
				offset = original - newer;
				System.out.println("Biome: " + biome);
			}
		}

		for (Slider s : sliders) {
			String e = s.getName();

			for (EntityAdder a : entityManager.adders) {
				if (a.name.contains(e) && e.contains(a.name))
					maxEntities.put(a.name, (int) (width * s.getWholeValue() * 0.1));
//					maxEntities.put(a.name, (int) (width * d[contInt + 1] * 0.1));
			}

			if (e == "Stones")
				stones = s.getValue();
			if (e == "Foliation")
				foliation = s.getValue();
			if (e == "Creatures")
				creatures = s.getValue();
			if (e == "Insects")
				insects = s.getValue();
			if (e == "Cannibals")
				cannibals = s.getValue();
		}

		for (EntityAdder a : entityManager.adders) {
			if (maxEntities.containsKey(a.name)) {
				origEntities.put(a.name, maxEntities.get(a.name) / 4);
			}
		}

		stone0 = (int) (width * stones * 0.1);
		stone1 = (int) (width * stones * 0.1);
		stone2 = (int) (width * stones * 0.1);
		flower0 = (int) (width * foliation * 0.1);
		flower1 = (int) (width * foliation * 0.1);
		flower2 = (int) (width * foliation * 0.1);
		youngTrees = (int) (width * foliation * 0.1);
		midTrees = (int) (width * foliation * 0.1);
		oldTrees = (int) ((width / 45) * foliation * 0.1);

		bee = (int) (width * insects * 0.1);
		butterfly = (int) (width * insects * 0.1);
		fairy = (int) (width * insects * 0.1);

		fox = (int) (width * creatures * 0.1);
		wolf = (int) (width * creatures * 0.1);
		bear = (int) (width * creatures * 0.1);
		bat = (int) (width * creatures * 0.1);
		skunk = (int) (width * creatures * 0.1);

		cannibal = (int) ((width / 100) * cannibals * 0.1);

		maxStone = (int) (width * stones * 0.1);
		maxFlower = (int) (width * stones * 0.1);
		maxTrees = (int) (width * stones * 0.1);

		maxBee = (int) (width * insects * 0.1);
		maxButterfly = (int) (width * insects * 0.1);
		maxFairy = (int) (width * insects * 0.1);

		maxFox = (int) (width * creatures * 0.1);

		maxCannibals = (int) (width * creatures * 0.1);

		for (int i = 0; i < caves; i++) {
			Noise n = new Noise((long) (Math.random() * 10000));

			double radius = 0;
			double momentum = Math.random() * 1000;
			double xc = Math.random() * (width);
			double yc = 0;
			while (radius < 10) {
				xc = Public.range(0, width, xc);
				xc += n.get1(momentum) * 2;
				yc = Math.max(yc, 0);
				yc += (n.get1(-momentum) + 0.1) * 0.4;

//			int r = (n.get1(radius)>-0.25 ? 3 : 0);
				int r = 5;
				for (double px = xc - r; px < xc + r; px++) {
					for (double py = yc - r; py < yc + r; py++) {
						int tx = (int) Public.range(0, tiles.length - 1, px);
						int ty = (int) Public.range(0, tiles[0].length - 1, py);
						int cx = (int) Public.range(0, tiles.length - 1, xc);
						int cy = (int) Public.range(0, tiles[0].length - 1, yc);

						if (tiles[tx][ty] != "TILE" + 0)
							backtiles[tx][ty] = "TILE5";
						if (Public.dist(tx, ty, cx, cy) <= r - 2)
							tiles[tx][ty] = "TILE" + 0;
					}
				}

				momentum += 0.0025;
				radius += 0.0025;
			}
		}

//			tiles[i][((int) Math.max(0.0D, Math.min(y, tiles[i].length - 2)))] = 2;
//
//			for (int j = (int) y + 1; j < height; j++) {
//				tiles[i][Math.max(0, j)] = 5;
//			}
//
//		}

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = formatTiles(tiles, i, j);
				backtiles[i][j] = formatBacktiles(backtiles, i, j);
			}
		}
		tilesload = System.nanoTime() - pre;

		highestTile();

		spawnx = 180;
		spawny = getHighest(180.0D);

		enviornment = new Enviornment(handler, Public.random(1.0D, 5.0D), Public.random(1.0D, 5.0D),
				Public.random(50.0D, 100.0D));
	}

	public BufferedImage[] createBackground(double index) {
		
		int bw = width * 18 + handler.getWidth();
		int bh = height * 18 + handler.getHeight();

		BufferedImage backgroundImage = new BufferedImage(bw, bh, 6);
		BufferedImage backgroundImageSnowy = new BufferedImage(bw, bh, 6);
		Graphics bg = backgroundImage.getGraphics();
		Graphics sg = backgroundImageSnowy.getGraphics();

		double c = Public.debugRandom(-1.0D, 1.0D);
		if (tiles == null || tiles.length == 0)
			return null;
		double y = Noise.noise1(0) * 30 + (tiles[0].length / 4) - (height / 10 * index) / 2;

		String[][] tiles = new String[bw / 9][bh / 9];
		
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = "TILE" + 0;
			}
		}

		for (int i = 0; i < bw / 9; i++) {

			y = Noise.noise1((i / 100.0) * 2) * 30 + (tiles[0].length / 4) - (height / 10 * index) / 2;

			tiles[i][((int) Math.max(0.0D, Math.min(y, tiles[0].length - 1)))] = "TILE" + 2;

			for (int j = (int) y + 1; j < bh / 9; j++) {
				tiles[i][Math.max(0, j)] = "TILE" + 5;
			}

			if (Math.abs(c) >= 2.0D)
				c /= 2.0D;
			if ((y >= cloudY + 4) && (c > 0.0D)) {
				c /= 2.0D;
				c -= 0.1D;
			}
			if ((y <= cloudY - 7) && (c < 0.0D)) {
				c /= 2.0D;
				c += 0.1D;
			}
		}
		
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = formatTiles(tiles, i, j);
			}
		}

		int bs = 0;
		
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length - 1; j++) {
				if ((tiles[i][j] == "TILE" + 0) && (tiles[i][(j + 1)] == "TILE" + 1 || tiles[i][j+1]=="TILE"+2 || tiles[i][j+1]=="TILE"+3)) {
					if ((Public.chance(maxStone / 2)) && (bs < maxStone)) {
						bg.drawImage(PublicAssets.stone[((int) Public.random(0.0D, 2.0D))], i * 15, j * 15, 15, 15,
								null);
						bs++;
					}

					if ((Public.chance(maxFlower / 2)) && (bs < maxFlower)) {
						bg.drawImage(PublicAssets.flower[((int) Public.random(0.0D, 8.0D))], i * 15, j * 15,
								PublicAssets.flower[((int) Public.random(0.0D, 2.0D))].getWidth() - 3,
								PublicAssets.flower[((int) Public.random(0.0D, 2.0D))].getHeight() - 3, null);
					}

					if (tiles[i][(j + 1)] == "TILE" + 1) {
						bg.drawImage(PublicAssets.shrubbery[0], i * 15, j * 15, 15, 15, null);
						sg.drawImage(PublicAssets.snowyGrassEntity.getSubimage(0, 0, 18, 18), i * 15, j * 15, null);
					} else if (tiles[i][(j + 1)] == "TILE" + 2) {
						bg.drawImage(PublicAssets.shrubbery[1], i * 15, j * 15, 15, 15, null);
						sg.drawImage(PublicAssets.snowyGrassEntity.getSubimage(18, 0, 18, 18), i * 15, j * 15, null);
					} else if (tiles[i][(j + 1)] == "TILE" + 3) {
						bg.drawImage(PublicAssets.shrubbery[2], i * 15, j * 15, 15, 15, null);
						sg.drawImage(PublicAssets.snowyGrassEntity.getSubimage(36, 0, 18, 18), i * 15, j * 15, null);
					}

					if ((Public.chance(maxStone / 2)) && (bs < maxStone)) {
						bg.drawImage(PublicAssets.stone[((int) Public.random(0.0D, 2.0D))], i * 15, j * 15, 15, 15,
								null);
						bs++;
					}

					if ((Public.chance(maxFlower / 2)) && (bs < maxFlower)) {
						bg.drawImage(PublicAssets.flower[((int) Public.random(0.0D, 8.0D))], i * 15, j * 15,
								PublicAssets.flower[((int) Public.random(0.0D, 2.0D))].getWidth() - 3,
								PublicAssets.flower[((int) Public.random(0.0D, 2.0D))].getHeight() - 3, null);
					}
				}

				if (Tile.getTile(tiles[i][j]).getImage() != null) {
					bg.drawImage(Tile.getTile(tiles[i][j]).getImage(), i * 15, j * 15, 15, 15, null);
//					sg.drawImage(Tile.getTile(tiles[i][j]).getSnowy(), i * 15, j * 15, 15, 15, null);
				}
			}
		}
		
		bg.dispose();
		sg.dispose();

		return new BufferedImage[] {
				Tran.effectColor(backgroundImage,
						new Color((int) (240 - index * 5), (int) (240 - index * 5), (int) (250 - index * 2))),
				backgroundSnowOverlay};
	}

	public static String formatTiles(String[][] tiles, int i, int j) {
		if (tiles[i][j] == "TILE" + 0) {
			return "TILE" + 0;
		}

		if (j > 0)
			if (tiles[i][(j - 1)] == "TILE" + 0) {
				if (i > 0 && i < tiles.length - 2 && (tiles[(i - 1)][j] == "TILE" + 0)
						&& (tiles[(i + 1)][j] == "TILE" + 0)) {
					if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == "TILE" + 0)
						return "TILE" + 16;
					return "TILE" + 13;
				}
				if (tiles.length > i + 1 && tiles[(i + 1)][j] == "TILE" + 0) {
					if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == "TILE" + 0)
						return "TILE" + 12;
					return "TILE" + 3;
				}
				if (i > 0 && tiles[(i - 1)][j] == "TILE" + 0) {
					if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == "TILE" + 0)
						return "TILE" + 10;
					return "TILE" + 1;
				}

				if (i > 0 && tiles.length > i + 1 && (tiles[(i - 1)][j] != "TILE" + 0)
						&& (tiles[(i + 1)][j] != "TILE" + 0)) {
					if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == "TILE" + 0)
						return "TILE" + 11;
					return "TILE" + 2;
				}
			}

		if (i > 0 && j > 0 && (tiles[(i - 1)][(j - 1)] == "TILE" + 0) && (tiles[(i - 1)][j] != "TILE" + 0)
				&& ((j == 0) || (tiles[i][(j - 1)] != "TILE" + 0))) {
			return "TILE" + 18;
		}
		if (tiles.length > i + 1 && j > 0 && (tiles[(i + 1)][(j - 1)] == "TILE" + 0)
				&& (tiles[(i + 1)][j] != "TILE" + 0) && (tiles[i][(j - 1)] != "TILE" + 0)) {
			return "TILE" + 17;
		}

		if (tiles.length > i + 1 && j > 0 && (tiles[(i + 1)][j] == "TILE" + 0) && (tiles[i][(j - 1)] != "TILE" + 0)) {
			if (i > 0 && tiles[i - 1][j] == "TILE" + 0) {
				if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == "TILE" + 0)
					return "TILE" + 15;
				return "TILE" + 14;
			}
			if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == "TILE" + 0)
				return "TILE" + 9;
			return "TILE" + 6;
		}
		if (i > 0 && j > 0 && (tiles[(i - 1)][j] == "TILE" + 0) && (tiles[i][(j - 1)] != "TILE" + 0)) {
			if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == "TILE" + 0)
				return "TILE" + 7;
			return "TILE" + 4;
		}
		if (tiles[i].length > j + 1 && tiles[i][j + 1] == "TILE" + 0)
			return "TILE" + 8;
		return "TILE" + 5;
	}

	public static String formatTiles(ArrayList<ArrayList<String>> tiles, int i, int j) {
		if (tiles.get(i).get(j) == "TILE" + 0) {
			return tiles.get(i).get(j);
		}

		if (j == 0 || tiles.get(i).get(j - 1) == "TILE" + 0) {
			if (i > 0 && i < tiles.size() - 2 && (tiles.get(i - 1).get(j) == "TILE" + 0)
					&& (tiles.get(i + 1).get(j) == "TILE" + 0)) {
				if (j == tiles.get(i).size() - 1
						|| (tiles.get(i).size() > j + 1 && tiles.get(i).get(j + 1) == "TILE" + 0))
					return "TILE" + 16;
				return "TILE" + 13;
			}
			if (i == tiles.size() - 1 || (tiles.size() > i + 1 && tiles.get(i + 1).get(j) == "TILE" + 0)) {
				if (j == tiles.get(i).size() - 1
						|| (tiles.get(i).size() > j + 1 && tiles.get(i).get(j + 1) == "TILE" + 0))
					return "TILE" + 12;
				return "TILE" + 3;
			}
			if (i == 0 || (i > 0 && tiles.get(i - 1).get(j) == "TILE" + 0)) {
				if (j == tiles.get(i).size() - 1
						|| (tiles.get(i).size() > j + 1 && tiles.get(i).get(j + 1) == "TILE" + 0))
					return "TILE" + 10;
				return "TILE" + 1;
			}

			if (i > 0 && tiles.size() > i + 1 && (tiles.get(i - 1).get(j) != "TILE" + 0)
					&& (tiles.get(i + 1).get(j) != "TILE" + 0)) {
				if (j == tiles.get(i).size() - 1
						|| (tiles.get(i).size() > j + 1 && tiles.get(i).get(j + 1) == "TILE" + 0))
					return "TILE" + 11;
				return "TILE" + 2;
			}
		}

		if (i > 0 && j > 0 && (tiles.get(i - 1).get(j - 1) == "TILE" + 0) && (tiles.get(i - 1).get(j) != "TILE" + 0)
				&& ((j == 0) || (tiles.get(i).get(j - 1) != "TILE" + 0))) {
			return "TILE" + 18;
		}
		if (tiles.size() > i + 1 && j > 0 && (tiles.get(i + 1).get(j - 1) == "TILE" + 0)
				&& (tiles.get(i + 1).get(j) != "TILE" + 0) && (tiles.get(i).get(j - 1) != "TILE" + 0)) {
			return "TILE" + 17;
		}

		if (tiles.size() > i + 1 && j > 0 && (tiles.get(i + 1).get(j) == "TILE" + 0)
				&& (tiles.get(i).get(j - 1) != "TILE" + 0)) {
			if (i > 0 && tiles.get(i - 1).get(j) == "TILE" + 0) {
				if (j == tiles.get(i).size() - 1
						|| (tiles.get(i).size() > j + 1 && tiles.get(i).get(j + 1) == "TILE" + 0))
					return "TILE" + 15;
				return "TILE" + 14;
			}
			if (j == tiles.get(i).size() - 1 || (tiles.get(i).size() > j + 1 && tiles.get(i).get(j + 1) == "TILE" + 0))
				return "TILE" + 9;
			return "TILE" + 6;
		}
		if (i == 0 || (i > 0 && j > 0 && (tiles.get(i - 1).get(j) == "TILE" + 0)
				&& (tiles.get(i).get(j - 1) != "TILE" + 0))) {
			if (j == tiles.get(i).size() - 1 || (tiles.get(i).size() > j + 1 && tiles.get(i).get(j + 1) == "TILE" + 0))
				return "TILE" + 7;
			return "TILE" + 4;
		}
		if (j == tiles.get(i).size() - 1 || (tiles.get(i).size() > j + 1 && tiles.get(i).get(j + 1) == "TILE" + 0))
			return "TILE" + 8;
		return tiles.get(i).get(j);
	}

	public static String formatBacktiles(String[][] tiles, int i, int j) {
		if ((i == 0) || (j == 0) || (i == tiles.length - 1) || (j == tiles[(tiles.length - 1)].length - 1)) {
			if (i == 0 && tiles[i][j] == "TILE2" && tiles[1][j] == "TILE0")
				return "TILE3";
			return tiles[i][j];
		}

		if (tiles[i][j] == "TILE0") {
			return "TILE0";
		}

		if (tiles[i][(j - 1)] == "TILE0") {
//			if ((tiles[(i - 1)][j] == 0) && (tiles[(i + 1)][j] == 0)) {
//				if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == 0)
//					return 16;
//				return 13;
//			}
			if (tiles[(i + 1)][j] == "TILE0") {
				if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == "TILE0")
					return "TILE12";
				return "TILE3";
			}
			if (tiles[(i - 1)][j] == "TILE0") {
				if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == "TILE0")
					return "TILE10";
				return "TILE1";
			}

			if ((tiles[(i - 1)][j] != "TILE0") && (tiles[(i + 1)][j] != "TILE0")) {
				if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == "TILE0")
					return "TILE11";
				return "TILE2";
			}
		}

		if ((tiles[(i - 1)][(j - 1)] == "TILE0") && (tiles[(i - 1)][j] != "TILE0")
				&& ((j == 0) || (tiles[i][(j - 1)] != "TILE0"))) {
			return "TILE11";
		}
		if ((tiles[(i + 1)][(j - 1)] == "TILE0") && (tiles[(i + 1)][j] != "TILE0") && (tiles[i][(j - 1)] != "TILE0")) {
			return "TILE10";
		}

		if ((tiles[(i + 1)][j] == "TILE0") && (tiles[i][(j - 1)] != "TILE0")) {
//			if (tiles[i-1][j] == 0) {
//				if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == 0)
//					return 15;
//				return 14;
//			}
			if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == "TILE0")
				return "TILE9";
			return "TILE6";
		}
		if ((tiles[(i - 1)][j] == "TILE0") && (tiles[i][(j - 1)] != "TILE0")) {
			if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == "TILE0")
				return "TILE7";
			return "TILE4";
		}
		if (tiles[i].length > j + 1 && tiles[i][j + 1] == "TILE0")
			return "TILE8";
		return "TILE5";

	}

	public static String formatBacktiles(ArrayList<ArrayList<String>> tiles, int i, int j) {
		if ((i == 0) || (j == 0) || (i == tiles.size() - 1) || (j == tiles.get(tiles.size() - 1).size() - 1)) {
			if (i == 0 && tiles.get(i).get(j) == "TILE2" && tiles.get(1).get(j) == "TILE0")
				return "TILE3";
			return tiles.get(i).get(j);
		}

		if (tiles.get(i).get(j) == "TILE0") {
			return "TILE0";
		}

		if (tiles.get(i).get((j - 1)) == "TILE0") {
//			if ((tiles[(i - 1)][j] == "TILE0") && (tiles[(i + 1)][j] == "TILE0")) {
//				if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == "TILE0")
//					return 16;
//				return 13;
//			}
			if (tiles.get(i + 1).get(j) == "TILE0") {
				if (tiles.get(i).size() > j + 1 && tiles.get(i).get(j + 1) == "TILE0")
					return "TILE12";
				return "TILE3";
			}
			if (tiles.get(i - 1).get(j) == "TILE0") {
				if (tiles.get(i).size() > j + 1 && tiles.get(i).get(j + 1) == "TILE0")
					return "TILE10";
				return "TILE1";
			}

			if ((tiles.get(i - 1).get(j) != "TILE0") && (tiles.get(i + 1).get(j) != "TILE0")) {
				if (tiles.get(i).size() > j + 1 && tiles.get(i).get(j + 1) == "TILE0")
					return "TILE11";
				return "TILE2";
			}
		}

		if ((tiles.get(i - 1).get(j - 1) == "TILE0") && (tiles.get(i - 1).get(j) != "TILE0")
				&& ((j == 0) || (tiles.get(i).get(j - 1) != "TILE0"))) {
			return "TILE11";
		}
		if ((tiles.get(i + 1).get(j - 1) == "TILE0") && (tiles.get(i + 1).get(j) != "TILE0")
				&& (tiles.get(i).get(j - 1) != "TILE0")) {
			return "TILE10";
		}

		if ((tiles.get(i + 1).get(j) == "TILE0") && (tiles.get(i).get(j - 1) != "TILE0")) {
//			if (tiles[i-1][j] == "TILE0") {
//				if (tiles[i].length > j + 1 && tiles[(i)][j + 1] == "TILE0")
//					return 15;
//				return 14;
//			}
			if (tiles.get(i).size() > j + 1 && tiles.get(i).get(j + 1) == "TILE0")
				return "TILE9";
			return "TILE6";
		}
		if ((tiles.get(i - 1).get(j) == "TILE0") && (tiles.get(i).get(j - 1) != "TILE0")) {
			if (tiles.get(i).size() > j + 1 && tiles.get(i).get(j + 1) == "TILE0")
				return "TILE7";
			return "TILE4";
		}
		if (tiles.get(i).size() > j + 1 && tiles.get(i).get(j + 1) == "TILE0")
			return "TILE8";
		return "TILE5";

	}

	public void finish(boolean beginning) {
		handler.logWorld("Finished loading world");
		state = 10;

		if (beginning) {
			GameState gset = (GameState) State.getState();
			gset.setLoadingPhase(-1);
		}
	}

	public void reset() {
		entityManager.getEntities().clear();
		tiles = new String[][] { new String[0] };
		dead = true;
		center = null;
		handler = null;
	}

	public void tick() {
		if (KeyManager.checkBind("Debug") && KeyManager.existantTyped) {
			Box = !Box;
			Console.log("Showing Box", Box);
		}

		if (dead) {
			respawn.tick();
		}
		
		handler.getGameCamera().centerOnEntity(center);

		long pre = System.nanoTime();

		entityManager.tick();

		entities = (System.nanoTime() - pre);
		pre = System.nanoTime();

		itemManager.tick();

		particleManager.tick();

		items = (System.nanoTime() - pre);
		pre = System.nanoTime();

		enviornment.tick();

		enviorn = (System.nanoTime() - pre);
		pre = System.nanoTime();

		waterManager.tick();
		waterManager.tickCol(entityManager);

		water = (System.nanoTime() - pre);
		pre = System.nanoTime();

		int xStart = (int) Math.max(handler.getGameCamera().getxOffset() / Tile.TILEWIDTH, 0.0F);
		int xEnd = (int) Math.min(width,
				(handler.getGameCamera().getxOffset() + handler.getWidth()) / Tile.TILEWIDTH + 1.0F);
		int yStart = (int) Math.max(handler.getGameCamera().getyOffset() / Tile.TILEHEIGHT, 0.0F);
		int yEnd = (int) Math.min(height,
				(handler.getGameCamera().getyOffset() + handler.getHeight()) / Tile.TILEHEIGHT + 1.0F);

		for (int y = yStart; y < yEnd; y++) {
			for (int x = xStart; x < xEnd; x++) {
				if (getTile(x, y).tickable())
					getTile(x, y).tick(handler, x, y);
			}
		}
		tilet = (System.nanoTime() - pre);

		if (respawn.on) {
			if (entityManager.getPlayer() == null) {
				entityManager.setPlayer(new Player(handler, 100.0D, 0.0D, false, 2.0D, 3));
			}
			entityManager.getPlayer().dead = false;
			entityManager.setPlayer(entityManager.getPlayer());
			entityManager.getPlayer().lives = 3;
			for (String s : entityManager.getPlayer().items.keySet()) {
				entityManager.getPlayer().items.get(s).amount = 0;
			}
			entityManager.getPlayer().toolbar = new String[] { "null", "null", "null", "null", "null", "null", "null",
					"null", "null", "null" };
			entityManager.getPlayer().setHealth(entityManager.getPlayer().MAX_HEALTH);
			entityManager.getPlayer().setX(spawnx);
			entityManager.getPlayer().setY(spawny);
			respawn.tick();
			center = entityManager.getPlayer();
			dead = false;
		}

		if(dead && KeyManager.checkBind("Up") && KeyManager.existantTyped)
			waitingForCreature = true;
		
		if (waitingForCreature) {
			Entity e2 = entityManager.getEntities().get((int) Public.random(0, entityManager.getEntities().size()-1));
			if (e2.creature) {
				center = e2;
				handler.logWorld("Centered on: " + e2.getClass());
				waitingForCreature = false;
				return;
			}
		}

		if (loading) {
			loading = false;
		}
//		handler.getGameCamera().centerOnEntity(center);
	}

	public boolean SAFETOWALK(double xp, double y) {
		int x = (int) (xp / 18);
		if (heights.size() > x && heights.get(x).size() > 0) {
			for (int i = 0; i < heights.get(x).size(); i++) {
				if (y < heights.get(x).get(i) * 18)
					return true;
			}
		}
		if (tops.size() > x && tops.get(x).size() > 0) {
			for (int i = 0; i < tops.get(x).size(); i++) {
				if (y < tops.get(x).get(i) * 18)
					return true;
			}
		}

		return false;
	}

	public boolean SAFETOWALK(double xp, double yp, double height) {
		for (double y = yp; y < height + yp; y += 18) {
			int x = (int) (xp / 18);
			if (heights.size() > x && heights.get(x).size() > 0) {
				for (int i = 0; i < heights.get(x).size(); i++) {
					if (y > heights.get(x).get(i) * 18 && y < heights.get(x).get(i) * 18 + 19)
						return true;
				}
			}
			if (tops.size() > x && tops.get(x).size() > 0) {
				for (int i = 0; i < tops.get(x).size(); i++) {
					if (y > tops.get(x).get(i) * 18 && y < tops.get(x).get(i) * 18 + 19)
						return true;
				}
			}
		}

		return false;
	}

	public boolean SAFETOWALK(double xc, double y, int width) {
		for (double xp = xc; xp <= xc + width; xp += 18) {
			int x = (int) (xp / 18);
			if (x >= 0 && heights.size() > x && heights.get(x) != null && heights.get(x).size() > 0) {
				for (int i = 0; i < heights.get(x).size(); i++) {
					if (y < heights.get(x).get(i) * 18)
						return true;
				}
			}
			if (x >= 0 &&tops.size() > x && tops.get(x) != null && tops.get(x).size() > 0) {
				for (int i = 0; i < tops.get(x).size(); i++) {
					if (y < tops.get(x).get(i) * 18)
						return true;
				}
			}
		}

		return false;
	}

	public boolean SAFETOWALK(double xc, double yp, int width, int height) {
		for (double y = yp; y < height + yp; y += 18) {
			for (double xp = xc; xp <= xc + width; xp += 18) {
				int x = (int) (xp / 18);
				if (heights.size() > x && heights.get(x).size() > 0) {
					for (int i = 0; i < heights.get(x).size(); i++) {
						if (y > heights.get(x).get(i) * 18 && y < heights.get(x).get(i) * 18 + 19)
							return true;
					}
				}
				if (tops.size() > x && tops.get(x).size() > 0) {
					for (int i = 0; i < tops.get(x).size(); i++) {
						if (y > tops.get(x).get(i) * 18 && y < tops.get(x).get(i) * 18 + 19)
							return true;
					}
				}
			}
		}

		return false;
	}

	public void spawing() {

		long pre = System.nanoTime();

		if ((enviornment.getTemp() > 32.0D) && (enviornment.getHumidity() > 2.0D)
				&& (flower0 + flower1 + flower2 < maxFlower)
				&& (Math.random() < 0.001D + Public.Map(enviornment.getHumidity(), 5.0D, 0.0D, 0.009D, 0.0D))) {
			if (Math.random() < 0.0D) {
				addFlower(2, 0);
				flower0 += 2;
			} else if (Math.random() < 0.5D) {
				addFlower(2, 1);
				flower1 += 2;
			} else {
				addFlower(2, 2);
				flower2 += 2;
			}
		}

		sFlowers = System.nanoTime() - pre;
		pre = System.nanoTime();

		if ((enviornment.getTemp() > 32.0D) && (youngTrees + midTrees + oldTrees < maxTrees) && Public.chance(1)) {

			youngTrees += 1;

			addTrees(1, 0, 15);
		}

		if (Public.chance(5) && shrubs.size() > 0) {
			int x = (int) Public.random(0, shrubs.size() - 1);

			int index = 0;
			
			int max = (width*height)/72;
			
			while (shrubs.get(x).size() == 0 && index < 10) {
				x = (int) Public.random(0, shrubs.size() - 1);
				index++;
			}

			if (shrubs.get(x).size() != 0) {
				index = (int) Public.random(0, shrubs.get(x).size() - 1);

				if (Public.chance(5) && shrubbery<max) {
					entityManager.addEntity(new Shrubbery(handler, x * 18 + Public.random(0, 18),
							shrubs.get(x).get(index).get(0) * 18 - 18, (int) Public.random(3, 4)), false);
				} else {
					if (shrubs.get(x).get(index).get(2) == 0) {
						entityManager.addEntity(new Shrubbery(handler, x * 18,
								(shrubs.get(x).get(index).get(0) - 1) * 18, shrubs.get(x).get(index).get(1), x, index),
								true);
						shrubs.get(x).get(index).set(2, 1);
					}
				}
			}
		}

		sTrees = System.nanoTime() - pre;
		pre = System.nanoTime();

		if ((stone0 + stone1 + stone2 < maxStone) && (Math.random() < 0.01D)) {
			if (Math.random() < 0.0D) {
				addStone(2, 0);
				stone0 += 2;
			} else if (Math.random() < 0.5D) {
				addStone(2, 1);
				stone1 += 2;
			} else {
				addStone(1, 2);
				stone2 += 1;
			}
		}

		sStones = System.nanoTime() - pre;
		pre = System.nanoTime();

		if ((fox < maxFox) && (Math.random() < 0.01D)) {
			addFox(1);
			fox++;
		}

		if ((wolf < maxWolf) && (Math.random() < 0.01D)) {
			addWolf(1);
			wolf++;
		}

		if ((skunk < maxSkunk) && (Math.random() < 0.01D)) {
			addSkunk(1);
			skunk++;
		}

		if ((bat < maxBat) && (enviornment.getHours() > 20 && enviornment.getHours() < 8) && (Math.random() < 0.01D)) {
			addBat(1);
			bat++;
		}

		if ((bear < maxBear) && (Math.random() < 0.01D)) {
			addBear(1);
			bear++;
		}

		if ((fairy < maxFairy) && (Math.random() < 0.01D)) {
			addFairy(1);
			fairy++;
		}

		if ((bee < maxBee) && (Math.random() < 0.01D)) {
			addBee(1, 10000L);
			bee += 1;
		}

		for (EntityAdder a : entityManager.adders) {
			if (Public.chance(1) && maxEntities.containsKey(a.name)) {
				int orig = 0;
				int max = 0;
				max = maxEntities.get(a.name);
				orig = origEntities.get(a.name);
//				System.out.println(orig+ " " + max);
				if (orig < max) {
					addEntities(1, a);
					origEntities.put(a.name, origEntities.get(a.name) + 1);
				}
			}
		}

		if ((butterfly < maxButterfly) && (Math.random() < 0.01D)) {
			addButterfly(1, 10000L);
			butterfly += 1;
		}
		
		if ((cannibal < maxCannibals) && (Math.random() < 0.0025D)) {
			addCannibalTribe(1, (int) (Public.random(5.0D, width - 5)*18));
			cannibal += 1;
		}

		sOther = System.nanoTime() - pre;
		pre = System.nanoTime();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void render(Graphics g, Graphics2D g2d) {
//		handler.getGameCamera().centerOnEntity(center);
		long pre = System.nanoTime();
//		g2d.translate(handler.getGameCamera().xOffset, handler.getGameCamera().yOffset);
		g2d.translate(-(Game.scale - 1.0D) * handler.getWidth() / 2.0D,
				-(Game.scale - 1.0D) * handler.getHeight() / 2.0D);
		g2d.scale(Game.scale, Game.scale);
		
		pre = System.nanoTime();
		enviornment.renderSunMoon(g2d);
		enviornment.renderStars(g2d);
		enviorr = (System.nanoTime() - pre);
		pre = System.nanoTime();

		if (OptionState.renderbackground)
			for (int i = backgroundImages.size() - 1; i >= 0; i--) {

				double a = i + 1.5;
				BufferedImage b = backgroundImages.get(i);

				int xoff = (int) ((handler.xOffset() / a));
				int yoff = (int) ((handler.yOffset() / a));
				g2d.clipRect(0, 0, 720, 405);
				if (xoff < b.getWidth())
					g2d.drawImage(b, -xoff, -yoff,null);
//					g2d.drawImage(b.getSubimage(xoff, yoff, 720, 405), 0, 0, null);

//				for (int l = 0; l < Tile.g1.snowyness(0, 0); l++) {
//					g.drawImage(backgroundSnowOverlays.get(i), (int) (-(handler.xOffset() / a) * 1.0 / a),
//							(int) (-(handler.yOffset() / a) * 1.0 / a), null);
//				}
			}

		if (rencount == 0) {
			resetGraphics();
		}
		itemsr = System.nanoTime() - pre;
		pre = System.nanoTime();

		if(((OptionState) handler.getGame().optionState).getToggle("Glow near sun"))
			entityManager.renderLight(g2d);
		renderBacktiles(g2d);
		renderTiles(g2d);

		tiler = (System.nanoTime() - pre);

		pre = System.nanoTime();
		entityManager.render(g2d, Box);
		entitiesr = (System.nanoTime() - pre);
		pre = System.nanoTime();
		itemManager.render(g2d, Box);
//		itemsr = (System.nanoTime() - pre); 
		particleManager.render(g2d);
		pre = System.nanoTime();
		waterManager.Render(g2d, Box);
		waterr = (System.nanoTime() - pre);
		
//		g2d.translate(-handler.getGameCamera().xOffset, -handler.getGameCamera().yOffset);

		if (dead) {
			respawn.render(g);
		}

		rencount += 1;

		pre = System.nanoTime();
		enviornment.render(g, g2d);
		enviornment.renderGui(g, g2d);
		enviorr += System.nanoTime() - pre;

		pre = System.nanoTime();

		entityManager.gui(g2d);

		if (KeyManager.checkBind("Toggle Map")) {
			map.render(g);
		}

		waterr = System.nanoTime() - pre;
		
//		handler.getGameCamera().centerOnEntity(center);
	}

	private void startForeground() {
		foreground = new BufferedImage(width * 18, height * 18, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = foreground.createGraphics();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				g.drawImage(getTile(x, y).getImage(), x * 18, y * 18, null);
			}
		}
		g.dispose();
	}
	
	private void startBackground() {
		background = new BufferedImage(width * 18, height * 18, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = background.createGraphics();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				g.drawImage(Backtile.getTile(backtiles[x][y]).getImage(), x * 18, y * 18, null);
			}
		}
		g.dispose();
	}

	private void renderTiles(Graphics2D g) {

		if (foreground == null)
			startForeground();

//		if (!OptionState.individualtilerender)
//			g.drawImage(foreground, (int) -handler.xOffset(), (int) -handler.yOffset(), null);

		int xStart = (int) Math.max(handler.xOffset() / 18, 0.0F);
		int xEnd = (int) Math.min(width, (handler.xOffset() + handler.getWidth()) / 18 + 1.0F);
		int yStart = (int) Math.max(handler.yOffset() / 18, 0.0F);
		int yEnd = (int) Math.min(height, (handler.yOffset() + handler.getHeight()) / Tile.TILEHEIGHT + 1.0F);

		int sx = enviornment.sunX() + (int) (9);
		int sy = enviornment.sunY() + (int) (9);

		if(((OptionState) handler.getGame().optionState).getToggle("Glow near sun")) {
			for (int j = (int) (sx / 18 - 5); j < sx / 18 + 5; j++) {
				for (int k = sy / 18 - 5; k < sy / 18 + 5; k++) {
					if (j >= 0 && k >= 0 && j < World.getWidth() && k < World.getHeight() && World.ready()) {

						float opacity = (float) Math.max(1 - Public.dist(j, k, sx / 18, sy / 18) / 5, 0);

						World.getTile(j, k).lightRender(g, (int) ((double) j * 18.0 - handler.xOffset()),
								(int) ((double) k * 18.0 - handler.yOffset()), (int) (opacity * 255));
					}
				}
			}
		}

		g.clipRect(0, 0, 720, 405);
		g.drawImage(foreground, -(int)handler.xOffset(), -(int)handler.yOffset(), null);
//		return;
//		g.drawImage(foreground.getSubimage((int) ((handler.xOffset())), (int) ((handler.yOffset())), handler.getWidth(),
//				(int) Math.min(handler.getHeight(), foreground.getHeight())), 0, 0, null);

		for (int y = yStart; y < yEnd; y++) {
			for (int x = xStart; x < xEnd; x++) {
	//			g.drawImage(getTile(x, y).getImage(), (int) Public.xO(x*18), (int) Public.yO(y*18), null);
				getTile(x, y).render(g, (int) Public.xO(x*18.0), (int) Public.yO(y*18.0), x, y);
			}
		}
	}

	private void renderBacktiles(Graphics2D g) {

		if(background==null)
			startBackground();
		
		try {
			g.clipRect(0, 0, 720, 405);
			g.drawImage(background, -(int)handler.xOffset(), -(int)handler.yOffset(), null);
		} catch (RasterFormatException e) {
			
		}
//		for (int y = yStart; y < yEnd; y++) {
//			for (int x = xStart; x < xEnd; x++) {
//				Backtile.getTile(backtiles[x][y]).render(g,
//						(int) (x * Tile.TILEWIDTH - handler.getGameCamera().getxOffset()),
//						(int) (y * Tile.TILEHEIGHT - handler.getGameCamera().getyOffset()), x, y);
//			}
//		}
	}

	public WaterManager getWaterManager() {
		return waterManager;
	}

	public ItemManager getItemManager() {
		return itemManager;
	}

	public void setItemManager(ItemManager itemManager) {
		this.itemManager = itemManager;
	}

	public ParticleManager getParticleManager() {
		return particleManager;
	}

	public void setParticleManager(ParticleManager particleManager) {
		this.particleManager = particleManager;
	}

	public void addBuilt(Building b) {
//		builtTiles.add(b);
	}

	private void resetGraphics() {
		for (int i = entityManager.getEntities().size() - 1; i > 0; i--) {
			Entity e = (Entity) entityManager.getEntities().get(i);
			e.reset();
		}
	}

	public static Tile getTile(int x, int y) {
		if (tiles==null || (x < 0) || (y < 0) || (x >= tiles.length) || (y >= tiles[x].length)) {
			return Tile.n0;
		}
		Tile t = Tile.getTile(tiles[x][y]);
		if (t == null) {
			return Tile.n0;
		}
		return t;
	}

	public static boolean ready() {
		return tiles != null && tiles.length > 0 && tiles[0] != null && tiles[0].length > 0;
	}

	public void loadWorld(String path) {
		String[] s;
		double[] d;
		int[] i;
		if (path.startsWith("/")) {
			try {
				s = FileLoader.streamToString(FileLoader.loadResource(path), path.length()).split("\\s+");
			} catch (IOException e) {
				s = new String[1];
				e.printStackTrace();
			}
		} else {
			File f = new File(path);

			s = FileLoader.readFile(f).split("\\s+");
		}

		d = new double[s.length];
		i = new int[s.length];
		for (int j = 0; j < s.length; j++) {
			d[j] = Utils.parseDouble(s[j]);
			i[j] = Utils.parseInt(s[j]);
		}

		handler.getGame().initMessage("Loading Variables...");

		long pre = System.nanoTime();

		int o = 0;

		System.out.println("\t\t" + d[1]);

		if (d[1] < 0.7) {
			loadWorldpre_0_7(path);
			return;
		}

		if (s[0].contains("World") || s[0].contains("Save")) {
			o = 2;
		}

		width = i[o];
		height = i[o + 1];

		System.out.println(s[o] + " " + s[o + 1] + " " + i[o] + " " + i[o + 1] + " " + d[o] + " " + d[o + 1]);

		spawnx = (i[o + 2]);
		spawny = (i[o + 3]);

		maxStone = 0;
		maxFlower = 0;
		maxTrees = 0;

		maxBee = 0;
		maxButterfly = 0;
		maxFairy = 0;
		maxBat = 0;

		maxFox = 0;
		maxBear = 0;
		maxSkunk = 0;
		maxWolf = 0;

		maxCannibals = 0;

		int maxClouds = 0;
		cloudY = 20;

		double wind = 5, windChange = 5, windSwing = 100;
		double temperatureOffset = 0;

		enviornment = new Enviornment(handler, wind, windChange, windSwing);

		varsload = System.nanoTime() - pre;
		pre = System.nanoTime();

		handler.getGame().initMessage("Loading Tiles...");

		o += 4;

		tiles = new String[width][height];
		backtiles = new String[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				tiles[x][y] = s[(x + y * width + o)];
			}
		}

		System.out.println("Width: " + width + " " + tiles.length + "   Height: " + height);

		tilesload = System.nanoTime() - pre;

		highestTile();

		pre = System.nanoTime();

		if (s.length <= width * height + o) {
			state = 0;
			customload = 0;
			return;
		}
		int contInt = width * height + o;
		if (s[contInt].contains("BACKTILES")) {
			o = width * height + o + 1;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					backtiles[x][y] = s[(x + y * width + o)];
				}
			}
//			o++;
		}

		contInt = width * height + o;
		System.out.println("COINTING     " + s[contInt]);

		if (s.length > width * height + o) {
			handler.logWorld("Adding custom entities... " + (s.length > width * height + 34));

			contInt = handleEntityLoading(i, d, s, o);
		}

		System.out.println("World length " + s.length + " " + contInt + " " + (contInt < s.length));

		while (contInt + 1 < s.length) {
			if (d[contInt + 1] > 10) {
				contInt++;
				continue;
			}

			String e = s[contInt].replaceAll(" ", "") + " ";

			for (EntityAdder a : entityManager.adders) {
				if (e.contains(a.name.replaceAll(" ", "") + " ")) {
					maxEntities.put(a.name, (int) (width * d[contInt + 1] * 0.1));
					if (maxEntities.containsKey(a.name))
						System.out.println("Applied Data");
				}
			}

			System.out.println(e + (e.contains("Stones ")));
			if (e.contains("Stones "))
				maxStone = (int) (width * d[contInt + 1] * 0.1);
			if (e.contains("Flowers "))
				maxFlower = (int) (width * d[contInt + 1] * 0.1);
			if (e.contains("Trees "))
				maxTrees = (int) (width * d[contInt + 1] * 0.1);

			if (e.contains("Bats "))
				maxBat = (int) (width * d[contInt + 1] * 0.1);
			if (e.contains("Faries "))
				maxFairy = (int) (width * d[contInt + 1] * 0.1);
			if (e.contains("Bees "))
				maxBee = (int) (width * d[contInt + 1] * 0.1);
			if (e.contains("Butterflies "))
				maxButterfly = (int) (width * d[contInt + 1] * 0.1);

			if (e.contains("Bears "))
				maxBear = (int) (width * d[contInt + 1] * 0.1);
			if (e.contains("Wolves "))
				maxWolf = (int) (width * d[contInt + 1] * 0.1);
			if (e.contains("Skunks "))
				maxSkunk = (int) (width * d[contInt + 1] * 0.1);
			if (e.contains("Foxes "))
				maxFox = (int) (width * d[contInt + 1] * 0.1);

			if (e.contains("Cannibals "))
				maxCannibals = (int) (width * d[contInt + 1] * 0.1);
			if (e.contains("Clouds "))
				maxClouds = (int) (width * d[contInt + 1] * 0.1);
			if (e.contains("CloudY "))
				cloudY = i[contInt + 1];
			if (e.contains("Wind "))
				wind = d[contInt + 1];
			if (e.contains("WindChange "))
				wind = d[contInt + 1];
			if (e.contains("WindSwing "))
				windSwing = d[contInt + 1];

			if (e.contains("Temperature "))
				wind = d[contInt + 1];

			contInt++;
		}

		for (EntityAdder a : entityManager.adders) {
			if (maxEntities.containsKey(a.name)) {
				origEntities.put(a.name, maxEntities.get(a.name) / 4);
				addEntities(maxEntities.get(a.name) / 4, a);
				System.out.println(a.name + " Success");
			}
		}

		stone0 = maxStone / 4;
		stone1 = maxStone / 4;
		stone2 = maxStone / 4;
		flower0 = maxFlower / 4;
		flower1 = maxFlower / 4;
		flower2 = maxFlower / 4;
		youngTrees = (int) (maxTrees * 0.1);
		midTrees = (int) (maxTrees * 0.2);
		oldTrees = (int) (maxTrees * 0.45);

		bee = maxBee / 2;
		butterfly = maxButterfly / 2;
		fairy = maxFairy / 2;
		fox = maxFox / 2;
		cannibal = maxCannibals / 2;

		cloud0 = maxClouds / 4;
		cloud1 = maxClouds / 4;
		cloud2 = maxClouds / 4;
		cloud3 = maxClouds / 4;

		enviornment = new Enviornment(handler, wind, windChange, windSwing);
		enviornment.tempOffset = temperatureOffset;

		customload = System.nanoTime() - pre;
	}

	private void loadWorldpre_0_7(String path) {
		File f = new File(path);

		String[] s = FileLoader.readFile(f).split("\\s+");
		double[] d = new double[s.length];
		int[] i = new int[s.length];

		for (int j = 0; j < s.length; j++) {
			d[j] = Utils.parseDouble(s[j]);
			i[j] = Utils.parseInt(s[j]);
		}

		handler.getGame().initMessage("Loading Variables...");

		long pre = System.nanoTime();

		int o = 0;

		if (s[0].contains("World") || s[0].contains("Save")) {
			o = 2;
		}

		width = i[o];
		height = i[o + 1];

		spawnx = (i[o + 2] * 18);
		spawny = (i[o + 3] * 18);

		stone0 = i[o + 4];
		stone1 = i[o + 5];
		stone2 = i[o + 6];
		flower0 = i[o + 7];
		flower1 = i[o + 8];
		flower2 = i[o + 9];
		youngTrees = i[o + 10];
		midTrees = i[o + 11];
		oldTrees = i[o + 12];

		bee = i[o + 13];
		butterfly = i[o + 14];
		fox = i[o + 15];
		cannibal = i[o + 16];

		maxStone = i[o + 19];
		maxFlower = i[o + 20];
		maxTrees = i[o + 21];

		maxBee = i[o + 22];
		maxButterfly = i[o + 23];
		maxFox = i[o + 24];
		maxCannibals = i[o + 25];

		cloud0 = i[o + 29];
		cloud1 = i[o + 30];
		cloud2 = i[o + 31];
		cloud3 = i[o + 32];
		cloudY = i[o + 33];

		varsload = System.nanoTime() - pre;
		pre = System.nanoTime();

		handler.getGame().initMessage("Loading Tiles...");

		enviornment = new Enviornment(handler, d[o + 26], d[o + 27], d[o + 28]);

		o += 34;

		tiles = new String[width][height];
		backtiles = new String[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				tiles[x][y] = "TILE" + i[(x + y * width + o)];
				backtiles[x][y] = "TILE0";
			}
		}

		tilesload = System.nanoTime() - pre;

		highestTile();

		Lia = new com.zandgall.arvopia.entity.creatures.npcs.Lia(handler, (width - 10) * 18,
				getLowest(Public.range(0.0D, width * Tile.TILEWIDTH, (width - 10) * Tile.TILEWIDTH)) * Tile.TILEHEIGHT);
		entityManager.addEntity(Lia, true);

		pre = System.nanoTime();

		if (s.length <= width * height + o) {
			state = 0;
			customload = 0;
			return;
		}
		int contInt = width * height + o;
		if (s.length > width * height + o) {
			handler.logWorld("Adding custom entities... " + (s.length > width * height + 34));

			contInt = handleEntityLoadingPRE_0_7(i, d, s, o);
		}

		while (contInt < s.length) {
			String e = s[contInt];
			if (e.contains("Bears")) {
				maxBear = i[contInt + 1];
			} else if (e.contains("Wolves")) {
				maxWolf = i[contInt + 1];
			} else if (e.contains("Bats")) {
				maxBat = i[contInt + 1];
			} else if (e.contains("Skunks")) {
				maxSkunk = i[contInt + 1];
			} else if (e.contains("Fairies")) {
				maxFairy = i[contInt + 1];
			} else if (e.contains("Birds")) {
				System.out.println("Birds have been found " + i[contInt + 1]);
				maxEntities.put("Bird", i[contInt + 1]);
				origEntities.put("Bird", 0);
			}
			contInt++;
		}

		customload = System.nanoTime() - pre;
	}

	private int handleEntityLoading(int[] i, double[] d, String[] s, int o) {
		int contInt = width * height + o;

		int size = i[contInt];

		int PRECONTINT = contInt;

		System.out.println(size);
		handler.getGame().initMessage("Loading " + size + " custom entities...");

		for (int v = 0; v < size;) {
			if (contInt >= s.length - 3) {
				System.out.println("Breaking...");
				break;
			}

//			System.out.println("V " + v + " " + size + " " + (contInt - PRECONTINT));
			String e = s[(contInt + 1)].replaceAll(" ", "") + " ";

			for (EntityAdder a : entityManager.adders) {
				if (e.contains(a.name.replaceAll(" ", "") + " ")) {
					a.add(d[(contInt + 2)], d[(contInt + 3)]);
					v++;
				}
			}

			System.out.println("ADDING : " + e);

			if (e.contains("Tree ")) {
				entityManager.addEntity(new Tree(handler, d[(contInt + 2)], d[(contInt + 3)], i[(contInt + 4)]), false);

				contInt += 4;
				v++;
			} else if (e.contains("Fairy ")) {
				entityManager.addEntity(
						new Fairy(handler, d[(contInt + 2)], d[(contInt + 3)], Utils.parseBoolean(s[contInt + 4])),
						false);
				contInt += 2;
				v++;
			} else if (e.contains("Wolf ")) {
				entityManager.addEntity(new Wolf(handler, d[(contInt + 2)], d[(contInt + 3)]), false);
				contInt += 2;
				v++;
			} else if (e.contains("Bear ")) {
				entityManager.addEntity(new Bear(handler, d[(contInt + 2)], d[(contInt + 3)]), false);
				contInt += 2;
				v++;
			} else if (e.contains("Bat ")) {
				entityManager.addEntity(new Bat(handler, d[(contInt + 2)], d[(contInt + 3)]), false);
				contInt += 2;
				v++;
			} else if (e.contains("Skunk ")) {
				entityManager.addEntity(new Skunk(handler, d[(contInt + 2)], d[(contInt + 3)]), false);
				contInt += 2;
				v++;
			} else if (e.contains("Flower ")) {
				entityManager.addEntity(
						new Flower(handler, d[(contInt + 2)], d[(contInt + 3)], i[(contInt + 4)], d[(contInt + 5)]),
						false);
				v++;
				contInt += 4;
			} else if (e.contains("Stone ")) {
				entityManager.addEntity(new Stone(handler, d[(contInt + 2)], d[(contInt + 3)], i[(contInt + 4)]),
						false);
				v++;
				contInt += 3;
			} else if (e.contains("Cannibal ")) {
				entityManager.addEntity(new Cannibal(handler, d[(contInt + 2)], d[(contInt + 3)], d[(contInt + 4)],
						i[(contInt + 5)], Utils.parseBoolean(s[(contInt + 6)])), false);
				v++;
				contInt += 5;
			} else if (e.contains("Shrubbery ")) {
				entityManager.addEntity(new Shrubbery(handler, d[(contInt + 2)], d[(contInt + 3)], i[(contInt + 4)]),
						false);
				v++;
				contInt += 3;
			} else if (e.contains("Butterfly ")) {
				entityManager.addEntity(new Butterfly(handler, d[(contInt + 2)], d[(contInt + 3)], false, 100000L),
						false);
				v++;
				contInt += 2;
			} else if (e.contains("Bee ")) {
				entityManager.addEntity(new Bee(handler, d[(contInt + 2)], d[(contInt + 3)], false, 100000L), false);
				v++;
				contInt += 2;
			} else if (e.contains("Fox ")) {
				entityManager.addEntity(new Fox(handler, d[(contInt + 2)], d[(contInt + 3)]), false);
				v++;
				contInt += 2;
			} else if (e.contains("Cloud ")) {
				entityManager.addEntity(
						new Cloud(handler, d[(contInt + 2)], d[(contInt + 3)], i[(contInt + 4)], d[(contInt + 5)]),
						false);
				v++;
				contInt += 4;
			} else if (e.contains("Water ")) {
				waterManager.newWater(i[(contInt + 2)], i[(contInt + 3)], i[(contInt + 4)], i[(contInt + 5)]);
				v++;
				contInt += 4;
			} else if (e.contains("House ")) {
				entityManager.addEntity(new com.zandgall.arvopia.entity.statics.House(handler, d[(contInt + 2)],
						d[(contInt + 3)], i[(contInt + 4)]), false);
				contInt += 3;
				v++;
			} else if (e.contains("Villager ")) {
				entityManager.addEntity(new com.zandgall.arvopia.entity.creatures.npcs.Villager(handler,
						d[(contInt + 2)], d[(contInt + 3)], i[(contInt + 4)]), false);
				v++;
				contInt += 3;
			} else if (e.contains("Lia ")) {
				entityManager.addEntity(new com.zandgall.arvopia.entity.creatures.npcs.Lia(handler, d[(contInt + 2)], d[(contInt + 3)]), false);
				v++;
				contInt += 2;
			} else if (e.contains("Frizzy ")) {
				entityManager.addEntity(new com.zandgall.arvopia.entity.creatures.npcs.Frizzy(handler, d[(contInt + 2)], d[(contInt + 3)]), false);
				v++;
				contInt += 2;
			} else if (e.contains("Fawncier ")) {
				entityManager.addEntity(new com.zandgall.arvopia.entity.creatures.npcs.Fawncier(handler, d[(contInt + 2)], d[(contInt + 3)]), false);
				v++;
				contInt += 2;
			} else if (e.contains("NPC ")) {
				java.util.Map<String, String> quests = new HashMap<String, String>();
				v++;
				String[] speeches = s[(contInt + 6)].replaceAll("NAME", Reporter.user).replaceAll("`", " ").split("~");

				if (!s[(contInt + 7)].contains("!!!")) {

					String[] speechesvquests = s[(contInt + 7)].replaceAll("NAME", Reporter.user).replaceAll("`", " ")
							.split("~");
					String[] questmanage = s[(contInt + 8)].replaceAll("`", " ").split("~");

					for (int q = 0; q < questmanage.length; q++) {
						if (questmanage.length > 0) {
							quests.put(speechesvquests[q], questmanage[q]);
						}
					}
				}

				entityManager.addEntity(new Template(handler, d[(contInt + 2)], d[(contInt + 3)], i[(contInt + 4)],
						s[(contInt + 5)].replaceAll("~", " "), speeches, quests), true);

				contInt += 8;
			} else if (e.contains("AreaAdder ")) {
				entityManager.addEntity(new AreaAdders(handler, d[(contInt + 3)], d[(contInt + 4)], i[(contInt + 5)],
						i[(contInt + 6)], s[contInt + 2], d[contInt + 7]), true);
				contInt += 6;
			} else {
				contInt++;
				System.out.println("Contint adding " + contInt + " " + PRECONTINT);
			}
		}

		return contInt;
	}

	private int handleEntityLoadingPRE_0_7(int[] i, double[] d, String[] s, int o) {
		int contInt = width * height + o;

		int size = i[contInt];

		System.out.println(size);
		handler.getGame().initMessage("Loading " + size + " custom entities...");

		for (int v = 0; v < size; v++) {
			if (contInt >= s.length - 3) {
				break;
			}
			String e = s[(contInt + 1)].replaceAll(" ", "") + " ";

			for (EntityAdder a : entityManager.adders) {
				if (e.contains(a.name.replaceAll(" ", "")))
					a.add(d[(contInt + 2)], d[(contInt + 3)]);
			}

			if (e.contains("Tree ")) {
				entityManager.addEntity(new Tree(handler, d[(contInt + 2)], d[(contInt + 3)], i[(contInt + 4)]), false);

				contInt += 4;
			} else if (e.contains("Flower ")) {
				entityManager.addEntity(
						new Flower(handler, d[(contInt + 2)], d[(contInt + 3)], i[(contInt + 4)], d[(contInt + 5)]),
						false);

				contInt += 5;
			} else if (e.contains("Stone ")) {
				entityManager.addEntity(new Stone(handler, d[(contInt + 2)], d[(contInt + 3)], i[(contInt + 4)]),
						false);

				contInt += 4;
			} else if (e.contains("Cannibal ")) {
				entityManager.addEntity(new Cannibal(handler, d[(contInt + 2)], d[(contInt + 3)], d[(contInt + 4)],
						i[(contInt + 5)], Utils.parseBoolean(s[(contInt + 6)])), false);

				contInt += 6;
			} else if (e.contains("Shrubbery ")) {
				entityManager.addEntity(new Shrubbery(handler, d[(contInt + 2)], d[(contInt + 3)], i[(contInt + 4)]),
						false);
				contInt += 4;
			} else if (e.contains("Butterfly ")) {
				entityManager.addEntity(new Butterfly(handler, d[(contInt + 2)], d[(contInt + 3)], false, 100000L),
						false);

				contInt += 3;
			} else if (e.contains("Bee ")) {
				entityManager.addEntity(new Bee(handler, d[(contInt + 2)], d[(contInt + 3)], false, 100000L), false);

				contInt += 3;
			} else if (e.contains("Fox ")) {
				entityManager.addEntity(new Fox(handler, d[(contInt + 2)], d[(contInt + 3)]), false);

				contInt += 3;
			} else if (e.contains("Cloud ")) {
				entityManager.addEntity(
						new Cloud(handler, d[(contInt + 2)], d[(contInt + 3)], i[(contInt + 4)], d[(contInt + 5)]),
						false);

				contInt += 5;
			} else if (e.contains("Water ")) {
				waterManager.newWater(i[(contInt + 2)], i[(contInt + 3)], i[(contInt + 4)], i[(contInt + 5)]);

				contInt += 5;
			} else if (e.contains("House ")) {
				entityManager.addEntity(new com.zandgall.arvopia.entity.statics.House(handler, d[(contInt + 2)],
						d[(contInt + 3)], i[(contInt + 4)]), false);
				contInt += 4;
			} else if (e.contains("Villager ")) {
				entityManager.addEntity(new com.zandgall.arvopia.entity.creatures.npcs.Villager(handler,
						d[(contInt + 2)], d[(contInt + 3)], i[(contInt + 4)]), false);

				contInt += 4;
			} else if (e.contains("NPC ")) {
				
				if(s[(contInt + 5)].replaceAll("~", " ").equals("Frizzy")) {
					entityManager.addEntity(new Frizzy(handler, d[contInt+2], d[contInt+3]), true);
					contInt +=8;
					continue;
				}
				
				if(s[(contInt + 5)].replaceAll("~", " ").equals("Le Fancier")) {
					entityManager.addEntity(new Fawncier(handler, d[contInt+2], d[contInt+3]), true);
					contInt +=8;
					continue;
				}
				
				java.util.Map<String, String> quests = new HashMap<String, String>();

				String[] speeches = s[(contInt + 6)].replaceAll("NAME", Reporter.user).replaceAll("`", " ").split("~");

				if (!s[(contInt + 7)].contains("!!!")) {

					String[] speechesvquests = s[(contInt + 7)].replaceAll("NAME", Reporter.user).replaceAll("`", " ")
							.split("~");
					String[] questmanage = s[(contInt + 8)].replaceAll("`", " ").split("~");

					for (int q = 0; q < questmanage.length; q++) {
						if (questmanage.length > 0) {
							quests.put(speechesvquests[q], questmanage[q]);
						}
					}
				}

				entityManager.addEntity(new Template(handler, d[(contInt + 2)], d[(contInt + 3)], i[(contInt + 4)],
						s[(contInt + 5)].replaceAll("~", " "), speeches, quests), true);

				contInt += 8;
			} else {
				v--;
				contInt++;
			}
		}

		return contInt;
	}

	public void loadSave(String path) {

		save = path;

		openWorldData(path + "\\World.arv");

		Tile.set(width, height);

		openBGImage(path);

		enviornment.setupStars();

		handler.loadMods(entityManager);

		type = "Save";
		handler.getGame().initMessage("Loading Enviornment...");
		openEnviornment(path + "\\Enviornment.arv");
		handler.getGame().initMessage("Loading Entities...");
		handler.loadMods(path + "\\mods", entityManager);
		openEntities(path + "\\Entities.arv");
		handler.getGame().initMessage("Loading Player...");
		openPlayer(path + "\\Player.arv");
		handler.getGame().initMessage("Loading quests...");
		openQuests(path + "\\Quests.arv");
		openAchievements(path + "\\Achievements.arv");

		handler.getGame().initMessage("Finishing up...");

		Initiator.aloadSave(handler, path);

		finish(false);

	}

	public void saveWorld(String path) {

		Utils.createDirectory(path);

		Utils.createDirectory(path + "\\mods");
		if (type == "Pack") {
			try {
				for (String f : new File(this.path + "\\mods").list())
					Files.copy(new File(this.path + "\\mods\\" + f).toPath(), new File(path + "\\mods\\" + f).toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (type != "Save") {
			try {
				for (String f : new File("C:\\Arvopia\\mods").list())
					Files.copy(new File("C:\\Arvopia\\mods\\" + f).toPath(), new File(path + "\\mods\\" + f).toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		writeWorldData(path + "\\World.arv");

		writeBGImage(path);

		writeEnviornment(path + "\\Enviornment.arv");

		writeEntities(path + "\\Entities.arv");

		writePlayer(path + "\\Player.arv");

		writeQuests(path + "\\Quests.arv");

		writeAchievements(path + "\\Achievements.arv");

		Initiator.asavingWorld(handler, path);

	}

	public void writeWorldData(String path) {
		String content = "Save " + System.lineSeparator() + "0.8" + System.lineSeparator() + width + " " + height
				+ System.lineSeparator();

		content += entityManager.getPlayer().getSpawnX() + " " + entityManager.getPlayer().getSpawnY()
				+ System.lineSeparator();

		for (int y = 0; y < height; y++) {
			content += System.lineSeparator();
			for (int x = 0; x < width; x++) {
				content += getTile(x, y).getId() + " ";
			}
		}

		content += "BACKTILES";
		for (int y = 0; y < height; y++) {
			content += System.lineSeparator();
			for (int x = 0; x < width; x++) {
				content += backtiles[x][y] + " ";
			}
		}

//		content += stone0 + " ";
//		content += stone1 + " ";
//		content += stone2 + " ";
//		content += flower0 + " ";
//		content += flower1 + " ";
//		content += flower2 + " ";
//		content += youngTrees + " ";
//		content += midTrees + " ";
//		content += oldTrees + System.lineSeparator();

//		content += bee + " ";
//		content += butterfly + " ";
//		content += fox + " ";
//		content += cannibalTribes + " ";
//		content += minPerTribe + " ";
//		content += maxPerTribe + System.lineSeparator();

		content += "Stones " + maxStone + System.lineSeparator();
		content += "Flowers " + maxFlower + System.lineSeparator();
		content += "Trees " + maxTrees + System.lineSeparator();

		content += "Bees " + maxBee + System.lineSeparator();
		content += "Butterflies " + maxButterfly + System.lineSeparator();
		content += "Foxes " + maxFox + System.lineSeparator();
		content += "Cannibals " + maxCannibals + System.lineSeparator();

//		content += enviornment.getWind() + " ";
//		content += enviornment.getWindChange() + " ";
//		content += enviornment.getWindSwing() + System.lineSeparator();

//		content += cloud0 + " ";
//		content += cloud1 + " ";
//		content += cloud2 + " ";
//		content += cloud3 + System.lineSeparator();
		content += "Clouds " + (cloud0 + cloud1 + cloud2 + cloud3) + System.lineSeparator();
		content += "CloudY " + cloudY;

		Utils.fileWriter(content, path);
	}

	public void writeBGImage(String path) {
		for (int i = 0; i < Math.min(backgroundImages.size(), backgroundSnowOverlays.size()); i++) {
			try {
				ImageIO.write(backgroundImages.get(i), "png", new File(path + "\\BGImage" + i + ".png"));
				ImageIO.write(backgroundSnowOverlays.get(i), "png", new File(path + "\\BGImageSnowy" + i + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeEnviornment(String path) {

		String content = "";

		content += "" + enviornment.lapse + " " + enviornment.rohundo + " " + enviornment.getTime()
				+ System.lineSeparator();
		content += "" + enviornment.getHumidity() + " " + enviornment.precipitation;

		Utils.fileWriter(content, path);

	}

	public void writeEntities(String path) {
		Utils.fileWriter(entityManager.saveString(), path);
	}

	public void writePlayer(String path) {
		String content = "";
		content += entityManager.getPlayer().getX() + " " + entityManager.getPlayer().getY() + System.lineSeparator();
		content += spawnx + " " + spawny + System.lineSeparator();
		content += entityManager.getPlayer().health + " " + entityManager.getPlayer().breath + System.lineSeparator();
		for (String i : entityManager.getPlayer().items.keySet()) {
			content += i + " " + entityManager.getPlayer().items.get(i).amount;
		}
		content += System.lineSeparator();
		for (int i = 0; i < entityManager.getPlayer().toolbar.length; i++) {
			content += entityManager.getPlayer().toolbar[i] + " ";
		}
		content += System.lineSeparator();
		Utils.fileWriter(content, path);
	}

	public void writeQuests(String path) {
		String out = "";
		out += QuestManager.al.size() + System.lineSeparator();
		out += QuestManager.alfin.size() + System.lineSeparator();
		for (Quest q : QuestManager.al)
			out += q.name + System.lineSeparator();
		for (Quest q : QuestManager.alfin)
			out += q.name + System.lineSeparator();
		Utils.fileWriter(out, path);
	}

	public void writeAchievements(String path) {
		String out = "";
		out += AchievementManager.al.size() + System.lineSeparator();
		for (Achievement q : AchievementManager.al)
			out += q.name + System.lineSeparator();
		Utils.fileWriter(out, path);
	}

	public void openWorldData(String path) {
		loadWorld(path);
	}

	public void openBGImage(String path) {
		backgroundImages.clear();
		backgroundSnowOverlays.clear();
		for (String s : new File(path).list()) {
			if (s.contains("BGImage"))
				backgroundImages.add(ImageLoader.loadImageEX(path + "\\" + s));
			if (s.contains("BGImageSnowy"))
				backgroundSnowOverlays.add(ImageLoader.loadImageEX(path + "\\" + s));
		}
	}

	public void openEnviornment(String path) {
		String[] t = FileLoader.readFile(new File(path)).split("\\s+");
		enviornment.lapse = Utils.parseInt(t[0]);
		enviornment.rohundo = Utils.parseInt(t[1]);
		enviornment.setTime(Utils.parseLong(t[2]));
		enviornment.setHumidity(Utils.parseDouble(t[3]));
		enviornment.precipitation = Utils.parseBoolean(t[4]);
	}

	public void openEntities(String path) {
		entityManager.loadEntities(FileLoader.readFile(path));
	}

	public void openPlayer(String path) {

		String[] t = FileLoader.readFile(path).split("\\s+");

//		entityManager.setPlayer(new Player(handler, Utils.parseDouble(t[0]), Utils.parseDouble(t[1]), false, 2, 3));

		entityManager.getPlayer().setX(Utils.parseDouble(t[0]));
		entityManager.getPlayer().setY(Utils.parseDouble(t[1]));
		entityManager.getPlayer().setSpawnX(Utils.parseDouble(t[2]));
		spawnx = (int) Utils.parseDouble(t[2]);
		entityManager.getPlayer().setSpawnY(Utils.parseDouble(t[3]));
		spawny = (int) Utils.parseDouble(t[3]);
		entityManager.getPlayer().health = Utils.parseDouble(t[4]);
		entityManager.getPlayer().breath = Utils.parseDouble(t[5]);

		int items = Utils.parseInt(t[6]);

		for (int i = 0; i < items; i++) {
			entityManager.getPlayer().putItem(t[6 + i * 3], Utils.parseInt(t[7 + i * 3]));
		}

		for (int i = 0; i < entityManager.getPlayer().toolbar.length; i++) {
			entityManager.getPlayer().toolbar[i] = (t[9 + items * 3 + i]);
		}

	}

	public void openQuests(String path) {
		String[] t = FileLoader.readFile(path).split("\\s+");
		int startlength = Utils.parseInt(t[0]);
		int endlength = Utils.parseInt(t[1]);
		for (int i = 0; i < startlength; i++) {
			Quest.startSilent(t[i + 2]);
		}
		for (int i = 0; i < endlength; i++) {
			Quest.finishSilent(t[i + startlength + 2]);
		}
	}

	public void openAchievements(String path) {
		String[] t = FileLoader.readFile(path).split("\\s+");
		int startlength = Utils.parseInt(t[0]);
		for (int i = 0; i < startlength; i++) {
			Achievement.finishSilent(Achievement.get(t[i + 1]));
		}
	}

	public void saveWorldBACKUP(String path) {
		String content = "Save " + System.lineSeparator() + "0.6" + System.lineSeparator() + width + " " + height
				+ System.lineSeparator();

		content += entityManager.getPlayer().getSpawnX() + " " + entityManager.getPlayer().getSpawnY()
				+ System.lineSeparator();

		content += stone0 + " ";
		content += stone1 + " ";
		content += stone2 + " ";
		content += flower0 + " ";
		content += flower1 + " ";
		content += flower2 + " ";
		content += youngTrees + " ";
		content += midTrees + " ";
		content += oldTrees + System.lineSeparator();

		content += bee + " ";
		content += butterfly + " ";
		content += fox + " ";
		content += cannibal + " ";
		content += 1 + " ";
		content += 1 + System.lineSeparator();

		content += maxStone + " ";
		content += maxFlower + " ";
		content += maxTrees + System.lineSeparator();

		content += maxBee + " ";
		content += maxButterfly + " ";
		content += maxFox + " ";
		content += maxCannibals + System.lineSeparator();

		content += enviornment.getWind() + " ";
		content += enviornment.getWindChange() + " ";
		content += enviornment.getWindSwing() + System.lineSeparator();

		content += cloud0 + " ";
		content += cloud1 + " ";
		content += cloud2 + " ";
		content += cloud3 + System.lineSeparator();
		content += cloudY;

		for (int y = 0; y < height; y++) {
			content += System.lineSeparator();
			for (int x = 0; x < width; x++) {
				content += getTile(x, y).getId() + " ";
			}
		}
		content += System.lineSeparator();

		content += entityManager.saveString();

		content += System.lineSeparator();

		content += "Continue";

		content += System.lineSeparator();

		Player p = entityManager.getPlayer();

//		for (int i : p.items) {
//			content += " " + i;
//		}
		content += System.lineSeparator();

		content += p.items.size();
		content += System.lineSeparator();

		for (int i = 0; i < p.items.size(); i++) {
			content += (p.items.get(p.items.keySet().toArray()[i])).getClass();
			content += System.lineSeparator();
		}

		Enviornment e = enviornment;

		content += e.getTime() + " " + e.rohundo + " 1 " + e.lapse;

		Utils.fileWriter(content, path);
	}

	public Enviornment getEnviornment() {
		return enviornment;
	}

	public int getSpawnx() {
		return spawnx;
	}

	public int getSpawny() {
		return spawny;
	}

	public void outOfBounds(Entity e) {
		if ((e.getY() > (height + 10) * Tile.TILEHEIGHT) || (e.getX() > (width + 10) * Tile.TILEWIDTH)
				|| (e.getX() < -10 * Tile.TILEWIDTH) || (e.getY() < -15 * Tile.TILEHEIGHT)) {
			if (e.getClass() == Player.class) {
				Player p = (Player) e;
				if (p.lives == 0) {
					kill(e);
				} else {
					p.lives -= 1;
					p.setX(spawnx);
					p.setY(spawny);
					p.setHealth(p.MAX_HEALTH);
					handler.logWorld("Player lives: " + p.lives);
				}
			} else if ((e.getClass() != Cloud.class)
					&& ((e.getClass() == Flower.class) || (e.getClass() == Stone.class))) {
				e.kill();
			} else if (e.getClass() != Cloud.class) {
				kill(e);
			}
		}
	}

	public void kill(Entity e) {

		for (EntityAdder a : entityManager.adders) {
			if (e.getClass() == a.c && origEntities.containsKey(a.name)) {
				origEntities.put(a.name, origEntities.get(a.name) - 1);
			}
		}

		if ((e.getClass() != Stone.class) && (e.getClass() != Player.class)) {
			e.dead = true;
			e.kill();
		}
		handler.logWorld("Killed: " + e);
		if (e.getClass() == Flower.class) {
			Flower f = (Flower) e;

			if (f.getType() == 0) {
				flower0 -= 1;
			} else if (f.getType() == 1) {
				flower1 -= 1;
			} else {
				flower2 -= 1;
				for (int i = 0; i < (int) Math.ceil(Math.random() * 3.0D); i++) {
					handler.getWorld().getItemManager()
							.addItem(Item.bluePetal.createNew((int) f.getX(), (int) f.getY()));
				}
			}
		} else if (e.getClass() == Stone.class) {
			Stone s = (Stone) e;
			int i = 1;
			if (s.getType() == 0) {
				if (s.getOrType() == 0) {
					stone0 -= 1;
				} else if (s.getOrType() == 1) {
					stone1 -= 1;
				} else {
					stone2 -= 1;
				}
				s.kill();
				i = 1;
			} else if (s.getType() == 1) {
				s.setType(s.getType() - 1);
				i = 2;
			} else {
				s.setType(s.getType() - 1);
				i = 3;
			}

			if (!loading) {
				for (int b = 0; b < i; b++) {
					itemManager.addItem(Item.metal.createNew((int) s.getX(), (int) (s.getY() - 36.0D)));
				}
			}
		} else if (e.getClass() == com.zandgall.arvopia.entity.statics.Soil.class) {
			for (int V = 0; V < Public.random(1.0D, 3.0D); V++)
				itemManager.addItem(Item.dirt.createNew((int) Public.random(e.getX() - 18.0D, e.getX() + 18.0D),
						(int) e.getY() + Tile.TILEHEIGHT * 2));
		} else if (e.getClass() == Butterfly.class) {
			butterfly -= 1;
		} else if (e.getClass() == Bee.class) {
			bee -= 1;
		} else if (e.getClass() == Fox.class) {
			fox -= 1;
		} else if (e.getClass() == Tree.class) {
			youngTrees -= 1;

			Tree t = (Tree) e;

			for (int b = 0; b < (t.getAge() / 2 - 3) * 2; b++) {
				itemManager.addItem(Item.wood.createNew((int) (e.getX() + e.getbounds().x),
						(int) (e.getY() + e.getbounds().y - 36.0D)));
			}
		} else if(e instanceof Cannibal) {
			Cannibal c = (Cannibal) e;
			cannibal--;
		}
		if (e.getClass() == Player.class) {
			Player p = (Player) e;
			if (p.lives == 0) {
				e.kill();
				dead = true;
				dead = true;
			} else {
				p.lives -= 1;
				p.setX(spawnx);
				p.setY(spawny);
				p.setHealth(p.MAX_HEALTH);
				handler.logWorld("Player lives: " + p.lives);
			}
		}

		if (e instanceof Shrubbery)
			if (((Shrubbery) e).type < 3)
				if (shrubs.size() > ((Shrubbery) e).xin
						&& shrubs.get(((Shrubbery) e).xin).size() > ((Shrubbery) e).index)
					shrubs.get(((Shrubbery) e).xin).get(((Shrubbery) e).index).set(2, 0);

		if ((e.getClass() == center.getClass()) && (dead)) {
			for (Entity e2 : entityManager.getEntities()) {
				if (e2.creature) {
					center = e2;
					handler.logWorld("Centered on: " + e2);
					return;
				}
			}
			if (entityManager.getEntities().size() > 0) {
				center = ((Entity) entityManager.getEntities().get(0));
				handler.logWorld("Centered on: " + center);
				waitingForCreature = true;
			} else {
				handler.logWorld("No more entities to center on");
				waitingForCreature = true;
			}
		}
	}

	public static int getWidth() {
		return width;
	}

	public static void setWidth(int width) {
		World.width = width;
	}

	public static int getHeight() {
		return height;
	}

	public static void setHeight(int height) {
		World.height = height;
	}

	public void highestTile() {
		handler.logWorld("Getting highest tiles...");
		heights = new ArrayList<ArrayList<Integer>>();
		tops = new ArrayList<ArrayList<Integer>>();
		shrubs = new ArrayList<ArrayList<ArrayList<Integer>>>();
		for (int x = 0; x < width + 2; x++) {
			handler.logWorldSilent("Tile chosen: " + x);
			heights.add(new ArrayList<Integer>());
			tops.add(new ArrayList<Integer>());
			shrubs.add(new ArrayList<ArrayList<Integer>>());
			for (int y = 0; y < height + 1; y++) {
				handler.logWorldSilent("Checking tile: (" + x + ", " + y + ")");

				if ((getTile(x, y).isTop()) || (getTile(x, y).isSolid())) {
					(tops.get(x)).add(y);
				}

				// TODO

				if (y > 0 && !getTile(x, y - 1).isSolid()) {
					if (getTile(x, y).isSolid())
						heights.get(x).add(y);
					if (getTile(x, y).supportsShrubbery()) {
						shrubs.get(x).add(new ArrayList<Integer>());
						shrubs.get(x).get(shrubs.get(x).size() - 1).add(y);
						if (x != 0 && !getTile(x - 1, y).isSolid())
							shrubs.get(x).get(shrubs.get(x).size() - 1).add(0);
						else if (x != width - 1 && !getTile(x + 1, y).isSolid())
							shrubs.get(x).get(shrubs.get(x).size() - 1).add(2);
						else
							shrubs.get(x).get(shrubs.get(x).size() - 1).add(1);

						shrubs.get(x).get(shrubs.get(x).size() - 1).add(1);

						if (save == "") {
							entityManager.addEntity(new Shrubbery(handler, x * Tile.TILEWIDTH,
									(y - 1) * Tile.TILEHEIGHT, shrubs.get(x).get(shrubs.get(x).size() - 1).get(1), x,
									shrubs.get(x).size() - 1), false);
							shrubbery++;
						}
					}
				}

				/*
				 * if ((v == 1) || (v == 2) || (v == 3) || (v == 10) || (v == 11) || (v == 12)
				 * || (v == 13) || (v == 16)) { handler.logWorldSilent("Tile (" + x + ", " + y +
				 * ") is solid"); (heights.get(x)).add(y);
				 * 
				 * if ((v == 1) || (v == 10) || (v == 13) || (v == 16)) { shrubs.get(x).add(new
				 * ArrayList<Integer>()); shrubs.get(x).get(shrubs.get(x).size() - 1).add(y);
				 * shrubs.get(x).get(shrubs.get(x).size() - 1).add(0);
				 * shrubs.get(x).get(shrubs.get(x).size() - 1).add(1); if (save == "")
				 * entityManager.addEntity(new Shrubbery(handler, x * Tile.TILEWIDTH, (y - 1) *
				 * Tile.TILEHEIGHT, 0, x, shrubs.get(x).size() - 1), false); } else if ((v == 2)
				 * || (v == 11)) { shrubs.get(x).add(new ArrayList<Integer>());
				 * shrubs.get(x).get(shrubs.get(x).size() - 1).add(y);
				 * shrubs.get(x).get(shrubs.get(x).size() - 1).add(1);
				 * shrubs.get(x).get(shrubs.get(x).size() - 1).add(1); if (save == "")
				 * entityManager.addEntity(new Shrubbery(handler, x * Tile.TILEWIDTH, (y - 1) *
				 * Tile.TILEHEIGHT, 1, x, shrubs.get(x).size() - 1), false); } else if ((v == 3)
				 * || (v == 12)) { shrubs.get(x).add(new ArrayList<Integer>());
				 * shrubs.get(x).get(shrubs.get(x).size() - 1).add(y);
				 * shrubs.get(x).get(shrubs.get(x).size() - 1).add(2);
				 * shrubs.get(x).get(shrubs.get(x).size() - 1).add(1); if (save == "")
				 * entityManager.addEntity(new Shrubbery(handler, x * Tile.TILEWIDTH, (y - 1) *
				 * Tile.TILEHEIGHT, 2, x, shrubs.get(x).size() - 1), false); }
				 * 
				 * heights.add(new ArrayList<Integer>()); }
				 */
			}

			if ((heights.get(x)).size() <= 0) {
				handler.logWorldSilent("404: No tile found");
				(heights.get(x)).add((-Tile.TILEHEIGHT * 2));

				if ((tops.get(x)).size() <= 0) {
					(tops.get(x)).add((-Tile.TILEHEIGHT * 2));
				}
			}
		}
	}

	public int getHighest(double x) {
		int nx = Public.grid(x, Tile.TILEWIDTH, 0.0D);

		if (((ArrayList<?>) heights.get(nx)).size() > 0) {
			return ((Integer) ((ArrayList<?>) heights.get(nx)).get(0)).intValue();
		}
		return -1;
	}

	public int getLowest(double x) {
		int nx = (int) (x / 18);

		nx = (int) Public.range(0, heights.size() - 1, nx);

		if (heights.get(nx).size() > 0) {
			return heights.get(nx).get(heights.get(nx).size() - 1);
		}
		return -1;
	}

	public ArrayList<Integer> getHeights(int x) {
		return heights.get(x);
	}

	public ArrayList<ArrayList<Integer>> getHeights() {
		return heights;
	}

	public int randomHeight(int x) {
		int length = heights.get(x).size();
		return heights.get(x).get((int) Public.random(0, length - 1));
	}

	public int getHigher(double x, double y, int threshold) {
		int nx = Public.grid(x, Tile.TILEWIDTH, 0.0D);

		int dist = threshold;
		int out = -1;

		for (int oy = 0; oy < ((ArrayList<?>) tops.get(nx)).size(); oy++) {
			if (Public.difference(((Integer) ((ArrayList<?>) tops.get(nx)).get(oy)).intValue() * Tile.TILEHEIGHT,
					y) < dist) {
				dist = (int) Public
						.difference(((Integer) ((ArrayList<?>) tops.get(nx)).get(oy)).intValue() * Tile.TILEHEIGHT, y);
				out = ((Integer) ((ArrayList<?>) tops.get(nx)).get(oy)).intValue();
			}
		}

		return out;
	}

	public boolean checkCollision(int x, int y) {
		x /= Tile.TILEWIDTH;
		y /= Tile.TILEHEIGHT;
		if (getTile(x, y).isSolid()) {
			return true;
		}
		return false;
	}

	private void addFlower(int amount, int type) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width * Tile.TILEWIDTH + 1.0D);
			double l = Public.random(-2.0D, 0.0D);
			entityManager.addEntity(new Flower(handler, x,
					(((Integer) ((ArrayList<?>) heights.get(x / Tile.TILEWIDTH)).get(
							(int) Public.random(0.0D, ((ArrayList<?>) heights.get(x / Tile.TILEWIDTH)).size() - 1)))
									.intValue()
							- 1) * Tile.TILEHEIGHT,
					type, l), true);
		}
	}

	private void addStone(int amount, int type) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1.0D) * Tile.TILEWIDTH;
			entityManager.addEntity(new Stone(handler, x,
					(((Integer) ((ArrayList<?>) heights.get(x / Tile.TILEWIDTH)).get(
							(int) Public.random(0.0D, ((ArrayList<?>) heights.get(x / Tile.TILEWIDTH)).size() - 1)))
									.intValue()
							- 1) * Tile.TILEHEIGHT,
					type), true);
		}
	}

	private void addTrees(int amount, int agemin, int agemax) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width * Tile.TILEWIDTH + 1.0D);
			entityManager.addEntity(new Tree(handler, x - 18,
					(((heights.get(x / Tile.TILEWIDTH)).get(
							(int) Public.random(0.0D, (heights.get(x / Tile.TILEWIDTH)).size() - 1)))) * Tile.TILEHEIGHT,
					(int) Public.random(agemin, agemax)), true);
		}
	}

	private void addBee(int amount, long timer) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1.0D);
			int y = (int) (Math.random() * height + 1.0D);

			while (getTile(x, y).isSolid()) {
				x = (int) (Math.random() * width + 1.0D);
				y = (int) (Math.random() * height + 1.0D);
			}

			entityManager.addEntity(new Bee(handler, x * Tile.TILEWIDTH, y * Tile.TILEHEIGHT, false, timer), true);
		}
	}

	private void addButterfly(int amount, long timer) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1.0D);
			int y = (int) (Math.random() * height + 1.0D);

			while (getTile(x, y).isSolid()) {
				x = (int) (Math.random() * width + 1.0D);
				y = (int) (Math.random() * height + 1.0D);
			}

			entityManager.addEntity(new Butterfly(handler, x * Tile.TILEWIDTH, y * Tile.TILEHEIGHT, false, timer),
					true);
		}
	}

	private void addFox(int amount) {
//		addBear(amount);
//		addWolf(amount);
//		addSkunk(amount);
//		addBat(amount);
//		addFairy(amount);
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1.0D) * Tile.TILEWIDTH;

			while (getTile(x, 0).isSolid()) {
				x = (int) (Math.random() * width + 1.0D) * Tile.TILEWIDTH;
			}

			int ix = (int) (x / Tile.TILEWIDTH);

			int y = (heights.get(ix).size() > 0 ? (heights.get(ix).get(0) - 2) * Tile.TILEHEIGHT : 0);

			entityManager.addEntity(new Fox(handler, x, y), true);
		}
	}

	private void addBear(int amount) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1.0D) * Tile.TILEWIDTH;

			while (getTile(x, 0).isSolid()) {
				x = (int) (Math.random() * width + 1.0D) * Tile.TILEWIDTH;
			}

			int ix = (int) (x / Tile.TILEWIDTH);

			int y = (heights.get(ix).size() > 0 ? (heights.get(ix).get(0) - 2) * Tile.TILEHEIGHT : 0);

			entityManager.addEntity(new Bear(handler, x, y), true);
		}
	}

	private void addWolf(int amount) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1.0D) * Tile.TILEWIDTH;

			while (getTile(x, 0).isSolid()) {
				x = (int) (Math.random() * width + 1.0D) * Tile.TILEWIDTH;
			}

			int ix = (int) (x / Tile.TILEWIDTH);

			int y = (heights.get(ix).size() > 0 ? (heights.get(ix).get(0) - 2) * Tile.TILEHEIGHT : 0);

			entityManager.addEntity(new Wolf(handler, x, y), true);
		}
	}

	private void addBat(int amount) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1.0D);
			int y = (int) (Math.random() * height + 1.0D);

			while (getTile(x, y).isSolid()) {
				x = (int) (Math.random() * width + 1.0D);
				y = (int) (Math.random() * height + 1.0D);
			}

			entityManager.addEntity(new Bat(handler, x * Tile.TILEWIDTH, y * Tile.TILEHEIGHT), true);
		}
	}

	private void addFairy(int amount) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1.0D);
			int y = (int) (Math.random() * height + 1.0D);

			while (getTile(x, y).isSolid()) {
				x = (int) (Math.random() * width + 1.0D);
				y = (int) (Math.random() * height + 1.0D);
			}

			entityManager.addEntity(new Fairy(handler, x * Tile.TILEWIDTH, y * Tile.TILEHEIGHT), true);
		}
	}

	private void addSkunk(int amount) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1.0D) * Tile.TILEWIDTH;

			while (getTile(x, 0).isSolid()) {
				x = (int) (Math.random() * width + 1.0D) * Tile.TILEWIDTH;
			}

			int ix = (int) (x / Tile.TILEWIDTH);

			int y = (heights.get(ix).size() > 0 ? (heights.get(ix).get(0) - 2) * Tile.TILEHEIGHT : 0);

			entityManager.addEntity(new Skunk(handler, x, y), true);
		}
	}

	private void addEntities(int amount, EntityAdder a) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1.0D) * Tile.TILEWIDTH;

			int ix = (int) (x / Tile.TILEWIDTH);
			while (heights.get(ix).size() == 0) {
				x = (int) (Math.random() * width + 1.0D) * Tile.TILEWIDTH;
				ix = (int) (x / Tile.TILEWIDTH);
			}

			int y = (heights.get(ix).size() > 0 ? (heights.get(ix).get(0) - 2) * Tile.TILEHEIGHT : 0);

			System.out.println("Adding : " + a.name + " (" + x + ", " + y + ");");

			a.add(x, y);
		}
	}

	private boolean nameScheming(String name) {
		if (name.contains("Tree"))
			return youngTrees + midTrees + oldTrees < maxTrees;
		if (name.contains("Bee"))
			return bee < maxBee;
		if (name.contains("Butterfly"))
			return butterfly < maxButterfly;
		if (name.contains("Fox"))
			return fox < maxFox;
		if (name.contains("Wolf"))
			return wolf < maxWolf;
		if (name.contains("Bear"))
			return bear < maxBear;
		if (name.contains("Bat"))
			return bat < maxBat;
		if (name.contains("Skunk"))
			return skunk < maxSkunk;
		if (name.contains("Fairy"))
			return fairy < maxFairy;
		if (name.contains("Stone"))
			return stone0 + stone1 + stone2 < maxStone;
		if (name.contains("Flower"))
			return flower0 + flower1 + flower2 < maxFlower;
		if (name.contains("Cannibal"))
			return cannibal < maxCannibals;
		return false;
	}

	public void updateOrigEntities(String name) {
		if (name.contains("Tree"))
			youngTrees++;
		if (name.contains("Bee"))
			bee++;
		if (name.contains("Butterfly"))
			butterfly++;
		if (name.contains("Fox"))
			fox++;
		if (name.contains("Wolf"))
			wolf++;
		if (name.contains("Bear"))
			bear++;
		if (name.contains("Bat"))
			bat++;
		if (name.contains("Skunk"))
			skunk++;
		if (name.contains("Fairy"))
			fairy++;
		if (name.contains("Stone"))
			stone0++;
		if (name.contains("Flower"))
			flower0++;
		if (name.contains("Cannibal"))
			cannibal++;
	}

	public void updateMaxEntities(String name) {
		if (name.contains("Tree"))
			maxTrees++;
		if (name.contains("Bee"))
			maxBee++;
		if (name.contains("Butterfly"))
			maxButterfly++;
		if (name.contains("Fox"))
			maxFox++;
		if (name.contains("Wolf"))
			maxWolf++;
		if (name.contains("Bear"))
			maxBear++;
		if (name.contains("Bat"))
			maxBat++;
		if (name.contains("Skunk"))
			maxSkunk++;
		if (name.contains("Fairy"))
			maxFairy++;
		if (name.contains("Stone"))
			maxStone++;
		if (name.contains("Flower"))
			maxFlower++;
		if (name.contains("Cannibal"))
			maxCannibals++;
	}

	public void updateMaxEntities(String name, int update) {
		if (name.contains("Tree"))
			maxTrees += update;
		if (name.contains("Bee"))
			maxBee += update;
		if (name.contains("Butterfly"))
			maxButterfly += update;
		if (name.contains("Fox"))
			maxFox += update;
		if (name.contains("Wolf"))
			maxWolf += update;
		if (name.contains("Bear"))
			maxBear += update;
		if (name.contains("Bat"))
			maxBat += update;
		if (name.contains("Skunk"))
			maxSkunk += update;
		if (name.contains("Fairy"))
			maxFairy += update;
		if (name.contains("Stone"))
			maxStone += update;
		if (name.contains("Flower"))
			maxFlower += update;
		if (name.contains("Cannibal"))
			maxCannibals += update;
	}

	public void addEntityOnGround(Entity e, double x, double miny, double maxy) {

		boolean pass = true;

		if (e instanceof Creature) {
			if (origEntities.containsKey(((Creature) e).getName()))
				if (origEntities.get(((Creature) e).getName()) < maxEntities.get(((Creature) e).getName()))
					pass = true;
				else
					pass = nameScheming(((Creature) e).getName());
		} else if (origEntities.containsKey(e.getClass().getName())) {
			pass = (origEntities.get(e.getClass().getName()) < maxEntities.get(e.getClass().getName()));
		} else {
			pass = nameScheming(e.getClass().getName());
		}

		if (!pass)
			return;

		int ix = (int) (x / Tile.TILEWIDTH);

		double y = (heights.get(ix).size() > 0 ? (heights.get(ix).get(0)) * Tile.TILEHEIGHT : 0);

		while (y > maxy || y < miny) {
			y = (heights.get(ix).size() > 0
					? (heights.get(ix).get((int) Public.random(0, heights.get(ix).size() - 1))) * Tile.TILEHEIGHT
					: maxy - 1);
		}

		e.setX(x);
		e.setY(y - e.getHeight());
		entityManager.addEntity(e, false);

		if (e instanceof Creature) {
			if (origEntities.containsKey(((Creature) e).getName()))
				origEntities.put(((Creature) e).getName(), origEntities.get(((Creature) e).getName()) + 1);
			else
				updateOrigEntities(((Creature) e).getName());
		} else if (origEntities.containsKey(e.getClass().getName())) {
			origEntities.put(e.getClass().getName(), origEntities.get(e.getClass().getName()) + 1);
		} else {
			updateOrigEntities(e.getClass().getName());
		}
	}

	public void addEntityInAir(Entity e, double x, double y) {

		boolean pass = true;

		if (e instanceof Creature) {
			if (origEntities.containsKey(((Creature) e).getName()))
				if (origEntities.get(((Creature) e).getName()) < maxEntities.get(((Creature) e).getName()))
					pass = true;
				else
					pass = nameScheming(((Creature) e).getName());
		} else if (origEntities.containsKey(e.getClass().getName())) {
			pass = (origEntities.get(e.getClass().getName()) < maxEntities.get(e.getClass().getName()));
		} else {
			pass = nameScheming(e.getClass().getName());
		}

		if (!pass)
			return;

		e.setX(x);
		e.setY(y - e.getHeight());
		entityManager.addEntity(e, false);

		if (e instanceof Creature) {
			if (origEntities.containsKey(((Creature) e).getName()))
				origEntities.put(((Creature) e).getName(), origEntities.get(((Creature) e).getName()) + 1);
			else
				updateOrigEntities(((Creature) e).getName());
		} else if (origEntities.containsKey(e.getClass().getName())) {
			origEntities.put(e.getClass().getName(), origEntities.get(e.getClass().getName()) + 1);
		} else {
			updateOrigEntities(e.getClass().getName());
		}
	}

	public void addConBee(int x, int y, long timer) {
		if (bee < maxBee) {
			entityManager.addEntity(new Bee(handler, x, y, false, timer), true);
		}
	}

	public void addConButterfly(int x, int y, long timer) {
		if (butterfly < maxButterfly) {
			entityManager.addEntity(new Butterfly(handler, x, y, false, timer), true);
		}
	}

	public void addCloud(int amount, int type) {
		for (int i = 0; i < amount; i++) {
			int y = (int) (Math.random() * -handler.getHeight() + cloudY * Tile.TILEHEIGHT);
			double x = Math.random() * (Tile.TILEWIDTH * (width + 8)) - Tile.TILEWIDTH * 4;
			entityManager.addEntity(new Cloud(handler, x, y, type, Math.random() / 2.0D), true);
		}
	}

	private void addCannibalTribe(int amount, int groupX) {
		entityManager.addEntity(new Cannibal(handler, groupX,

				((Integer) ((ArrayList<?>) heights.get(groupX / Tile.TILEWIDTH)).get(
						(int) Public.random(0.0D, ((ArrayList<?>) heights.get(groupX / Tile.TILEWIDTH)).size() - 1)))
								.intValue()
						- 2,
				Public.random(0.1D, 0.6D), 1, true), true);
		for (int i = 0; i < amount - 1; i++) {
			int x = (int) Public.random(groupX - 2, groupX + 2);
			int y = ((Integer) ((ArrayList<?>) heights.get(x / Tile.TILEWIDTH))
					.get((int) Public.random(0.0D, ((ArrayList<?>) heights.get(x / Tile.TILEWIDTH)).size() - 1)))
							.intValue()
					- 2;

			entityManager.addEntity(new Cannibal(handler, x * Tile.TILEWIDTH, y * Tile.TILEHEIGHT,
					Public.random(0.51D, 0.8D), 1, false), true);
		}
	}

	private void addShrubbery(int amount) {
		for (int i = 0; i < amount; i++) {
			int x = (int) Public.random(0.0D, width);
			int y = ((Integer) ((ArrayList<?>) heights.get(x))
					.get((int) Public.random(0.0D, ((ArrayList<?>) heights.get(x)).size() - 1))).intValue() - 1;

			entityManager.addEntity(
					new Shrubbery(handler, x * Tile.TILEWIDTH, y * Tile.TILEHEIGHT, (int) Public.random(3.0D, 4.0D)),
					true);
		}
	}

	public String toString() {
		return "World[" + width + ", " + height + "]";
	}

}
