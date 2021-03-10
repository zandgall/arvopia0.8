package com.zandgall.arvopia.utils;

import java.util.ArrayList;

import com.zandgall.arvopia.Game;

public class KeyFrame {
	
	public static final int INSTANT = -1, LINEAR = 0, SINE = 1, CUBIC = 2, QUAD = 3, QUINT = 4, EXPO = 5;  
	public static final int IN = 0, OUT = 1, INOUT = 2;
	
	public static double getValue(ArrayList<KeyFrame> frames, double time) {
		
		if(frames.size()==0) {
			return 0.0;
		}
		
		KeyFrame frame = frames.get(0);
		
		for(KeyFrame f : frames) {
			if(f.timePlace<time)
				frame=f;
			if(f.timePlace>=time)
				return frame.getVal(time, f);
		}
		
		return frame.getVal();
		
	}
	
	double value = 0.0, timePlace = 0.0;
	
	int type = LINEAR, easing = INOUT;
	
	public KeyFrame(double value, double timePlace) {
		this.value=value;
		this.timePlace=timePlace;
	}
	
	public KeyFrame(double value, double timePlace, int type) {
		this.value=value;
		this.timePlace=timePlace;
		this.type=type;
	}
	
	public KeyFrame(double value, double timePlace, int type, int easing) {
		this.value=value;
		this.timePlace=timePlace;
		this.type=type;
		this.easing=easing;
	}
	
	public double getVal() {
		return value;
	}
	
	public double getVal(double time, KeyFrame next) {
		//TODO
		
		double t = time-timePlace;
		double c = value;
		double n = next.value;
		double d = next.timePlace-timePlace;
		
		double out = value;
		
		if(n==c) {
			return n;
		}
		
		if(type==LINEAR)
			out = linear(t, c, n, d);
		if(type==SINE) {
			if(easing==IN)
				out = sineIn(t, c, n, d);
			else if(easing==OUT)
				out = sineOut(t, c, n, d);
			else if(easing==INOUT)
				out = sineInOut(t, c, n, d);
		}
		if(type==CUBIC) {
			if(easing==IN)
				out = cubicIn(t, c, n, d);
			else if(easing==OUT)
				out = cubicOut(t, c, n, d);
			else if(easing==INOUT)
				out = cubicInOut(t, c, n, d);
		}
		
		return out;
	}
	
	/**
	 * @param t Time value (minus the start of the current frame)
	 * @param c The Current frame's value
	 * @param n The Next frame's value
	 * @param d The Duration of the easing
	 * @return The Linear value from the parameters 
	 */
	public static double linear(double t, double c, double n, double d) {
		return (n-c)*r(t/d) + c;
	}
	
	/**
	 * @param t Time value (minus the start of the current frame)
	 * @param c The Current frame's value
	 * @param n The Next frame's value
	 * @param d The Duration of the easing
	 * @return The Sine easing In value from the parameters 
	 */
	public static double sineIn(double t, double c, double n, double d) {
		double f = Math.cos(r(t/d)*Math.PI/2.0);
		
		return (-(n-c) * f) + n;
	}
	
	/** 
	 * @param t Time value (minus the start of the current frame)
	 * @param c The Current frame's value
	 * @param n The Next frame's value
	 * @param d The Duration of the easing
	 * @return The Sine easing Out value from the parameters 
	 */
	public static double sineOut(double t, double c, double n, double d) {
		double f = Math.sin(r(t/d)*Math.PI/2.0);
		
		return ((n-c) * f) + c;
	}
	
	/**
	 * @param t Time value (minus the start of the current frame)
	 * @param c The Current frame's value
	 * @param n The Next frame's value
	 * @param d The Duration of the easing
	 * @return The Sine easing In and Out value from the parameters 
	 */
	public static double sineInOut(double t, double c, double n, double d) {
//		return -c/2 * ((float) Math.cos(Math.PI*t/d) - 1) + n;
		double f = Math.cos(r(t/d)*Math.PI);
		
		return (-(n-c)/2 * (f-1)) + c;
	}
	
	/**
	 * @param t Time value (minus the start of the current frame)
	 * @param c The Current frame's value
	 * @param n The Next frame's value
	 * @param d The Duration of the easing
	 * @return The Cubic easing In value from the parameters 
	 */
	public static double cubicIn(double t, double c, double n, double d) {
		return (n-c)*(t/=d)*t*t + c;
	}
	
	/**
	 * @param t Time value (minus the start of the current frame)
	 * @param c The Current frame's value
	 * @param n The Next frame's value
	 * @param d The Duration of the easing
	 * @return The Cubic easing Out value from the parameters 
	 */
	public static double cubicOut(double t, double c, double n, double d) {
		return (n-c)*((t=t/d-1)*t*t + 1) + c;
	}
	
	/**
	 * @param t Time value (minus the start of the current frame)
	 * @param c The Current frame's value
	 * @param n The Next frame's value
	 * @param d The Duration of the easing
	 * @return The Cubic easing In and Out value from the parameters 
	 */
	public static double cubicInOut(double t, double c, double n, double d) {
		if ((t/=d/2) < 1) return (n-c)/2*t*t*t + c;
		return (n-c)/2*((t-=2)*t*t + 2) + c;
	}
	
	private static double r(double d) {
		return Public.range(0.0, 1.0, d);
	}
	
}
