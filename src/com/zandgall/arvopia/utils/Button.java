package com.zandgall.arvopia.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import com.zandgall.arvopia.ArvopiaLauncher;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;

public class Button implements Serializable {
	private static final long serialVersionUID = 2634486299400608942L;
	
	com.zandgall.arvopia.Handler game;
	private int x;
	private int y;
	private int width;
	private int height;
	private int mouseX;
	private int mouseY;
	private boolean mouseLeft;
	private boolean mouseRight;
	public boolean on;
	public boolean data;
	public boolean image;
	public boolean hovered;
	public boolean locked = false;
	private String description;
	private String name;

	BufferedImage h, o, d, l;

	public Button(com.zandgall.arvopia.Handler game, int x, int y, int width, int height, String description,
			String name) {
		
		ArvopiaLauncher.game.handler.addSound("Sounds/button.ogg", "button", false, 0, 0, 0);
		
		this.x = x;
		this.y = y;
		this.width = width + 12;
		this.height = height + 12;
		this.game = ArvopiaLauncher.game.handler;
		this.description = description;
		this.name = name;

		initImage();
	}

	public Button(Handler game, int x, int y, String description, String name) {
		this(game, x, y, Tran.measureString(name, Public.fipps).x, Public.fipps.getSize(), description, name);
	}

	public Button(Handler game, int x, int y, int width, int height, String description, String name, int type) {
		this(game, x, y, width, height, description, name);

		if (type == 1)
			this.x = x - width / 2;
	}

	public Button(Handler game, int x, int y, String description, String name, int type) {
		this(game, x, y, 1, 25, description, name);
		width = Tran.measureString(name, Public.fipps).x + 12;
		height = Public.fipps.getSize() + 12;

		if (type == 1)
			this.x = x - width / 2;

		initImage();
	}

	public void initImage() {
		BufferedImage corners = ImageLoader.loadImage("/textures/Gui/sign2.png");
		
		int i = corners.getWidth()/2;
		
		h = new BufferedImage(width+6, height+6, BufferedImage.TYPE_4BYTE_ABGR);
//		h = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = h.createGraphics();

		g.setRenderingHints(Tran.antialias);

//		g.setColor(Color.white);
//		g.fillRoundRect(0, 0, width, height, 5, 5);
//		g.setColor(Color.gray);
//		g.drawRoundRect(0, 0, width - 1, height - 1, 5, 5);
		
		int j = h.getWidth(), k = h.getHeight();
		
		g.drawImage(corners.getSubimage(0, 0, i, i), 0, 0, null);
		g.drawImage(corners.getSubimage(i, 0, i, i), j - i, 0, null);
		g.drawImage(corners.getSubimage(i, i, i, i), j - i, k - i, null);
		g.drawImage(corners.getSubimage(0, i, i, i), 0, k - i, null);
		g.drawImage(corners.getSubimage(i-1, 0, 1, i), i, 0, j - i * 2, i, null);
		g.drawImage(corners.getSubimage(i-1, i, 1, i), i, k - i, j - i * 2, i, null);
		g.drawImage(corners.getSubimage(0, i - 1, i, 1), 0, i, i, k - i * 2, null);
		g.drawImage(corners.getSubimage(i, i - 1, i, 1), j - i, i, i, k - i * 2, null);
		g.drawImage(corners.getSubimage(i - 1, i - 1, 2, 2), i, i, j - i * 2, k - i * 2, null);

		g.dispose();

//		o = Tran.bevelShade(h, 5, 150, 50);
//		d = Tran.bevelShade(h, 5, 250, 100);
//		h = Tran.bevelShade(h, 5, 255, 220);

		o = Tran.staticShader(h, 100, 0);
		d = Tran.staticShader(h, 200, -50);
		l = Tran.staticShader(h, 200, 125);
		h = Tran.staticShader(h, 255, 0);
	}

	public void tick() {
		mouseX = game.getMouse().rMouseX();
		mouseY = game.getMouse().rMouseY();
		mouseRight = game.getMouse().isRight();
		
		boolean react = game.getMouse().isLeftClicked();
		
		//Vector3D f = game.soundSystem.getListenerData().position;
		//game.setPosition("button", (int) f.x, (int) f.y, (int) f.z);
		on = false;
		if ((mouseX > x - 1) && (mouseX < x + width + 1) && (mouseY > y - 1) && (mouseY < y + height + 1)) {
			if (react) {
				game.getMouse().setLeftClicked(false);
				on = true;
		//		game.soundSystem.stop("button");
				game.play("button");
				data = false;
			} else if ((mouseRight && game.getMouse().wasClicked()) || game.getMouse().STILLTIMER>100) {
				game.getMouse().setLeftClicked(false);
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
		
		mouseLeft = game.getMouse().fullLeft;

		if (hovered)
			game.getMouse().changeCursor("HAND");
	}
	
	public void tick(int mouseX, int mouseY) {
		mouseLeft = game.getMouse().isLeft();
		mouseRight = game.getMouse().isRight();
		//Vector3D f = game.soundSystem.getListenerData().position;
		//game.setPosition("button", (int) f.x, (int) f.y, (int) f.z);
		if ((mouseX > x - 1) && (mouseX < x + width + 1) && (mouseY > y - 1) && (mouseY < y + height + 1)) {
			if ((mouseLeft && game.getMouse().wasClicked())) {
				game.getMouse().setLeftClicked(false);
				on = true;
				//game.soundSystem.stop("button");
				game.play("button");
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

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}

	public void render(Graphics g) {

		render((Graphics2D) g);

	}

	public void render(Graphics2D g) {
		g.setColor(Color.black);
		g.setFont(Public.fipps);
		int off = Tran.measureString(name, Public.fipps).x;
		if (locked) {
			g.drawImage(l, x - 3, y - 3, null);
			g.setColor(Color.gray);
			g.drawString(name, x + width / 2 - off / 2, y + height - 7);
		} else if (hovered && game.getMouse().fullLeft) {
			g.drawImage(o, x - 3, y - 3, null);

			g.drawString(name, x + width / 2 - off / 2, y + height - 7);
		} else if (hovered) {
			g.drawImage(h, x - 3, y - 3, null);
			
			g.setColor(Color.white);
			g.drawString(name, x + width / 2 - off / 2, y + height - 7);
			g.setColor(new java.awt.Color(200, 200, 200, 20));
			g.fillOval(mouseX - 10, mouseY - 10, 20, 20);
		} else {
			g.drawImage(d, x - 3, y - 3, null);

			g.drawString(name, x + width / 2 - off / 2, y + height - 7);
		}

		if (data) {
			g.setFont(new java.awt.Font("Arial", 0, 12));
			
			int width = Tran.measureString(name+": "+description, g.getFont()).x;
			
			int x = Math.min(mouseX+10, game.getWidth()-width);
			
			g.setColor(java.awt.Color.black);
			g.drawRect(x-width/2, mouseY - 11, width +10, 14);
			g.setColor(java.awt.Color.white);
			g.fillRect(x+1-width/2, mouseY - 10, width +10 - 1, 13);
			g.setColor(java.awt.Color.black);
			g.drawString(name + ": " + description, x+2-width/2, mouseY);
		}
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}
	
	public int getWidth() {
		return h.getWidth();
	}
	
	public int getHeight() {
		return h.getHeight();
	}

	
	public void render(Graphics2D g, int i, int j) {
		g.setColor(Color.black);
		g.setFont(Public.fipps);
		int off = Tran.measureString(name, Public.fipps).x;
		if (locked) {
			g.drawImage(l, x - 3 + i, y - 3 + j, null);
			g.setColor(Color.gray);
			g.drawString(name, x + width / 2 - off / 2 + i, y + height - 7 + j);
		} else if (hovered && game.getMouse().fullLeft) {
			g.drawImage(o, x - 3 + i, y - 3 + j, null);

			g.drawString(name, x + width / 2 - off / 2 + i, y + height - 7 + j);
		} else if (hovered) {
			g.drawImage(h, x - 3 + i, y - 3 + j, null);
			
			g.setColor(Color.white);
			g.drawString(name, x + width / 2 - off / 2 + i, y + height - 7 + j);
			g.setColor(new java.awt.Color(200, 200, 200, 20));
			g.fillOval(mouseX - 10 + i, mouseY - 10 + j, 20, 20);
		} else {
			g.drawImage(d, x - 3 + i, y - 3 + j, null);

			g.drawString(name, x + width / 2 - off / 2 + i, y + height - 7 + j);
		}

		if (data) {
			g.setFont(new java.awt.Font("Arial", 0, 12));
			
			int width = Tran.measureString(name+": "+description, g.getFont()).x;
			
			int x = Math.min(mouseX+10, game.getWidth()-width);
			
			g.setColor(java.awt.Color.black);
			g.drawRect(x-width/2 + i, mouseY - 11 + j, width +10, 14);
			g.setColor(java.awt.Color.white);
			g.fillRect(x+1-width/2 + i, mouseY - 10 + j, width +10 - 1, 13);
			g.setColor(java.awt.Color.black);
			g.drawString(name + ": " + description, x+2-width/2 + i, mouseY + j);
		}
	}
}
