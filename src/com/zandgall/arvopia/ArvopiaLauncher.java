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
		
//		if(new File("C:\\Arvopia\\screen.txt").exists()) {
//			if(FileLoader.readFile("C:\\Arvopia\\screen.txt").contains("min")) {
//				startMin();
//			} else {
//				startFull();
//			}
//		} else {
//			startMin();
//		}
		
		Utils.createDirectory("C:\\Arvopia");
		Utils.createDirectory("C:\\Arvopia\\tmp");
		Utils.createDirectory("C:\\Arvopia\\logs");
		Utils.createDirectory("C:\\Arvopia\\logs\\World");
		Utils.createDirectory("C:\\Arvopia\\logs\\Player");
		Utils.createDirectory("C:\\Arvopia\\logs\\Key Events");
		Utils.createDirectory("C:\\Arvopia\\logs\\FPSLogs");
		Utils.createDirectory("C:\\Arvopia\\logs\\Enviornment");
		Utils.createDirectory("C:\\Arvopia\\logs\\FileLoading");
		Utils.createDirectory("C:\\Arvopia\\Saves");
		Utils.createDirectory("C:\\Arvopia\\Player");
		Utils.createDirectory("C:\\Arvopia\\Recording");
		Utils.createDirectory("C:\\Arvopia\\Mods");
		Utils.createDirectory("C:\\Arvopia\\World");
		Utils.createDirectory("C:\\Arvopia\\Pack");
		
		SetFiles.fileSet();
		
		startMin();
	}
	
	public static void startMin() {

		SetFiles.fileSet();

		log = new Log("C:\\Arvopia\\logs\\Arvopia0.8", "Main");
		
		Utils.fileWriter("min", "C:\\Arvopia\\screen.txt");
		
		if(game!=null) {
			game.stop();
			game = null;
		}
		game = new Game("Arvopia 0.8", 720, 405, true, log);
		game.start();

		log.log("Game Launched: Arvopia Alpha test");
	}
}
