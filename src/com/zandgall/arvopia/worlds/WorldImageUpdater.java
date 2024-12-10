package com.zandgall.arvopia.worlds;

import com.zandgall.arvopia.ArvopiaLauncher;
import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.tiles.backtile.Backtile;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.max;

class WorldImageUpdater implements Runnable {
    public static WorldImageUpdater instance;
    public static BufferedImage foreground, background;
    public static int xbOffset, ybOffset, xfOffset, yfOffset;

    public WorldImageUpdater() {
        Thread thread = new Thread(this, "World Image Updater");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }
    @Override
    public void run() {
        while(Game.running) {
            if (!OptionState.split_stream_lighting) {
                WorldImageUpdater.instance = null;
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
        if(World.getWidth() == 0 || World.getHeight() == 0 || World.tiles ==null || World.backtiles ==null)
            return;
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getDefaultConfiguration();
        int bx = (int)max(ArvopiaLauncher.game.handler.xOffset() / 18 - 2, 0);
        int by = (int)max(ArvopiaLauncher.game.handler.yOffset() / 18 - 2, 0);
        int ex = (int)((ArvopiaLauncher.game.handler.xOffset()+ArvopiaLauncher.game.width) / 18 + 2);
        int ey = (int)((ArvopiaLauncher.game.handler.yOffset()+ArvopiaLauncher.game.height) / 18 + 2);
        BufferedImage preForeground = config.createCompatibleImage((ex-bx)*18, (ey-by)*18, Transparency.TRANSLUCENT);
        Graphics2D g = preForeground.createGraphics();
        g.setRenderingHints(Tran.renderspeed);
        for (int y = by; y < ey && y < World.getHeight(); y++)
            for (int x = bx; x < ex && x < World.getWidth(); x++)
                World.getTile(x, y).render(ArvopiaLauncher.game.handler, g, x-bx, y-by);
        g.dispose();

        foreground = preForeground;
        xfOffset = bx;
        yfOffset = by;

        BufferedImage preBackground = config.createCompatibleImage((ex-bx)*18, (ey-by)*18, Transparency.TRANSLUCENT);
        g = preBackground.createGraphics();
        g.setRenderingHints(Tran.renderspeed);
        for (int y = by; y < ey && y < World.getHeight(); y++)
            for (int x = bx; x < ex && x < World.getWidth(); x++)
                Backtile.getTile(World.backtiles[x][y]).render(ArvopiaLauncher.game.handler, g, x-bx, y-by);
        g.dispose();
        background = preBackground;
        xbOffset = bx;
        ybOffset = by;
    }
}