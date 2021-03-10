package com.zandgall.arvopia.gfx.matriximg;

import java.awt.image.BufferedImage;

public class MatrixIMG {
	
	int[][] data;
	
	public MatrixIMG(BufferedImage base) {
		data = new int[base.getWidth()][base.getHeight()];
		for(int x = 0; x<base.getWidth(); x++)
			for(int y = 0; y<base.getHeight(); y++) {
				data[x][y] = base.getRGB(x, y);
			}
	}
	
	public MatrixIMG(int[][] data) {
		this.data=data;
	}
	
	public BufferedImage getImageEquivalent() {
		BufferedImage out = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_4BYTE_ABGR);
		for(int x = 0; x<out.getWidth(); x++)
			for(int y = 0; y<out.getHeight(); y++) {
				out.setRGB(x, y, data[x][y]);
			}
		
		return out;
	}
	
	public static MatrixIMG scale(MatrixIMG src, int width, int height) {
		int[][] out = new int[width][height];
		
		float wfactor = (src.data.length*1.0f)/(width*1.0f);
		float hfactor = (src.data[0].length*1.0f)/(height*1.0f);
		
		for(int x = 0; x<width; x++) {
			for(int y = 0; y<height; y++) {
				out[x][y] = src.data[(int) (x*wfactor)][(int)(y*hfactor)];
			}
		}
		
		return new MatrixIMG(out);
		
	}
	
	public static BufferedImage scale(BufferedImage src, int width, int height) {
		BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		
		float wfactor = (src.getWidth()*1.0f)/(width*1.0f);
		float hfactor = (src.getHeight()*1.0f)/(height*1.0f);
		
		for(int x = 0; x<width; x++) {
			for(int y = 0; y<height; y++) {
				out.setRGB(x, y, src.getRGB((int) (x*wfactor), (int) (y*hfactor)));
			}
		}
		
		return out;
	}
	
}
