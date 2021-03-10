package com.zandgall.arvopia.utils;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.transform.Tran;

import java.awt.Color;
import java.awt.Font;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Public {
	private static Handler game;
	public static Font digital, fipps, button, runescape;
	public static Font defaultFont;
	public static Font defaultBoldFont;

	public static double dist(double x1, double y1, double x2, double y2) {
		double d = Math.sqrt(Math.pow(x2 - x1, 2.0D) + Math.pow(y2 - y1, 2.0D));
		return Math.abs(d);
	}

	public static double difference(double num1, double num2) {
		return Math.max(num1, num2) - Math.min(num1, num2);
	}

	public static double range(double min, double max, double num) {
		return Math.min(max, Math.max(num, min));
	}

	public static Object identifyRange(double min, double max, double in, Object identifier) {
		if (range(min, max, in) != in) {
			return identifier;
		}
		return Double.valueOf(in);
	}

	public static boolean identifyRange(double min, double max, double in) {
		if (range(min, max, in) != in) {
			return false;
		}
		return true;
	}

	public static boolean chance(double percent) {
		return Math.random() < percent / 100.0D;
	}

	public static int grid(double in, double every, double offset) {
		return (int) Math.floor(in / every);
	}

	public static double zoomX(double num) {
		return num;
	}

	public static double zoomY(double num) {
		return num;
	}

	public static Color blend(Color c1, Color c2, float ratio) {
		if (ratio > 1.0F) {
			ratio = 1.0F;
		} else if (ratio < 0.0F)
			ratio = 0.0F;
		float iRatio = 1.0F - ratio;

		int i1 = c1.getRGB();
		int i2 = c2.getRGB();

		int a1 = i1 >> 24 & 0xFF;
		int r1 = (i1 & 0xFF0000) >> 16;
		int g1 = (i1 & 0xFF00) >> 8;
		int b1 = i1 & 0xFF;

		int a2 = i2 >> 24 & 0xFF;
		int r2 = (i2 & 0xFF0000) >> 16;
		int g2 = (i2 & 0xFF00) >> 8;
		int b2 = i2 & 0xFF;

		int a = (int) (a1 * iRatio + a2 * ratio);
		int r = (int) (r1 * iRatio + r2 * ratio);
		int g = (int) (g1 * iRatio + g2 * ratio);
		int b = (int) (b1 * iRatio + b2 * ratio);

		return new Color(r, g, b, a);
	}

	public static Color blend(Color c1, Color c2, float ratio, boolean includeAlpha) {
		if (ratio > 1.0F) {
			ratio = 1.0F;
		} else if (ratio < 0.0F)
			ratio = 0.0F;
		float iRatio = 1.0F - ratio;

		int i1 = c1.getRGB();
		int i2 = c2.getRGB();

		int a1 = i1 >> 24 & 0xFF;
		int r1 = (i1 & 0xFF0000) >> 16;
		int g1 = (i1 & 0xFF00) >> 8;
		int b1 = i1 & 0xFF;

		int a2 = i2 >> 24 & 0xFF;
		int r2 = (i2 & 0xFF0000) >> 16;
		int g2 = (i2 & 0xFF00) >> 8;
		int b2 = i2 & 0xFF;

		int a = (int) (a1 * iRatio + a2 * ratio);
		int r = (int) (r1 * iRatio + r2 * ratio);
		int g = (int) (g1 * iRatio + g2 * ratio);
		int b = (int) (b1 * iRatio + b2 * ratio);

		if (!includeAlpha) {
			a = Math.min(a1, a2);

			if (a1 == 0 || a2 == 0) {
				r = Math.min(r1, r2);
				g = Math.min(g1, g2);
				b = Math.min(b1, b2);
			}
		}
		return new Color(r, g, b, a);
	}

	public static Color invert(Color c) {
		return new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
	}

	public static double random(double min, double max) {
		return Math.random() * (max - min + 0.9999D) + min;
	}

	public static double debugRandom(double min, double max) {
		return Math.random() * (max - min) + min;
	}

	public static double wrap(double max, double num) {
		return num - (grid(num, max, 0) * max);
	}

	public static double wrap(double min, double max, double num) {
		return (num - min) - (grid(num, max, 0) * max) + min;
	}

	public static double Map(double Input, double InputHigh, double InputLow, double OutputHigh, double OutputLow) {
		return (Input - InputLow) / (InputHigh - InputLow) * (OutputHigh - OutputLow) + OutputLow;
	}

	public static boolean over(double Input, double OutputHigh, double OutputLow, double percentage) {
		return Input > (OutputHigh - OutputLow) * percentage;
	}

	public static float[] normalize(float x, float y) {

		float max = (float) Math.sqrt(x * x + y * y);

		return new float[] { x * max, y * max };
	}
	
	public static double[] normalize(double x, double y) {
		double max = Math.sqrt(x*x + y*y);
		
		return new double[] {x*max, y*max};
	}
	
	public static String limit(String src, int limit, Font basis) {
		String out = "", measure = "a";
		
		char[] chars = src.toCharArray();
		
		if(Tran.measureString(src, basis).x<limit)
			return src;
		
		for(int i = 0; i<chars.length; i++) {
			measure += chars[i];
			if(Tran.measureString(measure, basis).x>=limit) {
				measure="a";
				boolean found = false;
				for(int j = i; j>=1; j--) {
					if(chars[j]==' ') {
						String pre = (String) src.subSequence(0, j), end = src.substring(j);
						return pre+"\n"+limit(end, limit, basis);
					} else if(chars[j]=='\n') {
						String pre = (String) src.subSequence(0, j), end = src.substring(j);
						return pre+limit(end, limit, basis);
					}
				}
				if(!found) {
					if(src.length()<=2)
						return src;
						
					String pre = (String) src.subSequence(0, i-1), end = src.substring(i-1);
					return pre+"\n"+limit(end, limit, basis);
				}
					
			}
		}

		return out;
	}

	public static void init(Handler handler) {
		game = handler;
		try {
			digital = Font.createFont(0, new java.io.File("Fonts/Digital-Regular.ttf"));
			digital = digital.deriveFont(1, 12.0F);
		} catch (java.awt.FontFormatException | java.io.IOException e) {
			e.printStackTrace();
			digital = new Font("Arial", 1, 12);
		}

		try {
			runescape = Font.createFont(0, new java.io.File("Fonts/basicbit.ttf"));
			runescape = runescape.deriveFont(1, 12.0F);
		} catch (java.awt.FontFormatException | java.io.IOException e) {
			e.printStackTrace();
			digital = new Font("Arial", 1, 12);
		}

//		try {
//			fipps = Font.createFont(0, new java.io.File("Fonts/Fipps-Regular.otf"));
//			fipps = fipps.deriveFont(1, 12.0F);
//		} catch (java.awt.FontFormatException | java.io.IOException e) {
//			e.printStackTrace();
//			fipps = new Font("Arial", 1, 12);
//		}

		fipps = new Font("Century Gothic", Font.PLAIN, 20);
		defaultFont = new Font("Arial", 0, 12);
		defaultBoldFont = new Font("Arial", Font.BOLD, 12);
	}

	public static String getCurrentDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

		Date date = new Date();

		String dateString = formatter.format(date);

		return dateString;
	}

	public static double xO(double x) {
		return x - game.getGameCamera().getxOffset();
//		return x;
	}

	public static double yO(double y) {
		return y - game.getGameCamera().getyOffset();
//		return y;
	}

	public static double[] angle(double rotation) {
		double[] output = { 0.0D, 0.0D };
		output[0] = Math.cos(rotation * 0.017453292519943295D);
		output[1] = Math.sin(rotation * 0.017453292519943295D);
		return output;
	}

	public static double reflect(double rotation, double normal) {
		normal *= 2.0D;
		double y = Math.abs(rotation + 180.0D - normal + 180.0D);
		return 180.0D - y;
	}
	
	public static Object[] clone(Object... o) {
		Object[] out = new Object[o.length];
		for(int i = 0; i<o.length; i++) {
			out[i]=o[i];
		}
		
		return out;
	}
	
}
