package com.zandgall.arvopia.utils;

import java.util.Random;

public class Noise {

	public static Noise n;
	
	static int B = 0x100;
	static int BM = 0xff;

	static int N = 0x1000;
	static int NP = 12; /* 2^N */
	static int NM = 0xfff;

	int[] p;
	double[] g1;
	double[][] g2;
	double[][] g3;

	static int start = 1;

	public static double s_curve(double t) {
		return t * t * (3.0 - 2.0 * t);
	}

	public static double lerp(double t, double a, double b) {
		return a + t * (b - a);
	}

	public static double[] setup(double i) {
		double t = i + N;
		double b0 = ((int) Math.floor(t)) & BM;
		double b1 = (int) (b0 + 1) & BM;
		double r0 = t - Math.floor(t);
		double r1 = r0 - 1.0;

		return new double[] { t, b0, b1, r0, r1 };
	}

	public static double noise1(double arg) {
		return n.get1(arg);
	}

	public Noise(long seed) {
		int i, k;

		Random g = new Random(seed);

		p = new int[B * 3];
		g1 = new double[B * 3];
//		g2 = new double[B * 3][B * 4];
//		g3 = new double[B * 3][B * 6];

		for (i = 0; i < B; i++) {
			p[i] = i;

			g1[i] = ((g.nextDouble() * (B + B)) - B) / B;

//			g2[i] = new double[B * 4];
//			for (j = 0; j < 2; j++)
//				g2[i][j] = ((g.nextDouble() * (B + B)) - B) / B;
//			g2[i] = normalize2(g2[i]);
//
//			g3[i] = new double[B * 6];
//			for (j = 0; j < 3; j++)
//				g3[i][j] = ((g.nextDouble() * (B + B)) - B) / B;
//			g3[i] = normalize3(g3[i]);
		}

		while (i > 0) {
			i--;
			k = p[i];
			p[i] = p[(int) g.nextDouble()];
			p[(int) g.nextDouble()] = k;
		}
		for (i = 0; i < B + 2; i++) {
			p[B + i] = p[i];
			g1[B + i] = g1[i];
//			if (g2[B + i] == null)
//				g2[B + i] = new double[B * 4];
//			for (j = 0; j < 2; j++)
//				g2[B + i][j] = g2[i][j];
//
//			if (g3[B + i] == null)
//				g3[B + i] = new double[B * 6];
//			for (j = 0; j < 3; j++)
//				g3[B + i][j] = g3[i][j];
		}
	}

	public double get1(double arg) {
		double bx0, bx1;
		double rx0, rx1, sx, u, v;
		if (start == 1) {
			start = 0;
		}

		double[] s = setup(arg);

		bx0 = s[1];
		bx1 = s[2];
		rx0 = s[3];
		rx1 = s[4];

		sx = s_curve(rx0);

		u = rx0 * g1[(int) p[(int) bx0]];
		v = rx1 * g1[(int) p[(int) bx1]];

		return lerp(sx, u, v);
	}

	/*
	 * public static double noise2(double x, double y) { double bx0, bx1; double
	 * rx0, rx1, sx, t, u, v; double[] vec = new double[] { x, y}; if (start == 1) {
	 * start = 0; init(); }
	 * 
	 * double[] s = setup(x);
	 * 
	 * bx0 = s[1]; bx1 = s[2]; rx0 = s[3]; rx1 = s[4];
	 * 
	 * sx = s_curve(rx0);
	 * 
	 * u = rx0 * g1[(int) p[(int) bx0]]; v = rx1 * g1[(int) p[(int) bx1]];
	 * 
	 * return lerp(sx, u, v); }
	 * 
	 */

	public static double[] normalize2(double[] v) {
		double s;

		s = Math.sqrt(v[0] * v[0] + v[1] * v[1]);
		v[0] = v[0] / s;
		v[1] = v[1] / s;
		return v;
	}

	public static double[] normalize3(double[] v) {
		double s;

		s = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
		v[0] = v[0] / s;
		v[1] = v[1] / s;
		v[2] = v[2] / s;
		return v;
	}

	public static void init(long seed) {
		n = new Noise(seed);
	}

}
