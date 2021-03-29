package com.zandgall.arvopia.levelmaker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import static java.lang.Math.*;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.SmartCostume;
import com.zandgall.arvopia.entity.EntityAdder;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.state.GameState;
import com.zandgall.arvopia.state.State;
import com.zandgall.arvopia.tiles.Bridge;
import com.zandgall.arvopia.tiles.GrassTile;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.tiles.WoodFloor;
import com.zandgall.arvopia.tiles.backtile.BackGrassTile;
import com.zandgall.arvopia.tiles.backtile.Backtile;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.FileChooser;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.IconButton;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Slider;
import com.zandgall.arvopia.utils.TabMenu;
import com.zandgall.arvopia.utils.TextEditor;
import com.zandgall.arvopia.utils.ToggleButton;
import com.zandgall.arvopia.utils.Utils;
import com.zandgall.arvopia.worlds.World;

public class LevelMakerState extends State {

	public static final int TILER = 0, ERASER = 1, RECT = 2, SMARTRECT = 3, FLOODFILL = 4, LINE = 5;
	int tool = TILER;

	public static int numberOfWorlds = 0;

//	Slider whIncrement, height, foliage, stones, insects, creatures, cannibals, caves;
	Button save, confirm, back, open, image;
	ToggleButton useGrid, useForeground;

	IconButton plusW, minusW, plusH, minusH;

	double worldxOffset = 0, worldyOffset = 0, worldWidth = 40, worldHeight = 20;
	float worldZoom = 2;

	int W = 1280, H = 720, preW = 720, preH = 405;

	boolean grid = true;

	String selectedTile = "TILE1";
	String selectedBackTile = "TILE1";

	TabMenu tabmenu;

	TextEditor name;

	// tiles.get(x).get(y);
	ArrayList<ArrayList<String>> tiles;
	IconButton[] tileButtons, backtileButtons;
	ArrayList<ArrayList<String>> backtiles;

	boolean foreground = false;

	IconButton setTiler, setEraser, setRect, setSmartRect, setFloodFill, setLine;

	Slider stoneA, treeA, flowerA, temperatureA, batA, beeA, fairyA, butterflyA, bearA, skunkA, wolfA, foxA;

	ArrayList<Slider> slidersA;

	MakerEntity spawnpoint, bee, butterfly, fairy, bat, fox, wolf, bear, skunk, lia, cannibal, cloud, shrubbery, stone,
			tree, villager, flower, house, areaadder;

	ArrayList<MakerEntity> entities;

	public LevelMakerState(Handler handler) {
		super(handler);

		back = new Button(handler, 10, H - 35, "Goes back without generating world", "Back");
		open = new Button(handler, 20 + back.getWidth(), H - 35, "Opens a world", "Open");
		save = new Button(handler, 10 + open.getX() + open.getWidth(), H - 35, "Confirms your world selection", "Save");
		confirm = new Button(handler, 10 + save.getX() + save.getWidth(), H - 35, "Confirms your world selection",
				"Save and load");
		image = new Button(handler, 10 + confirm.getX() + confirm.getWidth(), H - 35, "Writes image to path",
				"Write Image");

		useGrid = new ToggleButton(handler, W - 100, 10, "Sets whether you render a grid or not", "Grid", 1);
		useGrid.on = true;
		useForeground = new ToggleButton(handler, W - 100, 50,
				"Setw whether you are drawing on the foreground or background", "Foreground", 1);
		useForeground.on = true;

		setupEntities();

		tiles = new ArrayList<ArrayList<String>>();
		backtiles = new ArrayList<ArrayList<String>>();
		refillWorld();

		setupTileIconButtons();

		tabmenu = new TabMenu(handler, new String[] { "Settings", "Tiles", "World Data", "Entities" }, 0, 0, W, H / 5,
				false);

		// World data
		stoneA = new Slider(handler, 0, 1, "Stones");
		flowerA = new Slider(handler, 0, 1, "Flowers");
		treeA = new Slider(handler, 0, 1, "Trees");
		temperatureA = new Slider(handler, -50, 50, "Temperature");
		batA = new Slider(handler, 0, 1, "Bats");
		beeA = new Slider(handler, 0, 1, "Bees");
		fairyA = new Slider(handler, 0, 1, "Fairies");
		butterflyA = new Slider(handler, 0, 1, "Butterflies");
		bearA = new Slider(handler, 0, 1, "Bears");
		skunkA = new Slider(handler, 0, 1, "Skunks");
		wolfA = new Slider(handler, 0, 1, "Wolves");
		foxA = new Slider(handler, 0, 1, "Foxes");

		slidersA = new ArrayList<Slider>();

		slidersA.add(stoneA);
		slidersA.add(flowerA);
		slidersA.add(treeA);
		slidersA.add(temperatureA);
		slidersA.add(batA);
		slidersA.add(beeA);
		slidersA.add(fairyA);
		slidersA.add(butterflyA);
		slidersA.add(bearA);
		slidersA.add(skunkA);
		slidersA.add(wolfA);
		slidersA.add(foxA);

		for (EntityAdder a : handler.getAdders()) {
			slidersA.add(new Slider(handler, 0, 1, a.name));
		}

		BufferedImage plus = Tran.scaleUp(ImageLoader.loadImage("/textures/Gui/+-.png").getSubimage(0, 0, 32, 32),
				1.5f);
		BufferedImage minus = Tran.scaleUp(ImageLoader.loadImage("/textures/Gui/+-.png").getSubimage(32, 0, 32, 32),
				1.5f);
		plusW = new IconButton(handler, 200, 5, "Plus W", plus, "Adds to the width of the world");
		minusW = new IconButton(handler, 10, 5, "Minus W", minus, "Subtracts from the width of the world");
		plusH = new IconButton(handler, 200, 60, "Plus H", plus, "Adds to the height of the world");
		minusH = new IconButton(handler, 10, 60, "Minus H", minus, "Subtracts from the height of the world");

		name = new TextEditor(handler, 250, 5, 800, 1, "World", Public.runescape.deriveFont(30f));

		Assets toolset = new Assets(ImageLoader.loadImage("/textures/Gui/LevelMakerIcons.png"), 36, 36,
				"Level Maker Icons");

		setTiler = new IconButton(handler, W - 40, H / 5 + 10, "Tile", toolset.get(0, 0),
				"Places a single tile wherever you click");
		setRect = new IconButton(handler, W - 40, H / 5 + 50, "Rectangle", toolset.get(0, 1),
				"Places the selected tile in the area selected");
		setSmartRect = new IconButton(handler, W - 40, H / 5 + 90, "Smart Rectangle", toolset.get(0, 2),
				"Puts a platfrom of grass tiles wherever selected");
		setEraser = new IconButton(handler, W - 40, H / 5 + 130, "Eraser", toolset.get(0, 3),
				"Erases the selected spot");
		setFloodFill = new IconButton(handler, W - 40, H / 5 + 170, "Flood fill", toolset.get(0, 4),
				"Fills the empty area");
		setLine = new IconButton(handler, W - 40, H / 5 + 210, "Line", toolset.get(0, 5),
				"Puts a line of tiles from first point clicked to last one let go");

//		whIncrement = new Slider(handler, 40, 1000, 200, true, "Width");
	}

	public void setupEntities() {
		
		spawnpoint = new MakerEntity(handler,
				SmartCostume.getStill().getSubimage(35, 23, 36, 54), -100, -100, new Slider[] {},
				new Slider[] {}, new ToggleButton[] {}, "Player");
		spawnpoint.instances.add(new IconButton(handler, 0, 0,
				SmartCostume.getStill().getSubimage(35, 23, 36, 54)));

		bee = new MakerEntity(handler, PublicAssets.bee[0], 10, 10, new Slider[] {}, new Slider[] {},
				new ToggleButton[] {}, "Bee");

		butterfly = new MakerEntity(handler, PublicAssets.butterfly[0], 10, 20, new Slider[] {}, new Slider[] {},
				new ToggleButton[] {}, "Butterfly");

		bat = new MakerEntity(handler, ImageLoader.loadImage("/textures/Creatures/Bat.png").getSubimage(0, 0, 36, 18),
				10, 40, new Slider[] {}, new Slider[] {}, new ToggleButton[] {}, "Bat");

		fairy = new MakerEntity(handler,
				ImageLoader.loadImage("/textures/Creatures/Fairy.png").getSubimage(0, 0, 18, 18), 10, 60,
				new Slider[] {}, new Slider[] {}, new ToggleButton[] { new ToggleButton(handler, 0, 0, "", "Hostile") },
				"Fairy");

		fox = new MakerEntity(handler, PublicAssets.fox[6], 50, 0, new Slider[] {}, new Slider[] {},
				new ToggleButton[] {}, "Fox");

		wolf = new MakerEntity(handler, ImageLoader.loadImage("/textures/Creatures/Wolf.png").getSubimage(0, 0, 72, 54),
				110, 0, new Slider[] {}, new Slider[] {}, new ToggleButton[] {}, "Wolf");

		bear = new MakerEntity(handler,
				ImageLoader.loadImage("/textures/Creatures/Bear.png").getSubimage(0, 0, 108, 72), 190, 0,
				new Slider[] {}, new Slider[] {}, new ToggleButton[] {}, "Bear");

		skunk = new MakerEntity(handler,
				ImageLoader.loadImage("/textures/Creatures/Skunk.png").getSubimage(0, 0, 54, 36), 50, 40,
				new Slider[] {}, new Slider[] {}, new ToggleButton[] {}, "Skunk");

		lia = new MakerEntity(handler, ImageLoader.loadImage("/textures/NPCs/Lia/Lia.png").getSubimage(0, 0, 36, 54),
				110, 60, new Slider[] {}, new Slider[] {}, new ToggleButton[] {}, "Lia");

		cannibal = new MakerEntity(handler,
				ImageLoader.loadImage("/textures/Creatures/Cannibal/Cannibal.png").getSubimage(0, 0, 36, 54), 146, 60,
				new Slider[] { new Slider(handler, 0, 5, 1, "Lives") },
				new Slider[] { new Slider(handler, 0, 5, 1, "Speed") },
				new ToggleButton[] { new ToggleButton(handler, 0, 0, "", "Alpha") }, "Cannibal");

		stone = new MakerEntity(handler, ImageLoader.loadImage("/textures/Statics/Stone.png").getSubimage(0, 0, 18, 18),
				10, 80, new Slider[] { new Slider(handler, 0, 2, 0, "Size") }, new Slider[] {}, new ToggleButton[] {},
				"Stone");

		flower = new MakerEntity(handler,
				ImageLoader.loadImage("/textures/Statics/Flowers/Flowers.png").getSubimage(0, 0, 18, 18), 30, 80,
				new Slider[] { new Slider(handler, 0, 2, 1, "Type") }, new Slider[] {}, new ToggleButton[] {},
				"Flower");

		house = new MakerEntity(handler, ImageLoader.loadImage("/textures/NPCs/Houses/House.png"), 310, 0,
				new Slider[] { new Slider(handler, 0, 1, 0, "Size") }, new Slider[] {}, new ToggleButton[] {}, "House");

		cloud = new MakerEntity(handler, ImageLoader.loadImage("/textures/Statics/Cloud.png").getSubimage(0, 0, 54, 36),
				220, 80, new Slider[] { new Slider(handler, 0, 3, 0, "Size") }, new Slider[] {}, new ToggleButton[] {},
				"Cloud");

		tree = new MakerEntity(handler, PublicAssets.tree[0][13], 440, -40,
				new Slider[] { new Slider(handler, 0, 15, 14, "Age") }, new Slider[] {}, new ToggleButton[] {}, "Tree");

		villager = new MakerEntity(handler,
				ImageLoader.loadImage("/textures/NPCs/Villagers/Varient0.png").getSubimage(0, 0, 36, 54), 182, 60,
				new Slider[] { new Slider(handler, 0, 6, 0, "Varient") }, new Slider[] {}, new ToggleButton[] {},
				"Villager");

		shrubbery = new MakerEntity(handler, PublicAssets.shrubbery[0], 50, 80,
				new Slider[] { new Slider(handler, 0, 4, 0, "Varient") }, new Slider[] {}, new ToggleButton[] {},
				"Shrubbery");

		areaadder = new MakerEntity(handler, getRect(18, 18, Color.red), 70, 80,
				new Slider[] { new Slider(handler, 0, 100, 1, "Width"), new Slider(handler, 0, 100, 1, "Height") },
				new Slider[] { new Slider(handler, 0, 1, 0.1, "Chance") }, new ToggleButton[] {}, "AreaAdder");

		entities = new ArrayList<MakerEntity>();
		entities.add(fox);
		entities.add(bee);
		entities.add(bat);
		entities.add(butterfly);
		entities.add(fairy);
		entities.add(wolf);
		entities.add(bear);
		entities.add(skunk);
		entities.add(lia);
		entities.add(cannibal);
		entities.add(stone);
		entities.add(flower);
		entities.add(house);
		entities.add(cloud);
		entities.add(tree);
		entities.add(villager);
		entities.add(shrubbery);
		entities.add(areaadder);

		int xoffset = 520, yoffset = 5, nextoffset = 0;

		for (EntityAdder a : handler.getAdders()) {
			if (yoffset + a.example.getHeight() > H / 5 && yoffset != 0) {
				yoffset = 5;
				xoffset += nextoffset;
				nextoffset = 0;
			}
			nextoffset = Math.max(nextoffset, a.example.getWidth()) + 5;
			MakerEntity newe = new MakerEntity(handler,
					ImageLoader.loadImage(a.name).getSubimage(0, 0, a.example.getWidth(), a.example.getHeight()),
					xoffset, yoffset, new Slider[] {}, new Slider[] {}, new ToggleButton[] {}, a.name);
			entities.add(newe);
			yoffset += a.example.getHeight() + 5;
		}
	}

	BufferedImage getRect(int width, int height, Color c) {
		width = width == 0 ? 1 : width;
		height = height == 0 ? 1 : height;
		BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = out.getGraphics();
		g.setColor(c);
		g.drawRect(0, 0, width - 1, height - 1);
		g.drawRect(1, 1, width - 3, height - 3);
		return out;
	}

	public void setupTileIconButtons() {

		tileButtons = new IconButton[Tile.tiles.size()];
		backtileButtons = new IconButton[Backtile.tiles.size()];

		int nx = 330;
		int ny = -10;

		for (int i = 0; i < tileButtons.length; i++) {
			if (Tile.tiles.get(i) == null)
				continue;
			IconButton b = new IconButton(handler, 0, 0, (BufferedImage) Tile.tiles.get(i).getImage());

			if (Tile.tiles.get(i) instanceof GrassTile) {
				GrassTile t = (GrassTile) Tile.tiles.get(i);
				b.x = t.gridX() * 20 + 10;
				b.y = t.gridY() * 20 + 10;
			}

			else if (Tile.tiles.get(i) instanceof Bridge) {
				Bridge t = (Bridge) Tile.tiles.get(i);
				b.x = 170 + 20 * t.gx;
				b.y = 10 + 20 * t.gy;
			}

			else if (Tile.tiles.get(i) instanceof WoodFloor) {
				WoodFloor t = (WoodFloor) Tile.tiles.get(i);
				b.x = 250 + 20 * t.x;
				b.y = 10 + 20 * t.y;
			} else if (Tile.tiles.get(i) != Tile.n0) {
				b.y = (ny += 20);
				b.x = (nx);
				if (ny >= 70) {
					ny = -10;
					nx += 20;
				}
			}

			tileButtons[i] = b;
		}

		nx = 90;
		ny = -10;

		for (int i = 0; i < backtileButtons.length; i++) {
			if (Backtile.tiles.get(i) == null)
				continue;
			IconButton b = new IconButton(handler, 0, 0, (BufferedImage) Backtile.tiles.get(i).getImage());

			if (Backtile.tiles.get(i) instanceof BackGrassTile) {
				BackGrassTile t = (BackGrassTile) Backtile.tiles.get(i);
				b.x = t.gridX() * 20 + 10;
				b.y = t.gridY() * 20 + 10;
			} else if (Backtile.tiles.get(i) != Backtile.n0) {
				b.y = (ny += 20);
				b.x = (nx);
				if (ny >= 70) {
					ny = -10;
					nx += 20;
				}
			}

			backtileButtons[i] = b;
		}

	}

	public void refillWorld() {
		for (int x = tiles.size(); x < W; x++) {
			tiles.add(new ArrayList<String>());
			backtiles.add(new ArrayList<String>());
			for (int y = tiles.get(x).size(); y < H; y++) {
				tiles.get(x).add("TILE0");
				backtiles.get(x).add("TILE0");
			}
		}
	}

	public void floodFill(int x, int y, String replacement) {
		if (!(Public.identifyRange(0, worldWidth - 1, x) && Public.identifyRange(0, worldHeight - 1, y)))
			return;
		floodFill(x, y, tiles.get(x).get(y), replacement);
	}

	public void floodFill(int x, int y, String target, String replacement) {
		if (!(Public.identifyRange(0, worldWidth - 1, x) && Public.identifyRange(0, worldHeight - 1, y)))
			return;
		if (tiles.get(x).get(y) == replacement || tiles.get(x).get(y) != target)
			return;

		tiles.get(x).set(y, replacement);

		floodFill(x, y + 1, target, replacement);
		floodFill(x, y - 1, target, replacement);
		floodFill(x - 1, y, target, replacement);
		floodFill(x + 1, y, target, replacement);
	}

	public void floodFillBack(int x, int y, String replacement) {
		if (!(Public.identifyRange(0, worldWidth - 1, x) && Public.identifyRange(0, worldHeight - 1, y)))
			return;
		floodFillBack(x, y, backtiles.get(x).get(y), replacement);
	}

	public void floodFillBack(int x, int y, String target, String replacement) {
		if (!(Public.identifyRange(0, worldWidth - 1, x) && Public.identifyRange(0, worldHeight - 1, y)))
			return;
		if (backtiles.get(x).get(y) == replacement || backtiles.get(x).get(y) != target)
			return;

		backtiles.get(x).set(y, replacement);

		floodFillBack(x, y + 1, target, replacement);
		floodFillBack(x, y - 1, target, replacement);
		floodFillBack(x - 1, y, target, replacement);
		floodFillBack(x + 1, y, target, replacement);
	}

	double premx = 0, premy = 0;

	double RECT_startX = 0, RECT_startY = 0, RECT_endX = 0, RECT_endY = 0;
	boolean RECT_started = false, RECT_smart = false;

	int LINE_startX = 0, LINE_startY = 0, LINE_endX = 0, LINE_endY = 0;
	boolean LINE_started = false;

	@Override
	public void tick() {
		confirm.tick();
		open.tick();
		save.tick();
		image.tick();
		
//		confirm.on = true;

		refillWorld();

		double mx = handler.getMouse().rMouseX();
		double my = handler.getMouse().rMouseY();
		double ms = handler.getMouse().getMouseScroll();

		int rtx = (int) ((((mx - W / 2) + worldWidth * 9 * worldZoom) - worldxOffset * worldZoom) / (worldZoom));
		int rty = (int) ((((my - H / 2) + worldHeight * 9 * worldZoom) - worldyOffset * worldZoom) / (worldZoom));

		int tx = (int) ((((mx - W / 2) + worldWidth * 9 * worldZoom) - worldxOffset * worldZoom) / (18 * worldZoom));
		int ty = (int) ((((my - H / 2) + worldHeight * 9 * worldZoom) - worldyOffset * worldZoom) / (18 * worldZoom));

		if (handler.getGame().getDisplay().getCanvas().getWidth() != W
				|| handler.getGame().getDisplay().getCanvas().getHeight() != H) {
			preW = handler.getGame().getWidth();
			preH = handler.getGame().getHeight();
			handler.width = W;
			handler.height = H;
			handler.getGame().useResize(false);
			handler.getGame().getDisplay().setSize(W, H);
		}
		if (image.on) {
			writeImage(Game.prefix + "\\Arvopia\\World\\" + name.getContent() + ".png");
		}

		if (confirm.on) {
			handler.getMouse().setClicked(false);
			handler.getGame().width = preW;
			handler.getGame().height = preH;
			handler.width = 720;
			handler.height = 400;
			handler.getGame().useResize(true);
			handler.getGame().getDisplay().setSize(preW, preH);
			saveWorld(Game.prefix + "\\Arvopia\\World\\" + name.getContent() + ".arv");
			((GameState) handler.getGame().gameState).loadWorld(Game.prefix + "\\Arvopia\\World\\" + name.getContent() + ".arv");
			setState(handler.getGame().gameState);
			return;
		}
		if (save.on) {
			saveWorld(Game.prefix + "\\Arvopia\\World\\" + name.getContent() + ".arv");
		}
		back.tick();
		if (back.on) {
			handler.getGame().width = preW;
			handler.getGame().height = preH;
			handler.width = 720;
			handler.height = 400;
			handler.getGame().useResize(true);
			handler.getGame().getDisplay().setSize(preW, preH);
			setState(getPrev());
		}

		if (open.on) {
			FileChooser f = new FileChooser();
			String s = f.getFile(Game.prefix + "\\Arvopia\\World");
			loadWorld(s);
		}

		String pretab = tabmenu.getTab();
		tabmenu.tick();

		if (tabmenu.getTab() != pretab) {
			MakerEntity.use = null;
		}

		if (tabmenu.getTab() == "Settings") {

			name.tick();

			useGrid.tick();
			useForeground.tick();
			foreground = useForeground.on;
			grid = useGrid.on;

			plusW.tick();
			if (plusW.on)
				worldWidth += (handler.getKeyManager().ctrl ? 1 : (handler.getKeyManager().shift ? 20 : 5));

			minusW.tick();
			if (minusW.on)
				worldWidth -= (handler.getKeyManager().ctrl ? 1 : (handler.getKeyManager().shift ? 20 : 5));

			plusH.tick();
			if (plusH.on)
				worldHeight += (handler.getKeyManager().ctrl ? 1 : (handler.getKeyManager().shift ? 20 : 5));

			minusH.tick();
			if (minusH.on)
				worldHeight -= (handler.getKeyManager().ctrl ? 1 : (handler.getKeyManager().shift ? 20 : 5));

			worldWidth = Math.max(worldWidth, 40);
			worldHeight = Math.max(worldHeight, 20);

			if (worldWidth == 40)
				minusW.hovered = true;

			if (worldHeight == 20)
				minusH.hovered = true;
		} else if (tabmenu.getTab() == "Tiles") {
			if (foreground) {
				for (int i = 0; i < tileButtons.length; i++) {
					if (tileButtons[i] != null) {
						tileButtons[i].tick();
						if (tileButtons[i].on)
							selectedTile = Tile.tiles.get(i).getId();
						else if (selectedTile == Tile.tiles.get(i).getId())
							tileButtons[i].hovered = true;
					}
				}
			} else {
				for (int i = 0; i < backtileButtons.length; i++) {
					if (backtileButtons[i] != null) {
						backtileButtons[i].tick();
						if (backtileButtons[i].on)
							selectedBackTile = Backtile.tiles.get(i).getId();
						else if (selectedBackTile == Backtile.tiles.get(i).getId())
							backtileButtons[i].hovered = true;
					}
				}
			}
		} else if (tabmenu.getTab() == "World Data") {
			int xoffset = 5, yoffset = 5;

			for (Slider s : slidersA) {
				if (yoffset + 40 > H / 5 && yoffset != 0) {
					yoffset = 5;
					xoffset += 105;
				}
				s.tick(xoffset, yoffset);
				yoffset += 40;
			}
		} else if (tabmenu.getTab() == "Entities") {
			for (Boolean[] b : fairy.fullbvalues)
				if (b[0])
					fairy.image(ImageLoader.loadImage("/textures/Creatures/Fairy.png").getSubimage(0, 18, 18, 18),
							fairy.fullbvalues.indexOf(b));
				else
					fairy.image(ImageLoader.loadImage("/textures/Creatures/Fairy.png").getSubimage(0, 0, 18, 18),
							fairy.fullbvalues.indexOf(b));
			for (Integer[] i : stone.fullivalues)
				stone.image(ImageLoader.loadImage("/textures/Statics/Stone.png").getSubimage(i[0] * 18, 0, 18, 18),
						stone.fullivalues.indexOf(i));
			for (Integer[] i : flower.fullivalues)
				flower.image(ImageLoader.loadImage("/textures/Statics/Flowers/Flowers.png").getSubimage(i[0] * 18, 0,
						18, 18), flower.fullivalues.indexOf(i));
			for (Integer[] i : house.fullivalues)
				house.image(ImageLoader.loadImage("/textures/NPCs/Houses/House" + (i[0] == 1 ? "2" : "") + ".png"),
						house.fullivalues.indexOf(i));
			for (Integer[] i : cloud.fullivalues)
				cloud.image(ImageLoader.loadImage("/textures/Statics/Cloud.png").getSubimage(0, i[0] * 36, 54, 36),
						cloud.fullivalues.indexOf(i));
			for (Integer[] i : tree.fullivalues)
				tree.image(PublicAssets.tree[0][i[0]], tree.fullivalues.indexOf(i));
			for (Integer[] i : villager.fullivalues)
				villager.image(ImageLoader.loadImage("/textures/NPCs/Villagers/Varient" + (i[0]) + ".png")
						.getSubimage(0, 0, 36, 54), villager.fullivalues.indexOf(i));
			for (Integer[] i : shrubbery.fullivalues)
				shrubbery.image(PublicAssets.shrubbery[i[0]], shrubbery.fullivalues.indexOf(i));
			for (Integer[] i : areaadder.fullivalues)
				areaadder.image(getRect(i[0] * 18, i[1] * 18, Color.red), areaadder.fullivalues.indexOf(i));

			for (MakerEntity e : entities)
				e.tick(rtx, rty);
			spawnpoint.tick(rtx, rty);

		}

		setTiler.tick();
		setRect.tick();
		setSmartRect.tick();
		setEraser.tick();
		setFloodFill.tick();
		setLine.tick();

		if (setTiler.on || handler.getKeyManager().keys[KeyEvent.VK_T])
			tool = TILER;
		if (setRect.on || handler.getKeyManager().keys[KeyEvent.VK_R])
			tool = RECT;
		if (setSmartRect.on || handler.getKeyManager().keys[KeyEvent.VK_S])
			tool = SMARTRECT;
		if (setEraser.on || handler.getKeyManager().keys[KeyEvent.VK_E])
			tool = ERASER;
		if (setFloodFill.on || handler.getKeyManager().keys[KeyEvent.VK_F])
			tool = FLOODFILL;
		if (setLine.on || handler.getKeyManager().keys[KeyEvent.VK_L])
			tool = LINE;

		if (my > H / 5) {

			worldZoom -= (ms / 20) * Math.max(worldZoom, 0.2);

			if (handler.getMouse().fullLeft && tabmenu.getTab() != "Entities" && !(MakerEntity.use != null
					&& (MakerEntity.use.usingvalues || MakerEntity.use.selected != -1 || MakerEntity.use.placing != -1))
					&& !(setLine.hovered || setTiler.hovered || setFloodFill.hovered || setRect.hovered
							|| setEraser.hovered || setSmartRect.hovered || back.hovered || save.hovered || open.hovered
							|| confirm.hovered)) {
				switch (tool) {
				case TILER:
					if (Public.identifyRange(0, worldWidth - 1, tx) && Public.identifyRange(0, worldHeight - 1, ty)) {
						if (foreground)
							tiles.get(tx).set(ty, selectedTile);
						else
							backtiles.get(tx).set(ty, selectedBackTile);
					}
					break;
				case ERASER:
					if (Public.identifyRange(0, worldWidth - 1, tx) && Public.identifyRange(0, worldHeight - 1, ty)) {
						if (foreground)
							tiles.get(tx).set(ty, "TILE0");
						else
							backtiles.get(tx).set(ty, "TILE0");
					}
					break;
				case RECT:
					RECT_smart = false;
					if (Public.identifyRange(0, worldWidth - 1, tx) && Public.identifyRange(0, worldHeight - 1, ty)) {
						if (!RECT_started) {
							RECT_startX = tx;
							RECT_startY = ty;
							RECT_started = true;
						}

						RECT_endX = tx;
						RECT_endY = ty;
					}
					break;
				case SMARTRECT:
					RECT_smart = true;
					if (Public.identifyRange(0, worldWidth - 1, tx) && Public.identifyRange(0, worldHeight - 1, ty)) {
						if (!RECT_started) {
							RECT_startX = tx;
							RECT_startY = ty;
							RECT_started = true;
						}

						RECT_endX = tx;
						RECT_endY = ty;
					}
					break;
				case FLOODFILL:
					if (foreground)
						floodFill(tx, ty, selectedTile);
					else
						floodFillBack(tx, ty, selectedBackTile);
					break;
				case LINE:
					if (Public.identifyRange(0, worldWidth - 1, tx) && Public.identifyRange(0, worldHeight - 1, ty)) {
						if (!LINE_started) {
							LINE_startX = tx;
							LINE_startY = ty;
							LINE_started = true;
						}

						LINE_endX = tx;
						LINE_endY = ty;
					}
					break;
				}
			} else {
				if (RECT_started)
					if (RECT_smart) {
						if (foreground)
							smartFillSelectedRect();
						else
							smartFillSelectedRectBack();
					} else if (foreground)
						fillSelectedRect();
					else
						fillSelectedRectBack();
				RECT_started = false;
				if (LINE_started) {
					for (Point a : linePoints(LINE_startX, LINE_startY, LINE_endX, LINE_endY))
						if (foreground)
							tiles.get(a.x).set(a.y, selectedTile);
						else
							backtiles.get(a.x).set(a.y, selectedBackTile);
				}
				LINE_started = false;
			}

			switch (tool) {
			case TILER:
				setTiler.hovered = true;
				break;
			case ERASER:
				setEraser.hovered = true;
				break;
			case RECT:
				setRect.hovered = true;
				break;
			case SMARTRECT:
				setSmartRect.hovered = true;
				break;
			case FLOODFILL:
				setFloodFill.hovered = true;
				break;
			case LINE:
				setLine.hovered = true;
				break;
			}

			if (handler.getMouse().fullRight) {

				worldxOffset += (mx - premx) / worldZoom;

				worldyOffset += (my - premy) / worldZoom;

			}

		}

		worldZoom = (float) Public.range(0.1, 20, worldZoom);

		premx = mx;
		premy = my;
	}

	public void fillSelectedRect() {
		int mx = (int) Math.min(RECT_startX, RECT_endX);
		int my = (int) Math.min(RECT_startY, RECT_endY);

		int xx = (int) Math.max(RECT_startX, RECT_endX);
		int xy = (int) Math.max(RECT_startY, RECT_endY);

		for (int x = mx; x <= xx; x++) {
			for (int y = my; y <= xy; y++) {
				tiles.get(x).set(y, selectedTile);
			}
		}
	}

	public void fillSelectedRectBack() {
		int mx = (int) Math.min(RECT_startX, RECT_endX);
		int my = (int) Math.min(RECT_startY, RECT_endY);

		int xx = (int) Math.max(RECT_startX, RECT_endX);
		int xy = (int) Math.max(RECT_startY, RECT_endY);

		for (int x = mx; x <= xx; x++) {
			for (int y = my; y <= xy; y++) {
				backtiles.get(x).set(y, selectedBackTile);
			}
		}
	}

	public void smartFillSelectedRect() {
		int mx = (int) Math.min(RECT_startX, RECT_endX);
		int my = (int) Math.min(RECT_startY, RECT_endY);

		int xx = (int) Math.max(RECT_startX, RECT_endX);
		int xy = (int) Math.max(RECT_startY, RECT_endY);

		for (int x = mx; x <= xx; x++) {
			for (int y = my; y <= xy; y++) {
				tiles.get(x).set(y, "TILE5");
			}
		}

		mx = Math.max(0, mx - 2);
		my = Math.max(0, my - 2);

		xx = (int) Math.min(worldWidth, xx + 2);
		xy = (int) Math.min(worldHeight, xy + 2);

		for (int x = mx; x < xx; x++) {
			for (int y = my; y < xy; y++) {
				tiles.get(x).set(y, World.formatTiles(tiles, x, y));
			}
		}

	}

	public void smartFillSelectedRectBack() {
		int mx = (int) Math.min(RECT_startX, RECT_endX);
		int my = (int) Math.min(RECT_startY, RECT_endY);

		int xx = (int) Math.max(RECT_startX, RECT_endX);
		int xy = (int) Math.max(RECT_startY, RECT_endY);

		for (int x = mx; x <= xx; x++) {
			for (int y = my; y <= xy; y++) {
				backtiles.get(x).set(y, "TILE5");
			}
		}

		mx = Math.max(0, mx - 2);
		my = Math.max(0, my - 2);

		xx = (int) Math.min(worldWidth + 1, xx + 2);
		xy = (int) Math.min(worldHeight + 1, xy + 2);

		for (int x = mx; x < xx; x++) {
			for (int y = my; y < xy; y++) {
				backtiles.get(x).set(y, World.formatBacktiles(backtiles, x, y));
			}
		}

	}

	private ArrayList<Point> linePoints(int x0, int y0, int x1, int y1) {
		ArrayList<Point> out = new ArrayList<Point>();

		boolean steep = abs(y1 - y0) > abs(x1 - x0);
		if (steep)
			return (lineSteepPoints(y0, x0, y1, x1));

		if (x0 > x1)
			out.addAll(linePoints(x1, y1, x0, y0));

		double dx = x1 - x0;
		double dy = y1 - y0;
		double gradient = dy / dx;

		// handle first endpoint
		double xend = round(x0);
		double yend = y0 + gradient * (xend - x0);
		double xpxl1 = xend; // this will be used in the main loop
		double ypxl1 = (int) (yend);

		if (steep) {
			out.add(new Point((int) ypxl1, (int) xpxl1));
		} else {
			out.add(new Point((int) xpxl1, (int) ypxl1));
		}

		// first y-intersection for the main loop
		double intery = yend + gradient;

		// handle second endpoint
		xend = round(x1);
		yend = y1 + gradient * (xend - x1);
		double xpxl2 = xend; // this will be used in the main loop
		double ypxl2 = (int) (yend);

		if (steep) {
			out.add(new Point((int) ypxl2, (int) xpxl2));
		} else {
			out.add(new Point((int) xpxl2, (int) ypxl2));
		}

		// main loop
		for (double x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
			if (steep) {
				out.add(new Point((int) (intery), (int) x));
			} else {
				out.add(new Point((int) x, (int) (intery)));
			}
			intery = intery + gradient;
		}

		return out;
	}

	public void saveWorld(String path) {

		String out = "World" + System.lineSeparator() + "0.7" + System.lineSeparator();
		out += (int) worldWidth + " " + (int) worldHeight + System.lineSeparator();
		out += spawnpoint.instances.get(0).x + " " + spawnpoint.instances.get(0).y + System.lineSeparator();
		for (int j = 0; j < worldHeight; j++) {
			for (int i = 0; i < worldWidth; i++) {
				out += tiles.get(i).get(j) + " ";
			}
			out += System.lineSeparator();
		}

		out += "BACKTILES" + System.lineSeparator();
		for (int j = 0; j < worldHeight; j++) {
			for (int i = 0; i < worldWidth; i++) {
				out += backtiles.get(i).get(j) + " ";
			}
			out += System.lineSeparator();
		}

		int eammount = 0;
		for (MakerEntity e : entities)
			eammount += e.instances.size();

		out += eammount + System.lineSeparator();
		for (MakerEntity e : entities)
			out += e.outputString();

		for (Slider s : slidersA)
			if (s.getWholeValue() >= 0.05)
				out += s.getName() + " " + s.getWholeValue() + System.lineSeparator();

//		out += "Stone " + stoneA.getWholeValue() + System.lineSeparator();
//		out += "Flower " + flowerA.getWholeValue() + System.lineSeparator();
//		out += "Tree " + flowerA.getWholeValue() + System.lineSeparator();
//		out += "Temperature " + flowerA.getWholeValue() + System.lineSeparator();
//		out += "Bat " + batA.getWholeValue() + System.lineSeparator();
//		out += "Bee " + beeA.getWholeValue() + System.lineSeparator();
//		out += "Fairy " + fairyA.getWholeValue() + System.lineSeparator();
//		out += "Butterfly " + butterflyA.getWholeValue() + System.lineSeparator();
//		out += "Bear " + bearA.getWholeValue() + System.lineSeparator();
//		out += "Skunk " + skunkA.getWholeValue() + System.lineSeparator();
//		out += "Wolf " + wolfA.getWholeValue() + System.lineSeparator();
//		out += "Fox " + foxA.getWholeValue() + System.lineSeparator();

		Utils.fileWriter(out, path);

		if (numberOfWorlds >= 3) {
			Achievement.award(Achievement.architect);
		} else
			numberOfWorlds++;

		System.out.println(numberOfWorlds);

	}

	public void writeImage(String path) {
		BufferedImage out = new BufferedImage((int) (worldWidth * 18), (int) (worldHeight * 18),
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = out.createGraphics();

		for (int x = 0; x < worldWidth; x++) {
			for (int y = 0; y < worldHeight; y++) {
				g.drawImage(Backtile.getTile(backtiles.get(x).get(y)).getImage(), x * 18, y * 18, null);

				g.drawImage(Tile.getTile(tiles.get(x).get(y)).getImage(), x * 18, y * 18, null);
			}
		}

		for (MakerEntity e : entities)
			e.renderInstances(g);
		spawnpoint.renderInstances(g);

		g.dispose();

		ImageLoader.saveImage(out, path);
	}

	public ArrayList<Point> lineSteepPoints(int x0, int y0, int x1, int y1) {
		ArrayList<Point> out = new ArrayList<Point>();

		boolean steep = true;

		if (x0 > x1)
			out.addAll(lineSteepPoints(x1, y1, x0, y0));

		double dx = x1 - x0;
		double dy = y1 - y0;
		double gradient = dy / dx;

		// handle first endpoint
		double xend = round(x0);
		double yend = y0 + gradient * (xend - x0);
		double xpxl1 = xend; // this will be used in the main loop
		double ypxl1 = (int) (yend);

		if (steep) {
			out.add(new Point((int) ypxl1, (int) xpxl1));
		} else {
			out.add(new Point((int) xpxl1, (int) ypxl1));
		}

		// first y-intersection for the main loop
		double intery = yend + gradient;

		// handle second endpoint
		xend = round(x1);
		yend = y1 + gradient * (xend - x1);
		double xpxl2 = xend; // this will be used in the main loop
		double ypxl2 = (int) (yend);

		if (steep) {
			out.add(new Point((int) ypxl2, (int) xpxl2));
		} else {
			out.add(new Point((int) xpxl2, (int) ypxl2));
		}

		// main loop
		for (double x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
			if (steep) {
				out.add(new Point((int) (intery), (int) x));
			} else {
				out.add(new Point((int) x, (int) (intery)));
			}
			intery = intery + gradient;
		}

		return out;
	}

	@Override
	public void render(Graphics2D g) {
		g.setTransform(handler.getGame().getDefaultTransform());

		g.setColor(new Color(120, 225, 255));
		g.fillRect(0, 0, W, H);

		renderGrid(g);
		renderGui(g);

	}

	public void renderGrid(Graphics2D g) {
		g.translate(W / 2, H / 2);
		
		g.scale(worldZoom, worldZoom);
		
		g.translate(-worldWidth * 9, -worldHeight * 9);

		g.translate(worldxOffset, worldyOffset);

		g.setColor(Color.black);
		g.drawRect(0, 0, (int) worldWidth * 18, (int) worldHeight * 18);
		
		int startX = (int) Public.range(0, worldWidth, (-(worldxOffset - worldWidth * 9) - (W/2/worldZoom))/18);
		int startY = (int) Public.range(0, worldHeight, (-(worldyOffset - worldHeight * 9) - (H/2/worldZoom))/18);
		
		int endX = (int) Public.range(0, worldWidth, (W/worldZoom-(worldxOffset - worldWidth * 9) - (W/2/worldZoom))/18 + 1);
		int endY = (int) Public.range(0, worldHeight, (H/worldZoom-(worldyOffset - worldHeight * 9) - (H/2/worldZoom))/18 + 1);
		
		for (int x = startX; x < endX; x++) {
			for (int y = startY; y < endY; y++) {
				
				if (foreground)
					g.drawImage(Backtile.getTile(backtiles.get(x).get(y)).getImage(), x * 18, y * 18, null);

				try {
					g.drawImage(Tile.getTile(tiles.get(x).get(y)).getImage(), x * 18, y * 18, null);
				} catch (IndexOutOfBoundsException e) {
					g.setColor(Color.magenta);
					g.fillRect((int) x * 18, (int) y * 18, 18, 18);
				}

				if (!foreground)
					g.drawImage(Backtile.getTile(backtiles.get(x).get(y)).getImage(), x * 18, y * 18, null);

				if (grid)
					g.drawRect(x * 18, y * 18, 18, 18);
			}
		}

		if (RECT_started) {
			int mx = (int) Math.min(RECT_startX, RECT_endX);
			int my = (int) Math.min(RECT_startY, RECT_endY);

			int xx = (int) Math.max(RECT_startX, RECT_endX);
			int xy = (int) Math.max(RECT_startY, RECT_endY);

			g.setColor(Color.red);
			g.drawRect(mx * 18, my * 18, (xx - mx + 1) * 18, (xy - my + 1) * 18);
		}

		if (LINE_started) {
			g.setColor(Color.red);
			for (Point a : linePoints(LINE_startX, LINE_startY, LINE_endX, LINE_endY))
				g.drawRect(a.x * 18, a.y * 18, 18, 18);
			g.drawLine(LINE_startX * 18 + 9, LINE_startY * 18 + 9, LINE_endX * 18 + 9, LINE_endY * 18 + 9);
		}

		if (tabmenu.getTab() == "Entities") {
			for (MakerEntity e : entities)
				e.renderInstances(g);
			spawnpoint.renderInstances(g);
		} else {
			for (MakerEntity e : entities)
				e.renderLocked(g);
			spawnpoint.renderLocked(g);
		}

		g.setTransform(handler.getGame().getDefaultTransform());
	}

	public void renderGui(Graphics2D g) {

		tabmenu.render(g);

		if (tabmenu.getTab() == "Settings") {
			useGrid.render(g);
			useForeground.render(g);

			name.render(g);

			plusW.render(g);
			minusW.render(g);
			plusH.render(g);
			minusH.render(g);

			g.setColor(Color.black);
			g.setFont(Public.runescape.deriveFont(25f));
			g.drawString("Width: " + worldWidth, 121 - Tran.measureString("Width: " + worldWidth, g.getFont()).x / 2,
					5 + Tran.measureString("Width: " + worldWidth, g.getFont()).y);
			g.drawString("Height: " + worldHeight,
					121 - Tran.measureString("Height: " + worldHeight, g.getFont()).x / 2,
					60 + Tran.measureString("Height: " + worldHeight, g.getFont()).y);
		} else if (tabmenu.getTab() == "Tiles") {
			if (foreground) {
				for (int i = 0; i < tileButtons.length; i++) {
					if (tileButtons[i] != null)
						tileButtons[i].render(g);
				}
			} else {
				for (int i = 0; i < backtileButtons.length; i++) {
					if (backtileButtons[i] != null)
						backtileButtons[i].render(g);
				}
			}
		} else if (tabmenu.getTab() == "World Data") {
			g.setColor(Color.black);
			for (Slider s : slidersA) {
				s.render(g);
			}
		} else if (tabmenu.getTab() == "Entities") {
			for (MakerEntity e : entities)
				e.renderSrc(g);
			spawnpoint.renderSrc(g);
		}

		setTiler.render(g);
		setRect.render(g);
		setSmartRect.render(g);
		setEraser.render(g);
		setFloodFill.render(g);
		setLine.render(g);
		
		confirm.render(g);
		back.render(g);
		open.render(g);
		save.render(g);
		image.render(g);
	}

	@Override
	public void init() {

	}

	public ArrayList<ArrayList<Integer>> linePointsbackup(int startx, int starty, int endx, int endy) {
		double sfx = startx * 18;
		double sfy = starty * 18;

		double efx = endx * 18;
		double efy = endy * 18;

		ArrayList<ArrayList<Integer>> out = new ArrayList<ArrayList<Integer>>();

		double ratiox = Public.normalize(efx - sfx, efy - sfy)[0];
		double ratioy = Public.normalize(efx - sfx, efy - sfy)[1];

		double x = sfx;
		double y = sfy;

		out.add(new ArrayList<Integer>());
		out.get(out.size() - 1).add((int) (x / 18));
		out.get(out.size() - 1).add((int) (y / 18));

		while ((int) (x / 18) != endx && (int) (y / 18) != endy) {
			x += ratiox;
			y += ratioy;
			if (!out.get(out.size() - 1).contains((int) (x / 18))
					|| !out.get(out.size() - 1).contains((int) (y / 18))) {
				out.add(new ArrayList<Integer>());
				out.get(out.size() - 1).add((int) (x / 18));
				out.get(out.size() - 1).add((int) (y / 18));
			}

			if (startx > endx)
				if (x < endx)
					return out;

			if (startx < endx)
				if (x > endx)
					return out;
		}

		return out;
	}

	private void loadWorld(String path) {
		File f = new File(path);
		
		name.setContent(f.getName());

		if (!f.exists()) {
			System.err.println("No world to load sorry");
			return;
		}

		String[] s = FileLoader.readFile(f).split("\\s+");
		double[] d = new double[s.length];
		int[] i = new int[s.length];

		for (int j = 0; j < s.length; j++) {
			d[j] = Utils.parseDouble(s[j]);
			i[j] = Utils.parseInt(s[j]);
		}

		handler.getGame().initMessage("Loading Variables...");

		int o = 0;

		System.out.println("\t\t" + d[1]);

		if (d[1] < 0.7) {
			loadWorldpre_0_7(path);
			return;
		}

		if (s[0].contains("World") || s[0].contains("Save")) {
			o = 2;
		}

		worldWidth = i[o];
		worldHeight = i[o + 1];

		System.out.println(s[o] + " " + s[o + 1] + " " + i[o] + " " + i[o + 1] + " " + d[o] + " " + d[o + 1]);

		spawnpoint.instances.get(0).x = (i[o + 2]);
		spawnpoint.instances.get(0).y = (i[o + 3]);

		handler.getGame().initMessage("Loading Tiles...");

		o += 4;

		tiles = new ArrayList<ArrayList<String>>();

		for (int x = 0; x < worldWidth; x++) {
			tiles.add(new ArrayList<String>());
			for (int y = 0; y < worldHeight; y++) {
				tiles.get(x).add(s[(int) (x + y * worldWidth + o)]);
			}
		}

		System.out.println(
				"Width: " + worldWidth + " " + tiles.size() + "   Height: " + worldHeight + " " + tiles.get(0).size());

		if (s.length <= worldWidth * worldHeight + o) {
			return;
		}
		backtiles = new ArrayList<ArrayList<String>>();
		int contInt = (int) (worldWidth * worldHeight + o);
		if (s[contInt].contains("BACKTILES")) {
			o = (int) (worldWidth * worldHeight + o + 1);

			for (int x = 0; x < worldWidth; x++) {
				for (int y = 0; y < worldHeight; y++) {
					backtiles.add(new ArrayList<String>());
					backtiles.get(x).add(s[(int) (x + y * worldWidth + o)]);
				}
			}
//			o++;
		}

		contInt = (int) (worldWidth * worldHeight + o);

		if (s.length > (int) (worldWidth * worldHeight + o)) {
			handler.getGame().initMessage("Adding custom entities... ");

			contInt = handleEntityLoading(i, d, s, o);
		}

		System.out.println(s.length);

		for (Slider a : slidersA) {
			a.setValue(0);
		}
		
		while (contInt < s.length) {
			String e = s[contInt].replaceAll(" ", "");

			for (Slider a : slidersA) {
				if (a.getName() == e)
					a.setValue(d[contInt + 1]);
			}

			contInt++;
		}
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

		int o = 0;

		if (s[0].contains("World") || s[0].contains("Save")) {
			o = 2;
		}

		worldWidth = i[o];
		worldHeight = i[o + 1];

		spawnpoint.instances.get(0).x = (i[o + 2] * 18);
		spawnpoint.instances.get(0).y = (i[o + 3] * 18);

		stoneA.setValue(i[o + 19]);
		flowerA.setValue(i[o + 20]);
		treeA.setValue(i[o + 21]);

		beeA.setValue(i[o + 22]);
		butterflyA.setValue(i[o + 23]);
		foxA.setValue(i[o + 24]);

		handler.getGame().initMessage("Loading Tiles...");

		o += 34;

		tiles = new ArrayList<ArrayList<String>>();

		for (int x = 0; x < worldWidth; x++) {
			tiles.add(new ArrayList<String>());
			for (int y = 0; y < worldHeight; y++) {
				tiles.get(x).add("TILE"+s[(int) (x + y * worldWidth + o)]);
//				backtiles[x][y] = 0; 
			}
		}

		if (s.length <= worldWidth * worldHeight + o) {
			return;
		}
		int contInt = (int) (worldWidth * worldHeight + o);
		if (s.length > worldWidth * worldHeight + o) {
			handler.logWorld("Adding custom entities... " + (s.length > worldWidth * worldHeight + 34));

			contInt = handleEntityLoading(i, d, s, o);
		}

		while (contInt < s.length) {
			String e = s[contInt];
			if (e.contains("Bears")) {
				bearA.setValue(i[contInt + 1]);
			} else if (e.contains("Wolves")) {
				wolfA.setValue(i[contInt + 1]);
			} else if (e.contains("Bats")) {
				batA.setValue(i[contInt + 1]);
			} else if (e.contains("Skunks")) {
				skunkA.setValue(i[contInt + 1]);
			} else if (e.contains("Fairies")) {
				fairyA.setValue(i[contInt + 1]);
			}
			contInt++;
		}
	}

	private int handleEntityLoading(int[] i, double[] d, String[] s, int o) {
		int contInt = (int) (worldWidth * worldHeight + o);

		int size = i[contInt];

		System.out.println(size);
		handler.getGame().initMessage("Loading " + size + " custom entities...");

		for (int v = 0; v < size; v++) {
			if (contInt >= s.length - 3) {
				break;
			}
			String e = s[(contInt + 1)]+" ";

			for (MakerEntity a : entities) {
				if (e.contains(a.name+" ")) {
					if (a.image == null)
						setupEntities();
					System.out.println(a.name + " " + (a.image));
					int offset = 4;

					boolean aa = a.name.contains("AreaAdder") && ("AreaAdder ").contains(a.name);
					if (aa) {
						a.fullent.add(s[contInt + 2]);
						a.instances.add(new IconButton(handler, (int) d[contInt + 3], (int) d[contInt + 4], a.image));
						offset++;
					} else {
						a.instances.add(new IconButton(handler, (int) d[contInt + 2], (int) d[contInt + 3], a.image));
					}

					Integer[] ivalues = new Integer[a.ivalues.length];
					for (int j = 0; j < ivalues.length; j++) {
						ivalues[j] = i[contInt + offset] / (aa ? 18 : 1);
						offset++;
					}
					Double[] dvalues = new Double[a.dvalues.length];
					for (int j = 0; j < dvalues.length; j++) {
						dvalues[j] = d[contInt + offset];
						offset++;
					}
					Boolean[] bvalues = new Boolean[a.bvalues.length];
					for (int j = 0; j < bvalues.length; j++) {
						bvalues[j] = Utils.parseBoolean(s[contInt + offset]);
						offset++;
					}

					a.fullivalues.add(ivalues);
					a.fulldvalues.add(dvalues);
					a.fullbvalues.add(bvalues);

					contInt += offset - 1;
				}
			}
		}

		return contInt;
	}

	@Override
	public void renderGUI(Graphics2D g2d) {
		
	}

}

class MakerEntity {

	static MakerEntity use;

	static boolean freeForSelection = true;

	Slider[] ivalues, dvalues;
	ToggleButton[] bvalues;

	ArrayList<Integer[]> fullivalues;
	ArrayList<Double[]> fulldvalues;
	ArrayList<Boolean[]> fullbvalues;
	ArrayList<String> fullent;

	int x, y;

	BufferedImage image, locked;

	IconButton src;
	ArrayList<IconButton> instances;
	int selected = -1, placing = -1;

	boolean usingvalues = false, selectionDrag = false;

	Button delete;

	Handler game;

	String name;

	TextEditor entname;

	public MakerEntity(Handler handler, BufferedImage image, int x, int y, Slider[] ivalues, Slider[] dvalues,
			ToggleButton[] bvalues, String name) {
		this.image = image;
		this.ivalues = ivalues;
		this.dvalues = dvalues;
		this.bvalues = bvalues;
		fullent = new ArrayList<String>();
		fullivalues = new ArrayList<Integer[]>();
		fulldvalues = new ArrayList<Double[]>();
		fullbvalues = new ArrayList<Boolean[]>();
		src = new IconButton(handler, x, y, image);
		locked = Tran.effectAlpha(image, 100);
		instances = new ArrayList<IconButton>();
		game = handler;

		entname = new TextEditor(handler, 1035, 650, 245, 1, "Entity", Public.defaultBoldFont.deriveFont(20.0f));

		delete = new Button(handler, 1180 - Tran.measureString(name, Public.defaultFont.deriveFont(18f)).x, 690, "",
				"Delete");

		this.x = x;
		this.y = y;

		this.name = name;
	}

	public void image(BufferedImage image, int index) {
		instances.get(index).icon = image;
		if (image != null)
			instances.get(index).initImages();
	}

	public void tick(int mx, int my) {
		usingvalues = false;

		boolean hovered = false;

		if (selected != -1) {
			int x = 1180;
			int y = 690;
			if (!selectionDrag) {
				delete.tick();
				usingvalues = delete.hovered || usingvalues;
				if (name == "AreaAdder") {
					entname.tick();
					usingvalues = usingvalues || entname.isSelected();
					fullent.set(selected, entname.getContent());
					y -= 35;
				}
				for (int i = 0; i < ivalues.length; i++) {
					ivalues[i].tick(x, y -= 35);
					usingvalues = usingvalues || ivalues[i].hovered;
					fullivalues.get(selected)[i] = ivalues[i].getValue();
				}
				for (int i = 0; i < dvalues.length; i++) {
					dvalues[i].tick(x, y -= 35);
					usingvalues = usingvalues || dvalues[i].hovered;
					fulldvalues.get(selected)[i] = dvalues[i].getWholeValue();
				}
				for (int i = 0; i < bvalues.length; i++) {
					bvalues[i].setX(1280 - bvalues[i].width);
					bvalues[i].setY(y -= bvalues[i].height + 5);
					bvalues[i].tick();
					usingvalues = usingvalues || bvalues[i].hovered;
					fullbvalues.get(selected)[i] = bvalues[i].on;
				}
			}

			instances.get(selected).tick(mx, my);

			if (game.getMouse().fullLeft) {
				if ((instances.get(selected).hovered || instances.get(selected).on) || (selectionDrag)) {
					if (game.getKeyManager().ctrl) {
						instances.get(selected).x = (int) (Math.floor(mx / 18.0) * 18);
						instances.get(selected).y = (int) (Math.floor(my / 18.0) * 18);
					} else if (game.getKeyManager().shift) {
						instances.get(selected).x = mx - instances.get(selected).width / 2;
						instances.get(selected).y = (int) (Math.floor(my / 18.0 + 1) * 18)-instances.get(selected).height;
					} else {
						instances.get(selected).x = mx - instances.get(selected).width / 2;
						instances.get(selected).y = my - instances.get(selected).height / 2;
					}
				} else {
					if (!usingvalues)
						selected = -1;
				}
			}

			if (delete.on) {
				instances.remove(selected);
				fullent.remove(selected);
				fullivalues.remove(selected);
				fulldvalues.remove(selected);
				fullbvalues.remove(selected);
				selected = -1;
			}
		}

		if (freeForSelection || use == this)
			for (int i = 0; i < instances.size(); i++) {
				if (i != selected && !usingvalues) {
					instances.get(i).tick(mx, my);
					hovered = hovered || instances.get(i).hovered;
					if (instances.get(i).on) {
						selected = i;
						use = this;

						if (fullivalues.size() > selected) {
							entname.setContent(fullent.get(selected));
							for (int j = 0; j < fullivalues.get(selected).length; j++)
								ivalues[j].setValue(fullivalues.get(selected)[j]);
							for (int j = 0; j < fulldvalues.get(selected).length; j++)
								dvalues[j].setValue(fulldvalues.get(selected)[j]);
							for (int j = 0; j < fullbvalues.get(selected).length; j++)
								bvalues[j].on = (fullbvalues.get(selected)[j]);
						} else {
							System.out.println("selected is too big for values");
						}
					}

				}
			}

		if (placing != -1) {
			freeForSelection = false;
			if (game.getKeyManager().ctrl) {
				instances.get(placing).x = (int) (Math.floor(mx / 18.0) * 18);
				instances.get(placing).y = (int) (Math.floor(my / 18.0) * 18);
			} else if (game.getKeyManager().shift) {
				instances.get(placing).x = mx - instances.get(placing).width / 2;
				instances.get(placing).y = (int) (Math.floor(my / 18.0 + 1) * 18)-instances.get(placing).height;
			} else {
				instances.get(placing).x = mx - instances.get(placing).width / 2;
				instances.get(placing).y = my - instances.get(placing).height / 2;
			}
			selected = -1;
			if (instances.get(placing).on) {
				fullent.add(entname.getContent());
				Integer[] newivalues = new Integer[ivalues.length];
				for (int i = 0; i < ivalues.length; i++)
					newivalues[i] = ivalues[i].getValue();
				fullivalues.add(newivalues);
				Double[] newdvalues = new Double[dvalues.length];
				for (int i = 0; i < dvalues.length; i++)
					newdvalues[i] = dvalues[i].getWholeValue();
				fulldvalues.add(newdvalues);
				Boolean[] newbvalues = new Boolean[bvalues.length];
				for (int i = 0; i < bvalues.length; i++)
					newbvalues[i] = bvalues[i].on;
				fullbvalues.add(newbvalues);
				selected = placing;
				placing = -1;
				freeForSelection = true;
			}
		}

		src.tick();

		if (src.on) {
			use = this;
			placing = instances.size();
			selected = -1;
			instances.add(new IconButton(game, x, y, src.icon));
		}

		selectionDrag = game.getMouse().fullLeft && !usingvalues;

	}

	Slider[] clone(Slider[] s) {
		Slider[] out = new Slider[s.length];
		for (int i = 0; i < s.length; i++) {
			out[i] = s[i];
		}

		return out.clone();
	}

	ToggleButton[] clone(ToggleButton[] s) {
		ToggleButton[] out = new ToggleButton[s.length];
		for (int i = 0; i < s.length; i++) {
			out[i] = s[i];
		}

		return out.clone();
	}

	public void setImage(BufferedImage image, int index) {
		instances.get(index).icon = image;
		instances.get(index).initImages();
	}

	public void renderSrc(Graphics2D g) {
		src.render(g);

		g.setFont(Public.defaultFont.deriveFont(18f));

		if (use == this && selected != -1) {
			for (Slider i : ivalues)
				i.render(g);
			for (Slider i : dvalues)
				i.render(g);
			for (ToggleButton i : bvalues)
				i.render(g);
			Tran.drawOutlinedText(g, 1275 - Tran.measureString(name, Public.defaultFont.deriveFont(18f)).x, 715, name,
					1.0, Color.black, Color.white);
			if (name != "Player")
				delete.render(g);
			if (name == "AreaAdder")
				entname.render(g);
		}
	}

	public void renderInstances(Graphics2D g) {

		for (IconButton i : instances) {
			i.render(g);
		}
	}

	public void renderLocked(Graphics2D g) {
		for (IconButton i : instances) {
			g.drawImage(locked, i.x, i.y, null);
		}
	}

	public String outputString() {
		String out = "";
		if (name == "AreaAdder") {
			for (int i = 0; i < instances.size(); i++) {
				out += name + " " + fullent.get(i) + " " + instances.get(i).x + " " + instances.get(i).y + " ";
				for (Integer j : fullivalues.get(i))
					out += j * 18 + " ";
				for (Double j : fulldvalues.get(i))
					out += j + " ";
				for (Boolean j : fullbvalues.get(i))
					out += j + " ";
				out += System.lineSeparator();
			}
		} else {
			for (int i = 0; i < instances.size(); i++) {
				out += name + " " + instances.get(i).x + " " + instances.get(i).y + " ";
				for (Integer j : fullivalues.get(i))
					out += j + " ";
				for (Double j : fulldvalues.get(i))
					out += j + " ";
				for (Boolean j : fullbvalues.get(i))
					out += j + " ";
				out += System.lineSeparator();
			}
		}

		return out;
	}

}
