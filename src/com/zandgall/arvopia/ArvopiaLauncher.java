package com.zandgall.arvopia;

import com.zandgall.arvopia.utils.Utils;

import java.io.File;
import java.io.IOException;

public class ArvopiaLauncher {
	public static final int[] height = { 360, 405, 540, 720, 900, 1080, 1080, 1080 };
	public static final int[] width = { 640, 720, 960, 1280, 1600, 1920, 2048, 2560 };

	public static Game game;
	public static Log log;

	public static void main(String[] args) {

		Utils.createDirectory(Game.prefix);
		Utils.createDirectory(Game.prefix + "/tmp");
		Utils.createDirectory(Game.prefix + "/logs");
		Utils.createDirectory(Game.prefix + "/logs/World");
		Utils.createDirectory(Game.prefix + "/logs/Player");
		Utils.createDirectory(Game.prefix + "/logs/Key Events");
		Utils.createDirectory(Game.prefix + "/logs/FPSLogs");
		Utils.createDirectory(Game.prefix + "/logs/Environment");
		Utils.createDirectory(Game.prefix + "/logs/FileLoading");
		Utils.createDirectory(Game.prefix + "/Saves");
		Utils.createDirectory(Game.prefix + "/Player");
		Utils.createDirectory(Game.prefix + "/Recording");
		Utils.createDirectory(Game.prefix + "/Mods");
		Utils.createDirectory(Game.prefix + "/World");
		Utils.createDirectory(Game.prefix + "/Pack");

		File file = new File(Game.prefix + "/Options.txt");
		if (!file.exists()) {
			try {
				if (file.createNewFile())
					System.out.println("Options File Created");
				Utils.existWriter("60 2 5 6 60 false", Game.prefix + "/Options.txt");
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Could not create options file! Unable to use files, crashing.");
				return;
			}
		}
		file = new File(Game.prefix + "/DontShowThisAgain");
		if (!file.exists()) {
			try {
				if (file.createNewFile())
					System.out.println("Don't Show File Created");
				Utils.existWriter("false", Game.prefix + "/DontShowThisAgain");
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Could not create \"Don't show this again\" file! Unable to use files, crashing.");
			}
		}

		log = new Log(Game.prefix + "/logs/Arvopia0.8", "Main");

		if(game!=null) {
			game.stop();
			game = null;
		}
		game = new Game("Arvopia 0.8", 720, 405, true, log);
		game.start();

		log.log("Game Launched: Arvopia Alpha test");
	}
}
