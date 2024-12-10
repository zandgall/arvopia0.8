package com.zandgall.arvopia;

import java.util.ArrayList;

import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.utils.ClassLoading;
import com.zandgall.arvopia.worlds.World;

public abstract class Initiator {
	
	public String modIdentifier = "Vanilla", initIdentifier = "Initiator";
	
	public static ArrayList<Initiator> initiations = new ArrayList<>();
	
	public static Initiator getFromClass(String className) {
		for(Initiator i: initiations) {
			if(i.getClass().getName().equals(className))
				return i;
		}
		return null;
	}
	
	public static Initiator getFromID(String mod, String id) {
		for(Initiator i: initiations) {
			if(i.modIdentifier.equals(mod)&&i.initIdentifier.equals(id))
				return i;
		}
		return null;
	}
	
	public static void agameInit(Handler game) {
		for(Initiator i: initiations) {
			i.gameInit(game);
		}
	}
	
	public static void aitemInit(Handler game, Player p) {
		for(Initiator i: initiations) {
			i.itemInit(game, p);
		}
	}
	
	public static void aworldInit(Handler game, World w) {
		for(Initiator i: initiations) {
			i.worldInit(game, w);
		}
	}
	
	public static void aplayerInit(Handler game, Player e) {
		for(Initiator i: initiations) {
			i.playerInit(game, e);
		}
	}
	
	public static void asavingWorld(Handler game, String pathToSave) {
		for(Initiator i: initiations) {
			i.savingWorld(game, pathToSave);
		}
	}
	
	public static void aloadSave(Handler game, String pathToSave) {
		for(Initiator i: initiations) {
			i.loadSave(game, pathToSave);
		}
	}
	
	public static void atick(Handler game) {
		for(Initiator i: initiations) {
			i.tick(game);
		}
	}

	public static void loadMods(String jarfile) {
		try {
			ArrayList<Class<?>> objects = ClassLoading.getClasses(jarfile);

			for (Class<?> out : objects) {
				if(Initiator.class.isAssignableFrom(out))
					initiations.add((Initiator) ClassLoading.getC(out));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Initiator() {}
	
	public abstract void gameInit(Handler game);
	
	public abstract void itemInit(Handler game, Player p);
	
	public abstract void worldInit(Handler game, World w);
	
	public abstract void playerInit(Handler game, Player p);
	
	public abstract void savingWorld(Handler game, String pathToSave);
	
	public abstract void loadSave(Handler game, String pathToSave);

	public abstract void tick(Handler game);
	
}
