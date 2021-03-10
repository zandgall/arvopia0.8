package com.zandgall.arvopia.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Reporter;
import com.zandgall.arvopia.SmartCostume;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.SerialImage;
import com.zandgall.arvopia.gfx.transform.HueCube;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.input.KeyManager;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.IconButton;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.TabMenu;
import com.zandgall.arvopia.utils.TextEditor;
import com.zandgall.arvopia.utils.Utils;

public class CustomizationState extends State {

	public static Color[] SKINCOLORS = new Color[] { new Color(254, 227, 198), new Color(253, 231, 173),
			new Color(248, 217, 152), new Color(249, 212, 160),

			new Color(236, 192, 145), new Color(242, 194, 128), new Color(212, 158, 122), new Color(187, 101, 54),

			new Color(207, 150, 95), new Color(173, 138, 96), new Color(147, 95, 55),

			new Color(178, 102, 68), new Color(127, 68, 34), };

	private TextEditor user;

	int selected = 0;

	Map<String, typeIndex> indexes;
	Map<String, Costume> costumes;

	private HueCube hue;

	private upArrow valueUp;
	private downArrow valueDown;

	private Button back, export, random;

	TabMenu tabs;

	boolean useCostume = false;
	String costumeIndex = "";

	SmartCostume c;

	public CustomizationState(Handler handler) {
		super(handler);

		valueUp = new upArrow(handler, 232, 36);
		valueDown = new downArrow(handler, 232, 314);

		back = new Button(handler, 10, handler.getHeight() - 40, "Go back", "Back");
		export = new Button(handler, 70, handler.getHeight() - 40, "Exports the image to C:\\Arvopia\\Player0.8\\out.png",
				"Export");
		random = new Button(handler, 150, handler.getHeight() - 40, "Randomizes the selections", "Randomize");

		Reporter.init();
		user = new TextEditor(handler, 20, 40, 200, 1, Reporter.user, Public.defaultFont.deriveFont(20));

		costumes = new HashMap<String, Costume>();
		init();

		c = new SmartCostume();

		tabs = new TabMenu(handler, new String[] { "Hair", "Face", "Eyes", "Pupils", "Body", "Arms", "Pants", "Shoes",
				"Hands", "Costumes" }, 0, 0, handler.getWidth(), handler.getHeight(), true);
	}

	public void save() {
		if (!FileLoader.readFile("C:\\Arvopia\\01.arv").contains("No Name") && Reporter.user != user.getContent()) {
			Reporter.addUser();
			Achievement.award(Achievement.noname);
		}

		Reporter.user = user.getContent();

		/*
		 * ImageLoader.addRedirect("PlayerFace", Tran.effectColor(faceIn.get(), faceC));
		 * ImageLoader.addRedirect("PlayerHair", Tran.effectColor(hairIn.get(), hairC));
		 * ImageLoader.addRedirect("PlayerEyes", Tran.effectColor(pupilsIn.get(),
		 * pupilsC)); ImageLoader.addRedirect("PlayerShirt",
		 * Tran.effectColor(bodyIn.get(), bodyC));
		 * ImageLoader.addRedirect("PlayerHands", Tran.effectColor(handsIn.get(),
		 * handsC)); ImageLoader.addRedirect("PlayerShoes",
		 * Tran.effectColor(shoesIn.get(), shoesC));
		 * ImageLoader.addRedirect("PlayerPants", Tran.effectColor(pantsIn.get(),
		 * pantsC)); ImageLoader.addRedirect("PlayerPupils",
		 * Tran.effectColor(eyesIn.get(), eyesC));
		 * 
		 * BufferedImage clear = new BufferedImage(faceIn.get().getWidth(),
		 * faceIn.get().getHeight(), BufferedImage.TYPE_4BYTE_ABGR); if(useCostume) {
		 * ImageLoader.addRedirect("PlayerFace", costumes.get(costumeIndex).src());
		 * ImageLoader.addRedirect("PlayerHair", clear);
		 * ImageLoader.addRedirect("PlayerEyes", clear);
		 * ImageLoader.addRedirect("PlayerShirt", clear);
		 * ImageLoader.addRedirect("PlayerHands", clear);
		 * ImageLoader.addRedirect("PlayerShoes", clear);
		 * ImageLoader.addRedirect("PlayerPants", clear);
		 * ImageLoader.addRedirect("PlayerPupils", clear);
		 * ImageLoader.addRedirect("PlayerShine", clear); }
		 * 
		 * Reporter.fc = faceC; Reporter.hc = hairC; Reporter.ec = pupilsC; Reporter.shc
		 * = bodyC; Reporter.hac = handsC; Reporter.scs = shoesC; Reporter.pc = pantsC;
		 * Reporter.ep = eyesC; Reporter.save();
		 * 
		 * String s = faceIn.index + System.lineSeparator(); s += hairIn.index +
		 * System.lineSeparator(); s += pupilsIn.index + System.lineSeparator(); s +=
		 * handsIn.index + System.lineSeparator(); s += bodyIn.index +
		 * System.lineSeparator(); s += shoesIn.index + System.lineSeparator(); s +=
		 * pantsIn.index + System.lineSeparator(); s += eyesIn.index +
		 * System.lineSeparator(); s += useCostume + System.lineSeparator(); s +=
		 * costumeIndex + System.lineSeparator(); Utils.fileWriter(s,
		 * "C:\\Arvopia\\Player0.8\\Indexes.txt"); System.out.println("Saved indexes " +
		 * s);
		 */

		String s = "";

		for (String in : indexes.keySet()) {
			if(indexes.get(in)==null)
				Console.log("Index is null");
			else if(indexes.get(in).c==null)
				Console.log("Color is null");
			s += in + " " + indexes.get(in).c.getRed() + " " + indexes.get(in).c.getGreen() + " "
					+ indexes.get(in).c.getBlue() + " " + indexes.get(in).index + System.lineSeparator();
		}
		Utils.fileWriter(s, "C:\\Arvopia\\Player0.8\\Indexes.txt");
		
		SmartCostume.body = new SerialImage(indexes.get("Body").getCo());
		SmartCostume.arm = new SerialImage(indexes.get("Arms").getCo());
		SmartCostume.hair = new SerialImage(indexes.get("Hair").getCo());
		SmartCostume.hand = new SerialImage(indexes.get("Hands").getCo());
		SmartCostume.leg = new SerialImage(indexes.get("Pants").getCo());
		SmartCostume.eye = new SerialImage(indexes.get("Pupils").getCo());
		SmartCostume.whiteye = new SerialImage(indexes.get("Eyes").getCo());
		SmartCostume.face = new SerialImage(indexes.get("Face").getCo());
		SmartCostume.shoe = new SerialImage(indexes.get("Shoes").getCo().getSubimage(0, 0, 36, 36));
		SmartCostume.barearm = new SerialImage(
				Tran.effectColor(ImageLoader.loadImage("/textures/Player/Skin/Arm.png"), indexes.get("Face").c));
		SmartCostume.bareleg = new SerialImage(
				Tran.effectColor(ImageLoader.loadImage("/textures/Player/Skin/Leg.png"), indexes.get("Face").c));
		SmartCostume.barefoot = new SerialImage(
				Tran.effectColor(ImageLoader.loadImage("/textures/Player/Skin/Feet.png").getSubimage(0, 0, 36, 36),
						indexes.get("Face").c));
		SmartCostume.barechest = new SerialImage(
				Tran.effectColor(ImageLoader.loadImage("/textures/Player/Skin/Body.png").getSubimage(0, 0, 36, 36),
						indexes.get("Face").c));
		
	}

	String pre = "nothing";

	@Override
	public void tick() {
		back.tick();
		export.tick();
		user.tick();
		random.tick();

		if (back.on) {
			State.setState(handler.getGame().menuState);

			save();
		}

		if (export.on)
			write();

		if (random.on)
			randomize();

		hue.tick(handler.getMouse());

		tabs.tick();
		String val = tabs.getTab();

		valueUp.tick();
		valueDown.tick();

		valueUp.selected = valueUp.selected || (KeyManager.checkBind("Up"));
		valueDown.selected = valueDown.selected || (KeyManager.checkBind("Down"));

		if (valueUp.selected || valueDown.selected) {
			useCostume = false;
		}

		for (String s : indexes.keySet()) {
			if (val.equals(s)) {
				if (pre != val)
					hue.setColor(indexes.get(s).c);
				indexes.get(s).c = hue.getColor();
				if (valueUp.selected)
					indexes.get(s).up();
				if (valueDown.selected)
					indexes.get(s).down();
				if (val.equals("Body"))
					indexes.get("Arms").c = hue.getColor();
				if (val.equals("Arms"))
					indexes.get("Body").c = hue.getColor();
			}
		}

		SmartCostume.body = new SerialImage(indexes.get("Body").getCo());
		SmartCostume.arm = new SerialImage(indexes.get("Arms").getCo());
		SmartCostume.hair = new SerialImage(indexes.get("Hair").getCo());
		SmartCostume.hand = new SerialImage(indexes.get("Hands").getCo());
		SmartCostume.leg = new SerialImage(indexes.get("Pants").getCo());
		SmartCostume.eye = new SerialImage(indexes.get("Pupils").getCo());
		SmartCostume.whiteye = new SerialImage(indexes.get("Eyes").getCo());
		SmartCostume.face = new SerialImage(indexes.get("Face").getCo());
		SmartCostume.shoe = new SerialImage(indexes.get("Shoes").getCo().getSubimage(0, 0, 36, 36));
		SmartCostume.barearm = new SerialImage(
				Tran.effectColor(ImageLoader.loadImageEX("Player/Skin/Arm.png"), indexes.get("Face").c));
		SmartCostume.bareleg = new SerialImage(
				Tran.effectColor(ImageLoader.loadImageEX("Player/Skin/Leg.png"), indexes.get("Face").c));
		SmartCostume.barefoot = new SerialImage(
				Tran.effectColor(ImageLoader.loadImageEX("Player/Skin/Feet.png").getSubimage(0, 0, 36, 36),
						indexes.get("Face").c));
		SmartCostume.barechest = new SerialImage(
				Tran.effectColor(ImageLoader.loadImageEX("Player/Skin/Body.png").getSubimage(0, 0, 36, 36),
						indexes.get("Face").c));

//		SmartCostume.setup();

		if (val.equals("Costumes")) {
			for (String s : costumes.keySet()) {
				costumes.get(s).tick();
				if (costumes.get(s).on()) {
					useCostume = true;
					costumeIndex = s;
				}
			}
		}

		pre = tabs.getTab();

		if (KeyManager.function[2])
			init();
	}

	@Override
	public void render(Graphics2D g) {

		g.setColor(new Color(150, 200, 255));
		g.fillRect(0, 0, handler.width, handler.height);

		tabs.render(g);

		user.render(g);
		export.render(g);
		back.render(g);
		random.render(g);

		if (tabs.getTab() != "Costumes")
			hue.render(g);
		else
			for (Costume c : costumes.values())
				c.render(g);

		if (useCostume) {
			g.drawImage(costumes.get(costumeIndex).src().getSubimage(0, 0, 36, 54), 10, 22, 216, 328, null);
		} else {
			if (indexes.containsKey(tabs.getTab())) {
				typeIndex in = indexes.get(tabs.getTab());
				for (int i = Math.max(in.i() - 2, 0); i < Math.min(in.types.size(), in.i() + 2); i++) {
					g.drawImage(in.getCo(i).getSubimage(0, 0, 36, 36), 212, 132 - (i - in.i()) * 48, 90, 90, null);
				}
			}

			g.drawImage(SmartCostume.getStill(), -100, -10, 432, 432, null);
//			g.drawImage(SmartCostume.getPose(SmartCostume.get(SmartCostume.run)), -50, 30, 216, 216, null);
		}

		valueUp.render(g);
		valueDown.render(g);

		g.setFont(Public.defaultBoldFont.deriveFont(20.0f));
//		Tran.drawOutlinedText(g, 270d, handler.getHeight()-10, "Game points: " + (Utils.parseInt(FileLoader.readFile("C:\\Arvopia\\00.arv")) + Utils.parseInt(FileLoader.readFile("C:\\Arvopia\\02.arv"))), 1, Color.black, Color.white);

	}

	public void initIndex() {
		System.out.println("players not printing maybe?");
		File parent;
		parent = new File("Player/Face");
		if (parent != null)
			System.out.println("\t\tPlayer textures exist? : " + (parent.exists()) + " Has kids? : "
					+ (parent.list() != null && parent.list().length > 0));
		Utils.createDirectory("C:\\Arvopia\\Player0.8\\Face");
		Utils.createDirectory("C:\\Arvopia\\Player0.8\\Arms");
		Utils.createDirectory("C:\\Arvopia\\Player0.8\\Hair");
		Utils.createDirectory("C:\\Arvopia\\Player0.8\\Eyes");
		Utils.createDirectory("C:\\Arvopia\\Player0.8\\Pants");
		Utils.createDirectory("C:\\Arvopia\\Player0.8\\Hands");
		Utils.createDirectory("C:\\Arvopia\\Player0.8\\Body");
		Utils.createDirectory("C:\\Arvopia\\Player0.8\\Shoes");
		Utils.createDirectory("C:\\Arvopia\\Player0.8\\Pupils");
		Utils.createDirectory("C:\\Arvopia\\Player0.8\\Costumes");
		Utils.createDirectory("C:\\Arvopia\\Player0.8\\Skin");

//		if(true)
//			return;

		copyImages("Face");
		copyImages("Arms");
		copyImages("Hair");
		copyImages("Eyes");
		copyImages("Pants");
		copyImages("Hands");
		copyImages("Body");
		copyImages("Shoes");
		copyImages("Pupils");
		copyImages("Costumes");
		copyImages("Skin");

		// Using TODO

		indexes = new HashMap<String, typeIndex>();

		initiateIndex("Face");
		initiateIndex("Arms");
		initiateIndex("Hair");
		initiateIndex("Eyes");
		initiateIndex("Pants");
		initiateIndex("Hands");
		initiateIndex("Body");
		initiateIndex("Shoes");
		initiateIndex("Pupils");

		int cx = 0, cy = 0;

		for (String s : new File("C:\\Arvopia\\Player0.8\\Costumes").list()) {
			costumes.put(s, new Costume(handler, ImageLoader.loadImageEX("C:\\Arvopia\\Player0.8\\Costumes\\" + s), s, s,
					handler.getWidth() - 375 + cx, 50 + cy));

			cx += 73;

			if (cx + 73 > 375) {
				cx = 0;
				cy += 109;
			}
		}

	}

	private void copyImages(String path) {
		for (String s : new File("Player/" + path).list()) {
			ImageLoader.saveImage(ImageLoader.loadImageEX("Player/" + path + "/" + s),
					"C:\\Arvopia\\Player0.8\\" + path + "\\" + s);
		}
	}

	private void initiateIndex(String path) {
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		for (String s : new File("C:\\Arvopia\\Player0.8\\" + path).list()) {
			images.add(ImageLoader.loadImageEX("C:\\Arvopia\\Player0.8\\" + path + "\\" + s));
		}
		indexes.put(path, new typeIndex(images));
	}

	@Override
	public void init() {
		hue = new HueCube(handler.getWidth() - 375, 50, 300, 300);

		String parentFiles = "Player/";

		initIndex();

		if (new File("C:\\Arvopia\\Player0.8\\Indexes.txt").exists()) {
			String orig = FileLoader.readFile("C:\\Arvopia\\Player0.8\\Indexes.txt");
			for (String line : orig.split(System.lineSeparator())) {
				if (indexes.containsKey(line.split("\\s+")[0])) {
					String[] codes = line.split("\\s+");
					indexes.get(codes[0]).c = new Color(Utils.parseInt(codes[1]), Utils.parseInt(codes[2]),
							Utils.parseInt(codes[3]));
					indexes.get(codes[0]).index = Utils.parseInt(codes[4]);
				}
			}
		} else {
			indexes.get("Face").c = Reporter.fc;
			indexes.get("Body").c = Reporter.shc;
			indexes.get("Arms").c = Reporter.shc;
			indexes.get("Hands").c = Reporter.hac;
			indexes.get("Hair").c = Reporter.hc;
			indexes.get("Pants").c = Reporter.pc;
			indexes.get("Pupils").c = Reporter.ec;
			indexes.get("Eyes").c = Reporter.ep;
			indexes.get("Shoes").c = Reporter.scs;
		}

		save();
	}

	public void write() {
		BufferedImage image = getFull();

		ImageLoader.saveImage(image, "C:\\Arvopia\\Player0.8\\out.png");

	}

	public static Color generateHairColor() {
		float h = (float) Public.debugRandom(0.05, 0.15);
		float s = (float) Public.debugRandom(0.35, 0.85);
		float v = (float) Public.debugRandom(0.05, 0.85);
		return Color.getHSBColor(h, s, v);
	}

	public static Color generateSkinColor() {
		float h = (float) Public.debugRandom(0.02, 0.15);
		float s = (float) Public.debugRandom(0.25, 0.65);
		float v = (float) Public.debugRandom(0.45, 0.98);
		return Color.getHSBColor(h, s, v);
	}

	public void randomize() {
		for (String s : indexes.keySet())
			indexes.get(s).random();

		Color c = generateSkinColor();

		indexes.get("Eyes").c = new Color((int) Public.random(245, 255), (int) Public.random(240, 255), 255);
		indexes.get("Face").c = c;
		indexes.get("Pupils").c = Tran.randomColor();
		if (Public.chance(1))
			indexes.get("Hair").c = Tran.randomColor();
		else
			indexes.get("Hair").c = generateHairColor();
		indexes.get("Pants").c = Tran.randomColor();
		indexes.get("Hands").c = c;
		indexes.get("Body").c = Tran.randomColor();
		indexes.get("Arms").c = indexes.get("Body").c;
		if (Public.chance(1))
			indexes.get("Shoes").c = Tran.randomColor();
		else
			indexes.get("Shoes").c = generateHairColor();

		pre = "nothing";
	}

	public BufferedImage getRandom() {
		/*
		 * Color c = generateSkinColor();
		 * 
		 * Color EyePupilsColor = new Color((int) Public.random(245, 255), (int)
		 * Public.random(240, 255), 255); Color FaceColor = c; Color EyeColor =
		 * Tran.randomColor(); Color HairColor; if (Public.chance(1)) HairColor =
		 * Tran.randomColor(); else HairColor = generateHairColor(); Color PantsColor =
		 * Tran.randomColor(); Color HandsColor = c; Color ShirtColor =
		 * Tran.randomColor(); Color ShoesColor; if (Public.chance(1)) ShoesColor =
		 * Tran.randomColor(); else ShoesColor = generateHairColor();
		 * 
		 * BufferedImage EyePupils = Tran.effectColor(eyesIn.getRandom(),
		 * EyePupilsColor); BufferedImage Face = Tran.effectColor(faceIn.getRandom(),
		 * FaceColor); BufferedImage Hair = Tran.effectColor(hairIn.getRandom(),
		 * HairColor); BufferedImage Hands = Tran.effectColor(handsIn.getRandom(),
		 * HandsColor); BufferedImage Shirt = Tran.effectColor(bodyIn.getRandom(),
		 * ShirtColor); BufferedImage Eyes = Tran.effectColor(pupilsIn.getRandom(),
		 * EyeColor); BufferedImage Pants = Tran.effectColor(pantsIn.getRandom(),
		 * PantsColor); BufferedImage Shoes = Tran.effectColor(shoesIn.getRandom(),
		 * ShoesColor);
		 * 
		 * BufferedImage image = new BufferedImage(Face.getWidth(), Face.getHeight(),
		 * BufferedImage.TYPE_4BYTE_ABGR); Graphics2D g = image.createGraphics();
		 * 
		 * g.drawImage(EyePupils, 0, 0, null); g.drawImage(Face, 0, 0, null);
		 * g.drawImage(Eyes, 0, 0, null); g.drawImage(Pants, 0, 0, null);
		 * g.drawImage(Hands, 0, 0, null); g.drawImage(Shirt, 0, 0, null);
		 * g.drawImage(Hair, 0, 0, null); g.drawImage(Shoes, 0, 0, null);
		 * g.drawImage(Shine, 0, 0, null);
		 * 
		 * g.dispose();
		 */ BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_4BYTE_ABGR);
		return image;
	}

	public BufferedImage getFull() {
		return SmartCostume.getStill();
	}

	@Override
	public void renderGUI(Graphics2D g2d) {

	}

}

class typeIndex {
	ArrayList<BufferedImage> types;

	int index = 0;

	Color c;

	public typeIndex(ArrayList<BufferedImage> types) {
		this.types = types;
	}

	public void up() {
		index++;
		index = (int) Public.range(0, types.size() - 1, index);
	}

	public void down() {
		index--;
		index = (int) Public.range(0, types.size() - 1, index);
	}

	public BufferedImage get() {
		return get(i());
	}

	public BufferedImage getCo() {
		return Tran.effectColor(get(), c);
	}

	public BufferedImage getCo(int i) {
		return Tran.effectColor(get(i), c);
	}

	public void setColor(Color c) {
		this.c = c;
	}

	public void random() {
		index = (int) Public.random(0, types.size() - 1);
	}

	public BufferedImage getRandom() {
		return get((int) Public.random(0, types.size() - 1));
	}

	public BufferedImage get(int i) {
		if (types.size() <= i) {
			if (types.size() == 0)
				return null;
			else
				return types.get(types.size() - 1);
		}
		return types.get(i);
	}

	public int i() {
		return index;
	}

	public void add(BufferedImage image) {
		types.add(image);
//		index = types.size()-1;
	}

	public void add(String path) {
		add(ImageLoader.loadImage(path));
	}

}

class upArrow {

	BufferedImage d, h, s;

	int x, y, width, height;

	Handler game;

	boolean hovered = false, selected = false, preSel = false;

	public upArrow(Handler game, int x, int y) {
		this.game = game;
		this.x = x;
		this.y = y;
		this.height = 11;
		d = ImageLoader.loadImage("/textures/Gui/GuiArrows.png").getSubimage(0, 0, 16, height);
		h = Tran.staticShader(d, 300, 150);
		s = Tran.staticShader(d, 175, 0);
		this.height = 44;
		this.width = d.getWidth() * 4;
	}

	public void tick() {
		hovered = game.getMouse().touches(x, y, x + width, y + height);

		selected = hovered && (game.getMouse().fullLeft) && !preSel;
		preSel = (game.getMouse().fullLeft);
	}

	public void render(Graphics2D g) {
		if (selected)
			g.drawImage(s, x, y, width, height, null);
		else if (hovered)
			g.drawImage(h, x, y, width, height, null);
		else
			g.drawImage(d, x, y, width, height, null);
	}

}

class downArrow {

	BufferedImage d, h, s;

	int x, y, width, height;

	Handler game;

	boolean hovered = false, selected = false, preSel = false;

	public downArrow(Handler game, int x, int y) {
		this.game = game;
		this.x = x;
		this.y = y;
		this.height = 11;
		d = ImageLoader.loadImage("/textures/Gui/GuiArrows.png").getSubimage(0, 11, 16, height);
		h = Tran.staticShader(d, 300, 150);
		s = Tran.staticShader(d, 175, 0);
		this.height = 44;
		this.width = d.getWidth() * 4;
	}

	public void tick() {
		hovered = game.getMouse().touches(x, y, x + width, y + height);

		selected = hovered && (game.getMouse().fullLeft) && !preSel;
		preSel = (game.getMouse().fullLeft);
	}

	public void render(Graphics2D g) {
		if (selected)
			g.drawImage(s, x, y, width, height, null);
		else if (hovered)
			g.drawImage(h, x, y, width, height, null);
		else
			g.drawImage(d, x, y, width, height, null);
	}

}

class Costume {

	Handler game;

	BufferedImage src;

	IconButton button;

	Button buy;

	public Costume(Handler game, BufferedImage src, String name, String description, int x, int y) {
		this.game = game;
		this.src = src;
		button = new IconButton(game, x, y, name, Tran.scaleUp(src.getSubimage(0, 0, 36, 54), 2.0f), description);

		if (new File("C:\\Arvopia\\05.arv").exists()) {
			if (!FileLoader.readFile("C:\\Arvopia\\05.arv").contains(name))
				button.locked = true;
		} else {
			button.locked = true;
			try {
				System.out.println("Created 05.arv " + new File("C:\\Arvopia\\05.arv").createNewFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		buy = new Button(game, x + 36, y + 50, "500 points", "Buy", 1);
	}

	public void tick() {
		button.tick();
		if (button.locked) {
			buy.locked = (Handler.getGamePoints() < 500);
			buy.tick();
			if (buy.on) {
				button.locked = false;
				Utils.existWriter(FileLoader.readFile("C:\\Arvopia\\05.arv") + System.lineSeparator() + button.name,
						"C:\\Arvopia\\05.arv");
				Utils.existWriter("" + (Utils.parseInt(FileLoader.readFile("C:\\Arvopia\\02.arv")) - 500),
						"C:\\Arvopia\\02.arv");
			}
		}
	}

	public boolean on() {
		return button.on;
	}

	public BufferedImage src() {
		return src;
	}

	public void render(Graphics2D g) {
		Tran.TEXT_MODE = 2;
		g.setColor(Color.black);
		g.setFont(Public.defaultBoldFont.deriveFont(12f));
		Tran.drawString(g, button.name.replaceAll(".png", ""), button.x + 36, button.y - 10);
		Tran.TEXT_MODE = 0;
		button.render(g);
		if (button.locked) {
			if (buy.hovered)
				buy.data = true;
			buy.render(g);
		}
	}

}
