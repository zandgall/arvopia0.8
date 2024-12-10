package com.zandgall.arvopia.state;

import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Handler;

import java.awt.Graphics2D;

public abstract class State {
	protected Handler handler;
	static Handler handlerStatic;

	private static State currentState = null;
	private static State prevState = null;

	public static void setState(State state) {
		if(state==null) {
			Console.log("State is null, could not load");
			return;
		}

		prevState = currentState;
		currentState = state;
		currentState.init();
		handlerStatic.log("State is now: " + state);
		handlerStatic.getMouse().setLeftClicked(false);
		handlerStatic.getMouse().fullLeft = false;
		handlerStatic.getMouse().fullRight = false;
		handlerStatic.getMouse().resets();
	}

	public static State getState() {
		return currentState;
	}

	public static State getPrev() {
		return prevState;
	}

	public State(Handler handler) {
		this.handler = handler;
		handlerStatic = handler;
	}

	public void openWorld(boolean open, int index) {

	}

	public abstract void tick();

	public void timing() {
//		player.getSong().timing();
	}

	public abstract void render(Graphics2D g2d);
	
	public abstract void renderGUI(Graphics2D g2d);

	public abstract void init();
}
