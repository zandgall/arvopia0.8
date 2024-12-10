package com.zandgall.arvopia.tiles;

import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FenceTile extends Tile{

    public FenceTile(BufferedImage texture, String id) {
        super(texture, id);
    }

    public static void loadAllFenceTiles() {
        Assets sheet = new Assets(ImageLoader.loadImage("/textures/Tiles/fence.png"), 18, 18, "fence");
        Assets kernelSheet = new Assets(ImageLoader.loadImage("/textures/Tiles/fencereference.png"), 3, 3, "fencereference");
        for(int i = 0; i < 16; i++) {
            for(int j = 0; j < 16; j++) {
                FenceTile g = new FenceTile(sheet.get(i, j), "FENCE"+(j + i * 16));
                BufferedImage k = kernelSheet.get(i,j);
                g.setValidityKernel(
                        k.getRGB(0, 0), k.getRGB(1, 0), k.getRGB(2, 0),
                        k.getRGB(0, 1), Color.YELLOW.getRGB(), k.getRGB(2, 1),
                        k.getRGB(0, 2), k.getRGB(1, 2), k.getRGB(2, 2));
            }
        }
    }

    @Override
    public Color getColor() {
        return new Color(100, 200, 255);
    }
}
