package com.zandgall.arvopia.guis;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.transform.Tran;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class InventoryItem {
	private Handler game;
	private int x;
	private int y;
	private String name;
	int amount;
	int movedX;
	int movedY;
	boolean used;
	public int customCraftingMessage = 255;
	public int customCraftingInt;
	public static InventoryItem TAKEN;
	private BufferedImage texture;
	private boolean hovered;

	public InventoryItem(Handler game, BufferedImage texture, int x, int y, int amount, String name) {
		this.texture = texture;
		this.x = (x);
		this.y = (y);

		this.name = name;

		this.amount = amount;
		hovered = false;

		this.game = game;
	}
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name; 
	}

	public void tick(int amount) {
		this.amount = amount;

		int mouseX = game.getMouse().rMouseX();
		int mouseY = game.getMouse().rMouseY();

		hovered = ((mouseX > x) && (mouseX < x + 18) && (mouseY > y) && (mouseY < y + 18));

//		if (left) {
//			if (TAKEN == this) {
//				TAKEN = this;
//				setX(mouseX - 9);
//				y = (mouseY - 9);
//				movedX = x;
//				movedY = y;
//			} else if ((TAKEN == null) && (hovered)) {
//				TAKEN = this;
//			}
//		} else if ((movedX == getX()) && (movedY == y)) {
//			if (TAKEN == this) {
//				TAKEN = null;
//			}
//			x = ((int) Public.range(0.0D, game.getWidth(), preX));
//			y = preY;
//
//		} else if (TAKEN == this) {
//			TAKEN = null;
//		}
	}

	public void render(Graphics g) {
		if (hovered) {
			g.setColor(Color.black);
			g.drawRect(getX(), y, 20, 20);
		}
		g.drawImage(texture, x, y, null);
		g.setColor(new Color(0, 0, 0, 200));
		g.setFont(new Font("Dialog", 1, 12));
		if (amount >= 0) {
			g.drawString("" + amount, x + 1, y + 16);
		}
	}

	public void render(Graphics g, int x, int y, int width, int height) {
		if (hovered) {
			g.setColor(Color.black);
			g.drawRect(x, y, width, width);
		}
		g.drawImage(texture, x, y, width, height, null);
//		g.setColor(new Color(0, 0, 0, 200));
		g.setFont(new Font("Dialog", 1, 12));
		if (amount >= 0)
			Tran.drawOutlinedText(g, x + 1, y + 16, ""+amount, 1, Color.black, Color.white);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}
}
