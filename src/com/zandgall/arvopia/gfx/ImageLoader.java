package com.zandgall.arvopia.gfx;

import com.zandgall.arvopia.ArvopiaLauncher;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Reporter;
import com.zandgall.arvopia.state.OptionState;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class ImageLoader {
	public static Handler game = new Handler(ArvopiaLauncher.game);

	static Map<String, BufferedImage> mem = new HashMap<String, BufferedImage>();
	
	public static BufferedImage empty = new BufferedImage(200, 200, 1);
	
	public ImageLoader() {
	}

	public static BufferedImage loadImage(String path) {
		if (mem.containsKey(path)) {
			return (BufferedImage) mem.get(path);
		}
		try {
			if (game.getGame().optionState != null && ((OptionState) game.getGame().optionState).getToggle("Extra Debug"))
				System.out.println("\t\t" + path + " loaded");
			BufferedImage out = ImageIO.read(ImageLoader.class.getResource(path));
			mem.put(path, out);
			return out;
		} catch (Exception e) {
//			game.log(e.getMessage());
			e.printStackTrace();
//			Reporter.quick("Could not load " + path + ", this could be a big problem");
			System.err.println("Could not load image " + path);
		}
		return empty;
	}

	public static BufferedImage loadImageNS(String path) {
		if (mem.containsKey(path)) {
			return (BufferedImage) mem.get(path);
		}
		try {
			if (game.getGame().optionState != null && ((OptionState) game.getGame().optionState).getToggle("Extra Debug"))
				System.out.println("\t\t" + path + " loaded");
			BufferedImage out = ImageIO.read(ImageLoader.class.getResource(path));
			return out;
		} catch (Exception e) {
			game.log(e.getMessage());
			e.printStackTrace();
			Reporter.quick("Could not load " + path + ", this could be a big problem");
		}
		return empty;
	}

	public static void addRedirect(String path, BufferedImage pointer) {
		mem.put(path, pointer);
	}

	public static BufferedImage onlyLoadImage(String path) {
		try {
			if (game.getGame().optionState != null && ((OptionState) game.getGame().optionState).getToggle("Extra Debug"))
				System.out.println("\t\t" + path + " loaded");
			BufferedImage out = ImageIO.read(ImageLoader.class.getResource(path));
			mem.put(path, out);
			return out;
		} catch (Exception e) {
			game.log(e.getMessage());
			e.printStackTrace();
			Reporter.quick("Could not load " + path + ", this could be a big problem");
		}
		return empty;
	}

	public static BufferedImage loadImageEX(String path) {
		if (mem.containsKey(path)) {
			return (BufferedImage) mem.get(path);
		}
		try {
			if (game.getGame().optionState != null && ((OptionState) game.getGame().optionState).getToggle("Extra Debug"))
				System.out.println("\t\t" + path + " loaded");
			BufferedImage out = ImageIO.read(new File(path));
			mem.put(path, out);
			return out;
		} catch (Exception e) {
//			game.log(e.getMessage());
			e.printStackTrace();
//			Reporter.quick("Could not load " + path + ", this could be a big problem");
			System.err.println("Could not load image " + path);
		}
		return empty;
	}
	
	public static BufferedImage loadImage(URL url) {
		try {
			BufferedImage out = ImageIO.read(url);
//			mem.put(path, out);
			return out;
		} catch (Exception e) {
//			game.log(e.getMessage());
//			e.printStackTrace();
//			Reporter.quick("Could not load " + path + ", this could be a big problem");
			System.err.println("Could not load image " + url.getPath());
		}
		return empty;
	}
	
	public static void saveImage(BufferedImage image, String path) {
		try {
//			System.out.println("Saving image " + path);
			ImageIO.write(image, "png", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
