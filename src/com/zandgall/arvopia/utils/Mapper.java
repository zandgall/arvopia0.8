package com.zandgall.arvopia.utils;

import java.io.Serializable;
import java.util.ArrayList;

import com.zandgall.arvopia.Console;

public class Mapper<A, B> implements Serializable {

	private static final long serialVersionUID = 766412215963546862L;

	private final ArrayList<A> keys;
	private final ArrayList<B> vals;
	private final ArrayList<Integer> codes;

	public Mapper() {
		keys = new ArrayList<>();
		vals = new ArrayList<>();
		codes = new ArrayList<>();
	}

	public void clear() {
		keys.clear();
		vals.clear();
		codes.clear();
	}

	public void put(A key, B val) {
		if (!keys.contains(key))
			keys.add(key);

		vals.add(val);
		codes.add(keys.indexOf(key));
	}

	public void remove(A key, B val) {
		if(!getValues(key).contains(val))
			return;
		else if(getValues(key).size()==1) {
			keys.remove(key);
		}
		
		Console.log("Values size", vals.size());
		for (int i = 0; i < vals.size(); i++) {
			if (keys.get(codes.get(i)) == key && vals.get(i) == val)
				vals.remove(i);
		}
	}
	
	public void remove(A key, int index) {
		if(!getValues(key).contains(vals.get(index)))
			return;
		else if(getValues(key).size()==1) {
			keys.remove(key);
		}
		codes.remove(index);
		vals.remove(index);
	}
	
	public void removeKey(A key) {
		if(!keys.contains(key))
			return;
		for (int i = 0; i < vals.size(); i++) {
			if (keys.get(codes.get(i)) == key)
				vals.remove(i);
		}
		keys.remove(key);
	}
	
	public void removeVal(B val) {
		if(!vals.contains(val))
			return;
		codes.remove(vals.indexOf(val));
		vals.remove(val);
	}
	
	public void remove(int index) {
		if(vals.size()<=index)
			return;
		codes.remove(index);
		vals.remove(index);
	}
	
	public int indexOfVal(A key, B val) {
		for(int i = 0; i<vals.size(); i++) {
			if(vals.get(i).equals(val)&&keys.get(codes.get(i)).equals(key)) {
				return i;
			}
		}	
		return -1;
	}
	
	public void set(A key, int index, B val) {
		if (!keys.contains(key))
			keys.add(key);

		vals.set(index, val);
		codes.set(index, keys.indexOf(key));
	}
	
	public void setDEBUG(A key, int index, B val) {
		if (!keys.contains(key))
			keys.add(key);

		int i = keys.indexOf(key);
		
		if(key instanceof String) {
			for(A k : keys)
				if(((String) k).contains((String) key) && ((String) key).contains((String) k))
					i = keys.indexOf(k);
		}
		
		vals.set(index, val);
		codes.set(index, keys.indexOf(i));
	}

	public ArrayList<A> getKeys() {
		return keys;
	}

	public boolean hasKey(A key) {
		for (A a : keys) {
			if (a instanceof String) {
				if (((String) key).contains((String) a) || ((String) a).contains((String) key))
					return true;
			} else {
				return keys.contains(key);
			}
		}
		return false;
	}

	public ArrayList<B> getValues(A key) {
		ArrayList<B> out = new ArrayList<B>();
		for (int i = 0; i < vals.size(); i++) {
			if (keys.get(codes.get(i)).equals(key))
				out.add(vals.get(i));
		}

		return out;
	}
	
	public ArrayList<B> getValues() {
		return vals;
	}

	public ArrayList<B> getValuesDEBUG(A key) {
		ArrayList<B> out = new ArrayList<B>();
		if (key instanceof String) {
			String a = (String) key;
			for (int i = 0; i < vals.size(); i++)
				if (((String) keys.get(codes.get(i))).contains(a))
					out.add(vals.get(i));
		} else
			for (int i = 0; i < vals.size(); i++) {
				if (keys.get(codes.get(i)) == key)
					out.add(vals.get(i));
			}

		return out;
	}

//	(a.name+"s").contains(p) && p.contains(a.name+"s")

	public void list() {
		for (A key : keys) {
			System.out.print(key + ": {");
			for (B value : getValues(key))
				System.out.print(value + ", ");
			System.out.println("}");
		}
	}

	public A keyFrom(B val) {
		return keys.get(codes.get(vals.indexOf(val)));
	}
	
	public Mapper<A, B> clone() {
		Mapper<A, B> out = new Mapper<A, B>();
		out.codes.addAll(codes);
		out.vals.addAll(vals);
		out.keys.addAll(keys);
		return out;
	}

}
