package com.zandgall.arvopia.state;

import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.input.KeyManager;
import com.zandgall.arvopia.utils.ArraySlider;
import com.zandgall.arvopia.utils.BevelPlatform;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Slider;
import com.zandgall.arvopia.utils.TabMenu;
import com.zandgall.arvopia.utils.ToggleButton;
import com.zandgall.arvopia.utils.Utils;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OptionState extends State {
	public static float fxVolume = 1, msVolume = 1;
	public static boolean clipRender = false, renderbackground = true, individualtilerender = true, splitstreamrender = true, splitstreamlighting = true;
	public static int lightType = 0, renType = 0;
	private Button back, reset;

	BevelPlatform body;

	double scroll = 0;

	ArrayList<Button> keybinds = new ArrayList<Button>();
	public static final String[] LIGHT_TYPES = new String[] { "Fast", "Smooth", "Quality", "Hyper" },
			RENDER_TYPES = new String[] { "Speed", "Normal", "Quality" };

	public Map<String, ArrayList<Option>> options;

	TabMenu tabs;

	public OptionState(Handler handler) {
		super(handler);

		Console.log("Starting options");

		tabs = new TabMenu(handler, new String[] { "General", "Controls/Keybinds" }, 0, 0, 720, 400, true);

		options = new HashMap<String, ArrayList<Option>>();
		options.put("Toggleables", new ArrayList<Option>());

		body = new BevelPlatform(2, 2, 720, 400);

		back = new Button(handler, handler.getWidth() - 70, handler.getHeight() - 40,
				"Brings you back to the main menu", "Back");

		addSlider("Game control", "FPS", 1, 90, 60, "fps");
		addSlider("Game control", "Render Type", 0, RENDER_TYPES.length - 1, 0, "none");

		addSlider("Enviornment", "Time Speed", 0, 100, 2, "min/sec");
		addSlider("Enviornment", "Light Quality", 1, 36, 9, "px per cell");
		addSlider("Enviornment", "Light Smoothing", 0, LIGHT_TYPES.length - 1, 0, "none");

		addSlider("Volume", "Music Volume", 0, 100, 75, "%");
		addSlider("Volume", "Sound Volume", 0, 100, 75, "%");

		addToggle("Slider Debug", "Use if sliders don't work for you", true);
		addToggle("Pause on Mouse Exit", "Pauses the game when the cursor leaves the screen", false);
		addToggle("Extra Debug", "Prints every little detail, not reccomended unless needed for debugging", false);
		addToggle("Render Background", "Draws hills in the background in each world, Lower framerate", true);
		addToggle("Glow near sun", "On: Tiles and entities will show an outline when nearby sun. (Possible loss in fps)", true);
		addToggle("Sound per layer", "On: Plays a new audio clip for everything, (RAM heavy?); \nOff: Every bird will use the one same singing sound, every fox with barking, etc", true);
		addToggle("Split-Stream Rendering", "On: CPU Heavy, but much faster and more powerful (Use for fullscreen); \nOff: Light on CPU but slower", true);
		addToggle("Split-Stream Lighting", "On: CPU Heavy, Faster and capable at better lighting; \nOff: Light on CPU but much slower", true);

		Console.log("Loading saved options");

		if (new File(Game.prefix + "/Arvopia/Options0.8.txt").exists()) {

			String s = FileLoader.readFile(Game.prefix + "/Arvopia/Options0.8.txt");

			for (String n : options.keySet())
				for (int i = 0; i < options.get(n).size(); i++) {
					get(n, i, s);
				}
		}

		Console.log("Setting volume");

		fxVolume = (float) Math.pow(getSlider("Volume", 1) / 100.0f, 3);
		handler.getGame().setFps((int) getSlider("Game control", 0));
		msVolume = (float) Math.pow(getSlider("Volume", 0) / 100.0f, 3);
		//handler.soundSystem.setVolume("music", msVolume);

		Console.log("Finished");
		reset = new Button(handler, back.getX() - 85, handler.getHeight() - 40, "", "Reset");
	}

	public void addSlider(String section, String name, double min, double max, double start, String ps) {
		if (!options.containsKey(section))
			options.put(section, new ArrayList<Option>());
		options.get(section).add(new Option(handler, name, min, max, start, ps));
	}

	public void addToggle(String name, boolean start) {
		options.get("Toggleables").add(new Option(handler, name, start));
	}
	
	public void addToggle(String name, String desc, boolean start) {
		Option o = new Option(handler, name, start);
		o.setDescription(desc);
		options.get("Toggleables").add(o);
	}

	private String get(String name, String file) {
		String[] lines = file.split(System.lineSeparator());
		for (String s : lines)
			if (s.startsWith(name + " "))
				return s.split("\\s+")[1];
		return "";
	}

	public String getValue(String sec, int index) {

		String output = ""
				+ (options.get(sec).get(index).toggle == null ? options.get(sec).get(index).slider.getWholeValue()
						: options.get(sec).get(index).toggle.on);

		return output;
	}

	public double getSlider(String sec, int index) {
		return options.get(sec).get(index).slider.getWholeValue();
	}

	public int getSlideri(String sec, int index) {
		return options.get(sec).get(index).slider.getValue();
	}

	public boolean getToggle(int index) {
		return Utils.parseBoolean(getValue("Toggleables", index));
	}

	public double getSlider(String sec, String name) {
		return getSlider(sec, nameIndex(sec, name));
	}

	public int getSlideri(String sec, String name) {
		return getSlideri(sec, nameIndex(sec, name));
	}

	public boolean getToggle(String name) {
		return getToggle(nameIndex("Toggleables", name));
	}

	public int nameIndex(String sec, String name) {
		for (Option o : options.get(sec))
			if (o.name.equals(name))
				return options.get(sec).indexOf(o);
		return 0;
	}

	public void get(String sec, int index, String file) {
		String val = get(options.get(sec).get(index).name.replaceAll(" ", ""), file);

		if (val != "")
			if (options.get(sec).get(index).slider == null)
				options.get(sec).get(index).toggle.on = val.toLowerCase().equals("true");
			else
				options.get(sec).get(index).slider.setValue(Utils.parseDouble(val));

	}

	public void tick() {

		if ((back.on) || (KeyManager.esc)) {
			State.setState(State.getPrev());

			String out = "";
			for (String s : options.keySet())
				for (int i = 0; i < options.get(s).size(); i++) {
					if (options.get(s).get(i).slider != null)
						out += options.get(s).get(i).name.replaceAll(" ", "") + " "
								+ options.get(s).get(i).slider.getWholeValue() + System.lineSeparator();
					else
						out += options.get(s).get(i).name.replaceAll(" ", "") + " " + options.get(s).get(i).toggle.on
								+ System.lineSeparator();
				}
			Utils.fileWriter(out, Game.prefix + "/Arvopia/Options0.8.txt");
		}

		scroll -= handler.getMouse().getMouseScroll() * 8;

		tabs.tick();

		back.tick();

		reset.tick();

		if (tabs.getTab() == "General")
			generalTick();
		else if (tabs.getTab() == "Controls/Keybinds")
			controlTick();

//		scroll = Public.range(-90, 0, scroll);
	}

	public void generalTick() {

		handler.getMouse().setSliderMalfunction(getToggle("Slider Debug"));

		lightType = getSlideri("Enviornment", "Light Smoothing");
		renType = getSlideri("Game control", "Render Type");

		renderbackground = getToggle("Render Background");

		fxVolume = (float) Math.pow(getSlider("Volume", "Sound Volume") / 100.0f, 3);
		msVolume = (float) Math.pow(getSlider("Volume", "Music Volume") / 100.0f, 3);

		double i = 0;
		double t = 0;
		double max = 0;
		for (String s : options.keySet()) {
			for (int j = 0; j < options.get(s).size(); j++) {

				if (s != "Toggleables") {
					options.get(s).get(j).tick(18, (int) (i + 40 + scroll));
					i += 54;
					max += 54;
				} else {
					options.get(s).get(j).tick(18, (int) (t * 40 + 40 + scroll));
					t++;
				}

			}
			if (s != "Toggleables")
				i += 18;
		}

		scroll = Public.range(-max + 320, 0, scroll);

		options.get("Game control").get(1).custom = RENDER_TYPES[options.get("Game control").get(1).slider.getValue()];
		options.get("Enviornment").get(2).custom = LIGHT_TYPES[options.get("Enviornment").get(2).slider.getValue()];

		handler.getWorld().getEnviornment().lightQuality = getSlideri("Enviornment", "Light Quality");

		handler.getGame().setFps((int) getSlider("Game control", "FPS"));
		handler.getWorld().getEnviornment().setTimeSpeed((int) getSlider("Enviornment", "Time Speed"));
		splitstreamrender = getToggle("Split-Stream Rendering");
		splitstreamlighting = getToggle("Split-Stream Lighting");

		//handler.soundSystem.setVolume("music", msVolume);

		if (reset.on)
			for (ArrayList<Option> ol : options.values())
				for (Option o : ol)
					o.reset();
	}

	public void controlTick() {
		handler.getMouse().STILLTIMER = 0;
		for (Button b : keybinds) {
			b.tick(handler.getMouse().rMouseX(), handler.getMouse().rMouseY() - (int) scroll);
			if (b.data) {
				if (b.getName() != "+") {
					String key = b.getDescription().split("~`~")[1].replaceAll("\\n", "");
					int val = Utils.parseInt(b.getDescription().split("~`~")[0]);
					if (KeyManager.keybinds.getValues(key.intern()).size() > 1) {
						Console.log("Removed", KeyManager.keybinds.getValues().get(val), "(" + val + ")", "from", key);
						KeyManager.keybinds.remove(val);
//						Thread.sleep(100);
						init();
					} else
						Console.log("Too small to remove from", key.intern(),
								KeyManager.keybinds.getValues(key.intern()));
				}
			}
			b.data = false;
			if (b.on) {
				if (b.getName() == "+") {
					while (!KeyManager.pressed) {
						Console.log(KeyManager.pressed);
						if (KeyManager.pressed)
							break;
					}
					KeyManager.keybinds.put(b.getDescription(), KeyManager.lastKey);
					init();
				} else {
					while (!KeyManager.pressed) {
						Console.log(KeyManager.pressed);
						if (KeyManager.pressed)
							break;
					}
					Console.log("Setting", b.getDescription().split("~`~")[0], KeyManager.lastKey);
					KeyManager.keybinds.getValues().set(Utils.parseInt(b.getDescription().split("~`~")[0]),
							KeyManager.lastKey);
				}
			}
		}

		if (reset.on) {
			KeyManager.keybinds = KeyManager.resetbind.clone();
			init();
		}

	}

	public void render(Graphics2D g) {
		g.setColor(new Color(134, 200, 255));
		g.fillRect(0, 0, handler.getWidth(), handler.getHeight());
		tabs.render(g);
//		body.render(g, 0, 0);

		if (tabs.getTab() == "General")
			g.drawImage(renderGeneral(), 0, 30, null);
		else if (tabs.getTab() == "Controls/Keybinds")
			g.drawImage(renderControl(), 0, 30, null);

		back.render(g);

		reset.render(g);
	}

	private BufferedImage renderGeneral() {
		BufferedImage out = new BufferedImage(720, 365, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = out.createGraphics();

		g.translate(0, -30);

		int i = 0;

		for (String s : options.keySet()) {
			for (int j = 0; j < options.get(s).size(); j++) {
				options.get(s).get(j).render(g);
				i = options.get(s).get(j).y + 54;
			}
			if (s != "Toggleables")
				g.drawLine(10, i, 300, i);
		}

		return out;
	}

	private BufferedImage renderControl() {
		BufferedImage out = new BufferedImage(720, 365, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = out.createGraphics();

		g.translate(0, -30);

//		int i = 0;

		g.setColor(Color.black);
		g.setFont(Public.defaultFont.deriveFont(20.0f));

		int off = 0;

		g.translate(0, scroll);
		for (Button b : keybinds)
			b.render(g);
		g.translate(0, -scroll);

		for (int s = 0; s < KeyManager.keysections.getKeys().size(); s++) {
			String section = (String) KeyManager.keysections.getKeys().get(s);
			off += 62;
			g.setFont(Public.runescape.deriveFont(40.0f));
//			g.drawString(section, 10, off + (int) scroll);
			Tran.drawOutlinedText(g, 10, off + scroll, section, 2, Color.black, Color.lightGray);
			for (int i = 0; i < KeyManager.keybinds.getKeys().size(); i++) {

				String option = KeyManager.keybinds.getKeys().get(i);

//				Console.log(option, KeyManager.keysections.getValues(section));

				if (option.contains("_SECTION") || !KeyManager.keysections.getValues(section).contains(option))
					continue;

				off += 46;

				g.setFont(Public.runescape.deriveFont(30.0f));

				Color c = new Color(153, 153, 175);
				c = Color.white;
				if (KeyManager.checkBind(KeyManager.keybinds.getKeys().get(i)))
					c = (Color.yellow);
//				g.drawString(KeyManager.keybinds.getKeys().get(i).toUpperCase(), 10, off + (int) scroll);

				Tran.drawOutlinedText(g, 10, off + scroll+5, KeyManager.keybinds.getKeys().get(i).toUpperCase(), 2,
						Color.black, c);

				g.setFont(Public.runescape.deriveFont(20.0f));

				for (int j = 0; j < KeyManager.keybinds.getValues(KeyManager.keybinds.getKeys().get(i)).size(); j++) {
					Tran.drawOutlinedText(g, j*100+300, off+scroll, KeyEvent.getKeyText(KeyManager.keybinds.getValues(option).get(j)).toUpperCase(), 1, Color.black, c);
				}
			}
		}

		scroll = Public.range(-off + 370, 0, scroll);

		return out;
	}

	public void init() {

		keybinds = new ArrayList<Button>();

		int off = 0;

		for (int s = 0; s < KeyManager.keysections.getKeys().size(); s++) {
			String section = (String) KeyManager.keysections.getKeys().get(s);
			off += 62;
			for (int i = 0; i < KeyManager.keybinds.getKeys().size(); i++) {

				String option = KeyManager.keybinds.getKeys().get(i);

				if (option.contains("_SECTION") || !KeyManager.keysections.getValues(section).contains(option))
					continue;

				off += 46;

				int j = 0;
				for (j = 0; j < KeyManager.keybinds.getValues(option).size(); j++) {
					int index = j;
					index = KeyManager.keybinds.indexOfVal(option, KeyManager.keybinds.getValues(option).get(j));

					keybinds.add(new Button(handler, j * 100 + 290, off - 26, 80, 24, index + "~`~" + option, ""));
				}

				keybinds.add(new Button(handler, j * 100 + 290, off - 26, option, "+"));

			}
		}
	}

	@Override
	public void renderGUI(Graphics2D g2d) {

	}

}

class Option {

	public Slider slider;
	public ToggleButton toggle;

	public Button nametag;

	int y, x;

	String ps = "", custom = "", name = "";

	Object start;

	public Option(Handler game, String name, double min, double max, double start, String ps) {
		this.name = name;
		slider = new Slider(game, min, max, start, false, name);
		nametag = new Button(game, 18, 0, "", name);
		this.ps = ps;
		this.start = start;
	}

	public Option(Handler game, String name, boolean start) {
		this.name = name;
		toggle = new ToggleButton(game, 468, 10, 216, 18, "", name);
		toggle.on = start;
		this.start = start;
	}

	public void tick(int x, int y) {
		this.x = x;
		this.y = y;

		if (slider != null)
			slider.tick(x + nametag.getWidth() + 10, y + 8);
		if (toggle != null) {
			toggle.setY(y);
			toggle.tick();
		}
		if (nametag != null) {
			nametag.setY(y);
		}
	}

	public void render(Graphics2D g) {
		if (slider != null) {
			nametag.render(g);
			slider.render(g);
			g.setFont(Public.defaultFont.deriveFont(20.0f));
			if (custom != "")
				g.drawString(custom, x + nametag.getWidth() + 130, nametag.getY() + 22);
			else
				g.drawString(slider.getValue() + " " + ps, x + nametag.getWidth() + 130, nametag.getY() + 22);
		} else {
			toggle.render(g);
		}
	}

	public void reset() {
		if (slider != null)
			slider.setValue((double) start);
		if (toggle != null) {
			toggle.on = (boolean) start;
		}
	}
	
	public void setDescription(String desc) {
		if(toggle!=null)
			toggle.setDescription(desc);
		else nametag.setDescription(desc);
	}
	
}