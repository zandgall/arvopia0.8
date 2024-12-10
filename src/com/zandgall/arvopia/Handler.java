package com.zandgall.arvopia;

import com.zandgall.arvopia.entity.*;
import com.zandgall.arvopia.entity.creatures.*;
import com.zandgall.arvopia.entity.creatures.basic.*;
import com.zandgall.arvopia.entity.creatures.npcs.*;
import com.zandgall.arvopia.entity.moveableStatics.*;
import com.zandgall.arvopia.entity.statics.*;
import com.zandgall.arvopia.environment.Environment;
import com.zandgall.arvopia.gfx.GameCamera;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.guis.basic.BasicGui;
import com.zandgall.arvopia.input.KeyManager;
import com.zandgall.arvopia.input.MouseManager;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.utils.ClassLoading;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Utils;
import com.zandgall.arvopia.worlds.World;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.Vector3D;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Math.*;

public class Handler implements Serializable {
	private static final long serialVersionUID = 2673656438928611097L;
	public Log player;
	public Log world_log;
	public Log keyEvent;
	public Log fpsLogger;
	public Log environment_log;
	public Log file_log;
	private Game game;

	public SoundSystem soundSystem;
	public String currentMusic = "empty";

	ArrayList<String> sounds;

	public int width = 720, height = 405;

	private World world;

	public long beginTime;

	public Game getGame() {
		return game;
	}

	public void setCursor(Cursor cursor) {
		game.getDisplay().getFrame().setCursor(cursor);
	}

	public Player getPlayer() {
		return getEntityManager().getPlayer();
	}

	public OptionState options() {
		return (OptionState) game.optionState;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public GameCamera getGameCamera() {
		return game.getGameCamera();
	}

	public double xOffset() {
		return game.getGameCamera().getxOffset();
	}

	public double yOffset() {
		return game.getGameCamera().getyOffset();
	}

	public void loadMods() {
		loadInternalMods();
		File f = new File(Game.prefix + "/Mods");
		if (f.exists() && f.listFiles()!=null)
			for (File s : Objects.requireNonNull(f.listFiles())) {
				System.out.println("Loading mod : " + s.getPath());
				if (s.isDirectory()) {
					loadMod(s.getPath());
				} else if (s.getName().endsWith(".jar")) {
					
					try {
					ClassLoading.moveTmp(s.getPath());
					} catch(Exception ex) {
						System.err.println(ex.getMessage());
					}
					
					Entity.loadJar(s.getPath());
					BasicGui.loadMods(s.getPath(), this);
					Initiator.loadMods(s.getPath());
					ModHandler.loadMods(s.getPath());
					
					if (game.levelMakerState != null)
						game.levelMakerState.setupTileIconButtons();

				}
			}
	}
	
	public ArrayList<EntityEntry> getAdders() {
		ArrayList<EntityEntry> out = new ArrayList<>();
		File f = new File(Game.prefix + "/Mods");
		if (f.exists())
			for (File s : Objects.requireNonNull(f.listFiles())) {
				ArrayList<Class<?>> objects = new ArrayList<>();
				try {
					objects = ClassLoading.getClasses(s.getPath());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				for (Class<?> obj : objects) {
					if (Entity.class.isAssignableFrom(obj))
						out.add(new EntityEntry(obj.asSubclass(Entity.class)));
				}
			}

		return out;
	}
	
	public void loadInternalMods() {
		EntityManager.entityEntries.put("AreaAdder", new EntityEntry(AreaAdder.class, "AreaAdder"));
		EntityManager.entityEntries.put("Bat", new EntityEntry(Bat.class, "Bat"));
		EntityManager.entityEntries.put("Bear", new EntityEntry(Bear.class, "Bear"));
		EntityManager.entityEntries.put("Bee", new EntityEntry(Bee.class, "Bee"));
		EntityManager.entityEntries.put("Bird", new EntityEntry(Bird.class, "Bird"));
		EntityManager.entityEntries.put("Butterfly", new EntityEntry(Butterfly.class, "Butterfly"));
		EntityManager.entityEntries.put("Cannibal", new EntityEntry(Cannibal.class, "Cannibal"));
		EntityManager.entityEntries.put("Cloud", new EntityEntry(Cloud.class, "Cloud"));
		EntityManager.entityEntries.put("Fairy", new EntityEntry(Fairy.class, "Fairy"));
		EntityManager.entityEntries.put("Fawncier", new EntityEntry(Fawncier.class, "Fawncier"));
		EntityManager.entityEntries.put("Flower", new EntityEntry(Flower.class, "Flower"));
		EntityManager.entityEntries.put("Fox", new EntityEntry(Fox.class, "Fox"));
		EntityManager.entityEntries.put("Frizzy", new EntityEntry(Frizzy.class, "Frizzy"));
		EntityManager.entityEntries.put("House", new EntityEntry(House.class, "House"));
		EntityManager.entityEntries.put("Lia", new EntityEntry(Lia.class, "Lia"));
//		EntityManager.entityEntries.put("NPCs", new EntityEntry(NPCs.class, "NPCs"));
		EntityManager.entityEntries.put("Platform", new EntityEntry(Platform.class, "Platform"));
//		EntityManager.entityEntries.put("Player", new EntityAdder(e, Player.class, "Player"));
		EntityManager.entityEntries.put("Shrubbery", new EntityEntry(Shrubbery.class, "Shrubbery"));
		EntityManager.entityEntries.put("Skunk", new EntityEntry(Skunk.class, "Skunk"));
		EntityManager.entityEntries.put("Stone", new EntityEntry(Stone.class, "Stone"));
		EntityManager.entityEntries.put("TileDecor", new EntityEntry(TileDecor.class, "TileDecor"));
		EntityManager.entityEntries.put("Tree", new EntityEntry(Tree.class, "Tree"));
		EntityManager.entityEntries.put("Villager", new EntityEntry(Villager.class, "Villager"));
		EntityManager.entityEntries.put("Wolf", new EntityEntry(Wolf.class, "Wolf"));
	}

	public void loadMods(String directory) {
		loadInternalMods();
		File f = new File(directory);
		if (f.exists())
			for (File s : Objects.requireNonNull(f.listFiles())) {
				System.out.println("Loading mod : " + s.getPath());
				if (s.isDirectory()) {
					loadMod(s.getPath());
				} else if (s.getName().endsWith(".jar")) {
					Entity.loadJar(s.getPath());
					BasicGui.loadMods(s.getPath(), this);
					Initiator.loadMods(s.getPath());

					ArrayList<BufferedImage> l = ClassLoading.getImages(s.getPath());
					ArrayList<String> n = ClassLoading.getImageNames(s.getPath());
					if (l != null && n != null && l.size() > 0) {
						for (BufferedImage b : l) {
							ImageLoader.saveImage(b, Game.prefix + "/Arvopia/tmp/" + n.get(l.indexOf(b)));
						}
					}
				}
			}
	}

	public void loadMod(String directory) {
		File f = new File(Game.prefix + "/Mods");
		if (f.exists())
			for (File s : Objects.requireNonNull(f.listFiles())) {
				if (s.isDirectory()) {
					for (String d : Objects.requireNonNull(s.list())) {
						System.out.println("\tLoading " + d);
						if (d.contains("Creatures")) {
							for (String c : Objects.requireNonNull(new File(directory + "/" + d).list())) {
								System.out.println("Loading creature " + c);
								Creature.loadModCreature(directory + "/" + d + "/" + c, this);
							}
						}
						if (d.contains("NPCs")) {
							for (String c : Objects.requireNonNull(new File(directory + "/" + d).list())) {
								System.out.println("Loading npc " + c);
								NPC.loadMODNPC(directory + "/" + d + "/" + c, this);
							}
						}
					}
				}
			}
	}

	public KeyManager getKeyManager() {
		return game.getKeyManager();
	}

	public MouseManager getMouse() {
		return game.getMouse();
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public World getWorld() {
		return world;
	}

	public Environment getEnvironment() {
		return world.getEnvironment();
	}

	public Color foliageColor(double temperature, double snowiness) {
		int r;
		if(getEnvironment().collevti<4)
			r = (int)(-30*atan(temperature/10-4)+50);
		else r = (int)(200*pow(E, -pow(temperature-40, 2)/220)+52-30*atan(temperature/10 - 4));
		int g = (int)(180/PI*atan(temperature/4-10)+165);
		return new Color((int) Public.range(0, 255, r*(1-snowiness)+255*snowiness), (int)Public.range(0, 255, g*(1-snowiness)+255*snowiness), (int)Public.range(0, 255, 60*(1-snowiness)+255*snowiness));
	}

	public Color foliageColor(double temperature) {
		return foliageColor(temperature, 0);
	}

	public com.zandgall.arvopia.entity.EntityManager getEntityManager() {
		return world.getEntityManager();
	}

	public double getWind(double x, double y) {
		return world.getEnvironment().getWind(x, y);
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Handler(Game game) {
		this.game = game;
		beginTime = System.currentTimeMillis();
		sounds = new ArrayList<>();
	}

	public Handler() {
	}

	public long getGameTime() {
		return System.currentTimeMillis() - beginTime;
	}

	public void log(String string) {
		Game.log.log(string);
	}

	public void init() {
		System.out.println("Starting sound system");
		try {
			SoundSystemConfig.addLibrary(LibraryJavaSound.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
			SoundSystemConfig.setCodec("wav", CodecWav.class);

			//soundSystem = new SoundSystem(LibraryJavaSound.class);

			//soundSystem.setMasterVolume(2000.0f);
		} catch (Exception e) {
			System.err.println("Error when loading Sound System");
			e.printStackTrace();
		}
		System.out.println("set up sound system");

		player = new Log(Game.prefix + "/logs/Player/player.txt", "Player");

		world_log = new Log(Game.prefix + "/logs/World/world.txt", "World");

		keyEvent = new Log(Game.prefix + "/logs/Key Events/keyEvent.txt", "Keys");

		fpsLogger = new Log(Game.prefix + "/logs/FPSLogs/Fps.txt", "Fps");

		environment_log = new Log(Game.prefix + "/logs/Environment/Environment.txt", "Environment");

		file_log = new Log(Game.prefix + "/logs/FileLoading/Files.txt", "Files");
	}

	public void logPlayer(String string) {
		if (game != null && (game.optionState != null) && ((OptionState) game.optionState).getToggle("Extra Debug"))
			player.log(string);
	}

	public void logWorld(String string) {
		if (game != null && (game.optionState != null) && ((OptionState) game.optionState).getToggle("Extra Debug"))
			world_log.logSilent(string);
	}

	public void logWorldSilent(String string) {
		if (game != null && (game.optionState != null) && ((OptionState) game.optionState).getToggle("Extra Debug"))
			world_log.logSilent(string);
	}

	public void logEnvironment(String string) {
		if (game != null && (game.optionState != null) && ((OptionState) game.optionState).getToggle("Extra Debug"))
			environment_log.log(string);
	}

	public void logEnvironmentSilent(String string) {
		if (game != null && (game.optionState != null) && ((OptionState) game.optionState).getToggle("Extra Debug"))
			environment_log.logSilent(string);
	}

	public static int getGamePoints() {
		return Utils.parseInt(FileLoader.readFile(Game.prefix + "/00.arv"))
				+ Utils.parseInt(FileLoader.readFile(Game.prefix + "/02.arv"));
	}

	public void logFiles(String string) {
		if (game != null && (game.optionState != null) && ((OptionState) game.optionState).getToggle("Extra Debug"))
			file_log.logSilent(string);
	}

	public void music(String path, boolean loop) {
		logEnvironment("Music is " + path);
		currentMusic = path;
		try {
			//soundSystem.backgroundMusic("music", new File(path).toURI().toURL(), path, true);
//			soundSystem.backgroundMusic("music", path, loop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void musicNative(String path, boolean loop) {
		logEnvironment("Music is " + path);
		currentMusic = path;
		try {
			//soundSystem.backgroundMusic("music", FileLoader.resourceURL(path), path, true);
//			soundSystem.backgroundMusic("music", path, loop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void quickPlay(String path, boolean loop, int x, int y, int z) {
		logEnvironment("Quick playing " + path);
		try {
			//soundSystem.newStreamingSource(false, path, new File(path).toURI().toURL(), new File(path).getName(), loop,
			//		x, y, z, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
			//soundSystem.play(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addAmbient(String path, String id, boolean loop) {
		logEnvironment("Music is " + id);
		try {
			//soundSystem.backgroundMusic(id, new File(path).toURI().toURL(), new File(path).getName(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play(String identifier) {
		logEnvironment("Playing : " + identifier);
		//soundSystem.play(identifier);
	}

	public void quickPlay(String path, String id, boolean loop, int x, int y, int z) {
		logEnvironment("Quick playing " + id);
		try {
			//soundSystem.newStreamingSource(false, id, new File(path).toURI().toURL(), path, loop, x, y, z,
			//		SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
			//soundSystem.play(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void quickie(String path, String id, boolean loop, int x, int y, int z) {

		logEnvironment("Doing a quickie " + id);

		try {
			//soundSystem.quickPlay(false, new File(path).toURI().toURL(), new File(path).getName(), loop, x, y, z,
			//		SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addSound(String path, String id, boolean loop, int x, int y, int z) {
		if (sounds.contains(id)) {
			logEnvironment("Already has " + id);
			return;
		}
		if (environment_log != null)
			logEnvironment("Adding " + id);
		try {
			//soundSystem.loadSound(new File(path).toURI().toURL(), new File(path).getName());
			//soundSystem.newSource(false, id, new File(path).getName(), loop, x, y, z,
			//		SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
//			soundSystem.newSource(false, id, new File(path).toURI().toURL(), new File(path).getName(), loop, x, y, z, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
			//sounds.add(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addStream(String path, String id, boolean loop, int x, int y, int z) {
		if (sounds.contains(id)) {
			logEnvironment("Already has " + id);
			return;
		}
		if (environment_log != null)
			logEnvironment("Adding " + id);
		try {
			//soundSystem.loadSound(new File(path).toURI().toURL(), new File(path).getName());
			//soundSystem.newStreamingSource(false, id, new File(path).getName(), loop, x, y, z,
			//		SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
//			soundSystem.newSource(false, id, new File(path).toURI().toURL(), new File(path).getName(), loop, x, y, z, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
			//sounds.add(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeSound(String path, String id) {
		try {
			//soundSystem.loadSound(new File(path).toURI().toURL(), new File(path).getName());
			//sounds.remove(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final int SOUND_MULT = 5;

	public void listenPosition(int x, int y, int z) {
		//soundSystem.setListenerPosition(x * SOUND_MULT, y * SOUND_MULT, z * SOUND_MULT);
	}
	
	public Vector3D listenerPos() {
		return new Vector3D(0,0,0);//soundSystem.getListenerData().position;
	}

	public void setPosition(String source_name, double x, double y, double z) {
		//soundSystem.setPosition(source_name, (float) (x * SOUND_MULT), (float) (y * SOUND_MULT), (float) (z * SOUND_MULT+100));
	}
	
	public void putAtListener(String id) {
		//soundSystem.setPosition(id, listenerPos().x, listenerPos().y, listenerPos().z+100);
	}
	
	public void fadeOut(String path, String id, long millis) {
		logEnvironment("Fading " + path);
		if (path == null) {
			//soundSystem.fadeOut(id, null, null, millis);
			return;
		}
		try {
			//soundSystem.fadeOut(id, new File(path).toURI().toURL(), new File(path).getName(), millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void fadeOutIn(String path, String id, long millis) {
		logEnvironment("Fading " + path);
		if (path == null) {
			//soundSystem.fadeOutIn(id, null, null, millis, millis);
			return;
		}
		try {
			//soundSystem.fadeOutIn(id, new File(path).toURI().toURL(), new File(path).getName(), millis, millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopMusic() {
		currentMusic = "empty";
		//soundSystem.stop("music");
	}

	public void fadeOut(String path, long millis) {
		logEnvironment("Fading " + path);
		currentMusic = path;
		if (path == null) {
			//soundSystem.fadeOut("music", null, null, millis);
			return;
		}
		try {
			//soundSystem.fadeOut("music", new File(path).toURI().toURL(), new File(path).getName(), millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fadeOutIn(String path, long millis) {
		logEnvironment("Fading " + path);
		currentMusic = path;
		if (path == null) {
			//soundSystem.fadeOutIn("music", null, null, millis, millis);
			return;
		}
		try {
			//soundSystem.fadeOutIn("music", new File(path).toURI().toURL(), new File(path).getName(), millis, millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getCanvasWidth() {
		return game.getDisplay().getCanvas().getWidth();
	}

	public int getCanvasHeight() {
		return game.getDisplay().getCanvas().getHeight();
	}
}
