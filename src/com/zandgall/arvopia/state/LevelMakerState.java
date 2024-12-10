package com.zandgall.arvopia.state;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.lang.Math.*;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.SmartCostume;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.entity.EntityEntry;
import com.zandgall.arvopia.entity.EntityManager;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.input.KeyManager;
import com.zandgall.arvopia.tiles.Tile;
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

public class LevelMakerState extends State {

	private final double maxEntityScroll, maxDataScroll;

	public enum Tools {
		TILER, ERASER, RECT, FLOOD_FILL, LINE
	}
	private Tools tool = Tools.TILER;

	public static int numberOfWorlds = 0;

	private long seed = 0;

	private final Button save, confirm, back, open, image;
	private final ToggleButton useGrid, useForeground;
	private final IconButton plusW, minusW, plusH, minusH;

	private double worldXOffset = 0, worldYOffset = 0, worldWidth = 40, worldHeight = 20;
	private float worldZoom = 2, tabScroll = 0;

	private int W = 1280, H = 720;

	private boolean grid = true;

	private TabMenu tabmenu;
	private TextEditor name;

	private String selectedTile = "TILE1", selectedBackTile = "TILE1";
	private IconButton[] tileButtons, backtileButtons;
	private ArrayList<ArrayList<String>> tiles, backtiles;
	private ArrayList<ArrayList<Integer>> heights = new ArrayList<>();

	private boolean foreground = false;

	private final IconButton setTiler, setEraser, setRect, setFloodFill, setLine;

	private final ArrayList<Slider> slidersA;

	private final MakerEntity spawn_point;
	private final ArrayList<MakerEntity> entities;
	private final HashMap<String, Entity> entityMap = new HashMap<>();

	private final Slider paintRadius;
	public LevelMakerState(Handler handler) {
		super(handler);

		back = new Button(handler, 10, H - 35, "Goes back without generating world", "Back");
		open = new Button(handler, 20 + back.getWidth(), H - 35, "Opens a world", "Open");
		save = new Button(handler, 10 + open.getX() + open.getWidth(), H - 35, "Confirms your world selection", "Save");
		confirm = new Button(handler, 10 + save.getX() + save.getWidth(), H - 35, "Confirms your world selection",
				"Save and load");
		image = new Button(handler, 10 + confirm.getX() + confirm.getWidth(), H - 35, "Writes image to path",
				"Write Image");
		paintRadius = new Slider(handler, 0.25, 5.0, 0.5, false, "Paint Radius");

		useGrid = new ToggleButton(handler, W - 100, 10, "Sets whether you render a grid or not", "Grid", 1);
		useGrid.on = true;
		useForeground = new ToggleButton(handler, W - 100, 60,
				"Set whether you are drawing on the foreground or background", "Foreground", 1);
		useForeground.on = true;

		spawn_point = new MakerEntity(handler, -100, -100, new EntityEntry(PlayerSpawn.class, "PlayerSpawn"));
		spawn_point.addInstance(new PlayerSpawn(handler, 0, 0));
		entities = new ArrayList<>();

		int topOffset = 0, bottomOff = 0;
		boolean top = true;
		for (EntityEntry a : EntityManager.entityEntries.values()) {
			MakerEntity newEntity = new MakerEntity(handler, top ? topOffset : bottomOff, top ? 10 : 60, a);
			entities.add(newEntity);
			if(top)
				topOffset += Tran.measureString("Add " + a.simpleName, Public.fipps).x + 24;
			else
				bottomOff += Tran.measureString("Add " + a.simpleName, Public.fipps).x + 24;
			top = !top;
		}
		maxEntityScroll = max(topOffset, bottomOff);

		tiles = new ArrayList<>();
		backtiles = new ArrayList<>();
		refillWorld();

		setupTileIconButtons();

		tabmenu = new TabMenu(handler, new String[] { "Settings", "World Data", "Entities" }, 0, 0, W, 144,
				false);

		slidersA = new ArrayList<>();

		for (EntityEntry a : EntityManager.entityEntries.values()) {
			// Set to 0 spawning as default
			slidersA.add(new Slider(handler, 0, 1, 0, false, a.name));
			entityMap.put(a.name, a.example);
		}

		maxDataScroll = 115*slidersA.size();

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

		setTiler = new IconButton(handler, W - 40, 144 - 30, "Tile", toolset.get(0, 0),
				"Places a single tile wherever you click");
		setEraser = new IconButton(handler, W - 40, 144 + 10, "Eraser", toolset.get(0, 1),
				"Erases the selected spot");
		setRect = new IconButton(handler, W - 40, 144 + 50, "Rectangle", toolset.get(0, 2),
				"Places the selected tile in the area selected");
		setFloodFill = new IconButton(handler, W - 40, 144 + 90, "Flood fill", toolset.get(0, 3),
				"Fills the empty area");
		setLine = new IconButton(handler, W - 40, 144 + 130, "Line", toolset.get(0, 4),
				"Puts a line of tiles from first point clicked to last one let go");

	}

	private double preMX = 0, preMY = 0;

	private double toolStartX = 0, toolStartY = 0, toolEndX = 0, toolEndY = 0;
	private boolean toolActive = false;

	private void plot(int x, int y, boolean foreground, String tile) {
		if(x < 0 || x >= tiles.size() || y < 0 || y >= tiles.get(x).size())
			return;
		if(foreground) {
			tiles.get(x).set(y, tile);
			Tile.formatTile(tiles, x, y);
			Tile.formatTiles(tiles, x - 1, y - 1, x + 1, y + 1);
		} else {
			backtiles.get(x).set(y, tile);
			Backtile.formatTile(backtiles, x, y);
			Backtile.formatTiles(backtiles, x-1, y-1, x+1, y+1);
		}
	}

	private void plot(double x, double y, double radius, boolean foreground, String tile) {
		for(int tx = (int)(x-radius); tx < x + radius + 1; tx++)
			for(int ty = (int)(y-radius); ty < y + radius + 1; ty++)
				if(pow(tx+0.5-x, 2)+pow(ty+0.5-y, 2) < radius*radius)
					plot(tx, ty, foreground, tile);
	}

	public void setupTileIconButtons() {
		tileButtons = new IconButton[3];
		tileButtons[0] = new IconButton(handler, 250, 60, "TILE256", Tran.scaleUp((BufferedImage)Tile.tileMap.get("TILE256").getImage(), 2), "Switches to grass palette");
		tileButtons[1] = new IconButton(handler, 290, 60, "FENCE255", Tran.scaleUp((BufferedImage)Tile.tileMap.get("FENCE0").getImage(), 2), "Switches to fence palette");
		tileButtons[2] = new IconButton(handler, 330, 60, "BRIDGE0", Tran.scaleUp((BufferedImage)Tile.tileMap.get("BRIDGE0").getImage(), 2), "Switches to bridge palette");

		backtileButtons = new IconButton[1];
		backtileButtons[0] = new IconButton(handler, 250, 60, "BACKGRASS0", Tran.scaleUp((BufferedImage)Backtile.tileMap.get("BACKGRASS0").getImage(), 2), "Switches to backtile dirt palette");
	}

	private void refillWorld() {
		for (int x = 0; x < worldWidth; x++) {
			if(x >= tiles.size())
				tiles.add(new ArrayList<>());
			if(x >= backtiles.size())
				backtiles.add(new ArrayList<>());
			for (int y = tiles.get(x).size(); y < worldHeight; y++) {
				tiles.get(x).add("TILE0");
				backtiles.get(x).add("TILE0");
			}
		}
	}

	private void floodFill(int x, int y, boolean foreground, String replacement) {
		floodFill(x, y, foreground, foreground ? tiles : backtiles, foreground ? tiles.get(x).get(y) : backtiles.get(x).get(y), replacement);
	}

	private void floodFill(int x, int y, boolean foreground, ArrayList<ArrayList<String>> tiles, String target, String replacement) {
		if (!(Public.identifyRange(0, worldWidth - 1, x) && Public.identifyRange(0, worldHeight - 1, y)))
			return;
		if (Objects.equals(tiles.get(x).get(y), replacement) || !Objects.equals(tiles.get(x).get(y), target))
			return;

		plot(x, y, foreground, replacement);

		floodFill(x, y + 1, foreground, tiles, target, replacement);
		floodFill(x, y - 1, foreground, tiles, target, replacement);
		floodFill(x - 1, y, foreground, tiles, target, replacement);
		floodFill(x + 1, y, foreground, tiles, target, replacement);
	}

	private void findHeights() {
		heights = new ArrayList<>();
		for (int i = 0; i < worldWidth; i++) {
			heights.add(new ArrayList<>());
			for(int j = 0; j < worldHeight; j++) {
				if(Tile.getTile(tiles.get(i).get(j)).isSolid() && (j == 0 || !Tile.getTile(tiles.get(i).get(j-1)).isSolid())) {
					heights.get(i).add(j);
				}
			}
		}
	}

	@Override
	public void tick() {
		handler.getGame().useResize(false);
		confirm.tick();
		open.tick();
		save.tick();
		image.tick();
		if (tool==Tools.TILER||tool==Tools.ERASER||tool==Tools.LINE)
			paintRadius.tick(image.getX() + image.getWidth() + 10, H-35);
		else paintRadius.hovered = false;


		Point shift = new Point(0, 0);
		if(KeyManager.typedKeys[KeyEvent.VK_LEFT]) {
			shift.x += (KeyManager.ctrl ? 5 : 1) * (KeyManager.shift ? 5 : 1);
		}
		if(KeyManager.typedKeys[KeyEvent.VK_RIGHT]) {
			shift.x -= (KeyManager.ctrl ? 5 : 1) * (KeyManager.shift ? 5 : 1);
		}
		if(KeyManager.typedKeys[KeyEvent.VK_UP]) {
			shift.y += (KeyManager.ctrl ? 5 : 1) * (KeyManager.shift ? 5 : 1);
		}
		if(KeyManager.typedKeys[KeyEvent.VK_DOWN]) {
			shift.y -= (KeyManager.ctrl ? 5 : 1) * (KeyManager.shift ? 5 : 1);
		}
		if(!shift.equals(new Point(0, 0))) {
			ArrayList<ArrayList<String>> newTiles = new ArrayList<>();
			for(int i = 0; i < tiles.size() + shift.x; i++) {
				newTiles.add(new ArrayList<>());
				for(int j = 0; j < tiles.get(0).size() + shift.y; j++) {
					if(i + shift.x < 0 || i + shift.x >= tiles.size() || j + shift.y < 0 || j + shift.y >= tiles.get(i).size()) {
						newTiles.get(i).add("TILE0");
					}
					else {
						newTiles.get(i).add(tiles.get(i+shift.x).get(j+shift.y));
					}
				}
				for(MakerEntity a : entities) {
					for(Entity b : a.instances) {
						b.setX(b.getX() - (shift.x / 140.0) * 18);
						b.setX(b.getX() - (shift.y / 140.0) * 18);
					}
				}
			}
			tiles = newTiles;
		}
		refillWorld();

		double mx = handler.getMouse().rMouseX();
		double my = handler.getMouse().rMouseY();
		double ms = handler.getMouse().getMouseScroll();

		int rtx = (int) ((((mx - W / 2.0) + worldWidth * 9 * worldZoom) - worldXOffset * worldZoom) / (worldZoom));
		int rty = (int) ((((my - H / 2.0) + worldHeight * 9 * worldZoom) - worldYOffset * worldZoom) / (worldZoom));

		int tx = (int) ((((mx - W / 2.0) + worldWidth * 9 * worldZoom) - worldXOffset * worldZoom) / (18 * worldZoom));
		int ty = (int) ((((my - H / 2.0) + worldHeight * 9 * worldZoom) - worldYOffset * worldZoom) / (18 * worldZoom));

		double dx = (((mx - W / 2.0) + worldWidth * 9 * worldZoom) - worldXOffset * worldZoom) / (18 * worldZoom);
		double dy = (((my - H / 2.0) + worldHeight * 9 * worldZoom) - worldYOffset * worldZoom) / (18 * worldZoom);

		if (handler.getGame().getDisplay().getCanvas().getWidth() != W ||
				handler.getGame().getDisplay().getCanvas().getHeight() != H) {
			handler.width = handler.getGame().getDisplay().getCanvas().getWidth();
			handler.height = handler.getGame().getDisplay().getCanvas().getHeight();
			handler.getGame().width = handler.width;
			handler.getGame().height = handler.height;
			W = handler.width;
			H = handler.height;
			back.setY(H - 35);
			open.setY(H - 35);
			save.setY(H - 35);
			confirm.setY(H - 35);
			image.setY(H - 35);
			useGrid.setX(W - 10 - useGrid.width);
			useForeground.setX(W - 10 - useForeground.width);
			setTiler.x = W - 40;
			setEraser.x = W - 40;
			setRect.x = W - 40;
			setFloodFill.x = W - 40;
			setLine.x = W - 40;

			tabmenu = new TabMenu(handler, new String[] { "Settings", "World Data", "Entities" }, 0, 0, W, 144,
					false);
			name = new TextEditor(handler, 250, 5, W-400, 1, "World", Public.runescape.deriveFont(30f));
		}
		if (image.on) {
			writeImage(Game.prefix + "/World/" + name.getContent() + ".png");
		}

		if (confirm.on) {
			handler.getMouse().setLeftClicked(false);
			handler.getGame().width = 720;
			handler.getGame().height = 405;
			handler.width = 720;
			handler.height = 400;
			handler.getGame().useResize(true);
			handler.getGame().getDisplay().setSize(720, 405);
			saveWorld(Game.prefix + "/World/" + name.getContent() + ".arv");
			handler.getGame().gameState.loadWorld(Game.prefix + "/World/" + name.getContent() + ".arv");
			setState(handler.getGame().gameState);
			return;
		}
		if (save.on) {
			saveWorld(Game.prefix + "/World/" + name.getContent() + ".arv");
		}
		back.tick();
		if (back.on) {
			handler.getGame().width = 720;
			handler.getGame().height = 405;
			handler.width = 720;
			handler.height = 400;
			handler.getGame().useResize(true);
			handler.getGame().getDisplay().setSize(720, 405);
			setState(getPrev());
		}

		if (open.on) {
			FileChooser f = new FileChooser();
			String s = f.getFile(Game.prefix + "/World");
			loadWorld(s);
		}

		String pre_tab = tabmenu.getTab();
		tabmenu.tick();

		if (!Objects.equals(tabmenu.getTab(), pre_tab)) {
			MakerEntity.selected_entity = null;
			MakerEntity.selected_maker = null;
			tabScroll = 0;
			if(tabmenu.getTab().equals("World Data")) {
				seed = System.nanoTime();
				findHeights();
			}
		}

		if (Objects.equals(tabmenu.getTab(), "Settings")) {

			name.tick();

			useGrid.tick();
			useForeground.tick();
			foreground = useForeground.on;
			grid = useGrid.on;

			plusW.tick();
			if (plusW.on)
				worldWidth += (KeyManager.ctrl ? 1 : (KeyManager.shift ? 20 : 5));

			minusW.tick();
			if (minusW.on)
				worldWidth -= (KeyManager.ctrl ? 1 : (KeyManager.shift ? 20 : 5));

			plusH.tick();
			if (plusH.on)
				worldHeight += (KeyManager.ctrl ? 1 : (KeyManager.shift ? 20 : 5));

			minusH.tick();
			if (minusH.on)
				worldHeight -= (KeyManager.ctrl ? 1 : (KeyManager.shift ? 20 : 5));

			worldWidth = Math.max(worldWidth, 40);
			worldHeight = Math.max(worldHeight, 20);

			if (worldWidth == 40)
				minusW.hovered = true;

			if (worldHeight == 20)
				minusH.hovered = true;

			if (foreground) {
				for (IconButton tileButton : tileButtons) {
					if (tileButton != null) {
						tileButton.tick();
						if (tileButton.on)
							selectedTile = tileButton.name;
						else if (Objects.equals(selectedTile, tileButton.name))
							tileButton.hovered = true;
					}
				}
			} else {
				for (IconButton tileButton : backtileButtons) {
					if (tileButton != null) {
						tileButton.tick();
						if (tileButton.on)
							selectedBackTile = tileButton.name;
						else if (Objects.equals(selectedBackTile, tileButton.name))
							tileButton.hovered = true;
					}
				}
			}
		} else if (Objects.equals(tabmenu.getTab(), "World Data")) {
			if(my < 144)
				tabScroll +=ms*10;
			int x_offset = 10;
			tabScroll = (float) Public.range(-maxDataScroll-5+handler.getCanvasWidth(), 0, tabScroll);
			for (Slider s : slidersA) {
				s.tick((int) (x_offset + tabScroll), 20);
				x_offset += 115;
			}
		} else if (Objects.equals(tabmenu.getTab(), "Entities")) {
			if(my < 144)
				tabScroll +=ms*10;
			tabScroll = (float) Public.range(-maxEntityScroll-20+handler.getCanvasWidth(), 0, tabScroll);
			for (MakerEntity e : entities)
				e.tick(rtx, rty, (int) -tabScroll-10);
			spawn_point.tick(rtx, rty, 0);
		}

		setTiler.tick();
		setRect.tick();
		setEraser.tick();
		setFloodFill.tick();
		setLine.tick();

		if (setTiler.on || KeyManager.checkBind("Paint"))
			tool = Tools.TILER;
		if (setEraser.on || KeyManager.checkBind("Eraser"))
			tool = Tools.ERASER;
		if (setRect.on || KeyManager.checkBind("Rectangle"))
			tool = Tools.RECT;
		if (setFloodFill.on || KeyManager.checkBind("Fill"))
			tool = Tools.FLOOD_FILL;
		if (setLine.on || KeyManager.checkBind("Line"))
			tool = Tools.LINE;

		if (my > 144) {

			worldZoom -= (ms / 20) * Math.max(worldZoom, 0.2);

			if (handler.getMouse().fullLeft && !Objects.equals(tabmenu.getTab(), "Entities")
					&& !(setLine.hovered || setTiler.hovered || setFloodFill.hovered || setRect.hovered
							|| setEraser.hovered
							|| back.hovered || save.hovered || open.hovered || confirm.hovered || paintRadius.hovered)) {
				switch (tool) {
					case TILER:
						plot(dx, dy, paintRadius.getWholeValue(), foreground, foreground ? selectedTile : selectedBackTile);
						break;
					case ERASER:
						plot(dx, dy, paintRadius.getWholeValue(), foreground, "TILE0");
						break;
					case RECT:
					case LINE:
						if (Public.identifyRange(0, worldWidth - 1, tx) && Public.identifyRange(0, worldHeight - 1, ty)) {
							if (!toolActive) {
								toolStartX = tx;
								toolStartY = ty;
								toolActive = true;
							}

							toolEndX = tx;
							toolEndY = ty;
						}
						break;
					case FLOOD_FILL:
						floodFill(tx, ty, foreground, foreground ? selectedTile : selectedBackTile);
						break;
				}
			} else {
				if (toolActive)
					if (tool == Tools.RECT) {
						fillRect((int) min(toolStartX, toolEndX), (int)Math.min(toolStartY, toolEndY),
								(int)Math.max(toolStartX, toolEndX), (int)Math.max(toolStartY, toolEndY), foreground, foreground ? selectedTile : selectedBackTile);
					} else if (tool == Tools.LINE) {
						for (Point a : linePoints((int) toolStartX, (int) toolStartY, (int) toolEndX, (int) toolEndY))
							if (foreground)
								tiles.get(a.x).set(a.y, selectedTile);
							else
								backtiles.get(a.x).set(a.y, selectedBackTile);
					}
				toolActive = false;
			}

			switch (tool) {
				case TILER -> setTiler.hovered = true;
				case ERASER -> setEraser.hovered = true;
				case RECT -> setRect.hovered = true;
				case FLOOD_FILL -> setFloodFill.hovered = true;
				case LINE -> setLine.hovered = true;
			}

			if (handler.getMouse().fullRight) {

				worldXOffset += (mx - preMX) / worldZoom;

				worldYOffset += (my - preMY) / worldZoom;

			}

		}

		worldZoom = (float) Public.range(0.1, 20, worldZoom);

		preMX = mx;
		preMY = my;
	}

	private void fillRect(int start_x, int start_y, int end_x, int end_y, boolean foreground, String selectedTile) {
		for (int x = start_x; x <= end_x; x++)
			for (int y = start_y; y <= end_y; y++)
				if(foreground)
					tiles.get(x).set(y, selectedTile);
				else backtiles.get(x).set(y, selectedTile);
		if(foreground)
			Tile.formatTiles(tiles, start_x-1, start_y-1, end_x+1, end_y+1);
		else Backtile.formatTiles(backtiles, start_x-1, start_y-1, end_x+1, end_y+1);
	}

	private ArrayList<Point> linePoints(int x0, int y0, int x1, int y1) {
		ArrayList<Point> out = new ArrayList<>();

		double m = (x1-1.0*x0)/(y1-1.0*y0);
		out.add(new Point(x0, y0));
		for(int y = min(y0, y1); y < max(y0, y1); y++)
			out.add(new Point(x0 + (int)(y * m), y));

		return out;
	}

	private void saveWorld(String path) {

		File file = new File(path);
		if(file.exists())
			if(!file.delete()) {
				System.err.println("Unable to delete to recreate \""+path+"\"");
				return;
			} else {
				try {
					if (!file.createNewFile()) {
						System.err.println("Unable to create \"" + path + "\"");
						return;
					}
				} catch(Exception e) {
					System.err.println("Unable to create \"" + path + "\"");
					return;
				}
			}
		try(PrintWriter writer = new PrintWriter(path)) {
			writer.println("World\n0.8");
			writer.println((int)worldWidth + " " + (int)worldHeight);
			writer.print((int)spawn_point.instances.get(0).getX());
			writer.print(" ");
			writer.println((int)spawn_point.instances.get(0).getY());
			for(int j = 0; j < worldHeight; j++) {
				for(int i = 0; i < worldWidth; i++) {
					writer.print(tiles.get(i).get(j)+" ");
				}
				writer.println();
			}

			for(int j = 0; j < worldHeight; j++) {
				for(int i = 0; i < worldWidth; i++) {
					writer.print(backtiles.get(i).get(j)+" ");
				}
				writer.println();
			}

			int numOfEntities = 0;
			for(MakerEntity e : entities)
				numOfEntities += e.instances.size();
			writer.println(numOfEntities);
			for(MakerEntity e : entities)
				writer.print(e.outputString());
			for(Slider a : slidersA)
				if(a.getWholeValue()!=0)
					writer.println(a.getName() + " " + a.getWholeValue());
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private void writeImage(String path) {
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
		spawn_point.renderInstances(g);

		g.dispose();

		ImageLoader.saveImage(out, path);
	}

	@Override
	public void render(Graphics2D g) {
		g.setTransform(new AffineTransform());

		g.setColor(new Color(120, 225, 255));
		g.fillRect(0, 0, W, H);

		renderGrid(g);
		renderGui(g);

	}

	private void renderGrid(Graphics2D g) {
		g.translate(W / 2, H / 2);
		
		g.scale(worldZoom, worldZoom);
		
		g.translate(-worldWidth * 9, -worldHeight * 9);

		g.translate(worldXOffset, worldYOffset);

		g.setColor(Color.black);
		g.drawRect(0, 0, (int) worldWidth * 18, (int) worldHeight * 18);
		
		int startX = (int) Public.range(0, worldWidth, (-(worldXOffset - worldWidth * 9) - (W/2.0/worldZoom))/18);
		int startY = (int) Public.range(0, worldHeight, (-(worldYOffset - worldHeight * 9) - (H/2.0/worldZoom))/18);
		
		int endX = (int) Public.range(0, worldWidth, (W/worldZoom-(worldXOffset - worldWidth * 9) - (W/2.0/worldZoom))/18 + 1);
		int endY = (int) Public.range(0, worldHeight, (H/worldZoom-(worldYOffset - worldHeight * 9) - (H/2.0/worldZoom))/18 + 1);
		
		for (int x = startX; x < endX; x++) {
			for (int y = startY; y < endY; y++) {
				
				if (foreground)
					g.drawImage(Backtile.getTile(backtiles.get(x).get(y)).getImage(), x * 18, y * 18, null);

				try {
					g.drawImage(Tile.getTile(tiles.get(x).get(y)).getImage(), x * 18, y * 18, null);
				} catch (IndexOutOfBoundsException e) {
					g.setColor(Color.magenta);
					g.fillRect( x * 18,  y * 18, 18, 18);
				}

				if (!foreground)
					g.drawImage(Backtile.getTile(backtiles.get(x).get(y)).getImage(), x * 18, y * 18, null);

				if (grid)
					g.drawRect(x * 18, y * 18, 18, 18);
			}
		}


		if (toolActive && tool==Tools.LINE) {
			g.setColor(Color.red);
			for (Point a : linePoints((int)toolStartX, (int)toolStartY, (int)toolEndX, (int)toolEndY))
				g.drawRect(a.x * 18, a.y * 18, 18, 18);
			g.drawLine((int)toolStartX * 18 + 9, (int)toolStartY * 18 + 9, (int)toolEndX * 18 + 9, (int)toolEndY * 18 + 9);
		} else if (toolActive) {
			int mx = (int) Math.min(toolStartX, toolEndX);
			int my = (int) Math.min(toolStartY, toolEndY);

			int xx = (int) Math.max(toolStartX, toolEndX);
			int xy = (int) Math.max(toolStartY, toolEndY);

			g.setColor(Color.red);
			g.drawRect(mx * 18, my * 18, (xx - mx + 1) * 18, (xy - my + 1) * 18);
		}

		if (Objects.equals(tabmenu.getTab(), "Entities")) {
			for (MakerEntity e : entities)
				e.renderInstances(g);
			spawn_point.renderInstances(g);
		} else {
			BufferedImage grayed_entities = new BufferedImage(handler.getCanvasWidth(), handler.getCanvasHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D h = grayed_entities.createGraphics();
			h.setTransform(g.getTransform());
			for (MakerEntity e : entities)
				e.renderInstances(h);
			spawn_point.renderInstances(h);
			h.dispose();
			AffineTransform e_pre = g.getTransform();
			g.setTransform(new AffineTransform());
			Composite pre_comp = g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g.drawImage(grayed_entities, 0, 0, null);
			g.setComposite(pre_comp);
			g.setTransform(e_pre);
		}
		if(tabmenu.getTab().equals("World Data")) {
			int offset = 0;
			for (Slider s : slidersA) {
				Random rand = new Random(seed + offset);
				offset++;
				g.setColor(Color.getHSBColor((float) ((offset*1.618033988749894) % 360.0), 1, 1));
				for(int i = 0; i < s.getWholeValue() * s.getWholeValue() * worldWidth; i++) {
					if(!entityMap.containsKey(s.getName())) {
						System.out.println("Can't find "+s.getName());
						continue;
					}
					Entity a = entityMap.get(s.getName());
					if(a.creature&&((Creature)a).flies) {
						int tile_x, tile_y, tries = 0;
						do {
							tile_x = rand.nextInt((int)worldWidth);
							tile_y = rand.nextInt((int)worldHeight);
							tries++;
						} while (!tiles.get(tile_x).get(tile_y).equals("TILE0")&&tries<100);
						a.setX(tile_x*18-a.getWidth()/2.0);
						a.setY(tile_y*18-a.getHeight()/2.0);
						a.render(g);
					} else {
						int tile_x, tries = 0;
						do {
							tile_x = rand.nextInt((int) worldWidth);
							tries++;
						} while (heights.get(tile_x).size() == 0 && tries < 100);
						int tile_y;
						if(tries >= 100)
							tile_y = (int) worldHeight;
						else
							tile_y = heights.get(tile_x).get(rand.nextInt(heights.get(tile_x).size()));

						if (tile_y < 0)
							continue;
						a.setX(tile_x*18-a.getWidth()/2.0);
						a.setY(tile_y*18-a.getbounds().y-a.getbounds().height);
						a.render(g);
					}
				}
			}
		}

		g.setTransform(new AffineTransform());
		g.setColor(Color.black);
		double nr = worldZoom * 18 * paintRadius.getWholeValue();
		if (tool==Tools.TILER||tool==Tools.ERASER||tool==Tools.LINE)
			g.drawOval(handler.getMouse().rMouseX()-(int)nr, handler.getMouse().rMouseY()-(int)nr, (int)(nr*2), (int)(nr*2));
	}

	private void renderGui(Graphics2D g) {

		tabmenu.render(g);

		if (Objects.equals(tabmenu.getTab(), "Settings")) {
			useGrid.render(g);
			useForeground.render(g);

			name.render(g);

			plusW.render(g);
			minusW.render(g);
			plusH.render(g);
			minusH.render(g);

			g.setColor(Color.black);
			g.setFont(Public.runescape.deriveFont(25f));
			g.drawString("Width: " + (int)worldWidth, 130 - Tran.measureString("Width: " + (int)worldWidth, g.getFont()).x / 2,
					10 + Tran.measureString("Width: " + (int)worldWidth, g.getFont()).y);
			g.drawString("Height: " + (int)worldHeight,
					130 - Tran.measureString("Height: " + (int)worldHeight, g.getFont()).x / 2,
					65 + Tran.measureString("Height: " + (int)worldHeight, g.getFont()).y);
			if (foreground) {
				for (IconButton tileButton : tileButtons) {
					if (tileButton != null)
						tileButton.render(g);
				}
			} else {
				for (IconButton backtileButton : backtileButtons) {
					if (backtileButton != null)
						backtileButton.render(g);
				}
			}
		} else if (Objects.equals(tabmenu.getTab(), "World Data")) {
			g.setColor(Color.black);
			float x = 60;
			g.setFont(Public.fipps);
			for (Slider s : slidersA) {
				s.render(g);
				g.drawString(s.getName() + ":", (int)(x+ tabScroll -Tran.measureString(s.getName()+":", g.getFont()).x/2), 60);
				g.drawString(""+s.getWholeValue(), (int)(x+ tabScroll -Tran.measureString(""+s.getWholeValue(), g.getFont()).x/2), 80);
				x += 115;
			}
		} else if (Objects.equals(tabmenu.getTab(), "Entities")) {
			for (MakerEntity e : entities)
				e.renderSrc(g);
			spawn_point.renderSrc(g);
		}

		setTiler.render(g);
		setRect.render(g);
		setEraser.render(g);
		setFloodFill.render(g);
		setLine.render(g);
		
		confirm.render(g);
		back.render(g);
		open.render(g);
		save.render(g);
		image.render(g);
		if (tool==Tools.TILER||tool==Tools.ERASER||tool==Tools.LINE)
			paintRadius.render(g);
	}

	@Override
	public void init() {
		handler.getGame().getDisplay().setSize(1280, 720);
		setupTileIconButtons();
	}

	private void loadWorld(String path) {
		Scanner scanner;
		if(path.startsWith("/"))
			try {
				scanner = new Scanner(FileLoader.loadResource(path));
			} catch(IOException e) {
				e.printStackTrace();
				return;
			}
		else
			try {
				scanner = new Scanner(new File(path));
			} catch(IOException e) {
				e.printStackTrace();
				return;
			}

		scanner.nextLine(); // Ignore first line

		float worldVersion = scanner.nextFloat();
		if(worldVersion < 0.8) {
			System.err.println("No longer supports worlds below version 0.8! Contact zandgall if you think there is an issue");
			return;
		}
		worldWidth = scanner.nextInt();
		worldHeight = scanner.nextInt();

		spawn_point.instances.get(0).setX(scanner.nextInt());
		spawn_point.instances.get(0).setY(scanner.nextInt());

		tiles = new ArrayList<>();
		for (int x = 0; x < worldWidth; x++) {
			tiles.add(new ArrayList<>((int) worldHeight));
			for(int y = 0; y < worldHeight; y++)
				tiles.get(x).add("");
		}
		backtiles = new ArrayList<>();
		for (int x = 0; x < worldWidth; x++) {
			backtiles.add(new ArrayList<>());
			for(int y = 0; y < worldHeight; y++)
				backtiles.get(x).add("");
		}

		for(int y = 0; y < worldHeight; y++)
			for (int x = 0; x < worldWidth; x++)
				tiles.get(x).set(y, scanner.next());

		for(int y = 0; y < worldHeight; y++)
			for (int x = 0; x < worldWidth; x++)
				backtiles.get(x).set(y, scanner.next());

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
							parTypes[j + 3] = int.class;
							par[j + 3] = Integer.parseInt(content[j * 2 + 4]);
						}
						case "f" -> {
							parTypes[j + 3] = float.class;
							par[j + 3] = Float.parseFloat(content[j * 2 + 4]);
						}
						case "d" -> {
							parTypes[j + 3] = double.class;
							par[j + 3] = Double.parseDouble(content[j * 2 + 4]);
						}
						case "b" -> {
							parTypes[j + 3] = boolean.class;
							par[j + 3] = Boolean.parseBoolean(content[j * 2 + 4]);
						}
					}
				for(MakerEntity a : entities) {
					if(a.entry.entityClass.equals(clazz))
						a.addInstance(clazz.getConstructor(parTypes).newInstance(par));
				}
			} catch(Exception e) {
				e.printStackTrace();
				System.err.printf("Exception while trying to instantiate entity \"%s\" at (%s, %s)%n", content[0], content[1], content[2]);
			}

		}

		while(scanner.hasNext()) {
			String ent = scanner.next();
			double amm = scanner.nextDouble();
			if(EntityManager.entityEntries.containsKey(ent))
				for(Slider a : slidersA)
					if(a.getName().equals(ent))
						a.setValue(amm);
		}
	}

	@Override
	public void renderGUI(Graphics2D g2d) {
		
	}

	public static class PlayerSpawn extends Entity {

		public PlayerSpawn(Handler handler, double x, double y) {
			super(handler, x, y, 36, 54, false, false, false, false);
		}

		@Override
		public void render(Graphics2D g) {
			g.drawImage(SmartCostume.getStill().getSubimage(35, 23, 36, 54), (int)x, (int)y, null);
		}
	}

}

class MakerEntity {

	static Entity selected_entity; // Test whether I need to make this an index
	private static Button delete;
	static MakerEntity selected_maker;

	private final ArrayList<ValueSelector> values;

	boolean adding;
	private final int x;
	private double xMouseOff = 0, yMouseOff = 0;

	public final EntityEntry entry;
	public ArrayList<Entity> instances;


	private final Handler game;

	private final String name;

	private final Button add;

	public MakerEntity(Handler handler, int x, int y, EntityEntry entry) {
		this.entry = entry;

		instances = new ArrayList<>();

		game = handler;
		if(delete==null)
			delete = new Button(handler, handler.width - 100, handler.height- 50, "", "Delete");
		this.name = entry.simpleName;
		this.x = x;
		add = new Button(handler, x, y, "Add a \""+name+"\" to the world", "Add "+name);
		BytecodeReadingParanamer paranamer = new BytecodeReadingParanamer();
		String[] names = paranamer.lookupParameterNames(entry.detailedConstructor, false);
		values = new ArrayList<>();
		for(int i = 3; i < entry.detailedConstructor.getParameterCount(); i++) {
			String name = (names == null || names.length <= i) ? entry.detailedConstructor.getParameters()[i].getName() : names[i];
			Class<?> type = entry.detailedConstructor.getParameterTypes()[i];
			if (int.class.equals(type))
				values.add(new IntValueSelector(game, i - 3, name));
			else if (long.class.equals(type))
				values.add(new IntValueSelector(game, i - 3, name));
			else if (double.class.equals(type))
				values.add(new DoubleValueSelector(game, i - 3, name));
			else if (float.class.equals(type))
				values.add(new DoubleValueSelector(game, i - 3, name));
			else if (boolean.class.equals(type))
				values.add(new BooleanValueSelector(game, i - 3, name));
			else if (String.class.equals(type))
				values.add(new StringValueSelector(game, i - 3, name));
		}
	}

	public void tick(float mx, float my, int scroll) {
		delete.setX(game.getCanvasWidth()-100);
		delete.setY(game.getCanvasHeight()-50);
		boolean usingUI = false;
		if(game.getMouse().rMouseY() < 144) {
			usingUI = true;
			add.setX(x - scroll);
			add.tick();
			if(add.on) {
				adding = true;
				instances.add(entry.spawn(0, 0));
				selected_entity = instances.get(instances.size()-1);
				selected_maker = this;
				for(ValueSelector i : values)
					i.addInstance();
			}
		}
		if(selected_entity==null||selected_maker!=this)
			adding = false;
		if(adding) {
			selected_entity.setX(mx - selected_entity.getbounds().width/2.0);
			selected_entity.setY(my - selected_entity.getbounds().height/2.0);
			if(KeyManager.shift)
				selected_entity.setY(floor((my + yMouseOff - selected_entity.getbounds().y - selected_entity.getbounds().height)/18) * 18 + selected_entity.getbounds().y + selected_entity.getbounds().height);
			if(game.getMouse().fullLeft && !game.getMouse().pFullLeft)
				adding = false;
		}
		if(selected_maker==this) {

			boolean reconstruct = false;
			if(!name.equals("PlayerSpawn")) {
				delete.tick();
				usingUI |= delete.hovered;
			}
			for(ValueSelector i : values) {
				reconstruct |= i.tick();
				usingUI |= i.usingUI();
			}
			if(reconstruct) {
				Object[] params = new Object[values.size() + 3];
				params[0] = game;
				params[1] = selected_entity.getX();
				params[2] = selected_entity.getY();
				for(int i = 3; i < params.length; i++)
					params[i] = values.get(i-3).get();
				try {
					selected_maker = this;
					int ind = instances.indexOf(selected_entity);
					instances.set(ind, (Entity) entry.detailedConstructor.newInstance(params));
					instances.get(ind).setX((double)params[1]);
					instances.get(ind).setY((double)params[2]);
					selected_entity = instances.get(ind);
				} catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			} else if(game.getMouse().fullLeft && game.getMouse().pFullLeft && !usingUI && !adding) {
				selected_entity.setX(mx + xMouseOff);
				selected_entity.setY(my + yMouseOff);
				if(KeyManager.shift)
					selected_entity.setY(floor((my + yMouseOff - selected_entity.getbounds().y - selected_entity.getbounds().height)/18) * 18 + selected_entity.getbounds().y + selected_entity.getbounds().height);
			}
		}

		if(game.getMouse().fullLeft && !game.getMouse().pFullLeft&&!usingUI && !adding) {
			boolean found = false;
			for (Entity e : instances) {
				if (mx >= e.getbounds().x + e.getX() && my >= e.getbounds().y + e.getY() && mx <= e.getbounds().x + e.getX() + e.getbounds().width && my <= e.getbounds().y + e.getY() + e.getbounds().height) {
					found = true;
					if (selected_entity != e) {
//						e.setX(e.getX() + mx - pmx);
//						e.setY(e.getY() + my - pmy);
//					} else {
						selected_entity = e;
						selected_maker = this;
						for (ValueSelector i : values)
							i.switchInstance(instances.indexOf(e));
					}
					xMouseOff = e.getX() - mx;
					yMouseOff = e.getY() - my;
				}
			}
			if (!found && selected_maker == this) {
				selected_maker = null;
				selected_entity = null;
			}
		}

		if(delete.on)
			if(selected_maker == this && selected_entity!=null && !name.equals("PlayerSpawn")) {
				instances.remove(selected_entity);
				for(ValueSelector i : values)
					i.deleteCurrentInstance();
				selected_maker = null;
				selected_entity = null;
			}

	}

	public void addInstance(Entity instance) {
		instances.add(instance);
		selected_entity = instance;
		selected_maker = this;
		for(ValueSelector i : values)
			i.addInstance();
	}

	public void renderSrc(Graphics2D g) {
		add.render(g);

		if (selected_maker == this) {
			for (ValueSelector i : values) {
				g.setFont(Public.fipps.deriveFont(18f));
				i.render(g);
			}
			if (!Objects.equals(name, "PlayerSpawn"))
				delete.render(g);
		}
	}

	public void renderInstances(Graphics2D g) {
		for (Entity i : instances) {
			i.render(g);
			g.setColor(Color.red);
			g.drawRect((int)i.getX()+i.getbounds().x, (int)i.getY()+i.getbounds().y, i.getbounds().width, i.getbounds().height);
		}
	}

	public String outputString() {
		StringBuilder out = new StringBuilder();
		for(int i = 0; i < instances.size(); i++) {
			out.append(name).append(" ").append((int)instances.get(i).getX()).append(" ").append((int)instances.get(i).getY());
			for(ValueSelector v : values)
				v.writeType(out, i);
			for(ValueSelector v : values)
				v.write(out, i);
			out.append("\n");
		}

		return out.toString();
	}

}

abstract class ValueSelector {
	protected final Handler game;
	protected final int index;
	protected final String name;
	protected int currentInstance;
	public ValueSelector(Handler game, int index, String name) {
		this.game = game;
		this.index = index;
		this.name = name;
		currentInstance = 0;
	}

	public abstract void addInstance();
	public abstract void switchInstance(int newInstance);
	public abstract void deleteCurrentInstance();
	// Returns true if there is an update to the content
	public abstract boolean tick();
	public abstract void render(Graphics2D g);

	public abstract void writeType(StringBuilder content, int index);

	public abstract void write(StringBuilder content, int index);

	public abstract Object get();

	public abstract boolean usingUI();
}

class IntValueSelector extends ValueSelector {
	private final IconButton add, sub;
	public ArrayList<Integer> instances;
	public int currentValue;
	public IntValueSelector(Handler game, int index, String name) {
		super(game, index, name);
		instances = new ArrayList<>();
		currentValue = 0;
		add = new IconButton(game, game.getCanvasWidth()-32, game.getCanvasHeight()-index*60-120+13, ImageLoader.loadImage("/textures/Gui/+-.png").getSubimage(0, 0, 32, 32));
		sub = new IconButton(game, game.getCanvasWidth()-100, game.getCanvasHeight()-index*60-120+13, ImageLoader.loadImage("/textures/Gui/+-.png").getSubimage(32, 0, 32, 32));
	}

	public void addInstance() {
		currentInstance = instances.size();
		instances.add(currentValue);
	}

	public void switchInstance(int index) {
		this.currentInstance = index;
		currentValue = instances.get(index);
	}

	public void deleteCurrentInstance() {
		instances.remove(instances.get(currentInstance)); // Make sure we're removing by index, not object
		this.currentInstance = -1;
		currentValue = 0;
	}
	public boolean tick() {
		sub.x = game.getCanvasWidth() - 100;
		sub.y = game.getCanvasHeight() - index*60-120+13;
		add.x = game.getCanvasWidth() - 32;
		add.y = game.getCanvasHeight() - index*60-120+13;
		add.tick();
		if(add.on)
			currentValue += (KeyManager.ctrl ? 5 : 1) * (KeyManager.shift ? 5 : 1);
		sub.tick();
		if(sub.on)
			currentValue -= (KeyManager.ctrl ? 5 : 1) * (KeyManager.shift ? 5 : 1);
		instances.set(currentInstance, currentValue);
		return add.on || sub.on;
	}

	@Override
	public boolean usingUI() {
		return add.hovered || sub.hovered;
	}

	public void render(Graphics2D g) {
		add.render(g);
		sub.render(g);
		Tran.drawOutlinedText(g, sub.x+40.0, sub.y+24, String.valueOf(currentValue), 1.0, Color.black, Color.white);
		Tran.drawOutlinedText(g, sub.x-10-Tran.measureString(name, g.getFont()).x, sub.y+22, name+":", 1.0, Color.black, Color.white);
	}

	public void writeType(StringBuilder content, int index){
		content.append(" ").append("i");
	}

	public void write(StringBuilder content, int index){
		content.append(" ").append(instances.get(index));
	}

	@Override
	public Object get() {
		return currentValue;
	}
}

class DoubleValueSelector extends ValueSelector {
	private final IconButton add, sub;
	private final Slider detail;
	public ArrayList<Double> instances;
	public double currentValue, currentInt;
	public DoubleValueSelector(Handler game, int index, String name) {
		super(game, index, name);
		instances = new ArrayList<>();
		currentValue = 0;
		add = new IconButton(game, game.getCanvasWidth()-32, game.getCanvasHeight()-index*60-120+13, ImageLoader.loadImage("/textures/Gui/+-.png").getSubimage(0, 0, 32, 32));
		sub = new IconButton(game, game.getCanvasWidth()-180, game.getCanvasHeight()-index*60-120+13, ImageLoader.loadImage("/textures/Gui/+-.png").getSubimage(32, 0, 32, 32));
		detail = new Slider(game, 0, 1, 0, false, "Decimal");
	}

	public void addInstance() {
		currentInstance = instances.size();
		instances.add(currentValue);
	}

	public void switchInstance(int index) {
		this.currentInstance = index;
		currentValue = instances.get(index);
	}

	public void deleteCurrentInstance() {
		instances.remove(instances.get(currentInstance)); // Make sure we're removing by index, not object
		this.currentInstance = -1;
		currentValue = 0;
	}
	public boolean tick() {
		add.x = game.getCanvasWidth() - 32;
		add.y = game.getCanvasHeight() - index*60-120+13;
		sub.x = game.getCanvasWidth() - 180;
		sub.y = game.getCanvasHeight() - index*60-120+13;
		detail.tick(game.getCanvasWidth()-136, game.getCanvasHeight()-index*60-120+23);
		add.tick();
		if(add.on)
			currentInt += (KeyManager.ctrl ? 5 : 1) * (KeyManager.shift ? 5 : 1);
		sub.tick();
		if(sub.on)
			currentInt -= (KeyManager.ctrl ? 5 : 1) * (KeyManager.shift ? 5 : 1);
		instances.set(currentInstance, currentValue);
		double pre = currentValue;
		currentValue = currentInt + detail.getWholeValue();
		return pre!=currentValue;
	}

	@Override
	public boolean usingUI() {
		return add.hovered || sub.hovered || detail.hovered;
	}

	public void render(Graphics2D g) {
		add.render(g);
		sub.render(g);
		detail.render(g);
		Tran.drawOutlinedText(g, sub.x+82.0,sub.y+52.0, String.valueOf(currentValue), 1.0, Color.black, Color.white);
		Tran.drawOutlinedText(g, sub.x-10.0-Tran.measureString(name, g.getFont()).x, sub.y+22, name+":", 1.0, Color.black, Color.white);
	}

	public void writeType(StringBuilder content, int index){
		content.append(" ").append("d");
	}

	public void write(StringBuilder content, int index){
		content.append(" ").append(instances.get(index));
	}

	public Object get() {
		return currentValue;
	}
}

class BooleanValueSelector extends ValueSelector {
	private final ToggleButton button;
	public ArrayList<Boolean> instances;
	public boolean currentValue;
	public BooleanValueSelector(Handler game, int index, String name) {
		super(game, index, name);
		instances = new ArrayList<>();
		currentValue = false;
		button = new ToggleButton(game, game.getCanvasWidth()-100, game.getCanvasHeight()-index*60-120+13, "Toggles " + name, name);
	}

	public void addInstance() {
		currentInstance = instances.size();
		instances.add(currentValue);
	}

	public void switchInstance(int index) {
		this.currentInstance = index;
		currentValue = instances.get(index);
	}

	public void deleteCurrentInstance() {
		instances.remove(instances.get(currentInstance)); // Make sure we're removing by index, not object
		currentInstance = -1;
		currentValue = false;
	}
	public boolean tick() {
		button.on = currentValue;
		boolean pre = currentValue;
		button.setX(game.getCanvasWidth() - 100);
		button.setY(game.getCanvasHeight() - index*60-120+13);
		button.tick();
		currentValue = button.on;
		return pre!=currentValue;
	}

	@Override
	public boolean usingUI() {
		return button.hovered;
	}

	public void render(Graphics2D g) {
		button.render(g);
	}

	public void writeType(StringBuilder content, int index){
		content.append(" ").append("b");
	}

	public void write(StringBuilder content, int index){
		content.append(" ").append(instances.get(index));
	}

	public Object get() {
		return currentValue;
	}
}

class StringValueSelector extends ValueSelector {
	private final TextEditor editor;
	public ArrayList<String> instances;
	public String currentValue;
	public StringValueSelector(Handler game, int index, String name) {
		super(game, index, name);
		instances = new ArrayList<>();
		currentValue = "Text";
		editor = new TextEditor(game, game.getCanvasWidth()-110, game.getCanvasHeight()-index*60-120+13, 100, 1, Public.defaultFont);
	}

	public void addInstance() {
		currentInstance = instances.size();
		instances.add(currentValue);
	}

	public void switchInstance(int index) {
		this.currentInstance = index;
		currentValue = instances.get(index);
	}

	public void deleteCurrentInstance() {
		instances.remove(instances.get(currentInstance)); // Make sure we're removing by index, not object
		this.currentInstance = -1;
		currentValue = "Text";
	}
	public boolean tick() {
		editor.setContent(currentValue);
		String pre = currentValue;
		editor.setX(game.getCanvasWidth()-110);
		editor.setY(game.getCanvasHeight() - index*60-120+13);
		editor.tick();
		currentValue = editor.getContent();
		return !Objects.equals(pre, currentValue);
	}

	@Override
	public boolean usingUI() {
		int x = game.getMouse().rMouseX();
		int y = game.getMouse().rMouseY();
		return x >= editor.getX() && y >= editor.getY() && x <= editor.getX() + 100 && y <= editor.getY() + 22;
	}

	public void render(Graphics2D g) {
		Tran.drawOutlinedText(g, editor.getX()-10.0-Tran.measureString(name, g.getFont()).x, editor.getY()+22, name+":", 1.0, Color.black, Color.white);
		editor.render(g);
	}

	public void writeType(StringBuilder content, int index){
		content.append(" ").append("s");
	}

	public void write(StringBuilder content, int index){
		content.append(" ").append(instances.get(index));
	}

	public Object get() {
		return currentValue;
	}
}
