package com.zandgall.arvopia.utils;

import java.io.File;

public class Utils {
	public Utils() {
	}

	public static int parseInt(String number) {
		try {
			return Integer.parseInt(number);
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	public static boolean parseBoolean(String bool) {
		if ((bool.contains("true")) || (bool.contains("True")) || (bool.contains("TRUE"))) {
//			System.out.println(bool);
			return true;
		}

//		System.out.println("False: " + bool);

		return false;
	}

	public static double parseDouble(String number) {
		try {
			return Double.parseDouble(number);
		} catch (NumberFormatException e) {
		}
		return 0.0D;
	}

	public static long parseLong(String number) {
		try {
			return Long.parseLong(number);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	public static void fileWriter(String string, String fileName) {
		if(fileName.startsWith("C:") && !System.getProperty("os.name").startsWith("Windows"))
			fileName.replace("C:", "~/Applications");
		File file = new File(fileName);

		try {
			if (file.exists()) {
				file.delete();
			}
//			System.out.println("Trying to create " + fileName);
			
			if (file.createNewFile()) {
//				System.out.println("File: " + file + " created!");
			}
			java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(fileName));

			writer.write(string);

			writer.close();
		} catch (java.io.IOException e) {
//			e.printStackTrace();
			System.err.println((e.getMessage()));
		}
	}

	public static void existWriter(String string, String fileName) {
		if(fileName.startsWith("C:") && !System.getProperty("os.name").startsWith("Windows"))
			fileName.replace("C:", "~/Applications");
		File file = new File(fileName);

		try {
			if ((!file.exists()) && (file.createNewFile())) {
				System.out.println("File exception");
			}
			java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(fileName));

			writer.write(string);

			writer.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	public static void createDirectory(String fileName) {

		if(fileName.startsWith("C:") && !System.getProperty("os.name").startsWith("Windows"))
			fileName.replace("C:", "~/Applications");

		File file = new File(fileName);

		if (file.exists()) {
			return;
		}
		System.out.println("Directory : " + file.mkdirs());
	}
}
