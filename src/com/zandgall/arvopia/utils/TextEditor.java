package com.zandgall.arvopia.utils;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.input.KeyManager;

import java.awt.*;
import java.util.ArrayList;

public class TextEditor {
	Handler game;
	private int x;
	private int y;
	private int width;
	private int height, rows;
	String content;
	private boolean selected = false;
	
	boolean trippin;
	double fullIndex = 0;
	int index = 0;
	
	int textOffset = 0;

	BevelIndent graphic; 
	
	Font font;
	
	public TextEditor(Handler game, int x, int y, int width, int rows, Font font) {
		this.game = game;

		content = "";

		this.rows = rows;
		this.x = x;
		this.y = y;
		this.width = width;
		this.font = font;
		textOffset = (int) (Tran.measureString("|g", font).getY()*1.5);
		height = (int) (rows * textOffset);
		
		graphic = new BevelIndent(1, 1, width, height);
	}

	public TextEditor(Handler game, int x, int y, int width, int rows, String charset, Font font) {
		this.game = game;

		content = charset;

		this.rows = rows;
		this.x = x;
		this.y = y;
		this.width = width;
		this.font=font;
		textOffset = (int) (Tran.measureString("|g", font).getY()*1.5);
		height = (int) (rows * textOffset);
		
		fullIndex = charset.length();
		index = charset.length();
		
		graphic = new BevelIndent(1, 1, width, height);
	}

	public boolean isSelected() {
		return selected;
	}

	long trippedtimer;
	
	long typeTimer=2, typeSpeed = 5;

	public void tick() {
		boolean[] keys = KeyManager.keys;

		if (game.getMouse().fullLeft) {
			int x = game.getMouse().rMouseX();
			int y = game.getMouse().rMouseY();
			selected = (x >= this.x) && (x <= this.x + width) && (y >= this.y) && (y <= this.y + height);
		}
		
		trippedtimer += 1L;
		trippin = Math.sin(trippedtimer/8.0)>0 && selected;
		
		if (KeyManager.existantTyped && (selected)) {
			if(KeyManager.keys[39]) {
				System.out.println("Moving index");
				fullIndex+=1;
				trippin = true;
				trippedtimer = 8;
			} else if(KeyManager.keys[37]) {
				System.out.println("Moving index");
				fullIndex-=1;
				trippin = true;
				trippedtimer = 8;
			}
			if(game.getKeyManager().getNameOfKey()!=null) {
				if ((keys[8]) && (content.length() >= 1)) {
					String oc = content;
					String first = oc.substring(0, Math.max(index-1, 0));
					String last = oc.substring(index);
					content = first + last;
					fullIndex--;
				} else if ((KeyManager.keys[10])) {
					if((getGameContent().length < rows)) {
						String oc = content;
						String first = oc.substring(0, index);
						String last = oc.substring(index);
						content = first+'\n' + last;
						fullIndex++;
					} else if(rows==1)
						selected = false;
				} else if(keys[35]) {
					fullIndex = content.length();
				} else if(keys[36]) {
					fullIndex = 0;
				} else if ((Character.isDefined(game.getKeyManager().getNameOfKey()) && !game.getKeyManager().getNameOfKey().toString().equals("")) && !(keys[8]||keys[10]||keys[27]||keys[127])) {
					String oc = content;
					String first = oc.substring(0, index);
					String last = oc.substring(index);
					content = first+game.getKeyManager().getNameOfKey().toString() + last;

					fullIndex++;
				}
			}
			typeTimer=0;
		}
		typeTimer++;

		if (selected) {
			game.getMouse().changeCursor("TYPE");
			fullIndex= Public.range(0, content.length(), fullIndex);
		}
		index = (int) fullIndex;
		textOffset = (int) ((Tran.measureString(content, font).getY()));
	}
	
	public void render(Graphics g) {
		g.setFont(font);
		graphic.render((Graphics2D) g, x, y);
		g.setColor(Color.black);
		g.drawRect(x, y, width, height);
		Shape prec = g.getClip();
		g.clipRect(x, y, width, height);
		Point size = Tran.measureString(content, font);
		Tran.drawString(g, content, x+5 - Math.max(size.x-width, 0), y+textOffset);
		if(trippin) { 
			String[] fullStrings = content.substring(0, Math.min(index, content.length())).split("\n");
			int offset = Tran.measureString(fullStrings[fullStrings.length-1], font).x;
			int offsety = fullStrings.length;
			g.drawLine(x+5+offset, y+textOffset*(offsety-1)+2, x+5+offset, y+textOffset*(offsety)+2);
		}
		g.setClip(prec);
	}

	public String[] getGameContent() {
		ArrayList<String> newString = new ArrayList<String>(); 

		newString.add("");

		if (content == null || content.length() == 0) {
			return new String[] { "" };
		}

		for (int i = 0; i < content.length(); i++) {
			if (content.charAt(i) == '\n') {
				newString.add("");
			} else {
				if(i==index-1)
					newString.set(newString.size() - 1, (String) newString.get(newString.size() - 1) + content.charAt(i)+(trippin ? '|' : " "));
				else newString.set(newString.size() - 1, (String) newString.get(newString.size() - 1) + content.charAt(i));
			}
		}
//		newString.set(newString.size() - 1, (String) newString.get(newString.size() - 1) + (trippin ? '|' : " "));

		String[] array = new String[newString.size()];
		newString.toArray(array);

		return array;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content=content;
	}
}
