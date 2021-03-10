package com.zandgall.arvopia.guis;

import com.zandgall.arvopia.Handler;
import java.awt.Graphics;
import java.io.Serializable;

public abstract class Gui implements Serializable {
	private static final long serialVersionUID = 1L;
	protected Handler game;

	public Gui(Handler game) {
		this.game = game;
	}

	public abstract void tick();

	public abstract void render(Graphics paramGraphics);

	public abstract void init();
}
