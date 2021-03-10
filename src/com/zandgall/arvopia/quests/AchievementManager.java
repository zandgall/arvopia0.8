package com.zandgall.arvopia.quests;

import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.BevelPlatform;
import com.zandgall.arvopia.utils.Public;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class AchievementManager {
	public AchievementManager() {
	}

	public ArrayList<Achievement> ach = new ArrayList<Achievement>();
	public static ArrayList<Achievement> al = new ArrayList<Achievement>();
	public static ArrayList<Achievement> left = new ArrayList<Achievement>();
	
	public static BevelPlatform body;

	long timer = 250L;

	public void Render(Graphics g) {
		if(body==null)
			body = new BevelPlatform(2, 2, 120, 50);
		for (Achievement a : ach) {
//			g.setColor(Color.black);
//			g.drawRect(600, -ach.indexOf(a) * 55 + 350, 118, 50);
//			g.setColor(Color.gray);
//			g.fillRect(601, -ach.indexOf(a) * 55 + 351, 117, 49);
			body.render((Graphics2D) g, 600, -ach.indexOf(a)*55+350);
			
			g.setColor(Color.black);
			g.setFont(Public.defaultBoldFont);
			Tran.TEXT_MODE=2;
			Tran.drawString(g, "Achievement:", 660, -ach.indexOf(a) * 55 + 350);
			Tran.drawString(g, a.name, 660, -ach.indexOf(a) * 55 + 360);
			g.setFont(new Font("Arial", 0, 10));
			Tran.drawString(g, a.description, 660, -ach.indexOf(a) * 55 + 370);
			g.setFont(new Font("Arial", 1, 10));
			Tran.drawString(g, "" + a.value, 660, -ach.indexOf(a) * 55 + 380);
			Tran.TEXT_MODE=0;
		}
	}

	public void tick() {
		if (ach.size() > 0) {
			timer -= 1L;
			if (timer <= 0L) {
				ach.remove(0);
				timer = 250L;
			}
		}
	}

	public void addForRemoval(Achievement a) {
		left.add(a);
	}

	public void add(Achievement a) {
		ach.add(a);
		al.add(a);
		if (left.contains(a))
			left.remove(a);
		if (al.size() == Achievement.full.size() - 1) {
			Achievement.award(Achievement.allachievements);
		}
	}

	public void addSilent(Achievement a) {
		al.add(a);
		if (left.contains(a))
			left.remove(a);
		if (al.size() == Achievement.full.size() - 1) {
			Achievement.award(Achievement.allachievements);
		}
	}
}
