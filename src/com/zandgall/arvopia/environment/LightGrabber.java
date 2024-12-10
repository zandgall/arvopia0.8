package com.zandgall.arvopia.environment;

import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.utils.Public;
import org.imgscalr.Scalr;

import java.awt.*;
import java.awt.image.BufferedImage;

class LightGrabber implements Runnable {

    public static LightGrabber instance;

    public static BufferedImage out = new BufferedImage(720, 405, BufferedImage.TYPE_4BYTE_ABGR);
    private static BufferedImage lights = new BufferedImage(720, 405, BufferedImage.TYPE_4BYTE_ABGR);

    public static Environment e;

    static int minutes = 779;

    public LightGrabber(Environment e) {
        LightGrabber.e = e;

        Thread t = new Thread(this, "Light grabber");
        t.start();
    }

    @Override
    public void run() {
        while (true) {

            if (!OptionState.split_stream_lighting) {
                LightGrabber.instance = null;
                return;
            }

            try {
                update();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void update() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getDefaultConfiguration();
        BufferedImage pre = config.createCompatibleImage(720, 405, Transparency.TRANSLUCENT);
        Graphics2D g = pre.createGraphics();
//		g.clearRect(0, 0, 720, 405);
        g.setRenderingHints(Tran.renderspeed);

        int lightQuality = e.lightQuality;
        LightManager lightManager = e.getLightManager();
        lightManager.game = e.game;
        minutes = e.getTotalMinutes();
        int mode = OptionState.lightType;

        if (lightQuality == 0) {
            lightQuality = 1;
        }

        if (lights.getWidth() != 720 / lightQuality + 1) {
            lights = new BufferedImage(720 / lightQuality + 1, 405 / lightQuality + 1,
                    BufferedImage.TYPE_4BYTE_ABGR);
        }

        long pre1;

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
            Scalr.Method[] methods = new Scalr.Method[]{Scalr.Method.SPEED, Scalr.Method.BALANCED, Scalr.Method.QUALITY,
                    Scalr.Method.ULTRA_QUALITY};

            if (mode >= 1) {
                g.drawImage(Scalr.resize(lights, methods[mode], Scalr.Mode.FIT_EXACT,
                                Math.max(720 / (4 - mode), lights.getWidth()), Math.max(405 / (4 - mode), lights.getHeight())), 0,
                        0, 720, 405, null);
            }

            g.dispose();
        }
//		r.dispose();
        LightGrabber.out = pre;
    }

}
