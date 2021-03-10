package com.zandgall.arvopia.guis;

import java.awt.Graphics2D;

import com.zandgall.arvopia.utils.BevelPlatform;
import com.zandgall.arvopia.utils.Button;

public class Menu extends Gui {
	private static final long serialVersionUID = 1L;
	
	private Button returnToGame;
	private Button titleScreen;
	private Button options;
	private Button exit;
	private Button save;
	private Button achievements;
	
	private BevelPlatform body;
	
	public Menu(com.zandgall.arvopia.Handler game) {
		super(game);

		exit = new Button(game, game.getWidth() / 2, game.getHeight() / 2 + 70, "Quits the application",
				"Quit", 1);
		save = new Button(game, game.getWidth() / 2, game.getHeight() / 2 + 30, "Saves the current world",
				"Save", 1);
		achievements = new Button(game, game.getWidth() / 2, game.getHeight() / 2 - 10,
				"Takes you to achievements menu", "Achievements", 1);
		options = new Button(game, game.getWidth() / 2, game.getHeight() / 2 - 50,
				"Takes you to the options menu", "Options", 1);

		returnToGame = new Button(game, game.getWidth() / 2, game.getHeight() / 2 - 130,
				"Takes you back the the current game", "Return to Game", 1);
		titleScreen = new Button(game, game.getWidth() / 2, game.getHeight() / 2 - 90,
				"Takes you to the title screen", "Title Screen", 1);
		
		body = new BevelPlatform(10, 10, 180, 250);
	}

	public void tick() {
		if (save.on) {
			com.zandgall.arvopia.state.GameState g = (com.zandgall.arvopia.state.GameState) game.getGame().gameState;

			g.saveWorld();
		}

		if (exit.on) {
			game.getGame().stop();
		}
		if (returnToGame.on) {
			game.getGame().unPause();
			game.getWorld().getEntityManager().getPlayer().viewMenu = false;
		}

		if (options.on) {
			com.zandgall.arvopia.state.State.setState(game.getGame().optionState);
			game.getGame().unPause();
			game.getWorld().getEntityManager().getPlayer().viewMenu = false;
		}

		if (titleScreen.on) {
			com.zandgall.arvopia.state.State.setState(game.getGame().menuState);
			game.getGame().unPause();
			game.getWorld().getEntityManager().getPlayer().viewMenu = false;
		}

		if (achievements.on) {
			com.zandgall.arvopia.state.State.setState(game.getGame().achievementsState);
			game.getGame().unPause();
			game.getWorld().getEntityManager().getPlayer().viewMenu = false;
		}

		exit.tick();
		options.tick();
		returnToGame.tick();
		titleScreen.tick();
		achievements.tick();
		save.tick();
	}

	public void render(java.awt.Graphics g) {
		body.render((Graphics2D) g, game.getWidth()/2-90, game.getHeight()/2-140);
		exit.render(g);
		options.render(g);
		returnToGame.render(g);
		titleScreen.render(g);
		achievements.render(g);
		save.render(g);
	}

	public void init() {
	}
}
