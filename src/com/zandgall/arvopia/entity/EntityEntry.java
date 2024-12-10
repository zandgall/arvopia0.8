package com.zandgall.arvopia.entity;

import com.zandgall.arvopia.ArvopiaLauncher;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Creature;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class EntityEntry {

	public final Entity example;
	public Class<? extends Entity> entityClass;

	public String name, simpleName;

	public Constructor<?> detailedConstructor;

	public EntityEntry(Class<? extends Entity> entityClass, String name) {
		this(entityClass);
		this.name=name;
		simpleName=name;

	}
	
	public EntityEntry(Class<? extends Entity> entityClass) {
		this.entityClass = entityClass;

		Entity initiated;
		try {
			initiated = entityClass.getDeclaredConstructor(Handler.class, double.class, double.class)
					.newInstance(ArvopiaLauncher.game.handler, 0, 0);
			if(initiated.creature) {
				simpleName = ((Creature) initiated).getName();
				name= entityClass.getName();
			} else {
				name = entityClass.getName();
				simpleName= entityClass.getSimpleName();
			}
		} catch(Exception e) {
			e.printStackTrace();
			initiated = null;
		}
		int maxPars = 0;
		for(Constructor<?> a : entityClass.getConstructors()) {
			if(a.getParameterCount()>maxPars) {
				maxPars = a.getParameterCount();
				detailedConstructor = a;
			}
		}

		example = initiated;
	}
	
	public EntityEntry(Entity e) {
		example = e.clone();
		this.entityClass = e.getClass();
		if(example.creature) {
			name = ((Creature) example).getName(); 
		} else 
			name = entityClass.getName();
	}

	public Entity spawn(double x, double y) {
		try {
			Entity b = entityClass.getDeclaredConstructor(Handler.class, double.class, double.class)
					.newInstance(ArvopiaLauncher.game.handler, x, y);
			return b;
		} catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

}