package com.zandgall.arvopia.gfx;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.Public;

public class SerialImage implements Serializable {

	private static final long serialVersionUID = 421L;

	public static final int NEAREST_NEIGHBOR = 0, BILINEAR = 1;

	public static int mode = 1;

	public int width, height;

	int[][] data;

	public SerialImage(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();

		data = new int[width][height];
		data = getRGB(image);
	}

	public SerialImage(int[][] items) {
		width = items.length;
		if (width == 0)
			height = 0;
		else
			height = items[width - 1].length;

		data = items;
	}
	
	public SerialImage(int width, int height) {
		this.width=width;
		this.height=height;
		data = new int[width][height];
		for(int i = 0; i<width; i++) {
			for(int j = 0; j<height; j++) {
				data[i][j] = Tran.accurateColor(new Color(0, 0, 0, 0));
			}
		}
	}

	public BufferedImage toImage() {
		return getImg(data);
	}

	public void normalize() {
		int w = data.length;
		int h = data[0].length;
		int mx = Math.max(w, h);
		int mn = Math.min(w, h);
		int off = Math.abs(w - h) / 2;
		int[][] out = new int[mx][mx];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (mn == w)
					out[x + off][y] = data[x][y];
				else if (mn == h)
					out[x][y + off] = data[x][y];
			}
		}
		this.data = out;
		width = out.length;
		height = out[0].length;
	}

	public int[][] getSpace(int space) {
		int w = data.length;
		int h = data[0].length;
		int mx = Math.max(w, h);
		int mn = Math.min(w, h);
		int off = Math.abs(w - h) / 2;
		int[][] out = new int[mx + space][mx + space];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {

				int ix = x - space / 2, iy = y - space / 2;

				if (ix < 0 || ix >= data.length || iy < 0 || iy >= data[0].length)
					continue;

				if (mn == w)
					out[x + off][y] = data[ix][iy];
				else if (mn == h)
					out[x][y + off] = data[ix][iy];
			}
		}
		return out;
	}

	public SerialImage space(int space) {
		return new SerialImage(getSpace(space));
	}

	public int[][] getSpaceTo(int width, int height) {
		int offX, offY;
		
		int[][] out = new int[width][height];
		
		double xo = (width/2.0 - this.width/2.0), yo = (height/2.0 - this.height/2.0);
		
		offX = (int) Math.floor(xo);
		offY = (int) Math.floor(yo);
		
		for(int x = offX; x<this.width+offX; x++) {
			for(int y = offY; y<this.height+offY; y++) {
				out[x][y] = data[x-offX][y-offY];
			}
		}
		
		return out;
	}
	
	public SerialImage spaceTo(int width, int height) {
		return new SerialImage(getSpaceTo(width, height));
	}
	
	public int[][] getSpaceTo(int offX, int offY, int width, int height) {
		int[][] out = new int[width][height];
		
		offX = (int) Public.range(0, width-this.width, offX);
		offY = (int) Public.range(0, height-this.height, offY);
		
		for(int x = offX; x<this.width+offX; x++) {
			for(int y = offY; y<this.height+offY; y++) {
				out[x][y] = data[x-offX][y-offY];
			}
		}
		
		return out;
	}
	
	public void put(int x, int y, Color c) {
		if(x<data.length&&y<data[x].length)
			data[x][y] = Tran.accurateColor(c);
	}
	
	public SerialImage spaceTo(int offX, int offY, int width, int height) {
		return new SerialImage(getSpaceTo(offX, offY, width, height));
	}
	
	
	public static int[][] getRGB(BufferedImage image) {
		int w = image.getWidth(), h = image.getHeight();
		int[][] out = new int[w][h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				out[x][y] = image.getRGB(x, y);
			}
		}
		return out;
	}

	public static BufferedImage getImg(int[][] rgb) {
		if (rgb.length == 0)
			return null;
		BufferedImage image = new BufferedImage(rgb.length, rgb[0].length, BufferedImage.TYPE_4BYTE_ABGR);

		for (int x = 0; x < rgb.length; x++) {
			for (int y = 0; y < rgb[x].length; y++) {
				image.setRGB(x, y, rgb[x][y]);
			}
		}

		return image;

	}

	public int[][] getFlip(int w, int h) {
		int[][] predata = new int[data.length][data[0].length];
		for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[x].length; y++) {
				predata[x][y] = data[(w == -1 ? data.length - x - 1 : x)][(h == -1 ? data[x].length - y - 1 : y)];
			}
		}
		return predata;
	}

	public static SerialImage flip(SerialImage img, int w, int h) {
		SerialImage out = new SerialImage(img.getFlip(w, h));
		return out;
	}

	public SerialImage flip(int w, int h) {
		return new SerialImage(getFlip(w, h));
	}

	public int[][] getRotate(double radians) {

		int[][] out = new int[width][height];

		for (int x = 0; x < out.length; x++) {
			for (int y = 0; y < out[x].length; y++) {

				double rotatedX = Math.cos(-radians) * (x - data.length / 2)
						- Math.sin(-radians) * (y - data[0].length / 2) + data.length / 2;

				double rotatedY = Math.sin(-radians) * (x - data.length / 2)
						+ Math.cos(-radians) * (y - data[0].length / 2) + data[0].length / 2;

				if (mode == 1) {
					if (Math.floor(rotatedX + 1) < data.length && Math.floor(rotatedY + 1) < data[x].length
							&& Math.floor(rotatedX) >= 0 && Math.floor(rotatedY) >= 0)
						out[x][y] = interpolate(data, rotatedX, rotatedY);
				} else if (mode == 0) {
					if (Math.ceil(rotatedX) < data.length && Math.ceil(rotatedY) < data[x].length
							&& Math.floor(rotatedX) >= 0 && Math.floor(rotatedY) >= 0)
						out[x][y] = data[(int) rotatedX][(int) rotatedY];
				}
			}
		}

		return out;
	}

	public int[][] getRotate(double radians, int orx, int ory) {

		int[][] out = new int[width][height];

		for (int x = 0; x < out.length; x++) {
			for (int y = 0; y < out[x].length; y++) {

				double rotatedX = Math.cos(-radians) * (x - orx) - Math.sin(-radians) * (y - ory) + orx;

				double rotatedY = Math.sin(-radians) * (x - orx) + Math.cos(-radians) * (y - ory) + ory;

				if (mode == 1) {
					if (Math.floor(rotatedX + 1) < data.length && Math.floor(rotatedY + 1) < data[x].length
							&& Math.floor(rotatedX) >= 0 && Math.floor(rotatedY) >= 0)
						out[x][y] = interpolate(data, rotatedX, rotatedY);
				} else if (mode == 0) {
					if (Math.ceil(rotatedX) < data.length && Math.ceil(rotatedY) < data[x].length
							&& Math.floor(rotatedX) >= 0 && Math.floor(rotatedY) >= 0)
						out[x][y] = data[(int) rotatedX][(int) rotatedY];
				}
			}
		}

		return out;
	}

	public int[][] getScale(double scaleX, double scaleY) {
		int[][] out = new int[(int) Math.ceil(data.length * scaleX)][(int) Math.ceil(data[0].length * scaleY)];

		for (int x = 0; x < out.length; x++) {
			for (int y = 0; y < out[x].length; y++) {

				if (mode == NEAREST_NEIGHBOR) {
					if (Math.floor(x / scaleX) < data.length && Math.floor(y / scaleY) < data[(int) (x / scaleX)].length
							&& Math.floor(x / scaleX) >= 0 && Math.floor(y / scaleY) >= 0) {
						out[x][y] = data[(int) (x / scaleX)][(int) (y / scaleY)];
					}
				} else if (mode == BILINEAR) {
					if (Math.floor(x / scaleX + 1) < data.length
							&& Math.floor(y / scaleY + 1) < data[(int) (x / scaleX)].length
							&& Math.floor(x / scaleX) >= 0 && Math.floor(y / scaleY) >= 0)
						out[x][y] = interpolate(data, x / scaleX, y / scaleY);
				}

			}
		}

		return out;
	}

	public int interpolate(int[][] src, double x, double y) {

		int y0 = (int) Math.floor(y);
		int y1 = (int) Math.floor(y + 1);
		int x0 = (int) Math.floor(x);
		int x1 = (int) Math.floor(x + 1);

		Color c00 = Tran.accurateRGB(src[x0][y0]), c10 = Tran.accurateRGB(src[x1][y0]),
				c01 = Tran.accurateRGB(src[x0][y1]), c11 = Tran.accurateRGB(src[x1][y1]);

		int r = interpolate(c00.getRed(), c10.getRed(), c01.getRed(), c11.getRed(), y0, y1, y, x0, x1, x);
		int g = interpolate(c00.getGreen(), c10.getGreen(), c01.getGreen(), c11.getGreen(), y0, y1, y, x0, x1, x);
		int b = interpolate(c00.getBlue(), c10.getBlue(), c01.getBlue(), c11.getBlue(), y0, y1, y, x0, x1, x);
		int a = interpolate(c00.getAlpha(), c10.getAlpha(), c01.getAlpha(), c11.getAlpha(), y0, y1, y, x0, x1, x);

		return Tran.accurateColor(new Color(r, g, b, a));
	}

	private int interpolate(int v00, int v10, int v01, int v11, int y0, int y1, double y, int x0, int x1, double x) {

		double R1 = ((x1 - x) / (x1 - x0)) * v00 + ((x - x0) / (x1 - x0)) * v10;
		double R2 = ((x1 - x) / (x1 - x0)) * v01 + ((x - x0) / (x1 - x0)) * v11;

		double P = (((y1 - y) / (y1 - y0)) * R1 + ((y - y0) / (y1 - y0)) * R2);

//		System.out.println(x + ", " + y + "(" + R1 + ", " + R2 + ") -> " + P);

		return (int) P;
	}

	public SerialImage rotate(double radians) {
		return new SerialImage(getRotate(radians));
	}

	public SerialImage rotate(double radians, int orx, int ory) {
		return new SerialImage(getRotate(radians, orx, ory));
	}

	public SerialImage scale(double scalex, double scaley) {
		return new SerialImage(getScale(scalex, scaley));
	}

	public int[][] getRotateX(double radians) {
		int[][] out = new int[data.length][data[0].length];

		for (int x = 0; x < out.length; x++) {
			for (int y = 0; y < out[x].length; y++) {

//				double rotatedX = Math.cos(radians) * (x - data.length / 2)
//						- Math.sin(radians) * (y - data[x].length / 2) + data.length / 2;
//				double rotatedX = (Math.cos(radians)*0.5 + 1.0)*x;

//				double rotatedY = Math.cos(radians) * (y-data[x].length/2)+data[x].length/2;
				double rotatedY = (y - data[x].length / 2) / Math.cos(radians) + data[x].length / 2;

				double rotatedX = x;

				if (mode == 1) {
					if (Math.floor(rotatedX + 1) < data.length && Math.floor(rotatedY + 1) < data[x].length
							&& Math.floor(rotatedX) >= 0 && Math.floor(rotatedY) >= 0)
						out[x][y] = interpolate(data, rotatedX, rotatedY);
				} else if (mode == 0) {
					if (Math.ceil(rotatedX) < data.length && Math.ceil(rotatedY) < data[x].length
							&& Math.floor(rotatedX) >= 0 && Math.floor(rotatedY) >= 0)
						out[x][y] = data[(int) rotatedX][(int) rotatedY];
				}
			}
		}

		return out;
	}

	public SerialImage rotateX(double radians) {
		return new SerialImage(getRotateX(radians));
	}

	public int[][] getRotateY(double radians) {
		int[][] out = new int[data.length][data[0].length];

		for (int x = 0; x < out.length; x++) {
			for (int y = 0; y < out[x].length; y++) {

//				double rotatedX = Math.cos(radians) * (x - data.length / 2)
//						- Math.sin(radians) * (y - data[x].length / 2) + data.length / 2;
//				double rotatedX = (Math.cos(radians)*0.5 + 1.0)*x;

//				double rotatedY = Math.cos(radians) * (y-data[x].length/2)+data[x].length/2;
				double rotatedY = y;

				double rotatedX = (x - data.length / 2) / Math.cos(radians) + data.length / 2;

				if (mode == 1) {
					if (Math.floor(rotatedX + 1) < data.length && Math.floor(rotatedY + 1) < data[x].length
							&& Math.floor(rotatedX) >= 0 && Math.floor(rotatedY) >= 0)
						out[x][y] = interpolate(data, rotatedX, rotatedY);
				} else if (mode == 0) {
					if (Math.ceil(rotatedX) < data.length && Math.ceil(rotatedY) < data[x].length
							&& Math.floor(rotatedX) >= 0 && Math.floor(rotatedY) >= 0)
						out[x][y] = data[(int) rotatedX][(int) rotatedY];
				}
			}
		}

		return out;
	}

	public SerialImage rotateY(double radians) {
		return new SerialImage(getRotateY(radians));
	}

	public void dump() {
		data = null;
		data = new int[1][1];
	}

	public double[] normalizedRotOffset(double radians) {
		double[] i = boundsRotate(radians, width, height);
		return new double[] {i[2], i[3]};
	}

	public double[] normalizedRotOffset(double radians, double orx, double ory) {
		double[] i = boundsRotate(radians, width, height, orx, ory);
		return new double[] {i[4], i[5]};
	}

	public int[][] getNormalizedRotation(double radians) {
		return getNormalizedRotation(radians, width/2.0, height/2.0);
	}

	public int[][] getNormalizedRotation(double radians, double orx, double ory) {
		
		double[] d = boundsRotate(radians, width, height, orx, ory);

		int[][] out = new int[(int) (d[2])][(int) (d[3])];

		for (int x = 0; x < out.length; x++) {
			for (int y = 0; y < out[x].length; y++) {

				double rotatedX = Math.cos(-radians) * (x - orx + d[4]) - Math.sin(-radians) * (y - ory + d[5]) + orx;

				double rotatedY = Math.sin(-radians) * (x - orx + d[4]) + Math.cos(-radians) * (y - ory + d[5]) + ory;

				if (mode == 1) {
					if (Math.floor(rotatedX + 1) < data.length && Math.floor(rotatedY + 1) < data[0].length
							&& Math.floor(rotatedX) >= 0 && Math.floor(rotatedY) >= 0)
						out[(int) (x)][(int) (y)] = interpolate(data, rotatedX, rotatedY);
				} else if (mode == 0) {
					if (Math.floor(rotatedX + 1) < data.length && Math.floor(rotatedY + 1) < data[0].length
							&& Math.floor(rotatedX) >= 0 && Math.floor(rotatedY) >= 0)
						out[(int) (x)][(int) (y)] = data[(int) rotatedX][(int) rotatedY];
				}
			}
		}

		return out;
	}

	public int[][] getNormalizedRotation(double[] radians, Pos... p) {

		Pos p00 = getRotationOff(radians, 0, 0, p);
		Pos p10 = getRotationOff(radians, width, 0, p);
		Pos p01 = getRotationOff(radians, 0, height, p);
		Pos p11 = getRotationOff(radians, width, height, p);

		double maxwidth = Math.max(Math.max(p00.x, p10.x), Math.max(p01.x, p11.x));
		double minwidth = Math.min(Math.min(p00.x, p10.x), Math.min(p01.x, p11.x));

		double maxheight = Math.max(Math.max(p00.y, p10.y), Math.max(p01.y, p11.y));
		double minheight = Math.min(Math.min(p00.y, p10.y), Math.min(p01.y, p11.y));

		int[][] out = new int[(int) (maxwidth - minwidth)][(int) (maxheight - minheight)];

		for (int x = (int) minwidth; x < out.length + minwidth; x++) {
			for (int y = (int) minheight; y < out[(int) (x - minwidth)].length + minheight; y++) {

				double rotatedX = getRotationOff(radians, x, y, p).x;

				double rotatedY = getRotationOff(radians, x, y, p).y;

				if (mode == 1) {
					if (Math.floor(rotatedX + 1) < data.length && Math.floor(rotatedY + 1) < data[0].length
							&& Math.floor(rotatedX) >= 0 && Math.floor(rotatedY) >= 0)
						out[(int) (x - minwidth)][(int) (y - minheight)] = interpolate(data, rotatedX, rotatedY);
				} else if (mode == 0) {
					if (Math.floor(rotatedX + 1) < data.length && Math.floor(rotatedY + 1) < data[0].length
							&& Math.floor(rotatedX) >= 0 && Math.floor(rotatedY) >= 0)
						out[(int) (x - minwidth)][(int) (y - minheight)] = data[(int) rotatedX][(int) rotatedY];
				}
			}
		}

		return out;
	}

	private Pos getRotationOff(double[] rot, double x, double y, Pos... origin) {
		double ox = 0;
		double oy = 0;

		for (int i = 0; i < rot.length && i < origin.length; i++) {
			ox += Math.cos(-rot[i]) * (x - origin[i].x) - Math.sin(-rot[i]) * (y - origin[i].y) + origin[i].x;
			oy += Math.sin(-rot[i]) * (x - origin[i].x) + Math.cos(-rot[i]) * (y - origin[i].y) + origin[i].y;
		}

		return new Pos(ox, oy);
	}

	private Pos getRotationOff(double radians, double x, double y, Pos origin) {
		double ox = Math.cos(-radians) * (x - origin.x) - Math.sin(-radians) * (y - origin.y) + origin.x;
		double oy = Math.sin(-radians) * (x - origin.x) + Math.cos(-radians) * (y - origin.y) + origin.y;
		return new Pos(ox, oy);
	}

	public double[] boundsRotate(double radians, double width, double height) {

		double w = width * Math.abs(Math.cos(radians)) + height * Math.abs(Math.sin(radians));
		double h = width * Math.abs(Math.sin(radians)) + height * Math.abs(Math.cos(radians));

		double x = getRotationOff(-radians, 0, 0, new Pos(width / 2, height / 2)).x + (w - width) / 2;
		double y = getRotationOff(-radians, 0, 0, new Pos(width / 2, height / 2)).y + (h - height) / 2;

		return new double[] { x, y, w, h };
	}

	public double[] boundsRotate(double radians, double width, double height, double oriX, double oriY) {

		double w = width * Math.abs(Math.cos(radians)) + height * Math.abs(Math.sin(radians));
		double h = width * Math.abs(Math.sin(radians)) + height * Math.abs(Math.cos(radians));

		double x = getRotationOff(-radians, 0, 0, new Pos(oriX, oriY)).x + (w - width) / 2;
		double y = getRotationOff(-radians, 0, 0, new Pos(oriX, oriY)).y + (h - height) / 2;

		Pos p00 = getRotationOff(-radians, 0, 0, new Pos(oriX, oriY)),
				p10 = getRotationOff(-radians, width, 0, new Pos(oriX, oriY)),
				p01 = getRotationOff(-radians, 0, height, new Pos(oriX, oriY)),
				p11 = getRotationOff(-radians, width, height, new Pos(oriX, oriY));
		
		double minx = Math.min(Math.min(p00.x, p10.x), Math.min(p01.x, p11.x)) + (w - width) / 2;
		double miny = Math.min(Math.min(p00.y, p10.y), Math.min(p01.y, p11.y)) + (h - height) / 2;

		return new double[] { x, y, w, h, minx, miny };
	}

	public Pos rotAround(double radians, Pos point, Pos origin) {
		
		double rotatedX = Math.cos(radians) * (point.x - origin.x) - Math.sin(radians) * (point.y - origin.y) + origin.x;

		double rotatedY = Math.sin(radians) * (point.x - origin.x) + Math.cos(radians) * (point.y - origin.y) + origin.y;
		
		return new Pos(rotatedX, rotatedY);
	}
	
	public Pos rotAroundOff(double radians, Pos point, Pos origin) {
		
		double rotatedX = Math.cos(radians) * (point.x - origin.x) - Math.sin(radians) * (point.y - origin.y);

		double rotatedY = Math.sin(radians) * (point.x - origin.x) + Math.cos(radians) * (point.y - origin.y);
		
		return new Pos(rotatedX, rotatedY);
	}
	
	public SerialImage normalizeRotation(double radians) {
		return new SerialImage(getNormalizedRotation(radians));
	}

	public SerialImage normalizeRotation(double radians, double orx, double ory) {
		return new SerialImage(getNormalizedRotation(radians, orx, ory));
	}

	public SerialImage normalizeRotation(double[] radians, Pos... origin) {
		return new SerialImage(getNormalizedRotation(radians, origin));
	}

	public int[][] getCrop(int x, int y, int w, int h) {
		int[][] out = new int[w][h];
		for (int i = 0; i < w && i < width + x; i++) {
			for (int j = 0; j < h && j < height + y; j++) {
				out[i][j] = data[i + x][j + y];
			}
		}
		return out;
	}

	public SerialImage crop(int x, int y, int w, int h) {
		return new SerialImage(getCrop(x, y, w, h));
	}

	public int[][] getMapTo(Pos[] p, Pos[] t) {

		double minx = width, maxx = 0, miny = height, maxy = 0;

		double wp = width, hp = height;

		for (Pos s : t) {
			minx = Math.min(s.x, minx);
			maxx = Math.max(s.x, maxx);
			miny = Math.min(s.y, miny);
			maxy = Math.max(s.y, maxy);
		}

		wp = maxx - minx;
		hp = maxy - miny;

		int[][] out = new int[(int) wp][(int) hp];

//		System.out.println(out.length + ", " + out[0].length);

		for (int x = 0; x < out.length; x++)
			for (int y = 0; y < out[x].length; y++) {
				Pos s = getUV(x, y, width, height, p, t);
				if ((int) (s.x) < data.length && (int) (s.x) > 0 && (int) (s.y) < data[0].length && (int) (s.y) > 0)
					out[x][y] = data[(int) (s.x)][(int) (s.y)];
			}

		return out;
	}

	public Pos getUV(double x, double y, int w, int h, Pos[] p, Pos[] t) {

		double minx = w, maxx = 0, miny = h, maxy = 0;

		for (Pos s : t) {
			minx = Math.min(s.x, minx);
			maxx = Math.max(s.x, maxx);
			miny = Math.min(s.y, miny);
			maxy = Math.max(s.y, maxy);
		}

		double xs = x / (w * 1.0);
		double ys = y / (h * 1.0);

		double outx = 0, outy = 0;

//		System.out.println(x+", "+y + " ("+xs +", " + ys + ") " + w + " " + h);

		for (int i = 0; i < p.length; i++) {
			outx += Math.abs(xs - p[i].x) * t[i].x;
			outy += Math.abs(ys - p[i].x) * t[i].y;
		}

		return new Pos((outx / (p.length / 2)), (outy / (p.length / 2)));
	}

	public SerialImage mapTo(Pos[] p, Pos[] t) {
		return new SerialImage(getMapTo(p, t));
	}

}
