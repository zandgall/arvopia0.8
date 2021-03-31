package com.zandgall.arvopia;

import org.opencv.core.Core;

import com.zandgall.arvopia.fileSetter.SetFiles;
import com.zandgall.arvopia.utils.Utils;

public class ArvopiaLauncher {
	public static final int[] height = { 360, 405, 540, 720, 900, 1080, 1080, 1080 };
	public static final int[] width = { 640, 720, 960, 1280, 1600, 1920, 2048, 2560 };

	public static Game game;
	public static Log log;

	public static void main(String[] args) {
		
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
//		if(new File("C:/Arvopia/screen.txt").exists()) {
//			if(FileLoader.readFile("C:/Arvopia/screen.txt").contains("min")) {
//				startMin();
//			} else {
//				startFull();
//			}
//		} else {
//			startMin();
//		}
		
		Utils.createDirectory(Game.prefix + "/Arvopia");
		Utils.createDirectory(Game.prefix + "/Arvopia/tmp");
		Utils.createDirectory(Game.prefix + "/Arvopia/logs");
		Utils.createDirectory(Game.prefix + "/Arvopia/logs/World");
		Utils.createDirectory(Game.prefix + "/Arvopia/logs/Player");
		Utils.createDirectory(Game.prefix + "/Arvopia/logs/Key Events");
		Utils.createDirectory(Game.prefix + "/Arvopia/logs/FPSLogs");
		Utils.createDirectory(Game.prefix + "/Arvopia/logs/Enviornment");
		Utils.createDirectory(Game.prefix + "/Arvopia/logs/FileLoading");
		Utils.createDirectory(Game.prefix + "/Arvopia/Saves");
		Utils.createDirectory(Game.prefix + "/Arvopia/Player");
		Utils.createDirectory(Game.prefix + "/Arvopia/Recording");
		Utils.createDirectory(Game.prefix + "/Arvopia/Mods");
		Utils.createDirectory(Game.prefix + "/Arvopia/World");
		Utils.createDirectory(Game.prefix + "/Arvopia/Pack");
		
		SetFiles.fileSet();
		
		startMin();
	}
	
	public static void startMin() {

		SetFiles.fileSet();

		log = new Log("Arvopia/logs/Arvopia0.8", "Main");
		
		if(game!=null) {
			game.stop();
			game = null;
		}
		game = new Game("Arvopia 0.8", 720, 405, true, log);
		game.start();

		log.log("Game Launched: Arvopia Alpha test");
	}
}
