package com.zandgall.arvopia;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.zandgall.arvopia.gfx.Accessory;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.Pos;
import com.zandgall.arvopia.gfx.SerialImage;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.KeyFrame;

public class SmartCostume {

	public static SerialImage face, hair, eye, eye_whites, body, leg, shoe, arm, hand, bare_arm, bare_chest, bare_leg,
			barefoot;
	public static Point point_face, point_body, point_left_leg, point_right_leg, point_left_arm, point_right_arm, point_hand, point_left_shoe, point_right_shoe;

	static Map<Map<String, Double>, BufferedImage> saved = new HashMap<>();

	public static Map<String, Accessory> accessories = new HashMap<>();

	public static Map<String, Accessory> officialData = new HashMap<>();

	public static void setDefault() {

		point_face = new Point(38, 21);
		point_body = new Point(36, 36);
		point_left_leg = new Point(39, 48);
		point_right_leg = new Point(33, 48);
		point_left_arm = new Point(42, 35);
		point_right_arm = new Point(30, 35);
		point_hand = new Point(36, 36);
		point_left_shoe = new Point(40, 56);
		point_right_shoe = new Point(34, 56);

		face = new SerialImage(ImageLoader.loadImage("/textures/Player/Smart/DefaultFace.png"));
		hair = new SerialImage(new BufferedImage(72, 72, BufferedImage.TYPE_4BYTE_ABGR));
		eye = new SerialImage(new BufferedImage(72, 72, BufferedImage.TYPE_4BYTE_ABGR));
		eye_whites = new SerialImage(ImageLoader.loadImage("/textures/Player/Smart/DefaultEyes.png"));
		hand = new SerialImage(ImageLoader.loadImage("/textures/Player/Smart/DefaultHands.png"));
		shoe = new SerialImage(
				ImageLoader.loadImage("/textures/Player/Smart/DefaultShoe.png").getSubimage(0, 0, 36, 36));
		body = new SerialImage(ImageLoader.loadImage("/textures/Player/Smart/DefaultBody.png"));
		bare_chest = new SerialImage(ImageLoader.loadImage("/textures/Player/Skin/Body.png"));
		arm = new SerialImage(ImageLoader.loadImage("/textures/Player/Smart/DefaultArms.png"));
		bare_arm = new SerialImage(ImageLoader.loadImage("/textures/Player/Skin/Arm.png"));
		leg = new SerialImage(ImageLoader.loadImage("/textures/Player/Smart/DefaultLegs.png"));
		bare_leg = new SerialImage(ImageLoader.loadImage("/textures/Player/Skin/Leg.png"));
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

		Map<String, Double> data = new HashMap<>();

		return getPose(data);
	}

	public static BufferedImage getPose(Map<String, Double> data) {
		BufferedImage out = new BufferedImage(108, 108, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = out.createGraphics();

		g.addRenderingHints(Tran.mix(Tran.interneighbor, Tran.alias));

		parent("rightarm", "rightforearm", data);
		parent("leftarm", "leftforearm", data);
		parent("rightleg", "rightforeleg", data);
		parent("leftleg", "leftforeleg", data);
		parent("rightforeleg", "rightshoe", data);
		parent("leftforeleg", "leftshoe", data);

		draw(g, hand.crop(36, 0, 36, 36), point_left_arm, 17, 12, "leftforearm", data);
		draw(g, arm.crop(36, 36, 36, 36), point_left_arm, 17, 12, "leftforearm", data);
		draw(g, arm.crop(36, 0, 36, 36), point_left_arm, 17, 12, "leftarm", data);
		draw(g, leg.crop(0, 36, 36, 36), point_left_leg, 18, 12, "leftforeleg", data);
		draw(g, leg.crop(0, 0, 36, 36), point_left_leg, 18, 12, "leftleg", data);
		draw(g, shoe, point_left_shoe, 18, 34, "leftshoe", data);
		draw(g, leg.crop(0, 36, 36, 36), point_right_leg, 18, 12, "rightforeleg", data);
		draw(g, leg.crop(0, 0, 36, 36), point_right_leg, 18, 12, "rightleg", data);
		draw(g, shoe, point_right_shoe, 18, 34, "rightshoe", data);
		draw(g, body.crop(0, 0, 36, 36), point_body, "body", data);
		draw(g, face.crop(0, 0, 36, 36), point_face, 18, 18, "face", data);
		if (!data.containsKey("blinking") || data.get("blinking") == 0)
			draw(g, eye_whites, point_face, 18, 18, "face", data);
		if (!data.containsKey("blinking") || data.get("blinking") == 0)
			draw(g, eye, point_face, 18, 18, "face", data);
		draw(g, hair, point_face, 18, 18, "face", data);
		draw(g, hand.crop(0, 0, 36, 36), point_right_arm, 20, 12, "rightforearm", data);
		draw(g, arm.crop(0, 36, 36, 36), point_right_arm, 20, 12, "rightforearm", data);
		draw(g, arm.crop(0, 0, 36, 36), point_right_arm, 20, 12, "rightarm", data);

		for (int b = 0; b <= 10; b += 1)

			for (String s : data.keySet()) {

				if (s.endsWith("ontop") && data.get(s) > 0 && data.get(s) == b) {

					if (s.startsWith("leftforearm"))
						draw(g, hand.crop(36, 0, 36, 36), point_left_arm, 17, 12, "leftforearm", data);
					if (s.startsWith("leftforearm"))
						draw(g, arm.crop(36, 36, 36, 36), point_left_arm, 17, 12, "leftforearm", data);
					if (s.startsWith("leftarm"))
						draw(g, arm.crop(36, 0, 36, 36), point_left_arm, 17, 12, "leftarm", data);
					if (s.startsWith("leftforeleg"))
						draw(g, leg.crop(0, 36, 36, 36), point_left_leg, 18, 12, "leftforeleg", data);
					if (s.startsWith("leftleg"))
						draw(g, leg.crop(0, 0, 36, 36), point_left_leg, 18, 12, "leftleg", data);
					if (s.startsWith("leftshoe"))
						draw(g, shoe, point_left_shoe, 18, 34, "leftshoe", data);
					if (s.startsWith("rightforeleg"))
						draw(g, leg.crop(0, 36, 36, 36), point_right_leg, 18, 12, "rightforeleg", data);
					if (s.startsWith("rightleg"))
						draw(g, leg.crop(0, 0, 36, 36), point_right_leg, 18, 12, "rightleg", data);
					if (s.startsWith("rightshoe"))
						draw(g, shoe, point_right_shoe, 18, 34, "rightshoe", data);
					if (s.startsWith("body"))
						draw(g, body.crop(0, 0, 36, 36), point_body, "body", data);
					if (s.startsWith("face"))
						draw(g, face.crop(0, 0, 36, 36), point_face, 18, 18, "face", data);
					if (s.startsWith("face") || s.startsWith("whiteye"))
						if (!data.containsKey("blinking") || data.get("blinking") == 0)
							draw(g, eye_whites, point_face, 18, 18, "face", data);
					if (s.startsWith("face") || s.startsWith("eye"))
						if (!data.containsKey("blinking") || data.get("blinking") == 0)
							draw(g, eye, point_face, 18, 18, "face", data);
					if (s.startsWith("face") || s.startsWith("hair"))
						draw(g, hair, point_face, 18, 18, "face", data);
					if (s.startsWith("rightforearm"))
						draw(g, hand.crop(0, 0, 36, 36), point_right_arm, 20, 12, "rightforearm", data);
					if (s.startsWith("rightforearm"))
						draw(g, arm.crop(0, 36, 36, 36), point_right_arm, 20, 12, "rightforearm", data);
					if (s.startsWith("rightarm"))
						draw(g, arm.crop(0, 0, 36, 36), point_right_arm, 20, 12, "rightarm", data);
				}
			}

		g.dispose();

		out.flush();

		return out;
	}

	public static BufferedImage getAccessories(Map<String, Double> data) {
		BufferedImage out = new BufferedImage(108, 108, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = out.createGraphics();

		g.addRenderingHints(Tran.mix(Tran.interneighbor, Tran.alias));

		for (String s : accessories.keySet()) {
			Accessory a = accessories.get(s);
//			Console.log(s, getFrom(s+"rot", data), data.containsKey(s), data);
			parent(a.parent, s, data);
			if(a.parent.contains("fore"))
				parent(a.parent.replaceAll("fore", ""), s, data);
			draw(g, a.image.spaceTo(54-a.orX, 54-a.orY, 108, 108), new Point(a.x, a.y),
					officialData.get(a.parent).orX + officialData.get(a.parent).x,
					officialData.get(a.parent).orY + officialData.get(a.parent).y,
					officialData.get(a.parent.replaceAll("fore", "")).orX
							+ officialData.get(a.parent.replaceAll("fore", "")).x,
					officialData.get(a.parent.replaceAll("fore", "")).orY
							+ officialData.get(a.parent.replaceAll("fore", "")).y,
					s, a.parent, data);
		}

		g.dispose();

		out.flush();

		return out;
	}

	private static void parent(String name, String affected, Map<String, Double> data) {

		data.put(affected + "rot", getFrom(name + "rot", data) + getFrom(affected + "rot", data));
		data.put(affected + "x", getFrom(name + "x", data) + getFrom(affected + "x", data));
		data.put(affected + "y", getFrom(name + "y", data) + getFrom(affected + "y", data));
		
	}

	private static int[] getCrop(String type, Map<String, Double> data) {
		return new int[] { (int) (data.containsKey(type + "cropx") ? data.get(type + "cropx") : 0),
				(int) (data.containsKey(type + "cropy") ? data.get(type + "cropy") : 0),
				(int) (data.containsKey(type + "cropw") ? data.get(type + "cropw") : 0),
				(int) (data.containsKey(type + "croph") ? data.get(type + "croph") : 0) };
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
				.normalizeRotation(Math.toRadians(rot), image.width / 2.0, image.height / 2.0);

		x += image.crop(c[0], c[1], image.width + c[2], image.height + c[3]).normalizedRotOffset(Math.toRadians(rot),
				image.width / 2.0, image.height / 2.0)[0];
		y += image.crop(c[0], c[1], image.width + c[2], image.height + c[3]).normalizedRotOffset(Math.toRadians(rot),
				image.width / 2.0, image.height / 2.0)[1];
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
				img2 = bare_arm.crop(c[0], 36 + c[1], 36 + c[2], 36 + c[3]);
				img2 = img2.flip(flip, 1).normalizeRotation(Math.toRadians(rot), 18, 18);
			} else if (name.equals("leftforearm")) {
				second = true;
				img2 = bare_arm.crop(36 + c[0], 36 + c[1], 36 + c[2], 36 + c[3]);
				img2 = img2.flip(flip, 1).normalizeRotation(Math.toRadians(rot), 18, 18);
			} else if (name.equals("rightforeleg") || name.startsWith("leftforeleg")) {
				second = true;
				img2 = bare_leg.crop(c[0], 36 + c[1], 36 + c[2], 36 + c[3]);
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
				img2 = bare_chest.crop(c[0], c[1], 36 + c[2], 36 + c[3]);
				img2 = img2.flip(flip, 1).normalizeRotation(Math.toRadians(rot), originX, originY);
			} else if (name.equals("rightarm")) {
				second = true;
				img2 = bare_arm.crop(c[0], c[1], 36 + c[2], 36 + c[3]);
				img2 = img2.flip(flip, 1).normalizeRotation(Math.toRadians(rot), originX, originY);
			} else if (name.equals("leftarm")) {
				second = true;
				img2 = bare_arm.crop(36 + c[0], c[1], 36 + c[2], 36 + c[3]);
				img2 = img2.flip(flip, 1).normalizeRotation(Math.toRadians(rot), originX, originY);
			} else if (name.equals("rightleg") || name.startsWith("leftleg")) {
				second = true;
				img2 = bare_leg.crop(c[0], c[1], 36 + c[2], 36 + c[3]);
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

	public static void draw(Graphics2D g, SerialImage image, Point point, double parX,
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
		
		if (parent.contains("fore")) {

			Pos mid = img.rotAround(rad3, new Pos(ox + jointOff.x + jO, oy + jointOff.y),
					new Pos(ox2 + jointOff.x + jO, oy2 + jointOff.y));

			parX = mid.x;
			parY = mid.y;

			Pos finOff = img.rotAround(rad2, finOff2, new Pos(parX, parY));

			x += finOff.x;
			y += finOff.y;

		} else {
			x = finOff2.x;
			y = finOff2.y;

		}

		x += img.normalizedRotOffset(rad, img.width / 2.0, img.height / 2.0)[0];
		y += img.normalizedRotOffset(rad, img.width / 2.0, img.height / 2.0)[1];
		img = img.normalizeRotation(rad, img.width / 2.0, img.height / 2.0);

		BufferedImage o = img.toImage();
		
		g.translate(x, y);
		g.drawImage(o, -54, -54, null);
		img.dump();
		g.setRenderingHints(h);
		g.setTransform(af);
	}

	public static double getFrom(String id, Map<String, Double> data) {
		return (data.containsKey(id) ? data.get(id) : 0);
	}

	// Templates
	public static Template smash = new Template(6, 0.2) {

		@Override
		public void createData() {
			data = new HashMap<>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<>();
			array.add(new KeyFrame(-45.0, 0.0, 1, 2));
			data.put("rightshoerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-1.0, 0.0, 1, 2));
			array.add(new KeyFrame(0.0, 4.0, 0, 2));
			data.put("leftarmx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-1.0, 0.0, 1, 2));
			array.add(new KeyFrame(1.0, 4.0, 0, 2));
			data.put("leftarmy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-180.0, 0.0, 2, 0));
			array.add(new KeyFrame(-40.0, 4.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.5, 0.0, 1, 0));
			data.put("facex", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(25.0, 0.0, 1, 2));
			data.put("rightlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.8000000000000003, 0.0, 0, 2));
			data.put("facey", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(2.4000000000000004, 0.0, 2, 0));
			data.put("y", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-2.0, 4.0, 0, 2));
			data.put("bodyx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 4.0, 0, 2));
			data.put("bodyy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(20.0, 0.0, 1, 2));
			data.put("rightforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(10.0, 4.0, 1, 2));
			data.put("bodyrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-20.0, 0.0, 1, 2));
			data.put("leftlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-180.0, 1.0, 2, 0));
			array.add(new KeyFrame(5.0, 4.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-3.0, 0.0, 0, 2));
			data.put("rightlegx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-2.0, 0.0, 0, 2));
			data.put("leftlegy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-2.0, 0.0, 1, 2));
			array.add(new KeyFrame(0.0, 4.0, 0, 2));
			data.put("rightarmy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-3.0, 0.0, 0, 2));
			data.put("leftlegx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, -1, 0));
			array.add(new KeyFrame(1.0, 2.0, -1, 0));
			array.add(new KeyFrame(0.0, 5.0, -1, 0));
			data.put("blinking", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 0, 2));
			array.add(new KeyFrame(0.0, 4.0, 0, 2));
			data.put("rightarmx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(20.0, 0.0, 1, 2));
			data.put("leftforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-180.0, 0.0, 1, 2));
			data.put("Itemrot", array);

		}

	};

	public static Template stab = new Template(12, 0.4) {

		@Override
		public void createData() {
			data = new HashMap<>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<>();
			array.add(new KeyFrame(-50.0, 0.0, -1, 2));
			data.put("rightshoerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			data.put("rightforearmontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-2.0, 2.0, 0, 2));
			array.add(new KeyFrame(0.0, 4.0, 1, 2));
			data.put("leftforearmx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-59.99999999999999, 2.0, -1, 2));
			array.add(new KeyFrame(-40.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 12.0, 1, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 2, 1));
			array.add(new KeyFrame(-3.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 12.0, 1, 2));
			data.put("leftarmx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-2.0, 2.0, 1, 2));
			array.add(new KeyFrame(0.0, 4.0, 1, 0));
			array.add(new KeyFrame(-2.0, 8.0, 1, 1));
			array.add(new KeyFrame(0.0, 12.0, 1, 2));
			data.put("leftarmy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(50.0, 0.0, 1, 1));
			array.add(new KeyFrame(-90.0, 4.0, 1, 2));
			array.add(new KeyFrame(50.0, 12.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			data.put("rightarmontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			array.add(new KeyFrame(2.0, 4.0, 2, 0));
			array.add(new KeyFrame(1.0, 12.0, 2, 0));
			data.put("facex", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(30.0, 0.0, -1, 2));
			data.put("rightlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("facey", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(3.0, 0.0, 2, 0));
			data.put("y", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(3.0, 0.0, 1, 2));
			data.put("faceontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 4.0, 0, 2));
			data.put("rightforearmx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(20.0, 0.0, -1, 2));
			data.put("rightforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(10.0, 0.0, 1, 1));
			data.put("bodyrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(5.0, 0.0, -1, 2));
			data.put("facerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-20.0, 0.0, -1, 2));
			data.put("leftlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(65.0, 0.0, 0, 2));
			array.add(new KeyFrame(-60.0, 4.0, 1, 2));
			array.add(new KeyFrame(65.0, 12.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-2.5999999999999996, 0.0, 0, 2));
			data.put("leftlegy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, -1, 2));
			array.add(new KeyFrame(1.0, 2.0, 2, 0));
			array.add(new KeyFrame(0.0, 7.0, 2, 0));
			data.put("blinking", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 2, 0));
			array.add(new KeyFrame(3.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 12.0, 1, 2));
			data.put("rightarmx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(20.0, 0.0, -1, 2));
			data.put("leftforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("leftforearmontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, -1, 2));
			array.add(new KeyFrame(1.0, 1.0, -1, 2));
			array.add(new KeyFrame(0.0, 2.0, -1, 2));
			array.add(new KeyFrame(2.0, 8.0, -1, 2));
			data.put("leftarmontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-100.0, 0.0, 1, 2));
			array.add(new KeyFrame(0.0, 4.0, 1, 2));
			array.add(new KeyFrame(-100.0, 12.0, 1, 2));
			data.put("rightforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-180.0, 0.0, 1, 2));
			data.put("Itemrot", array);
		}

	};

	public static Template airkick = new Template(12, 0.35) {

		@Override
		public void createData() {
			data = new HashMap<>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<>();
			array.add(new KeyFrame(50.0, 0.0, 0, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("leftarmy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(45.0, 0.0, 1, 2));
			array.add(new KeyFrame(55.0, 6.0, 1, 2));
			array.add(new KeyFrame(45.0, 12.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.5000000000000004, 0.0, 0, 2));
			data.put("facex", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(20.0, 0.0, 0, 2));
			data.put("rightlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("facey", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(70.0, 0.0, 0, 2));
			array.add(new KeyFrame(50.0, 4.0, 1, 1));
			array.add(new KeyFrame(70.0, 12.0, 1, 1));
			data.put("rightforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(5.0, 0.0, 0, 2));
			array.add(new KeyFrame(8.0, 6.0, 1, 2));
			array.add(new KeyFrame(5.0, 12.0, 1, 2));
			data.put("bodyrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(10.0, 0.0, 0, 2));
			array.add(new KeyFrame(6.0, 6.0, 1, 2));
			array.add(new KeyFrame(10.0, 12.0, 1, 2));
			data.put("facerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-20.0, 0.0, 0, 2));
			array.add(new KeyFrame(-90.0, 4.0, 1, 1));
			array.add(new KeyFrame(-20.0, 12.0, 1, 2));
			data.put("leftlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-30.0, 0.0, 0, 2));
			array.add(new KeyFrame(-40.0, 6.0, 1, 2));
			array.add(new KeyFrame(-30.0, 12.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-1.0, 0.0, 0, 2));
			data.put("leftlegy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 0, 2));
			data.put("leftlegx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 1, 2));
			data.put("blinking", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(70.0, 0.0, 0, 2));
			array.add(new KeyFrame(0.0, 4.0, 1, 1));
			array.add(new KeyFrame(70.0, 12.0, 1, 1));
			data.put("leftforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-85.0, 0.0, 0, 2));
			data.put("rightforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(90.0, 0.0, 1, 2));
			data.put("Itemrot", array);
		}

	};

	public static Template punch = new Template(12, 0.5) {
		public void createData() {
			data = new HashMap<>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<>();
			array.add(new KeyFrame(-40.0, 0.0, 1, 2));
			data.put("rightshoerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-100.0, 0.0, 1, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(3.0, 0.0, 2, 0));
			array.add(new KeyFrame(4.0, 4.0, 1, 1));
			array.add(new KeyFrame(3.0, 12.0, 2, 0));
			data.put("leftarmx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 2, 0));
			array.add(new KeyFrame(2.0, 4.0, 1, 1));
			array.add(new KeyFrame(1.0, 12.0, 2, 0));
			data.put("leftarmy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(30.0, 0.0, 2, 0));
			array.add(new KeyFrame(-80.0, 4.0, 1, 1));
			array.add(new KeyFrame(30.0, 12.0, 2, 0));
			data.put("rightarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(3.0, 0.0, 2, 0));
			array.add(new KeyFrame(5.0, 4.0, 1, 1));
			array.add(new KeyFrame(3.0, 12.0, 2, 0));
			data.put("facex", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(20.0, 0.0, 2, 0));
			array.add(new KeyFrame(25.0, 4.0, 2, 0));
			array.add(new KeyFrame(20.0, 12.0, 1, 1));
			data.put("rightlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 2, 0));
			array.add(new KeyFrame(2.0, 4.0, 1, 1));
			array.add(new KeyFrame(1.0, 12.0, 2, 0));
			data.put("facey", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.7999999999999998, 0.0, 2, 0));
			data.put("y", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 2, 0));
			array.add(new KeyFrame(2.0, 4.0, 1, 1));
			array.add(new KeyFrame(1.0, 12.0, 2, 1));
			data.put("bodyx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(20.0, 0.0, 2, 0));
			array.add(new KeyFrame(13.0, 4.0, 2, 0));
			array.add(new KeyFrame(20.0, 12.0, 2, 0));
			data.put("rightforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(10.0, 0.0, 2, 0));
			array.add(new KeyFrame(20.0, 4.0, 1, 1));
			array.add(new KeyFrame(10.0, 12.0, 1, 2));
			data.put("bodyrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(5.0, 0.0, 2, 0));
			array.add(new KeyFrame(10.0, 4.0, 2, 0));
			array.add(new KeyFrame(5.0, 12.0, 2, 0));
			data.put("facerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-20.0, 0.0, 2, 0));
			array.add(new KeyFrame(-15.0, 4.0, 2, 0));
			array.add(new KeyFrame(-20.0, 12.0, 2, 0));
			data.put("leftlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-5.0, 0.0, 2, 2));
			array.add(new KeyFrame(35.0, 4.0, 1, 1));
			array.add(new KeyFrame(-5.0, 12.0, 1, 1));
			data.put("leftarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-1.3000000000000003, 0.0, 0, 2));
			data.put("leftlegy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-1.0, 0.0, 2, 0));
			array.add(new KeyFrame(0.0, 4.0, 1, 1));
			array.add(new KeyFrame(-1.0, 12.0, 1, 1));
			data.put("rightarmy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, -1, 1));
			array.add(new KeyFrame(1.0, 1.0, -1, 1));
			array.add(new KeyFrame(0.0, 7.0, -1, 1));
			data.put("blinking", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 2, 0));
			array.add(new KeyFrame(3.0, 4.0, 1, 1));
			array.add(new KeyFrame(1.0, 12.0, 2, 0));
			data.put("rightarmx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(20.0, 0.0, 1, 2));
			array.add(new KeyFrame(14.0, 4.0, 2, 0));
			array.add(new KeyFrame(20.0, 12.0, 2, 0));
			data.put("leftforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-120.0, 0.0, 2, 0));
			array.add(new KeyFrame(-10.0, 4.0, 1, 2));
			array.add(new KeyFrame(-120.0, 12.0, 1, 2));
			data.put("rightforearmrot", array);
		}
	};

	public static Template still = new Template(16, 0.05) {

		@Override
		public void createData() {
			data = new HashMap<>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(10.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(-5.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightshoerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-20.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(20.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(20.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(-10.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-1.5, 4.0, 1, 2));
			array.add(new KeyFrame(0, 8.0, 1, 2));
			array.add(new KeyFrame(-0.5, 12.0, 1, 2));
			array.add(new KeyFrame(0, 16.0, 1, 2));
			data.put("y", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-20.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(10.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(10.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(-5.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("leftshoerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-5.0, 0.0, 1, 2));
			array.add(new KeyFrame(0.0, 4.0, 1, 2));
			array.add(new KeyFrame(-5.0, 8.0, 1, 2));
			array.add(new KeyFrame(-25.0, 12.0, 1, 2));
			array.add(new KeyFrame(-5.0, 16.0, 1, 2));
			data.put("rightforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(90.0, 0.0, 1, 2));
			data.put("Itemrot", array);

		}

	};

	public static Template hold = new Template(16, 0.05) {

		@Override
		public void createData() {
			data = new HashMap<>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(10.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(-5.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightshoerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-20.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(20.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(20.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(-10.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-1, 4.0, 1, 2));
			array.add(new KeyFrame(0, 8.0, 1, 2));
			array.add(new KeyFrame(-0.5, 12.0, 1, 2));
			array.add(new KeyFrame(0, 16.0, 1, 2));
			data.put("y", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-20.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(10.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(10.0, 4.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(-5.0, 12.0, 1, 2));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("leftshoerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-5.0, 0.0, 1, 2));
			array.add(new KeyFrame(0.0, 4.0, 1, 2));
			array.add(new KeyFrame(-5.0, 8.0, 1, 2));
			array.add(new KeyFrame(-25.0, 12.0, 1, 2));
			array.add(new KeyFrame(-5.0, 16.0, 1, 2));
			data.put("rightforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(90.0, 0.0, 1, 2));
			data.put("Itemrot", array);

		}

	};

	public static Template walk = new Template(16, 0.3) {

		@Override
		public void createData() {
			data = new HashMap<>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			array.add(new KeyFrame(-15, 3.0, 1, 0));
			array.add(new KeyFrame(15, 7.0, 1, 1));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightshoerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(10.0, 4.0, 1, 2));
			array.add(new KeyFrame(-30, 13.0, 2, 0));
			array.add(new KeyFrame(0.0, 16.0, 2, 0));
			data.put("leftforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(-30.0, 4.0, 2, 2));
			array.add(new KeyFrame(30, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-0.3, 0.0, 1, 1));
			array.add(new KeyFrame(0.3, 4.0, 1, 2));
			array.add(new KeyFrame(-0.6, 12.0, 1, 0));
			array.add(new KeyFrame(-0.3, 16.0, 1, 1));
			data.put("facex", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(30.0, 4.0, 1, 2));
			array.add(new KeyFrame(-35, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 0, 2));
			data.put("rightlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.4, 0.0, 1, 1));
			array.add(new KeyFrame(0.4, 4.0, 1, 2));
			array.add(new KeyFrame(0.5, 12.0, 1, 0));
			array.add(new KeyFrame(0.5, 16.0, 1, 1));
			data.put("facey", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(10.0, 0.0, 1, 2));
			array.add(new KeyFrame(85.0, 8.0, 1, 2));
			array.add(new KeyFrame(-5.0, 14.0, 1, 0));
			array.add(new KeyFrame(10.0, 16.0, 1, 0));
			data.put("rightforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(5.1, 4.0, 1, 2));
			array.add(new KeyFrame(-5.0, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 1));
			data.put("bodyrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-2.1, 0.0, 1, 1));
			array.add(new KeyFrame(5.0, 4.0, 1, 2));
			array.add(new KeyFrame(-5, 12.0, 1, 0));
			array.add(new KeyFrame(-2, 16.0, 1, 1));
			data.put("facerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, -1, 2));
			array.add(new KeyFrame(0.0, 8.0, -1, 2));
			data.put("blinking", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(-40.0, 4.0, 1, 2));
			array.add(new KeyFrame(40.0, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 0));
			data.put("leftlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 2, 1));
			array.add(new KeyFrame(30.0, 4.0, 2, 2));
			array.add(new KeyFrame(-30, 12.0, 2, 0));
			array.add(new KeyFrame(0.0, 16.0, 2, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(85.0, 0.0, 1, 2));
			array.add(new KeyFrame(-5, 6.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(85.0, 16.0, 1, 2));
			data.put("leftforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(15.0, 0.0, 1, 1));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(-15.0, 12.0, 1, 0));
			array.add(new KeyFrame(15.0, 16.0, 1, 2));
			data.put("leftshoerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(-30.0, 5.0, 2, 2));
			array.add(new KeyFrame(10.0, 12.0, 2, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(90.0, 0.0, 1, 2));
			data.put("Itemrot", array);
			
			array = new ArrayList<>();
			array.add(new KeyFrame(-2.5, 0.0, 1, 2));
			data.put("y", array);

		}

	};

	public static Template run = new Template(16, 0.4) {

		@Override
		public void createData() {
			data = new HashMap<>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 0, 1));
			array.add(new KeyFrame(-20, 2.0, 1, 1));
			array.add(new KeyFrame(10.0, 4.0, 1, 0));
			array.add(new KeyFrame(6, 7.0, 1, 1));
			array.add(new KeyFrame(-22, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 0));
			data.put("rightshoerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-77.0, 0.0, 1, 2));
			array.add(new KeyFrame(-55.0, 4.0, 1, 2));
			array.add(new KeyFrame(-100.0, 12.0, 1, 2));
			array.add(new KeyFrame(-77.0, 16.0, 1, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(4.0, 0.0, 1, 2));
			array.add(new KeyFrame(2.0, 8.0, 1, 2));
			array.add(new KeyFrame(4.0, 16.0, 1, 2));
			data.put("leftarmx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			array.add(new KeyFrame(1.0, 8.0, 0, 2));
			array.add(new KeyFrame(2.0, 16.0, 0, 2));
			data.put("leftarmy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(-30.0, 4.0, 1, 2));
			array.add(new KeyFrame(60.0, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(5.0, 0.0, 1, 2));
			array.add(new KeyFrame(2.0, 8.0, 0, 2));
			array.add(new KeyFrame(5.0, 16.0, 1, 2));
			data.put("facex", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(45.0, 4.0, 1, 2));
			array.add(new KeyFrame(-45.0, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 1));
			data.put("rightlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 0, 2));
			array.add(new KeyFrame(1.0, 16.0, 1, 2));
			data.put("facey", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(2.0, 0.0, 1, 2));
			array.add(new KeyFrame(1.0, 8.0, 1, 2));
			array.add(new KeyFrame(2.0, 16.0, 1, 2));
			data.put("bodyx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 0));
			array.add(new KeyFrame(25.0, 4.0, 1, 1));
			array.add(new KeyFrame(55, 8.0, 1, 1));
			array.add(new KeyFrame(0, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("rightforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(15.0, 0.0, 1, 2));
			array.add(new KeyFrame(5.0, 8.0, 1, 2));
			array.add(new KeyFrame(15.0, 16.0, 1, 2));
			data.put("bodyrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(6.0, 0.0, 1, 2));
			array.add(new KeyFrame(0.0, 8.0, 1, 2));
			array.add(new KeyFrame(6.0, 16.0, 1, 2));
			data.put("facerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(-45.0, 4.0, 1, 2));
			array.add(new KeyFrame(45, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 0));
			data.put("leftlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 1));
			array.add(new KeyFrame(60.0, 4.0, 1, 2));
			array.add(new KeyFrame(-30.0, 12.0, 1, 0));
			array.add(new KeyFrame(0.0, 16.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, -1, 2));
			array.add(new KeyFrame(0.0, 8.0, -1, 2));
			data.put("blinking", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(2.0, 0.0, 1, 2));
			array.add(new KeyFrame(1.0, 8.0, 0, 2));
			array.add(new KeyFrame(2.0, 16.0, 1, 2));
			data.put("rightarmx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(70.0, 0.0, 1, 0));
			array.add(new KeyFrame(0.0, 4.0, 1, 1));
			array.add(new KeyFrame(0.1, 8.0, 0, 1));
			array.add(new KeyFrame(70.0, 16.0, 1, 1));
			data.put("leftforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(6.0, 0.0, 1, 1));
			array.add(new KeyFrame(-20, 4.0, 1, 1));
			array.add(new KeyFrame(0.0, 8.0, 1, 0));
			array.add(new KeyFrame(-10.0, 10.0, 1, 1));
			array.add(new KeyFrame(25.0, 12.0, 1, 0));
			array.add(new KeyFrame(30.0, 16.0, 1, 2));
			data.put("leftshoerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-55.0, 0.0, 1, 2));
			array.add(new KeyFrame(-100.0, 4.0, 1, 2));
			array.add(new KeyFrame(-55.0, 12.0, 1, 0));
			array.add(new KeyFrame(-55.0, 16.0, 1, 2));
			data.put("rightforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(180.0, 0.0, 1, 2));
			data.put("Itemrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-2, 0.0, 1, 2));
			data.put("y", array);
			
		}

	};

	public static Template air = new Template(8, 0.05) {

		@Override
		public void createData() {
			data = new HashMap<>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<>();
			array.add(new KeyFrame(20.0, 0.0, 0, 2));
			data.put("rightshoerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-40.0, 0.0, 0, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(60.0, 0.0, 0, 2));
			array.add(new KeyFrame(50.0, 3.0, 1, 2));
			array.add(new KeyFrame(60.0, 8.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("facex", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(10.0, 0.0, 1, 2));
			array.add(new KeyFrame(20.0, 3.0, 1, 2));
			array.add(new KeyFrame(10.0, 8.0, 1, 2));
			data.put("rightlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 0, 2));
			array.add(new KeyFrame(0.5, 4.0, 0, 2));
			array.add(new KeyFrame(0.0, 8.0, 0, 2));
			data.put("facey", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(50.0, 0.0, 0, 2));
			data.put("rightforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(5.0, 0.0, 0, 2));
			data.put("bodyrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(5.0, 0.0, 0, 2));
			array.add(new KeyFrame(7.0, 4.0, 0, 2));
			array.add(new KeyFrame(5.0, 8.0, 0, 2));
			data.put("facerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-30.0, 0.0, 1, 2));
			array.add(new KeyFrame(-40.0, 5.0, 1, 2));
			array.add(new KeyFrame(-30.0, 8.0, 1, 2));
			data.put("leftlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(10.0, 0.0, 1, 2));
			array.add(new KeyFrame(20.0, 5.0, 1, 2));
			array.add(new KeyFrame(10.0, 8.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(50.0, 0.0, 0, 2));
			data.put("leftforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(30.0, 0.0, 0, 2));
			data.put("leftshoerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-40.0, 0.0, 0, 2));
			data.put("rightforearmrot", array);
			
			array = new ArrayList<>();
			array.add(new KeyFrame(90.0, 0.0, 1, 2));
			data.put("Itemrot", array);
			
		}

	};

	public static Template crouch = new Template(8, 0.05) {

		@Override
		public void createData() {
			data = new HashMap<>();
			ArrayList<KeyFrame> array;

			array = new ArrayList<>();
			array.add(new KeyFrame(0.6, 0.0, 0, 2));
			data.put("rightshoey", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(3.0, 0.0, 1, 2));
			data.put("rightforearmontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 0, 2));
			data.put("leftforearmx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			data.put("rightlegontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-1.0, 0.0, 0, 2));
			data.put("leftforearmy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(145.0, 0.0, 1, 2));
			data.put("leftforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("leftarmx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(20.0, 0.0, 1, 2));
			array.add(new KeyFrame(35.0, 4.0, 1, 2));
			array.add(new KeyFrame(20.0, 8.0, 1, 2));
			data.put("rightarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			data.put("rightforelegontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(3.0, 0.0, 1, 2));
			data.put("rightarmontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("leftforelegontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-160.0, 0.0, 0, 2));
			data.put("rightlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(2.0, 0.0, 1, 2));
			array.add(new KeyFrame(1, 4.0, 1, 2));
			array.add(new KeyFrame(2.0, 8.0, 1, 2));
			data.put("facey", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			data.put("rightshoeontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(3.0, 0.0, 1, 2));
			data.put("faceontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-14.0, 0.0, 0, 2));
			data.put("bodycroph", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("leftlegontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(1.0, 0.0, 0, 2));
			data.put("leftshoeontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(160.0, 0.0, 0, 2));
			data.put("rightforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(3.0, 0.0, 0, 2));
			data.put("leftforelegy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(4.0, 0.0, 0, 2));
			data.put("leftforelegx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.6, 0.0, 0, 2));
			data.put("leftshoey", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-160.0, 0.0, 0, 2));
			data.put("leftlegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-25.0, 0.0, 1, 2));
			array.add(new KeyFrame(-45.0, 4.0, 1, 2));
			array.add(new KeyFrame(-25.0, 8.0, 1, 2));
			data.put("leftarmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-5.0, 0.0, 0, 2));
			data.put("rightlegy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-1.0, 0.0, 0, 2));
			data.put("rightlegx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-6.0, 0.0, 0, 2));
			data.put("leftlegy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-3.0, 0.0, 0, 2));
			data.put("leftlegx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 3.0, -1, 0));
			array.add(new KeyFrame(1.0, 5.0, -1, 0));
			array.add(new KeyFrame(0.0, 7.0, -1, 0));
			data.put("blinking", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(160.0, 0.0, 0, 2));
			data.put("leftforelegrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 0, 2));
			data.put("leftshoerot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(3.0, 0.0, 0, 2));
			data.put("leftforearmontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(0.0, 0.0, 1, 2));
			data.put("leftarmontop", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			data.put("rightforelegy", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(2.0, 0.0, 0, 2));
			data.put("rightforelegx", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(-125.0, 0.0, 1, 2));
			data.put("rightforearmrot", array);

			array = new ArrayList<>();
			array.add(new KeyFrame(13.0, 0.0, 1, 2));
			data.put("y", array);
			
			array = new ArrayList<>();
			array.add(new KeyFrame(90.0, 0.0, 1, 2));
			data.put("Itemrot", array);

		}

	};

	public double stilli = 0;

	public Map<String, Double> still() {

		stilli += 0.05;
		if (stilli >= 16)
			stilli = 0;

		Map<String, ArrayList<KeyFrame>> data = new HashMap<>();
		ArrayList<KeyFrame> array;

		array = new ArrayList<>();
		array.add(new KeyFrame(0.0, 0.0, 1, 2));
		array.add(new KeyFrame(10.0, 4.0, 1, 2));
		array.add(new KeyFrame(0.0, 8.0, 1, 2));
		array.add(new KeyFrame(-5.0, 12.0, 1, 2));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("rightshoerot", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(0.0, 0.0, 1, 2));
		array.add(new KeyFrame(-20.0, 4.0, 1, 2));
		array.add(new KeyFrame(0.0, 8.0, 1, 2));
		array.add(new KeyFrame(20.0, 12.0, 1, 2));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("leftforearmrot", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(0.0, 0.0, 1, 2));
		array.add(new KeyFrame(20.0, 4.0, 1, 2));
		array.add(new KeyFrame(0.0, 8.0, 1, 2));
		array.add(new KeyFrame(-10.0, 12.0, 1, 2));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("rightarmrot", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(0.0, 0.0, 1, 2));
		array.add(new KeyFrame(-1, 4.0, 1, 2));
		array.add(new KeyFrame(0, 8.0, 1, 2));
		array.add(new KeyFrame(-0.5, 12.0, 1, 2));
		array.add(new KeyFrame(0, 16.0, 1, 2));
		data.put("y", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(0.0, 0.0, 1, 2));
		array.add(new KeyFrame(-20.0, 4.0, 1, 2));
		array.add(new KeyFrame(0.0, 8.0, 1, 2));
		array.add(new KeyFrame(10.0, 12.0, 1, 2));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("leftarmrot", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(0.0, 0.0, 1, 2));
		array.add(new KeyFrame(10.0, 4.0, 1, 2));
		array.add(new KeyFrame(0.0, 8.0, 1, 2));
		array.add(new KeyFrame(-5.0, 12.0, 1, 2));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("leftshoerot", array);

		array = new ArrayList<>();
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

	public double walki = 0.0;

	public Map<String, Double> walk() {
		walki += 0.3;
		if (walki >= 16)
			walki = 0;

		Map<String, ArrayList<KeyFrame>> data = new HashMap<>();
		ArrayList<KeyFrame> array;

		array = new ArrayList<>();
		array.add(new KeyFrame(0.0, 0.0, 1, 2));
		array.add(new KeyFrame(-15, 3.0, 1, 0));
		array.add(new KeyFrame(15, 7.0, 1, 1));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("rightshoerot", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(10.0, 4.0, 1, 2));
		array.add(new KeyFrame(-30, 13.0, 2, 0));
		array.add(new KeyFrame(0.0, 16.0, 2, 0));
		data.put("leftforearmrot", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(-30.0, 4.0, 2, 2));
		array.add(new KeyFrame(30, 12.0, 1, 0));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("rightarmrot", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(-0.3, 0.0, 1, 1));
		array.add(new KeyFrame(0.3, 4.0, 1, 2));
		array.add(new KeyFrame(-0.6, 12.0, 1, 0));
		array.add(new KeyFrame(-0.3, 16.0, 1, 1));
		data.put("facex", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(30.0, 4.0, 1, 2));
		array.add(new KeyFrame(-35, 12.0, 1, 0));
		array.add(new KeyFrame(0.0, 16.0, 0, 2));
		data.put("rightlegrot", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(0.4, 0.0, 1, 1));
		array.add(new KeyFrame(0.4, 4.0, 1, 2));
		array.add(new KeyFrame(0.5, 12.0, 1, 0));
		array.add(new KeyFrame(0.5, 16.0, 1, 1));
		data.put("facey", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(10.0, 0.0, 1, 2));
		array.add(new KeyFrame(85.0, 8.0, 1, 2));
		array.add(new KeyFrame(-5.0, 14.0, 1, 0));
		array.add(new KeyFrame(10.0, 16.0, 1, 0));
		data.put("rightforelegrot", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(5.1, 4.0, 1, 2));
		array.add(new KeyFrame(-5.0, 12.0, 1, 0));
		array.add(new KeyFrame(0.0, 16.0, 1, 1));
		data.put("bodyrot", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(-2.1, 0.0, 1, 1));
		array.add(new KeyFrame(5.0, 4.0, 1, 2));
		array.add(new KeyFrame(-5, 12.0, 1, 0));
		array.add(new KeyFrame(-2, 16.0, 1, 1));
		data.put("facerot", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(1.0, 0.0, -1, 2));
		array.add(new KeyFrame(0.0, 8.0, -1, 2));
		data.put("blinking", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(-40.0, 4.0, 1, 2));
		array.add(new KeyFrame(40.0, 12.0, 1, 0));
		array.add(new KeyFrame(0.0, 16.0, 1, 0));
		data.put("leftlegrot", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(0.0, 0.0, 2, 1));
		array.add(new KeyFrame(30.0, 4.0, 2, 2));
		array.add(new KeyFrame(-30, 12.0, 2, 0));
		array.add(new KeyFrame(0.0, 16.0, 2, 2));
		data.put("leftarmrot", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(85.0, 0.0, 1, 2));
		array.add(new KeyFrame(-5, 6.0, 1, 2));
		array.add(new KeyFrame(0.0, 8.0, 1, 2));
		array.add(new KeyFrame(85.0, 16.0, 1, 2));
		data.put("leftforelegrot", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(15.0, 0.0, 1, 1));
		array.add(new KeyFrame(0.0, 8.0, 1, 2));
		array.add(new KeyFrame(-15.0, 12.0, 1, 0));
		array.add(new KeyFrame(15.0, 16.0, 1, 2));
		data.put("leftshoerot", array);

		array = new ArrayList<>();
		array.add(new KeyFrame(0.0, 0.0, 1, 1));
		array.add(new KeyFrame(-30.0, 5.0, 2, 2));
		array.add(new KeyFrame(10.0, 12.0, 2, 0));
		array.add(new KeyFrame(0.0, 16.0, 1, 2));
		data.put("rightforearmrot", array);

		return fromKeyframes(walki, data);
	}

	public static Map<String, Double> fromKeyframes(double index, Map<String, ArrayList<KeyFrame>> data) {
		Map<String, Double> out = new HashMap<>();

		for (String s : data.keySet()) {
			out.put(s, KeyFrame.getValue(data.get(s), index));
		}

		return out;
	}

	public static Map<String, Double> get(Template t) {
		return t.get();
	}

	public static Animation getFakeAnimation(Template t) {
		return Template.getFakeAnimation(t);
	}

	public static void imitate(Animation a, Template t) {
		t.index = a.index * t.speed;
	}

	public static void setSpeed(Template t, double speed) {
		t.speed=speed;
	}
	
}

abstract class Template {

	int length;
	double speed, index = 0;

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

	public static Animation getFakeAnimation(Template t) {
		BufferedImage[] frames = new BufferedImage[(int) (t.length / t.speed)];

		t.index = 0;

		BufferedImage b = new BufferedImage(108, 108, BufferedImage.TYPE_4BYTE_ABGR);

		Arrays.fill(frames, b);

		return new Animation(1, frames, "Generated", "Smart Costume");

	}

}
