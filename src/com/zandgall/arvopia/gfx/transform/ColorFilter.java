package com.zandgall.arvopia.gfx.transform;

import java.awt.Color;
import java.awt.image.BufferedImage;

public abstract class ColorFilter {

	public static final int NONE = -1, MAX = 0, AVERAGE = 1, TOTALED = 2, MAX_MULT = 3, MAPPED = 4;

	protected int mode;

	public abstract Color filter(int x, int y, BufferedImage src, int rgbi);

	public void setMode(int mode) {
		this.mode = mode;
	}

}
