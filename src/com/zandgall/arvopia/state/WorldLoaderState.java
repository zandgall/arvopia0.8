package com.zandgall.arvopia.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.FileChooser;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Utils;

public class WorldLoaderState extends State {
	private final Button loadOtherWorld, loadOtherSave, back, installMod, installPack;


	private final Button generate, customize;
	ArrayList<world_file> worlds;

	boolean saves = false;
	boolean packs = false;
	double scroll;

	public WorldLoaderState(Handler handler) {
		super(handler);

		generate = new Button(handler, handler.getWidth() / 2 + 54, 20, 510, 35, "Generates a world", "", 1);
		customize = new Button(handler, handler.getWidth() / 2 + 54, 75, 510, 35, "Switches to the level maker", "", 1);

		// Get default worlds
		File world_file = new File(Game.prefix + "/World/World 1.arv");
		handler.getGame().initMessage("Downloading Content: LevelOne");
		if (!world_file.exists())
			Utils.fileWriter(FileLoader.readFile("Packed Worlds/LevelOne"), world_file.getPath());

		world_file = new File(Game.prefix + "/World/World 2.arv");
		handler.getGame().initMessage("Downloading Content: LevelTwo");
		if (!world_file.exists())
			Utils.fileWriter(FileLoader.readFile("Packed Worlds/LevelTwo"), world_file.getPath());

		world_file = new File(Game.prefix + "/World/World 3.arv");
		handler.getGame().initMessage("Downloading Content: 0.5Forest");
		if (!world_file.exists())
			Utils.fileWriter(FileLoader.readFile("Packed Worlds/0.5Forest"), world_file.getPath());

		world_file = new File(Game.prefix + "/World/World 4.arv");
		handler.getGame().initMessage("Downloading Content: 0.6World");
		if (!world_file.exists())
			Utils.fileWriter(FileLoader.readFile("Packed Worlds/0.6World"), world_file.getPath());

		world_file = new File(Game.prefix + "/World/World 1 (2nd edition).arv");
		handler.getGame().initMessage("Downloading Content: World 1 remake");
		if (!world_file.exists())
			Utils.fileWriter(FileLoader.readFile("Packed Worlds/World 1 remake"), world_file.getPath());

		loadStuff();

		loadOtherWorld = new Button(handler, 10, 10, 123, 20, "Opens a file browser to select a file", "Choose World");
		loadOtherSave = new Button(handler, 10, 50, 123, 20, "Opens a save file of your choice", "Choose Save");
		installPack = new Button(handler, 10, 90, 123, 20, "Installs selected story pack", "Install Pack");
		installMod = new Button(handler, 10, 130, 123, 20, "Installs selected mod", "Install Mod");
		back = new Button(handler, 10, handler.height-40, 123, 20, "Takes you back to title screen", "Back");

	}
	
	public void loadStuff() {

		// Directory's file paths = directory.list();
		worlds = new ArrayList<>();

		// Load and add pack worlds
		File world = new File(Game.prefix + "/Pack");
		String[] paths = world.list();
		if(paths!=null && paths.length > 0) {
			packs = true;
			for (String path : paths)
				worlds.add(new world_file(handler, worlds.size(), new File(Game.prefix + "/Pack/" + path), "Pack"));
		}

		// Load and add default worlds
		world = new File(Game.prefix + "/World");
		paths = world.list();
		if(paths!=null)
			for (String path : paths)
				worlds.add(new world_file(handler, worlds.size(), new File(Game.prefix + "/World/" + path)));

		// Load and add save worlds
		world = new File(Game.prefix + "/Saves");
		paths = world.list();
		if(paths!=null && paths.length > 0) {
			saves = true;
			for (String path : paths)
				worlds.add(new world_file(handler, worlds.size(), new File(Game.prefix + "/Saves/" + path), "Save"));
		}
	}

	public void tick() {

		loadOtherWorld.tick();
		loadOtherSave.tick();
		back.tick();
		installMod.tick();
		installPack.tick();

		if(installMod.on) {
			FileChooser f = new FileChooser();
			
			String path = f.getFile("Home");
			File file = new File(path);
			
			if(file.exists()) {
				try {
					Files.copy(file.toPath(), new File(Game.prefix + "/Mods/"+new File(path).getName()).toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			handler.loadMods();
			return;
		} else if(installPack.on) {
			FileChooser f = new FileChooser();
			
			String path = f.getFolder("Home");
			File file = new File(path);
			
			if(file.exists())
				FileLoader.copyFolder(path, Game.prefix + "/Pack/"+file.getName());
			
			loadStuff();
			return;
		} else if (back.on) {
			State.setState(handler.getGame().menuState);
			return;
		} else if (loadOtherWorld.on) {
			handler.getMouse().setLeftClicked(false);
			State.setState(handler.getGame().gameState);
			handler.getGame().gameState.openWorld(true, 0);
			return;
		} else if (loadOtherSave.on) {
			handler.getMouse().setLeftClicked(false);
			handler.getMouse().fullLeft = false;
			State.setState(handler.getGame().gameState);
			handler.getGame().gameState.openSave();
			return;
		}

		if (handler.getMouse().fullLeft && handler.getMouse().rMouseX() > handler.getWidth() - 30) {
			scroll -= handler.getMouse().rMouseY() - handler.getMouse().rPMouseY();
		}

		scroll -= handler.getMouse().getMouseScroll() * 10;
		scroll = Public.range(handler.getHeight()-(worlds.size() * 55 + 240), 0, scroll);

		generate.setY((int) (20 + scroll));
		generate.tick();

		customize.setY((int) (75 + scroll));
		customize.tick();

		if (generate.on)
			State.setState(handler.getGame().generationState);

		if (customize.on)
			State.setState(handler.getGame().levelMakerState);

		for (world_file w : worlds)
			w.tick(scroll);
	}

	public void render(Graphics2D g) {
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
		if(packs)
			g.drawString("--- Story Packs ---",
					handler.getWidth() / 2 + 60 - Tran.measureString("--- Story Packs ---", Public.defaultBoldFont).x / 2,
					(int) (world_file.default_offset - 30 + scroll));

		for (world_file w : worlds) {
			if (!savesYET && w.type.contains("Save")) {
				g.setColor(Color.black);
				g.setFont(Public.defaultBoldFont);
				g.drawString("--- Saves ---",
						handler.getWidth() / 2 + 60 - Tran.measureString("--- Saves ---", Public.defaultBoldFont).x / 2,
						(int) (w.index * 55 + world_file.save_offset - 30 + scroll));
				savesYET = true;
			}
			if (!worldsYET && w.type.contains("World")) {
				g.setColor(Color.black);
				g.setFont(Public.defaultBoldFont);
				g.drawString("--- Worlds ---",
						handler.getWidth() / 2 + 60
								- Tran.measureString("--- Worlds ---", Public.defaultBoldFont).x / 2,
						(int) (w.index * 55 + world_file.world_offset - 30 + scroll));
				worldsYET = true;
			}
			w.render(g, scroll);

			if (w.body.on) {
				handler.getGame().initMessage("Loading...");
			}
		}

		loadOtherWorld.render(g);
		loadOtherSave.render(g);

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
		g.setColor(Color.gray);
		g.fillRoundRect(handler.getWidth() - 30, 10, 20, handler.getHeight() - 20, 5, 5);
		g.setColor(Color.darkGray);

		double max = -(worlds.size() * 55 + 240) + handler.getHeight();
		g.fillRoundRect(handler.getWidth()-30, (int) (10-scroll), 20, (int) (handler.getHeight()-20 + max), 5, 5);
	}

	public void init() {
	}

	@Override
	public void renderGUI(Graphics2D g2d) {
		
	}
}

class world_file {

	public static final int default_offset = 185, world_offset = 185, save_offset = 200;

	Handler handler;

	int index;
	File file;

//	BufferedImage thumbnail;

	String type = "World", version = "0.6";
	int offset = 185;

	int length;

	Button body;

	Date modified;

	public world_file(Handler handler, int index, File file) {
		this.index = index;
		this.file = file;

		this.handler = handler;

		length = (int) (file.length() / 1000);

		if (!file.getPath().equals("Generate new world")) {

			modified = new Date(file.lastModified());
			if (file.isDirectory()) {
				String[] s = FileLoader.readFile(new File(file.getAbsolutePath() + "/World.arv")).split("\\s+");
				type = s[0];
				version = s[1];

				if (type.contains("Pack"))
					offset = default_offset;
				else if (type.contains("World"))
					offset = world_offset;
				else if (type.contains("Save"))
					offset = save_offset;
				else {
					type = "Unknown";
					version = "???";
				}
			} else {
				String[] s = FileLoader.readFile(file).split("\\s+");
				type = s[0];
				version = s[1];

				if (type.contains("Pack"))
					offset = default_offset;
				else if (type.contains("World"))
					offset = world_offset;
				else if (type.contains("Save"))
					offset = save_offset;
				else {
					type = "Unknown";
					version = "???";
				}
			}
		}
		body = new Button(handler, handler.getWidth() / 2 + 54, index * 55 + offset - 25, 510, 35,
				"Loads the displayed world", "", 1);
	}

	public world_file(Handler handler, int index, File file, String type) {
		this(handler, index, file);

		length = (int) (file.length() / 1000);

		if (type.contains("Pack")) {
			offset = default_offset;
			length = (int) (new File(file.getPath() + "/World.arv").length() / 1000);
		} else if (type.contains("World"))
			offset = world_offset;
		else if (type.contains("Save"))
			offset = save_offset;
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
			handler.fadeOut(null, 2000);

			handler.getMouse().setLeftClicked(false);
			handler.getMouse().fullLeft = false;

			if (file.getPath().equals("Generate new world")) {
				handler.getGame().gameState.loadWorld("generation");
				return;
			}

			if(!file.setLastModified(new Date().getTime()))
				System.err.println("Error: Unable to set last Modification Time for file " + file.getPath());
			if (type.contains("World") || type.contains("Pack"))
				handler.getGame().gameState.loadWorld(file.getAbsolutePath());
			else if (type.contains("Save"))
				handler.getGame().gameState.openSave(file.getAbsolutePath());
			
//			if(type.contains("Pack"))
//				handler.loadMods(file.getParent()+"/mods", handler.getWorld().getEntityManager());
			State.setState(handler.getGame().gameState);
			body.on = false;
			body.locked = true;
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
