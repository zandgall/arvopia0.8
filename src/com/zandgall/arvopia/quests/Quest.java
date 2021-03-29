package com.zandgall.arvopia.quests;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.entity.statics.House;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Utils;

public class Quest {
	
	public static int QUEST_FIN = 0;
	
	public static Quest getWoodForLia = new Quest("Get wood for Lia", "Get Lia her 10 pieces of wood", 100) {
		public boolean completable(Handler game) {
			return game.getPlayer().getItem("Wood").amount>=10;
		}
		
		public void complete(Handler game) {
			game.getPlayer().getItem("Wood").amount-=10;
		}
	};
	public static Quest stoneForFawncier = new Quest("Stone for Fawncier", "Get Fawncier 50 stone to revamp the houses",
			200) {
		public boolean completable(Handler game) {
			return game.getPlayer().getItem("Stone").amount>=50;
		}
		public void complete(Handler game) {
			game.getPlayer().getItem("Stone").amount-=50;
		}
	};
	public static Quest materialsForFrizzy = new Quest("Materials for Frizzy",
			"Get 50 wood, 50 stone, and 20 flower petals for Frizzy", 400) {
		public boolean completable(Handler game) {
			return game.getPlayer().getItem("Wood").amount>=50 && game.getPlayer().getItem("Stone").amount>=50 && game.getPlayer().getItem("Petals").amount>=20;
		}
		public void complete(Handler game) {
			game.getPlayer().getItem("Wood").amount-=50;
			game.getPlayer().getItem("Stone").amount-=50;
			game.getPlayer().getItem("Petal").amount-=20;
		}
	};
	public static Quest isolatedWood = new Quest("Isolated Wood", "Get 40 wood for the isolated builder", 350);

	static Handler game;

	static QuestManager qm;
	static ArrayList<Quest> full;
	public String name;
	public String description;
	public int value;

	public Quest(String name, String description, int value) {
		this.name = name;
		this.description = Public.limit(description, 210, new Font("Arial", 0, 10));
		this.value = value;
	}

	public static void init(Handler game, QuestManager qm) {
		Quest.game = game;

		File f = new File(Game.prefix + "\\Arvopia\\02.arv");
		if (!f.exists()) {
			Utils.fileWriter("0", Game.prefix + "\\Arvopia\\02.arv");
		}
		f = new File(Game.prefix + "\\Arvopia\\03.arv");
		if (!f.exists()) {
			Utils.fileWriter("", Game.prefix + "\\Arvopia\\03.arv");
		}
		f = new File(Game.prefix + "\\Arvopia\\04.arv");
		if (!f.exists()) {
			Utils.fileWriter("", Game.prefix + "\\Arvopia\\04.arv");
		}
		Quest.qm = qm;

		full = new ArrayList<Quest>();
		full.add(getWoodForLia);
		full.add(stoneForFawncier);
		full.add(materialsForFrizzy);
		full.add(isolatedWood);

//		String s = FileLoader.readFile("C:\\Arvopia\\03.arv");
//		String s2 = FileLoader.readFile("C:\\Arvopia\\03.arv");

		for (Quest a : full) {
			qm.addForRemoval(a);

//			if (s.contains(a.name))
//				qm.addSilent(a);
//			if (s2.contains(a.name)) {
//				qm.finSilent(a);
//			}
		}
	}
	
	public static void addQuest(Quest q) {
		full.add(q);
		qm.addForRemoval(q);
	}
	
	public boolean completable(Handler game) {
		return false;
	}
	
	public void complete(Handler game) {
		
	}
	
	public static void startSilent(String name) {
		qm.addSilent(getQuest(name));
	}
	
	public static void finishSilent(String name) {
		QUEST_FIN++;
		qm.finSilent(getQuest(name));
	}
	
	public static Quest getQuest(String name) {
		for (Quest a : full) {
			if ((a.name.contains(name)) || (name.contains(a.name)))
				return a;
		}
		return null;
	}

	public static void begin(Quest a) {
//		if ((FileLoader.readFile("C:\\Arvopia\\03.arv", false) != null)
//				&& (FileLoader.readFile("C:\\Arvopia\\03.arv", false).contains(a.name))
//				) {
//			return;
//		}
//		String bef = FileLoader.readFile("C:\\Arvopia\\03.arv");
//		Utils.existWriter(bef + System.lineSeparator() + a.name, "C:\\Arvopia\\03.arv");

		if(!QuestManager.al.contains(a))
			qm.add(a); 
	}

	public static void finish(Quest a) {
		if ((FileLoader.readFile(Game.prefix + "\\Arvopia\\04.arv", false) != null)
				&& (FileLoader.readFile(Game.prefix + "\\Arvopia\\04.arv", false).contains(a.name))) {
		}
		if(!FileLoader.readFile(Game.prefix + "\\Arvopia\\03.arv").contains(a.name)) {
			int pre = Utils.parseInt(FileLoader.readFile(Game.prefix + "\\Arvopia\\02.arv", false));
			Utils.existWriter("" + (pre + a.value), Game.prefix + "\\Arvopia\\02.arv");
			Utils.existWriter(FileLoader.readFile(Game.prefix + "\\Arvopia\\03.arv")+System.lineSeparator()+a.name, Game.prefix + "\\Arvopia\\03.arv");
		}
		
		QUEST_FIN++;
		if(QUEST_FIN>=3)
			Achievement.award(Achievement.helper);
		
		Achievement.award(Achievement.firstquest);

		a.complete(game);
		qm.fin(a);
	}

	public static boolean questcompletable(Quest a) {
		Player p = game.getEntityManager().getPlayer();
		/*
		if ((a == stoneForFawncier) && (p.items.get("Metal").amount >= 50))
			return true;
		if ((a == materialsForFrizzy) && 
				(p.items.get("Metal").amount >= 50) && 
				(p.items.get("Wood").amount >= 50) && 
				(p.items.get("Petals").amount >= 20))
			return true;
		if ((a == isolatedWood) && (p.items.get("Wood").amount >= 40))
			return true;
		if ((a == getWoodForLia) && (p.items.get("Wood").amount >= 10))
			return true;
		*/
		return a.completable(game);
//		return false;
	}

	public static void extrawork(Quest a) {
		
	}
	
	public String toString() {
		return name + " " + description + " " + value;
	}
	
}