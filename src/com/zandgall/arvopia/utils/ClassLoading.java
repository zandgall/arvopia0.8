package com.zandgall.arvopia.utils;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.gfx.ImageLoader;

public class ClassLoading {
	
	public static ArrayList<Class<?>> getClasses(String pathToJar) throws Exception {
		@SuppressWarnings("resource")
		JarFile jarFile = new JarFile(pathToJar);
		Enumeration<JarEntry> e = jarFile.entries();

		URL[] urls = { new URL("jar:file:" + pathToJar + "!/") };
		URLClassLoader cl = URLClassLoader.newInstance(urls);

		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

		while (e.hasMoreElements()) {
			JarEntry je = e.nextElement();

			if (je.isDirectory() || !je.getName().endsWith(".class")) {
				continue;
			}
			// -6 because of .class
			String className = je.getName().substring(0, je.getName().length() - 6);
			className = className.replace('/', '.');
//			System.out.println(className);
			Class<?> c = cl.loadClass(className);
			
			classes.add(c);

		}

		return classes;
	}

	public static ArrayList<BufferedImage> getImages(String pathToJar) {
		try { 
		@SuppressWarnings("resource")
		JarFile jarFile = new JarFile(pathToJar);
		Enumeration<JarEntry> e = jarFile.entries();

		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();

		while (e.hasMoreElements()) {
			JarEntry je = e.nextElement();

			if (je.isDirectory() || !je.getName().endsWith(".png")) {
				continue;
			}
			// -4 because of .png
			String className = je.getName().substring(0, je.getName().length() - 4);
			//className = className.replace('/', '.');
			System.out.println("Image mod " + className);
			
			BufferedImage b = ImageLoader.loadImage(new URL("jar:file:" + pathToJar + "!/"+className+".png"));

			images.add(b);
			
		}

		return images;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<String> getFiles(String pathToJar) {
		try { 
		@SuppressWarnings("resource")
		JarFile jarFile = new JarFile(pathToJar);
		Enumeration<JarEntry> e = jarFile.entries();

		ArrayList<String> files = new ArrayList<String>();

		while (e.hasMoreElements()) {
			JarEntry je = e.nextElement();

			if (je.isDirectory() || je.getName().endsWith(".png")) {
				continue;
			}
			// -4 because of .png
			String className = je.getName();
			//className = className.replace('/', '.');
			System.out.println("File " + className);
			
			String b = FileLoader.streamToString(new URL("jar:file:" + pathToJar + "!/"+className).openStream(), ("jar:file:" + pathToJar + "!/"+className).length());
			System.out.println("Mod will have " + pathToJar+"/"+className+": "+b);
			files.add(b);
			
		}

		return files;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void moveTmp(String pathToJar) {
		try { 
			@SuppressWarnings("resource")
			JarFile jarFile = new JarFile(pathToJar);
			Enumeration<JarEntry> e = jarFile.entries();
	
			while (e.hasMoreElements()) {
				JarEntry je = e.nextElement();
				if (je.getName().startsWith("com/") || je.getName() == "com")
					continue;
				if (je.isDirectory()) {
					Utils.createDirectory(Game.prefix + "\\Arvopia\\tmp\\" + je.getName());
				} else if(je.getName().endsWith(".png")) {
					BufferedImage image = ImageLoader.loadImage(new URL("jar:file:" + pathToJar + "!/"+je.getName()));
					ImageLoader.saveImage(image, Game.prefix + "\\Arvopia\\tmp\\" + je.getName());
				} else {
					String b = FileLoader.streamToString(new URL("jar:file:" + pathToJar + "!/"+je.getName()).openStream(), ("jar:file:" + pathToJar + "!/"+je.getName()).length());
					Utils.fileWriter(b, Game.prefix + "\\Arvopia\\tmp\\" + je.getName());
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> getFiles(String pathToJar, String folder) {
		try { 
		@SuppressWarnings("resource")
		JarFile jarFile = new JarFile(pathToJar);
		Enumeration<JarEntry> e = jarFile.entries();

		ArrayList<String> files = new ArrayList<String>();

		while (e.hasMoreElements()) {
			JarEntry je = e.nextElement();

			if (je.isDirectory()||je.getName().endsWith(".png")) {
				continue;
			}
			// -4 because of .png
			String className = je.getName();
			//className = className.replace('/', '.');
			System.out.println("File " + className);
			
			String b = FileLoader.streamToString(new URL("jar:file:" + pathToJar + "!/"+className).openStream(), ("jar:file:" + pathToJar + "!/"+className).length());
			files.add(b);
			
		}

		return files;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<String> getImageNames(String pathToJar) {
		try { 
			@SuppressWarnings("resource")
			JarFile jarFile = new JarFile(pathToJar);
			Enumeration<JarEntry> e = jarFile.entries();
	
			ArrayList<String> images = new ArrayList<String>();
	
			while (e.hasMoreElements()) {
				JarEntry je = e.nextElement();
	
				if (je.isDirectory() || !je.getName().endsWith(".png")) {
					continue;
				}
				// -4 because of .png
				String className = je.getName();
	//			className = className.replace('/', '.');
				System.out.println("Image mod " + className);
	
				images.add(className);
				
			}
	
			return images;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<String> getFileNames(String pathToJar) {
		try { 
			@SuppressWarnings("resource")
			JarFile jarFile = new JarFile(pathToJar);
			Enumeration<JarEntry> e = jarFile.entries();
	
			ArrayList<String> files = new ArrayList<String>();
	
			while (e.hasMoreElements()) {
				JarEntry je = e.nextElement();
	
				if (je.isDirectory()) {
					continue;
				}
				String className = je.getName();
	//			className = className.replace('/', '.');
				System.out.println("Image " + className);
	
				files.add(className);
				
			}
	
			return files;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<String> getFileNames(String pathToJar, String folder) {
		try { 
			@SuppressWarnings("resource")
			JarFile jarFile = new JarFile(pathToJar);
			Enumeration<JarEntry> e = jarFile.entries();
	
			ArrayList<String> files = new ArrayList<String>();
	
			while (e.hasMoreElements()) {
				JarEntry je = e.nextElement();
	
				if (je.isDirectory()) {
					continue;
				}
				String className = je.getName();
	//			className = className.replace('/', '.');
				System.out.println("Image " + className);
	
				files.add(className);
				
			}
	
			return files;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<String> getDirectoryNames(String pathToJar) {
		try { 
			@SuppressWarnings("resource")
			JarFile jarFile = new JarFile(pathToJar);
			Enumeration<JarEntry> e = jarFile.entries();
	
			ArrayList<String> files = new ArrayList<String>();
	
			while (e.hasMoreElements()) {
				JarEntry je = e.nextElement();
	
				if (!je.isDirectory()) {
					continue;
				}
				String className = je.getName();
	//			className = className.replace('/', '.');
				System.out.println("Image " + className);
	
				files.add(className);
				
			}
	
			return files;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param pathToJar Path to used jar file
	 * @return List of objects
	 */

	public static ArrayList<Object> loadObjects(String pathToJar) {
		ArrayList<Object> objects = new ArrayList<Object>();
		try {
			ArrayList<Class<?>> classes = getClasses(pathToJar);

			for (Class<?> c : classes)
				objects.add(getC(c));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objects;
	}

	/**
	 * Uses the provided class to get you an object
	 * 
	 * @param c Class that you'd like to get an object from
	 * @return New Object from Class c
	 */

	@SuppressWarnings({ "rawtypes" })
	public static Object getC(Class c) {
		try {
			return c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
