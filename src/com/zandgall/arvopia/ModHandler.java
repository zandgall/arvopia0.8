package com.zandgall.arvopia;

import java.util.ArrayList;

import com.zandgall.arvopia.utils.ClassLoading;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ModHandler {
	
	public static ArrayList<Class<?>> mods = new ArrayList<>();
	
	public static void loadMods(String jarfile) {
		try {
			mods.addAll(ClassLoading.getClasses(jarfile));
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Failed to add mod " + jarfile);
		}
	}
	
	public static Class<?> getClass(String name) {
		for(Class<?> c: mods) {
			if(c.getName().equals(name)) {
				return c;
			}
		}
		throw new NullPointerException();
	}
	
	public static ArrayList<Class<?>> getPackage(String packageName) {
		ArrayList<Class<?>> out = new ArrayList<>();

		for(Class<?> c: mods) {
			if(c.getName().startsWith(packageName))
				out.add(c);
		}
		
		return out;
	}
	
	public static Class<?> getClass(String packageName, String name) {
		for(Class<?> c: getPackage(packageName)) {
			if(c.getName().replaceFirst(packageName, "").equals(name))
				return c;
		}
		throw new NullPointerException();
	}
	
	public static Object invokeMethod(Class<?> c, Object obj, String methodName, Class<?>[] type, Object... arg) {
		try {
			Method m = c.getMethod(methodName, type);
			return m.invoke(obj, arg);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object invokeMethod(Class<?> c, Object obj, String methodName, Object[] arg, Class<?>... type) {
		try {
			Method m = c.getMethod(methodName, type);
			return m.invoke(obj, arg);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object construct(Class<?> c, Object[] arg, Class<?>...type) throws Exception {
		return c.getConstructor(type).newInstance(arg);
	}
	
	public static Object construct(Class<?> c, Class<?>[] type, Object... arg) throws Exception {
		return c.getConstructor(type).newInstance(arg);
	}
		
}
