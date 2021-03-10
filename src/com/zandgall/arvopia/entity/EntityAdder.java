package com.zandgall.arvopia.entity;

import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.utils.ClassLoading;

public class EntityAdder {

	public EntityManager em;
	public Class<? extends Entity> c;
	
	public Entity example;

	public String name;
	
	public String simpleName;
	
	public EntityAdder(EntityManager em, Class<? extends Entity> c, String name) {
		this.em = em;
		this.c = c;
		this.name=name;
		simpleName=name;
		example = (Entity) ClassLoading.getC(c);
	}
	
	public EntityAdder(EntityManager em, Class<? extends Entity> c) {
		this.em = em;
		this.c = c;
		example = (Entity) ClassLoading.getC(c);
		if(example.creature) {
			simpleName = ((Creature) example).getName();
			name=c.getName();
		} else {
			name = c.getName();
			simpleName=c.getSimpleName();
		}
	}
	
	public EntityAdder(EntityManager em, Entity e) {
		this.em=em;
		example = e;
		this.c = e.getClass();
		if(example.creature) {
			name = ((Creature) example).getName(); 
		} else 
		name = c.getName();
	}

	public void add(String in, double x, double y) {
		//Space in front of name to only return true if it contains only the name and not, for example, "VillagerPlus"
		if(in.contains(name)&&in.contains(name)) {
			Entity b = (Entity) ClassLoading.getC(c);
			b.setX(x);
			b.setY(y);
			em.addEntity(b, true);
		}
	}
	
	public void add(double x, double y) {
		Entity b = (Entity) ClassLoading.getC(c);
		b.setX(x);
		b.setY(y);
		em.addEntity(b, true);
	}

}

//public class EntityAdder {
//
//	public Handler h;
//	public String directory;
//
//	public String name;
//	
//	public EntityAdder(Handler h, String directory, String name) {
//		this.h = h;
//		this.directory = directory;
//		this.name=name;
//	}
//
//	public void add(String in, int x, int y) {
//		if(name==in)
//			h.getEntityManager().addEntity(NPC.loadMODNPC(directory, h, x, y), false);
//	}
//	
//	public void add(int x, int y) {
//		h.getEntityManager().addEntity(NPC.loadMODNPC(directory, h, x, y), false);
//	}
//
//}