
package com.zandgall.arvopia.display;

import java.awt.Canvas;
import java.awt.Dimension;
import javax.swing.JFrame;

public class Display {
	private JFrame frame;
	private Canvas canvas;
	private String title;
	private int width;
	private int height;

	public Display(String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;

		createDisplay();
	}

	private void createDisplay() {
		frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setMaximumSize(new Dimension(width, height));
		canvas.setMinimumSize(new Dimension(width, height));

		canvas.setFocusable(false);

		frame.add(canvas);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.pack();
		System.out.println("\t\t FRAME: " +frame.getWidth() + " " + frame.getHeight());
		System.out.println("\t\t CANVAS: " +canvas.getWidth() + " " + canvas.getHeight());
	}
	
	public void setSize(int width, int height) {
		frame.setSize(width, height);
//		frame.setDefaultCloseOperation(3);
//		frame.setLocationRelativeTo(null);
//		frame.setVisible(true);
		
		canvas.setPreferredSize(new Dimension(width, height));
//		canvas.setMaximumSize(new Dimension(width, height));
//		canvas.setMinimumSize(new Dimension(width, height));
		
		frame.pack();
		System.out.println("\t\t FRAME: " +frame.getWidth() + " " + frame.getHeight());
		System.out.println("\t\t CANVAS: " +canvas.getWidth() + " " + canvas.getHeight());
	}

	public void stop() {
		frame.dispose();
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public JFrame getFrame() {
		return frame;
	}
}
