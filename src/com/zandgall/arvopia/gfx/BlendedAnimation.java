package com.zandgall.arvopia.gfx;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.transform.Tran;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BlendedAnimation {
	private Game g;
	private Handler game = new Handler(g);
	private int speed;
	private long blendIndex;
	private BufferedImage[] frames;
	public int frameInt;

	private ArrayList<BufferedImage> blendFrames;

	public BlendedAnimation(int speed, BufferedImage[] frames, String name, String source) {
		speed *= 60.0 / 100.0;
		this.speed = speed;
		this.frames = frames;

		blendIndex = 0;

		blendFrames = new ArrayList<BufferedImage>();

		for (int f = 0; f < frames.length; f++) {
			if(f<frames.length-1)
				for (long i = speed * f; i < speed * (f + 1); i++) {
					System.out.println(((i - speed * f) / (speed + 0.0)));
					blendFrames.add(Tran.blendImages(frames[f], frames[f + 1], (float) ((i - speed * f) / (speed + 0.0))));
				}
			else {
				for (long i = speed * f; i < speed * (f + 1); i++) {
					System.out.println(((i - speed * f) / (speed + 0.0)));
					blendFrames.add(Tran.blendImages(frames[f], frames[0], (float) ((i - speed * f) / (speed + 0.0))));
				}
			}
		}

//		lastTime = System.currentTimeMillis();

		game.logPlayer("\tBlended Animation: " + name + " loaded for " + source);
	}

	public void tick() {
		blendIndex++;

		if (blendIndex >= blendFrames.size()) {
			blendIndex = 0;
		}
	}

	public BufferedImage[] getArray() {
		return frames;
	}

	public ArrayList<BufferedImage> getBlendArray() {
		return blendFrames;
	}

	public BufferedImage getFrame() {
		return blendFrames.get((int) blendIndex);
	}

	public void setFrame(int frame) {
		blendIndex = frame * speed;
	}

	public void setblendFrame(int frame) {
		blendIndex = frame;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
}
