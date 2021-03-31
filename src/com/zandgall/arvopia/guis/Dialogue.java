package com.zandgall.arvopia.guis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.Public;

public class Dialogue {
	public static Map<String, Double> pauses = new HashMap<String, Double>();
	
	public static ArrayList<Dialogue> dialogue = new ArrayList<Dialogue>();
	
	static boolean lv = false;
	
	String text, name, voice = "DefaultVoice";
	
	double x, y;
	
	double index = 0;
	
	public double speed = 0.2, spacing = 1;
	
	public Dialogue(String text, String name, double x, double y) {
		this.text=text;
		this.name=name;
		this.x=x;
		this.y=y;
	}
	
	public Dialogue(String text, String name, String voice, double x, double y) {
		this.text=text;
		this.name=name;
		this.voice=voice;
		this.x=x;
		this.y=y;
	}
	
	public Dialogue(String text, String name, String voice, double x, double y, double speed, double spacing) {
		this.text=text;
		this.name=name;
		this.voice=voice;
		this.x=x;
		this.y=y;
		this.speed=speed;
		this.spacing=spacing;
	}
	
	public static void tick(Handler game) {
		
//		if(pauses.size()==0) {
			setupPauses();
//		}
		
		if(!lv) {
			lv=true;
			Trading.loadVoices(game);
		}
		
		try {
			for(Dialogue d: dialogue) {
				
				int p = (int) (d.index/d.spacing);
				
				boolean isNext = d.index<d.text.length()-1, vowelNext = isNext && vowels.contains(d.text.charAt((int) d.index+1)), isSpace = d.text.charAt((int) d.index)==' ';
				
				if (isSpace&&vowelNext)
					d.index+=d.speed*1.5;
				else if(pauses.containsKey(""+d.text.charAt((int) d.index)))
					d.index+=d.speed/pauses.get(""+d.text.charAt((int) d.index));
				else
					d.index+=d.speed;
				
				if(d.index>=d.text.length()) {
					dialogue.remove(d);
					break;
				}
				
				if(skip.contains(d.text.charAt((int) d.index)))
					d.index++;
				
				if((int) (d.index/d.spacing)!=p)
					if(!Character.isWhitespace(d.text.charAt((int) d.index))&&!punc.contains(d.text.charAt((int) d.index))) {
						//game.soundSystem.stop(d.voice);
						game.setPosition(d.voice, d.x, d.y, 0);
						//game.soundSystem.setVolume(d.voice, 1000.0f);
						game.play(d.voice);
					}
			}
		} catch(ConcurrentModificationException e) {
			
		}
	}
	
	public static final List<Character> punc = Arrays.asList(',','.','?','!','<','\'','\"',';',':'), skip = Arrays.asList('\'', '\"'), vowels = Arrays.asList('a', 'e', 'i', 'o', 'u');
	
	public static boolean sounds(char c) {
		return !(punc.contains(c));
	}
	
	public static void render(Graphics2D g, Handler game) {
		int i = 0;
		for(Dialogue d: dialogue) {
			if(Public.dist(d.x, d.y, game.getPlayer().getX(), game.getPlayer().getY())<200){
				Tran.TEXT_MODE=2;
				g.setFont(Public.runescape);
				Tran.drawOutlinedText(g, 360d, 386d+i, d.name + ": "+d.text, 1, Color.black, Color.white);
				i-=14;
				Tran.TEXT_MODE=0;
			}
		}
	}
	
	public static void setupPauses() {
		pauses.put(".", 14.0);
		pauses.put(",", 8.0);
		pauses.put("?", 16.0);
		pauses.put("!", 9.5);
		pauses.put(" ", 1.4);
	}
	
}
