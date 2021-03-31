package com.zandgall.arvopia.quests;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Utils;
import java.io.File;
import java.util.ArrayList;

public class Achievement {
	public static Handler game;
	public static Achievement firstcraft = new Achievement("Craft first!", "Craft a material or tool",
			"Raw materials aren't enough", 100);
	public static Achievement hoarder = new Achievement("Hoarder", "Get 100 of any item", "Do you need more items?",
			250);
	public static Achievement helper = new Achievement("Neighbor Helper", "Help 3 people", "Keep on helping people",
			200);
	public static Achievement noname = new Achievement("No name", "Name yourself", "What is your name?", 50);

	public static Achievement reporter = new Achievement("Something's wrong...", "Report a bug",
			"The game may be broken", 200);
	public static Achievement smithy = new Achievement("Smithy", "Craft 10 materials or tools",
			"Make yourself some more materials", 200);
	public static Achievement architect = new Achievement("Architect", "Make 3 different levels in the level maker",
			"Try making something of your own", 500);
	public static Achievement firstquest = new Achievement("First Quest", "Finish your first quest", "Help someone out",
			100);
	public static Achievement disrespectful = new Achievement("Disrespectful!", "Kill an NPC", "Get a little mean",
			200);
	public static Achievement allachievements = new Achievement("Is that all?", "Get all (vanilla) achievements",
			"You aren't finished", 1000);
	static AchievementManager am;
	public String name;
	public String description;
	public String hint;
	public int value;
	static ArrayList<Achievement> full;

	public boolean IN_GAME = true;

	public Achievement(String name, String description, String hint, int value) {
		this.name = name;
		this.hint = hint;
		this.description = description;
		this.value = value;
	}

	public static void init(Handler game, AchievementManager am) {
		Achievement.game = game;

		File f = new File(Game.prefix + "/Arvopia/00.arv");
		if (!f.exists()) {
			Utils.fileWriter("0", Game.prefix + "/Arvopia/00.arv");
		}
		f = new File(Game.prefix + "/Arvopia/01.arv");
		if (!f.exists()) {
			Utils.fileWriter("", Game.prefix + "/Arvopia/01.arv");
		}
		Achievement.am = am;

		full = new ArrayList<Achievement>();
		full.add(helper);
		full.add(hoarder);
		full.add(reporter);
		reporter.IN_GAME = false;
		full.add(noname);
		noname.IN_GAME = false;
		full.add(architect);
		architect.IN_GAME = false;
		full.add(firstcraft);
		full.add(smithy);
		full.add(firstquest);
		full.add(disrespectful);
		full.add(allachievements);

		String s = FileLoader.readFile(Game.prefix + "/Arvopia/01.arv");

		for (Achievement a : full) {
			am.addForRemoval(a);

			if (s.contains(a.name)) {
				if (a.IN_GAME)
					a.value = 0;
				else
					finishSilent(a);
			}
		}
	}

	public static void addAchievement(Achievement a) {
		full.add(a);
		am.addForRemoval(a);
	}

	public static void finishSilent(Achievement a) {
		am.addSilent(a);
	}

	public static void award(Achievement a) {
		if (AchievementManager.al.contains(a))
			return;
		game.log("CONGRATULATION! YOU WERE JUST AWARDED " + a.name + ": " + a.description);

		if (!FileLoader.readFile(Game.prefix + "/Arvopia/01.arv").contains(a.name)) {
			int prev = Utils.parseInt(FileLoader.readFile(Game.prefix + "/Arvopia/00.arv", false));
			game.log("You earned: " + a.value);
			Utils.fileWriter("" + (prev + a.value), Game.prefix + "/Arvopia/00.arv");
			Utils.fileWriter(FileLoader.readFile(Game.prefix + "/Arvopia/01.arv") + System.lineSeparator() + a.name,
					Game.prefix + "/Arvopia/01.arv");
		}

		am.add(a);
	}

	public static Achievement get(String name) {
		for (Achievement a : full)
			if (a.name.contains(name) || name.contains(a.name))
				return a;
		return null;
	}
}
