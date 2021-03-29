package com.zandgall.arvopia;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class Recorder {
	Handler game;
	Robot rt;
	ArrayList<BufferedImage> images;
	public boolean recording;

	public Recorder(Handler game) {
		images = new ArrayList<BufferedImage>();

		this.game = game;
		try {
			rt = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			rt = null;
		}
	}

	public static int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();

	public static int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

	public void record() {
		recording = true;
		images.add(rt.createScreenCapture(new Rectangle(screenWidth, screenHeight)));
	}

	public void stop() {
		recording = false;

		System.out.println("Done");

		try {
			ImageOutputStream output = new FileImageOutputStream(
					new File(Game.prefix + "\\Arvopia\\Recordings\\Recording" + System.currentTimeMillis() + ".gif"));
			GifSequenceWriter writer = new GifSequenceWriter(output, ((BufferedImage) images.get(0)).getType(), 1,
					false);

			for (BufferedImage b : images) {
				writer.writeToSequence(b);
			}

			writer.close();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
