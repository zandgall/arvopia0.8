package com.zandgall.arvopia.state;

import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.input.KeyManager;
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
	public static boolean render_background = true, individual_tile_render = true, split_stream_render = true, split_stream_lighting = true;
	public static int lightType = 0, renType = 0;
	private final Button back, reset;

	double scroll = 0;

	ArrayList<Button> keybinds = new ArrayList<>();
	public static final String[] LIGHT_TYPES = new String[] { "Fast", "Smooth", "Quality", "Hyper" },
			RENDER_TYPES = new String[] { "Speed", "Normal", "Quality" };

	public final Map<String, ArrayList<Option>> options;

	final TabMenu tabs;

	public OptionState(Handler handler) {
		super(handler);

		Console.log("Starting options");

		tabs = new TabMenu(handler, new String[] { "General", "Controls/Keybinds" }, 0, 0, 720, 400, true);

		options = new HashMap<>();
		options.put("Toggleables", new ArrayList<>());

		back = new Button(handler, handler.getWidth() - 70, handler.getHeight() - 40,
				"Brings you back to the main menu", "Back");

		addSlider("Game control", "FPS", 1, 90, 60, "fps");
		addSlider("Game control", "Render Type", 0, RENDER_TYPES.length - 1, 0, "none");

		addSlider("Environment", "Time Speed", 0, 100, 2, "min/sec");
		addSlider("Environment", "Light Quality", 1, 36, 9, "px per cell");
		addSlider("Environment", "Light Smoothing", 0, LIGHT_TYPES.length - 1, 0, "none");

		addSlider("Volume", "Music Volume", 0, 100, 75, "%");
		addSlider("Volume", "Sound Volume", 0, 100, 75, "%");

		addToggle("Slider Debug", "Use if sliders don't work for you", true);
		addToggle("Pause on Mouse Exit", "Pauses the game when the cursor leaves the screen", false);
		addToggle("Extra Debug", "Prints every little detail, not recommended unless needed for debugging", false);
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

		init();
	}

	public void addSlider(String section, String name, double min, double max, double start, String ps) {
		if (!options.containsKey(section))
			options.put(section, new ArrayList<>());
		options.get(section).add(new Option(handler, name, min, max, start, ps));
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
		return "" + (options.get(sec).get(index).toggle == null ? options.get(sec).get(index).slider.getWholeValue()
				: options.get(sec).get(index).toggle.on);
	}

	public double getSlider(String sec, int index) {
		return options.get(sec).get(index).slider.getWholeValue();
	}

	public int getIntSlider(String sec, int index) {
		return options.get(sec).get(index).slider.getValue();
	}

	public boolean getToggle(int index) {
		return Utils.parseBoolean(getValue("Toggleables", index));
	}

	public double getSlider(String sec, String name) {
		return getSlider(sec, nameIndex(sec, name));
	}

	public int getIntSlider(String sec, String name) {
		return getIntSlider(sec, nameIndex(sec, name));
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

		if (!val.equals(""))
			if (options.get(sec).get(index).slider == null)
				options.get(sec).get(index).toggle.on = val.equalsIgnoreCase("true");
			else
				options.get(sec).get(index).slider.setValue(Utils.parseDouble(val));

	}

	public void tick() {

		if ((back.on) || (KeyManager.esc)) {
			State.setState(State.getPrev());

			StringBuilder out = new StringBuilder();
			for (String s : options.keySet())
				for (int i = 0; i < options.get(s).size(); i++) {
					if (options.get(s).get(i).slider != null)
						out.append(options.get(s).get(i).name.replaceAll(" ", "")).append(" ").append(options.get(s).get(i).slider.getWholeValue()).append(System.lineSeparator());
					else
						out.append(options.get(s).get(i).name.replaceAll(" ", "")).append(" ").append(options.get(s).get(i).toggle.on).append(System.lineSeparator());
				}
			Utils.fileWriter(out.toString(), Game.prefix + "/Arvopia/Options0.8.txt");

			out = new StringBuilder();
			for(int i = 0; i < KeyManager.keybinds.getKeys().size(); i++) {
				String key = KeyManager.keybinds.getKeys().get(i);
				out.append(key).append("~");
				out.append(KeyManager.keysections.keyFrom(key)).append("~");
				for(int j = 0; j < KeyManager.keybinds.getValues(key).size(); j++)
					out.append(KeyManager.keybinds.getValues(key).get(j)).append(" ");
				out.append(System.lineSeparator());
			}

			Utils.fileWriter(out.toString(), Game.prefix + "/Arvopia/Keybinds.txt");
		}

		scroll -= handler.getMouse().getMouseScroll() * 8;

		tabs.tick();

		back.tick();

		reset.tick();

		if (tabs.getTab().equals("General"))
			generalTick();
		else if (tabs.getTab().equals("Controls/Keybinds"))
			controlTick();
	}

	public void generalTick() {

		handler.getMouse().setSliderMalfunction(getToggle("Slider Debug"));

		lightType = getIntSlider("Environment", "Light Smoothing");
		renType = getIntSlider("Game control", "Render Type");

		render_background = getToggle("Render Background");

		fxVolume = (float) Math.pow(getSlider("Volume", "Sound Volume") / 100.0f, 3);
		msVolume = (float) Math.pow(getSlider("Volume", "Music Volume") / 100.0f, 3);

		double i = 0;
		double t = 0;
		double max = 0;
		for (String s : options.keySet()) {
			for (int j = 0; j < options.get(s).size(); j++) {

				if (!s.equals("Toggleables")) {
					options.get(s).get(j).tick(18, (int) (i + 40 + scroll));
					i += 54;
					max += 54;
				} else {
					options.get(s).get(j).tick(18, (int) (t * 40 + 40 + scroll));
					t++;
				}

			}
			if (!s.equals("Toggleables"))
				i += 18;
		}

		scroll = Public.range(-max + 320, 0, scroll);

		options.get("Game control").get(1).custom = RENDER_TYPES[options.get("Game control").get(1).slider.getValue()];
		options.get("Environment").get(2).custom = LIGHT_TYPES[options.get("Environment").get(2).slider.getValue()];

		handler.getWorld().getEnvironment().lightQuality = getIntSlider("Environment", "Light Quality");

		handler.getGame().setFps((int) getSlider("Game control", "FPS"));
		handler.getWorld().getEnvironment().setTimeSpeed((int) getSlider("Environment", "Time Speed"));
		split_stream_render = getToggle("Split-Stream Rendering");
		split_stream_lighting = getToggle("Split-Stream Lighting");

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
				if (!b.getName().equals("+")) {
					String key = b.getDescription().split("~`~")[1].replaceAll("\\n", "");
					int val = Utils.parseInt(b.getDescription().split("~`~")[0]);
					if (KeyManager.keybinds.getValues(key.intern()).size() > 1) {
						Console.log("Removed", KeyManager.keybinds.getValues().get(val), "(" + val + ")", "from", key);
						KeyManager.keybinds.remove(val);
						init();
					} else
						Console.log("Too small to remove from", key.intern(),
								KeyManager.keybinds.getValues(key.intern()));
				}
			}
			b.data = false;
			if (b.on) {
				while (!KeyManager.pressed) {
					System.out.print("");
				}
				if (b.getName().equals("+")) {
					KeyManager.keybinds.put(b.getDescription(), KeyManager.lastKey);
					init();
				} else {
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

		if (tabs.getTab().equals("General"))
			g.drawImage(renderGeneral(), 0, 37, null);
		else if (tabs.getTab().equals("Controls/Keybinds"))
			g.drawImage(renderControl(), 0, 37, null);

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
			if (!s.equals("Toggleables"))
				g.drawLine(10, i, 300, i);
		}

		return out;
	}

	private BufferedImage renderControl() {
		BufferedImage out = new BufferedImage(720, 358, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = out.createGraphics();

		g.translate(0, -30);

		g.setColor(Color.black);
		g.setFont(Public.defaultFont.deriveFont(20.0f));

		int off = 0;

		g.translate(0, scroll);
		for (Button b : keybinds)
			b.render(g);
		g.translate(0, -scroll);

		for (int s = 0; s < KeyManager.keysections.getKeys().size(); s++) {
			String section = KeyManager.keysections.getKeys().get(s);
			off += 62;
			g.setFont(Public.runescape.deriveFont(40.0f));
			Tran.drawOutlinedText(g, 10, off + scroll, section, 2, Color.black, Color.lightGray);
			for (int i = 0; i < KeyManager.keybinds.getKeys().size(); i++) {

				String option = KeyManager.keybinds.getKeys().get(i);

				if (option.contains("_SECTION") || !KeyManager.keysections.getValues(section).contains(option))
					continue;

				off += 46;

				g.setFont(Public.runescape.deriveFont(30.0f));

				Color c;
				c = Color.white;
				if (KeyManager.checkBind(KeyManager.keybinds.getKeys().get(i)))
					c = (Color.yellow);

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

		if (new File(Game.prefix + "/Keybinds.txt").exists()) {
			String[] opts = FileLoader.readFile(Game.prefix + "/Keybinds.txt").split(System.lineSeparator());
			KeyManager.keybinds.clear();
			KeyManager.keysections.clear();
			for(String line : opts) {
				String[] elements = line.split("~");
				String[] numbers = elements[2].split("\\s+");
				for (String number : numbers)
					KeyManager.addKeybind(elements[0], elements[1], Utils.parseInt(number));
			}
		}

		keybinds = new ArrayList<>();

		int off = 0;

		for (int s = 0; s < KeyManager.keysections.getKeys().size(); s++) {
			String section = KeyManager.keysections.getKeys().get(s);
			off += 62;
			for (int i = 0; i < KeyManager.keybinds.getKeys().size(); i++) {

				String option = KeyManager.keybinds.getKeys().get(i);

				if (option.contains("_SECTION") || !KeyManager.keysections.getValues(section).contains(option))
					continue;

				off += 46;

				int j;
				for (j = 0; j < KeyManager.keybinds.getValues(option).size(); j++) {
					int index = KeyManager.keybinds.indexOfVal(option, KeyManager.keybinds.getValues(option).get(j));

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

	public Button name_tag;

	int y, x;

	String ps = "";
	String custom = "";
	final String name;

	final Object start;

	public Option(Handler game, String name, double min, double max, double start, String ps) {
		this.name = name;
		slider = new Slider(game, min, max, start, false, name);
		name_tag = new Button(game, 18, 0, "", name);
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
			slider.tick(x + name_tag.getWidth() + 10, y + 8);
		if (toggle != null) {
			toggle.setY(y);
			toggle.tick();
		}
		if (name_tag != null) {
			name_tag.setY(y);
		}
	}

	public void render(Graphics2D g) {
		if (slider != null) {
			name_tag.render(g);
			slider.render(g);
			g.setFont(Public.defaultFont.deriveFont(20.0f));
			if (!custom.equals(""))
				g.drawString(custom, x + name_tag.getWidth() + 130, name_tag.getY() + 22);
			else
				g.drawString(slider.getValue() + " " + ps, x + name_tag.getWidth() + 130, name_tag.getY() + 22);
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
		else name_tag.setDescription(desc);
	}
	
}