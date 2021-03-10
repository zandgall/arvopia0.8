package com.zandgall.arvopia.quests;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.BevelPlatform;

public class QuestManager {
	public QuestManager() {
	}

	public ArrayList<Quest> ach = new ArrayList<Quest>();
	public ArrayList<Quest> fin = new ArrayList<Quest>();
	public static ArrayList<Quest> al = new ArrayList<Quest>();
	public static ArrayList<Quest> alfin = new ArrayList<Quest>();
	public static ArrayList<Quest> left = new ArrayList<Quest>();
	
	public static BevelPlatform body;

	long timer = 250L;
	long finTimer = 250L;

	int offset = 0;
	
	public void Render(Graphics g) {
		if(body == null)
			body = new BevelPlatform(2, 2, 220, 60);
		for (Quest a : ach) {
			body.render((Graphics2D) g, 500, ach.indexOf(a)*55-offset);

			g.setColor(Color.black);
			g.setFont(com.zandgall.arvopia.utils.Public.defaultBoldFont);
			Tran.TEXT_MODE=2;
			Tran.drawString(g, "Quest Started:", 610, ach.indexOf(a)*55-offset);
			Tran.drawString(g, a.name, 610, ach.indexOf(a)*55+10-offset);
			g.setFont(new Font("Arial", 0, 10));
			Tran.drawString(g, a.description, 610, ach.indexOf(a)*55+20-offset);
			g.setFont(new Font("Arial", 1, 10));
			Tran.drawString(g, ""+a.value, 610, ach.indexOf(a)*55+40-offset);
			Tran.TEXT_MODE=0;
		}
		
		for (Quest a : fin) {
			body.render((Graphics2D) g, 500, ach.indexOf(a)*55-offset);

			g.setColor(Color.black);
			g.setFont(com.zandgall.arvopia.utils.Public.defaultBoldFont);
			Tran.TEXT_MODE=2;
			Tran.drawString(g, "Quest Finished:", 610, ach.indexOf(a)*55-offset);
			Tran.drawString(g, a.name, 610, ach.indexOf(a)*55+10-offset);
			g.setFont(new Font("Arial", 0, 10));
			Tran.drawString(g, a.description, 610, ach.indexOf(a)*55+20-offset);
			g.setFont(new Font("Arial", 1, 10));
			Tran.drawString(g, ""+a.value, 610, ach.indexOf(a)*55+40-offset);
			Tran.TEXT_MODE=0;
		}
	}

	public void tick() {
		if (ach.size() > 0) {
			timer -= 1L;
			if (timer <= 0L) {
				offset+=2;
				if(offset>=58) {
					offset=0;
					ach.remove(0);
					timer = 250L;
				}
			}
		}

		if (fin.size() > 0) {
			finTimer -= 1L;
			if (finTimer <= 0L) {
				offset+=2;
				if(offset>=58) {
					offset=0;
					fin.remove(0);
					finTimer = 250L;
				}
			}
		}
	}

	public void addForRemoval(Quest a) {
		left.add(a);
	}

	public void add(Quest a) {
		ach.add(a);
		al.add(a);
	}

	public void addSilent(Quest a) {
		al.add(a);
	}

	public void fin(Quest a) {
		fin.add(a);
		alfin.add(a);
	}

	public void finSilent(Quest a) {
		alfin.add(a);
	}
}
