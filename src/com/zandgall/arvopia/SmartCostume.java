package com.zandgall.arvopia;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zandgall.arvopia.gfx.Accessory;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.Pos;
import com.zandgall.arvopia.gfx.SerialImage;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.KeyFrame;
import com.zandgall.arvopia.utils.Public;

public class SmartCostume {

	public static SerialImage face, hair, eye, whiteye, body, leg, shoe, arm, hand, barearm, barechest, bareleg,
			barefoot;
	public static Point pface, pbody, pleg, rleg, parm, rarm, phand, pshoe, rshoe;

	static Map<Map<String, Double>, BufferedImage> saved = new HashMap<Map<String, Double>, BufferedImage>();

	public static Map<String, Accessory> accessories = new HashMap<String, Accessory>();

	public static Map<String, Accessory> officialData = new HashMap<String, Accessory>();

	public static void setDefault() {

		pface = new Point(38, 21);
		pbody = new Point(36, 36);
		pleg = new Point(39, 48);
		rleg = new Point(33, 48);
		parm = new Point(42, 35);
		rarm = new Point(30, 35);
		phand = new Point(36, 36);
		pshoe = new Point(40, 56);
		rshoe = new Point(34, 56);

		face = new SerialImage(ImageLoader.loadImage("/textures/Player/Smart/DefaultFace.png"));
		hair = new SerialImage(new BufferedImage(72, 72, BufferedImage.TYPE_4BYTE_ABGR));
		eye = new SerialImage(new BufferedImage(72, 72, BufferedImage.TYPE_4BYTE_ABGR));
		whiteye = new SerialImage(ImageLoader.loadImage("/textures/Player/Smart/DefaultEyes.png"));
		hand = new SerialImage(ImageLoader.loadImage("/textures/Player/Smart/DefaultHands.png"));
		shoe = new SerialImage(
				ImageLoader.loadImage("/textures/Player/Smart/DefaultShoe.png").getSubimage(0, 0, 36, 36));
		body = new SerialImage(ImageLoader.loadImage("/textures/Player/Smart/DefaultBody.png"));
		barechest = new SerialImage(ImageLoader.loadImage("/textures/Player/Skin/Body.png"));
		arm = new SerialImage(ImageLoader.loadImage("/textures/Player/Smart/DefaultArms.png"));
		barearm = new SerialImage(ImageLoader.loadImage("/textures/Player/Skin/Arm.png"));
		leg = new SerialImage(ImageLoader.loadImage("/textures/Player/Smart/DefaultLegs.png"));
		bareleg = new SerialImage(ImageLoader.loadImage("/textures/Player/Skin/Leg.png"));
		barefoot = new SerialImage(ImageLoader.loadImage("/textures/Player/Skin/Feet.png").getSubimage(0, 0, 36, 36));

		officialData.put("face", new Accessory(38, 21, 18, 18, "none", face));
		officialData.put("righthand", new Accessory(36, 36, 17, 12, "none", hand));
		officialData.put("lefthand", new Accessory(36, 36, 17, 12, "none", hand));
		officialData.put("rightshoe", new Accessory(34, 56, 18, 34, "none", shoe));
		officialData.put("leftshoe", new Accessory(40, 56, 18, 34, "none", shoe));
		officialData.put("body", new Accessory(36, 36, 18, 34, "none", body));
		officialData.put("rightarm", new Accessory(30, 35, 17, 12, "none", arm));
		officialData.put("rightforearm", new Accessory(30, 35, 18, 18, "none", arm));
		officialData.put("leftarm", new Accessory(42, 35, 17, 12, "none", arm));
		officialData.put("leftforearm", new Accessory(42, 35, 18, 18, "none", arm));
		officialData.put("rightleg", new Accessory(33, 48, 18, 12, "none", leg));
		officialData.put("rightforeleg", new Accessory(33, 48, 18, 12, "none", leg));
		officialData.put("leftleg", new Accessory(39, 48, 18, 12, "none", leg));
		officialData.put("leftforeleg", new Accessory(39, 48, 18, 12, "none", leg));
	}

	public static void reset() {
		System.out.println("Reset saved data");
		saved.clear();
	}

	public static BufferedImage getStill() {

		Map<String, Double> data = new HashMap<String, Double>();

		return getPose(data);
	}

	public static BufferedImage getPose(Map<String, Double> data) {
		BufferedImage out = new BufferedImage(108, 108, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = out.createGraphics();

		g.addRenderingHints(Tran.mix(Tran.interneighbor, Tran.alias));

		parent("rightarm", "rightforearm", data, 20, 12);
		parent("leftarm", "leftforearm", data, 17, 12);
		parent("rightleg", "rightforeleg", data, 18, 0);
		parent("leftleg", "leftforeleg", data, 18, 0);
		parent("rightforeleg", "rightshoe", data, 18, 0);
		parent("leftforeleg", "leftshoe", data, 18, 0);

		draw(g, hand.crop(36, 0, 36, 36), parm, 17, 12, "leftforearm", data);
		draw(g, arm.crop(36, 36, 36, 36), parm, 17, 12, "leftforearm", data);
		draw(g, arm.crop(36, 0, 36, 36), parm, 17, 12, "leftarm", data);
		draw(g, leg.crop(0, 36, 36, 36), pleg, 18, 12, "leftforeleg", data);
		draw(g, leg.crop(0, 0, 36, 36), pleg, 18, 12, "leftleg", data);
		draw(g, shoe, pshoe, 18, 34, "leftshoe", data);
		draw(g, leg.crop(0, 36, 36, 36), rleg, 18, 12, "rightforeleg", data);
		draw(g, leg.crop(0, 0, 36, 36), rleg, 18, 12, "rightleg", data);
		draw(g, shoe, rshoe, 18, 34, "rightshoe", data);
		draw(g, body.crop(0, 0, 36, 36), pbody, "body", data);
		draw(g, face.crop(0, 0, 36, 36), pface, 18, 18, "face", data);
		if (data.containsKey("blinking") ? data.get("blinking") == 0 : true)
			draw(g, whiteye, pface, 18, 18, "face", data);
		if (data.containsKey("blinking") ? data.get("blinking") == 0 : true)
			draw(g, eye, pface, 18, 18, "face", data);
		draw(g, hair, pface, 18, 18, "face", data);
		draw(g, hand.crop(0, 0, 36, 36), rarm, 20, 12, "rightforearm", data);
		draw(g, arm.crop(0, 36, 36, 36), rarm, 20, 12, "rightforearm", data);
		draw(g, arm.crop(0, 0, 36, 36), rarm, 20, 12, "rightarm", data);

//		for (String s : accessories.keySet()) {
//			Accessory a = accessories.get(s);
//			parent(s, a.parent, data, a.orX, a.orY);
//			draw(g, a.image.spaceTo((54 - a.image.width / 2) + a.x, (54 - a.image.height / 2) + a.y, 108, 108),
//					new Point(a.x, a.y), a.orX, a.orY, officialData.get(a.parent).orX + officialData.get(a.parent).x,
//					officialData.get(a.parent).orY + officialData.get(a.parent).y,
//					officialData.get(a.parent.replaceAll("fore", "")).orX
//							+ officialData.get(a.parent.replaceAll("fore", "")).x,
//					officialData.get(a.parent.replaceAll("fore", "")).orY
//							+ officialData.get(a.parent.replaceAll("fore", "")).y,
//					s, a.parent, data);
//		}

		for (int b = 0; b <= 10; b += 1)

			for (String s : data.keySet()) {

				if (s.endsWith("ontop") && data.get(s) > 0 && data.get(s) == b) {

					if (s.startsWith("leftforearm"))
						draw(g, hand.crop(36, 0, 36, 36), parm, 17, 12, "leftforearm", data);
					if (s.startsWith("leftforearm"))
						draw(g, arm.crop(36, 36, 36, 36), parm, 17, 12, "leftforearm", data);
					if (s.startsWith("leftarm"))
						draw(g, arm.crop(36, 0, 36, 36), parm, 17, 12, "leftarm", data);
					if (s.startsWith("leftforeleg"))
						draw(g, leg.crop(0, 36, 36, 36), pleg, 18, 12, "leftforeleg", data);
					if (s.startsWith("leftleg"))
						draw(g, leg.crop(0, 0, 36, 36), pleg, 18, 12, "leftleg", data);
					if (s.startsWith("leftshoe"))
						draw(g, shoe, pshoe, 18, 34, "leftshoe", data);
					if (s.startsWith("rightforeleg"))
						draw(g, leg.crop(0, 36, 36, 36), rleg, 18, 12, "rightforeleg", data);
					if (s.startsWith("rightleg"))
						draw(g, leg.crop(0, 0, 36, 36), rleg, 18, 12, "rightleg", data);
					if (s.startsWith("rightshoe"))
						draw(g, shoe, rshoe, 18, 34, "rightshoe", data);
					if (s.startsWith("body"))
						draw(g, body.crop(0, 0, 36, 36), pbody, "body", data);
					if (s.startsWith("face"))
						draw(g, face.crop(0, 0, 36, 36), pface, 18, 18, "face", data);
					if (s.startsWith("face") || s.startsWith("whiteye"))
						if (data.containsKey("blinking") ? data.get("blinking") == 0 : true)
							draw(g, whiteye, pface, 18, 18, "face", data);
					if (s.startsWith("face") || s.startsWith("eye"))
						if (data.containsKey("blinking") ? data.get("blinking") == 0 : true)
							draw(g, eye, pface, 18, 18, "face", data);
					if (s.startsWith("face") || s.startsWith("hair"))
						draw(g, hair, pface, 18, 18, "face", data);
					if (s.startsWith("rightforearm"))
						draw(g, hand.crop(0, 0, 36, 36), rarm, 20, 12, "rightforearm", data);
					if (s.startsWith("rightforearm"))
						draw(g, arm.crop(0, 36, 36, 36), rarm, 20, 12, "rightforearm", data);
					if (s.startsWith("rightarm"))
						draw(g, arm.crop(0, 0, 36, 36), rarm, 20, 12, "rightarm", data);
				}
			}

		g.dispose();
		g = null;

		out.flush();

		return out;
	}

	public static BufferedImage getAccessories(Map<String, Double> data) {
		BufferedImage out = new BufferedImage(108, 108, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = out.createGraphics();

		g.addRenderingHints(Tran.mix(Tran.interneighbor, Tran.alias));

//		parent("rightarm", "rightforearm", data, 20, 12);
//		parent("leftarm", "leftforearm", data, 17, 12);
//		parent("rightleg", "rightforeleg", data, 18, 0);
//		parent("leftleg", "leftforeleg", data, 18, 0);
//		parent("rightforeleg", "rightshoe", data, 18, 0);
//		parent("leftforeleg", "leftshoe", data, 18, 0);

		for (String s : accessories.keySet()) {
			Accessory a = accessories.get(s);
//			Console.log(s, getFrom(s+"rot", data), data.containsKey(s), data);
			parent(a.parent, s, data, a.orX, a.orY);
			if(a.parent.contains("fore"))
				parent(a.parent.replaceAll("fore", ""), s, data, a.orX, a.orY);
			draw(g, a.image.spaceTo(54-a.orX, 54-a.orY, 108, 108), new Point(a.x, a.y), a.orX, a.orY,
					officialData.get(a.parent).orX + officialData.get(a.parent).x,
					officialData.get(a.parent).orY + officialData.get(a.parent).y,
					officialData.get(a.parent.replaceAll("fore", "")).orX
							+ officialData.get(a.parent.replaceAll("fore", "")).x,
					officialData.get(a.parent.replaceAll("fore", "")).orY
							+ officialData.get(a.parent.replaceAll("fore", "")).y,
					s, a.parent, data);
		}

		g.dispose();
		g = null;

		out.flush();

		return out;
	}

	private static void parent(String name, String affected, Map<String, Double> data, int orx, int ory) {

		data.put(affected + "rot", getFrom(name + "rot", data) + getFrom(affected + "rot", data));
		data.put(affected + "x", getFrom(name + "x", data) + getFrom(affected + "x", data));
		data.put(affected + "y", getFrom(name + "y", data) + getFrom(affected + "y", data));
		
	}

	public static void joint(String name, Map<String, Double> data, int x, int y) {
		if (data.containsKey(name + "rot")) {
			data.put(name + "x", -Math.sin(Math.toRadians(data.get(name + "rot"))) * 4 - x
					+ (data.containsKey(name + "x") ? data.get(name + "x") : 0));
			data.put(name + "y", Math.cos(Math.toRadians(data.get(name + "rot"))) * 6 + y
					+ (data.containsKey(name + "y") ? data.get(name + "y") : 0));
		}
	}

	private static int[] getCrop(String type, Map<String, Double> data) {
		return new int[] { (int) (data.containsKey(type + "cropx") ? data.get(type + "cropx") : 0),
				(int) (data.containsKey(type + "cropy") ? data.get(type + "cropy") : 0),
				(int) (data.containsKey(type + "cropw") ? data.get(type + "cropw") : 0),
				(int) (data.containsKey(type + "croph") ? data.get(type + "croph") : 0) };
	}

	public static void draw(Graphics2D g, BufferedImage image, Point point, int originX, int originY, double rotation) {
		Tran.drawRotatedImage(g, image, (int) point.getX(), (int) point.getY(), originX, originY, rotation);
	}

	public static void draw(Graphics2D g, BufferedImage image, Point point, int originX, int originY, String name,
			Map<String, Double> data) {
		BufferedImage b = draw(image, point, originX, originY, name, data);
		g.drawImage(b, 0, 0, null);
		b.flush();
		b = null;
	}

	public static void draw(Graphics2D g, SerialImage image, Point point, String name, Map<String, Double> data) {
		RenderingHints h = g.getRenderingHints();
		AffineTransform af = g.getTransform();
		g.setRenderingHints(Tran.mix(Tran.antialias, Tran.interbilinear));

		int[] c = getCrop(name, data);
		int flip = (int) getFrom(name + "width", data);
		double x = (point.x + getFrom(name + "x", data) + getFrom("x", data) + c[0]);
		double y = (point.y + getFrom(name + "y", data) + getFrom("y", data) + c[1]);
		double rot = (getFrom(name + "rot", data));

		SerialImage img = image.crop(c[0], c[1], image.width + c[2], image.height + c[3]).flip(flip, 1)
				.normalizeRotation(Math.toRadians(rot), image.width / 2, image.height / 2);

		x += image.crop(c[0], c[1], image.width + c[2], image.height + c[3]).normalizedRotOffset(Math.toRadians(rot),
				image.width / 2, image.height / 2)[0];
		y += image.crop(c[0], c[1], image.width + c[2], image.height + c[3]).normalizedRotOffset(Math.toRadians(rot),
				image.width / 2, image.height / 2)[1];
		g.translate(x, y);
		g.drawImage(img.toImage(), 0, 0, null);
		img.dump();
		g.setRenderingHints(h);
		g.setTransform(af);
	}

	public static void draw(Graphics2D g, SerialImage image, Point point, int originX, int originY, String name,
			Map<String, Double> data) {
		RenderingHints h = g.getRenderingHints();
		AffineTransform af = g.getTransform();
		g.setRenderingHints(Tran.mix(Tran.antialias, Tran.interbilinear));

		int[] c = getCrop(name, data);
		int flip = (int) getFrom(name + "width", data);
		double x = (point.x + getFrom(name + "x", data) + getFrom("x", data) + c[0]);
		double y = (point.y + getFrom(name + "y", data) + getFrom("y", data) + c[1]);
		double rot = (getFrom(name + "rot", data));

		boolean second = false;
		SerialImage img2 = image.crop(c[0], c[1], image.width + c[2], image.height + c[3]);

		SerialImage img = image.crop(c[0], c[1], image.width + c[2], image.height + c[3]);

		if (name.contains("fore")) {
			double rad2 = Math.toRadians(getFrom(name.replace("fore", "") + "rot", data));

			x += img.normalizedRotOffset((rad2), originX, originY)[0];
			y += img.normalizedRotOffset((rad2), originX, originY)[1];
			x += img.normalizedRotOffset(Math.toRadians(rot), 18, 18)[0];
			y += img.normalizedRotOffset(Math.toRadians(rot), 18, 18)[1];

			if (name.equals("rightforearm")) {
				second = true;
				img2 = barearm.crop(c[0], 36 + c[1], 36 + c[2], 36 + c[3]);
				img2 = img2.flip(flip, 1).normalizeRotation(Math.toRadians(rot), 18, 18);
			} else if (name.equals("leftforearm")) {
				second = true;
				img2 = barearm.crop(36 + c[0], 36 + c[1], 36 + c[2], 36 + c[3]);
				img2 = img2.flip(flip, 1).normalizeRotation(Math.toRadians(rot), 18, 18);
			} else if (name.equals("rightforeleg") || name.startsWith("leftforeleg")) {
				second = true;
				img2 = bareleg.crop(c[0], 36 + c[1], 36 + c[2], 36 + c[3]);
				img2 = img2.flip(flip, 1).normalizeRotation(Math.toRadians(rot), 18, 18);
			}

			img = img.flip(flip, 1).normalizeRotation(Math.toRadians(rot), 18, 18);// .normalizeRotation((rad2),
																					// originX, originY);
		} else if (name.contains("shoe")) {
			double rad2 = Math.toRadians(getFrom(name.replace("shoe", "leg") + "rot", data));
			double rad3 = Math.toRadians(getFrom(name.replace("shoe", "foreleg") + "rot", data));

			x += img.normalizedRotOffset((rad2), 18, 12)[0];
			y += img.normalizedRotOffset((rad2), 18, 12)[1];
			x += img.normalizedRotOffset((rad3), 18, 12)[0];
			y += img.normalizedRotOffset((rad3), 18, 12)[1];
			x += img.normalizedRotOffset(Math.toRadians(rot), 16, 16)[0];
			y += img.normalizedRotOffset(Math.toRadians(rot), 16, 16)[1];

			second = true;
			img2 = barefoot.crop(c[0], c[1], 36 + c[2], 36 + c[3]);
			img2 = img2.flip(flip, 1).normalizeRotation(Math.toRadians(rot), 16, 16);

			img = img.flip(flip, 1).normalizeRotation(Math.toRadians(rot), 16, 16);// .normalizeRotation((rad2),
																					// originX, originY);
		} else {

			if (name.equals("body")) {
				second = true;
				img2 = barechest.crop(c[0], c[1], 36 + c[2], 36 + c[3]);
				img2 = img2.flip(flip, 1).normalizeRotation(Math.toRadians(rot), originX, originY);
			} else if (name.equals("rightarm")) {
				second = true;
				img2 = barearm.crop(c[0], c[1], 36 + c[2], 36 + c[3]);
				img2 = img2.flip(flip, 1).normalizeRotation(Math.toRadians(rot), originX, originY);
			} else if (name.equals("leftarm")) {
				second = true;
				img2 = barearm.crop(36 + c[0], c[1], 36 + c[2], 36 + c[3]);
				img2 = img2.flip(flip, 1).normalizeRotation(Math.toRadians(rot), originX, originY);
			} else if (name.equals("rightleg") || name.startsWith("leftleg")) {
				second = true;
				img2 = bareleg.crop(c[0], c[1], 36 + c[2], 36 + c[3]);
				img2 = img2.flip(flip, 1).normalizeRotation(Math.toRadians(rot), originX, originY);
			}

			x += img.normalizedRotOffset(Math.toRadians(rot), originX, originY)[0];
			y += img.normalizedRotOffset(Math.toRadians(rot), originX, originY)[1];
			img = img.flip(flip, 1).normalizeRotation(Math.toRadians(rot), originX, originY);
		}

		g.translate(x, y);
		if (second)
			g.drawImage(img2.toImage(), 0, 0, null);
		g.drawImage(img.toImage(), 0, 0, null);
		img.dump();
		g.setRenderingHints(h);
		g.setTransform(af);
	}

	public static void draw(Graphics2D g, SerialImage image, Point point, int originX, int originY, double parX,
			double parY, double parX2, double parY2, String name, String parent, Map<String, Double> data) {
		RenderingHints h = g.getRenderingHints();
		AffineTransform af = g.getTransform();
		g.setRenderingHints(Tran.mix(Tran.antialias, Tran.interbilinear));

		parX += getFrom(parent + "x", data) + getFrom("x", data);
		parY += getFrom(parent + "y", data) + getFrom("y", data);

		parX2 += getFrom(parent.replaceAll("fore", "") + "x", data) + getFrom("x", data);
		parY2 += getFrom(parent.replaceAll("fore", "") + "y", data) + getFrom("y", data);

		point.x += getFrom(parent + "x", data) + getFrom(parent.replaceAll("fore", "") + "x", data)
				+ getFrom("x", data);
		point.y += getFrom(parent + "x", data) + getFrom(parent.replaceAll("fore", "") + "y", data)
				+ getFrom("y", data);

		double ox = parX, ox2 = parX2;
		double oy = parY, oy2 = parY2;
		
		int jO = 1;

		int[] c = getCrop(name, data);
		int flip = (int) getFrom(name + "width", data);
		double x = (getFrom(name + "x", data) + getFrom("x", data) + c[0]);
		double y = (getFrom(name + "y", data) + getFrom("y", data) + c[1]);

		double rad = Math.toRadians(getFrom(name + "rot", data));

		SerialImage img = image.crop(c[0], c[1], image.width + c[2], image.height + c[3]).flip(flip, 1);

		double rad2 = Math.toRadians(getFrom(parent + "rot", data));
		double rad3 = Math.toRadians(getFrom(parent.replaceAll("fore", "") + "rot", data));

		g.setColor(Color.red);

		Pos jointOff = img.rotAroundOff(rad3, new Pos(parX2, parY2), new Pos(parX2 + jO, parY2));
		Pos finOff2 = img.rotAround(rad3, new Pos(point.x + 54 + jointOff.x + jO, point.y + 54 + jointOff.y),
				new Pos(parX2 + jointOff.x + jO, parY2 + jointOff.y));
		Pos finOff = img.rotAroundOff(rad2, new Pos(point.x + 54, point.y + 54), new Pos(parX, parY));

		if (parent.contains("fore")) {

			parX2 += jointOff.x + jO;
			parY2 += jointOff.y;

			Pos mid = img.rotAround(rad3, new Pos(ox + jointOff.x + jO, oy + jointOff.y),
					new Pos(ox2 + jointOff.x + jO, oy2 + jointOff.y));

			parX = mid.x;
			parY = mid.y;

			finOff = img.rotAround(rad2, finOff2, new Pos(parX, parY));

			x += finOff.x;
			y += finOff.y;

		} else {

			x = finOff2.x;
			y = finOff2.y;

		}

		x += img.normalizedRotOffset(rad, img.width / 2, img.height / 2)[0];
		y += img.normalizedRotOffset(rad, img.width / 2, img.height / 2)[1];
		img = img.normalizeRotation(rad, img.width / 2, img.height / 2);

		BufferedImage o = img.toImage();
		
		g.translate(x, y);
		g.drawImage(o, -54, -54, null);
		img.dump();
		g.setRenderingHints(h);
		g.setTransform(af);
	}

	public static BufferedImage draw(BufferedImage image, Point point, int originX, int originY, String name,
			Map<String, Double> data) {

		BufferedImage out = new BufferedImage(108, 108, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = out.createGraphics();
		g.setRenderingHints(Tran.mix(Tran.antialias, Tran.interbilinear));

		int[] c = getCrop(name, data);
		int flip = (int) getFrom(name + "width", data);
		double x = (point.x + getFrom(name + "x", data) + getFrom("x", data) + c[0]);
		double y = (point.y + getFrom(name + "y", data) + getFrom("y", data) + c[1]);
		double rot = (getFrom(name + "rot", data));

		g.translate(x, y);
		g.rotate(Math.toRadians(rot), originX, originY);
		g.drawImage(
				Tran.flip(image.getSubimage(c[0], c[1], image.getWidth() + c[2], image.getHeight() + c[3]), flip, 1), 0,
				0, null);

		g.dispose();
		out.flush();
		return out;
	}

	public static double getFrom(String id, Map<String, Double> data) {
		return (data.containsKey(id) ? data.get(id) : 0);
	}

	public static String rotKey(String limb) {
		return limb.toLowerCase() + "rot";
	}

	// Templates

	public static Template smash = new Template(6, 0.2) {

		@Override
		public void createData() {
			data = new HashMap<String, ArrayList<KeyFrame>>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-45.0, 0.0, 1, 2));
			data.put("rightshoerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-1.0, 0.0, 1, 2));
			array.add(new KeyFrame(0.0, 4.0, 0, 2));
			data.put("leftarmx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-1.0, 0.0, 1, 2));
			array.add(new KeyFrame(1.0, 4.0, 0, 2));
			data.put("leftarmy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-180.0, 0.0, 2, 0));
			array.add(new KeyFrame(-40.0, 4.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.5, 0.0, 1, 0));
			data.put("facex", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(25.0, 0.0, 1, 2));
			data.put("rightlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.8000000000000003, 0.0, 0, 2));
			data.put("facey", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(2.4000000000000004, 0.0, 2, 0));
			data.put("y", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-2.0, 4.0, 0, 2));
			data.put("bodyx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 4.0, 0, 2));
			data.put("bodyy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(20.0, 0.0, 1, 2));
			data.put("rightforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(10.0, 4.0, 1, 2));
			data.put("bodyrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-20.0, 0.0, 1, 2));
			data.put("leftlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-180.0, 1.0, 2, 0));
			array.add(new KeyFrame(5.0, 4.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-3.0, 0.0, 0, 2));
			data.put("rightlegx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-2.0, 0.0, 0, 2));
			data.put("leftlegy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-2.0, 0.0, 1, 2));
			array.add(new KeyFrame(0.0, 4.0, 0, 2));
			data.put("rightarmy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-3.0, 0.0, 0, 2));
			data.put("leftlegx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, -1, 0));
			array.add(new KeyFrame(1.0, 2.0, -1, 0));
			array.add(new KeyFrame(0.0, 5.0, -1, 0));
			data.put("blinking", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 0, 2));
			array.add(new KeyFrame(0.0, 4.0, 0, 2));
			data.put("rightarmx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(20.0, 0.0, 1, 2));
			data.put("leftforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-180.0, 0.0, 1, 2));
			data.put("Itemrot", array);

		}

	};

	public static Template stab = new Template(12, 0.4) {

		@Override
		public void createData() {
			data = new HashMap<String, ArrayList<KeyFrame>>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-50.0, 0.0, -1, 2));
			data.put("rightshoerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			data.put("rightforearmontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-2.0, 2.0, 0, 2));
			array.add(new KeyFrame(0.0, 4.0, 1, 2));
			data.put("leftforearmx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-59.99999999999999, 2.0, -1, 2));
			array.add(new KeyFrame(-40.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 12.0, 1, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 2, 1));
			array.add(new KeyFrame(-3.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 12.0, 1, 2));
			data.put("leftarmx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-2.0, 2.0, 1, 2));
			array.add(new KeyFrame(0.0, 4.0, 1, 0));
			array.add(new KeyFrame(-2.0, 8.0, 1, 1));
			array.add(new KeyFrame(0.0, 12.0, 1, 2));
			data.put("leftarmy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(50.0, 0.0, 1, 1));
			array.add(new KeyFrame(-90.0, 4.0, 1, 2));
			array.add(new KeyFrame(50.0, 12.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			data.put("rightarmontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			array.add(new KeyFrame(2.0, 4.0, 2, 0));
			array.add(new KeyFrame(1.0, 12.0, 2, 0));
			data.put("facex", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(30.0, 0.0, -1, 2));
			data.put("rightlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("facey", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(3.0, 0.0, 2, 0));
			data.put("y", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(3.0, 0.0, 1, 2));
			data.put("faceontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 4.0, 0, 2));
			data.put("rightforearmx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(20.0, 0.0, -1, 2));
			data.put("rightforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(10.0, 0.0, 1, 1));
			data.put("bodyrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(5.0, 0.0, -1, 2));
			data.put("facerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-20.0, 0.0, -1, 2));
			data.put("leftlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(65.0, 0.0, 0, 2));
			array.add(new KeyFrame(-60.0, 4.0, 1, 2));
			array.add(new KeyFrame(65.0, 12.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-2.5999999999999996, 0.0, 0, 2));
			data.put("leftlegy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, -1, 2));
			array.add(new KeyFrame(1.0, 2.0, 2, 0));
			array.add(new KeyFrame(0.0, 7.0, 2, 0));
			data.put("blinking", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 2, 0));
			array.add(new KeyFrame(3.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 12.0, 1, 2));
			data.put("rightarmx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(20.0, 0.0, -1, 2));
			data.put("leftforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("leftforearmontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, -1, 2));
			array.add(new KeyFrame(1.0, 1.0, -1, 2));
			array.add(new KeyFrame(0.0, 2.0, -1, 2));
			array.add(new KeyFrame(2.0, 8.0, -1, 2));
			data.put("leftarmontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-100.0, 0.0, 1, 2));
			array.add(new KeyFrame(0.0, 4.0, 1, 2));
			array.add(new KeyFrame(-100.0, 12.0, 1, 2));
			data.put("rightforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-180.0, 0.0, 1, 2));
			data.put("Itemrot", array);
		}

	};

	public static Template airkick = new Template(12, 0.35) {

		@Override
		public void createData() {
			data = new HashMap<String, ArrayList<KeyFrame>>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(50.0, 0.0, 0, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("leftarmy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(45.0, 0.0, 1, 2));
			array.add(new KeyFrame(55.0, 6.0, 1, 2));
			array.add(new KeyFrame(45.0, 12.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.5000000000000004, 0.0, 0, 2));
			data.put("facex", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(20.0, 0.0, 0, 2));
			data.put("rightlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("facey", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(70.0, 0.0, 0, 2));
			array.add(new KeyFrame(50.0, 4.0, 1, 1));
			array.add(new KeyFrame(70.0, 12.0, 1, 1));
			data.put("rightforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(5.0, 0.0, 0, 2));
			array.add(new KeyFrame(8.0, 6.0, 1, 2));
			array.add(new KeyFrame(5.0, 12.0, 1, 2));
			data.put("bodyrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(10.0, 0.0, 0, 2));
			array.add(new KeyFrame(6.0, 6.0, 1, 2));
			array.add(new KeyFrame(10.0, 12.0, 1, 2));
			data.put("facerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-20.0, 0.0, 0, 2));
			array.add(new KeyFrame(-90.0, 4.0, 1, 1));
			array.add(new KeyFrame(-20.0, 12.0, 1, 2));
			data.put("leftlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-30.0, 0.0, 0, 2));
			array.add(new KeyFrame(-40.0, 6.0, 1, 2));
			array.add(new KeyFrame(-30.0, 12.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-1.0, 0.0, 0, 2));
			data.put("leftlegy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 0, 2));
			data.put("leftlegx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 1, 2));
			data.put("blinking", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(70.0, 0.0, 0, 2));
			array.add(new KeyFrame(0.0, 4.0, 1, 1));
			array.add(new KeyFrame(70.0, 12.0, 1, 1));
			data.put("leftforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-85.0, 0.0, 0, 2));
			data.put("rightforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(90.0, 0.0, 1, 2));
			data.put("Itemrot", array);
		}

	};

	public static Template punch = new Template(12, 0.5) {
		public void createData() {
			data = new HashMap<String, ArrayList<KeyFrame>>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-40.0, 0.0, 1, 2));
			data.put("rightshoerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-100.0, 0.0, 1, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(3.0, 0.0, 2, 0));
			array.add(new KeyFrame(4.0, 4.0, 1, 1));
			array.add(new KeyFrame(3.0, 12.0, 2, 0));
			data.put("leftarmx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 2, 0));
			array.add(new KeyFrame(2.0, 4.0, 1, 1));
			array.add(new KeyFrame(1.0, 12.0, 2, 0));
			data.put("leftarmy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(30.0, 0.0, 2, 0));
			array.add(new KeyFrame(-80.0, 4.0, 1, 1));
			array.add(new KeyFrame(30.0, 12.0, 2, 0));
			data.put("rightarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(3.0, 0.0, 2, 0));
			array.add(new KeyFrame(5.0, 4.0, 1, 1));
			array.add(new KeyFrame(3.0, 12.0, 2, 0));
			data.put("facex", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(20.0, 0.0, 2, 0));
			array.add(new KeyFrame(25.0, 4.0, 2, 0));
			array.add(new KeyFrame(20.0, 12.0, 1, 1));
			data.put("rightlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 2, 0));
			array.add(new KeyFrame(2.0, 4.0, 1, 1));
			array.add(new KeyFrame(1.0, 12.0, 2, 0));
			data.put("facey", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.7999999999999998, 0.0, 2, 0));
			data.put("y", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 2, 0));
			array.add(new KeyFrame(2.0, 4.0, 1, 1));
			array.add(new KeyFrame(1.0, 12.0, 2, 1));
			data.put("bodyx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(20.0, 0.0, 2, 0));
			array.add(new KeyFrame(13.0, 4.0, 2, 0));
			array.add(new KeyFrame(20.0, 12.0, 2, 0));
			data.put("rightforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(10.0, 0.0, 2, 0));
			array.add(new KeyFrame(20.0, 4.0, 1, 1));
			array.add(new KeyFrame(10.0, 12.0, 1, 2));
			data.put("bodyrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(5.0, 0.0, 2, 0));
			array.add(new KeyFrame(10.0, 4.0, 2, 0));
			array.add(new KeyFrame(5.0, 12.0, 2, 0));
			data.put("facerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-20.0, 0.0, 2, 0));
			array.add(new KeyFrame(-15.0, 4.0, 2, 0));
			array.add(new KeyFrame(-20.0, 12.0, 2, 0));
			data.put("leftlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-5.0, 0.0, 2, 2));
			array.add(new KeyFrame(35.0, 4.0, 1, 1));
			array.add(new KeyFrame(-5.0, 12.0, 1, 1));
			data.put("leftarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-1.3000000000000003, 0.0, 0, 2));
			data.put("leftlegy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-1.0, 0.0, 2, 0));
			array.add(new KeyFrame(0.0, 4.0, 1, 1));
			array.add(new KeyFrame(-1.0, 12.0, 1, 1));
			data.put("rightarmy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, -1, 1));
			array.add(new KeyFrame(1.0, 1.0, -1, 1));
			array.add(new KeyFrame(0.0, 7.0, -1, 1));
			data.put("blinking", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 2, 0));
			array.add(new KeyFrame(3.0, 4.0, 1, 1));
			array.add(new KeyFrame(1.0, 12.0, 2, 0));
			data.put("rightarmx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(20.0, 0.0, 1, 2));
			array.add(new KeyFrame(14.0, 4.0, 2, 0));
			array.add(new KeyFrame(20.0, 12.0, 2, 0));
			data.put("leftforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-120.0, 0.0, 2, 0));
			array.add(new KeyFrame(-10.0, 4.0, 1, 2));
			array.add(new KeyFrame(-120.0, 12.0, 1, 2));
			data.put("rightforearmrot", array);
		}
	};

	public static Template still = new Template(16, 0.05) {

		@Override
		public void createData() {
			data = new HashMap<String, ArrayList<KeyFrame>>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(10.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(-5.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightshoerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-20.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(20.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(20.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(-10.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-1.5, 4.0, 1, 2));
			array.add(new KeyFrame(0, 8.0, 1, 2));
			array.add(new KeyFrame(-0.5, 12.0, 1, 2));
			array.add(new KeyFrame(0, 16.0, 1, 2));
			data.put("y", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-20.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(10.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(10.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(-5.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("leftshoerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-5.0, 0.0, 1, 2));
			array.add(new KeyFrame(0.0, 4.0, 1, 2));
			array.add(new KeyFrame(-5.0, 8.0, 1, 2));
			array.add(new KeyFrame(-25.0, 12.0, 1, 2));
			array.add(new KeyFrame(-5.0, 16.0, 1, 2));
			data.put("rightforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(90.0, 0.0, 1, 2));
			data.put("Itemrot", array);

		}

	};

	public static Template hold = new Template(16, 0.05) {

		@Override
		public void createData() {
			data = new HashMap<String, ArrayList<KeyFrame>>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(10.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(-5.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightshoerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-20.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(20.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(20.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(-10.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-1, 4.0, 1, 2));
			array.add(new KeyFrame(0, 8.0, 1, 2));
			array.add(new KeyFrame(-0.5, 12.0, 1, 2));
			array.add(new KeyFrame(0, 16.0, 1, 2));
			data.put("y", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-20.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(10.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(10.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(-5.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("leftshoerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-5.0, 0.0, 1, 2));
			array.add(new KeyFrame(0.0, 4.0, 1, 2));
			array.add(new KeyFrame(-5.0, 8.0, 1, 2));
			array.add(new KeyFrame(-25.0, 12.0, 1, 2));
			array.add(new KeyFrame(-5.0, 16.0, 1, 2));
			data.put("rightforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(90.0, 0.0, 1, 2));
			data.put("Itemrot", array);

		}

	};

	public static Template walk = new Template(16, 0.3) {

		@Override
		public void createData() {
			data = new HashMap<String, ArrayList<KeyFrame>>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-15, 3.0, 1, 0));
			array.add(new KeyFrame(15, 7.0, 1, 1));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightshoerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(10.0, 4.0, 1, 2));
			array.add(new KeyFrame(-30, 13.0, 2, 0));
			array.add(new KeyFrame(0.0, 16.0, 2, 0));
			data.put("leftforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(-30.0, 4.0, 2, 2));
			array.add(new KeyFrame(30, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-0.3, 0.0, 1, 1));
			array.add(new KeyFrame(0.3, 4.0, 1, 2));
			array.add(new KeyFrame(-0.6, 12.0, 1, 0));
			array.add(new KeyFrame(-0.3, 16.0, 1, 1));
			data.put("facex", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(30.0, 4.0, 1, 2));
			array.add(new KeyFrame(-35, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 0, 2));
			data.put("rightlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.4, 0.0, 1, 1));
			array.add(new KeyFrame(0.4, 4.0, 1, 2));
			array.add(new KeyFrame(0.5, 12.0, 1, 0));
			array.add(new KeyFrame(0.5, 16.0, 1, 1));
			data.put("facey", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(10.0, 0.0, 1, 2));
			array.add(new KeyFrame(85.0, 8.0, 1, 2));
			array.add(new KeyFrame(-5.0, 14.0, 1, 0));
			array.add(new KeyFrame(10.0, 16.0, 1, 0));
			data.put("rightforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(5.1, 4.0, 1, 2));
			array.add(new KeyFrame(-5.0, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 1));
			data.put("bodyrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-2.1, 0.0, 1, 1));
			array.add(new KeyFrame(5.0, 4.0, 1, 2));
			array.add(new KeyFrame(-5, 12.0, 1, 0));
			array.add(new KeyFrame(-2, 16.0, 1, 1));
			data.put("facerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, -1, 2));
			array.add(new KeyFrame(0.0, 8.0, -1, 2));
			data.put("blinking", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(-40.0, 4.0, 1, 2));
			array.add(new KeyFrame(40.0, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 0));
			data.put("leftlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 2, 1));
			array.add(new KeyFrame(30.0, 4.0, 2, 2));
			array.add(new KeyFrame(-30, 12.0, 2, 0));
			array.add(new KeyFrame(0.0, 16.0, 2, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(85.0, 0.0, 1, 2));
			array.add(new KeyFrame(-5, 6.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(85.0, 16.0, 1, 2));
			data.put("leftforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(15.0, 0.0, 1, 1));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(-15.0, 12.0, 1, 0));
			array.add(new KeyFrame(15.0, 16.0, 1, 2));
			data.put("leftshoerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(-30.0, 5.0, 2, 2));
			array.add(new KeyFrame(10.0, 12.0, 2, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(90.0, 0.0, 1, 2));
			data.put("Itemrot", array);
			
			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-2.5, 0.0, 1, 2));
			data.put("y", array);

		}

	};

	public static Template run = new Template(16, 0.4) {

		@Override
		public void createData() {
			data = new HashMap<String, ArrayList<KeyFrame>>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 0, 1));
			array.add(new KeyFrame(-20, 2.0, 1, 1));
			array.add(new KeyFrame(10.0, 4.0, 1, 0));
			array.add(new KeyFrame(6, 7.0, 1, 1));
			array.add(new KeyFrame(-22, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 0));
			data.put("rightshoerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-77.0, 0.0, 1, 2));
			array.add(new KeyFrame(-55.0, 4.0, 1, 2));
			array.add(new KeyFrame(-100.0, 12.0, 1, 2));
			array.add(new KeyFrame(-77.0, 16.0, 1, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(4.0, 0.0, 1, 2));
			array.add(new KeyFrame(2.0, 8.0, 1, 2));
			array.add(new KeyFrame(4.0, 16.0, 1, 2));
			data.put("leftarmx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			array.add(new KeyFrame(1.0, 8.0, 0, 2));
			array.add(new KeyFrame(2.0, 16.0, 0, 2));
			data.put("leftarmy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(-30.0, 4.0, 1, 2));
			array.add(new KeyFrame(60.0, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(5.0, 0.0, 1, 2));
			array.add(new KeyFrame(2.0, 8.0, 0, 2));
			array.add(new KeyFrame(5.0, 16.0, 1, 2));
			data.put("facex", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(45.0, 4.0, 1, 2));
			array.add(new KeyFrame(-45.0, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 1));
			data.put("rightlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 0, 2));
			array.add(new KeyFrame(1.0, 16.0, 1, 2));
			data.put("facey", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(2.0, 0.0, 1, 2));
			array.add(new KeyFrame(1.0, 8.0, 1, 2));
			array.add(new KeyFrame(2.0, 16.0, 1, 2));
			data.put("bodyx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 0));
			array.add(new KeyFrame(25.0, 4.0, 1, 1));
			array.add(new KeyFrame(55, 8.0, 1, 1));
			array.add(new KeyFrame(0, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(15.0, 0.0, 1, 2));
			array.add(new KeyFrame(5.0, 8.0, 1, 2));
			array.add(new KeyFrame(15.0, 16.0, 1, 2));
			data.put("bodyrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(6.0, 0.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(6.0, 16.0, 1, 2));
			data.put("facerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(-45.0, 4.0, 1, 2));
			array.add(new KeyFrame(45, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 0));
			data.put("leftlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(60.0, 4.0, 1, 2));
			array.add(new KeyFrame(-30.0, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, -1, 2));
			array.add(new KeyFrame(0.0, 8.0, -1, 2));
			data.put("blinking", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(2.0, 0.0, 1, 2));
			array.add(new KeyFrame(1.0, 8.0, 0, 2));
			array.add(new KeyFrame(2.0, 16.0, 1, 2));
			data.put("rightarmx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(70.0, 0.0, 1, 0));
			array.add(new KeyFrame(0.0, 4.0, 1, 1));
			array.add(new KeyFrame(0.1, 8.0, 0, 1));
			array.add(new KeyFrame(70.0, 16.0, 1, 1));
			data.put("leftforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(6.0, 0.0, 1, 1));
			array.add(new KeyFrame(-20, 4.0, 1, 1));
			array.add(new KeyFrame(0.0, 8.0, 1, 0));
			array.add(new KeyFrame(-10.0, 10.0, 1, 1));
			array.add(new KeyFrame(25.0, 12.0, 1, 0));
			array.add(new KeyFrame(30.0, 16.0, 1, 2));
			data.put("leftshoerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-55.0, 0.0, 1, 2));
			array.add(new KeyFrame(-100.0, 4.0, 1, 2));
			array.add(new KeyFrame(-55.0, 12.0, 1, 0));
			array.add(new KeyFrame(-55.0, 16.0, 1, 2));
			data.put("rightforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(180.0, 0.0, 1, 2));
			data.put("Itemrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-2, 0.0, 1, 2));
			data.put("y", array);
			
		}

	};

	public static Template air = new Template(8, 0.05) {

		@Override
		public void createData() {
			data = new HashMap<String, ArrayList<KeyFrame>>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(20.0, 0.0, 0, 2));
			data.put("rightshoerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-40.0, 0.0, 0, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(60.0, 0.0, 0, 2));
			array.add(new KeyFrame(50.0, 3.0, 1, 2));
			array.add(new KeyFrame(60.0, 8.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("facex", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(10.0, 0.0, 1, 2));
			array.add(new KeyFrame(20.0, 3.0, 1, 2));
			array.add(new KeyFrame(10.0, 8.0, 1, 2));
			data.put("rightlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 0, 2));
			array.add(new KeyFrame(0.5, 4.0, 0, 2));
			array.add(new KeyFrame(0.0, 8.0, 0, 2));
			data.put("facey", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(50.0, 0.0, 0, 2));
			data.put("rightforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(5.0, 0.0, 0, 2));
			data.put("bodyrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(5.0, 0.0, 0, 2));
			array.add(new KeyFrame(7.0, 4.0, 0, 2));
			array.add(new KeyFrame(5.0, 8.0, 0, 2));
			data.put("facerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-30.0, 0.0, 1, 2));
			array.add(new KeyFrame(-40.0, 5.0, 1, 2));
			array.add(new KeyFrame(-30.0, 8.0, 1, 2));
			data.put("leftlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(10.0, 0.0, 1, 2));
			array.add(new KeyFrame(20.0, 5.0, 1, 2));
			array.add(new KeyFrame(10.0, 8.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(50.0, 0.0, 0, 2));
			data.put("leftforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(30.0, 0.0, 0, 2));
			data.put("leftshoerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-40.0, 0.0, 0, 2));
			data.put("rightforearmrot", array);
			
			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(90.0, 0.0, 1, 2));
			data.put("Itemrot", array);
			
		}

	};

	public static Template crouch = new Template(8, 0.05) {

		@Override
		public void createData() {
			data = new HashMap<String, ArrayList<KeyFrame>>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.6, 0.0, 0, 2));
			data.put("rightshoey", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(3.0, 0.0, 1, 2));
			data.put("rightforearmontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 0, 2));
			data.put("leftforearmx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			data.put("rightlegontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-1.0, 0.0, 0, 2));
			data.put("leftforearmy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(145.0, 0.0, 1, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("leftarmx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(20.0, 0.0, 1, 2));
			array.add(new KeyFrame(35.0, 4.0, 1, 2));
			array.add(new KeyFrame(20.0, 8.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			data.put("rightforelegontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(3.0, 0.0, 1, 2));
			data.put("rightarmontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("leftforelegontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-160.0, 0.0, 0, 2));
			data.put("rightlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(2.0, 0.0, 1, 2));
			array.add(new KeyFrame(1, 4.0, 1, 2));
			array.add(new KeyFrame(2.0, 8.0, 1, 2));
			data.put("facey", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			data.put("rightshoeontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(3.0, 0.0, 1, 2));
			data.put("faceontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-14.0, 0.0, 0, 2));
			data.put("bodycroph", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("leftlegontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("leftshoeontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(160.0, 0.0, 0, 2));
			data.put("rightforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(3.0, 0.0, 0, 2));
			data.put("leftforelegy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(4.0, 0.0, 0, 2));
			data.put("leftforelegx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.6, 0.0, 0, 2));
			data.put("leftshoey", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-160.0, 0.0, 0, 2));
			data.put("leftlegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-25.0, 0.0, 1, 2));
			array.add(new KeyFrame(-45.0, 4.0, 1, 2));
			array.add(new KeyFrame(-25.0, 8.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-5.0, 0.0, 0, 2));
			data.put("rightlegy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-1.0, 0.0, 0, 2));
			data.put("rightlegx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-6.0, 0.0, 0, 2));
			data.put("leftlegy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-3.0, 0.0, 0, 2));
			data.put("leftlegx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 3.0, -1, 0));
			array.add(new KeyFrame(1.0, 5.0, -1, 0));
			array.add(new KeyFrame(0.0, 7.0, -1, 0));
			data.put("blinking", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(160.0, 0.0, 0, 2));
			data.put("leftforelegrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 0, 2));
			data.put("leftshoerot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(3.0, 0.0, 0, 2));
			data.put("leftforearmontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			data.put("leftarmontop", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			data.put("rightforelegy", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			data.put("rightforelegx", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(-125.0, 0.0, 1, 2));
			data.put("rightforearmrot", array);

			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(13.0, 0.0, 1, 2));
			data.put("y", array);
			
			array = new ArrayList<KeyFrame>();
			array.add(new KeyFrame(90.0, 0.0, 1, 2));
			data.put("Itemrot", array);

		}

	};

	public Map<String, Double> goCrazyGoStupid() {
		Map<String, Double> data = new HashMap<String, Double>();
		data.put("leftarmrot", (System.currentTimeMillis() / 10) * 1.0d);
		data.put("leftarmx", Math.sin(Math.toRadians(System.currentTimeMillis() / 10)) * -6.0d);
		data.put("leftarmy", Math.cos(Math.toRadians(System.currentTimeMillis() / 10)) * 6.0d - 5);
		data.put("leftforearmx", Math.sin(Math.toRadians(System.currentTimeMillis() / 10)) * -6.0d);
		data.put("leftforearmy", Math.cos(Math.toRadians(System.currentTimeMillis() / 10)) * 6.0d - 5);
		data.put("leftforearmrot", (System.currentTimeMillis()) * 1.0d);
		data.put("rightarmrot", (System.currentTimeMillis() / 10) * 1.0d);
		data.put("rightarmx", Math.sin(Math.toRadians(System.currentTimeMillis() / 10)) * -6.0d);
		data.put("rightarmy", Math.cos(Math.toRadians(System.currentTimeMillis() / 10)) * 6.0d - 5);
		data.put("rightforearmx", Math.sin(Math.toRadians(System.currentTimeMillis() / 10)) * -6.0d);
		data.put("rightforearmy", Math.cos(Math.toRadians(System.currentTimeMillis() / 10)) * 6.0d - 5);
		data.put("rightforearmrot", (System.currentTimeMillis()) * 1.0d);
		return data;
	}

	public long walk_INDEX = 0;

	public Map<String, Double> defaultWalk(double speed) {
		Map<String, Double> data = new HashMap<String, Double>();

		walk_INDEX += speed;

		data.put("leftarmrot", Math.sin(walk_INDEX / 150.0) * 20.0d);
		data.put("leftarmx", Math.sin(walk_INDEX / 150.0) * -3.0d);
		data.put("leftforearmrot", Math.sin(walk_INDEX / 150.0) * 30.0d);
		data.put("leftforearmx", Math.sin(walk_INDEX / 150.0) * -3.0d);

		data.put("rightarmrot", Math.sin(walk_INDEX / 150.0) * -20.0d);
		data.put("rightarmx", Math.sin(walk_INDEX / 150.0) * 3.0d);
		data.put("rightforearmrot", Math.sin(walk_INDEX / 150.0) * -30.0d);
		data.put("rightforearmx", Math.sin(walk_INDEX / 150.0) * 3.0d);

		data.put("rightlegrot", Math.sin(walk_INDEX / 150.0) * 20.0d);
		data.put("rightlegx", Math.sin(walk_INDEX / 150.0) * -3.0d);
		data.put("rightforelegrot", Math.sin(walk_INDEX / 150.0) * 30.0d);
		data.put("rightforelegx", Math.sin(walk_INDEX / 150.0) * -3.0d);

		data.put("leftlegrot", Math.sin(walk_INDEX / 150.0) * -20.0d);
		data.put("leftlegx", Math.sin(walk_INDEX / 150.0) * 3.0d);
		data.put("leftforelegrot", Math.sin(walk_INDEX / 150.0) * -30.0d);
		data.put("leftforelegx", Math.sin(walk_INDEX / 150.0) * 3.0d);

		data.put("rightshoerot", Math.sin(walk_INDEX / 150.0) * 40.0d);
		data.put("rightshoex", Math.sin(walk_INDEX / 150.0) * -7.0d);
		data.put("rightshoey", Math.cos(walk_INDEX / 75.0) * 2.0d);

		data.put("leftshoerot", Math.sin(walk_INDEX / 150.0) * -40.0d);
		data.put("leftshoex", Math.sin(walk_INDEX / 150.0) * 7.0d);
		data.put("leftshoey", Math.cos(walk_INDEX / 75.0) * 2.0d);

		data.put("bodyrot", Math.sin(walk_INDEX / 150.0) * 5.0d);
		data.put("leftarmy", Math.sin(walk_INDEX / 150.0) * 1.0d);
		data.put("leftforearmy", Math.sin(walk_INDEX / 150.0) * 1.0d);

		data.put("facerot", Math.sin(walk_INDEX / 150.0) * 5.0d);
		data.put("facex", Math.sin(walk_INDEX / 150.0) * 1.0d);
		data.put("facey", Math.sin(walk_INDEX / 150.0) * 0.0d);

		return data;
	}

	public long still_INDEX = 0;

	public Map<String, Double> defaultStill(double speed) {
		Map<String, Double> data = new HashMap<String, Double>();

		still_INDEX += speed;

		data.put("leftarmrot", Math.sin(still_INDEX / 100.0) * 10.0d - 10);
		data.put("leftarmx", Math.sin(still_INDEX / 100.0) * -1.5d);
		data.put("leftforearmrot", Math.sin(still_INDEX / 100.0) * 10.0d - 10);
		data.put("leftforearmx", Math.sin(still_INDEX / 100.0) * -1.5d);

		data.put("rightarmrot", Math.sin(still_INDEX / 100.0) * -10.0d + 10);
		data.put("rightarmx", Math.sin(still_INDEX / 100.0) * 1.5d);
		data.put("rightforearmrot", Math.sin(still_INDEX / 100.0) * -10.0d + 10);
		data.put("rightforearmx", Math.sin(still_INDEX / 100.0) * 1.5d);

		data.put("blinking", Math.sin(still_INDEX / 100.0) > 0.8 ? 1.0 : 0.0);

		return data;
	}

	public double stilli = 0;

	public Map<String, Double> still() {

		stilli += 0.05;
		if (stilli >= 16)
			stilli = 0;

		Map<String, ArrayList<KeyFrame>> data = new HashMap<String, ArrayList<KeyFrame>>();
		ArrayList<KeyFrame> array;

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 2));
		array.add(new KeyFrame(10.0, 4.0, 1, 2));
		array.add(new KeyFrame(0.0, 8.0, 1, 2));
		array.add(new KeyFrame(-5.0, 12.0, 1, 2));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("rightshoerot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 2));
		array.add(new KeyFrame(-20.0, 4.0, 1, 2));
		array.add(new KeyFrame(0.0, 8.0, 1, 2));
		array.add(new KeyFrame(20.0, 12.0, 1, 2));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("leftforearmrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 2));
		array.add(new KeyFrame(20.0, 4.0, 1, 2));
		array.add(new KeyFrame(0.0, 8.0, 1, 2));
		array.add(new KeyFrame(-10.0, 12.0, 1, 2));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("rightarmrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 2));
		array.add(new KeyFrame(-1, 4.0, 1, 2));
		array.add(new KeyFrame(0, 8.0, 1, 2));
		array.add(new KeyFrame(-0.5, 12.0, 1, 2));
		array.add(new KeyFrame(0, 16.0, 1, 2));
		data.put("y", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 2));
		array.add(new KeyFrame(-20.0, 4.0, 1, 2));
		array.add(new KeyFrame(0.0, 8.0, 1, 2));
		array.add(new KeyFrame(10.0, 12.0, 1, 2));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("leftarmrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 2));
		array.add(new KeyFrame(10.0, 4.0, 1, 2));
		array.add(new KeyFrame(0.0, 8.0, 1, 2));
		array.add(new KeyFrame(-5.0, 12.0, 1, 2));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("leftshoerot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(-5.0, 0.0, 1, 2));
		array.add(new KeyFrame(0.0, 4.0, 1, 2));
		array.add(new KeyFrame(-5.0, 8.0, 1, 2));
		array.add(new KeyFrame(-25.0, 12.0, 1, 2));
		array.add(new KeyFrame(-5.0, 16.0, 1, 2));
		data.put("rightforearmrot", array);

		Map<String, Double> out = fromKeyframes(stilli, data);
		out.put("blinking", (stilli > 8 ? 0.0 : 1.0));

		return out;
	}

	public double runi = 0.0;

	public Map<String, Double> run() {

		runi += 0.4;
		if (runi >= 16)
			runi = 0;

		Map<String, ArrayList<KeyFrame>> data = new HashMap<String, ArrayList<KeyFrame>>();
		ArrayList<KeyFrame> array;

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 0, 1));
		array.add(new KeyFrame(-20, 2.0, 1, 1));
		array.add(new KeyFrame(10.0, 4.0, 1, 0));
		array.add(new KeyFrame(6, 7.0, 1, 1));
		array.add(new KeyFrame(-22, 12.0, 1, 0));
		array.add(new KeyFrame(0.0, 16.0, 1, 0));
		data.put("rightshoerot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(-77.0, 0.0, 1, 2));
		array.add(new KeyFrame(-55.0, 4.0, 1, 2));
		array.add(new KeyFrame(-100.0, 12.0, 1, 2));
		array.add(new KeyFrame(-77.0, 16.0, 1, 2));
		data.put("leftforearmrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(4.0, 0.0, 1, 2));
		array.add(new KeyFrame(2.0, 8.0, 1, 2));
		array.add(new KeyFrame(4.0, 16.0, 1, 2));
		data.put("leftarmx", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(2.0, 0.0, 0, 2));
		array.add(new KeyFrame(1.0, 8.0, 0, 2));
		array.add(new KeyFrame(2.0, 16.0, 0, 2));
		data.put("leftarmy", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(-30.0, 4.0, 1, 2));
		array.add(new KeyFrame(60.0, 12.0, 1, 0));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("rightarmrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(5.0, 0.0, 1, 2));
		array.add(new KeyFrame(2.0, 8.0, 0, 2));
		array.add(new KeyFrame(5.0, 16.0, 1, 2));
		data.put("facex", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(45.0, 4.0, 1, 2));
		array.add(new KeyFrame(-45.0, 12.0, 1, 0));
		array.add(new KeyFrame(0.0, 16.0, 1, 1));
		data.put("rightlegrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(1.0, 0.0, 1, 2));
		array.add(new KeyFrame(0.0, 8.0, 0, 2));
		array.add(new KeyFrame(1.0, 16.0, 1, 2));
		data.put("facey", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(2.0, 0.0, 1, 2));
		array.add(new KeyFrame(1.0, 8.0, 1, 2));
		array.add(new KeyFrame(2.0, 16.0, 1, 2));
		data.put("bodyx", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 0));
		array.add(new KeyFrame(25.0, 4.0, 1, 1));
		array.add(new KeyFrame(55, 8.0, 1, 1));
		array.add(new KeyFrame(0, 12.0, 1, 0));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("rightforelegrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(15.0, 0.0, 1, 2));
		array.add(new KeyFrame(5.0, 8.0, 1, 2));
		array.add(new KeyFrame(15.0, 16.0, 1, 2));
		data.put("bodyrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(6.0, 0.0, 1, 2));
		array.add(new KeyFrame(0.0, 8.0, 1, 2));
		array.add(new KeyFrame(6.0, 16.0, 1, 2));
		data.put("facerot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(-45.0, 4.0, 1, 2));
		array.add(new KeyFrame(45, 12.0, 1, 0));
		array.add(new KeyFrame(0.0, 16.0, 1, 0));
		data.put("leftlegrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(60.0, 4.0, 1, 2));
		array.add(new KeyFrame(-30.0, 12.0, 1, 0));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("leftarmrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(1.0, 0.0, -1, 2));
		array.add(new KeyFrame(0.0, 8.0, -1, 2));
		data.put("blinking", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(2.0, 0.0, 1, 2));
		array.add(new KeyFrame(1.0, 8.0, 0, 2));
		array.add(new KeyFrame(2.0, 16.0, 1, 2));
		data.put("rightarmx", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(70.0, 0.0, 1, 0));
		array.add(new KeyFrame(0.0, 4.0, 1, 1));
		array.add(new KeyFrame(0.1, 8.0, 0, 1));
		array.add(new KeyFrame(70.0, 16.0, 1, 1));
		data.put("leftforelegrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(6.0, 0.0, 1, 1));
		array.add(new KeyFrame(-20, 4.0, 1, 1));
		array.add(new KeyFrame(0.0, 8.0, 1, 0));
		array.add(new KeyFrame(-10.0, 10.0, 1, 1));
		array.add(new KeyFrame(25.0, 12.0, 1, 0));
		array.add(new KeyFrame(30.0, 16.0, 1, 2));
		data.put("leftshoerot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(-55.0, 0.0, 1, 2));
		array.add(new KeyFrame(-100.0, 4.0, 1, 2));
		array.add(new KeyFrame(-55.0, 12.0, 1, 0));
		array.add(new KeyFrame(-55.0, 16.0, 1, 2));
		data.put("rightforearmrot", array);

		return fromKeyframes(runi, data);
	}

	public double walki = 0.0;

	public Map<String, Double> walk() {
		walki += 0.3;
		if (walki >= 16)
			walki = 0;

		Map<String, ArrayList<KeyFrame>> data = new HashMap<String, ArrayList<KeyFrame>>();
		ArrayList<KeyFrame> array;

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 2));
		array.add(new KeyFrame(-15, 3.0, 1, 0));
		array.add(new KeyFrame(15, 7.0, 1, 1));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("rightshoerot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(10.0, 4.0, 1, 2));
		array.add(new KeyFrame(-30, 13.0, 2, 0));
		array.add(new KeyFrame(0.0, 16.0, 2, 0));
		data.put("leftforearmrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(-30.0, 4.0, 2, 2));
		array.add(new KeyFrame(30, 12.0, 1, 0));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("rightarmrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(-0.3, 0.0, 1, 1));
		array.add(new KeyFrame(0.3, 4.0, 1, 2));
		array.add(new KeyFrame(-0.6, 12.0, 1, 0));
		array.add(new KeyFrame(-0.3, 16.0, 1, 1));
		data.put("facex", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(30.0, 4.0, 1, 2));
		array.add(new KeyFrame(-35, 12.0, 1, 0));
		array.add(new KeyFrame(0.0, 16.0, 0, 2));
		data.put("rightlegrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.4, 0.0, 1, 1));
		array.add(new KeyFrame(0.4, 4.0, 1, 2));
		array.add(new KeyFrame(0.5, 12.0, 1, 0));
		array.add(new KeyFrame(0.5, 16.0, 1, 1));
		data.put("facey", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(10.0, 0.0, 1, 2));
		array.add(new KeyFrame(85.0, 8.0, 1, 2));
		array.add(new KeyFrame(-5.0, 14.0, 1, 0));
		array.add(new KeyFrame(10.0, 16.0, 1, 0));
		data.put("rightforelegrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(5.1, 4.0, 1, 2));
		array.add(new KeyFrame(-5.0, 12.0, 1, 0));
		array.add(new KeyFrame(0.0, 16.0, 1, 1));
		data.put("bodyrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(-2.1, 0.0, 1, 1));
		array.add(new KeyFrame(5.0, 4.0, 1, 2));
		array.add(new KeyFrame(-5, 12.0, 1, 0));
		array.add(new KeyFrame(-2, 16.0, 1, 1));
		data.put("facerot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(1.0, 0.0, -1, 2));
		array.add(new KeyFrame(0.0, 8.0, -1, 2));
		data.put("blinking", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(-40.0, 4.0, 1, 2));
		array.add(new KeyFrame(40.0, 12.0, 1, 0));
		array.add(new KeyFrame(0.0, 16.0, 1, 0));
		data.put("leftlegrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 2, 1));
		array.add(new KeyFrame(30.0, 4.0, 2, 2));
		array.add(new KeyFrame(-30, 12.0, 2, 0));
		array.add(new KeyFrame(0.0, 16.0, 2, 2));
		data.put("leftarmrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(85.0, 0.0, 1, 2));
		array.add(new KeyFrame(-5, 6.0, 1, 2));
		array.add(new KeyFrame(0.0, 8.0, 1, 2));
		array.add(new KeyFrame(85.0, 16.0, 1, 2));
		data.put("leftforelegrot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(15.0, 0.0, 1, 1));
		array.add(new KeyFrame(0.0, 8.0, 1, 2));
		array.add(new KeyFrame(-15.0, 12.0, 1, 0));
		array.add(new KeyFrame(15.0, 16.0, 1, 2));
		data.put("leftshoerot", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(-30.0, 5.0, 2, 2));
		array.add(new KeyFrame(10.0, 12.0, 2, 0));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("rightforearmrot", array);

		return fromKeyframes(walki, data);
	}

	public double jumpin = 0;

	public Map<String, Double> defaultJump(double speed) {
		Map<String, Double> data = new HashMap<String, Double>();

		jumpin += speed;
		double d = jumpin / 100.0d;

		data.put("leftarmrot", Math.sin(d) * 3.0d - 45);
		data.put("leftarmx", 3.7d + Math.sin(d) * 0.1);
		data.put("leftforearmrot", Math.sin(d) * 4.0d - 60);
		data.put("leftforearmx", Math.sin(d) * 0.0d + 3.7d);

		data.put("rightarmrot", Math.sin(d) * 3.0d + 45);
		data.put("rightarmx", -3.7d + Math.sin(d) * 0.1);
		data.put("rightarmy", -1.7d);
		data.put("rightforearmrot", Math.sin(d) * 4.0d + 20);
		data.put("rightforearmy", -1.7d + Math.sin(d) * 0.1);
		data.put("rightforearmx", Math.sin(d) * 0.1d - 3.7d);

		data.put("rightlegrot", Math.sin(d) * 2.0d + 20);
		data.put("rightlegx", Math.sin(d) * 0d - 3);
		data.put("rightforelegrot", Math.sin(d) * 5.0d + 45);
		data.put("rightforelegx", -3.0d);

		data.put("leftlegrot", Math.sin(d) * 2.0d - 60);
		data.put("leftlegx", Math.sin(d) * 0d + 6);
		data.put("leftlegy", Math.sin(d) * 0d - 2);
		data.put("leftforelegrot", Math.sin(d) * 5.0d - 30);
		data.put("leftforelegx", 6.0d);
		data.put("leftforelegy", -2.0d);

		data.put("leftshoex", -Math.sin(d) * 1.0d + 10.0d);
		data.put("leftshoey", Math.sin(d) * 1.0d - 3.0d);
		data.put("leftshoerot", Math.cos(d) * -4 - 18.0d);

		data.put("rightshoex", -Math.sin(d) * 1.0d - 8.0d);
		data.put("rightshoey", -Math.sin(d) * 0.5d - 2.5d);
		data.put("rightshoerot", Math.sin(d - 1) * 9 + 50.0d);

		data.put("facerot", Math.sin(d) * 2.0d);

		return data;
	}

	double crouchindex = 0;

	public Map<String, Double> defaultCrouch(double speed) {
		crouchindex += speed;

		double d = crouchindex / 10.0;

		Map<String, Double> data = new HashMap<String, Double>();

		data.put("leftlegrot", -150d);
		data.put("leftlegy", -1d);
		data.put("leftlegx", 4d);
		data.put("leftlegontop", 1d);
		data.put("leftforelegontop", 1d);
		data.put("leftforelegy", 2d);
		data.put("leftforelegx", 5d);

		data.put("rightlegrot", -150d);
		data.put("rightlegy", -1d);
		data.put("rightlegx", 4d);
		data.put("rightforelegontop", 1d);
		data.put("rightlegontop", 1d);
		data.put("rightforelegy", 2d);
		data.put("rightforelegx", 5d);

		data.put("rightshoex", 6d);
		data.put("leftshoex", 6d);
		data.put("leftshoeontop", 3d);
		data.put("rightshoeontop", 4d);

		data.put("leftarmy", 16d);
		data.put("leftforearmy", 13d);
		data.put("leftforearmx", -1d);
		data.put("leftarmrot", Math.sin(d) * 5);
		data.put("leftforearmrot", -60d);
		data.put("lefthandontop", 2d);

		data.put("rightarmy", 16d);
		data.put("rightforearmx", 1d);
		data.put("rightarmrot", Math.sin(d) * 5 + 17d);
		data.put("rightforearmy", 18d);
		data.put("rightforearmontop", 2d);
		data.put("rightforearmrot", -100d);
		data.put("righthandontop", 2d);

		data.put("bodyy", 12.0d);
		data.put("bodyx", -7.0d);
		data.put("bodycroph", -15.0d);

		data.put("facey", 17.0d);
		data.put("facerot", Math.sin(d) + 1);
		data.put("facex", 1.0d);

		data.put("blinking", Math.sin(d) > 0.8 ? 1.0 : 0.0);

		return data;

	}

	double punchindex = 0;

	public Map<String, Double> defaultPunch(double speed) {
		punchindex += speed;

		Map<String, Double> data = new HashMap<String, Double>();

		return defaultStill(speed);
	}

	public Map<String, Double> defaultKick(double speed) {
		punchindex += speed;

		Map<String, Double> data = new HashMap<String, Double>();

		return defaultStill(speed);
	}

	double skipi = 0;

	public Map<String, Double> defaultSkip(double speed) {

		Map<String, Double> data = new HashMap<String, Double>();

		skipi += speed;

		if (skipi > 100)
			skipi = 0;

		ArrayList<KeyFrame> k = new ArrayList<KeyFrame>();
		k.add(new KeyFrame(-45, 0, 2, 1));
		k.add(new KeyFrame(45, 50, 2, 1));
		k.add(new KeyFrame(-45, 100, 2, 1));
		double d = KeyFrame.getValue(k, skipi);
		data.put("rightarmrot", d);
		data.put("rightarmx", 5 * -Math.sin(Math.toRadians(d)));
		data.put("rightarmy", -1 - 2 * Math.sin(Math.toRadians(d)));
		data.put("rightforearmx", 5 * -Math.sin(Math.toRadians(d)));
		data.put("rightforearmy", -1 - 2 * Math.sin(Math.toRadians(d)));
		d -= 20;
		data.put("leftlegrot", d);
		data.put("leftlegx", 5 * -Math.sin(Math.toRadians(d)));
		data.put("leftlegy", -2 + 2 * Math.sin(Math.toRadians(d)));
		data.put("leftforelegx", 5 * -Math.sin(Math.toRadians(d)));
		data.put("leftforelegy", -2 + 2 * Math.sin(Math.toRadians(d)));

		k = new ArrayList<KeyFrame>();
		k.add(new KeyFrame(-65, 0, 2, 1));
		k.add(new KeyFrame(45, 50, 2, 1));
		k.add(new KeyFrame(-65, 100, 2, 1));
		d = KeyFrame.getValue(k, skipi);
		data.put("rightforearmrot", d);
		data.put("leftforelegrot", d);

		k = new ArrayList<KeyFrame>();
		k.add(new KeyFrame(45, 0, 2, 1));
		k.add(new KeyFrame(-45, 50, 2, 1));
		k.add(new KeyFrame(45, 100, 2, 1));
		d = KeyFrame.getValue(k, skipi);
		data.put("leftarmrot", d);
		data.put("leftarmx", 5 * -Math.sin(Math.toRadians(d)));
		data.put("leftarmy", -1 - 2 * Math.sin(Math.toRadians(d)));
		data.put("leftforearmx", 5 * -Math.sin(Math.toRadians(d)));
		data.put("leftforearmy", -1 - 2 * Math.sin(Math.toRadians(d)));
//		d-=20;
		data.put("rightlegrot", d);
		data.put("rightlegx", 5 * -Math.sin(Math.toRadians(d)));
		data.put("rightlegy", -2 + 2 * Math.sin(Math.toRadians(d)));
		data.put("rightforelegx", 5 * -Math.sin(Math.toRadians(d)));
		data.put("rightforelegy", -2 + 2 * Math.sin(Math.toRadians(d)));
		data.put("rightshoex", 5 * -Math.sin(Math.toRadians(d)));
		data.put("rightshoey", -2 + 2 * Math.sin(Math.toRadians(d)));

		k = new ArrayList<KeyFrame>();
		k.add(new KeyFrame(45, 0, 2, 1));
		k.add(new KeyFrame(-65, 50, 2, 1));
		k.add(new KeyFrame(45, 100, 2, 1));
		d = KeyFrame.getValue(k, skipi);
		data.put("leftforearmrot", d);
		data.put("rightforelegrot", d);
		data.put("rightshoex", 12 * -Math.sin(Math.toRadians(d)));
		data.put("rightshoey", -4 + 4 * Math.sin(Math.toRadians(d * 2)));
		k = new ArrayList<KeyFrame>();
		k.add(new KeyFrame(45, 0, 1));
		k.add(new KeyFrame(-65, 50, 1));
		k.add(new KeyFrame(45, 100, 1));
		d = KeyFrame.getValue(k, skipi + 10);
		data.put("rightshoerot", d + 10);

		k = new ArrayList<KeyFrame>();
		k.add(new KeyFrame(0, 0, 1));
		k.add(new KeyFrame(-10, 25, 1));
		k.add(new KeyFrame(0, 50, 1));
		k.add(new KeyFrame(-10, 75, 1));
		k.add(new KeyFrame(0, 100, 1));
		data.put("y", KeyFrame.getValue(k, skipi));

		return data;

	}

	public static Map<String, Double> fromKeyframes(double index, Map<String, ArrayList<KeyFrame>> data) {
		Map<String, Double> out = new HashMap<String, Double>();

		for (String s : data.keySet()) {
//			if(s.equals("Item"))
//				Console.log(s, data.get(s));
			out.put(s, KeyFrame.getValue(data.get(s), index));
		}

		return out;
	}

	static double testindex = 0;

	public static Map<String, Double> test() {
		testindex += 0.05;
		if (testindex >= 6)
			testindex = 0;

		Map<String, ArrayList<KeyFrame>> data = new HashMap<String, ArrayList<KeyFrame>>();
		ArrayList<KeyFrame> array;

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(-1.0, 0.0, -1, 2));
		array.add(new KeyFrame(1.0, 3.0, -1, 2));
		data.put("facewidth", array);

		array = new ArrayList<KeyFrame>();
		array.add(new KeyFrame(45.0, 0.0, 1, 2));
		array.add(new KeyFrame(-45.0, 3.0, 1, 2));
		array.add(new KeyFrame(45.0, 6.0, 2, 2));
		data.put("x", array);

		return fromKeyframes(testindex, data);
	}

	public static Animation createWalk() {
		BufferedImage[] images = new BufferedImage[(int) (16 / 0.3)];

		SmartCostume c = new SmartCostume();

		for (int i = 0; i < images.length; i++)
			images[i] = getPose(c.walk());

		return new Animation(1, images, "Walking", "Smart Costume");

	}

	public static Animation createDefaultWalk() {
		SmartCostume c = new SmartCostume();

		BufferedImage[] images = new BufferedImage[38];

		for (int index = 0; index < 38; index++) {
			Map<String, Double> data = c.defaultWalk(50.0d);
			images[index] = getPose(data);
		}

		return new Animation(1.0d, images, "Default Walk", "Smart Costumes");
	}

	public static Animation createDefaultStill() {
		SmartCostume c = new SmartCostume();

		c.still_INDEX = 0l;

		BufferedImage[] images = new BufferedImage[148];

		for (int index = 0; index < images.length; index++) {
			Map<String, Double> data = c.defaultStill(4.0d);
			images[index] = getPose(data);

			double topx = Math.sin(data.get("leftarmrot")) * 18, topy = Math.cos(data.get("leftarmrot")) * 18;
			double botx = Math.sin(data.get("leftforearmrot")) * 18, boty = Math.cos(data.get("leftforearmrot")) * 18;
		}

		return new Animation(1.0d, images, "Default Walk", "Smart Costumes");
	}

	public static Animation createDefaultJump() {
		SmartCostume c = new SmartCostume();

		BufferedImage[] images = new BufferedImage[64];

		for (int index = 0; index < images.length; index++) {
			images[index] = getPose(c.defaultJump(10.0d));
		}

		return new Animation(1.0d, images, "Default Walk", "Smart Costumes");
	}

	public static Animation createDefaultCrouch() {
		SmartCostume c = new SmartCostume();

		BufferedImage[] images = new BufferedImage[64];

		for (int index = 0; index < images.length; index++) {
			images[index] = getPose(c.defaultCrouch(1.0d));
		}

		return new Animation(1.0d, images, "Default Walk", "Smart Costumes");
	}

	public static Map<String, Double> get(Template t) {
		return t.get();
	}

	public static Animation getAnimation(Template t) {
		return Template.getAnimation(t);
	}
	
	public static Animation getFakeAnimation(Template t) {
		return Template.getFakeAnimation(t);
	}
	
	public static Animation getAccessoryAnimation(Template t) {
		return Template.getAccessoryAnimation(t);
	}

	public static void imitate(Animation a, Template t) {
//		if(a.index==0) {
//			t.index = 0;
//			Console.log(t.index, a.index);
//		}
		t.index = a.index * t.speed;
	}
	
	public static void setIndex(Template t, double index) {
		t.index=index;
	}
	
	public static void setSpeed(Template t, double speed) {
		t.speed=speed;
	}
	
}

abstract class Template {

	int length = 0;
	double speed = 0, index = 0;

	public Template(int length, double speed) {
		this.length = length;
		this.speed = speed;
	}

	Map<String, ArrayList<KeyFrame>> data;

	public abstract void createData();

	public Map<String, Double> get() {
		index += speed;
		if (index >= length)
			index = 0;

		if (data == null)
			createData();

		return SmartCostume.fromKeyframes(index, data);
	}

	public static Animation getAnimation(Template t) {
		BufferedImage[] frames = new BufferedImage[(int) (t.length / t.speed)];

		t.index = 0;

		for (int i = 0; i < frames.length; i++)
			frames[i] = SmartCostume.getPose(t.get());

		return new Animation(1, frames, "Generated", "Smart Costume");

	}
	
	public static Animation getFakeAnimation(Template t) {
		BufferedImage[] frames = new BufferedImage[(int) (t.length / t.speed)];

		t.index = 0;

		BufferedImage b = new BufferedImage(108, 108, BufferedImage.TYPE_4BYTE_ABGR);
		
		for (int i = 0; i < frames.length; i++)
			frames[i] = b;

		return new Animation(1, frames, "Generated", "Smart Costume");

	}
	
	public static Animation getAccessoryAnimation(Template t) {
		BufferedImage[] frames = new BufferedImage[(int) (t.length / t.speed)];

		t.index = 0;

		for (int i = 0; i < frames.length; i++)
			frames[i] = SmartCostume.getAccessories(t.get());

		return new Animation(1, frames, "Generated", "Smart Costume");

	}

}
