package com.zandgall.arvopia.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.BlendedAnimation;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.FileChooser;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Utils;

public class WorldLoaderState extends State {
	public File fworld1;
	public File fworld2;
	public File fworld3;
	public File fworld4;
	public File fworld5;
	public Button level1;
	public Button level2;
	public Button staircase;
	public Button defaultworld;
	public Button loadWorld;
	public Button loadSave;
	public Button back, installMod, installPack;
	int init = 0;

	double scroll;

	BufferedImage flow1, flow2, blend;

	BlendedAnimation aniTest;

	ArrayList<worldfile> worlds;

	boolean saves = false;

	Button generate, customize;

	public WorldLoaderState(Handler handler) {
		super(handler);

		generate = new Button(handler, handler.getWidth() / 2 + 54, 20, 510, 35, "Generates a world", "", 1);
		customize = new Button(handler, handler.getWidth() / 2 + 54, 75, 510, 35, "Switches to the level maker", "", 1);

		flow1 = PublicAssets.flowerFinal[0].getArray()[0];
		flow2 = PublicAssets.flowerFinal[0].getArray()[1];

		loadStuff();

//    blend = Tran.blendImages(flow1, flow2, 0.2f);

//    aniTest = new BlendedAnimation(100, PublicAssets.flowerFinal[0].getArray(), "Flower Test", "Testing ground");

		loadWorld = new Button(handler, 10, 10, 123, 13, "Opens a file browser to select a file", "Open Other");

		loadSave = new Button(handler, 10, 40, 123, 13, "Opens a save file of your choice", "Saves");

		back = new Button(handler, 10, 70, 123, 13, "Takes you back to title screen", "Back");
		
		installMod = new Button(handler, 10, 100, 123, 13, "Installs selected mod", "Install Mod");
		
		installPack = new Button(handler, 10, 130, 123, 13, "Installs selected story pack", "Install Pack");
		
	}
	
	public void loadStuff() {
		fworld1 = new File(Game.prefix + "//Arvopia//World//World 1.arv");
//		if (!fworld1.exists() || fworld1.length() < 1) {
		System.out.println("\t\tDownloading Content: LevelOne");
		fworld1 = new File("Packed Worlds/LevelOne");

		Utils.fileWriter(FileLoader.readFile(fworld1), Game.prefix + "//Arvopia//World//World 1.arv");
		fworld1 = new File(Game.prefix + "//Arvopia//World//World 1.arv");
//		}

		fworld2 = new File(Game.prefix + "//Arvopia//World//World 2.arv");
		if (!fworld2.exists() || fworld2.length() < 1) {
			System.out.println("\t\tDownloading Content: LevelTwo");
			fworld2 = new File("Packed Worlds/LevelTwo");

			Utils.fileWriter(FileLoader.readFile(fworld2), Game.prefix + "//Arvopia//World//World 2.arv");
			fworld2 = new File(Game.prefix + "//Arvopia//World//World 2.arv");
		}

		fworld3 = new File(Game.prefix + "//Arvopia//World//World 3.arv");
		if (!fworld3.exists() || fworld3.length() < 1) {
			System.out.println("\t\tDownloading Content: 0.5Forest");
			fworld3 = new File("Packed Worlds/0.5Forest");

			Utils.fileWriter(FileLoader.readFile(fworld3), Game.prefix + "//Arvopia//World//World 3.arv");
			fworld3 = new File(Game.prefix + "//Arvopia//World//World 3.arv");
		}

		fworld4 = new File("//Arvopia//World//World 4.arv");
		if (!fworld4.exists() || fworld4.length() < 1) {
			System.out.println("\t\tDownloading Content: 0.6World");
			fworld4 = new File("Packed Worlds/0.6World");

			Utils.fileWriter(FileLoader.readFile(fworld4), Game.prefix + "//Arvopia//World//World 4.arv");
			fworld4 = new File(Game.prefix + "//Arvopia//World//World 4.arv");
		}
		
		fworld5 = new File(Game.prefix + "//Arvopia//World//World 1 (2nd edition).arv");
		if (!fworld5.exists() || fworld5.length() < 1) {
			System.out.println("\t\tDownloading Content: World 1 remake");
			fworld5 = new File("Packed Worlds/World 1 remake");

			Utils.fileWriter(FileLoader.readFile(fworld4), Game.prefix + "//Arvopia//World//World 1 (2nd edition).arv");
			fworld5 = new File(Game.prefix + "//Arvopia//World//World 1 (2nd edition).arv");
		}

		// Directory's file paths = directory.list();

		File world = new File(Game.prefix + "/Arvopia/Pack");

		String[] paths = world.list();

		worlds = new ArrayList<worldfile>();

		for (int i = 0; i < paths.length; i++) {
			worlds.add(new worldfile(handler, worlds.size(), new File(Game.prefix + "/Arvopia/Pack/" + paths[i]), "Pack"));

		}

		world = new File(Game.prefix + "/Arvopia/World");

		paths = world.list();

//		worlds.add(new worldfile(handler, 0, new File("Generate new world")));

		for (int i = 0; i < paths.length; i++) {
			worlds.add(new worldfile(handler, worlds.size(), new File(Game.prefix + "/Arvopia/World/" + paths[i])));
		}

		world = new File(Game.prefix + "/Arvopia/Saves");

		paths = world.list();

		for (int i = 0; i < paths.length; i++) {

			saves = true;

			worlds.add(new worldfile(handler, worlds.size(), new File(Game.prefix + "/Arvopia/Saves/" + paths[i]), "Save"));

		}
	}

	public void tick() {

		loadWorld.tick();

		loadSave.tick();

		back.tick();
		
		installMod.tick();
		
		installPack.tick();
		
		if(installMod.on) {
			FileChooser f = new FileChooser();
			
			String path = f.getFile("Home");
			File file = new File(path);
			
			if(file.exists()) {
				try {
					Files.copy(file.toPath(), new File(Game.prefix + "/Arvopia/Mods/"+new File(path).getName()).toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			handler.loadMods(handler.getEntityManager());
			
		}
		
		if(installPack.on) {
			FileChooser f = new FileChooser();
			
			String path = f.getFolder("Home");
			File file = new File(path);
			
			if(file.exists()) {
				FileLoader.copyFolder(path, Game.prefix + "/Arvopia/Pack/"+file.getName());
			}
			
			loadStuff();
			
		}
			

		generate.setY((int) (20 + scroll));
		generate.tick();

		customize.setY((int) (75 + scroll));
		customize.tick();

		if (generate.on)
			State.setState(handler.getGame().generationState);

		if (customize.on)
			State.setState(handler.getGame().levelMakerState);

		if (back.on)
			State.setState(handler.getGame().menuState);

		if (loadWorld.on) {
			handler.getMouse().setClicked(false);
			State.setState(handler.getGame().gameState);
			handler.getGame().gameState.openWorld(true, 0);
		} else if (loadSave.on) {
			handler.getMouse().setClicked(false);
			handler.getMouse().fullLeft = false;
			State.setState(handler.getGame().gameState);
			((GameState) handler.getGame().gameState).openSave();
		}

		if (handler.getMouse().fullLeft && handler.getMouse().rMouseX() > handler.getWidth() - 30) {
			scroll = Public.Map(handler.getMouse().rMouseY(), handler.getHeight() - 10, 10,
					-(worlds.size() * 55 + 240) + handler.getHeight(), 0);
		}
//    	aniTest.tick();

		scroll -= handler.getMouse().getMouseScroll() * 20;

		scroll = Public.range(-(worlds.size() * 55 + 240) + handler.getHeight(), 0, scroll);

		for (worldfile w : worlds) {
			w.tick(scroll);
		}
	}

	public void render(java.awt.Graphics2D g) {
		g.setColor(new Color(120, 225, 255));
		g.fillRect(0, 0, handler.getWidth(), handler.getHeight());

		g.setColor(new Color(100, 100, 100, 100));
		g.fillRoundRect(handler.getWidth() / 2 - 216, 0, 532, handler.getHeight() + 22, 30, 30);
		g.setColor(Color.darkGray);
		g.fillRoundRect(handler.getWidth() / 2 - 206, -11, 532, handler.getHeight() + 22, 30, 30);
		g.setColor(new Color(120, 120, 120));
		g.fillRoundRect(handler.getWidth() / 2 - 205, -10, 530, handler.getHeight() + 20, 30, 30);
		g.setColor(Color.gray);
		g.fillRoundRect(handler.getWidth() / 2 - 200, -5, 520, handler.getHeight() + 10, 30, 30);

		boolean savesYET = false, worldsYET = false;

		g.setFont(Public.defaultBoldFont);
		g.setColor(Color.black);
		g.drawString("--- Story Packs ---",
				handler.getWidth() / 2 + 60 - Tran.measureString("--- Story Packs ---", Public.defaultBoldFont).x / 2,
				(int) (worldfile.defaultoffset - 30 + scroll));

		for (worldfile w : worlds) {
			if (!savesYET && w.type.contains("Save")) {
				g.setColor(Color.black);
				g.setFont(Public.defaultBoldFont);
				g.drawString("--- Saves ---",
						handler.getWidth() / 2 + 60 - Tran.measureString("--- Saves ---", Public.defaultBoldFont).x / 2,
						(int) (w.index * 55 + worldfile.saveoffset - 30 + scroll));
				savesYET = true;
			}
			if (!worldsYET && w.type.contains("World")) {
				g.setColor(Color.black);
				g.setFont(Public.defaultBoldFont);
				g.drawString("--- Worlds ---",
						handler.getWidth() / 2 + 60
								- Tran.measureString("--- Worlds ---", Public.defaultBoldFont).x / 2,
						(int) (w.index * 55 + worldfile.worldoffset - 30 + scroll));
				worldsYET = true;
			}
			w.render(g, scroll);

			if (w.body.on) {
				g.setColor(Color.black);
				g.fillRect(0, 0, handler.getWidth(), handler.getHeight());
				g.setColor(Color.white);
//				g.drawString("Loading " + w.type + "...", 100, 100);
			}
		}

		loadWorld.render(g);
		loadSave.render(g);

		back.render(g);
		
		installMod.render(g);
		installPack.render(g);

		generate.render(g);
		if (generate.hovered)
			g.setColor(Color.white);
		else
			g.setColor(Color.black);
		g.drawString("Generate World", 170, (int) (45 + scroll));

		customize.render(g);
		if (customize.hovered)
			g.setColor(Color.white);
		else
			g.setColor(Color.black);
		g.drawString("Customize World", 170, (int) (100 + scroll));

		// TODO Test and configure BlendedAnimation

//    blend = Tran.blendImages(flow1, flow2, (float) ((Math.sin(System.currentTimeMillis()/100)+1)/4));
//    g.drawImage(aniTest.getFrame(), 100, 190, 200, 250, null);
//    g.drawImage(aniTest.getArray()[0], 100, 130, 50, 75, null);
//    g.drawImage(aniTest.getArray()[1], 150, 130, 50, 75, null);
//    g.drawImage(aniTest.getArray()[2], 200, 130, 50, 75, null);
//    g.drawImage(aniTest.getArray()[3], 250, 130, 50, 75, null);

		g.setColor(Color.gray);
		g.fillRoundRect(handler.getWidth() - 30, 10, 20, handler.getHeight() - 20, 5, 5);
		g.setColor(Color.darkGray);

		double max = -(worlds.size() * 55 + 240) + handler.getHeight();

		g.fillRoundRect(handler.getWidth() - 30,
				(int) (10 + ((handler.getHeight() - 20 - ((handler.getHeight() - 20) / worlds.size()))) * scroll / max),
				20, ((handler.getHeight() - 20) / worlds.size()), 5, 5);

	}

	public void init() {
	}

	@Override
	public void renderGUI(Graphics2D g2d) {
		
	}
}

class worldfile {

	public static final int defaultoffset = 185, worldoffset = 185, saveoffset = 200;

	Handler handler;

	int index;
	File file;

//	BufferedImage thumbnail;

	String type = "World", version = "0.6";
	int offset = 185;

	int length = 0;

	Button body;

	Date modified;

	public worldfile(Handler handler, int index, File file) {
		this.index = index;
		this.file = file;

		this.handler = handler;

		length = (int) (file.length() / 1000);

		if (file.getPath() != "Generate new world") {

			modified = new Date(file.lastModified());
			if (file.isDirectory()) {
				String[] s = FileLoader.readFile(new File(file.getAbsolutePath() + "/World.arv")).split("\\s+");
				type = s[0];
				version = s[1];

				if (type.contains("Pack"))
					offset = defaultoffset;
				else if (type.contains("World"))
					offset = worldoffset;
				else if (type.contains("Save"))
					offset = saveoffset;
				else {
					type = "Unknown";
					version = "???";
				}
			} else {
				String[] s = FileLoader.readFile(file).split("\\s+");
				type = s[0];
				version = s[1];

				if (type.contains("Pack"))
					offset = defaultoffset;
				else if (type.contains("World"))
					offset = worldoffset;
				else if (type.contains("Save"))
					offset = saveoffset;
				else {
					type = "Unknown";
					version = "???";
				}
			}
		}
		body = new Button(handler, handler.getWidth() / 2 + 54, index * 55 + offset - 25, 510, 35,
				"Loads the displayed world", "", 1);
	}

	public worldfile(Handler handler, int index, File file, String type) {
		this(handler, index, file);

		length = (int) (file.length() / 1000);

		if (type.contains("Pack")) {
			offset = defaultoffset;
			length = (int) (new File(file.getPath() + "/world.arv").length() / 1000);
		} else if (type.contains("World"))
			offset = worldoffset;
		else if (type.contains("Save"))
			offset = saveoffset;
		else {
			type = "Unknown";
			version = "???";
		}

		this.type = type;
		body = new Button(handler, handler.getWidth() / 2 + 54, index * 55 + offset - 25, 505, 35,
				"Loads the displayed world", "", 1);
	}

	public void tick(double scroll) {

		body.setY((int) (index * 55 + offset - 25 + scroll));

		body.tick();

		if (body.on) {
			State.setState(handler.getGame().gameState);
			handler.fadeOut(null, 2000);

			handler.getMouse().setClicked(false);
			handler.getMouse().fullLeft = false;

			if (file.getPath() == "Generate new world") {
				((GameState) handler.getGame().gameState).loadWorld("generation");
				return;
			}

			if (type.contains("World") || type.contains("Pack"))
				((GameState) handler.getGame().gameState).loadWorld(file.getAbsolutePath());
			else if (type.contains("Save"))
				((GameState) handler.getGame().gameState).openSave(file.getAbsolutePath());
			file.setLastModified(new Date().getTime());
			
//			if(type.contains("Pack"))
//				handler.loadMods(file.getParent()+"/mods", handler.getWorld().getEntityManager());
		}
	}

	public void render(Graphics2D g, double scroll) {
		body.render(g);

		if (body.hovered)
			g.setColor(Color.white);
		else
			g.setColor(Color.black);

		g.setFont(Public.defaultBoldFont.deriveFont(20.0f));

		g.drawString(file.getName().replaceAll(".arv", ""), 170, (int) (index * 55 + offset + scroll));
		g.drawString(length + "KB", 670 - g.getFontMetrics().stringWidth(length + "KB"),
				(int) (index * 55 + offset + scroll));
		g.setFont(Public.defaultBoldFont);
		g.drawString("Last opened: " + modified, 670 - g.getFontMetrics().stringWidth("Last opened: " + modified),
				(int) (index * 55 + offset + 15 + scroll));
		g.drawString("Type: " + type + ", Version: " + version, 170, (int) (index * 55 + offset + 15 + scroll));
	}

}
