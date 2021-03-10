package com.zandgall.arvopia;

public class Console {
	
	public static void log(Object...objects) {
		String s = "";
		for(Object o: objects)
			s+=o+", ";
		
		s = s.substring(0, s.length()-2);
		System.out.println(s);
	}
	
}
