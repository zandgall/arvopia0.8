package com.zandgall.arvopia.environment;

import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Handler;

import java.awt.Color;
import java.awt.Graphics2D;

public class Light {
	private int x;
	private int y;
	public int Strength;
	public double max, colorDistribution;
	public Color color;
	private boolean on;
	private Handler game;

	public Light(Handler game, int x, int y, int Strength, double max, Color color) {
		this.x = x;
		this.y = y;
		this.Strength = Strength;
		this.max = max;
		this.color = color;
		this.game = game;
		colorDistribution = 1.0;
	}
	
	public Light(Handler game, int x, int y, int Strength, double max, double colorDistribution, Color color) {
		this.x = x;
		this.y = y;
		this.Strength = Strength;
		this.max = max;
		this.color = color;
		this.game = game;
		this.colorDistribution = colorDistribution;
	}

	public void render(Graphics2D g) {
	}

	public double getMax() {
		return max;
	}

	public double colorDistribution() {
		return colorDistribution;
	}
	
	public void setMax(int max) {
		this.max = max;
	}

	public void turnOn() {
		on = true;
	}

	public void turnOff() {
		on = false;
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
		Console.log("Set on", on, this);
//		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
//		  for (int i = 1; i < elements.length; i++) {
//		    StackTraceElement s = elements[i];
//		    System.out.println("\tat " + s.getClassName() + "." + s.getMethodName()
//		        + "(" + s.getFileName() + ":" + s.getLineNumber() + ")");
//		  }

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

	public void setY(int y) {
		this.y = y;
	}

	public int getStrength() {
		return Strength;
	}

	public void setStrength(int strength) {
		Strength = strength;
	}
}
