package com.zandgall.arvopia.gfx.transform;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;

import com.zandgall.arvopia.utils.Public;

public class Tran extends Canvas {

	private static final long serialVersionUID = 1L;

	public static Canvas c = new Canvas();

	public Tran() {
	}

	public static BufferedImage nullimage = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);

	public static int TEXT_MODE = 0; // 0: y=baseline; 1: y=top

	public static BufferedImage interpolate(BufferedImage img, int amount) {
		BufferedImage copy = img;

		Interpolation ef = new Interpolation(amount);

		return effectImage(ef, copy);
	}

	public static BufferedImage effectColor(BufferedImage img, Color c, Color shadowModifier, int target) {
		BufferedImage copy = img;
		ColorEffect ce = new ColorEffect(c, shadowModifier, target);
		ce.setMode(ColorFilter.NONE);
		return effectImage(ce, copy);
	}

	public static BufferedImage effectColor(BufferedImage img, Color c, Color shadowModifier) {
		return effectColor(img, c, shadowModifier, 255);
	}

	public static BufferedImage scaleUp(BufferedImage img, float scalar) {
		BufferedImage out = new BufferedImage((int) (img.getWidth() * scalar), (int) (img.getHeight() * scalar),
				BufferedImage.TYPE_4BYTE_ABGR);
		out.getGraphics().drawImage(img, 0, 0, out.getWidth(), out.getHeight(), null);
		out.getGraphics().dispose();

		return out;
	}

	public static boolean pixelTouching(int x, int y, BufferedImage img) {
		int rgb = img.getRGB(x, y);

		int a = (rgb >> 24) & 0xff;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int bl = (rgb) & 0xff;
		Color c = new Color(r, g, bl, a);

		boolean b = c.getAlpha() != 0;
		if (b)
			System.out.println("TOUCHING");
		return b;
	}

	public static BufferedImage effectColor(BufferedImage img, Color c) {
		return effectColor(img, c, Color.white);
	}

	public static void drawString(Graphics g, String text, int x, int y) {
		if (TEXT_MODE == 0)
			y -= g.getFontMetrics().getHeight();
		if (TEXT_MODE == 2) {
			for (String line : text.split("\n"))
				drawStringlet(g, line, x, y += g.getFontMetrics().getHeight());
		} else {
			for (String line : text.split("\n"))
				g.drawString(line, x, y += g.getFontMetrics().getHeight());
		}
	}

	public static void drawString(Graphics2D g, String text, int x, int y) {
		if (TEXT_MODE == 0)
			y -= g.getFontMetrics().getHeight();
		if (TEXT_MODE == 2) {
			for (String line : text.split("\n"))
				drawStringlet(g, line, x, y += g.getFontMetrics().getHeight());
		} else {
			for (String line : text.split("\n"))
				if(line!=null)
					g.drawString(line, x, y += g.getFontMetrics().getHeight());
		}
	}

	private static void drawStringlet(Graphics g, String textlet, int x, int y) {
		if (TEXT_MODE == 2)
			x -= measureString(textlet, g.getFont()).x / 2;
		g.drawString(textlet, x, y);
	}

	private static void drawStringlet(Graphics2D g, String textlet, int x, int y) {
		if (TEXT_MODE == 2)
			x -= measureString(textlet, g.getFont()).x / 2;
		g.drawString(textlet, x, y);
	}

	public static void drawTextOutline(Graphics g, int x, int y, String s, int strokeweight) {
		drawString(g, s, x - strokeweight, y - strokeweight);
		drawString(g, s, x + strokeweight, y - strokeweight);
		drawString(g, s, x + strokeweight, y + strokeweight);
		drawString(g, s, x - strokeweight, y + strokeweight);
		drawString(g, s, x, y - strokeweight);
		drawString(g, s, x, y + strokeweight);
		drawString(g, s, x + strokeweight, y);
		drawString(g, s, x - strokeweight, y);
	}

	public static void drawTextOutline(Graphics2D g, double x, double y, String s, double strokeweight) {
		drawString(g, s, (int) (x - strokeweight), (int) (y - strokeweight));
		drawString(g, s, (int) (x + strokeweight), (int) (y - strokeweight));
		drawString(g, s, (int) (x + strokeweight), (int) (y + strokeweight));
		drawString(g, s, (int) (x - strokeweight), (int) (y + strokeweight));
		drawString(g, s, (int) x, (int) (y - strokeweight));
		drawString(g, s, (int) x, (int) (y + strokeweight));
		drawString(g, s, (int) (x + strokeweight), (int) y);
		drawString(g, s, (int) (x - strokeweight), (int) y);
	}

	public static void drawOutlinedText(Graphics g, int x, int y, String s, int strokeweight, Color stroke,
			Color fill) {
		g.setColor(stroke);
		drawTextOutline(g, x, y, s, strokeweight);
		g.setColor(fill);
		drawString(g, s, x, y);
	}

	public static void drawOutlinedText(Graphics2D g, double x, double y, String s, double strokeweight, Color stroke,
			Color fill) {
		g.setColor(stroke);
		drawTextOutline(g, x, y, s, strokeweight);
		g.setColor(fill);
		drawString(g, s, (int) x, (int) y);
	}

	static Map<Map<BufferedImage, Integer>, BufferedImage> quickie = new HashMap<Map<BufferedImage, Integer>, BufferedImage>();

	public static long graphics, alphacom, maskdispose;

	public static BufferedImage ApplyTransparency(BufferedImage image, Image mask) {
		long pre = System.nanoTime();
		BufferedImage dest = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = dest.createGraphics();
		g2.drawImage(image, 0, 0, null);
		pre = System.nanoTime();
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_IN, 1.0F);
		g2.setComposite(ac);
		alphacom = System.nanoTime() - pre;
		pre = System.nanoTime();
		g2.drawImage(mask, 0, 0, null);
		graphics = System.nanoTime() - pre;
		pre = System.nanoTime();

		g2.dispose();
		maskdispose = System.nanoTime() - pre;
		return dest;
	}

	public static BufferedImage createImage() {

		int width = 200;
		int height = 200;

		// Generate the source pixels for our image

		// Lets just keep it to a simple blank image for now

		byte[] pixels = new byte[width * height];

		DataBuffer dataBuffer = new DataBufferByte(pixels, width * height, 0);

		SampleModel sampleModel = new SinglePixelPackedSampleModel(DataBuffer.TYPE_BYTE, width, height,
				new int[] { (byte) 0xf });

		WritableRaster raster = Raster.createWritableRaster(sampleModel, dataBuffer, null);

		return new BufferedImage(createColorModel(0), raster, false, null);

	}

	public static BufferedImage effectImage(ColorFilter filter, BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				Color c = filter.filter(x, y, img, img.getRGB(x, y));
				newImage.setRGB(x, y, c.getRGB());
			}
		}

		return newImage;
	}

	public static BufferedImage x2(BufferedImage src, boolean corners) {
		BufferedImage out = new BufferedImage(src.getWidth() * 2, src.getHeight() * 2, BufferedImage.TYPE_4BYTE_ABGR);

		for (int x = 0; x < out.getWidth() - 2; x += 2) {
			for (int y = 0; y < out.getHeight() - 2; y += 2) {
				int colorX0Y0 = src.getRGB(x / 2, y / 2);
				int colorX1Y0 = src.getRGB(x / 2 + 1, y / 2);
				int colorX0Y1 = src.getRGB(x / 2, y / 2 + 1);
				int colorX1Y1 = src.getRGB(x / 2 + 1, y / 2 + 1);

				Color cX1Y0 = Tran.accurateRGB(colorX1Y0);
				Color cX0Y1 = Tran.accurateRGB(colorX0Y1);
				Color cX1Y1 = Tran.accurateRGB(colorX1Y1);
				Color cX0Y0 = Tran.accurateRGB(colorX0Y0);

				int wX0Y0 = colorX0Y0;
				int wX1Y0 = Tran.accurateColor(Public.blend(cX0Y0, cX1Y0, 0.5f, false));
				int wX0Y1 = Tran.accurateColor(Public.blend(cX0Y0, cX0Y1, 0.5f, false));
				int wX1Y1 = Tran.accurateColor(Public.blend(cX0Y0, cX1Y1, 0.5f, false));
				int sX1Y1 = Tran.accurateColor(Public.blend(cX1Y0, cX0Y1, 0.5f, false));
				out.setRGB(x, y, wX0Y0);
				out.setRGB(x + 1, y, wX1Y0);
				out.setRGB(x, y + 1, wX0Y1);
				if (corners)
					out.setRGB(x + 1, y + 1, wX1Y1);
				else
					out.setRGB(x + 1, y + 1, sX1Y1);
			}
		}

		return out;
	}

	public static BufferedImage x3(BufferedImage src, boolean corners) {
		BufferedImage out = new BufferedImage(src.getWidth() * 2, src.getHeight() * 2, BufferedImage.TYPE_4BYTE_ABGR);

		for (int x = 2; x < out.getWidth() - 4; x += 3) {
			for (int y = 2; y < out.getHeight() - 4; y += 3) {
				int colorX0Y0 = src.getRGB(x / 2, y / 2);
				int colorX1Y0 = src.getRGB(x / 2 + 1, y / 2);
				int colorX0Y1 = src.getRGB(x / 2, y / 2 + 1);
				int colorXn1Y0 = src.getRGB(x / 2 - 1, y / 2);
				int colorX0Yn1 = src.getRGB(x / 2, y / 2 - 1);
				int colorX1Y1 = src.getRGB(x / 2 + 1, y / 2 + 1);
				int colorXn1Y1 = src.getRGB(x / 2 - 1, y / 2 + 1);
				int colorX1Yn1 = src.getRGB(x / 2 + 1, y / 2 - 1);
				int colorXn1Yn1 = src.getRGB(x / 2 - 1, y / 2 - 1);

				Color cX0Y0 = Tran.accurateRGB(colorX0Y0);
				Color cX1Y0 = Tran.accurateRGB(colorX1Y0);
				Color cX0Y1 = Tran.accurateRGB(colorX0Y1);
				Color cXn1Y0 = Tran.accurateRGB(colorXn1Y0);
				Color cX0Yn1 = Tran.accurateRGB(colorX0Yn1);
				Color cX1Y1 = Tran.accurateRGB(colorX1Y1);
				Color cXn1Y1 = Tran.accurateRGB(colorXn1Y1);
				Color cX1Yn1 = Tran.accurateRGB(colorX1Yn1);
				Color cXn1Yn1 = Tran.accurateRGB(colorXn1Yn1);

				int wX0Y0 = colorX0Y0;
				int wX1Y0 = Tran.accurateColor(Public.blend(cX0Y0, cX1Y0, 0.5f, false));
				int wX0Y1 = Tran.accurateColor(Public.blend(cX0Y0, cX0Y1, 0.5f, false));
				int wXn1Y0 = Tran.accurateColor(Public.blend(cX0Y0, cXn1Y0, 0.5f, false));
				int wX0Yn1 = Tran.accurateColor(Public.blend(cX0Y0, cX0Yn1, 0.5f, false));
				int wX1Y1 = Tran.accurateColor(Public.blend(cX0Y0, cX1Y1, 0.5f, false));
				int wXn1Y1 = Tran.accurateColor(Public.blend(cX0Y0, cXn1Y1, 0.5f, false));
				int wX1Yn1 = Tran.accurateColor(Public.blend(cX0Y0, cX1Yn1, 0.5f, false));
				int wXn1Yn1 = Tran.accurateColor(Public.blend(cX0Y0, cXn1Yn1, 0.5f, false));
				int sX1Y1 = Tran.accurateColor(Public.blend(cX1Y0, cX0Y1, 0.5f, false));
				int sXn1Y1 = Tran.accurateColor(Public.blend(cXn1Y0, cX0Y1, 0.5f, false));
				int sX1Yn1 = Tran.accurateColor(Public.blend(cX1Y0, cX0Yn1, 0.5f, false));
				int sXn1Yn1 = Tran.accurateColor(Public.blend(cXn1Y0, cX0Yn1, 0.5f, false));
				out.setRGB(x, y, wX0Y0);
				out.setRGB(x + 1, y, wX1Y0);
				out.setRGB(x, y + 1, wX0Y1);
				out.setRGB(x - 1, y, wXn1Y0);
				out.setRGB(x, y - 1, wX0Yn1);
				if (corners) {
					out.setRGB(x + 1, y + 1, wX1Y1);
					out.setRGB(x - 1, y + 1, wXn1Y1);
					out.setRGB(x + 1, y - 1, wX1Yn1);
					out.setRGB(x - 1, y - 1, wXn1Yn1);
				} else {
					out.setRGB(x + 1, y + 1, sX1Y1);
					out.setRGB(x - 1, y + 1, sXn1Y1);
					out.setRGB(x + 1, y - 1, sX1Yn1);
					out.setRGB(x - 1, y - 1, sXn1Yn1);
				}
			}
		}

		return out;
	}

	public static Color getColor(BufferedImage img, int x, int y) {
		int argb = img.getRGB(x, y);
		return accurateRGB(argb);
	}

	public static BufferedImage shade(BufferedImage img, int lightx, int lighty) {
		ShadeEffect effect = new ShadeEffect(-1, lightx, lighty);

		BufferedImage newimg = effectImage(effect, img);

		return newimg;
	}

	public static BufferedImage bevelShade(BufferedImage img, int bevel, int lightx, int lighty) {
		ShadeEffect effect = new ShadeEffect(bevel, lightx, lighty);

		BufferedImage newimg = effectImage(effect, img);

		return newimg;
	}

	public static Color accurateRGB(int argb) {
		int aa = (argb >> 24) & 0xff;
		int ar = (argb >> 16) & 0xff;
		int ag = (argb >> 8) & 0xff;
		int ab = (argb) & 0xff;
		Color aC = new Color(ar, ag, ab, aa);
		return aC;
	}

	public static BufferedImage staticShader(BufferedImage src, Color max, Color min) {
		StaticShader shader = new StaticShader(max, min);

		BufferedImage newimg = effectImage(shader, src);

		return newimg;
	}

	public static BufferedImage staticShader(BufferedImage src, int max, int min) {
		StaticShader shader = new StaticShader(max, min);

		BufferedImage newimg = effectImage(shader, src);

		return newimg;
	}

	public static int accurateColor(Color c) {
		int red = c.getRed();
		int green = c.getGreen();
		int blue = c.getBlue();
		int alpha = c.getAlpha();

		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	public static void setColor(BufferedImage img, int x, int y, Color c) {
		img.setRGB(x, y, accurateColor(c));
	}

	public static BufferedImage bleachImage(BufferedImage img, Color c) {

		int x = 0, y = 0, w = img.getWidth(), h = img.getHeight();

		BufferedImage copy = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);

		for (x = 0; x < w; x++) {
			for (y = 0; y < h; y++) {
				int alpha = accurateRGB(img.getRGB(x, y)).getAlpha();
				Color newColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
				setColor(copy, x, y, newColor);
			}
		}

		return copy;

	}

	public static BufferedImage getOutline(BufferedImage img, Color c) {

		int x = 1, y = 1, w = img.getWidth(), h = img.getHeight();

		BufferedImage copy = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);

		for (; x < w - 1; x++) {
			for (; y < h - 1; y++) {
				int alpha00 = accurateRGB(img.getRGB(x - 1, y - 1)).getAlpha();
				int alpha10 = accurateRGB(img.getRGB(x, y - 1)).getAlpha();
				int alpha20 = accurateRGB(img.getRGB(x + 1, y - 1)).getAlpha();
				int alpha01 = accurateRGB(img.getRGB(x - 1, y)).getAlpha();
				int alpha11 = accurateRGB(img.getRGB(x, y)).getAlpha();
				int alpha21 = accurateRGB(img.getRGB(x + 1, y)).getAlpha();
				int alpha02 = accurateRGB(img.getRGB(x - 1, y + 1)).getAlpha();
				int alpha12 = accurateRGB(img.getRGB(x, y + 1)).getAlpha();
				int alpha22 = accurateRGB(img.getRGB(x + 1, y + 1)).getAlpha();

				if (alpha11 == 0
						&& (alpha00 + alpha10 + alpha20 + alpha01 + alpha21 + alpha02 + alpha12 + alpha22 >= 255))
					setColor(copy, x, y, c);
			}
		}

		return copy;

	}

	public static BufferedImage blendImages(BufferedImage a, BufferedImage b, float rat) {
		BufferedImage out;

		int w = Math.min(a.getWidth(), b.getWidth());
		int h = Math.min(a.getHeight(), b.getHeight());

		out = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);

		float nRat = 1.0f - rat;

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int argb = a.getRGB(x, y);
				int aa = (argb >> 24) & 0xff;
				int ar = (argb >> 16) & 0xff;
				int ag = (argb >> 8) & 0xff;
				int ab = (argb) & 0xff;
//				Color aC = new Color(ar, ag, ab, aa);

				int brgb = b.getRGB(x, y);
				int ba = (brgb >> 24) & 0xff;
				int br = (brgb >> 16) & 0xff;
				int bg = (brgb >> 8) & 0xff;
				int bb = (brgb) & 0xff;
//				Color bC = new Color(br, bg, bb, ba);

				int red = (int) (ar * rat + br * nRat);
				int green = (int) (ag * rat + bg * nRat);
				int blue = (int) (ab * rat + bb * nRat);
				int alpha = (int) (aa * rat + ba * nRat);

				int color = (alpha << 24) | (red << 16) | (green << 8) | blue;

				out.setRGB(x, y, color);

			}
		}

		return out;
	}

	public static BufferedImage createImage(ColorModel cm) {

		int width = 200;
		int height = 200;

		// Generate the source pixels for our image
		// Lets just keep it to a simple blank image for now

		byte[] pixels = new byte[width * height];

		DataBuffer dataBuffer = new DataBufferByte(pixels, width * height, 0);

		SampleModel sampleModel = new SinglePixelPackedSampleModel(DataBuffer.TYPE_BYTE, width, height,
				new int[] { (byte) 0xf });

		WritableRaster raster = Raster.createWritableRaster(sampleModel, dataBuffer, null);

		return new BufferedImage(cm, raster, false, null);
	}

	public static Image createImages(FilteredImageSource ims) {
		ImageProducer ip = ims;
		return c.createImage(ip);
	}

	public static ColorModel createColorModel(int n) {

		// Create a simple color model with all values mapping to
		// a single shade of gray

		// nb. this could be improved by reusing the byte array

		byte[] r = new byte[16];
		byte[] g = new byte[16];
		byte[] b = new byte[16];

		for (int i = 0; i < r.length; i++) {
			r[i] = (byte) n;
			g[i] = (byte) n;
			b[i] = (byte) n;
		}

		return new IndexColorModel(4, 16, r, g, b);

	}

	private static BufferedImage createTransformed(BufferedImage image, AffineTransform at, double width,
			double height) {
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		return op.filter(image, null);
	}

	private static BufferedImage createTransformed(BufferedImage image, AffineTransform at, double width, double height,
			int imageHeight, int imageWidth) {
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		return op.filter(image, null);
	}

	private static BufferedImage createTransformed(BufferedImage image, AffineTransform at, double width, double height,
			int type) {
		AffineTransformOp op = new AffineTransformOp(at, type);
		return op.filter(image, null);
	}

	public static BufferedImage combine(BufferedImage image1, BufferedImage image2) {
		int w = Math.max(image1.getWidth(), image2.getWidth());
		int h = Math.max(image1.getHeight(), image2.getHeight());
		BufferedImage combined = new BufferedImage(w, h, 2);

		java.awt.Graphics g = combined.getGraphics();
		g.drawImage(image1, 0, 0, null);
		g.drawImage(image2, 0, 0, null);
		return combined;
	}

	public static BufferedImage flip(BufferedImage image, int width, int height, int imageWidth, int imageHeight) {
		Map<BufferedImage, Integer> map = new HashMap<BufferedImage, Integer>();
		map.put(image, Integer.valueOf(width));

		if ((quickie.size() > 0) && (quickie.containsKey(map))) {
			return (BufferedImage) quickie.get(map);
		}

		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(width, height));
		if (height == 1)
			height = 0;
		if (width == 1)
			width = 0;
		at.concatenate(AffineTransform.getTranslateInstance(width * imageWidth, height * imageHeight));

		BufferedImage out = createTransformed(image, at, 1.0D, 1.0D, imageWidth, imageHeight);

		quickie.put(map, out);

		return out;
	}

	public static Color randomColor() {
		return new Color((int) Public.random(0, 255), (int) Public.random(0, 255), (int) Public.random(0, 255));
	}

	public static BufferedImage flip(BufferedImage image, int width, int height) {
		Map<BufferedImage, Integer> map = new HashMap<BufferedImage, Integer>();
		map.put(image, Integer.valueOf(width));

		if ((quickie.size() > 0) && (quickie.containsKey(map))) {
			return (BufferedImage) quickie.get(map);
		}

		width = (width == 0 ? 1 : width);
		height = (height == 0 ? 1 : height);

		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(width, height));
		if (height == 1)
			height = 0;
		if (width == 1)
			width = 0;
		at.concatenate(AffineTransform.getTranslateInstance(width * image.getWidth(), height * image.getHeight()));

		BufferedImage out = createTransformed(image, at, 1.0D, 1.0D);

		quickie.put(map, out);

		return out;
	}
	
	public static BufferedImage flipNS(BufferedImage image, int width, int height) {
		Map<BufferedImage, Integer> map = new HashMap<BufferedImage, Integer>();
		map.put(image, Integer.valueOf(width));

		if ((quickie.size() > 0) && (quickie.containsKey(map))) {
			return (BufferedImage) quickie.get(map);
		}

		width = (width == 0 ? 1 : width);
		height = (height == 0 ? 1 : height);

		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(width, height));
		if (height == 1)
			height = 0;
		if (width == 1)
			width = 0;
		at.concatenate(AffineTransform.getTranslateInstance(width * image.getWidth(), height * image.getHeight()));

		BufferedImage out = createTransformed(image, at, 1.0D, 1.0D);

		return out;
	}

	public static BufferedImage rotate(BufferedImage image, double rotation) {
		double locationX = image.getWidth() / 2;
		double locationY = image.getHeight() / 2;

		double diff = Math.abs(image.getWidth() - image.getHeight());

		// To correct the set of origin point and the overflow
		double rotationRequired = Math.toRadians(rotation);
		double unitX = Math.abs(Math.cos(rotationRequired));
		double unitY = Math.abs(Math.sin(rotationRequired));

		double correctUx = unitX;
		double correctUy = unitY;

		// if the height is greater than the width, so you have to 'change' the axis to
		// correct the overflow
		if (image.getWidth() < image.getHeight()) {
			correctUx = unitY;
			correctUy = unitX;
		}

		int posAffineTransformOpX = 0 - (int) (locationX) - (int) (correctUx * diff);
		int posAffineTransformOpY = 0 - (int) (locationY) - (int) (correctUy * diff);

		// translate the image center to same diff that dislocates the origin, to
		// correct its point set
		AffineTransform objTrans = new AffineTransform();
		objTrans.translate(correctUx * diff, correctUy * diff);
		objTrans.rotate(rotationRequired, locationX, locationY);

		BufferedImage sout = createTransformed(image, objTrans, 1.0d, 1.0d);
		BufferedImage out = new BufferedImage(sout.getWidth(), sout.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = out.createGraphics();
		g.drawImage(sout, posAffineTransformOpX / 2, posAffineTransformOpY / 2, null);
		g.dispose();

		return out;
	}

	public static BufferedImage fixBlankTransparency(BufferedImage image) {
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				if (accurateRGB(image.getRGB(x, y)).getAlpha() == 0) {
					image.setRGB(x, y, accurateColor(new Color(0, 0, 0, 0)));
				}
			}
		}
		return image;
	}

	public static void drawRotatedImage(Graphics2D g, BufferedImage image, int posX, int posY, double rotation) {
		double locationX = image.getWidth() / 2;
		double locationY = image.getHeight() / 2;

		double diff = Math.abs(image.getWidth() - image.getHeight());

		// To correct the set of origin point and the overflow
		double rotationRequired = Math.toRadians(rotation);
		double unitX = Math.abs(Math.cos(rotationRequired));
		double unitY = Math.abs(Math.sin(rotationRequired));

		double correctUx = unitX;
		double correctUy = unitY;

		// if the height is greater than the width, so you have to 'change' the axis to
		// correct the overflow
		if (image.getWidth() < image.getHeight()) {
			correctUx = unitY;
			correctUy = unitX;
		}

		int posAffineTransformOpX = posX - (int) (locationX / 2) - (int) (correctUx / 2 * diff);
		int posAffineTransformOpY = posY - (int) (locationY / 2) - (int) (correctUy / 2 * diff);

		// translate the image center to same diff that dislocates the origin, to
		// correct its point set
		AffineTransform objTrans = new AffineTransform();
		objTrans.translate(correctUx * diff, correctUy * diff);
		objTrans.rotate(rotationRequired, locationX, locationY);

		g.drawImage(createTransformed(image, objTrans, 1, 1), posAffineTransformOpX, posAffineTransformOpY, null);
	}

	public static void drawRotatedImage(Graphics2D g, BufferedImage image, int posX, int posY, int originX, int originY,
			double rotation) {
		double locationX = originX;
		double locationY = originY;

		g.setRenderingHints(interneighbor);

		double diff = Math.abs(image.getWidth() - image.getHeight());

		// To correct the set of origin point and the overflow
		double rotationRequired = Math.toRadians(rotation);
		double unitX = Math.abs(Math.cos(rotationRequired));
		double unitY = Math.abs(Math.sin(rotationRequired));

		double correctUx = unitX;
		double correctUy = unitY;

		// if the height is greater than the width, so you have to 'change' the axis to
		// correct the overflow
		if (image.getWidth() < image.getHeight()) {
			correctUx = unitY;
			correctUy = unitX;
		}

		int posAffineTransformOpX = posX - (int) (locationX / 2) - (int) (correctUx / 2 * diff);
		int posAffineTransformOpY = posY - (int) (locationY / 2) - (int) (correctUy / 2 * diff);

		// translate the image center to same diff that dislocates the origin, to
		// correct its point set
		AffineTransform objTrans = new AffineTransform();
		objTrans.translate(correctUx * diff, correctUy * diff);
		objTrans.rotate(rotationRequired, locationX, locationY);

		g.drawImage(createTransformed(image, objTrans, 1, 1, AffineTransformOp.TYPE_BILINEAR), posAffineTransformOpX,
				posAffineTransformOpY, null);
	}

	public static BufferedImage scale(BufferedImage image, double width, double height) {
		AffineTransform at = new AffineTransform();
		at.scale(width, height);

		BufferedImage out = createTransformed(image, at, width, height);

		return out;
	}

	public static Point measureString(String s, Font f) {
		BufferedImage t = new BufferedImage(200, 200, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = t.getGraphics();
		g.setFont(f);
		
		int lines = s.split("\\n").length;
		
		int width = 0;
		
		for(int i = 0; i<lines; i++) {
			width = Math.max(g.getFontMetrics().stringWidth(s.split("\\n")[i]), width);
		}
		
		
		return new Point(width, g.getFontMetrics().getHeight()*lines);
	}

	public static java.awt.Rectangle toRect(BufferedImage image, int xOff, int yOff) {
		return new java.awt.Rectangle(xOff, yOff, image.getWidth(), image.getHeight());
	}

	public static BufferedImage effectAlpha(BufferedImage image, int alpha) {
		Alpha a = new Alpha(alpha);

		BufferedImage newIMG = effectImage(a, image);

		return newIMG;
	}

	public static RenderingHints mix(RenderingHints... hints) {
		ArrayList<Object> keys = new ArrayList<Object>();
		ArrayList<Object> values = new ArrayList<Object>();

		for (int i = 0; i < hints.length; i++) {
			keys.add(hints[i].keySet().toArray()[0]);
			values.add(hints[i].get(keys.get(i)));
		}

		RenderingHints out = new RenderingHints((Key) keys.get(0), values.get(0));

		for (int i = 1; i < keys.size(); i++) {
			out.put(keys.get(i), values.get(i));
		}

		return out;

	}

	public static byte[] toBytes(BufferedImage img) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "png", baos);
			baos.flush();
			byte[] bytes = baos.toByteArray();
			baos.close();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Mat toMat(BufferedImage in) {
		Mat out;
		byte[] data;
		int r, g, b;

		int width = in.getWidth(), height = in.getHeight();

		if (in.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
			out = new Mat(height, width, CvType.CV_8UC4);

			data = toBytes(in);
//			int[] dataBuff = in.getRGB(0, 0, width, height, null, 0, width);
//			for (int i = 0; i < dataBuff.length; i++) {
////				data[i * 4] = (byte) ((dataBuff[i] >> 32) & 0xFF);
//				data[i * 3 + 0] = (byte) ((dataBuff[i] >> 16) & 0xFF);
//				data[i * 3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
//				data[i * 3 + 2] = (byte) ((dataBuff[i] >> 0) & 0xFF);
//			}

			for (int x = 0; x < in.getWidth(); x++) {
				for (int c = 0; c < in.getHeight(); c++) {
					out.put(c, x, accurateRGB(in.getRGB(x, c)).getRed(), accurateRGB(in.getRGB(x, c)).getGreen(),
							accurateRGB(in.getRGB(x, c)).getBlue(), accurateRGB(in.getRGB(x, c)).getAlpha());
				}
			}
			return out;
		} else {
			out = new Mat(height, width, CvType.CV_8UC1);
			data = new byte[width * height * (int) out.elemSize()];
			int[] dataBuff = in.getRGB(0, 0, width, height, null, 0, width);
			for (int i = 0; i < dataBuff.length; i++) {
				r = (byte) ((dataBuff[i] >> 16) & 0xFF);
				g = (byte) ((dataBuff[i] >> 8) & 0xFF);
				b = (byte) ((dataBuff[i] >> 0) & 0xFF);
				data[i] = (byte) ((0.21 * r) + (0.71 * g) + (0.07 * b)); // luminosity
			}
			out.put(0, 0, data);
			return out;
		}
	}

	public static BufferedImage toImage(Mat in) {
		BufferedImage out;
		byte[] data = new byte[in.width() * in.height() * (int) in.elemSize()];
		int type;
		in.get(0, 0, data);

		if (in.channels() == 1)
			type = BufferedImage.TYPE_BYTE_GRAY;
		else
			type = BufferedImage.TYPE_4BYTE_ABGR;

		out = new BufferedImage(in.width(), in.height(), type);

		out.getRaster().setDataElements(0, 0, in.width(), in.height(), data);
		return out;
	}

	public static BufferedImage MatToBufferedImage(Mat matrix) {
		int cols = matrix.cols();
		int rows = matrix.rows();
		int elemSize = (int) matrix.elemSize();
		byte[] data = new byte[cols * rows * elemSize];
		int type;
		matrix.get(0, 0, data);
		switch (matrix.channels()) {
		case 1:
			type = BufferedImage.TYPE_BYTE_GRAY;
			break;
		case 3:
			type = BufferedImage.TYPE_3BYTE_BGR;
			// bgr to rgb
			byte b;
			for (int i = 0; i < data.length; i = i + 3) {
				b = data[i];
				data[i] = data[i + 2];
				data[i + 2] = b;
			}
			break;
		default:
			type = BufferedImage.TYPE_4BYTE_ABGR;
		}
		BufferedImage image = new BufferedImage(cols, rows, type);
		image.getRaster().setDataElements(0, 0, cols, rows, data);
		return image;
	}

	public static BufferedImage copyPaste(BufferedImage src) {
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = dest.createGraphics();
		g.drawImage(src, 0, 0, null);
		g.dispose();
		return dest;
	}
	
	public static RenderingHints antialias = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
	public static RenderingHints alias = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_OFF);

	public static RenderingHints textantialias = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	public static RenderingHints textalias = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

	public static RenderingHints renderspeed = new RenderingHints(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_SPEED);
	public static RenderingHints renderquality = new RenderingHints(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);
	public static RenderingHints rendernormal = new RenderingHints(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_DEFAULT);

	public static RenderingHints interbilinear = new RenderingHints(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	public static RenderingHints interneighbor = new RenderingHints(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

	// 0 - 255
	public static void contrast(int contrast) {

	}

}

class ColorEffect extends ColorFilter {

	float rf, gf, bf;
	float rs, gs, bs;

	boolean shade = false;

	public static int contrast = 0;

	public int target;

	public ColorEffect(Color c, Color s, int target) {
		if (c == null) {
			c = Color.red;
		}

		this.target = target;

		rf = (float) (c.getRed() / 255.0);
		gf = (float) (c.getGreen() / 255.0);
		bf = (float) (c.getBlue() / 255.0);

		if (s != Color.white) {
			shade = true;
			rs = (float) (s.getRed() / 255.0);
			gs = (float) (s.getGreen() / 255.0);
			bs = (float) (s.getBlue() / 255.0);
		}
	}

	@Override
	public Color filter(int x, int y, BufferedImage source, int rgbi) {
		int a = (rgbi >> 24) & 0xff;
		if (a == 0)
			return new Color(0, 0, 0, 0);
		int orr = ((rgbi >> 16) & 0xff); // Gets red of color
		int org = ((rgbi >> 8) & 0xff); // Gets green of color
		int orb = ((rgbi) & 0xff); // Gets blue of color

		int oc = Math.max(orr, Math.max(org, orb));
		if (mode == 1) {
			oc = (int) ((orr + org + orb) / 3.0);
		} else if (mode == 2) {
			oc = (int) Public.range(0, 255, orr + org + orb);
		} else if (mode == 3) {
			oc = Math.max(orr, Math.max(org, orb)) * 3;
		} else if (mode == 4) {
			oc = (int) Public.Map((orr + org + orb) / 3.0, Math.max(orr, Math.max(org, orb)), 0, 255, 0);
		}

		if (mode != -1) {
			orr = oc;
			org = oc;
			orb = oc;
		}

		orr = orr - target + 255;
		org = org - target + 255;
		orb = orb - target + 255;

		int roff = 255 - orr;
		int goff = 255 - org;
		int boff = 255 - orb;

		int r = (int) (255 * rf) - roff; // Effects the red
		int g = (int) (255 * gf) - goff; // Effects the green
		int b = (int) (255 * bf) - boff; // Effects the blue

//		int r = (int) (orr * rf);
//		int g = (int) (org * gf);
//		int b = (int) (orb * bf);

		if (shade) {
			// Creates shadow varients
			int crs = (int) (orr * rs);
			int cgs = (int) (org * gs);
			int cbs = (int) (orb * bs);

			float cr = (float) (crs / 255.0);
			float cg = (float) (cgs / 255.0);
			float cb = (float) (cbs / 255.0);

			// Creates ratio based on shade in area
			float ratio = (float) (Public.difference((orr / 255.0 + org / 255.0 + orb / 255.0), (cr + cg + cb + 0.0))
					/ 2.0);
			float nratio = 1 - ratio;

			r = (int) (ratio * (float) r + nratio * (float) crs);
			g = (int) (ratio * (float) g + nratio * (float) cgs);
			b = (int) (ratio * (float) b + nratio * (float) cbs);
		}

		// Flatten it to range
		r = (int) Public.range(0, 255, r);
		g = (int) Public.range(0, 255, g);
		b = (int) Public.range(0, 255, b);

		Color out = new Color(r, g, b, a);

		return out;
	}

}

class ShadeEffect extends ColorFilter {

	boolean shade = false;

	int bevel, lightx, lighty;

	public static int contrast = 0;

	public ShadeEffect(int bevel, int lightx, int lighty) {
		this.bevel = bevel;
		this.lightx = lightx;
		this.lighty = lighty;
	}

	@Override
	public Color filter(int x, int y, BufferedImage source, int rgbi) {
		int a = (rgbi >> 24) & 0xff;
		if (a == 0)
			return new Color(0, 0, 0, 0);
		int orr = ((rgbi >> 16) & 0xff); // Gets red of color
		int org = ((rgbi >> 8) & 0xff); // Gets green of color
		int orb = ((rgbi) & 0xff); // Gets blue of color

		float rr = (float) (orr / 255.0);
		float gr = (float) (org / 255.0);
		float br = (float) (orb / 255.0);

		float ratiox = (float) ((x + 0.0) / source.getWidth());
		float ratioy = (float) ((y + 0.0) / source.getHeight());

		float ratio = (ratiox + ratioy);

		ratio = (float) Public.Map(ratio, 1, 0, 1 + contrast / 255.0, 0.2 - contrast / 255.0);

		if (bevel == -1) {
			orr = 255 - (int) (orr * ratio);
			org = 255 - (int) (org * ratio);
			orb = 255 - (int) (orb * ratio);
		} else {
			if (x < bevel || x > source.getWidth() - bevel || y < bevel || y > source.getHeight() - bevel) {
				int cx = Math.min(Math.max(x, bevel), source.getWidth() - bevel);
				int cy = Math.min(Math.max(y, bevel), source.getHeight() - bevel);

				orr = 255 - (int) ((Public.dist(x, y, cx, cy) * 255 / bevel) * ratio);
				org = 255 - (int) ((Public.dist(x, y, cx, cy) * 255 / bevel) * ratio);
				orb = 255 - (int) ((Public.dist(x, y, cx, cy) * 255 / bevel) * ratio);
			} else {
				orr = 255;
				org = 255;
				orb = 255;
			}
		}

		orr = (int) Public.Map(orr, 255, 0, lightx, lighty);
		org = (int) Public.Map(org, 255, 0, lightx, lighty);
		orb = (int) Public.Map(orb, 255, 0, lightx, lighty);

		int r = (int) Public.range(0, 255, orr * rr);
		int g = (int) Public.range(0, 255, org * gr);
		int b = (int) Public.range(0, 255, orb * br);

		Color out = new Color(r, g, b, a);

		return out;
	}

}

class StaticShader extends ColorFilter {

	Color max, min;

	int imax, imin;

	public StaticShader(Color max, Color min) {
		this.max = max;
		this.min = min;
	}

	public StaticShader(int max, int min) {
		imax = max;
		imin = min;
	}

	@Override
	public Color filter(int x, int y, BufferedImage src, int rgbi) {

		int a = (rgbi >> 24) & 0xff;

		if (a == 0)
			return new Color(0, 0, 0, 0);

		int r = ((rgbi >> 16) & 0xff); // Gets red of color
		int g = ((rgbi >> 8) & 0xff); // Gets green of color
		int b = ((rgbi) & 0xff); // Gets blue of color

		if (max != null && min != null) {
			r = (int) Public.Map(r, 255, 0, max.getRed(), min.getRed());
			g = (int) Public.Map(g, 255, 0, max.getGreen(), min.getGreen());
			b = (int) Public.Map(b, 255, 0, max.getBlue(), min.getBlue());
		} else {
			r = (int) Public.Map(r, 255, 0, imax, imin);
			g = (int) Public.Map(g, 255, 0, imax, imin);
			b = (int) Public.Map(b, 255, 0, imax, imin);
		}

		r = (int) Public.range(0, 255, r);
		g = (int) Public.range(0, 255, g);
		b = (int) Public.range(0, 255, b);

		return new Color(r, g, b, a);
	}

}

class Interpolation extends ColorFilter {

	int inter;

	public Interpolation(int inter) {
		this.inter = inter;
	}

	@Override
	public Color filter(int x, int y, BufferedImage src, int rgbi) {

		Color rgbC = Tran.accurateRGB(rgbi);

		int r = rgbC.getRed();
		int g = rgbC.getGreen();
		int b = rgbC.getBlue();
		int a = (rgbi >> 24) & 0xff;
		if (a == 0)
			return new Color(0, 0, 0, 0);

		Color mix = new Color(r, g, b, a);

//		for(int i = 1; i<inter; i++) {
//			if(!(x-i<=0 || x+i>=src.getWidth())) {
//				rightC = Tran.accurateRGB(src.getRGB(x+i, y));
//				leftC = Tran.accurateRGB(src.getRGB(x-i, y));
//				Color c = Public.blend(rightC, leftC, 0.5f);
//				mix = Public.blend(c, mix, 0.6f-(i/inter));
//			}
//		}
//		
//		for(int j = 1; j<inter; j++) {
//			if(!(y-j<=0 || y+j>=src.getHeight())) {
//				downC = Tran.accurateRGB(src.getRGB(x, y+j));
//				upC = Tran.accurateRGB(src.getRGB(x, y-j));
//				Color c = Public.blend(upC, downC, 0.5f);
//				mix = Public.blend(c, mix, 0.6f-(j/inter));
//			}
//		}

		for (int i = x - inter; i < x + inter; i++) {
			for (int j = y - inter; j < y + inter; j++) {
				if (!(i <= 0 || i >= src.getWidth()) && !(j <= 0 || j >= src.getHeight())) {
					Color c = Tran.accurateRGB(src.getRGB(i, j));
					mix = Public.blend(c, mix, 0.5f);
				}
			}
		}

		return mix;
	}

}

class Alpha extends ColorFilter {

	int alpha;

	public Alpha(int alpha) {
		this.alpha = alpha;
	}

	@Override
	public Color filter(int x, int y, BufferedImage src, int rgbi) {

		int a = Tran.accurateRGB(rgbi).getAlpha();
		int r = Tran.accurateRGB(rgbi).getRed();
		int g = Tran.accurateRGB(rgbi).getGreen();
		int b = Tran.accurateRGB(rgbi).getBlue();

		if (a > 0)
			a = alpha;

		return new Color(r, g, b, a);
	}

}
