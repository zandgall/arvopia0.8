package com.zandgall.arvopia.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.transform.Tran;

public class IconButton {

	public int x, y, width, height;

	public BufferedImage icon, shaded, lock;

	Handler game;

	public boolean on;

	public boolean hovered;

	public boolean data;
	
	public boolean locked = false;
	
	public String name, description;

	public IconButton(Handler game, int x, int y, BufferedImage icon) {
		this.game = game;
		this.x = x;
		this.y = y;
		this.icon = icon;

		width = icon.getWidth();
		height = icon.getHeight();
		shaded = Tran.effectColor(icon, new Color(200, 200, 225));
		name="Iconbutton";
		description="Does something when clicked!";
		lock = Tran.staticShader(icon, 250, 150);
	}
	
	public IconButton(Handler game, int x, int y, String name, BufferedImage icon, String description) {
		this.game = game;
		this.x = x;
		this.y = y;
		this.icon = icon;

		width = icon.getWidth();
		height = icon.getHeight();
		shaded = Tran.effectColor(icon, new Color(200, 200, 225));
		this.name=name;
		this.description=description;
		lock = Tran.staticShader(icon, 250, 150);
	}
	
	public void initImages() {
		shaded = Tran.effectColor(icon, new Color(200, 200, 225));
		width = icon.getWidth();
		height = icon.getHeight();
		lock = Tran.staticShader(icon, 250, 150);
	}

	public void tick() {
		int mouseX = game.getMouse().rMouseX();
		int mouseY = game.getMouse().rMouseY();
		boolean mouseLeft = game.getMouse().isLeft();
		boolean mouseRight = game.getMouse().isRight();
		if ((mouseX > x - 1) && (mouseX < x + width + 1) && (mouseY > y - 1) && (mouseY < y + height + 1) && !locked) {
			if ((mouseLeft && game.getMouse().wasClicked())) {
				game.getMouse().setLeftClicked(false);
				on = true;
				data = false;
			} else if (mouseRight) {
				data = true;
				on = false;
			} else {
				hovered = true;
				on = false;
			}
		} else {
			hovered = false;
			on = false;
			data = false;
		}

		if (hovered)
			game.getMouse().changeCursor("HAND");
	}

	public void tick(int mouseX, int mouseY) {
		boolean mouseLeft = game.getMouse().isLeft();
		boolean mouseRight = game.getMouse().isRight();
		if ((mouseX > x - 1) && (mouseX < x + width + 1) && (mouseY > y - 1) && (mouseY < y + height + 1) && !locked) {
			if ((mouseLeft && game.getMouse().wasClicked())) {
				game.getMouse().setLeftClicked(false);
				on = true;
				data = false;
			} else if (mouseRight || game.getMouse().STILLTIMER>100) {
				data = true;
			} else {
				hovered = true;
				on = false;
			}
		} else {
			hovered = false;
			on = false;
			data = false;
		}

		if (hovered)
			game.getMouse().changeCursor("HAND");
	}

	public void render(Graphics g) {
		render((Graphics2D) g);
	}

	public void render(Graphics2D g) {
		if(locked)
			g.drawImage(lock, x, y, null);
		else if (hovered)
			g.drawImage(shaded, x, y, null);
		else
			g.drawImage(icon, x, y, null);
		
		int mouseX = game.getMouse().rMouseX();
		int mouseY = game.getMouse().rMouseY();
		
		if (data) {
			g.setFont(new java.awt.Font("Arial", 0, 12));
			
			int width = Tran.measureString(name+": "+description, g.getFont()).x;
			
			int x = Math.min(mouseX+10, game.getWidth()-width);
			
			g.setColor(java.awt.Color.black);
			g.drawRect(x, mouseY, width +10, 14);
			g.setColor(java.awt.Color.white);
			g.fillRect(x+1, mouseY + 1, width +10 - 1, 13);
			g.setColor(java.awt.Color.black);
			g.drawString(name + ": " + description, x+2, mouseY + 11);
		}
	}

}
