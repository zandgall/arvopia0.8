package com.zandgall.arvopia.utils;

import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class FileChooser extends JFrame {
	private JTextField filename = new JTextField();
	private JTextField dir = new JTextField();

	public String[] extensions = new String[0];

	public FileChooser() {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }
	}

	public String getFile(String directory) {
		JFileChooser c = new JFileChooser(directory);

		int rVal = c.showOpenDialog(this);
		if (rVal == 0) {
			filename.setText(c.getSelectedFile().getName());
			dir.setText(c.getSelectedFile().getPath());
		}
		if (rVal == 1) {
			filename.setText("You pressed cancel");
			dir.setText("");
		}

		ArrayList<String> path = new ArrayList<String>();
		for (int i = 0; i < dir.getText().length(); i++) {
			path.add(String.valueOf(dir.getText().toCharArray()[i]));
		}

		String fin = "";

		for (int i = 0; i < path.size(); i++) {
			fin = fin + (String) path.get(i);
		}

		System.out.println("World: " + fin);

		return dir.getText();
	}
	
	public String getFolder(String directory) {
		JFileChooser c = new JFileChooser(directory);
		c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int rVal = c.showOpenDialog(this);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			filename.setText(c.getSelectedFile().getName());
			dir.setText(c.getSelectedFile().getPath());
		}
		if (rVal == 1) {
			filename.setText("You pressed cancel");
			dir.setText("");
		}

		ArrayList<String> path = new ArrayList<String>();
		for (int i = 0; i < dir.getText().length(); i++) {
			path.add(String.valueOf(dir.getText().toCharArray()[i]));
		}

		String fin = "";

		for (int i = 0; i < path.size(); i++) {
			fin = fin + (String) path.get(i);
		}

		System.out.println("World: " + fin);

		return dir.getText();
	}

	private static final long serialVersionUID = 1L;

	public String saveFile(String directory) {
		JFileChooser c = new JFileChooser(directory);

		int rVal = c.showSaveDialog(this);
		if (rVal == 0) {
			filename.setText(c.getSelectedFile().getName());
			dir.setText(c.getSelectedFile().getPath());
		}
		if (rVal == 1) {
			filename.setText("You pressed cancel");
			dir.setText("");
		}

		ArrayList<String> path = new ArrayList<String>();
		for (int i = 0; i < dir.getText().length(); i++) {
			path.add(String.valueOf(dir.getText().toCharArray()[i]));
		}

		String fin = "";

		for (int i = 0; i < path.size(); i++) {
			fin = fin + (String) path.get(i);
		}

		System.out.println("World: " + fin);

		return dir.getText();
	}

	public boolean acceptJar(File file) {

		return !(file.getName().endsWith(".jar"));
//		if (file.isDirectory()) {
//			return true;
//		}
//		String path = file.getAbsolutePath().toLowerCase();
//		int i = 0;
//		for (int n = extensions.length; i < n; i++) {
//			String extension = extensions[i];
//			if (path.endsWith("." + extension)) {
//				return true;
//			}
//		}
//
//		return false;
	}
	
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
		String path = file.getAbsolutePath().toLowerCase();
		int i = 0;
		for (int n = extensions.length; i < n; i++) {
			String extension = extensions[i];
			if ((path.endsWith(extension)) && (path.charAt(path.length() - extension.length() - 1) == '.')) {
				return true;
			}
		}

		return false;
	}
}
