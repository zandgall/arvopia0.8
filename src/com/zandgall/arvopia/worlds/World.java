package com.zandgall.arvopia.worlds;

import com.zandgall.arvopia.*;
import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.entity.AreaAdder;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.entity.EntityEntry;
import com.zandgall.arvopia.entity.EntityManager;
import com.zandgall.arvopia.entity.creatures.Bee;
import com.zandgall.arvopia.entity.creatures.Butterfly;
import com.zandgall.arvopia.entity.creatures.Cannibal;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.entity.creatures.basic.*;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.entity.creatures.npcs.Lia;
import com.zandgall.arvopia.entity.creatures.npcs.Template;
import com.zandgall.arvopia.entity.creatures.npcs.Villager;
import com.zandgall.arvopia.entity.moveableStatics.Cloud;
import com.zandgall.arvopia.entity.statics.*;
import com.zandgall.arvopia.environment.Environment;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.guis.Inventory;
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
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Noise;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Slider;
import com.zandgall.arvopia.utils.Utils;
import com.zandgall.arvopia.water.WaterManager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

import javax.imageio.ImageIO;

public class World implements Serializable {

	@Serial
	private static final long serialVersionUID = 1125716483561321653L;

	public int state = 0;

	// Loading and drawing time
	public long entities = 1, items = 1, enviorn = 1, water = 1, tilet = 1, entitiesr = 1, itemsr = 1, enviorr = 1,
			waterr = 1, tiler = 1;

	// Loading time
	public long preload, varsload = 1, tilesload = 1, customload = 1, backgroundload = 1, addingload = 1, postload = 1;

	// Spawning time
	public long sFlowers = 1, sStones = 1, sTrees = 1, sOther = 1;

	private Environment environment;
	private Handler handler;

	private static int width, height;

	private int spawnx, spawny;

	static String[][] tiles;
	static String[][] backtiles;

	private int bee;
	private int butterfly;
	private int fox;
	private int wolf;
	private int bear;
	private int bat;
	private int skunk;
	private int fairy;
	private int stone0;
	private int stone1;
	private int stone2;
	private int flower0;
	private int flower1;
	private int flower2;
	private int youngTrees;
	private int midTrees;
	private int oldTrees;
	private int cloud0;
	private int cloud1;
	private int cloud2;
	private int cloud3;
	private int cloudY;
	private int cannibal;
	private int maxBee;
	private int maxButterfly;
	private int maxFox;
	private int maxWolf;
	private int maxBear;
	private int maxBat;
	private int maxSkunk;
	private int maxFairy;
	private int maxStone;
	private int maxFlower;
	private int maxTrees;
	private int maxCannibals;
	public int shrubbery = 0;

	private int render_count = 0;

	private boolean waitingForCreature;

	private final Button respawn;

	private boolean dead;

	private ItemManager itemManager;
	private WaterManager waterManager;
	private ParticleManager particleManager;
	private final EntityManager entityManager;
	private final HashMap<String, Integer> entityCaps, entityCount;

	public Entity center;

	private boolean Box = false;

	private boolean loading;
	public double percentDone;

	private ArrayList<ArrayList<Integer>> heights, tops;

	private ArrayList<ArrayList<ArrayList<Integer>>> shrubs; // x-->index--> y, id


	private final Map map;

	private ArrayList<BufferedImage> backgroundImages, backgroundSnowOverlays;

	public String save = "";
	private String type = "World", path = "";

	public static WorldImageUpdater imageCreator = null;

	public World(Handler handler) {
		long pre = System.nanoTime();
		this.handler = handler;
		percentDone = 0.0D;

		map = new Map(handler);

		Creature.init();
		entityManager = new EntityManager(handler, new Player(handler, 100.0D, 0.0D, false, 2.0D, 3));
		Inventory.ForgeHandler.init(handler, entityManager.getPlayer());
		itemManager = new ItemManager(handler, entityManager.getPlayer());
		waterManager = new WaterManager(handler);
		particleManager = new ParticleManager(handler);
		entityCaps = new HashMap<>();
		entityCount = new HashMap<>();

		center = entityManager.getPlayer();

		waitingForCreature = false;
		loading = true;
		respawn = new Button(handler, handler.getWidth() / 2 - 50, handler.getHeight() / 2 - 25, 100, 25,
				"Respawns the character", "Respawn");

		preload = System.nanoTime() - pre;

		environment = new Environment(handler);
		environment.setupStars();

//		if(imageCreator!=null)
//			try {
//				imageCreator.join();
//			} catch(InterruptedException e) {
//				e.printStackTrace();
//			}

		if(imageCreator==null)
			imageCreator = new WorldImageUpdater();
	}

	public World(Handler handler, String path) {
		this(handler);
		handler.getGame().initMessage("Loading world...");
		this.path = path;
		if (path.contains("/Pack")) {
			loadWorld(path + "/World.arv");
			type = "Pack";
		} else {
			loadWorld(path);
		}

		WorldImageUpdater.update();

		long pre = System.nanoTime();
		Tile.resetSnowiness();

		handler.getGame().initMessage("Loading Custom Entities...");
		postload = System.nanoTime() - pre;
		pre = System.nanoTime();

		entityManager.getPlayer().setX(spawnx);
		entityManager.getPlayer().setY(spawny);

		handler.getGame().initMessage("Loading Background...");
		addingload = System.nanoTime() - pre;
		pre = System.nanoTime();

		backgroundImages = new ArrayList<>();
		backgroundSnowOverlays = new ArrayList<>();

		BufferedImage[] bg1 = createBackground(3);
		backgroundImages.add(bg1[0]);
		backgroundSnowOverlays.add(bg1[1]);
		Noise.init((long) Public.expandedRand(0, 100000));
		BufferedImage[] bg2 = createBackground(4);
		backgroundImages.add(bg2[0]);
		backgroundSnowOverlays.add(bg2[1]);
		Noise.init((long) Public.expandedRand(0, 100000));
		BufferedImage[] bg3 = createBackground(5);
		backgroundImages.add(bg3[0]);
		backgroundSnowOverlays.add(bg3[1]);
		backgroundload = System.nanoTime() - pre;
//		try {
//			ImageIO.write(bg1[0], "png", new File("C:\\Output.png"));
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
	}

	public World(Handler handler, long seed, ArrayList<Slider> sliders) {
		this(handler);

		Creature.init();

		type = "Generation";
		generateWorld(seed, true, sliders);
		WorldImageUpdater.update();
		handler.getGame().initMessage("Loading Custom Entities...");
		long pre = System.nanoTime();

		handler.getGame().initMessage("Making Background...");
		addingload = System.nanoTime() - pre;
		pre = System.nanoTime();

		backgroundImages = new ArrayList<>();
		backgroundSnowOverlays = new ArrayList<>();

		BufferedImage[] bg1 = createBackground(3);
		backgroundImages.add(bg1[0]);
		backgroundSnowOverlays.add(bg1[1]);
		Noise.init((long) Public.expandedRand(0, 100000));
		BufferedImage[] bg2 = createBackground(4);
		backgroundImages.add(bg2[0]);
		backgroundSnowOverlays.add(bg2[1]);
		Noise.init((long) Public.expandedRand(0, 100000));
		BufferedImage[] bg3 = createBackground(5);
		backgroundImages.add(bg3[0]);
		backgroundSnowOverlays.add(bg3[1]);

		backgroundload = System.nanoTime() - pre;
	}

	public void generateWorld(long seed, boolean beginning, ArrayList<Slider> sliders) {
		// If you change this, make sure to change GenerateWorldState preview generation
		if (beginning) {
			handler.getGame().gameState.setLoadingPhase(1);
			State.setState(handler.getGame().gameState);
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

		double stones = 0, foliation = 0, insects = 0, creatures = 0, cannibals = 0;
		Random rand = new Random(seed);
		Noise.init(seed);
		cloud0 = rand.nextInt(width / 50, width / 20);
		cloud1 = rand.nextInt(width / 50, width / 20);
		cloud2 = rand.nextInt(width / 50, width / 20);
		cloud3 = rand.nextInt(width / 50, width / 20);
		cloudY = rand.nextInt(height / 4, height / 2);

		varsload = System.nanoTime() - pre;
		pre = System.nanoTime();

		handler.getGame().initMessage("Loading Tiles...");

		int bh = height * 18;
		double c = rand.nextDouble(-1, 1);
		double y = (Noise.noise1(c) * 10 + (bh / 72.f));

		tiles = new String[width][height];
		backtiles = new String[width][height];

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = "TILE0";
				backtiles[i][j] = "TILE0";
			}
		}

		int[][] caveTiles = new int[width][height];
		// CAVES
		// Done beforehand, both so that preview image in GenerateWorldState is easier to create,
		// But also so that caves are more consistent per world, rather than randomized if the width of a world is
		// changed, and thus rand.next gets more calls before caves are generated
		for (int i = 0; i < caves; i++) {
			Noise n = new Noise(rand.nextLong());

			double radius = 0;
			double momentum = rand.nextDouble(1000);
			double xc = rand.nextDouble(width);
			double yc = 0;
			double r = 5;
			while (yc < height - r - 5) {
				xc = Public.range(0, width, xc);
				xc += n.get1(momentum) * 2;
				yc = Math.max(yc, 0);
				yc += (n.get1(-momentum) + 0.1) * 0.4;

				r = Public.range(1.75, 5.0, n.get1(radius)*5.0+5.0);
				int cx = (int) Public.range(0, tiles.length - 1, xc);
				int cy = (int) Public.range(0, tiles[0].length - 1, yc);
				for (double px = xc - r; px < xc + r; px++) {
					for (double py = yc - r; py < yc + r; py++) {
						int tx = (int) Public.range(0, tiles.length - 1, px);
						int ty = (int) Public.range(0, tiles[0].length - 1, py);

						if (Public.dist(tx, ty, cx, cy) <= r)
							caveTiles[tx][ty] = 1;
					}
				}

				momentum += 0.0025;
				radius += 0.0025;
			}
		}

		int biome = 1;
		double offset = 0;

		for (int i = 0; i < width; i++) {
			y = Public.range(0, height, y);
			if(caveTiles[i][(int) y]==0)
				tiles[i][(int) y] = "TILE" + 2;

			for (int j = (int) y + 1; j < height; j++)
				if(caveTiles[i][j]==0)
					tiles[i][Math.max(0, j)] = "TILE" + 5;

			c += 0.02;

			y = (Noise.noise1(c) * 10 * biome + (bh / 72.f)) + offset;

			if (rand.nextDouble() < 0.01) {
				biome = rand.nextInt(1, 6);
				double original = (y - (bh / 72.f));
				double newer = (Noise.noise1(c) * 20 * biome);
				offset = original - newer;
				System.out.println("Biome: " + biome);
			}
		}

		for (Slider s : sliders) {
			String e = s.getName();

			for (EntityEntry a : entityManager.entityEntries.values()) {
				if (a.name.contains(e) && e.contains(a.name)) {
					entityCaps.put(a.name, (int) (width * s.getWholeValue() * 0.1));
				}
//					maxEntities.put(a.name, (int) (width * d[contInt + 1] * 0.1));
			}

			if (Objects.equals(e, "Stones")) {
				stones = s.getWholeValue();
			}
			if (Objects.equals(e, "Foliation")) {
				foliation = s.getWholeValue();
			}
			if (Objects.equals(e, "Creatures")) {
				creatures = s.getWholeValue();
			}
			if (Objects.equals(e, "Insects")) {
				insects = s.getWholeValue();
			}
			if (Objects.equals(e, "Cannibals")) {
				cannibals = s.getWholeValue();
			}
		}

		for (EntityEntry a : entityManager.entityEntries.values()) {
			if (entityCaps.containsKey(a.name)) {
				entityCount.put(a.name, entityCaps.get(a.name) / 4);
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
		oldTrees = (int) ((width / 45.0) * foliation * 0.1);

		bee = (int) (width * insects * 0.1);
		butterfly = (int) (width * insects * 0.1);
		fairy = (int) (width * insects * 0.1);

		fox = (int) (width * creatures * 0.1);
		wolf = (int) (width * creatures * 0.1);
		bear = (int) (width * creatures * 0.1);
		bat = (int) (width * creatures * 0.1);
		skunk = (int) (width * creatures * 0.1);

		maxCannibals = (int) (width * cannibals * 0.1);
		cannibal = maxCannibals / 2;

		maxStone = (int) (width * stones * 0.1);
		maxFlower = (int) (width * stones * 0.1);
		maxTrees = (int) (width * stones * 0.1);

		maxBee = (int) (width * insects * 0.1);
		maxButterfly = (int) (width * insects * 0.1);
		maxFairy = (int) (width * insects * 0.1);

		maxFox = (int) (width * creatures * 0.1);

//		Todo
//		for (int i = 0; i < tiles.length; i++) {
//			for (int j = 0; j < tiles[0].length; j++) {
//				tiles[i][j] = formatTiles(tiles, i, j);
//				backtiles[i][j] = formatBacktiles(backtiles, i, j);
//			}
//		}
		tilesload = System.nanoTime() - pre;

		highestTile();

		spawnx = 180;
		spawny = getHighest(180.0D)*Tile.HEIGHT;

		environment = new Environment(handler, Public.expandedRand(1.0D, 5.0D), Public.expandedRand(1.0D, 5.0D),
				Public.expandedRand(50.0D, 100.0D));
	}

	public BufferedImage[] createBackground(double index) {
		
		int bw = width * 18;
		int bh = height * 18;

		BufferedImage backgroundImage = new BufferedImage(bw, bh, 6);
		BufferedImage backgroundImageSnowy = new BufferedImage(bw, bh, 6);
		Graphics bg = backgroundImage.getGraphics();
		Graphics sg = backgroundImageSnowy.getGraphics();

		double c = Public.rand(-1.0D, 1.0D);
		if (tiles == null || tiles.length == 0)
			return null;
		double y;

		ArrayList<ArrayList<String>> tiles = new ArrayList<>();
		
		for (int i = 0; i < width; i++) {
			tiles.add(new ArrayList<>());
			for (int j = 0; j < height; j++) {
				tiles.get(i).add("TILE0");
			}
		}

		for (int i = 0; i < width; i++) {

			y = Noise.noise1((i / 100.0) * 2) * 30 + (height / 2.0) - (height / 10.0 * index) / 2;

			tiles.get(i).set(((int) Math.max(0.0D, Math.min(y, height - 1))), "TILE6");

			for (int j = (int) y + 1; j < height; j++) {
				tiles.get(i).set(Math.max(0, j), "TILE5");
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
		
		Tile.formatTiles(tiles, 0, 0, width, height);

		int bs = 0;

		Color fol = new Color(10, 250, 60);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height-1; j++) {
				if (tiles.get(i).get(j).equals("TILE0") && !tiles.get(i).get(j+1).equals("TILE0")) {
					if (entityCaps.containsKey("Stone") && Public.chance(entityCaps.get("Stone")*5.0 / width))
						bg.drawImage(PublicAssets.stone[((int) Public.expandedRand(0.0D, 2.0D))], i * 18, j * 18, 18, 18,
								null);
					else if (entityCaps.containsKey("Flower") && Public.chance(entityCaps.get("Flower")*40.0 / width))
						bg.drawImage(PublicAssets.flower[((int) Public.expandedRand(0.0D, 8.0D))], i * 18, j * 18,
								PublicAssets.flower[0].getWidth(),
								PublicAssets.flower[0].getHeight(), null);
					else if (Public.chance(4)) {
						int f = Public.randInt(4, 6);
						bg.drawImage(Tran.effectImage(new Tran.ShadowBrightColorEffect(new Color(50, 50, 100), fol),
										PublicAssets.shrubbery[f]), i * 18, j * 18, PublicAssets.shrubbery[f].getWidth(),
								PublicAssets.shrubbery[f].getHeight(), null);
						if(f==5)
							bg.drawImage(PublicAssets.shrubbery[12], i * 18, j * 18, PublicAssets.shrubbery[12].getWidth(),
									PublicAssets.shrubbery[12].getHeight(), null);
					}

					if (i > 0 && tiles.get(i-1).get(j+1).equals("TILE0")) {
						bg.drawImage(Tran.effectImage(new Tran.ShadowBrightColorEffect(new Color(50, 50, 100), fol), PublicAssets.shrubbery[0]), i * 18, j * 18, 18, 18, null);
						sg.drawImage(PublicAssets.snowyGrassEntity.getSubimage(0, 0, 18, 18), i * 18, j * 18, null);
					} else if (i < width-1 && tiles.get(i+1).get(j+1).equals("TILE0")) {
						bg.drawImage(Tran.effectImage(new Tran.ShadowBrightColorEffect(new Color(50, 50, 100), fol), PublicAssets.shrubbery[2]), i * 18, j * 18, 18, 18, null);
						sg.drawImage(PublicAssets.snowyGrassEntity.getSubimage(18, 0, 18, 18), i * 18, j * 18, null);
					} else {
						bg.drawImage(Tran.effectImage(new Tran.ShadowBrightColorEffect(new Color(50, 50, 100), fol), PublicAssets.shrubbery[1]), i * 18, j * 18, 18, 18, null);
						sg.drawImage(PublicAssets.snowyGrassEntity.getSubimage(36, 0, 18, 18), i * 18, j * 18, null);
					}

					if (entityCaps.containsKey("Stone") && Public.chance(entityCaps.get("Stone")*10.0 / width))
						bg.drawImage(PublicAssets.stone[((int) Public.expandedRand(0.0D, 2.0D))], i * 18, j * 18, 18, 18,
								null);
					else if (entityCaps.containsKey("Flower") && Public.chance(entityCaps.get("Flower")*40.0 / width))
						bg.drawImage(PublicAssets.flower[((int) Public.expandedRand(0.0D, 8.0D))], i * 18, j * 18,
								PublicAssets.flower[((int) Public.expandedRand(0.0D, 2.0D))].getWidth(),
								PublicAssets.flower[((int) Public.expandedRand(0.0D, 2.0D))].getHeight(), null);
					else if (Public.chance(4)) {
						int f = Public.randInt(4, 6);
						bg.drawImage(Tran.effectImage(new Tran.ShadowBrightColorEffect(new Color(50, 50, 100), fol),
								PublicAssets.shrubbery[f]), i * 18, j * 18, PublicAssets.shrubbery[f].getWidth(),
								PublicAssets.shrubbery[f].getHeight(), null);
						if(f==5)
							bg.drawImage(PublicAssets.shrubbery[12], i * 18, j * 18, PublicAssets.shrubbery[12].getWidth(),
									PublicAssets.shrubbery[12].getHeight(), null);
					}

				}

				if (Tile.getTile(tiles.get(i).get(j)).getImage() != null) {
					bg.drawImage(Tile.getTile(tiles.get(i).get(j)).getImage(), i * 18, j * 18, 18, 18, null);
				}
			}
		}
		
		bg.dispose();
		sg.dispose();

		return new BufferedImage[] {backgroundImage,null};
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

		environment.tick();

		enviorn = (System.nanoTime() - pre);
		pre = System.nanoTime();

		waterManager.tick();
		waterManager.tickCol(entityManager);

		water = (System.nanoTime() - pre);
		pre = System.nanoTime();

		int xStart = (int) Math.max(handler.getGameCamera().getxOffset() / Tile.WIDTH, 0.0F);
		int xEnd = (int) Math.min(width,
				(handler.getGameCamera().getxOffset() + handler.getWidth()) / Tile.WIDTH + 1.0F);
		int yStart = (int) Math.max(handler.getGameCamera().getyOffset() / Tile.HEIGHT, 0.0F);
		int yEnd = (int) Math.min(height,
				(handler.getGameCamera().getyOffset() + handler.getHeight()) / Tile.HEIGHT + 1.0F);

		for (int y = yStart; y < yEnd; y++)
			for (int x = xStart; x < xEnd; x++)
				getTile(x, y).tick(handler, x, y);
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
			Entity e2 = entityManager.getEntities().get((int) Public.expandedRand(0, entityManager.getEntities().size()-1));
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

	public boolean safeToWalk(double xp, double y) {
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

	public boolean safeToWalk(double xc, double y, int width) {
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

	public void spawnEntities() {

		long pre = System.nanoTime();

		if (Public.chance(5) && shrubs.size() > 0) {
			int x = (int) Public.expandedRand(0, shrubs.size() - 1);

			int index = 0;
			
			int max = (width*height)/72;
			
			while (shrubs.get(x).size() == 0 && index < 10) {
				x = (int) Public.expandedRand(0, shrubs.size() - 1);
				index++;
			}

			if (shrubs.get(x).size() != 0) {
				index = (int) Public.expandedRand(0, shrubs.get(x).size() - 1);

				if (Public.chance(5) && shrubbery<max) {
					entityManager.addEntity(new Shrubbery(handler, x * 18 + Public.expandedRand(0, 18),
							shrubs.get(x).get(index).get(0) * 18 - 18, (int) Public.expandedRand(3, 4)), false);
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

		ArrayList<String> availableSpawnableEntities = new ArrayList<>();
		for(String key : entityCaps.keySet())
			if(entityCount.get(key)< entityCaps.get(key)&&entityManager.entityEntries.containsKey(key)
				&& entityManager.entityEntries.get(key).example.canCurrentlySpawn(handler))
				availableSpawnableEntities.add(key);
		if(availableSpawnableEntities.size()==0)
			return;
		String i = availableSpawnableEntities.get((int)(Math.random()*availableSpawnableEntities.size()));
		addEntities(1, entityManager.entityEntries.get(i));
		entityCount.put(i, entityCount.get(i) + 1);

		sOther = System.nanoTime() - pre;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void render(Graphics2D g) {
		if (!OptionState.split_stream_lighting)
			WorldImageUpdater.update();
		else if (WorldImageUpdater.instance == null)
			WorldImageUpdater.instance = new WorldImageUpdater();

		long pre = System.nanoTime();
		g.setClip(0, 0, 720, 405);

		pre = System.nanoTime();
		environment.renderSunMoon(g);
		environment.renderStars(g);
		enviorr = (System.nanoTime() - pre);
		pre = System.nanoTime();

		if (OptionState.render_background)
			for (int i = backgroundImages.size() - 1; i >= 0; i--) {

				double a = i + 1.5;
				BufferedImage b = backgroundImages.get(i);

				int xoff = (int) ((handler.xOffset() / a));
				int yoff = (int) ((handler.yOffset() / a));
				if (xoff < b.getWidth()) {
					g.drawImage(b, -xoff, -yoff, null);
					g.setColor(new Color(80, 100, 180, 128));
				}
//					g2d.drawImage(b.getSubimage(xoff, yoff, 720, 405), 0, 0, null);

//				for (int l = 0; l < Tile.g1.snowyness(0, 0); l++) {
//					g.drawImage(backgroundSnowOverlays.get(i), (int) (-(handler.xOffset() / a) * 1.0 / a),
//							(int) (-(handler.yOffset() / a) * 1.0 / a), null);
//				}
			}

		if (render_count == 0)
			resetGraphics();

		itemsr = System.nanoTime() - pre;
		pre = System.nanoTime();


		AffineTransform previousTransform = g.getTransform();
		g.translate(-handler.xOffset(), -handler.yOffset());
		if(handler.getGame().optionState.getToggle("Glow near sun"))
			entityManager.renderLight(g);
		int sx = environment.sunX() + 9;
		int sy = environment.sunY() + 9;

		if(handler.getGame().optionState.getToggle("Glow near sun"))
			for (int j = sx / 18 - 5; j < sx / 18 + 5; j++)
				for (int k = sy / 18 - 5; k < sy / 18 + 5; k++) {
					if (j < 0 || k < 0 || j >= World.getWidth() || k >= World.getHeight() || !World.ready())
						continue;

					float opacity = Math.max(1 - (float) Public.dist(j, k, sx / 18.f, sy / 18.f) / 5.f, 0.f);

					World.getTile(j, k).lightRender(g, j * 18, k * 18, opacity);
				}

		g.drawImage(WorldImageUpdater.background, WorldImageUpdater.xbOffset*18, WorldImageUpdater.ybOffset*18, null);
		g.drawImage(WorldImageUpdater.foreground, WorldImageUpdater.xfOffset*18, WorldImageUpdater.yfOffset*18, null);

		tiler = (System.nanoTime() - pre);

		pre = System.nanoTime();
		entityManager.render(g, Box);
		entitiesr = (System.nanoTime() - pre);
		pre = System.nanoTime();
		itemManager.render(g, Box);
//		itemsr = (System.nanoTime() - pre);
		particleManager.render(g);
		pre = System.nanoTime();
		waterManager.Render(g, Box);
		waterr = (System.nanoTime() - pre);

//		g2d.translate(-handler.getGameCamera().xOffset, -handler.getGameCamera().yOffset);

		if (dead) {
			respawn.render(g);
		}

		render_count += 1;

		pre = System.nanoTime();
		environment.renderWeather(g);
		g.setTransform(previousTransform);
		environment.render(g);
		environment.renderGui(g);
		enviorr += System.nanoTime() - pre;

		pre = System.nanoTime();

		entityManager.gui(g);

		if (KeyManager.checkBind("Toggle Map"))
			map.render(g);

		waterr = System.nanoTime() - pre;
		
//		handler.getGameCamera().centerOnEntity(center);
	}

	public WaterManager getWaterManager() {
		return waterManager;
	}

	public ItemManager getItemManager() {
		return itemManager;
	}

	public ParticleManager getParticleManager() {
		return particleManager;
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
		if (t == null)
			return Tile.n0;
		return t;
	}

	public static boolean ready() {
		return tiles != null && tiles.length > 0 && tiles[0] != null && tiles[0].length > 0;
	}

	public void loadWorld(String path) {
		Scanner scanner;
		try {
			scanner = new Scanner(FileLoader.loadResource(path));
		} catch(IOException e) {
			try {
				scanner = new Scanner(new File(path));
			} catch(IOException f) {
				System.err.println("Could not load world: \"" + path + "\", as either resource or generic path.");
				e.printStackTrace();
				f.printStackTrace();
				return;
			}
		}

		scanner.nextLine(); // Ignore first line

		float worldVersion = scanner.nextFloat();
		if(worldVersion < 0.8) {
			scanner.close();
//			System.err.println("No longer supports worlds below version 0.8! Contact zandgall if you think there is an issue");
			loadWorld_0_7(path);
			return;
		}
		width = scanner.nextInt();
		height = scanner.nextInt();

		spawnx = scanner.nextInt();
		spawny = scanner.nextInt();

		tiles = new String[width][height];
		backtiles = new String[width][height];

		for(int y = 0; y < height; y++)
			for(int x = 0; x < width; x++)
				tiles[x][y] = scanner.next();
		highestTile();

		for(int y = 0; y < height; y++)
			for(int x = 0; x < width; x++)
				backtiles[x][y] = scanner.next();

		int numOfEntities = scanner.nextInt();
		scanner.nextLine(); // Jump to next line, starting the entities
		for(int i = 0; i < numOfEntities; i++) {
			String[] content = scanner.nextLine().split("\\s+");
			if(!EntityManager.entityEntries.containsKey(content[0])) {
				System.err.printf("Entity \"%s\" Unknown or does not exist%n", content[0]);
				continue; // Entity is unknown or does not exist
			}
			try {
				int x = Integer.parseInt(content[1]);
				int y = Integer.parseInt(content[2]);
				Class<? extends Entity> clazz = EntityManager.entityEntries.get(content[0]).entityClass;
				Class<?>[] parTypes = new Class[3+(content.length-3)/2];
				Object[] par = new Object[parTypes.length];
				parTypes[0] = Handler.class;
				par[0] = handler;
				parTypes[1] = double.class;
				par[1] = (double)x;
				parTypes[2] = double.class;
				par[2] = (double)y;

				for(int j = 0; j < parTypes.length-3; j++)
					switch(content[j*2+3]) {
						case "i" -> {
							parTypes[j+3] = int.class;
							par[j+3] = Integer.parseInt(content[j*2+4]);
						}
						case "f" -> {
							parTypes[j+3] = float.class;
							par[j+3] = Float.parseFloat(content[j*2+4]);
						}
						case "d" -> {
							parTypes[j+3] = double.class;
							par[j+3] = Double.parseDouble(content[j*2+4]);
						}
						case "b" -> {
							parTypes[j+3] = boolean.class;
							par[j+3] = Boolean.parseBoolean(content[j*2+4]);
						}
					}
				entityManager.addEntity(clazz.getConstructor(parTypes).newInstance(par));
			} catch(Exception e) {
				e.printStackTrace();
				System.err.printf("Exception while trying to instantiate entity \"%s\" at (%s, %s)%n", content[0], content[1], content[2]);
			}

		}

		double wind = 5, windChange = 5, windSwing = 100;
		double temperatureOffset = 0;
		while(scanner.hasNext()) {
			String ent = scanner.next();
			double amm = scanner.nextDouble();
			if(EntityManager.entityEntries.containsKey(ent)) {
				entityCaps.put(ent, (int)(width*amm*amm));
				entityCount.put(ent, entityCaps.get(ent)/4);
				addEntities(entityCount.get(ent), EntityManager.entityEntries.get(ent));
			} else if(ent.equalsIgnoreCase("wind"))
				wind = amm;
			else if(ent.equalsIgnoreCase("windChange"))
				windChange = amm;
			else if(ent.equalsIgnoreCase("windSwing"))
				windSwing = amm;
			else if(ent.equalsIgnoreCase("temperatureOffset"))
				temperatureOffset = amm;
		}
		environment = new Environment(handler, wind, windChange, windSwing);
		environment.tempOffset = temperatureOffset;
		environment.setupStars();
	}

	public void loadWorld_0_7(String path) {
		Scanner scanner;
		try {
			scanner = new Scanner(FileLoader.loadResource(path));
		} catch(IOException e) {
			try {
				scanner = new Scanner(new File(path));
			} catch(IOException f) {
				System.err.println("Could not load world: \"" + path + "\", as either resource or generic path.");
				e.printStackTrace();
				f.printStackTrace();
				return;
			}
		}

		scanner.nextLine();
		float version = scanner.nextFloat();
		if(version < 0.7f) {
			scanner.close();
			loadWorld_pre_0_7(path);
			return;
		}

		width = scanner.nextInt();
		height = scanner.nextInt();

		spawnx = scanner.nextInt();
		spawny = scanner.nextInt();

		tiles = new String[width][height];
		backtiles = new String[width][height];
		for(int t = 0; t < width * height; t++)
			tiles[t % width][t / width] = scanner.next();
		highestTile();
		if (scanner.next().equals("BACKTILES"))
			for(int t = 0; t < width * height; t++)
				backtiles[t % width][t / width] = scanner.next();

		int numOfEntities = scanner.nextInt();
		for (int e = 0; e < numOfEntities; e++) {
			if(!scanner.hasNext())
				break;
			String name = scanner.next();
			switch (name) {
				case "Tree" ->
						entityManager.addEntity(new Tree(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt()));
				case "Fairy" ->
						entityManager.addEntity(new Fairy(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextBoolean()));
				case "Wolf" ->
						entityManager.addEntity(new Wolf(handler, scanner.nextDouble(), scanner.nextDouble()));
				case "Bear" ->
						entityManager.addEntity(new Bear(handler, scanner.nextDouble(), scanner.nextDouble()));
				case "Bat" ->
						entityManager.addEntity(new Bat(handler, scanner.nextDouble(), scanner.nextDouble()));
				case "Skunk" ->
						entityManager.addEntity(new Skunk(handler, scanner.nextDouble(), scanner.nextDouble()));
				case "Flower" ->
						entityManager.addEntity(new Flower(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt(), scanner.nextDouble()));
				case "Stone" ->
						entityManager.addEntity(new Stone(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt()));
				case "Cannibal" ->
						entityManager.addEntity(new Cannibal(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt(), scanner.nextBoolean()));
				case "Shrubbery" ->
						entityManager.addEntity(new Shrubbery(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt()));
				case "Butterfly" ->
						entityManager.addEntity(new Butterfly(handler, scanner.nextDouble(), scanner.nextDouble(), false, 100000L));
				case "Bee" ->
						entityManager.addEntity(new Bee(handler, scanner.nextDouble(), scanner.nextDouble(), false, 10000L));
				case "Fox" -> entityManager.addEntity(new Fox(handler, scanner.nextDouble(), scanner.nextDouble()));
				case "Cloud" ->
						entityManager.addEntity(new Cloud(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt(), scanner.nextDouble()));
				case "House" ->
						entityManager.addEntity(new House(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt()));
				case "Villager" ->
						entityManager.addEntity(new Villager(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt()));
				case "NPC" -> {
					double x = scanner.nextDouble(), y = scanner.nextDouble();
					int type = scanner.nextInt();
					String npcName = scanner.next().replaceAll("~", " ");

					java.util.Map<String, String> quests = new HashMap<>();
					String[] speeches = scanner.next().replaceAll("NAME", Reporter.user).replaceAll("`", " ").split("~");

					String questTriggersString = scanner.next();
					String questNamesString = scanner.next();
					if(!questTriggersString.contains("!!!")) {
						String[] questTriggers = questTriggersString.replaceAll("NAME", Reporter.user).replaceAll("`", " ").split("~");
						String[] questNames = questNamesString.replaceAll("`", " ").split("~");
						for (int q = 0; q < questNames.length; q++) {
							quests.put(questTriggers[q], questNames[1]);
						}
					}
					entityManager.addEntity(new Template(handler, x, y, type, name, speeches, quests));
				}
				case "AreaAdder" ->
						entityManager.addEntity(new AreaAdder(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt(), scanner.nextInt(), scanner.next(), scanner.nextDouble()));
				default -> {
					System.err.println("Unrecognized pre-0.7 entity; \"" + path + "\" Entity #" + e + ": \""+name+"\". Scanning one element forward and continuing.");
					e--;
				}
			}
		}

		double wind = 5, windChange = 5, windSwing = 100;
		double temperatureOffset = 0;
		while(scanner.hasNext()) {
			String ent = scanner.next();
			double amm = scanner.nextDouble();
			if(EntityManager.entityEntries.containsKey(ent)) {
				entityCaps.put(ent, (int)(width*amm*amm));
				entityCount.put(ent, entityCaps.get(ent)/4);
				addEntities(entityCount.get(ent), EntityManager.entityEntries.get(ent));
			} else if(ent.equalsIgnoreCase("wind"))
				wind = amm;
			else if(ent.equalsIgnoreCase("windChange"))
				windChange = amm;
			else if(ent.equalsIgnoreCase("windSwing"))
				windSwing = amm;
			else if(ent.equalsIgnoreCase("temperatureOffset"))
				temperatureOffset = amm;
		}
		environment = new Environment(handler, wind, windChange, windSwing);
		environment.tempOffset = temperatureOffset;
		environment.setupStars();
		EntityManager.entityEntries.forEach((k,v) -> {
			if (entityCaps.get(k) == null)
				entityCaps.put(k, 0);
			entityCount.put(k, entityCaps.get(k) / 4);
			addEntities(entityCount.get(k), v);
		});
	}

	public void loadWorld_pre_0_7(String path) {
		Scanner scanner;
		try {
			scanner = new Scanner(FileLoader.loadResource(path));
		} catch(IOException e) {
			try {
				scanner = new Scanner(new File(path));
			} catch(IOException f) {
				System.err.println("Could not load world: \"" + path + "\", as either resource or generic path.");
				e.printStackTrace();
				f.printStackTrace();
				return;
			}
		}

		scanner.nextLine();
		float version = scanner.nextFloat();

		width = scanner.nextInt();
		height = scanner.nextInt();

		spawnx = scanner.nextInt() * Tile.WIDTH;
		spawny = scanner.nextInt() * Tile.HEIGHT;

		for(int i = 0; i < 12; i++)
			scanner.nextInt(); // Skip unused world values
		int cannibalTribes = scanner.nextInt();
		int minPerTribe = scanner.nextInt();
		int maxPerTribe = scanner.nextInt();

		entityCaps.put("Stone", scanner.nextInt());
		entityCaps.put("Flower", scanner.nextInt());
		entityCaps.put("Tree", scanner.nextInt());
		entityCaps.put("Bee", scanner.nextInt());
		entityCaps.put("Butterfly", scanner.nextInt());
		entityCaps.put("Fox", scanner.nextInt());
		int maxCannibalTribes = scanner.nextInt();
		environment = new Environment(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextDouble());
		scanner.nextInt(); // Cloud stuff
		scanner.nextInt();
		scanner.nextInt();
		scanner.nextInt();
		scanner.nextInt();

		tiles = new String[width][height];
		backtiles = new String[width][height];
		for(int t = 0; t < width * height; t++) {
			int oldTile = scanner.nextInt();
			if(oldTile >= Tile.pre_0_7_identities.length)
				tiles[t % width][t / width] = "TILE0";
			else
				tiles[t % width][t / width] = Tile.pre_0_7_identities[oldTile];
			backtiles[t % width][t / width] = "TILE0";
		}
		highestTile();

		for(int i = 0; i < cannibalTribes; i++)
			addCannibalTribe(Public.randInt(minPerTribe, maxPerTribe), Public.randInt(width)*Tile.WIDTH);

		entityManager.addEntity(new Lia(handler, (width - 10)*Tile.WIDTH,
				getLowest(Public.range(0, width * Tile.WIDTH, (width - 10) * Tile.WIDTH))*Tile.WIDTH), false);

		int num_entities = scanner.nextInt();

		for (int e = 0; e < num_entities; e++) {
			if(!scanner.hasNext())
				break;
			String name = scanner.next();
			switch (name) {
				case "Tree" ->
						entityManager.addEntity(new Tree(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt()));
				case "Flower" ->
						entityManager.addEntity(new Flower(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt(), scanner.nextDouble()));
				case "Stone" ->
						entityManager.addEntity(new Stone(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt()));
				case "Cannibal" ->
						entityManager.addEntity(new Cannibal(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt(), scanner.nextBoolean()));
				case "Shrubbery" ->
						entityManager.addEntity(new Shrubbery(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt()));
				case "Butterfly" ->
						entityManager.addEntity(new Butterfly(handler, scanner.nextDouble(), scanner.nextDouble(), false, 100000L));
				case "Bee" ->
						entityManager.addEntity(new Bee(handler, scanner.nextDouble(), scanner.nextDouble(), false, 10000L));
				case "Fox" -> entityManager.addEntity(new Fox(handler, scanner.nextDouble(), scanner.nextDouble()));
				case "Cloud" ->
						entityManager.addEntity(new Cloud(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt(), scanner.nextDouble()));
				case "House" ->
						entityManager.addEntity(new House(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt()));
				case "Villager" ->
						entityManager.addEntity(new Villager(handler, scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt()));
				case "NPC" -> {
					double x = scanner.nextDouble(), y = scanner.nextDouble();
					int type = scanner.nextInt();
					String npcName = scanner.next().replaceAll("~", " ");

					java.util.Map<String, String> quests = new HashMap<>();
					String[] speeches = scanner.next().replaceAll("NAME", Reporter.user).replaceAll("`", " ").split("~");

					String questTriggersString = scanner.next();
					String questNamesString = scanner.next();
					if(!questTriggersString.contains("!!!")) {
						String[] questTriggers = questTriggersString.replaceAll("NAME", Reporter.user).replaceAll("`", " ").split("~");
						String[] questNames = questNamesString.replaceAll("`", " ").split("~");
						for (int q = 0; q < questNames.length; q++) {
							quests.put(questTriggers[q], questNames[q]);
						}
					}
					entityManager.addEntity(new Template(handler, x, y, type, name, speeches, quests));
				}
				default -> {
					System.err.println("Unrecognized pre-0.7 entity; \"" + path + "\" Entity #" + e + ": \""+name+"\". Scanning one element forward and continuing.");
					e--;
				}
			}
		}

		while(scanner.hasNext()) {
			switch(scanner.next()) {
				case "Bears" -> {
					entityCaps.put("Bear", scanner.nextInt());
				}
				case "Wolves" -> {
					entityCaps.put("Wolf", scanner.nextInt());
				}
				case "Skunks" -> {
					entityCaps.put("Skunk", scanner.nextInt());
				}
				case "Fairies" -> {
					entityCaps.put("Fairy", scanner.nextInt());
				}
				case "Birds" -> {
					entityCaps.put("Bird", scanner.nextInt());
				}
			}
		}

		environment.setupStars();
		EntityManager.entityEntries.forEach((k,v) -> {
			if (entityCaps.get(k) == null)
				entityCaps.put(k, 0);
			entityCount.put(k, entityCaps.get(k) / 4);
			addEntities(entityCount.get(k), v);
		});
	}

	public void loadWorld_0_7_(String path) {
		String[] s;
		String[] lines;
		double[] d;
		int[] i;
		if (path.startsWith("/")) {
			try {
				s = FileLoader.streamToString(FileLoader.loadResource(path), path.length()).split("\\s+");
				lines = FileLoader.streamToString(FileLoader.loadResource(path), path.length()).split(System.lineSeparator());
			} catch (IOException e) {
				s = new String[1];
				e.printStackTrace();
			}
		} else {
			File f = new File(path);

			s = FileLoader.readFile(f).split("\\s+");
			lines = FileLoader.readFile(f).split(System.lineSeparator());
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

		if (d[1] < 0.7) {
			System.err.println("This program no longer supports worlds from versions below 0.7, please load a different world, or contact zandgall if this is an issue");
			return;
		}

		if (s[0].contains("World") || s[0].contains("Save")) {
			o = 2;
		}

		width = i[o];
		height = i[o + 1];


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

		cloudY = 20;

		double wind = 5, windChange = 5, windSwing = 100;
		double temperatureOffset = 0;

		environment = new Environment(handler, wind, windChange, windSwing);

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

		tilesload = System.nanoTime() - pre;

		highestTile();

		pre = System.nanoTime();

		if (s.length <= width * height + o) {
			state = 0;
			customload = 0;
			return;
		}
		int contInt = width * height + o;
		int line = 4 + width*height;
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

		if (s.length > width * height + o) {
			handler.logWorld("Adding custom entities... " + (s.length > width * height + 34));

//			contInt = handleEntityLoading(i, d, s, o);
			int size = Integer.parseInt(s[contInt]);

			for(int e = 0; e < size; e++) {
				String ent = s[contInt];
				if(EntityManager.entityEntries.containsKey(ent)) {

				}
			}
		}

		while (contInt + 1 < s.length) {
			if (d[contInt + 1] > 10) {
				contInt++;
				continue;
			}

			String e = s[contInt].strip();

			if(entityManager.entityEntries.containsKey(e)) {
				entityCaps.put(e, (int) (width * d[contInt + 1] * d[contInt+1]));
				entityCount.put(e, entityCaps.get(e) / 4);
				addEntities(entityCaps.get(e) / 4, entityManager.entityEntries.get(e));
			}

			if (e.contains("Wind "))
				wind = d[contInt + 1];
			else if (e.contains("WindChange "))
				wind = d[contInt + 1];
			else if (e.contains("WindSwing "))
				windSwing = d[contInt + 1];

			contInt++;
		}

		environment = new Environment(handler, wind, windChange, windSwing);
		environment.tempOffset = temperatureOffset;
		environment.setupStars();

		customload = System.nanoTime() - pre;
	}

	private int handleEntityLoading(int[] i, double[] d, String[] s, int o) {
		int contInt = width * height + o;

		int size = i[contInt];

		int PRECONTINT = contInt;

		handler.getGame().initMessage("Loading " + size + " custom entities...");

		for (int v = 0; v < size;) {
			if (contInt >= s.length - 3) {
				break;
			}

			String e = s[(contInt + 1)].replaceAll(" ", "") + " ";

			for (EntityEntry a : entityManager.entityEntries.values()) {
				if (e.contains(a.name.replaceAll(" ", "") + " ")) {
					a.spawn(d[(contInt + 2)], d[(contInt + 3)]);
					v++;
				}
			}

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
				entityManager.addEntity(new AreaAdder(handler, d[(contInt + 3)], d[(contInt + 4)], i[(contInt + 5)],
						i[(contInt + 6)], s[contInt + 2], d[contInt + 7]), true);
				contInt += 6;
			} else {
				contInt++;
			}
		}

		return contInt;
	}

	public void loadSave(String path) {

		save = path;

		openWorldData(path + "/World.arv");

		Tile.resetSnowiness();

		openBGImage(path);

		environment.setupStars();

		handler.loadMods();

		type = "Save";
		handler.getGame().initMessage("Loading Environment...");
		openEnvironment(path + "/Environment.arv");
		handler.getGame().initMessage("Loading Entities...");
		handler.loadMods(path + "/mods");
		openEntities(path + "/Entities.arv");
		handler.getGame().initMessage("Loading Player...");
		openPlayer(path + "/Player.arv");
		handler.getGame().initMessage("Loading quests...");
		openQuests(path + "/Quests.arv");
		openAchievements(path + "/Achievements.arv");

		handler.getGame().initMessage("Finishing up...");

		Initiator.aloadSave(handler, path);

		finish(false);

	}

	public void saveWorld(String path) {

		Utils.createDirectory(path);

		Utils.createDirectory(path + "/mods");
		if (type == "Pack") {
			try {
				for (String f : new File(this.path + "/mods").list())
					Files.copy(new File(this.path + "/mods/" + f).toPath(), new File(path + "/mods/" + f).toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (type != "Save") {
			try {
				for (String f : new File(Game.prefix + "/Arvopia/mods").list())
					Files.copy(new File(Game.prefix + "/Arvopia/mods/" + f).toPath(), new File(path + "/mods/" + f).toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		writeWorldData(path + "/World.arv");

		writeBGImage(path);

		writeEnvironment(path + "/Environment.arv");

		writeEntities(path + "/Entities.arv");

		writePlayer(path + "/Player.arv");

		writeQuests(path + "/Quests.arv");

		writeAchievements(path + "/Achievements.arv");

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
				ImageIO.write(backgroundImages.get(i), "png", new File(path + "/BGImage" + i + ".png"));
				ImageIO.write(backgroundSnowOverlays.get(i), "png", new File(path + "/BGImageSnowy" + i + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeEnvironment(String path) {

		String content = "";

		content += "" + environment.lapse + " " + environment.rohundo + " " + environment.getTime()
				+ System.lineSeparator();
		content += "" + environment.getHumidity() + " " + environment.precipitation;

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
				backgroundImages.add(ImageLoader.loadImageEX(path + "/" + s));
			if (s.contains("BGImageSnowy"))
				backgroundSnowOverlays.add(ImageLoader.loadImageEX(path + "/" + s));
		}
	}

	public void openEnvironment(String path) {
		String[] t = FileLoader.readFile(new File(path)).split("\\s+");
		environment.lapse = Utils.parseInt(t[0]);
		environment.rohundo = Utils.parseInt(t[1]);
		environment.setTime(Utils.parseLong(t[2]));
		environment.setHumidity(Utils.parseDouble(t[3]));
		environment.precipitation = Utils.parseBoolean(t[4]);
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

		content += environment.getMaxWind()*2 + " ";
		content += environment.getWindChange() + " ";
		content += environment.getWindSwing() + System.lineSeparator();

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

		Environment e = environment;

		content += e.getTime() + " " + e.rohundo + " 1 " + e.lapse;

		Utils.fileWriter(content, path);
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void outOfBounds(Entity e) {
		if ((e.getY() > (height + 10) * Tile.HEIGHT) || (e.getX() > (width + 10) * Tile.WIDTH)
				|| (e.getX() < -10 * Tile.WIDTH) || (e.getY() < -15 * Tile.HEIGHT)) {
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

		for (EntityEntry a : entityManager.entityEntries.values()) {
			if (e.getClass() == a.entityClass && entityCount.containsKey(a.name)) {
				entityCount.put(a.name, entityCount.get(a.name) - 1);
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
			for (int V = 0; V < Public.expandedRand(1.0D, 3.0D); V++)
				itemManager.addItem(Item.dirt.createNew((int) Public.expandedRand(e.getX() - 18.0D, e.getX() + 18.0D),
						(int) e.getY() + Tile.HEIGHT * 2));
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
			heights.add(new ArrayList<Integer>());
			tops.add(new ArrayList<Integer>());
			shrubs.add(new ArrayList<ArrayList<Integer>>());
			for (int y = 0; y < height + 1; y++) {

				if ((getTile(x, y).isTop()) || (getTile(x, y).isSolid())) {
					(tops.get(x)).add(y);
				}

				// TODO

				if (y > 0 && !getTile(x, y - 1).isSolid()) {
					if (getTile(x, y).isSolid())
						heights.get(x).add(y);
					if (getTile(x, y).isOrganic()) {
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
							entityManager.addEntity(new Shrubbery(handler, x * Tile.WIDTH,
									(y - 1) * Tile.HEIGHT, shrubs.get(x).get(shrubs.get(x).size() - 1).get(1), x,
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
				(heights.get(x)).add((-Tile.HEIGHT * 2));

				if ((tops.get(x)).size() <= 0) {
					(tops.get(x)).add((-Tile.HEIGHT * 2));
				}
			}
		}
	}

	public int getHighest(double x) {
		int nx = Public.grid(x, Tile.WIDTH, 0.0D);

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

	public int randomHeight(int x) {
		int length = heights.get(x).size();
		return heights.get(x).get((int) Public.expandedRand(0, length - 1));
	}

	public boolean checkCollision(int x, int y) {
		x /= Tile.WIDTH;
		y /= Tile.HEIGHT;
		return getTile(x, y).isSolid();
	}

	private void addFlower(int amount, int type) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width * Tile.WIDTH + 1.0D);
			double l = Public.expandedRand(-2.0D, 0.0D);
			entityManager.addEntity(new Flower(handler, x,
					(((Integer) ((ArrayList<?>) heights.get(x / Tile.WIDTH)).get(
							(int) Public.expandedRand(0.0D, ((ArrayList<?>) heights.get(x / Tile.WIDTH)).size() - 1)))
									.intValue()
							- 1) * Tile.HEIGHT,
					type, l), true);
		}
	}

	private void addStone(int amount, int type) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1.0D) * Tile.WIDTH;
			entityManager.addEntity(new Stone(handler, x,
					(((Integer) ((ArrayList<?>) heights.get(x / Tile.WIDTH)).get(
							(int) Public.expandedRand(0.0D, ((ArrayList<?>) heights.get(x / Tile.WIDTH)).size() - 1)))
									.intValue()
							- 1) * Tile.HEIGHT,
					type), true);
		}
	}

	private void addTrees(int amount, int agemin, int agemax) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width * Tile.WIDTH + 1.0D);
			entityManager.addEntity(new Tree(handler, x - 18,
					(((heights.get(x / Tile.WIDTH)).get(
							(int) Public.expandedRand(0.0D, (heights.get(x / Tile.WIDTH)).size() - 1)))) * Tile.HEIGHT,
					(int) Public.expandedRand(agemin, agemax)), true);
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

			entityManager.addEntity(new Bee(handler, x * Tile.WIDTH, y * Tile.HEIGHT, false, timer), true);
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

			entityManager.addEntity(new Butterfly(handler, x * Tile.WIDTH, y * Tile.HEIGHT, false, timer),
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
			int x = (int) (Math.random() * width + 1.0D) * Tile.WIDTH;

			while (getTile(x, 0).isSolid()) {
				x = (int) (Math.random() * width + 1.0D) * Tile.WIDTH;
			}

			int ix = (int) (x / Tile.WIDTH);

			int y = (heights.get(ix).size() > 0 ? (heights.get(ix).get(0) - 2) * Tile.HEIGHT : 0);

			entityManager.addEntity(new Fox(handler, x, y), true);
		}
	}

	private void addBear(int amount) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1.0D) * Tile.WIDTH;

			while (getTile(x, 0).isSolid()) {
				x = (int) (Math.random() * width + 1.0D) * Tile.WIDTH;
			}

			int ix = (int) (x / Tile.WIDTH);

			int y = (heights.get(ix).size() > 0 ? (heights.get(ix).get(0) - 2) * Tile.HEIGHT : 0);

			entityManager.addEntity(new Bear(handler, x, y), true);
		}
	}

	private void addWolf(int amount) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1.0D) * Tile.WIDTH;

			while (getTile(x, 0).isSolid()) {
				x = (int) (Math.random() * width + 1.0D) * Tile.WIDTH;
			}

			int ix = (int) (x / Tile.WIDTH);

			int y = (heights.get(ix).size() > 0 ? (heights.get(ix).get(0) - 2) * Tile.HEIGHT : 0);

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

			entityManager.addEntity(new Bat(handler, x * Tile.WIDTH, y * Tile.HEIGHT), true);
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

			entityManager.addEntity(new Fairy(handler, x * Tile.WIDTH, y * Tile.HEIGHT), true);
		}
	}

	private void addSkunk(int amount) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1.0D) * Tile.WIDTH;

			while (getTile(x, 0).isSolid()) {
				x = (int) (Math.random() * width + 1.0D) * Tile.WIDTH;
			}

			int ix = (int) (x / Tile.WIDTH);

			int y = (heights.get(ix).size() > 0 ? (heights.get(ix).get(0) - 2) * Tile.HEIGHT : 0);

			entityManager.addEntity(new Skunk(handler, x, y), true);
		}
	}

	private void addEntities(int amount, EntityEntry a) {
		if(a.entityClass.isAssignableFrom(Creature.class) && ((Creature)a.example).flies)
			for (int i = 0; i < amount; i++) {
				int x, y;
				do {
					x = (int) (Math.random() * width + 1.0D) * Tile.WIDTH;
					y = (int) (Math.random() * width + 1.0D) * Tile.WIDTH;
				} while (getTile(x, y).isSolid());
				entityManager.addEntity(a.spawn(x, y));
			}
		else
			for (int i = 0; i < amount; i++) {
				int x, size;
				do {
					x = (int) (Math.random() * width + 1.0D) * Tile.WIDTH;
					size = heights.get(x/Tile.WIDTH).size();
				} while (size == 0);

				int y = heights.get(x / Tile.WIDTH).get((int)(Math.random()*size)) * Tile.HEIGHT;

				entityManager.addEntity(a.spawn(x, y));
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
			if (entityCount.containsKey(((Creature) e).getName()))
				if (entityCount.get(((Creature) e).getName()) < entityCaps.get(((Creature) e).getName()))
					pass = true;
				else
					pass = nameScheming(((Creature) e).getName());
		} else if (entityCount.containsKey(e.getClass().getName())) {
			pass = (entityCount.get(e.getClass().getName()) < entityCaps.get(e.getClass().getName()));
		} else {
			pass = nameScheming(e.getClass().getName());
		}

		if (!pass)
			return;

		int ix = (int) (x / Tile.WIDTH);

		double y = (heights.get(ix).size() > 0 ? (heights.get(ix).get(0)) * Tile.HEIGHT : 0);

		while (y > maxy || y < miny) {
			y = (heights.get(ix).size() > 0
					? (heights.get(ix).get((int) Public.expandedRand(0, heights.get(ix).size() - 1))) * Tile.HEIGHT
					: maxy - 1);
		}

		e.setX(x);
		e.setY(y - e.getHeight());
		entityManager.addEntity(e, false);

		if (e instanceof Creature) {
			if (entityCount.containsKey(((Creature) e).getName()))
				entityCount.put(((Creature) e).getName(), entityCount.get(((Creature) e).getName()) + 1);
			else
				updateOrigEntities(((Creature) e).getName());
		} else if (entityCount.containsKey(e.getClass().getName())) {
			entityCount.put(e.getClass().getName(), entityCount.get(e.getClass().getName()) + 1);
		} else {
			updateOrigEntities(e.getClass().getName());
		}
	}

	public void addEntityInAir(Entity e, double x, double y) {

		boolean pass = true;

		if (e instanceof Creature) {
			if (entityCount.containsKey(((Creature) e).getName()))
				if (entityCount.get(((Creature) e).getName()) < entityCaps.get(((Creature) e).getName()))
					pass = true;
				else
					pass = nameScheming(((Creature) e).getName());
		} else if (entityCount.containsKey(e.getClass().getName())) {
			pass = (entityCount.get(e.getClass().getName()) < entityCaps.get(e.getClass().getName()));
		} else {
			pass = nameScheming(e.getClass().getName());
		}

		if (!pass)
			return;

		e.setX(x);
		e.setY(y - e.getHeight());
		entityManager.addEntity(e, false);

		if (e instanceof Creature) {
			if (entityCount.containsKey(((Creature) e).getName()))
				entityCount.put(((Creature) e).getName(), entityCount.get(((Creature) e).getName()) + 1);
			else
				updateOrigEntities(((Creature) e).getName());
		} else if (entityCount.containsKey(e.getClass().getName())) {
			entityCount.put(e.getClass().getName(), entityCount.get(e.getClass().getName()) + 1);
		} else {
			updateOrigEntities(e.getClass().getName());
		}
	}

	private void addCannibalTribe(int amount, int groupX) {
		entityManager.addEntity(new Cannibal(handler, groupX,

				((Integer) ((ArrayList<?>) heights.get(groupX / Tile.WIDTH)).get(
						(int) Public.expandedRand(0.0D, ((ArrayList<?>) heights.get(groupX / Tile.WIDTH)).size() - 1)))
								.intValue()
						- 2,
				Public.expandedRand(0.1D, 0.6D), 1, true), true);
		for (int i = 0; i < amount - 1; i++) {
			int x = (int) Public.expandedRand(groupX - 2, groupX + 2);
			int y = ((Integer) ((ArrayList<?>) heights.get(x / Tile.WIDTH))
					.get((int) Public.expandedRand(0.0D, ((ArrayList<?>) heights.get(x / Tile.WIDTH)).size() - 1)))
							.intValue()
					- 2;

			entityManager.addEntity(new Cannibal(handler, x * Tile.WIDTH, y * Tile.HEIGHT,
					Public.expandedRand(0.51D, 0.8D), 1, false), true);
		}
	}

	public String getType() {
		return type;
	}

	public String toString() {
		return "World[" + width + ", " + height + "]";
	}

}
