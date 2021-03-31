package com.zandgall.arvopia.utils;

import com.zandgall.arvopia.ArvopiaLauncher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

public class FileLoader {
	public FileLoader() {
	}

	public static URL resourceURL(String resourcePath)  {
		return FileLoader.class.getResource(resourcePath);
	}
	
	public static InputStream loadResource(String resourcePath) throws IOException {
		URL url = FileLoader.class.getResource(resourcePath);
		if (url == null) {
			throw new IOException(resourcePath + ": resource not found");
		} else {
			return url.openStream();
		}
	}

	public static String streamToString(InputStream is, int bufferSize) {
		char[] buffer = new char[bufferSize];
		StringBuilder out = new StringBuilder();

		try {
			InputStreamReader in = new InputStreamReader(is, "UTF-8");

			try {
				while (true) {
					int rsz = in.read(buffer, 0, buffer.length);
					if (rsz < 0) {
						break;
					}

					out.append(buffer, 0, rsz);
				}
			} finally {
				if (in != null) {
					in.close();
				}

			}
		} catch (Throwable e) {

		}

		return out.toString();
	}

//	public static File resource(String path) {
//		if (FileLoader.class.getResource(path) == null) {
//			System.err.println("Could not find file " + path);
//			return null;
//		}
//		String p = FileLoader.class.getResource(path).getFile().replaceAll("%20", " ");
//		System.out.println(p);
//		
//		if(p.contains(".jar")) {
//			
//		} else {
//			return new File(p);
//		}
//		
//	}

	public static String readFile(String path) {
		if(path.startsWith("C:") && !System.getProperty("os.name").startsWith("Windows"))
			path.replace("C:", "~/Applications");
		String output = null;
		try {
			File file = new File(path);
//			System.out.println("File chosen: " + file.getAbsolutePath() + " is " + file.exists());
			Scanner sc = new Scanner(file);

			sc.useDelimiter("//Z");

			if (sc.hasNext())
				output = sc.next();
			else {
				output = "couldn't read";
			}
			sc.close();
			if ((ArvopiaLauncher.game != null) && (ArvopiaLauncher.game.handler != null)
					&& (ArvopiaLauncher.game.handler.filelogger != null)) {
				ArvopiaLauncher.game.handler.logFiles("File read: " + output);
			}
		} catch (IOException e) {
			e.printStackTrace();
			
		}

		return output;
	}
	
	public static String readFile(File file) {
		if(file.getPath().startsWith("C:") && !System.getProperty("os.name").startsWith("Windows"))
			file = new File(file.getPath().replace("C:", "~/Applications"));
		String output = null;
		try {
//			System.out.println("File chosen: " + file.getAbsolutePath() + " is " + file.exists());
			Scanner sc = new Scanner(file);

			sc.useDelimiter("//Z");

			if (sc.hasNext())
				output = sc.next();
			else {
				output = "couldn't read";
			}
			if ((ArvopiaLauncher.game != null) && (ArvopiaLauncher.game.handler != null)
					&& (ArvopiaLauncher.game.handler.filelogger != null)) {
				ArvopiaLauncher.game.handler.logFiles("File read: " + output);
			}
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output;
	}

	public static String readFile(String path, boolean doesntmatter) {
		if(path.startsWith("C:") && !System.getProperty("os.name").startsWith("Windows"))
			path.replace("C:", "~/Applications");
		String output = null;
		try {
			File file = new File(path);
			Scanner sc = new Scanner(file);

			sc.useDelimiter("//Z");

			if (sc.hasNext())
				output = sc.next();
			else
				output = "couldn't read";
			
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output;
	}

	public static Object[] readObjects(String path, int length) throws IOException, ClassNotFoundException {
		if(path.startsWith("C:") && !System.getProperty("os.name").startsWith("Windows"))
			path.replace("C:", "~/Applications");
		FileInputStream inputStream = new FileInputStream(new File(path));

		ObjectInputStream reader = new ObjectInputStream(inputStream);

		ArrayList<Object> objects = new ArrayList<Object>();

		for (int i = 0; i < length; i++) {
			Object o = reader.readObject();
			if(o!=null)
				objects.add(o);
		}

		reader.close();
		inputStream.close();

		return objects.toArray();
	}

	public static void writeObjects(String path, Object[] objects) throws IOException {
		if(path.startsWith("C:") && !System.getProperty("os.name").startsWith("Windows"))
			path.replace("C:", "~/Applications");

		FileOutputStream outputStream = new FileOutputStream(new File(path));

		ObjectOutputStream writer = new ObjectOutputStream(outputStream);

		for (int i = 0; i < objects.length; i++) {
			writer.writeObject(objects[i]);
		}

		writer.close();
		outputStream.close();

	}
	
	public static void copyFolder(String a, String b) {
		File af = new File(a);
		
		Utils.createDirectory(b);
		
		for(String s: af.list()) {
			if(new File(a+"/"+s).isDirectory())
				copyFolder(a + "/" + s, b + "/" + s);
			else {
				try {
					Files.copy(new File(a+"/"+s).toPath(), new File(b + "/" + s).toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
