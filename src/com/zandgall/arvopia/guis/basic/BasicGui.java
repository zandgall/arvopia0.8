package com.zandgall.arvopia.guis.basic;

import java.awt.Graphics2D;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.state.State;
import com.zandgall.arvopia.utils.ClassLoading;

public abstract class BasicGui {
	
	/*
	 * Static modifiers, to indicate manager
	 */
	
	public String modIdentifier = "Vanilla", guiIdentifier = "GUI";
	
	public static ArrayList<BasicGui> guis = new ArrayList<BasicGui>();
	
	public static BasicGui findByID(String mod, String guid) {
		for(BasicGui gui: guis) {
			if(gui.modIdentifier.equals(mod)&&gui.guiIdentifier.equals(guid)) {
				return gui;
			}
		}
		return null;
	}
	
	public static void addGui(BasicGui gui) {
		guis.add(gui);
	}
	
	public static void tickAll() {
		for(BasicGui g: guis)
			g.unitick();
	}
	
	public static void renderAll(Graphics2D g) {
		for(BasicGui gui: guis)
			if(gui.showing)
				gui.render(g);
	}
	
	public static void loadMods(String jarfile, Handler handler) {
		if(handler==null)
			System.err.println("This handler is null, why'd you give it to us?");
		try {
			ArrayList<Class<?>> objects = ClassLoading.getClasses(jarfile);

			for (Class<?> out : objects)
				if(BasicGui.class.isAssignableFrom(out)) {
					BasicGui gui = (BasicGui) ClassLoading.getC(out);
					gui.game = handler;
					addGui(gui);
				}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/*
	 * Local class
	 */
	
	public boolean showing, global, alwaysShown;
	
	Handler game;
	
	public BasicGui(Handler game, boolean global, boolean alwaysShown) {
		this.global=global;
		this.alwaysShown=alwaysShown;
		
		showing = alwaysShown;
		this.game=game;
	}
	
	public void unitick() {
		if(showing)
			tick();
		
		showing = (showing || alwaysShown) && (global || State.getState()==game.getGame().gameState);
	}
	
	public abstract void tick();
	
	public abstract void render(Graphics2D g); 
	
}
