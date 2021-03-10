package com.zandgall.arvopia.utils;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.transform.Tran;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
		boolean[] keys = game.getKeyManager().keys;

		if (game.getMouse().fullLeft) {
			int x = game.getMouse().rMouseX();
			int y = game.getMouse().rMouseY();
			if ((x >= this.x) && (x <= this.x + width) && (y >= this.y) && (y <= this.y + height))
				selected = true;
			else {
				selected = false;
			}
		}
		
		trippedtimer += 1L;
		trippin = Math.sin(trippedtimer/8)>0 && selected;
		
		if (game.getKeyManager().existantTyped && (selected)) {
			if(game.getKeyManager().keys[39]) {
				System.out.println("Moving index");
				fullIndex+=1;
				trippin = true;
				trippedtimer = 8;
			} else if(game.getKeyManager().keys[37]) {
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
				} else if ((game.getKeyManager().keys[10])) {
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
				} else if ((Character.isDefined(game.getKeyManager().getNameOfKey().charValue()) && !game.getKeyManager().getNameOfKey().toString().equals("")) && !(keys[8]||keys[10]||keys[27]||keys[127])) {
	//				if(Tran.measureString(content+game.getKeyManager().getNameOfKey().toString()+"|", font).x+5<width) {
	//					content += game.getKeyManager().getNameOfKey().toString();
						String oc = content;
						String first = oc.substring(0, index);
						String last = oc.substring(index);
						content = first+game.getKeyManager().getNameOfKey().toString() + last;
						
						fullIndex++;
	//				}
				}
			}
			typeTimer=0;
		}
		typeTimer++;
		
//		System.out.println(game.getKeyManager().keys[37] + ", " + game.getKeyManager().existantTyped);
		if (selected) {
			game.getMouse().changeCursor("TYPE");
			
//			game.getKeyManager().up=false;
//			game.getKeyManager().down=false;
//			game.getKeyManager().left=false;
//			game.getKeyManager().right=false;
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
//		for (int i = 0; i < getGameContent().length; i++) {
//			g.drawString(getGameContent()[i], x + 5, (int) (y + textOffset*0.75) + i * textOffset);
//		}
		Tran.drawString(g, Public.limit(getContent(), width, font), x+5, (int) (y+textOffset));
		if(trippin) { 
			String[] fullStrings = Public.limit(getContent().substring(0, Math.min(index, content.length())), width, font).split("\n");
			int offset = Tran.measureString(fullStrings[fullStrings.length-1], font).x;
			int offsety = fullStrings.length;
			g.drawLine(x+5+offset, y+textOffset*(offsety-1)+2, x+5+offset, y+textOffset*(offsety)+2);
		}
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

	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content=content;
	}
}
