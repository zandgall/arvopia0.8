package com.zandgall.arvopia.enviornment;

import com.zandgall.arvopia.ArvopiaLauncher;
import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.enviornment.weather.Rain;
import com.zandgall.arvopia.enviornment.weather.Snow;
import com.zandgall.arvopia.enviornment.weather.Storm;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.SerialImage;
import com.zandgall.arvopia.gfx.matriximg.MatrixIMG;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.input.KeyManager;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Chart;
import com.zandgall.arvopia.utils.LongTimeline;
import com.zandgall.arvopia.utils.Noise;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RGBImageFilter;
import java.io.IOException;
import java.io.Serializable;

import org.imgscalr.Scalr;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Enviornment implements Serializable {
	private static final long serialVersionUID = -8114611669397868240L;
	private double wind;
	private double windChange;
	private double windSwing;
	private double windTimer;
	private double maxWind;
	private double minWind;
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

	public static Chart lightingBreakdown = new Chart(new double[] { 1, 1, 1, 1 },
			new String[] { "Alpha detect", "Color detect", "Drawing", "Stretch" },
			new Color[] { Color.green, Color.blue, Color.red, Color.orange }, "Lighting Breakdown", 340, 200, 100, 100);

	public LongTimeline skies;

	public LightManager getLightManager() {
		return lightManager;
	}

	public double getWind() {
		return wind;
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
			game.soundSystem.stop("Rain");
		}

	}

	public Enviornment(Handler handler, double wind, double windChange, double windSwing) {
		this.wind = (wind / 10.0D);
		maxWind = (wind * 0.5D);
		minWind = (wind * -0.2D);
		this.windChange = (windChange / 10.0D);
		this.windSwing = windSwing;
		windTimer = 0.0D;

		lightQuality = (int) ((OptionState) handler.getGame().optionState).getSlider("Enviornment", "Light Quality");
		game = handler;

		snow = new Snow(game, 100);
		rain = new Rain(game, 100);
		storm = new Storm(game, 200);

		font = Public.digital;

		game.log("Enviornment " + this + " initialized with wind: " + this.wind + " windChange: " + this.windChange
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

		moonIm = ImageLoader.loadImage("/textures/Enviornment/Moon.png");
		sunIm = ImageLoader.loadImage("/textures/Enviornment/Sun.png");

		OptionState o = (OptionState) game.getGame().optionState;
		TimeChange = (int) o.getSlider("Enviornment", "Time Speed");
		Time = 43199L;

		skies = new LongTimeline(new Color[] { Color.blue, Color.green, Color.red },
				new String[] { "Blue", "Green", "Red" }, "Weather", 10, 10, 20, 50);

		game.addSound("Sounds/Rain.ogg", "Rain", true, 0, 0, 0);

		game.addSound("Sounds/Wind.ogg", "Wind", true, 0, 0, 0);
		game.play("Wind");

		if(LightGrabber.instance==null)
			LightGrabber.instance = new LightGrabber(this);
		lG = LightGrabber.instance;
	}

	public Enviornment(Handler handler) {
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

		if (windTimer >= windSwing) {
			wind += Public.debugRandom(-windChange, windChange);
//			wind += windChange;
			windTimer = 0.0D;
			if (wind < minWind) {
				wind += windChange;
			} else if (wind > maxWind) {
				wind -= windChange;
			}
			game.logEnviornmentSilent(Time + System.lineSeparator() + "Wind: " + Math.round(wind * 10.0D));
		} else {
			windTimer += 1.0D;
		}

		if (precipitation) {
			if (temp < 32.0D) {
				Snow.wind = wind;
				snow.tick(6);
				if (snow.ammount > snow.snowFlakes.size()) {
					snow.add(game);
				}
			} else if (stormy) {
				storm.tick(12);
			} else {
				Rain.wind = wind;
				rain.tick(12);
			}

		} else if (snow.snowFlakes.size() > 0) {
			snow.melt();
		}

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

			game.logEnviornment("It " + (precipitation ? "is " : "is not ")
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
			game.soundSystem.stop("Rain");
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
			stormChance = Public.range(0.0D, 1.0D, Public.debugRandom(-1.0D, humidity));

			game.logEnviornmentSilent("Humidity: " + humidity);
			game.logEnviornmentSilent("Storm chance: " + stormChance);

			game.logEnviornmentSilent("Temp: " + temp);

			game.logEnviornment("~~~~~~~~~~~~~~~DAY " + rohundo + "~~~~~~~~~~~~~~~" + System.lineSeparator()
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

			game.logEnviornment(
					System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator());
			game.logEnviornment("Happy birthday!!! You hit a Lapse of " + lapse + System.lineSeparator()
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

		x = (((totmin / 14.99) / 100) * (World.getWidth() * Tile.TILEWIDTH) * 2 + game.getWidth() / 2) * 0.6;

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
		stars = new int[(int) Public.random(10.0D, 30.0D) * 2];
		starsAlpha = new int[stars.length];
		for (int i = 0; i < stars.length - 1; i += 2) {
			stars[i] = ((int) Public.random(0.0D, game.getWidth()));
			stars[(i + 1)] = ((int) Public.random(0.0D, game.getHeight()));
			starsAlpha[(i / 2)] = ((int) Public.random(170.0D, 300.0D));
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

	public void render(Graphics g, java.awt.Graphics2D g2d) {
		long pre = System.nanoTime();
		if (precipitation) {
			if (temp < 32.0D) {
				snow.render(g);
			} else if (stormy) {
				storm.render(g, g2d);
			} else {
				rain.render(g);
			}
		}

		if (lightQuality == 0)
			lightQuality = 1;

		weather = System.nanoTime() - pre;
		pre = System.nanoTime();

		long pre1 = System.nanoTime();

		long alphadet = 0, colordet = 0, drawtim = 0, stretch = 0;

		double minimum = Public.range(0.0D, 170.0D, 0.0015 * Math.pow(getTotalMinutes() - 850, 2) - 120);
		if(minimum!=0.0) {
			if (!OptionState.splitstreamlighting)
				LightGrabber.update();
			else {
				if (LightGrabber.instance == null)
					LightGrabber.instance = new LightGrabber(this);
				LightGrabber.e = this;
			}

			g2d.drawImage(LightGrabber.out, 0, 0, null);
		}
		stretch = System.nanoTime() - pre1;

		lighting = System.nanoTime() - pre;
		pre = System.nanoTime();

		lightcoloring = System.nanoTime() - pre;
		pre = System.nanoTime();

		lightingBreakdown.update(new double[] { alphadet, colordet, drawtim, stretch });

		skies.add((long) (colordet), 0);
		skies.add((long) (drawtim), 1);
		skies.add((long) (stretch), 2);

		if (KeyManager.function[2])
			skies.render(g2d, skies.getLargest(), 680, 200);
	}

	public void renderGui(Graphics g, Graphics2D g2d) {
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

		Tran.drawOutlinedText(g2d, 100, game.getHeight() - 5, h + m + ampm, 1.0, Color.black, Color.white);

		Tran.drawOutlinedText(g2d, 200, game.getHeight() - 5, (rohundo % 10 + 1) + "-" + collevti + "-" + lapse, 1.0,
				Color.black, Color.white);

		Tran.drawOutlinedText(g2d, 270, game.getHeight() - 5, Public.grid(temp, 0.1D, 0.0D) / 10.0D + "", 1.0,
				Color.black, Color.white);
		Tran.drawOutlinedText(g2d, temp <= -10 || temp >= 100 ? 332 : 320, game.getHeight() - 5, "F", 1.0, Color.black,
				Color.white);

		Tran.drawOutlinedText(g2d, 360, game.getHeight() - 5, "~" + (int) (humidity * 100) + "% Humidity", 1.0,
				Color.black, Color.white);

		g.setFont(Public.runescape.deriveFont(10.0f));
		Tran.drawOutlinedText(g2d, temp <= -10 || temp >= 100 ? 325 : 313, game.getHeight() - 15, "o", 1.0, Color.black,
				Color.white);

		Tran.drawOutlinedText(g2d, 200, game.getHeight() - 25, "d - m - y", 1.0, Color.black, Color.white);

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

class AlphaFilter extends RGBImageFilter {

	public AlphaFilter() {
		canFilterIndexColorModel = true;
	}

	public int filterRGB(int x, int y, int rgb) {
		// Adjust the alpha value
		int alpha = (rgb >> 24) & 0xff;
		alpha = 255 - alpha;

		// Return the result
		return ((rgb & 0x00ffffff) | (alpha << 24));
	}
}

class LightGrabber implements Runnable {

	public static LightGrabber instance;
	
	public static BufferedImage out = new BufferedImage(720, 405, BufferedImage.TYPE_4BYTE_ABGR);
	static BufferedImage pre = new BufferedImage(720, 405, BufferedImage.TYPE_4BYTE_ABGR);
	static BufferedImage lights = new BufferedImage(720, 405, BufferedImage.TYPE_4BYTE_ABGR);

	public static Enviornment e;

	static int minutes = 779;
	
	public LightGrabber(Enviornment e) {
		LightGrabber.e = e;

		Thread t = new Thread(this, "Light grabber");
		t.start();
	}

	@Override
	public void run() {
		while (true) {
			
			if(!OptionState.splitstreamlighting) {
				LightGrabber.instance=null;
				return;
			}
			
			try {
				update();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void update() {
		pre = new BufferedImage(720, 405, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = pre.createGraphics();
//		g.clearRect(0, 0, 720, 405);
		g.setRenderingHints(Tran.renderspeed);

		int lightQuality = e.lightQuality;
		LightManager lightManager = e.getLightManager();
		lightManager.game=e.game;
		minutes = e.getTotalMinutes();
		int mode = OptionState.lightType;
		
		if(lightQuality == 0) {
			lightQuality = 1;
		}

//		BufferedImage lights = new BufferedImage(720 / lightQuality + 1, 405 / lightQuality + 1,
//				BufferedImage.TYPE_4BYTE_ABGR);
		if(lights.getWidth()!=720/lightQuality+1) {
			lights = new BufferedImage(720 / lightQuality + 1, 405 / lightQuality + 1,
				BufferedImage.TYPE_4BYTE_ABGR);
		}

//		Graphics2D r = lights.createGraphics();
//		r.setRenderingHints(Tran.renderspeed);

		long pre1 = System.nanoTime();

		long alphadet = 0, colordet = 0, drawtim = 0, stretch = 0;

		double minimum = Public.range(0.0D, 170.0D, 0.0015 * Math.pow(minutes - 850, 2) - 120);

		lightManager.updateLights();
		
		if (minimum != 0) {

			for (int x = 0; x < (720 / lightQuality) + 2; x += 2) {
				for (int y = 0; y < (405 / lightQuality) + 2; y += 2) {

					pre1 = System.nanoTime();

					int alpha00 = (int) Public.range(lightManager.getMax(x, y), minimum,
							255 - lightManager.getLight(x, y) * 255);
					int alpha10 = (int) Public.range(lightManager.getMax((x + 1), y), minimum,
							255 - lightManager.getLight((x + 1), y) * 255);
					int alpha01 = (int) Public.range(lightManager.getMax(x, (y + 1)), minimum,
							255 - lightManager.getLight(x, (y + 1)) * 255);
					int alpha11 = (int) Public.range(lightManager.getMax((x + 1), (y + 1)), minimum,
							255 - lightManager.getLight((x + 1), (y + 1)) * 255);
					alphadet += System.nanoTime() - pre1;
					pre1 = System.nanoTime();

					int rx = x;
					int ry = y;
					int h = 1;

					Color c00 = lightManager.getColor(x, y);
					Color c10 = lightManager.getColor(x + 1, y);
					Color c01 = lightManager.getColor(x, y + 1);
					Color c11 = lightManager.getColor(x + 1, y + 1);
					colordet += System.nanoTime() - pre1;
					pre1 = System.nanoTime();

					alpha00 = (int) Public.range(0, 255, alpha00);
					alpha10 = (int) Public.range(0, 255, alpha10);
					alpha01 = (int) Public.range(0, 255, alpha01);
					alpha11 = (int) Public.range(0, 255, alpha11);

					c00 = new Color(c00.getRed(), c00.getGreen(), c00.getBlue(), alpha00);
					c10 = new Color(c10.getRed(), c10.getGreen(), c10.getBlue(), alpha10);
					c01 = new Color(c01.getRed(), c01.getGreen(), c01.getBlue(), alpha01);
					c11 = new Color(c11.getRed(), c11.getGreen(), c11.getBlue(), alpha11);

						if (rx < lights.getWidth() && ry < lights.getHeight())
							lights.setRGB(rx, ry, Tran.accurateColor(c00));

						if (rx + 1 < lights.getWidth() && ry < lights.getHeight())
							lights.setRGB(rx + 1, ry, Tran.accurateColor(c10));

						if (rx < lights.getWidth() && ry + 1 < lights.getHeight())
							lights.setRGB(rx, ry + 1, Tran.accurateColor(c01));

						if (rx + 1 < lights.getWidth() && ry + 1 < lights.getHeight())
							lights.setRGB(rx + 1, ry + 1, Tran.accurateColor(c11));
					drawtim += System.nanoTime() - pre1;
				}
			}
			if (mode == 0)
				g.drawImage(lights, 0, 0, 720, 405, null);
			Scalr.Method[] methods = new Scalr.Method[] { Scalr.Method.SPEED, Scalr.Method.BALANCED, Scalr.Method.QUALITY,
					Scalr.Method.ULTRA_QUALITY };

			if (mode >= 1) {
				g.drawImage(Scalr.resize(lights, methods[mode], Scalr.Mode.FIT_EXACT,
						Math.max(720 / (4 - mode), lights.getWidth()), Math.max(405 / (4 - mode), lights.getHeight())), 0,
						0, 720, 405, null);
			}

			g.dispose();
		}
//		r.dispose();
		LightGrabber.out=pre;
	}

}
