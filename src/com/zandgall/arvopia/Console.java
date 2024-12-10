package com.zandgall.arvopia;

public class Console {
	
	public static void log(Object...objects) {
		StringBuilder s = new StringBuilder();
		for(Object o: objects)
			s.append(o).append(", ");
		System.out.println(s.substring(0, s.length()-2));
	}
	
}
