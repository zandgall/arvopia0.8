package com.zandgall.arvopia.environment;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.environment.weather.Rain;
import com.zandgall.arvopia.environment.weather.Snow;
import com.zandgall.arvopia.environment.weather.Storm;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.input.KeyManager;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.*;
import com.zandgall.arvopia.worlds.World;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Environment implements Serializable {
	private static final long serialVersionUID = -8114611669397868240L;
	private Noise wind;
	private double windChange, windSwing, windTimer, maxWind, minWind;
	private double temp = 33.0D;
	public double tempOffset = 0;
	private double humidity = 0.4D;
	private double stormChance = 0.0D;
	private long humiditySwing = 2400L;
	private long humidityTimer = 0L;
	private long humidIndex = 0;

	public Snow snow;

	public double fadingRain = 0;

	public Rain rain;
	public Storm storm;
	public boolean precipitation = false;
	public boolean stormy = false;
	private long Time;
	private long totalTime;
	public int TimeChange;
	public int rohundo = 1;
	public int lapse = 1;
	public int collevti = 1;
	public int state = 0;
	public int preState = 0;

	public double daylightOff = 0;

	private Light moon;
	private Light sun;
	private BufferedImage moonIm;
	private BufferedImage sunIm;
	private LightManager lightManager;

	public LightGrabber lG;

	public static long weather = 1, lighting = 1, lightcoloring = 1, guitime = 1, sky = 1;

	public static Chart lightingBreakdown = new Chart(
			new String[] { "Alpha detect", "Color detect", "Drawing", "Stretch" },
			new Color[] { Color.green, Color.blue, Color.red, Color.orange }, "Lighting Breakdown", 340, 200, 100, 100);

	public LongTimeline skies;

	public LightManager getLightManager() {
		return lightManager;
	}

	public double getWind(double x, double y) {
		return Public.Map(SimplexNoise.noise(x*0.01, y*0.01, System.nanoTime() * 1.0e-10)+wind.get1(0*System.nanoTime()*1.0-9), 1, -1, maxWind, minWind);
	}

	public double getMaxWind() {
		return maxWind;
	}

	public double getMinWind() {
		return maxWind;
	}

	public double getWindChange() {
		return windChange;
	}

	public double getWindSwing() {
		return windSwing;
	}

	public void setWeather(int type) {

		boolean prep = precipitation;

		if (type == 0) {
			precipitation = false;
			humidity = 0;
			stormy = false;
		} else if (type == 1) {
			precipitation = true;
			humidity = 1;
			stormy = false;
		} else if (type == 2) {
			precipitation = true;
			humidity = 1;
			stormy = true;
		}

		if (precipitation && !prep) {
			System.err.println("Started Rain");
//			game.fadeOutIn("Rain", "Rain", 100);
			game.play("Rain");
		} else if (!precipitation && prep) {
			System.err.println("Ended Rain");
//			game.fadeOut("Rain", "Rain", 100);
			//game.soundSystem.stop("Rain");
		}

	}

	public Environment(Handler handler, double wind, double windChange, double windSwing) {
		this.wind = new Noise(System.nanoTime());
		maxWind = (wind * 0.5D);
		minWind = (wind * -0.5D);
		this.windChange = (windChange / 10.0D);
		this.windSwing = windSwing;
		windTimer = 0.0D;

		lightQuality = (int) ((OptionState) handler.getGame().optionState).getSlider("Environment", "Light Quality");
		game = handler;

		snow = new Snow(game, 100);
		rain = new Rain(game, 100);
		storm = new Storm(game, 200);

		font = Public.digital;

		game.log("Environment " + this + " initialized with wind: " + this.wind + " windChange: " + this.windChange
				+ " windSwing: " + this.windSwing);

		rohundo = 60;
		lapse = 1;

		sun = new Light(handler, 0, 0, 40, 100, Color.red);
		moon = new Light(handler, 0, 0, 10, 50, Color.white);
		lightManager = new LightManager(handler);
		lightManager.addLight(moon);
		lightManager.addLight(sun);
		sun.turnOn();
		moon.turnOn();

		moonIm = ImageLoader.loadImage("/textures/Environment/Moon.png");
		sunIm = ImageLoader.loadImage("/textures/Environment/Sun.png");

		OptionState o = (OptionState) game.getGame().optionState;
		TimeChange = (int) o.getSlider("Environment", "Time Speed");
		Time = 43199L;

		skies = new LongTimeline(new Color[] { Color.blue, Color.green, Color.red },
				new String[] { "Blue", "Green", "Red" }, "Weather", 10, 10, 20);

		game.addSound("Sounds/Rain.ogg", "Rain", true, 0, 0, 0);

		game.addSound("Sounds/Wind.ogg", "Wind", true, 0, 0, 0);
		game.play("Wind");

		if(LightGrabber.instance==null)
			LightGrabber.instance = new LightGrabber(this);
		lG = LightGrabber.instance;
	}

	public Environment(Handler handler) {
		game=handler;
	}
	
	public double getHumidity() {
		return humidity;
	}

	public void setTimeSpeed(int timeSpeed) {
		TimeChange = timeSpeed;
	}

	public long getTime() {
		return Time;
	}

	public void setTime(long Time) {
		this.Time = Time;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public int getHours() {
		return (int) (Time / 60L / 60L) + 1;
	}

	public double getTemp() {
		return temp;
	}

	public int getMinutes() {
		return (int) (Time / 60L) % 60;
	}

	public int getTotalMinutes() {
		return getMinutes() + getHours() * 60;
	}

	public void tick() {
		if (precipitation) {
			if (temp < 32.0D) {
				snow.tick(6);
				if (snow.amount > snow.snowFlakes.size()) {
					snow.add(game);
				}
			} else if (stormy)
				storm.tick(12);
			else
				rain.tick(12);

		} else if (snow.snowFlakes.size() > 0)
			snow.melt();

		boolean prep = precipitation;

		if (humidityTimer >= humiditySwing) {
			humidityTimer = 0L;

			precipitation = (Math.random() < humidity);

			fadingRain = 0;

			stormy = (Math.random() < stormChance);
			stormy = false;

			if (rohundo == 78) {
				precipitation = true;
			}
//			if (precipitation) {
//				rain.start();
//				storm.start();
//			} else {
//				rain.stop();
//				storm.stop();
//			}

			game.logEnvironment("It " + (precipitation ? "is " : "is not ")
					+ (temp < 32.0D ? "snowing." : stormy ? "storming." : temp < 32.0D ? "blizzarding." : "raining."));
		} else {
			humidityTimer += 1L;
		}

		if (precipitation) {
			fadingRain++;
		} else {
			fadingRain--;
		}

		fadingRain = Public.range(0, 100, fadingRain);

		if (precipitation && !prep) {
			System.err.println("Started Rain");
			game.play("Rain");
		} else if (!precipitation && prep) {
			System.err.println("Ended Rain");
			//game.soundSystem.stop("Rain");
		}

		game.putAtListener("Rain");
		game.putAtListener("Wind");

		Time += TimeChange;
		totalTime += TimeChange;

		temp = (60.0D * Math.sin(Math.PI * (rohundo - 21 + (getTotalMinutes() / 1500.0)) / 40) + 40.0D) + tempOffset;

		humidity = Noise.noise1(humidIndex / 10000.0) * 0.6 + 0.4;
		humidIndex++;

		if (getHours() == 25) {
			Time = 0L;
			rohundo += 1;

			humidityTimer = 0L;
			humidIndex += 100;
			stormChance = Public.range(0.0D, 1.0D, Public.rand(-1.0D, humidity));

			game.logEnvironmentSilent("Humidity: " + humidity);
			game.logEnvironmentSilent("Storm chance: " + stormChance);

			game.logEnvironmentSilent("Temp: " + temp);

			game.logEnvironment("~~~~~~~~~~~~~~~DAY " + rohundo + "~~~~~~~~~~~~~~~" + System.lineSeparator()
					+ "Average temp: " + Math.round(temp) + System.lineSeparator() + "Chance of rain/snow: "
					+ Public.range(0.0D, 100.0D, Math.round(humidity * 100.0D)) + "%" + System.lineSeparator()
					+ "Chance of storm: " + Public.range(0.0D, 100.0D, Math.round(stormChance * 100.0D)) + "%"
					+ System.lineSeparator() + "~~~~~~~~~~~~~~~DAY " + rohundo + "~~~~~~~~~~~~~~~");
		}

		collevti = rohundo / 10;

		if (rohundo == 81) {
			collevti = 1;
			rohundo = 1;
			lapse += 1;
			if (lapse == 1) {
				Time = 0;
				humidity = 0.8D;
			}

			game.logEnvironment(
					System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator());
			game.logEnvironment("Happy birthday!!! You hit a Lapse of " + lapse + System.lineSeparator()
					+ ((lapse < 10) && (lapse > 8) ? "Two digits is just around the corner!" : "")
					+ System.lineSeparator() + "~~~~~~~~~~~~~~LAPSE " + lapse + "~~~~~~~~~~~~~~~~~");
		}
	}

	public int lightQuality;
	public boolean interpolateLight = false;
	java.awt.Font font;
	Handler game;
	private int[] stars;
	private int[] starsAlpha;

	public void renderSunMoon(Graphics2D g) {
		long pre = System.nanoTime();
		int green = (int) Public.range(10.0D, 200.0D,
				255.0 * Math.sin(Math.PI * (getTotalMinutes() + daylightOff - 480) / 719.5) + 255);
		int red = (int) Public.range(25.0D, 255.0D, Math.max(
				Math.min(200.0 * Math.sin(Math.PI * (getTotalMinutes() + daylightOff - 480) / 719.5) + 255, 100),
				-0.000002 * Math.pow((getTotalMinutes() + daylightOff - 1250), 4) + 255));
		int blue = (int) Public.range(120.0D, 255.0D,
				Math.min(200.0 * Math.sin(Math.PI * (getTotalMinutes() + daylightOff - 480) / 719.5) + 255,
						0.00000004 * Math.pow((getTotalMinutes() + daylightOff - 1408), 4) + 120));

		g.setColor(new Color(red, green, blue));

		g.fillRect(0, 0, game.getWidth(), game.getHeight());

		int y;
		double x;
		int totmin;
		if (getHours() < 12) {
			y = (int) (Math.pow(getTotalMinutes() - 120, 2) * 0.001);
			totmin = getTotalMinutes();
		} else {
			y = (int) (Math.pow(getTotalMinutes() - 1200 - game.getWidth() / 2, 2) * 0.001);
			totmin = getTotalMinutes() - (24 * 60);
		}

		x = (((totmin / 14.99) / 100) * (World.getWidth() * Tile.WIDTH) * 2 + game.getWidth() / 2) * 0.6;

		y = (int) (y - game.getGameCamera().getyOffset() / 10);
		x -= game.getGameCamera().getxOffset() / 10;

		g.drawImage(moonIm, (int) x, y, null);

		moon.setY(y + 10+(int) game.yOffset());
		moon.setX((int) x + 10 + (int) game.xOffset());
		moon.colorDistribution = 0.8;
		moon.color = new Color(255, 210, 200);

		if (getHours() >= 0) {
			x = (int) (((getTotalMinutes() * 1.6D) - 1050) * 1.2 - game.xOffset() * 0.2);
			y = (int) (0.001D * Math.pow(getTotalMinutes() * 1.3D - game.getWidth() / 2 - 750.0D, 2.0D) * 1.2
					- game.yOffset() * 0.2);
			g.drawImage(sunIm, (int) (x), (int) (y), 30, 30, null);

			sun.setX((int) (game.xOffset() + x));
			sun.setY(y + 15 + (int) game.yOffset());
			sun.color = new Color((int) Math.min(red*1.8, 255), (int) Math.min(green*1.2, 255), (int) Math.min(blue*0.99, 255));
		}

		sky = System.nanoTime() - pre;
	}

	public int sunX() {
		return sun.getX();
	}

	public int sunY() {
		return sun.getY();
	}

	public void setupStars() {
		stars = new int[(int) Public.expandedRand(10.0D, 30.0D) * 2];
		starsAlpha = new int[stars.length];
		for (int i = 0; i < stars.length - 1; i += 2) {
			stars[i] = ((int) Public.expandedRand(0.0D, game.getWidth()));
			stars[(i + 1)] = ((int) Public.expandedRand(0.0D, game.getHeight()));
			starsAlpha[(i / 2)] = ((int) Public.expandedRand(170.0D, 300.0D));
		}
	}

	public void renderStars(Graphics g) {
		for (int i = 0; i < stars.length - 1; i += 2) {
			int x = stars[i];
			int y = stars[(i + 1)];
			int alpha1, alpha2;
			Light l = lightManager.getClosest(x, y);
			if(l==null) {
				alpha1 = (int) Public.range(0.0D, Public.range(0.0D, 255.0D, starsAlpha[(i / 2)]),
						255.0D - getTotalMinutes() * 0.75D + 250.0D);
				alpha2 = (int) Public.range(0.0D, Public.range(0.0D, 255.0D, starsAlpha[(i / 2)]), getTotalMinutes() - 1100);
			} else {
				double num = Public.dist(x, y, l.getX(), l.getY())
						/ (l.getStrength() / 10);
				
				alpha1 = (int) Public.range(l.getMax(),
						Public.range(0.0D, Public.range(0.0D, 255.0D, starsAlpha[(i / 2)]),
								255.0D - getTotalMinutes() * 0.75D + 250.0D),
						num);
				alpha2 = (int) Public.range(l.getMax(),
						Public.range(0.0D, Public.range(0.0D, 255.0D, starsAlpha[(i / 2)]), getTotalMinutes() - 1100),
						num);
			}
			
			x = (int) (x - game.getGameCamera().getxOffset() / 10.0F);
			y = (int) (y - game.getGameCamera().getyOffset() / 10.0F);

			if (getHours() < 12) {
				g.setColor(new Color(255, 255, 255, alpha1));
			} else {
				g.setColor(new Color(255, 255, 255, alpha2));
			}
			g.drawRect(x, y, 1, 1);
		}
	}

	boolean useGrabber = true;

	public void renderWeather(Graphics2D g) {
		long pre = System.nanoTime();
		if (precipitation) {
			if (temp < 32.0D) {
				snow.render(g);
			} else if (stormy) {
				storm.render(g);
			} else {
				rain.render(g);
			}
		}

		if (lightQuality == 0)
			lightQuality = 1;

		weather = System.nanoTime() - pre;
	}
	public void render(Graphics2D g) {

		long pre = System.nanoTime();

		long pre1 = System.nanoTime();

		long alphadet = 0, colordet = 0, drawtim = 0, stretch = 0;

		double minimum = Public.range(0.0D, 170.0D, 0.0015 * Math.pow(getTotalMinutes() - 850, 2) - 120);
		if(minimum!=0.0) {
			if (!OptionState.split_stream_lighting)
				LightGrabber.update();
			else {
				if (LightGrabber.instance == null)
					LightGrabber.instance = new LightGrabber(this);
				LightGrabber.e = this;
			}

			g.drawImage(LightGrabber.out, 0, 0, null);
		}
		stretch = System.nanoTime() - pre1;

		lighting = System.nanoTime() - pre;
		pre = System.nanoTime();

		lightcoloring = System.nanoTime() - pre;

		lightingBreakdown.update(new double[] { alphadet, colordet, drawtim, stretch });

		skies.add(colordet, 0);
		skies.add(drawtim, 1);
		skies.add(stretch, 2);

		if (KeyManager.function[2])
			skies.render(g, skies.getLargest(), 680, 200);
	}

	public void renderGui(Graphics2D g) {
		long pre = System.nanoTime();

		int hours = getHours();

		if (!KeyManager.checkBind("Interact"))
			return;

		String ampm;
		if ((hours >= 12) && (hours < 24)) {
			ampm = " PM";
		} else {
			ampm = " AM";
		}

		if (hours > 12) {
			hours -= 12;
		}

		g.setFont(Public.runescape.deriveFont(20.0f));
		g.setColor(Color.black);

		String h = hours < 10 ? " " + hours : "" + hours;
		String m = getMinutes() < 10 ? ":0" + getMinutes() : ":" + getMinutes();

		Tran.drawOutlinedText(g, 100, game.getHeight() - 5, h + m + ampm, 1.0, Color.black, Color.white);

		Tran.drawOutlinedText(g, 200, game.getHeight() - 5, (rohundo % 10 + 1) + "-" + collevti + "-" + lapse, 1.0,
				Color.black, Color.white);

		Tran.drawOutlinedText(g, 270, game.getHeight() - 5, Public.grid(temp, 0.1D, 0.0D) / 10.0D + "", 1.0,
				Color.black, Color.white);
		Tran.drawOutlinedText(g, temp <= -10 || temp >= 100 ? 332 : 320, game.getHeight() - 5, "F", 1.0, Color.black,
				Color.white);

		Tran.drawOutlinedText(g, 360, game.getHeight() - 5, "~" + (int) (humidity * 100) + "% Humidity", 1.0,
				Color.black, Color.white);

		g.setFont(Public.runescape.deriveFont(10.0f));
		Tran.drawOutlinedText(g, temp <= -10 || temp >= 100 ? 325 : 313, game.getHeight() - 15, "o", 1.0, Color.black,
				Color.white);

		Tran.drawOutlinedText(g, 200, game.getHeight() - 25, "d - m - y", 1.0, Color.black, Color.white);

		guitime = System.nanoTime() - pre;
		pre = System.nanoTime();
	}

	public boolean collisionWithTile(double x, double y) {
		return World.getTile((int) (x) / 18, (int) (y) / 18).isSolid();
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}
}

