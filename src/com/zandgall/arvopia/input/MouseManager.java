package com.zandgall.arvopia.input;

import com.zandgall.arvopia.ArvopiaLauncher;
import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseManager
		implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.awt.event.MouseWheelListener {
	private Handler game;
	private boolean left;
	private boolean right;
	private boolean dragged;
	private boolean in;
	private boolean clicked;
	private boolean held;
	private boolean prevHeld;
	private boolean sliderMalfunc;
	public boolean fullLeft, fullRight;
	private double mouseX;
	private double mouseY;
	private double mouseScroll;
	private long timer = 0L;
	private boolean wasClicked = false;

	public long STILLTIMER = 0;
	
	public MouseManager(Handler game) {
		this.game = game;
	}

	public String name;

	public void changeCursor(String name) {
		this.name = name;
	}

	public boolean isLeft() {
		return left;
	}

	public boolean isRight() {
		return right;
	}

	public int getMouseX() {
		try {
			return (int) (((mouseX/Game.resizingx - game.getWidth() / 2) / Game.scale + game.getWidth() / 2));
		} catch (NullPointerException e) {
			System.out.println("Error: " + e);
		}
		return 0;
	}

	public int fullMouseX() {
		return (int) (getMouseX()/Game.resizingx + game.xOffset());
	}

	public int rMouseX() {
		if(ArvopiaLauncher.game.useResize)
			return (int) (mouseX/Game.resizingx);
		return (int) (mouseX);
	}

	public int rMouseY() {
		if(ArvopiaLauncher.game.useResize)
			return (int) (mouseY/Game.resizingy);
		return (int) (mouseY);
	}

	public int getMouseY() {
		try {
			return (int) (((mouseY/Game.resizingy - game.getHeight() / 2) / Game.scale + game.getHeight() / 2));
		} catch (NullPointerException e) {
			System.out.println("Error: " + e);
		}
		return 0;
	}

	public int fullMouseY() {
		return (int) (getMouseY()/Game.resizingy + game.yOffset());
	}

	public boolean isDragged() {
		return dragged;
	}

	public boolean isHeld() {
		return held;
	}

	public void setDragged(boolean dragged) {
		this.dragged = dragged;
	}

	public boolean isClicked() {
//		if(clicked) {
//			clicked = false;
//			return true;
//		}
		return clicked;
	}

	public boolean wasClicked() {
		return isClicked();
	}
	
	public void setClicked(boolean clicked) {
		this.clicked = clicked;
		wasClicked = false;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isIn() {
		return in;
	}

	public void visualize(Graphics g) {
		g.setColor(java.awt.Color.black);
		g.fillRect(game.getWidth() - 160, game.getHeight() - 90, 160, 90);
		g.setColor(java.awt.Color.green);
		g.fillRect((int) (rMouseX() / 4.5D + game.getWidth() - 160.0D),
				(int) (rMouseY() / 4.5D + game.getHeight() - 90.0D), 2, 2);
	}

	public void tick() {
		if ((timer % 100L == 0L) && (prevHeld)) {
			if (left) {
				held = true;
			} else {
				prevHeld = false;
			}
		}

		if ((!clicked) && (!dragged)) {
			left = false;
			right = false;
		}

		mouseScroll *= 0.8;

		timer += 1L;
		wasClicked = clicked;
//		if(wasClicked)
			clicked = false;

		STILLTIMER++;
			
		if (timer >= 1000000L) {
			timer = 0L;
		}

		if (name == "WAIT") {
			game.setCursor(Cursor.getPredefinedCursor(3));
		} else if (name == "HAND") {
			game.setCursor(Cursor.getPredefinedCursor(12));
		} else if (name == "TYPE")
			game.setCursor(Cursor.getPredefinedCursor(2));
		else
			game.setCursor(Cursor.getPredefinedCursor(0));

	}

	public void setSliderMalfunction(boolean tf) {
		sliderMalfunc = tf;
	}

	public void setHandler(Handler game) {
		this.game = game;
	}
	
	public boolean touches(int minx, int miny, int maxx, int maxy) {
//		Console.log(Game.resizingx, Game.resizingy);
		return mouseX>minx&&mouseX<maxx&&mouseY>miny&&mouseY<maxy;
	}

	public void mouseDragged(MouseEvent e) {
		STILLTIMER=0;
		dragged = true;
		mouseX = e.getX();
		mouseY = e.getY();

		if (sliderMalfunc) {
			int b1 = 1024;
			int b2 = 2048;
			if ((e.getModifiersEx() & (b1 | b2)) == b1) {
				left = true;
			} else if ((e.getModifiersEx() & (b1 | b2)) == b2) {
				right = true;
			}
		} else if (e.getButton() == 1) {
			left = true;
		} else if (e.getButton() == 3) {
			right = true;
		}
	}

	public void mouseMoved(MouseEvent e) {
		STILLTIMER=0;
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void mouseClicked(MouseEvent e) {
		wasClicked = true;
		clicked = true;
		if (e.getButton() == 1) {
			left = true;
		} else if (e.getButton() == 3) {
			right = true;
		}

		game.log("Mouse clicked: (" + rMouseX() + ", " + rMouseY() + ")");
	}

	public void mouseEntered(MouseEvent e) {
		in = true;
	}

	public void mouseExited(MouseEvent e) {
		in = false;
	}

	public void mousePressed(MouseEvent e) {
		if(!wasClicked)
			wasClicked = true;
		if (e.getButton() == 1) {
			left = true;
			fullLeft = true;
		} else if (e.getButton() == 3) {
			right = true;
			fullRight = true;
		}
	}

	public void mouseReleased(MouseEvent e) {
//		clicked = false;
		dragged = false;
		if (e.getButton() == 1) {
			left = false;
			fullLeft = false;
		} else if (e.getButton() == 3) {
			right = false;
			fullRight = false;
		}
	}

	public void resets() {
//		clicked = false;
		dragged = false;
		left = false;
		right = false;
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		mouseScroll = e.getWheelRotation();
		game.log("Mouse Scrolled: " + mouseScroll);
	}

	public double getMouseScroll() {
		return mouseScroll;
	}
}
