package com.zandgall.arvopia;

import com.zandgall.arvopia.display.Display;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.entity.EntityAdder;
import com.zandgall.arvopia.entity.EntityManager;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.entity.creatures.basic.Bird;
import com.zandgall.arvopia.entity.creatures.npcs.NPC;
import com.zandgall.arvopia.enviornment.Enviornment;
import com.zandgall.arvopia.gfx.GameCamera;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.guis.basic.BasicGui;
import com.zandgall.arvopia.levelmaker.LevelMakerState;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.utils.ClassLoading;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Utils;
import com.zandgall.arvopia.worlds.World;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.Vector3D;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Handler implements Serializable {
	private static final long serialVersionUID = 2673656438928611097L;
	public Log player;
	public Log worldl;
	public Log keyEvent;
	public Log fpsLogger;
	public Log enviornmentl;
	public Log filelogger;
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

	public void loadMods(EntityManager e) {
		loadInternalMods(e);
		File f = new File("C:\\Arvopia\\Mods");
		if (f.exists())
			for (File s : f.listFiles()) {
				System.out.println("Loading mod : " + s.getPath());
				if (s.isDirectory()) {
					loadMod(s.getPath(), e);
				} else if (s.getName().endsWith(".jar")) {
					
					try {
					ClassLoading.moveTmp(s.getPath());
					} catch(Exception ex) {
						System.err.println(ex.getMessage());
					}
					
					Entity.loadJar(s.getPath(), this, e);
					BasicGui.loadMods(s.getPath(), this);
					Initiator.loadMods(s.getPath());
					ModHandler.loadMods(s.getPath());
					
					if (game.levelMakerState != null)
						((LevelMakerState) game.levelMakerState).setupTileIconButtons();

				}
			}
	}
	
	public ArrayList<EntityAdder> getAdders() {
		ArrayList<EntityAdder> out = new ArrayList<EntityAdder>();
		File f = new File("C:\\Arvopia\\Mods");
		if (f.exists())
			for (File s : f.listFiles()) {
				ArrayList<Class<?>> objects = new ArrayList<Class<?>>();
				try {
					objects = ClassLoading.getClasses(s.getPath());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				for (Class<?> obj : objects) {
					if(Entity.class.isAssignableFrom(obj)) {
						out.add(new EntityAdder(null, (Class<? extends Entity>) obj));
					}
				}
			}
		
		return out;
	}
	
	public void loadInternalMods(EntityManager e) {
		e.adders.add(new EntityAdder(e, Bird.class, "Bird")); 
	}

	public void loadMods(String directory, EntityManager e) {
		loadInternalMods(e);
		File f = new File(directory);
		if (f.exists())
			for (File s : f.listFiles()) {
				System.out.println("Loading mod : " + s.getPath());
				if (s.isDirectory()) {
					loadMod(s.getPath(), e);
				} else if (s.getName().endsWith(".jar")) {
					Entity.loadJar(s.getPath(), this, e);
					BasicGui.loadMods(s.getPath(), this);
					Initiator.loadMods(s.getPath());

					ArrayList<BufferedImage> l = ClassLoading.getImages(s.getPath());
					ArrayList<String> n = ClassLoading.getImageNames(s.getPath());
					if (l != null && l.size() > 0) {
						for (BufferedImage b : l) {
							ImageLoader.saveImage(b, "C:\\Arvopia\\tmp\\" + n.get(l.indexOf(b)));
						}
					}
				}
			}
	}

	public void loadMod(String directory, EntityManager e) {
		File f = new File("C:\\Arvopia\\Mods");
		if (f.exists())
			for (File s : f.listFiles()) {
				if (s.isDirectory()) {
					for (String d : s.list()) {
						System.out.println("\tLoading " + d);
						if (d.contains("Creatures")) {
							for (String c : new File(directory + "\\" + d).list()) {
								System.out.println("Loading creature " + c);
								Creature.loadModCreature(directory + "\\" + d + "\\" + c, this, e);
							}
						}
						if (d.contains("NPCs")) {
							for (String c : new File(directory + "\\" + d).list()) {
								System.out.println("Loading npc " + c);
								NPC.loadMODNPC(directory + "\\" + d + "\\" + c, this, e);
							}
						}
					}
				}
			}
	}

	public com.zandgall.arvopia.input.KeyManager getKeyManager() {
		return game.getKeyManager();
	}

	public com.zandgall.arvopia.input.MouseManager getMouse() {
		return game.getMouse();
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public World getWorld() {
		return world;
	}

	public Enviornment getEnviornment() {
		return world.getEnviornment();
	}

	public com.zandgall.arvopia.entity.EntityManager getEntityManager() {
		return world.getEntityManager();
	}

	public double getWind() {
		return world.getEnviornment().getWind();
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Handler(Game game) {
		this.game = game;
		beginTime = System.currentTimeMillis();
		sounds = new ArrayList<String>();
	}

	public Handler() {
	}

	public long getGameTime() {
		return System.currentTimeMillis() - beginTime;
	}

	public void log(String string) {
		Game.log.log(string);
	}

	public Display display() {
		return game.getDisplay();
	}

	public double getVolume() {
		return OptionState.msVolume*100;
	}

	public void init() {
		System.out.println("Starting sound system");
		try {
			SoundSystemConfig.addLibrary(LibraryJavaSound.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
			SoundSystemConfig.setCodec("wav", CodecWav.class);

//			SoundSystemConfig.setSoundFilesPackage("Sounds");
//			System.out.println(SoundSystemConfig.getSoundFilesPackage());

			soundSystem = new SoundSystem(LibraryJavaSound.class);

			soundSystem.setMasterVolume(2000.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("set up sound system");

		player = new Log("C:\\Arvopia\\logs\\Player\\player.txt", "Player");

		worldl = new Log("C:\\Arvopia\\logs\\World\\world.txt", "World");

		keyEvent = new Log("C:\\Arvopia\\logs\\Key Events\\keyEvent.txt", "Keys");

		fpsLogger = new Log("C:\\Arvopia\\logs\\FPSLogs\\Fps.txt", "Fps");

		enviornmentl = new Log("C:\\Arvopia\\logs\\Enviornment\\Enviornment.txt", "Enviornment");

		filelogger = new Log("C:\\Arvopia\\logs\\FileLoading\\Files.txt", "Files");
	}

	public void logPlayer(String string) {
		if (game != null && (game.optionState != null) && ((OptionState) game.optionState).getToggle("Extra Debug"))
			player.log(string);
	}

	public void logWorld(String string) {
		if (game != null && (game.optionState != null) && ((OptionState) game.optionState).getToggle("Extra Debug"))
			worldl.logSilent(string);
	}

	public void logKeys(String string) {
		keyEvent.log(string);
	}

	public void logWorldSilent(String string) {
		if (game != null && (game.optionState != null) && ((OptionState) game.optionState).getToggle("Extra Debug"))
			worldl.logSilent(string);
	}

	public void logEnviornment(String string) {
		if (game != null && (game.optionState != null) && ((OptionState) game.optionState).getToggle("Extra Debug"))
			enviornmentl.log(string);
	}

	public void logEnviornmentSilent(String string) {
		if (game != null && (game.optionState != null) && ((OptionState) game.optionState).getToggle("Extra Debug"))
			enviornmentl.logSilent(string);
	}

	public static int getGamePoints() {
		return Utils.parseInt(FileLoader.readFile("C:\\Arvopia\\00.arv"))
				+ Utils.parseInt(FileLoader.readFile("C:\\Arvopia\\02.arv"));
	}

	public void logSilent(String message) {
		if (game != null && (game.optionState != null) && ((OptionState) game.optionState).getToggle("Extra Debug"))
			Game.log.logSilent(message);
	}

	public void saveFps(int fps) {
//		if (((OptionState) game.optionState).extraDebug.on)
		fpsLogger.out("FPS: " + fps);
	}

	public void logFiles(String string) {
		if (game != null && (game.optionState != null) && ((OptionState) game.optionState).getToggle("Extra Debug"))
			filelogger.logSilent(string);
	}

	public void logFilesSilent(String string) {
		if (game != null && (game.optionState != null) && ((OptionState) game.optionState).getToggle("Extra Debug"))
			filelogger.logSilent(string);
	}

	public void music(String path, boolean loop) {
		logEnviornment("Music is " + path);
		currentMusic = path;
		try {
			soundSystem.backgroundMusic("music", new File(path).toURI().toURL(), path, true);
//			soundSystem.backgroundMusic("musi", path, loop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void musicNative(String path, boolean loop) {
		logEnviornment("Music is " + path);
		currentMusic = path;
		try {
			soundSystem.backgroundMusic("music", FileLoader.resourceURL(path), path, true);
//			soundSystem.backgroundMusic("musi", path, loop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void quickPlay(String path, boolean loop, int x, int y, int z) {
		logEnviornment("Quick playing " + path);
		try {
			soundSystem.newStreamingSource(false, path, new File(path).toURI().toURL(), new File(path).getName(), loop,
					x, y, z, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
			soundSystem.play(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addAmbient(String path, String id, boolean loop) {
		logEnviornment("Music is " + id);
		try {
			soundSystem.backgroundMusic(id, new File(path).toURI().toURL(), new File(path).getName(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play(String identifier) {
		logEnviornment("Playing : " + identifier);
		soundSystem.play(identifier);
	}

	public void quickPlay(String path, String id, boolean loop, int x, int y, int z) {
		logEnviornment("Quick playing " + id);
		try {
			soundSystem.newStreamingSource(false, id, new File(path).toURI().toURL(), path, loop, x, y, z,
					SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
			soundSystem.play(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void quickie(String path, String id, boolean loop, int x, int y, int z) {

		logEnviornment("Doin a quickie " + id);

		try {
			soundSystem.quickPlay(false, new File(path).toURI().toURL(), new File(path).getName(), loop, x, y, z,
					SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addSound(String path, String id, boolean loop, int x, int y, int z) {
		if (sounds.contains(id)) {
			logEnviornment("Already has " + id);
			return;
		}
		if (enviornmentl != null)
			logEnviornment("Adding " + id);
		try {
			soundSystem.loadSound(new File(path).toURI().toURL(), new File(path).getName());
			soundSystem.newSource(false, id, new File(path).getName(), loop, x, y, z,
					SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
//			soundSystem.newSource(false, id, new File(path).toURI().toURL(), new File(path).getName(), loop, x, y, z, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
			sounds.add(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addStream(String path, String id, boolean loop, int x, int y, int z) {
		if (sounds.contains(id)) {
			logEnviornment("Already has " + id);
			return;
		}
		if (enviornmentl != null)
			logEnviornment("Adding " + id);
		try {
			soundSystem.loadSound(new File(path).toURI().toURL(), new File(path).getName());
			soundSystem.newStreamingSource(false, id, new File(path).getName(), loop, x, y, z,
					SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
//			soundSystem.newSource(false, id, new File(path).toURI().toURL(), new File(path).getName(), loop, x, y, z, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
			sounds.add(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeSound(String path, String id) {
		try {
			soundSystem.loadSound(new File(path).toURI().toURL(), new File(path).getName());
			sounds.remove(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final int SOUNDMULT = 5;

	public void listenPosition(int x, int y, int z) {
		soundSystem.setListenerPosition(x * SOUNDMULT, y * SOUNDMULT, z * SOUNDMULT);
	}
	
	public Vector3D listenerPos() {
		return soundSystem.getListenerData().position;
	}

	public void setPosition(String sourcename, double x, double y, double z) {
		soundSystem.setPosition(sourcename, (float) (x * SOUNDMULT), (float) (y * SOUNDMULT), (float) (z * SOUNDMULT+100));
	}
	
	public void putAtListener(String id) {
		soundSystem.setPosition(id, listenerPos().x, listenerPos().y, listenerPos().z+100);
	}
	
	public void fadeOut(String path, String id, long millis) {
		logEnviornment("Fading " + path);
		if (path == null) {
			soundSystem.fadeOut(id, null, null, millis);
			return;
		}
		try {
			soundSystem.fadeOut(id, new File(path).toURI().toURL(), new File(path).getName(), millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void fadeOutIn(String path, String id, long millis) {
		logEnviornment("Fading " + path);
		if (path == null) {
			soundSystem.fadeOutIn(id, null, null, millis, millis);
			return;
		}
		try {
			soundSystem.fadeOutIn(id, new File(path).toURI().toURL(), new File(path).getName(), millis, millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopMusic() {
		currentMusic = "empty";
		soundSystem.stop("music");
	}

	public void fadeOut(String path, long millis) {
		logEnviornment("Fading " + path);
		currentMusic = path;
		if (path == null) {
			soundSystem.fadeOut("music", null, null, millis);
			return;
		}
		try {
			soundSystem.fadeOut("music", new File(path).toURI().toURL(), new File(path).getName(), millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fadeOutIn(String path, long millis) {
		logEnviornment("Fading " + path);
		currentMusic = path;
		if (path == null) {
			soundSystem.fadeOutIn("music", null, null, millis, millis);
			return;
		}
		try {
			soundSystem.fadeOutIn("music", new File(path).toURI().toURL(), new File(path).getName(), millis, millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
