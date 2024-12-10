package com.zandgall.arvopia.input;

import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.utils.Mapper;

import java.awt.event.KeyEvent;

public class KeyManager implements java.awt.event.KeyListener {
	public static boolean[] keys, preKeys, typedKeys, typedIteration, num = new boolean[11], function = new boolean[12];
	
//	public boolean up, down, left, right, invtry, crft, b, c, m, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12;

	private Character typedKey;
	public static boolean ctrl, shift, esc, enter;
	public static int lastKey = 0;
	
	public static Mapper<String, Integer> keybinds = new Mapper<String, Integer>(), resetbind = new Mapper<String, Integer>();
	public static Mapper<String, String> keysections = new Mapper<String, String>();
	public static boolean settingKeybind = false;
	public static String selectedBind = "";
	public static boolean typed, pressed, preTyped, existantTyped = false;
	
	public KeyManager() {
		keys = new boolean[255];
		typedKeys = new boolean[255];
		typedIteration = new boolean[255];
		
		addKeybind("Up", "General", KeyEvent.VK_W, KeyEvent.VK_UP, KeyEvent.VK_SPACE);
		addKeybind("Right", "General", KeyEvent.VK_D, KeyEvent.VK_RIGHT);
		addKeybind("Left", "General", KeyEvent.VK_A, KeyEvent.VK_LEFT);
		addKeybind("Down", "General", KeyEvent.VK_S, KeyEvent.VK_DOWN);
		addKeybind("Stats", "General", KeyEvent.VK_Z);
		addKeybind("Crafting", "General", KeyEvent.VK_X);
		addKeybind("Interact", "General", KeyEvent.VK_C);
		addKeybind("Debug", "General", KeyEvent.VK_V);
		addKeybind("Toggle Map", "General", KeyEvent.VK_M);

		addKeybind("Sculpt", "Level Creator", KeyEvent.VK_Q);
		addKeybind("Smart Rect", "Level Creator", KeyEvent.VK_S);
		addKeybind("Rectangle", "Level Creator", KeyEvent.VK_R);
		addKeybind("Format", "Level Creator", KeyEvent.VK_F);
		addKeybind("Single Tile", "Level Creator", KeyEvent.VK_T);
		addKeybind("Eraser", "Level Creator", KeyEvent.VK_E);
		addKeybind("Fill", "Level Creator", KeyEvent.VK_G);
		addKeybind("Line", "Level Creator", KeyEvent.VK_W);
	}
	
	public static void addKeybind(String name, int key) {
		keysections.put("Misc", name);
		keybinds.put(name, key);
		
		resetbind = keybinds.clone();
	}
	
	public static void addKeybind(String name, String section, int key) {
		keybinds.put(name, key);
		keysections.put(section, name);
//		Console.log(section, name, keysections.getValues(section));
		
		resetbind = keybinds.clone();
	}
	
	public static void addKeybind(String name, String section, int... key) {
		for(int i : key)
			keybinds.put(name, i);
		keysections.put(section, name);
//		Console.log(section, name, keysections.getValues(section));
		
		resetbind = keybinds.clone();
	}
	
	public static void addKeybind(String name, int... key) {
		keysections.put("Misc", name);
		for(int i : key)
			keybinds.put(name, i);
		
		resetbind = (Mapper<String, Integer>) keybinds.clone();
	}
	
	public static boolean checkBind(String key) {
		if(!keybinds.hasKey(key) || keybinds.getValues(key).size()==0)
			return false;
		
		for(int i = 0; i<keybinds.getValues(key).size(); i++) {
			if(keys[keybinds.getValues(key).get(i)])
				return true;
		}
		
		return false;
	}

	public Character getNameOfKey() {
		return typedKey;
	}

	public boolean prej;

	public int keyCode() {
		return KeyEvent.getExtendedKeyCodeForChar(typedKey.charValue());
	}
	
	public void tick() {
		/*
		up = ((keys[87]) || (keys[38]) || (keys[32]));
		down = ((keys[83]) || (keys[40]));
		left = ((keys[65]) || (keys[37]));
		right = ((keys[68]) || (keys[39]));
		invtry = ((keys[90]) && (typed));
		crft = ((keys[88]) && (typed));
		b = ((keys[66]) && (typed));
		c = ((keys[67]));
		m = keys[77];

		f1 = keys[112];
		f2 = keys[113];
		f3 = keys[114];
		f4 = keys[115];
		f5 = keys[116];
		f6 = keys[117];
		f7 = keys[118];
		f8 = keys[119];
		f9 = keys[120];
		f10 = keys[121];
		f11 = keys[122];
		f12 = keys[123];
		*/
		
		num[0] = keys[49];
		num[1] = keys[50];
		num[2] = keys[51];
		num[3] = keys[52];
		num[4] = keys[53];
		num[5] = keys[54];
		num[6] = keys[55];
		num[7] = keys[56];
		num[8] = keys[57];
		num[9] = keys[48];
		
		function[0] = keys[112];
		function[1] = keys[113];
		function[2] = keys[114];
		function[3] = keys[115];
		function[4] = keys[116];
		function[5] = keys[117];
		function[6] = keys[118];
		function[7] = keys[119];
		function[8] = keys[120];
		function[9] = keys[121];
		function[10] = keys[122];
		function[11] = keys[123];
		
		ctrl = keys[KeyEvent.VK_CONTROL];
		shift = keys[KeyEvent.VK_SHIFT];

//    if ((keys[75] != 0) && (keys[''] != 0) && (gamerecorder.recording)) {
//      gamerecorder.record();
//    } else if ((keys[74] != 0) && (keys[''] != 0) && (gamerecorder.recording)) {
//      System.out.println("STOP");
//      gamerecorder.stop();
//      System.out.println("DONE" + System.lineSeparator() + System.lineSeparator() + System.lineSeparator());
//    }

		prej = keys[74];

		esc = ((keys[27]) && (typed));
		
		enter = keys[KeyEvent.VK_ENTER];

//		if(typed||preTyped)
//			pressed = false;
	}

	public void postTick() {
		existantTyped = false;
		for(int i = 0; i<typedKeys.length; i++) {
			existantTyped = typedKeys[i]||existantTyped;
			if(typedKeys[i])
				typedKeys[i]=false;
//			typedIteration[i]=typedKeys[i];
		}

		preTyped = typed;

		typed = false;
	}

	public void keyPressed(KeyEvent e) {
		if(settingKeybind)
			keybinds.put(selectedBind, e.getKeyCode());
		
//		if (!keys[e.getKeyCode()]) {
//			Game.log.log("Key Code pressed: " + e.getKeyCode() + " Name: " + e.getKeyChar());
//		}
		pressed = true;
		keys[e.getKeyCode()] = true;
		typedKeys[e.getKeyCode()] = true;
		lastKey = e.getKeyCode();
		typed = false;
		preTyped = true;
		typedKey = e.getKeyChar();
	}

	public void keyReleased(KeyEvent e) {
		pressed = false;
		keys[e.getKeyCode()] = false;
		typedKeys[e.getKeyCode()] = false;
		preTyped = false;
		typed = false;
	}

	public void keyTyped(KeyEvent e) {
		keys[e.getKeyCode()] = true;
		typedKeys[e.getKeyCode()] = true;
		typedKey = e.getKeyChar();
		preTyped = true;
		typed = true;
	}
}
