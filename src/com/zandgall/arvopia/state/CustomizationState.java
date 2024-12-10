package com.zandgall.arvopia.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.zandgall.arvopia.*;
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

	private final TextEditor user;
	private final upArrow valueUp;
	private final downArrow valueDown;
	private final Button back, export, random;
	private final TabMenu tabs;
	private String previousTab = "None";

	private Map<String, typeIndex> indexes;
	private final Map<String, Costume> costumes;

	private HueCube hue;

	boolean useCostume = false;
	private String costumeIndex = "";

	public CustomizationState(Handler handler) {
		super(handler);

		valueUp = new upArrow(handler, 232, 36);
		valueDown = new downArrow(handler, 232, 314);
		back = new Button(handler, 10, handler.getHeight() - 40, "Go back", "Back");
		export = new Button(handler, 70, handler.getHeight() - 40, "Exports the image to Arvopia/Player0.8/out.png",
				"Export");
		random = new Button(handler, 150, handler.getHeight() - 40, "Randomizes the selections", "Randomize");
		user = new TextEditor(handler, 20, 40, 200, 1, Reporter.user, Public.defaultFont.deriveFont(20.0F));
		tabs = new TabMenu(handler, new String[] { "Hair", "Face", "Eyes", "Pupils", "Body", "Arms", "Pants", "Shoes",
				"Hands", "Costumes" }, 0, 0, handler.getWidth(), handler.getHeight(), true);

//		Reporter.init();

		costumes = new HashMap<>();
		init();

	}

	public void save() {
		if (!FileLoader.readFile(Game.prefix + "/01.arv").contains("No Name") && !Reporter.user.equals(user.getContent())) {
			Reporter.user = user.getContent();
			Reporter.addUser();
			Achievement.award(Achievement.noname);
		}

		StringBuilder s = new StringBuilder();

		for (String in : indexes.keySet()) {
			if(indexes.get(in)==null)
				Console.log("Index is null");
			else if(indexes.get(in).c==null)
				Console.log("Color is null");
			s.append(in).append(" ").append(indexes.get(in).c.getRed()).append(" ").append(indexes.get(in).c.getGreen())
					.append(" ").append(indexes.get(in).c.getBlue()).append(" ").append(indexes.get(in).index)
					.append(System.lineSeparator());
		}
		Utils.fileWriter(s.toString(), Game.prefix + "/Player0.8/Indexes.txt");

		SmartCostume.body = new SerialImage(indexes.get("Body").getCo());
		SmartCostume.arm = new SerialImage(indexes.get("Arms").getCo());
		SmartCostume.hair = new SerialImage(indexes.get("Hair").getCo());
		SmartCostume.hand = new SerialImage(indexes.get("Hands").getCo());
		SmartCostume.leg = new SerialImage(indexes.get("Pants").getCo());
		SmartCostume.eye = new SerialImage(indexes.get("Pupils").getCo());
		SmartCostume.eye_whites = new SerialImage(indexes.get("Eyes").getCo());
		SmartCostume.face = new SerialImage(indexes.get("Face").getCo());
		SmartCostume.shoe = new SerialImage(indexes.get("Shoes").getCo().getSubimage(0, 0, 36, 36));
		SmartCostume.bare_arm = new SerialImage(
				Tran.effectColor(ImageLoader.loadImage("/textures/Player/Skin/Arm.png"), indexes.get("Face").c));
		SmartCostume.bare_leg = new SerialImage(
				Tran.effectColor(ImageLoader.loadImage("/textures/Player/Skin/Leg.png"), indexes.get("Face").c));
		SmartCostume.barefoot = new SerialImage(
				Tran.effectColor(ImageLoader.loadImage("/textures/Player/Skin/Feet.png").getSubimage(0, 0, 36, 36),
						indexes.get("Face").c));
		SmartCostume.bare_chest = new SerialImage(
				Tran.effectColor(ImageLoader.loadImage("/textures/Player/Skin/Body.png").getSubimage(0, 0, 36, 36),
						indexes.get("Face").c));
	}

	@Override
	public void tick() {
		back.tick();
		export.tick();
		user.tick();
		random.tick();

		if (back.on) {
			State.setState(handler.getGame().menuState);

			save();
		} else if (export.on)
			write();
		else if (random.on)
			randomize();

		hue.tick(handler.getMouse());

		tabs.tick();
		String val = tabs.getTab();

		valueUp.tick();
		valueUp.selected = valueUp.selected || (KeyManager.checkBind("Up"));
		valueDown.tick();
		valueDown.selected = valueDown.selected || (KeyManager.checkBind("Down"));

		if (valueUp.selected || valueDown.selected)
			useCostume = false;

		for (String s : indexes.keySet()) {
			if (val.equals(s)) {
				if (!previousTab.equals(val))
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

		switch (tabs.getTab()) {
			case "Hair" -> SmartCostume.hair = new SerialImage(indexes.get("Hair").getCo());
			case "Eyes" -> SmartCostume.eye_whites = new SerialImage(indexes.get("Eyes").getCo());
			case "Pupils" -> SmartCostume.eye = new SerialImage(indexes.get("Pupils").getCo());
			case "Body" -> SmartCostume.body = new SerialImage(indexes.get("Body").getCo());
			case "Arms" -> SmartCostume.arm = new SerialImage(indexes.get("Arms").getCo());
			case "Pants" -> SmartCostume.leg = new SerialImage(indexes.get("Pants").getCo());
			case "Shoes" -> SmartCostume.shoe = new SerialImage(indexes.get("Shoes").getCo().getSubimage(0, 0, 36, 36));
			case "Hands" -> SmartCostume.hand = new SerialImage(indexes.get("Hands").getCo());
			case "Face" -> {
				SmartCostume.face = new SerialImage(indexes.get("Face").getCo());
				SmartCostume.bare_arm = new SerialImage(
						Tran.effectColor(ImageLoader.loadImageEX("Player/Skin/Arm.png"), indexes.get("Face").c));
				SmartCostume.bare_leg = new SerialImage(
						Tran.effectColor(ImageLoader.loadImageEX("Player/Skin/Leg.png"), indexes.get("Face").c));
				SmartCostume.barefoot = new SerialImage(
						Tran.effectColor(ImageLoader.loadImageEX("Player/Skin/Feet.png").getSubimage(0, 0, 36, 36),
								indexes.get("Face").c));
				SmartCostume.bare_chest = new SerialImage(
						Tran.effectColor(ImageLoader.loadImageEX("Player/Skin/Body.png").getSubimage(0, 0, 36, 36),
								indexes.get("Face").c));
			}
		}

		if (val.equals("Costumes")) {
			for (String s : costumes.keySet()) {
				costumes.get(s).tick();
				if (costumes.get(s).on()) {
					useCostume = true;
					costumeIndex = s;
				}
			}
		}

		previousTab = tabs.getTab();

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

		if (!tabs.getTab().equals("Costumes"))
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
//		Tran.drawOutlinedText(g, 270d, handler.getHeight()-10, "Game points: " + (Utils.parseInt(FileLoader.readFile("C:/Arvopia/00.arv")) + Utils.parseInt(FileLoader.readFile("C:/Arvopia/02.arv"))), 1, Color.black, Color.white);

	}

	@Override
	public void init() {
		hue = new HueCube(handler.getWidth() - 375, 50, 300, 300);

		initIndex();

		if (new File(Game.prefix + "/Arvopia/Player0.8/Indexes.txt").exists()) {
			String orig = FileLoader.readFile(Game.prefix + "/Arvopia/Player0.8/Indexes.txt");
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

	public void initIndex() {

		Utils.createDirectory(Game.prefix + "/Arvopia/Player0.8/Face");
		Utils.createDirectory(Game.prefix + "/Arvopia/Player0.8/Arms");
		Utils.createDirectory(Game.prefix + "/Arvopia/Player0.8/Hair");
		Utils.createDirectory(Game.prefix + "/Arvopia/Player0.8/Eyes");
		Utils.createDirectory(Game.prefix + "/Arvopia/Player0.8/Pants");
		Utils.createDirectory(Game.prefix + "/Arvopia/Player0.8/Hands");
		Utils.createDirectory(Game.prefix + "/Arvopia/Player0.8/Body");
		Utils.createDirectory(Game.prefix + "/Arvopia/Player0.8/Shoes");
		Utils.createDirectory(Game.prefix + "/Arvopia/Player0.8/Pupils");
		Utils.createDirectory(Game.prefix + "/Arvopia/Player0.8/Costumes");
		Utils.createDirectory(Game.prefix + "/Arvopia/Player0.8/Skin");

		copyImages("Face");
		copyImages("Arms");
		copyImages("Hair");
		copyImages("Eyes");
		copyImages("Pants");
		copyImages("Hands");
		copyImages("Body");
		copyImages("Shoes");
		copyImages("Pupils");
		//copyImages("Costumes");
		copyImages("Skin");

		// Using TODO

		indexes = new HashMap<>();

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

		for (String s : Objects.requireNonNull(new File(Game.prefix + "/Arvopia/Player0.8/Costumes").list())) {
			costumes.put(s, new Costume(handler, ImageLoader.loadImageEX(Game.prefix + "/Arvopia/Player0.8/Costumes/" + s), s, s,
					handler.getWidth() - 375 + cx, 50 + cy));

			cx += 73;

			if (cx + 73 > 375) {
				cx = 0;
				cy += 109;
			}
		}

	}

	private void copyImages(String path) {
		String[] list = new File("Player/" + path).list();
		if(list != null)
			for (String s : list) {
				ImageLoader.saveImage(ImageLoader.loadImageEX("Player/" + path + "/" + s),
						Game.prefix + "/Arvopia/Player0.8/" + path + "/" + s);
			}
	}

	private void initiateIndex(String path) {
		ArrayList<BufferedImage> images = new ArrayList<>();
		String[] list = new File(Game.prefix + "/Arvopia/Player0.8/" + path).list();
		if(list!=null)
			for (String s : list) {
				images.add(ImageLoader.loadImageEX(Game.prefix + "/Arvopia/Player0.8/" + path + "/" + s));
			}
		indexes.put(path, new typeIndex(images));
	}

	public void write() {
		BufferedImage image = getFull();

		ImageLoader.saveImage(image, Game.prefix + "/Arvopia/Player0.8/out.png");

	}

	public static Color generateHairColor() {
		float h = (float) Public.rand(0.05, 0.15);
		float s = (float) Public.rand(0.35, 0.85);
		float v = (float) Public.rand(0.05, 0.85);
		return Color.getHSBColor(h, s, v);
	}

	public static Color generateSkinColor() {
		float h = (float) Public.rand(0.02, 0.15);
		float s = (float) Public.rand(0.25, 0.65);
		float v = (float) Public.rand(0.45, 0.98);
		return Color.getHSBColor(h, s, v);
	}

	public void randomize() {
		for (String s : indexes.keySet())
			indexes.get(s).random();

		Color c = generateSkinColor();

		indexes.get("Eyes").c = new Color((int) Public.expandedRand(245, 255), (int) Public.expandedRand(240, 255), 255);
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

		previousTab = "None";
	}

	public BufferedImage getRandom() {
		return new BufferedImage(1000, 1000, BufferedImage.TYPE_4BYTE_ABGR);
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
		index = (int) Public.expandedRand(0, types.size() - 1);
	}

	public BufferedImage getRandom() {
		return get((int) Public.expandedRand(0, types.size() - 1));
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

		if (new File(Game.prefix + "/Arvopia/05.arv").exists()) {
			if (!FileLoader.readFile(Game.prefix + "/Arvopia/05.arv").contains(name))
				button.locked = true;
		} else {
			button.locked = true;
			try {
				System.out.println("Created 05.arv " + new File(Game.prefix + "/Arvopia/05.arv").createNewFile());
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
				Utils.existWriter(FileLoader.readFile(Game.prefix + "/Arvopia/05.arv") + System.lineSeparator() + button.name,
						Game.prefix + "/Arvopia/05.arv");
				Utils.existWriter("" + (Utils.parseInt(FileLoader.readFile(Game.prefix + "/Arvopia/02.arv")) - 500),
						Game.prefix + "/Arvopia/02.arv");
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
