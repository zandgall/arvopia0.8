package com.zandgall.arvopia.gfx;

import java.awt.image.BufferedImage;

public class Accessory {
	
	public int x, y, orX, orY;
	
	public String parent;
	
	public SerialImage image;
	
	public Accessory(int x, int y, int orX, int orY, String parent, SerialImage image) {
		this.x=x;
		this.y=y;
		this.orX=orX;
		this.orY=orY;
		this.parent=parent;
		this.image=image;
	}
}
