package com.zandgall.arvopia.state;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Initiator;
import com.zandgall.arvopia.guis.Gui;
import com.zandgall.arvopia.guis.PlayerGui;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.quests.AchievementManager;
import com.zandgall.arvopia.utils.*;
import com.zandgall.arvopia.worlds.World;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

public class GameState extends State {
	private World world;

	public long worldt = 1;
	public long uit = 1;
	public long songst = 1;
	public long othert = 1;

	public long worldtr = 1;
	public long uir = 1;
	public long songsr = 1;
	public long otherr = 1;

	public Gui u;
	private final String[] levels = { "/Worlds/LevelOne", "/Worlds/LevelTwo", "/Worlds/0.5Forest", "/Worlds/world1.txt",
			"/Worlds/world2.txt", "/Worlds/Staircase.txt" };

	public boolean isSongStopped() {
		return songStopped;
	}

	private boolean songStopped;
	private boolean loadingWorld;

	ArrayList<ConditionalMusic> music;

	public GameState(Handler handler) {
		super(handler);

		songStopped = true;

		com.zandgall.arvopia.gfx.PublicAssets.init();

		world = new World(handler);
		world.finish(false);
		handler.setWorld(world);

		u = new PlayerGui(handler);

		music = new ArrayList<ConditionalMusic>();
		music.add(new ConditionalMusic(handler, new String[] { "Songs/Thunderstorm.ogg" }) {
			public boolean playable(Handler g) {
				return g.getEnvironment().precipitation && g.getEnvironment().stormy;
			}
		});
		music.add(new ConditionalMusic(handler, new String[] { "Songs/Playtime.ogg", "Songs/SweetGuitar.ogg" }) {
			public boolean playable(Handler g) {
				return g.getEnvironment().getHours() > 5 && g.getEnvironment().getHours() < 20
						&& !g.getEnvironment().precipitation && !g.getEnvironment().stormy;
			}
		});
		music.add(new ConditionalMusic(handler,
				new String[] { "Songs/StarsInTheNight.ogg", "Songs/SpaceIsAMirror.ogg" }) {
			public boolean playable(Handler g) {
				return (g.getEnvironment().getHours() <= 5 || g.getEnvironment().getHours() >= 20)
						&& !g.getEnvironment().precipitation && !g.getEnvironment().stormy;
			}
		});
		music.add(new ConditionalMusic(handler, new String[] { "Songs/Raindrops.ogg" }) {
			public boolean playable(Handler g) {
				return g.getEnvironment().precipitation && !g.getEnvironment().stormy;
			}
		});

	}

	public void openWorld(boolean open, int index) {
		world.reset();
		loadingWorld = true;
		ready = false;

		if (open) {
			FileChooser fileGet = new FileChooser();

			String i = fileGet.getFile(Game.prefix);

			if (i.length() > 0) {
				loadWorld(i);
			} else {
				State.setState(getPrev());
				handler.log("Couldn't load the world specified");
			}
		} else {
			loadWorld(levels[index]);
		}
	}

	public void setWorld(World w) {
		this.world = w;
		Initiator.aworldInit(handler, w);
	}

	public void saveWorld() {

		if (world.save == "") {
			FileChooser fileSet = new FileChooser();

			String i = fileSet.saveFile(Game.prefix + "/Saves");

			if(!new File(i).isDirectory())
				world.saveWorld(i);
		} else {
			world.saveWorld(world.save);
		}
	}

	public void openSave() {
		FileChooser fileGet = new FileChooser();

		String i = fileGet.getFile(Game.prefix + "/Saves");

		if (i.length() > 0) {
			loadWorld(i);
		} else {
			State.setState(getPrev());
			handler.log("Couldn't load the world specified");
		}
	}

	public void openSave(String path) {
		world = new World(handler, "/Worlds/DefaultWorld");
		world.finish(false);
		world.loadSave(path);
		
		Initiator.aworldInit(handler, world);

		world.percentDone = 0.0D;
		loadingWorld = true;
		ready = false;

		handler.getGame().stable = false;

		String[] t = FileLoader.readFile(path + "/Player.arv").split("\\s+");

		world.getEntityManager().getPlayer().setX(Utils.parseDouble(t[0]));
		world.getEntityManager().getPlayer().setY(Utils.parseDouble(t[1]));

	}

	public void loadWorld(String path) {
		handler.log("World: " + path + " loaded");
		world = new World(handler, path); 
		world.percentDone = 0.0D;
		loadingWorld = true;
		ready = false;

		if(world.getType()=="Pack")
			handler.loadMod(path + "/mods");
		Initiator.aworldInit(handler, world);

		handler.getGame().stable = false;
	}

	public void generateWorld(long seed, ArrayList<Slider> sliders) {
		handler.getGame().initMessage("Generating world...");
		world = new World(handler, seed, sliders);
		world.percentDone = 0.0D;
		loadingWorld = true;
		ready = false;
		
		Initiator.aworldInit(handler, world);

		handler.getGame().stable = false;
	}

	public void tick() {
		if (!loadingWorld) {
			long pre = System.nanoTime();
			world.tick();
			if(Public.chance(1))
				world.spawnEntities();
			worldt = (System.nanoTime() - pre);
		}
		if (!ready) {
			System.out.println("Finishing");
			world.finish(true);
//			handler.getGame().forceRender();
		}
		long pre = System.nanoTime();
		u.tick();
		uit = (System.nanoTime() - pre);

		pre = System.nanoTime();

		songst = (System.nanoTime() - pre);
		pre = System.nanoTime();

		if (world.getEntityManager().getPlayer().items.size() == 1) {
			Achievement.award(Achievement.firstcraft);
		}

		String h = world.getEntityManager().getPlayer().has;

		if ((h.contains("-1")) && (h.contains("-2")) && (h.contains("-3")) && (h.contains("-4"))
				&& (h.contains("-5"))) {
			Achievement.award(Achievement.smithy);
		}
//		if (!com.zandgall.arvopia.utils.FileLoader.readFile("C:/Arvopia/01.arv", false).contains("Hoarder")) {
		if(!AchievementManager.al.contains(Achievement.get("Hoarder")))
			for (String i : world.getEntityManager().getPlayer().items.keySet())
				if (world.getEntityManager().getPlayer().items.get(i).amount >= 100)
					Achievement.award(Achievement.hoarder);
//		}
		othert = (System.nanoTime() - pre);
		
		for (ConditionalMusic c : music) {
			c.tick(music);
		}
	}

	boolean preWorked = false;
	boolean ready = false;
	public boolean resettingFps = true;

	int loadingPhase = 0;

	public void setLoadingPhase(int loadingPhase) {
		this.loadingPhase = loadingPhase;
	}

	public void render(Graphics2D g) {
		if (ready) {
			long pre = System.nanoTime();
			world.render(g);
			worldtr = (System.nanoTime() - pre);
			pre = System.nanoTime();
			if (!world.getEntityManager().getPlayer().viewCrafting) {
				u.render(g);
				uir = (System.nanoTime() - pre);
			}
		}

		long pre = System.nanoTime();

		if (!ready) {
			if (loadingPhase == -1) {
				handler.getGame().setTps(60);

				handler.setWorld(world);
				resettingFps = false;
				loadingWorld = false;
				ready = true;
			} else {
				g.setColor(Color.black);
				g.fillRect(0, 0, handler.getWidth(), handler.getHeight());
				g.setColor(Color.white);
				g.setFont(new java.awt.Font("Arial", 0, 20));
				switch (loadingPhase) {
				case 1:
					g.drawString("Getting variables...", 100, 100);
					break;
				case 2:
					g.drawString("Loading tiles...", 100, 100);
					break;
				case 3:
					g.drawString("Loading custom entities...", 100, 100);
					break;
				case 4:
					g.drawString("Making background...", 100, 100);
					break;
				default:
					g.drawString("Loading world...", 100, 100);
				}
			}

			otherr = (System.nanoTime() - pre);

		}
	}

	public void init() {

	}

	@Override
	public void renderGUI(Graphics2D g2d) {
		
	}
}
