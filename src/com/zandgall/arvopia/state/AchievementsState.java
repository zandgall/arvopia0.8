package com.zandgall.arvopia.state;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.input.KeyManager;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.quests.AchievementManager;
import com.zandgall.arvopia.quests.Quest;
import com.zandgall.arvopia.quests.QuestManager;
import com.zandgall.arvopia.utils.BevelPlatform;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Utils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class AchievementsState extends State {
	double scroll;
	Button back;
	double scrollMax = 100.0D;

	BevelPlatform achievement, achievementh, quest, questh;

	public AchievementsState(Handler game) {
		super(game);

		scroll = 0.0D;
		back = new Button(handler, handler.getWidth() - 70, handler.getHeight() - 40, 55, 20,
				"Brings you back to the main menu", "Back");
		achievement = new BevelPlatform(2, 2, 240, 120);
		achievementh = new BevelPlatform(2, 2, 240, 120, 175, -50);
		quest = new BevelPlatform(2, 2, 440, 120);
		questh = new BevelPlatform(2, 2, 440, 120, 205, 50);
	}

	public void tick() {
		scrollMax = Math.max(
				(AchievementManager.al.size() + AchievementManager.left.size()) * 250 - handler.getWidth() + 20,
				(QuestManager.al.size() + QuestManager.alfin.size()) * 450 - handler.getWidth() + 10);

		back.tick();

		if ((back.on) || (KeyManager.esc)) {
			State.setState(State.getPrev());
		}

		if ((KeyManager.checkBind("Up")) && (scroll > 0.0D))
			scroll -= 15.0D;
		if ((KeyManager.checkBind("Down")) && (scroll < scrollMax)) {
			scroll += 15.0D;
		}
		if (((handler.getMouse().getMouseScroll() > 0.0D) && (scroll > 0.0D))
				|| ((handler.getMouse().getMouseScroll() < 0.0D) && (scroll < scrollMax))) {
			scroll -= handler.getMouse().getMouseScroll() * 15.0D;
		}
		if (scroll < 0.0D)
			scroll = 0.0D;
		if (scroll > scrollMax) {
			scroll = scrollMax;
		}
	}

	public void render(Graphics2D g) {
		g.setColor(new Color(134, 200, 255));
		g.fillRect(0, 0, handler.getWidth(), handler.getHeight());

		g.translate(-scroll, 0.0D);

		int leftOff = 10;

		for (Achievement a : AchievementManager.al) {

			achievement.render(g, AchievementManager.al.indexOf(a) * 250 + 10, 10);

			g.setColor(Color.black);
			g.setFont(Public.defaultBoldFont.deriveFont(19.0f));
			Tran.TEXT_MODE = 2;
			Tran.drawString(g, "Achievement:", AchievementManager.al.indexOf(a) * 250 + 130, 12);
			Tran.drawString(g, a.name, AchievementManager.al.indexOf(a) * 250 + 130, 32);
			g.setFont(new Font("Arial", 0, 16));
			Tran.drawString(g, Public.limit(a.description, 240, g.getFont()),
					AchievementManager.al.indexOf(a) * 250 + 130, 52);
			g.setFont(new Font("Arial", 1, 16));
			Tran.drawString(g, "" + a.value, AchievementManager.al.indexOf(a) * 250 + 130, 92);
			Tran.TEXT_MODE = 0;

			leftOff += 250;
		}

		int leftOff2 = 0;
		
		int ala = 0;

		for (Quest a : QuestManager.al) {
			if (!QuestManager.alfin.contains(a)) {

				questh.render(g, leftOff2 + 10, 150);

				g.setColor(Color.black);
				g.setFont(Public.defaultBoldFont.deriveFont(19.0f));
				Tran.TEXT_MODE = 2;
				Tran.drawString(g, "Started Quest:", ala * 450 + 230, 162);
				Tran.drawString(g, a.name, ala * 450 + 230, 182);
				g.setFont(new Font("Arial", 0, 16));
				Tran.drawString(g, Public.limit(a.description, 440, g.getFont()),
						ala * 450 + 230, 202);
				g.setFont(new Font("Arial", 1, 16));
				Tran.drawString(g, "" + a.value, ala * 450 + 230, 242);
				Tran.TEXT_MODE = 0;
				
				ala++;

				leftOff2 += 450;
			}
		}
		for (Achievement a : AchievementManager.left) {

			achievementh.render(g, AchievementManager.left.indexOf(a) * 250 + leftOff, 10);

			g.setColor(Color.black);
			g.setFont(Public.defaultBoldFont.deriveFont(19.0f));
			Tran.TEXT_MODE = 2;
			Tran.drawString(g, "Achievement:", AchievementManager.left.indexOf(a) * 250 + leftOff + 120, 12);
			Tran.drawString(g, "????", AchievementManager.left.indexOf(a) * 250 + leftOff + 120, 32);
			g.setFont(new Font("Arial", 0, 16));
			Tran.drawString(g, Public.limit(a.hint, 240, g.getFont()),
					AchievementManager.left.indexOf(a) * 250 + leftOff + 120, 52);
			g.setFont(new Font("Arial", 1, 16));
			Tran.drawString(g, "" + a.value, AchievementManager.left.indexOf(a) * 250 + leftOff + 120, 92);
			Tran.TEXT_MODE = 0;
		}

		for (Quest a : QuestManager.alfin) {
			g.setColor(Color.black);
			
			quest.render(g, QuestManager.alfin.indexOf(a) * 450 + 10 + leftOff2, 150);

			g.setColor(Color.black);
			g.setFont(Public.defaultBoldFont.deriveFont(19.0f));
			Tran.TEXT_MODE = 2;
			Tran.drawString(g, "Finished Quest:", QuestManager.alfin.indexOf(a) * 450 + 220 + leftOff2, 162);
			Tran.drawString(g, a.name, QuestManager.alfin.indexOf(a) * 450 + 220 + leftOff2, 182);
			g.setFont(new Font("Arial", 0, 16));
			Tran.drawString(g, Public.limit(a.description, 440, g.getFont()),
					QuestManager.alfin.indexOf(a) * 450 + 220 + leftOff2, 202);
			g.setFont(new Font("Arial", 1, 16));
			Tran.drawString(g, "" + a.value, QuestManager.alfin.indexOf(a) * 450 + 220 + leftOff2, 242);
			Tran.TEXT_MODE = 0;
		}

		g.setTransform(new AffineTransform());

		g.setColor(Color.white);
		g.setFont(new Font("Arial", 1, 20));
		Tran.drawOutlinedText(g, 10d, handler.getHeight()-10, "Game points: " + (Utils.parseInt(FileLoader.readFile(Game.prefix + "/00.arv")) + Utils.parseInt(FileLoader.readFile(Game.prefix + "/02.arv"))), 1, Color.black, Color.white);
		Tran.drawOutlinedText(g, 10d, 150, "Quests:", 1, Color.black, Color.white); 

		back.render(g);
	}

	public void init() {
		back = new Button(handler, handler.getWidth() - 60, handler.getHeight() - 30, 55, 20,
				"Brings you back to the main menu", "Back");
	}

	@Override
	public void renderGUI(Graphics2D g2d) {
		
	}
}
