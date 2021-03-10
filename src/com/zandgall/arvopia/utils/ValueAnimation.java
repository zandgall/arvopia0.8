package com.zandgall.arvopia.utils;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import java.io.Serializable;

public class ValueAnimation implements Serializable {
	
	private static final long serialVersionUID = -126030786437368807L;
	
	private Game g;
	private Handler game = new Handler(g);
	private double speed,index, length;
	public int frameInt;

	public ValueAnimation(double speed, double length, String name, String source) {
		this.speed = speed;
		
		this.length=length;
		
		index = 0;
		
		game.logPlayer("\tAnimation: " + name + " loaded for " + source);
	}
	
	public ValueAnimation(double speed, double length) {
		this.speed=speed;
		this.length=length;
		index=0;
	}

	public void tick() {
		
		index+=speed/60.0;
		
		if(index>length)
			index=0;
		
	}
	
	public double value() {
		return index;
	}
	
	public int intValue() {
		return (int) index;
	}

	public void setValue(int frame) {
		index = frame;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}
}
