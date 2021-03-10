package com.zandgall.arvopia.gfx;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.Public;

import java.awt.image.BufferedImage;

public class Animation {
	private Game g;
	private Handler game = new Handler(g);
	private int resetTime;
	double speed = 0;
	public double index;
	private long lastTime;
	private long timer;
	private BufferedImage[] frames;
	public int frameInt;
	
	public boolean justLooped = false;

	public static Animation getStill(BufferedImage still) {
		return new Animation(10000, new BufferedImage[] {still}, "Still", "Still");
	}
	
	public static Animation getStill(BufferedImage still, String name, String source) {
		return new Animation(10000, new BufferedImage[] {still}, name, source);
	}
	
	public Animation(int speed, BufferedImage[] frames, String name, String source) {
		this.resetTime = (int) Public.random(speed*0.9, speed*1.1);
		this.frames = frames;

		index = 0;

		timer = 0L;
		lastTime = System.currentTimeMillis();

		game.logPlayer("\tAnimation: " + name + " loaded for " + source);
	}
	
	public Animation(double speed, BufferedImage[] frames, String name, String source) {
		this.speed = Public.random(speed*0.9, speed*1.1);
		this.frames = frames;

		index = 0;

		timer = 0L;
		lastTime = System.currentTimeMillis();

		game.logPlayer("\tAnimation: " + name + " loaded for " + source);
	}

	public void tick() {
		timer += System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();

		if(speed==0) {
			if (timer > resetTime) {
				index += 1;
				timer = 0L;
				justLooped=(index >= frames.length);
				if (justLooped) {
					index = 0;
				}
	
				frameInt = (int) index;
			}
		} else {
			index+=speed;
			justLooped=(index >= frames.length);
			if (justLooped)
				index = 0;
			frameInt = (int) index;
		}
			
	}

	public BufferedImage[] getArray() {
		return frames;
	}

	public BufferedImage getFrame() {
		index = Public.range(0, frames.length-1, index);
		return frames[(int) index];
	}

	public void setFrame(int frame) {
		index = frame;
	}
	
	public void setImage(int frame, BufferedImage image) {
		frames[frame] = image;
	}
	
	public int getIndex() {
		return (int) index;
	}

	public void setResetTime(int speed) {
		this.resetTime = speed;
	}
	
	public void setSpeed(double speed) {
		this.speed=speed;
	}

	public Animation flipped() {
		BufferedImage[] fframes = new BufferedImage[frames.length];
		
		for(int i = 0; i<fframes.length; i++) {
			fframes[i]=Tran.flip(frames[i], -1, 1);
		}
		
		return new Animation(this.speed, fframes, "", "");
	}
}
